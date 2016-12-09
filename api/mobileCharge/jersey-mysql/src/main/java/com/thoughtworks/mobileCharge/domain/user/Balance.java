package com.thoughtworks.mobileCharge.domain.user;

import com.thoughtworks.mobileCharge.api.jersey.Routes;
import com.thoughtworks.mobileCharge.domain.ChargeType;
import com.thoughtworks.mobileCharge.infrastructure.records.Record;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * Created by pzzheng on 11/29/16.
 */
public class Balance implements Record {
    protected double remainMoney;
    protected HashMap<ChargeTypeGroup, ChargeBalance> accounts;


    public Balance() {
        remainMoney = 0.0;

        accounts = new HashMap<>();
//        callAccounts = new HashMap();
        for (CommunicationRecord.CommunicationType communicationType : CommunicationRecord.CommunicationType.values()) {
            for (CallRecord.CallType callType : CallRecord.CallType.values()) {
                accounts.put(new ChargeTypeGroup(communicationType, callType), new ChargeBalance());
            }
        }

//        messageAccounts = new HashMap();
        for (CommunicationRecord.CommunicationType communicationType : CommunicationRecord.CommunicationType.values()) {
            for (MessageRecord.Type messageType : MessageRecord.Type.values()) {
                for (MessageRecord.SendType sendType : MessageRecord.SendType.values()) {
                    accounts.put(new ChargeTypeGroup(communicationType, messageType, sendType), new ChargeBalance());
                }
            }
        }

//        dataAccessAccounts = new HashMap<>();
        Stream.of(CommunicationRecord.CommunicationType.values()).forEach(type -> {
            accounts.put(new ChargeTypeGroup(type), new ChargeBalance());
        });

    }

    public double charge(CommunicationRecord record, BiFunction<CommunicationRecord, Balance, Double> chargeStrategy) {
        return chargeStrategy.apply(record, this);
    }

    @Override
    public Map<String, Object> toRefJson(Routes routes) {
        return new HashMap() {{
            put("remainMoney", remainMoney);
            put("remainData", new HashMap() {{
                put("local", accounts.get(new ChargeTypeGroup(CommunicationRecord.CommunicationType.LOCAL)).freeNumbers);
                put("internal", accounts.get(new ChargeTypeGroup(CommunicationRecord.CommunicationType.INTERNAL)).freeNumbers);
                put("international", accounts.get(new ChargeTypeGroup(CommunicationRecord.CommunicationType.INTERNATIONAL)).freeNumbers);
            }});
            put("remainCallMinutes", new HashMap() {{
                put("local", getFreeNumbers(accounts, CommunicationRecord.CommunicationType.LOCAL));
                put("internal", getFreeNumbers(accounts, CommunicationRecord.CommunicationType.INTERNAL));
                put("international", getFreeNumbers(accounts, CommunicationRecord.CommunicationType.INTERNATIONAL));
            }
                public long getFreeNumbers
                        (Map<ChargeTypeGroup, ChargeBalance> accounts, CommunicationRecord.CommunicationType communicationType) {
                    return accounts.get(new ChargeTypeGroup(communicationType, CallRecord.CallType.CALLEE)).freeNumbers +
                            accounts.get(new ChargeTypeGroup(communicationType, CallRecord.CallType.CALLER)).freeNumbers;
                }
            });
            put("remainMessages", new HashMap() {{
                    put("local", getMessageMap(CommunicationRecord.CommunicationType.LOCAL));
                    put("internal", getMessageMap(CommunicationRecord.CommunicationType.INTERNAL));
                    put("international", getMessageMap(CommunicationRecord.CommunicationType.INTERNATIONAL));
            }
                public long getFreeNumbers
                        (Map<ChargeTypeGroup, ChargeBalance> accounts, CommunicationRecord.CommunicationType
                                communicationType, MessageRecord.Type type) {
                    return accounts.get(new ChargeTypeGroup(communicationType, type, MessageRecord.SendType.SENDER)).freeNumbers +
                            accounts.get(new ChargeTypeGroup(communicationType, type, MessageRecord.SendType.RECEIVER)).freeNumbers;
                }

                public HashMap getMessageMap(final CommunicationRecord.CommunicationType communicationType) {
                    return new HashMap() {{
                        put(MessageRecord.Type.MMS.name(), getFreeNumbers(accounts, communicationType, MessageRecord.Type.MMS));
                        put(MessageRecord.Type.SMS.name(), getFreeNumbers(accounts, communicationType, MessageRecord.Type.SMS));
                    }};
                }
            });

        }};
    }

    @Override
    public Map<String, Object> toJson(Routes routes) {
        return toRefJson(routes);
    }

    public static Balance buildFromDocument(Document document) {
        Balance balance = new Balance();
        balance.remainMoney = document.getDouble("remain_money");
//        balance.
        return balance;
    }

    public static class ChargeStrategies {
        public static BiFunction<CommunicationRecord, Balance, Double> callCharge() {
            return (record, balance) -> {
                double charge = 0.0;

                ChargeBalance callChargeBalance = balance.accounts.get(new ChargeTypeGroup(record.communicationType, ((CallRecord) record).callType));
                Long costMinutes = ((CallRecord) record).duration.getStandardMinutes();
                long freeMinutes = callChargeBalance.freeNumbers;
                if (freeMinutes > costMinutes) {
                    callChargeBalance.addFreeNumber(-costMinutes);
                } else {
                    callChargeBalance.addFreeNumber(-freeMinutes);
                    charge = callChargeBalance.unitPrice * (costMinutes - freeMinutes);
                    balance.remainMoney -= charge;
                }

                return charge;
            };
        }

        public static BiFunction<CommunicationRecord, Balance, Double> dataAccessCharge() {
            return (record, balance) -> {
                double charge = 0.0;

                if (((DataAccessRecord) record).chargeType.equals(ChargeType.CHARGE)) {
                    ChargeBalance dataAccessChargeBalance = balance.accounts.get(new ChargeTypeGroup(record.communicationType));
                    Long costData = ((DataAccessRecord) record).data;
                    long freeData = dataAccessChargeBalance.freeNumbers;
                    if (freeData > costData) {
                        dataAccessChargeBalance.addFreeNumber(-costData);
                    } else {
                        dataAccessChargeBalance.addFreeNumber(-freeData);
                        charge = dataAccessChargeBalance.unitPrice * (costData - freeData);
                        balance.remainMoney -= charge;
                    }
                }

                return charge;
            };
        }

        public static BiFunction<CommunicationRecord, Balance, Double> messageCharge() {
            return (record, balance) -> {
                double charge = 0.0;

                ChargeBalance messageChargeBalance = balance.accounts.get(new ChargeTypeGroup(record.communicationType, ((MessageRecord) record).type, ((MessageRecord) record).sendType));
                if (messageChargeBalance.freeNumbers > 0) {
                    messageChargeBalance.addFreeNumber(-1);
                } else {
                    charge = messageChargeBalance.unitPrice;
                    balance.remainMoney -= charge;
                }

                return charge;
            };
        }
    }
}
