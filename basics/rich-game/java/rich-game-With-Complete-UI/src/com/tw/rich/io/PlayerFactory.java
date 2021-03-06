package com.tw.rich.io;

import com.tw.rich.core.player.Player;
import com.tw.rich.core.Identity;
import com.tw.rich.util.ColorCodes;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pzzheng on 12/6/16.`
 */
public class PlayerFactory {
    private static HashMap<String, Identity> allPlayerIdentities = new HashMap(){{
        put("1", new Identity("1", "钱夫人", ColorCodes.RED, "Q"));
        put("2", new Identity("2", "阿土伯", ColorCodes.GREEN, "A"));
        put("3", new Identity("3", "孙小美", ColorCodes.BLUE, "S"));
        put("4", new Identity("4", "金贝贝", ColorCodes.YELLOW, "J"));
    }};

    public static List<Player> getPlayers(String playerIds, int initialFund) {
        return playerIds.trim().chars()
                .mapToObj(i -> Player.createWithIdentityAndFund_WaitTurn(allPlayerIdentities.get(String.valueOf((char)i)), initialFund))
                .collect(Collectors.toList());
    }
}
