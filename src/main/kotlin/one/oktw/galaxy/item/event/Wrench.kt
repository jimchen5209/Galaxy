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

package one.oktw.galaxy.item.event

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks.*
import net.minecraft.block.ChestBlock
import net.minecraft.block.WallMountedBlock.FACE
import net.minecraft.block.entity.ShulkerBoxBlockEntity
import net.minecraft.block.enums.ChestType
import net.minecraft.block.enums.RailShape
import net.minecraft.block.enums.SlabType
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.state.property.Properties.*
import net.minecraft.util.Hand.MAIN_HAND
import net.minecraft.util.Hand.OFF_HAND
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerSneakReleaseEvent
import one.oktw.galaxy.event.type.PlayerUseItemOnBlock
import one.oktw.galaxy.item.Tool
import one.oktw.galaxy.item.type.ToolType
import java.util.*
import kotlin.collections.set

class Wrench {
    private val wrenchItemStack = Tool(ToolType.WRENCH).createItemStack()
    private val startDirection = WeakHashMap<ServerPlayerEntity, Pair<BlockPos, Direction>>() // Don't leak ServerPlayerEntity

    @EventListener(true)
    fun onUseItemOnBlock(event: PlayerUseItemOnBlock) {
        val player = event.context.player ?: return

        if (!player.shouldCancelInteraction()) return

        when (event.context.hand ?: return) {
            MAIN_HAND -> if (ItemStack.areEqual(player.mainHandStack, wrenchItemStack)) {
                if (wrenchSpin(event)) player.swingHand(MAIN_HAND, true)
            }
            OFF_HAND -> if (player.mainHandStack.isEmpty && ItemStack.areEqual(player.offHandStack, wrenchItemStack)) {
                if (wrenchSpin(event)) player.swingHand(OFF_HAND, true)
            }
        }
    }

    @EventListener(true)
    fun onSneakRelease(event: PlayerSneakReleaseEvent) {
        startDirection.remove(event.player)
    }

    private fun wrenchSpin(event: PlayerUseItemOnBlock): Boolean {
        val world = event.context.world
        val player = event.context.player as ServerPlayerEntity
        val blockPos = event.context.blockPos
        val blockState = world.getBlockState(blockPos)
        val block = blockState.block
        val clickSide: Direction = event.context.side

        // Check destructible
        if (blockState.getHardness(world, blockPos) < 0.0) return false
        if (block == PISTON_HEAD || (block == PISTON || block == STICKY_PISTON) && blockState.get(EXTENDED)) return false
        if (blockState.contains(BED_PART)) return false

        // Remove outdated start direction
        if (startDirection[player]?.first != blockPos) startDirection.remove(player)

        // Change torch to wall torch
        when (block) {
            TORCH, SOUL_TORCH, REDSTONE_TORCH -> {
                val direction = if (!startDirection.contains(player) && clickSide !in listOf(Direction.UP, Direction.DOWN)) clickSide else Direction.NORTH

                world.setBlockState(blockPos, switchTorch(block).defaultState.with(HORIZONTAL_FACING, direction))

                startDirection[player] = blockPos to direction
                return true
            }
        }

        // Rotate Direction
        var allowUp = true
        var allowDown = true
        val propAndDirection = when {
            blockState.contains(FACING) -> {
                // Only allow rotate closed shulker box
                if (block == SHULKER_BOX) {
                    val entity = world.getBlockEntity(blockPos) as? ShulkerBoxBlockEntity ?: return false
                    if (entity.animationStage != ShulkerBoxBlockEntity.AnimationStage.CLOSED) return false
                }

                FACING to blockState[FACING]
            }
            blockState.contains(HORIZONTAL_FACING) -> {
                allowUp = false
                allowDown = false

                // Chest merge & split
                if (blockState.contains(CHEST_TYPE)) {
                    val chestFacing = ChestBlock.getFacing(blockState)

                    when (blockState[CHEST_TYPE]!!) {
                        ChestType.LEFT, ChestType.RIGHT -> {
                            val anotherPos = blockPos.offset(chestFacing)
                            world.setBlockState(blockPos, blockState.with(CHEST_TYPE, ChestType.SINGLE))
                            world.setBlockState(anotherPos, world.getBlockState(anotherPos).with(CHEST_TYPE, ChestType.SINGLE))
                            return true
                        }
                        ChestType.SINGLE -> {
                            // TODO refactor
                            if (chestFacing == clickSide) {
                                if (connectChest(event, chestFacing, blockPos, blockState, false)) return true
                            } else if (chestFacing.opposite == clickSide) {
                                if (connectChest(event, chestFacing, blockPos, blockState, true)) return true
                            }
                        }
                    }
                }

                HORIZONTAL_FACING to blockState[HORIZONTAL_FACING]
            }
            blockState.contains(HOPPER_FACING) -> {
                allowUp = false
                HOPPER_FACING to blockState[HOPPER_FACING]
            }
            else -> when { // Not direction rotate
                blockState.contains(RAIL_SHAPE) -> {
                    val currentState = blockState[RAIL_SHAPE]
                    world.setBlockState(blockPos, blockState.with(RAIL_SHAPE, spinRail(currentState)))
                    return currentState != spinRail(currentState)
                }
                blockState.contains(STRAIGHT_RAIL_SHAPE) -> {
                    val currentState = blockState[STRAIGHT_RAIL_SHAPE]
                    world.setBlockState(blockPos, blockState.with(STRAIGHT_RAIL_SHAPE, spinRail(currentState)))
                    return currentState != spinRail(currentState)
                }
                blockState.contains(AXIS) -> {
                    world.setBlockState(blockPos, blockState.cycle(AXIS))
                    return true
                }
                blockState.contains(ROTATION) -> {
                    world.setBlockState(blockPos, blockState.cycle(ROTATION))
                    return true
                }
                blockState.contains(SLAB_TYPE) -> {
                    val direction = blockState[SLAB_TYPE]
                    if (direction == SlabType.TOP) {
                        world.setBlockState(blockPos, blockState.with(SLAB_TYPE, SlabType.BOTTOM))
                    } else if (direction == SlabType.BOTTOM) {
                        world.setBlockState(blockPos, blockState.with(SLAB_TYPE, SlabType.TOP))
                    }
                    return true
                }
                else -> return false
            }
        }

        val direction = if (startDirection.contains(player)) {
            rotate(propAndDirection.second, startDirection[player]!!.second, allowUp, allowDown)
        } else when (clickSide) { // First rotate
            Direction.UP -> if (allowUp && propAndDirection.second != Direction.UP) Direction.UP else null
            Direction.DOWN -> if (allowDown && propAndDirection.second != Direction.DOWN) Direction.DOWN else null
            propAndDirection.second -> null
            else -> clickSide
        } ?: rotate(propAndDirection.second, propAndDirection.second, allowUp, allowDown)

        var newState = blockState.with(propAndDirection.first, direction)
        if (!startDirection.contains(player)) {
            // Always set clickSide as start
            startDirection[player] = blockPos to when (clickSide) {
                Direction.UP -> if (allowUp) clickSide else propAndDirection.second // fallback to origin direction
                Direction.DOWN -> if (allowDown) clickSide else propAndDirection.second // fallback to origin direction
                else -> clickSide
            }
        } else if (direction == startDirection[player]?.second && blockState.contains(HORIZONTAL_FACING)) { // Magic rotate
            // Torch
            if (block in listOf(WALL_TORCH, SOUL_WALL_TORCH, REDSTONE_WALL_TORCH)) {
                newState = switchTorch(block).defaultState
            }

            // Cycle property
            for (prop in listOf(BLOCK_HALF, FACE, ATTACHMENT)) {
                if (newState!!.contains(prop)) {
                    newState = newState.cycle(prop)
                    break
                }
            }
        }

        world.setBlockState(blockPos, newState)
        newState = Block.postProcessState(newState, world, blockPos)
        newState.neighborUpdate(world, blockPos, newState.block, blockPos, true)
        world.setBlockState(blockPos, newState, 2) // Only send update to client

        return true
    }

    private fun rotate(now: Direction, start: Direction, up: Boolean = true, down: Boolean = true): Direction {
        return when (now) {
            Direction.DOWN -> if (start.axis.isHorizontal) start else Direction.NORTH
            Direction.UP -> if (down) Direction.DOWN else if (start.axis.isHorizontal) start else Direction.NORTH
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST -> {
                val direction = now.rotateYClockwise()
                if (direction == start || direction == Direction.NORTH && (start.axis.isVertical)) when {
                    up -> Direction.UP
                    down -> Direction.DOWN
                    else -> direction
                } else direction
            }
        }
    }

    private fun switchTorch(block: Block) = when (block) {
        TORCH -> WALL_TORCH
        SOUL_TORCH -> SOUL_WALL_TORCH
        REDSTONE_TORCH -> REDSTONE_WALL_TORCH
        WALL_TORCH -> TORCH
        SOUL_WALL_TORCH -> SOUL_TORCH
        REDSTONE_WALL_TORCH -> REDSTONE_TORCH
        else -> throw IllegalArgumentException("Not supported block.")
    }

    // TODO clean up
    private fun connectChest(event: PlayerUseItemOnBlock, chestFacing: Direction, blockPos: BlockPos, blockState: BlockState, opposite: Boolean): Boolean {
        val world = event.context.world
        val anotherPos = blockPos.offset(if (opposite) chestFacing.opposite else chestFacing)
        val anotherState = world.getBlockState(anotherPos)

        if (anotherState.block != blockState.block) return false

        if (
            (anotherState.block == CHEST || anotherState.block == TRAPPED_CHEST) &&
            anotherState.get(CHEST_TYPE) == ChestType.SINGLE &&
            blockState.get(HORIZONTAL_FACING) == anotherState.get(HORIZONTAL_FACING)
        ) {
            world.setBlockState(blockPos, blockState.with(CHEST_TYPE, if (opposite) ChestType.LEFT else ChestType.RIGHT))
            world.setBlockState(anotherPos, anotherState.with(CHEST_TYPE, if (opposite) ChestType.RIGHT else ChestType.LEFT))
            return true
        }

        return false
    }

    private fun spinRail(shape: RailShape) = when (shape) {
        RailShape.NORTH_SOUTH -> RailShape.EAST_WEST
        RailShape.EAST_WEST -> RailShape.NORTH_SOUTH
        RailShape.SOUTH_WEST -> RailShape.NORTH_WEST
        RailShape.NORTH_WEST -> RailShape.NORTH_EAST
        RailShape.NORTH_EAST -> RailShape.SOUTH_EAST
        RailShape.SOUTH_EAST -> RailShape.SOUTH_WEST
        else -> shape
    }
}
