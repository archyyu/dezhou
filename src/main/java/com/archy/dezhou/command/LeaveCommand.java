package com.archy.dezhou.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.archy.dezhou.container.JsonObjectWrapper;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.global.ConstList;

@Component
public class LeaveCommand implements GameCommand {

    @Override
    public String getCommandName() {
        return ConstList.CMD_LEAVE;
    }

    @Override
    public JsonObjectWrapper execute(PukerGame gameRoom, Player player, Map<String, String> params) {

        gameRoom.playerLeave(player);
        return new JsonObjectWrapper();

    }
    
}
