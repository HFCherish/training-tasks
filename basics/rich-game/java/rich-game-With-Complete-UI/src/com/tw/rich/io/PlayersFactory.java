package com.tw.rich.io;

import com.tw.rich.core.player.Player;
import com.tw.rich.core.player.PlayerIdentity;
import com.tw.rich.util.ColorCodes;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pzzheng on 12/6/16.`
 */
public class PlayersFactory {
    private static HashMap<String, PlayerIdentity> allPlayerIdentities = new HashMap(){{
        put("1", new PlayerIdentity("1", "钱夫人", ColorCodes.RED));
        put("2", new PlayerIdentity("1", "钱夫人", ColorCodes.GREEN));
        put("3", new PlayerIdentity("1", "钱夫人", ColorCodes.BLUE));
        put("4", new PlayerIdentity("1", "钱夫人", ColorCodes.YELLOW));
    }};

    public static List<Player> getPlayers(String playerIds, int initialFund) {
        return playerIds.trim().chars()
                .mapToObj(i -> Player.createWithIdentityAndFund_WaitTurn(allPlayerIdentities.get(String.valueOf((char)i)), initialFund))
                .collect(Collectors.toList());
    }
}