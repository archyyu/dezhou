package com.archy.texasholder.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.archy.texasholder.entity.MoneyResult;
import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.room.PukerGame;
import com.archy.texasholder.global.ConstList;

@Component
public class AllInCommand implements GameCommand{

    private int money = 0;

    @Override
    public String getCommandName() {
        return ConstList.CMD_ALL_IN;
    }

    @Override
    public boolean execute(PukerGame gameRoom, Player player, Map<String, String> params) {
        int bet = Integer.parseInt(params.get("cb"));
        MoneyResult result = gameRoom.playerAllIn(player, bet);
        money = result.money();
        return result.result();
    }

    public int getBet() {
        return this.money;
    }
    
}
