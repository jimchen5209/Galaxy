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

package one.oktw.galaxy.block.event

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import one.oktw.galaxy.block.Block
import one.oktw.galaxy.block.type.BlockType
import one.oktw.galaxy.block.util.CustomBlockUtil
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerActionEvent
import one.oktw.galaxy.event.type.PlayerInteractItemEvent

class AngelBlock {
    private val justBreaked = HashSet<ServerPlayerEntity>()

    init {
        ServerTickEvents.END_WORLD_TICK.register(ServerTickEvents.EndWorldTick { justBreaked.clear() })
    }

    private fun placeAngelBlock(player: ServerPlayerEntity, hand: Hand) {
        val playerLookVec = player.rotationVector
        val playerPosition = player.pos
        CustomBlockUtil.placeBlock(
            player.serverWorld,
            BlockPos(
                Vec3d(
                    playerPosition.x + playerLookVec.x * 3,
                    playerPosition.y + playerLookVec.y * 3 + 1.5,
                    playerPosition.z + playerLookVec.z * 3
                )
            ),
            Block(BlockType.ANGEL_BLOCK).item!!.createItemStack(), BlockType.ANGEL_BLOCK
        )
            .run {
                player.setStackInHand(hand, Block(BlockType.ANGEL_BLOCK).item!!.createItemStack().also { it.count = player.getStackInHand(hand).count - 1 })
            }
    }

    @EventListener
    fun onPlace(event: PlayerInteractItemEvent) {
        val player = event.player
        val customBlockInHandIsAngelBlock = player.getStackInHand(Hand.MAIN_HAND).tag?.get("customBlockType")?.asString() == "ANGEL_BLOCK"
        if (customBlockInHandIsAngelBlock) {
            placeAngelBlock(player, Hand.MAIN_HAND)
        }

        if (customBlockInHandIsAngelBlock) {
            placeAngelBlock(player, Hand.OFF_HAND)
        }
    }

    @EventListener(sync = true)
    fun onBreak(event: PlayerActionEvent) {
        val player = event.player
        val blockPos = event.packet.pos
        val customBlockEntity = CustomBlockUtil.getCustomBlockEntity(player.serverWorld, blockPos)
        if (event.packet.action == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK &&
            customBlockEntity?.let { CustomBlockUtil.getTypeFromCustomBlockEntity(it) } == BlockType.ANGEL_BLOCK &&
            !justBreaked.contains(player)
        ) {
            CustomBlockUtil.removeBlock(player.serverWorld, blockPos)
            justBreaked.add(player)
            player.serverWorld.playSound(null, blockPos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F)
        }
    }
}
