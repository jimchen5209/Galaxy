/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.command.commands

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import one.oktw.galaxy.command.Command
import one.oktw.galaxy.item.Button
import one.oktw.galaxy.item.Material
import one.oktw.galaxy.item.Tool
import one.oktw.galaxy.item.Weapon
import one.oktw.galaxy.item.type.ToolType.WRENCH

class Item : Command {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("item")
                .executes { context ->
                    execute(context.source)
                }

        )
    }

    private fun execute(source: ServerCommandSource): Int {
        val items = listOf(
            Button().createItemStack(),
            Tool().createItemStack(),
            Tool(WRENCH).createItemStack(),
            Weapon().createItemStack(),
            Material().createItemStack()
        )

        items.forEach { item ->
            val success = source.player.inventory.insertStack(item)
            if (!success) {
                val itemEntity = source.player.dropItem(item, false)
                if (itemEntity != null) {
                    itemEntity.resetPickupDelay()
                    itemEntity.owner = source.player.uuid
                }
            }
        }
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
