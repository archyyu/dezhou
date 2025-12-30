package com.archy.dezhou.command;

import java.util.Map;

import org.springframework.stereotype.Component;


import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.global.ConstList;

@Component
public class StandUpCommand implements GameCommand{

    @Override
    public String getCommandName() {
        return ConstList.CMD_STANDUP;
    }

    @Override
    public boolean execute(PukerGame gameRoom, Player player, Map<String, String> params) {
        return gameRoom.playerStandUp(player);
    }
    
}
