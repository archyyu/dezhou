package com.archy.dezhou.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.archy.dezhou.container.JsonObjectWrapper;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.global.ConstList;

@Component
public class AllInCommand implements GameCommand{

    @Override
    public String getCommandName() {
        return ConstList.CMD_ALL_IN;
    }

    @Override
    public JsonObjectWrapper execute(PukerGame gameRoom, Player player, Map<String, String> params) {
        int bet = Integer.parseInt(params.get("cb"));
        return gameRoom.playerAllIn(player, bet);
    }
    
}
