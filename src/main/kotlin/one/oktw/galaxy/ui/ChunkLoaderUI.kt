package one.oktw.galaxy.ui

import com.codehusky.huskyui.StateContainer
import com.codehusky.huskyui.states.Page
import com.codehusky.huskyui.states.action.ActionType
import com.codehusky.huskyui.states.action.runnable.RunnableAction
import com.codehusky.huskyui.states.action.runnable.UIRunnable
import com.codehusky.huskyui.states.element.ActionableElement
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryDimension
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class ChunkLoaderUI {
    companion object {
        fun chunkLoaderUI(src: Player){
            val container = StateContainer()
            //Main Menu
            val clBuilder = ItemStack.builder().itemType(ItemTypes.LEVER)
//            if () { //TODO
//                clBuilder.add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN,"啟用 ChunkLoader"))
//            }else{
//                clBuilder.add(Keys.DISPLAY_NAME, Text.of(TextColors.RED,"停用 ChunkLoader"))
//            }
            val chunkLoaderSwitch = clBuilder.build()
            val menu = Page.builder()
                    .setTitle(Text.of("ChunkLoader"))
                    .setInventoryDimension(InventoryDimension(9,1))
                    .putElement(calcSlot(0, 2),
                            ActionableElement(
                                    RunnableAction(container, ActionType.NONE,"galaxyManager", UIRunnable {
                                        //TODO
                                    }),
                                    chunkLoaderSwitch
                            )
                    )
                    .putElement(calcSlot(0, 4),
                            ActionableElement(
                                    RunnableAction(container, ActionType.NONE,"galaxyManager", UIRunnable {
                                        //TODO
                                    }),
                                    ItemStack.builder()
                                            .itemType(ItemTypes.ENCHANTED_BOOK)
                                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD,"升級"))
                                            .build()
                            )
                    )
                    .putElement(calcSlot(0, 6),
                            ActionableElement(
                                    RunnableAction(container, ActionType.NONE,"galaxyManager", UIRunnable {
                                        //TODO
                                    }),
                                    ItemStack.builder()
                                            .itemType(ItemTypes.BARRIER)
                                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.RED,"刪除此 ChunkLoader"))
                                            .build()
                            )
                    )
                    .build("menu")
            container.setInitialState(menu)
            container.launchFor(src)
        }
        private fun calcSlot(Line: Int, Column: Int): Int {
            return 9 * Line + Column
        }
    }
}
