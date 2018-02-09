package one.oktw.galaxy.UI

import com.codehusky.huskyui.StateContainer
import com.codehusky.huskyui.states.Page
import com.codehusky.huskyui.states.action.Action
import com.codehusky.huskyui.states.action.ActionType
import com.codehusky.huskyui.states.action.runnable.RunnableAction
import com.codehusky.huskyui.states.action.runnable.UIRunnable
import com.codehusky.huskyui.states.element.ActionableElement
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.SkullTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryDimension
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*

class UI {
    companion object {
        fun mainMenu(src: Player){
            val mainContainer = StateContainer()
            //Main Menu
            val mainMenu = Page.builder()
                    .setTitle(Text.of("OKTWGalaxy"))
                    .setInventoryDimension(InventoryDimension(9,2))
                    .putElement(calcSlot(0, 2),
                            ActionableElement(
                                    Action(mainContainer, ActionType.NORMAL,"galaxyManager"),
                                    ItemStack.builder()
                                            .itemType(ItemTypes.SKULL)
                                            .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                                            .add(Keys.REPRESENTED_PLAYER, GameProfile.of(UUID.fromString("39693d8e-0bf8-4e62-a891-c0bd27b08600"),"0qt"))
                                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD,"Galaxy"))
                                            .add(Keys.ITEM_LORE, listOf(Text.of(TextColors.AQUA,"星系管理")))
                                            .build()
                            )
                    )
                    .putElement(calcSlot(0, 6),
                            ActionableElement(
                            Action(mainContainer, ActionType.NORMAL,"playerFeature"),
                            ItemStack.builder()
                                    .itemType(ItemTypes.SKULL)
                                    .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                                    .add(Keys.REPRESENTED_PLAYER, GameProfile.of(UUID.fromString(src.identifier)))
                                    .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD,"Player"))
                                    .add(Keys.ITEM_LORE, listOf(Text.of(TextColors.AQUA,"玩家功能")))
                                    .build()
                            )
                    )
                    .putElement(calcSlot(1, 8),
                            ActionableElement(
                                    Action(mainContainer, ActionType.CLOSE,"mainMenu"),
                                    ItemStack.builder()
                                            .itemType(ItemTypes.BARRIER)
                                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD,"EXIT"))
                                            .add(Keys.ITEM_LORE, listOf(Text.of(TextColors.RED,"離開選單")))
                                            .build()
                            )
                    )
                    .build("mainMenu")
            mainContainer.setInitialState(mainMenu)
            //GalaxyManager
            val galaxyManager = Page.builder()
                    .setTitle(Text.of("星系管理"))
                    .setInventoryDimension(InventoryDimension(9,2))
                    //TODO
                    .putElement(calcSlot(1, 8),
                            ActionableElement(
                                    Action(mainContainer, ActionType.BACK,"galaxyManager"),
                                    ItemStack.builder()
                                            .itemType(ItemTypes.BARRIER)
                                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD,"Back"))
                                            .add(Keys.ITEM_LORE, listOf(Text.of(TextColors.RED,"上一頁")))
                                            .build()
                                    )
                    )
                    .setParent("mainMenu")
                    .build("galaxyManager")
            mainContainer.addState(galaxyManager)
            //PlayerFeature
            val playerFeature = Page.builder()
                    .setTitle(Text.of("玩家功能"))
                    .setInventoryDimension(InventoryDimension(9,2))
                    //TODO
                    .putElement(calcSlot(0, 2),
                            ActionableElement(
                                    RunnableAction(mainContainer, ActionType.CLOSE,"playerFeature", UIRunnable {
                                        Sponge.getCommandManager().process(src, "unstuck")
                                    }),
                                    ItemStack.builder()
                                            .itemType(ItemTypes.ENDER_EYE)
                                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD,"Unstuck"))
                                            .add(Keys.ITEM_LORE, listOf(Text.of(TextColors.AQUA,"卡點自救")))
                                            .build()
                            )
                    )
                    .putElement(calcSlot(0, 6),
                            ActionableElement(
                                    RunnableAction(mainContainer, ActionType.CLOSE,"playerFeature", UIRunnable {
                                        src.sendMessage(Text.of("請選擇一個選項"))
                                        val tpa = Text.builder("請求傳送到玩家")
                                                .color(TextColors.GREEN)
                                                .style(TextStyles.UNDERLINE)
                                                .onHover(TextActions.showText(Text.of( "/tpa <玩家>")))
                                                .onClick(TextActions.suggestCommand("/tpa "))
                                                .build()
                                        val tpaHere = Text.builder("請求傳送玩家到您這裡")
                                                .color(TextColors.GREEN)
                                                .style(TextStyles.UNDERLINE)
                                                .onHover(TextActions.showText(Text.of( "/tpahere <玩家>")))
                                                .onClick(TextActions.suggestCommand("/tpahere "))
                                                .build()
                                        src.sendMessage(Text.of("- ",tpa,"\n- ",tpaHere))

                                    }),
                                    ItemStack.builder()
                                            .itemType(ItemTypes.SKULL)
                                            .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                                            .add(Keys.REPRESENTED_PLAYER, GameProfile.of(UUID.fromString("40ffb372-12f6-4678-b3f2-2176bf56dd4b"),"MHF_Enderman"))
                                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD,"TeleportAsk"))
                                            .add(Keys.ITEM_LORE, listOf(Text.of(TextColors.AQUA,"傳送請求")))
                                            .build()
                            )
                    )
                    .putElement(calcSlot(1, 8),
                            ActionableElement(
                                    Action(mainContainer, ActionType.BACK,"playerFeature"),
                                    ItemStack.builder()
                                            .itemType(ItemTypes.BARRIER)
                                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD,"Back"))
                                            .add(Keys.ITEM_LORE, listOf(Text.of(TextColors.RED,"上一頁")))
                                            .build()
                            )
                    )
                    .setParent("mainMenu")
                    .build("playerFeature")
            mainContainer.addState(playerFeature)
            mainContainer.launchFor(src)
        }
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