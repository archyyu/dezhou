package com.archy.dezhou.command;

import java.util.Map;

import com.archy.dezhou.container.JsonObjectWrapper;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.global.ConstList;

public class SitDownCommand implements GameCommand{

    @Override
    public String getCommandName() {
        return ConstList.CMD_SITDOWN;
    }

    @Override
    public JsonObjectWrapper execute(PukerGame gameRoom, Player player, Map<String, String> params) {
        int seatId = Integer.parseInt(params.get("sid"));
        int bet = Integer.parseInt(params.get("cb"));
        return gameRoom.getGameRoom().playerSitDown(seatId, player, bet);
    }
    
}
