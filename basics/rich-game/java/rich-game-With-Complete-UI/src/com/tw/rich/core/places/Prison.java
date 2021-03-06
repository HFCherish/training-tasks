package com.tw.rich.core.places;

import com.tw.rich.core.messages.Message;
import com.tw.rich.core.player.Player;
import com.tw.rich.core.commands.Command;

/**
 * Created by pzzheng on 11/27/16.
 */
public class Prison extends Place {

    public static final int PRISON_DAYS = 2;

    @Override
    public Command comeHere(Player player) {
        player.moveTo(this);
        player.addMessage(Message.STUCK_IN_PRISON);
        player.stuckFor(PRISON_DAYS);
        player.endTurn();
        return null;
    }
}
