package one.oktw.galaxy

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.*
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
            register(main, TeleportHereAsk().spec, "tpahere")
            register(main, TeleportAsk().spec, "tpa")
            register(main, TPX().spec, "tpx")
            register(main, Hat().spec, "hat")
            register(main, Sign().spec, "sign")
            register(main, Gun().spec, "gun")
            register(main, Sniper().spec, "sniper")
        }
    }
}






