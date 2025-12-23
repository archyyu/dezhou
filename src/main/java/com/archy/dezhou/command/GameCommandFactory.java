package com.archy.dezhou.command;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameCommandFactory {
    private final Map<String, GameCommand> commandRegistry;

    @Autowired
    public GameCommandFactory(List<GameCommand> commands) {
        commandRegistry = commands.stream()
                .collect(Collectors.toMap(GameCommand::getCommandName, command -> command));
    }

    public GameCommand getCommand(String commandName) {
        GameCommand command = commandRegistry.get(commandName);
        if (command == null) {
            throw new IllegalArgumentException("Unknown command: " + commandName);
        }
        return command;
    }

    public Collection<String> getAvailableCommands() {
        return commandRegistry.keySet();
    }
}
