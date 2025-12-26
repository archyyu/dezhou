package com.archy.dezhou.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.archy.dezhou.container.JsonObjectWrapper;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.global.ConstList;

@Component
public class SitDownCommand implements GameCommand{

    @Override
    public String getCommandName() {
        return ConstList.CMD_SITDOWN;
    }

    @Override
    public JsonObjectWrapper execute(PukerGame gameRoom, Player player, Map<String, String> params) throws Exception{
        int seatId = Integer.parseInt(params.get("sid"));
        int bet = Integer.parseInt(params.get("cb"));
        System.err.println("sitdown seatId" + seatId);
        return gameRoom.playerSitDown(seatId, player, bet);
    }
    
}
