package raven.api.common.utils

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.IBlockAccess
import raven.api.common.math.vector.Vec3

/**
 * Created by r4v3n6101 on 09.06.2016.
 */

data class BlockData(
        val world: IBlockAccess,
        val pos: Vec3
) {
    val block: Block?
        get() {
            val block = world.getBlock(
                    pos.x.toInt(),
                    pos.y.toInt(),
                    pos.z.toInt()
            )
            return if (block == Blocks.air) null else block
        }
    val metadata: Int
        get() = world.getBlockMetadata(pos.x.toInt(), pos.y.toInt(), pos.z.toInt())
    val tileEntity: TileEntity?
        get() = world.getTileEntity(pos.x.toInt(), pos.y.toInt(), pos.z.toInt())

    val x: Int
        get() = pos.x.toInt()

    val y: Int
        get() = pos.y.toInt()

    val z: Int
        get() = pos.z.toInt()
}