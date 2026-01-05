package com.archy.texasholder.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.archy.texasholder.GameCmdException;
import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.room.PukerGame;
import com.archy.texasholder.global.ConstList;

@Component
public class SitDownCommand implements GameCommand{

    @Override
    public String getCommandName() {
        return ConstList.CMD_SITDOWN;
    }

    @Override
    public boolean execute(PukerGame gameRoom, Player player, Map<String, String> params) throws GameCmdException{
        int seatId = Integer.parseInt(params.get("sid"));
        int bet = Integer.parseInt(params.get("cb"));
        System.err.println("sitdown seatId" + seatId);
        return gameRoom.playerSitDown(seatId, player, bet);
    }
    
}
