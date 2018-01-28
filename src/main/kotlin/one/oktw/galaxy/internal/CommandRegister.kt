package one.oktw.galaxy.internal

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.CommandGalaxy
import one.oktw.galaxy.command.CommandTpa
import one.oktw.galaxy.command.CommandTpaHere
import one.oktw.galaxy.command.SaveMySelf
import one.oktw.galaxy.command.CommandTest
import one.oktw.galaxy.command.CommandUnStuck
import org.spongepowered.api.Sponge

class CommandRegister {
    private val logger = main.logger

    init {
        logger.info("Register command...")
        Sponge.getCommandManager().apply {
            register(main, CommandGalaxy().spec, "galaxy")
            register(main, CommandTpa().spec, "tpa")
            register(main, CommandTpaHere().spec, "tpahere")
            register(main, CommandUnStuck().spec, "unstuck")
            register(main, CommandTest().spec, "test")
        }
    }
}






