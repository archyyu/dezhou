package com.archy.texasholder.command;

import java.util.Map;

import org.springframework.stereotype.Component;


import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.room.PukerGame;
import com.archy.texasholder.global.ConstList;

@Component
public class LeaveCommand implements GameCommand {

    @Override
    public String getCommandName() {
        return ConstList.CMD_LEAVE;
    }

    @Override
    public boolean execute(PukerGame gameRoom, Player player, Map<String, String> params) {

        return gameRoom.playerLeave(player);

    }
    
}
