package com.kamesuta.pongcraft.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.configlib.ConfigCommand;

public class MainCommand extends Command {
    public MainCommand(String name, ConfigCommand configCommand) {
        super(name);

        children(new StartCommand(), new StopCommand(), configCommand);
    }
}