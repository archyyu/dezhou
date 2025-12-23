package com.archy.dezhou.command;

import java.util.Map;

import com.archy.dezhou.container.JsonObjectWrapper;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.room.PukerGame;

public interface GameCommand {

    String getCommandName();

    JsonObjectWrapper execute(PukerGame gameRoom, Player player, Map<String,String> params);

}