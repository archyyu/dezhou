package com.archy.dezhou.command;

import java.util.Map;

import com.archy.dezhou.container.JsonObjectWrapper;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.global.ConstList;

public class DropCardCommand implements GameCommand{

    @Override
    public String getCommandName() {
        return ConstList.CMD_DROP_CARD;
    }

    @Override
    public JsonObjectWrapper execute(PukerGame gameRoom, Player player, Map<String, String> params) {
        return gameRoom.playerDropCard(player);
    }
    
}
