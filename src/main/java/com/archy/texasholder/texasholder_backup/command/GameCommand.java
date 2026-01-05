package com.archy.texasholder.command;

import java.util.Map;

import com.archy.texasholder.GameCmdException;
import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.room.PukerGame;

public interface GameCommand {

    String getCommandName();

    boolean execute(PukerGame gameRoom, Player player, Map<String,String> params) throws GameCmdException;

}