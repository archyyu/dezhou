package com.archy.dezhou.command;

import java.util.Map;

import com.archy.dezhou.container.JsonObjectWrapper;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.room.PukerGame;

public class CheckCommand implements GameCommand{

    @Override
    public String getCommandName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCommandName'");
    }

    @Override
    public JsonObjectWrapper execute(PukerGame gameRoom, Player player, Map<String, String> params) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }
    
}
