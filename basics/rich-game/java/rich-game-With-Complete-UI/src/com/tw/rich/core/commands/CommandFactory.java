package com.tw.rich.core.commands;

import com.tw.rich.core.Dice;
import com.tw.rich.core.Reportable;
import com.tw.rich.core.assistenceItems.Tool;
import com.tw.rich.core.places.Estate;

/**
 * Created by pzzheng on 11/27/16.
 */
public class CommandFactory {
    public static Command SayYes = new SimpleCommand();
    public static Command BuyTool = new BuyTool();
    public static Command GetGift = new GetGift();
    public static Command Quit = new SimpleCommand();
    public static Command SayNo = new SimpleCommand();

    public static Command Roll(Dice dice) {
        return new Roll(dice);
    }

    public static Command Query(Reportable reportable) {
        return new Query(reportable);
    }

    public static Command Help(Reportable reportable) {
        return new Help(reportable);
    }

    public static Command BuyEstate(Estate estate) {
        return new BuyEstate(estate);
    }

    public static Command SellTool(Tool tool) {
        return new SellTool(tool);
    }

    public static Command UseTool(Tool tool, int steps) {
        return new UseTool(tool, steps);
    }

    public static Command SellEstate(Estate estate) {
        return new SellEstate(estate);
    }

    public static Command UpgradeEstate(Estate estate) {
        return new UpgradeEstate(estate);
    }

    public static Command Selection(int selection) {
        return new Selection(selection);
    }

}
