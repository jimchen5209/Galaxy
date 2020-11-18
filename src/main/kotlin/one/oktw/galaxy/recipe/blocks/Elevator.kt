/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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

package one.oktw.galaxy.recipe.blocks

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.Items.ENDER_PEARL
import net.minecraft.item.Items.IRON_BLOCK
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier
import net.minecraft.world.World
import one.oktw.galaxy.block.item.BlockItem
import one.oktw.galaxy.block.type.BlockType

class Elevator : CraftingRecipe {
    private val item = BlockItem(BlockType.ELEVATOR).createItemStack()
    private val list = listOf(ENDER_PEARL, IRON_BLOCK)

    override fun matches(inv: CraftingInventory, world: World): Boolean {
        list.forEach { item ->
            var match = false
            for (i in 0 until inv.size()) {
                val invItem = inv.getStack(i)
                if (invItem.isEmpty) continue
                if (invItem.item == item) {
                    match = true
                } else if (!list.contains(invItem.item)) {
                    return false
                }
            }
            if (!match) return false
        }
        return true
    }

    override fun craft(inv: CraftingInventory) = item.copy()

    @Environment(EnvType.CLIENT)
    override fun fits(width: Int, height: Int): Boolean {
        throw NotImplementedError()
    }

    override fun getOutput() = item

    override fun getId() = Identifier("galaxy", "block/elevator")

    override fun getSerializer(): RecipeSerializer<*> {
        TODO("Not yet implemented, support client mod.")
    }
}
