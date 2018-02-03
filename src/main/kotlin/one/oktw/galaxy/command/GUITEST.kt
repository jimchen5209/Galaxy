package one.oktw.galaxy.command

import com.codehusky.huskyui.StateContainer
import com.codehusky.huskyui.states.Page
import com.codehusky.huskyui.states.element.Element
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryDimension
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class GUI : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.test")
                .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src is Player) {
            val test = StateContainer()
            val testpage = Page.builder()
                .setTitle(Text.of("測試頁面"))
                .setInventoryDimension(InventoryDimension(9,2))
                .putElement(13,
                        Element(
                                ItemStack.builder()
                                        .itemType(ItemTypes.BARRIER)
                                        .add(Keys.DISPLAY_NAME,Text.of(TextColors.GOLD,"EXIT"))
                                        .add(Keys.ITEM_LORE, listOf(Text.of(TextColors.GOLD,"EXIT")))
                                        .build()
                        )
                )

                .build("testpage")
            test.setInitialState(testpage)
            test.launchFor(src)
            src.sendMessage(Text.of("Test!").toText())
            return CommandResult.success()
        }
        return CommandResult.empty()
    }
}
