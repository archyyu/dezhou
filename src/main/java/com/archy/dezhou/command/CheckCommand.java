package com.archy.dezhou.command;

import java.util.Map;

import org.springframework.stereotype.Component;


import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.room.PukerGame;

// @Component
public class CheckCommand implements GameCommand{

    @Override
    public String getCommandName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCommandName'");
    }

    @Override
    public boolean execute(PukerGame gameRoom, Player player, Map<String, String> params) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }
    
}
