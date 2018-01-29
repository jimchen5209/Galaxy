package one.oktw.galaxy.internal

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.CommandTpa
import one.oktw.galaxy.command.CommandTpaHere
import one.oktw.galaxy.command.Test
import one.oktw.galaxy.command.UnStuck
import org.spongepowered.api.Sponge

class CommandRegister {
    private val logger = main.logger

    init {
        logger.info("Register command...")
        Sponge.getCommandManager().apply {
            register(main, CommandTpa().spec, "tpa")
            register(main, CommandTpaHere().spec, "tpahere")
            register(main, UnStuck().spec, "unstuck")
            register(main, Test().spec, "test")
        }
    }
}






