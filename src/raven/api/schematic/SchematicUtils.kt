package raven.api.schematic

import net.minecraft.block.Block
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

object SchematicUtils {

    @Deprecated("It isn't stable")
    fun build(w: World, schematic: Schematic, buildX: Int, buildY: Int, buildZ: Int) {
        val height = schematic.height.toInt()
        val width = schematic.width.toInt()
        val length = schematic.length.toInt()
        for (y in 0..height - 1) {
            for (z in 0..length - 1) {
                for (x in 0..width - 1) {
                    val address = y * width * length + z * width + x
                    val id = schematic.blocks[address]
                    val data = schematic.data[address]
                    val block = Block.getBlockById(id.toInt())
                    w.setBlock(buildX + x, buildY + y, buildZ + z, block)
                    w.setBlockMetadataWithNotify(buildX + x, buildY + y, buildZ + z, data.toInt(), 2)
                }
            }
        }
        val tiles = schematic.tileEntities
        for (i in 0..tiles.tagCount() - 1) {
            val tileTag = tiles.getCompoundTagAt(i)
            val tile = TileEntity.createAndLoadEntity(tileTag)
            w.addTileEntity(tile)
        }
    }
}
