package com.archy.dezhou.command;

import java.util.Map;

import org.springframework.stereotype.Component;


import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.global.ConstList;

@Component
public class RaiseBetCommand implements GameCommand{

    @Override
    public String getCommandName() {
        return ConstList.CMD_ADD_BET;
    }

    @Override
    public boolean execute(PukerGame gameRoom, Player player, Map<String, String> params) {
        int bet = Integer.parseInt(params.get("cb"));
        return gameRoom.playerAddBet(player, bet);
    }
    
}
