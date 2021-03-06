package com.thoughtworks.mobileCharge.api.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.mobileCharge.domain.user.PhoneCard;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by pzzheng on 12/1/16.
 */
@XmlRootElement
public class CardBean {
//    @JsonProperty("phone_number")
    String phoneNumber;

//    @JsonProperty("card_locale")
    LocaleBean cardLocale;

    public CardBean(@JsonProperty(value = "phone_number", required = true) String phoneNumber,
                    @JsonProperty(value = "card_locale", required = true) LocaleBean cardLocale) {
        this.phoneNumber = phoneNumber;
        this.cardLocale = cardLocale;
    }

    public PhoneCard getPhoneCard() {
        return new PhoneCard(phoneNumber, cardLocale.getLocale());
    }
}
