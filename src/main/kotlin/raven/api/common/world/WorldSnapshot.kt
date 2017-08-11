package raven.api.common.world

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.ChunkCache
import net.minecraft.world.World
import raven.api.common.utils.iterator

/**
 * Created by r4v3n6101 on 12.06.2016.
 */
data class WorldSnapshot(
        val world: World,
        val worldBounds: AxisAlignedBB
) {
    val chunkCache: ChunkCache
    val entities = mutableListOf<Entity>()
    val tiles = mutableListOf<TileEntity>()

    init {
        val chunksLoadingRadius = Math.abs(worldBounds.maxX - worldBounds.minX) / 2
        chunkCache = ChunkCache(
                world,
                worldBounds.minX.toInt(),
                worldBounds.minY.toInt(),
                worldBounds.minZ.toInt(),
                worldBounds.maxX.toInt(),
                worldBounds.maxY.toInt(),
                worldBounds.maxZ.toInt(),
                chunksLoadingRadius.toInt() * 16
        )

        world.getEntitiesWithinAABB(EntityLivingBase::class.java, worldBounds).forEach { entities += it as Entity }

        for (pos in worldBounds) {
            tiles += world.getTileEntity(pos.x.toInt(), pos.y.toInt(), pos.z.toInt())
        }
        println("Took snapshot, $worldBounds")
    }
}