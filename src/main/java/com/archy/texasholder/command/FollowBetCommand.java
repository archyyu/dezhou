package com.archy.texasholder.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.archy.texasholder.entity.MoneyResult;
import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.room.PukerGame;
import com.archy.texasholder.global.ConstList;

@Component
public class FollowBetCommand implements GameCommand{

    private int money;

    @Override
    public String getCommandName() {
        return ConstList.CMD_FOLLOW_BET;
    }

    @Override
    public boolean execute(PukerGame gameRoom, Player player, Map<String, String> params) {
        MoneyResult moneyResult = gameRoom.playerFollow(player);
        this.money = moneyResult.money();
        return moneyResult.result();
    }

    @Override
    public int getBet() {
        return this.money;
    }

    
    
}
