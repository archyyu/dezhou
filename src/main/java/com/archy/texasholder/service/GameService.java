package com.archy.texasholder.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.archy.texasholder.GameCmdException;
import com.archy.texasholder.command.GameCommand;
import com.archy.texasholder.command.GameCommandFactory;
import com.archy.texasholder.entity.GameActionDB;
import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.room.PukerGame;
import com.archy.texasholder.repo.GameActionDBRepository;

import jakarta.annotation.Resource;


@Service
public class GameService {
    
    @Resource
    private GameActionDBRepository gameActionDBRepository;

    @Resource
    private GameCommandFactory gameCommandFactory;

    public boolean exec(String cmd, PukerGame room, Player player, Map<String, String> additionalParams) throws GameCmdException{

        GameCommand gameCommand = this.gameCommandFactory.getCommand(cmd);

        boolean result = gameCommand.execute(room, player, additionalParams);
        if (result) {

            GameActionDB gameActionDB = GameActionDB.builder()
                                                    .gameroomId(room.getRoomid())
                                                    .action(gameCommand.getCommandName())
                                                    .userId(player.getUid())
                                                    .money(gameCommand.getBet())
                                                    .timestamp(System.currentTimeMillis()/1000)
                                                    .params(JSON.toJSONString(additionalParams))
                                                    .build();
                                                    
            gameActionDBRepository.save(gameActionDB);

        }

        return result;
    }
    
}
