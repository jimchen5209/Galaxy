package one.oktw.galaxy.command

import one.oktw.galaxy.ui.GalaxyUI
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player

class GUI : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.test")
                .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src is Player) {
            GalaxyUI.mainMenu(src)
            return CommandResult.success()
        }
        return CommandResult.empty()
    }
}
