package com.archy.dezhou.command;

import java.util.Map;

import com.archy.dezhou.GameCmdException;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.room.PukerGame;

public interface GameCommand {

    String getCommandName();

    boolean execute(PukerGame gameRoom, Player player, Map<String,String> params) throws GameCmdException;

}