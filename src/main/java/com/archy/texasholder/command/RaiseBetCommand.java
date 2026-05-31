package com.archy.texasholder.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.archy.texasholder.entity.MoneyResult;
import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.room.PukerGame;
import com.archy.texasholder.global.ConstList;

@Component
public class RaiseBetCommand implements GameCommand{
    
    private int money;

    @Override
    public String getCommandName() {
        return ConstList.CMD_ADD_BET;
    }

    @Override
    public boolean execute(PukerGame gameRoom, Player player, Map<String, String> params) {
        int bet = Integer.parseInt(params.get("cb"));
        MoneyResult moneyResult = gameRoom.playerAddBet(player, bet);
        this.money = moneyResult.money();
        return moneyResult.result();
    }
    
    @Override
    public int getBet() {
        return this.money;
    }

}
