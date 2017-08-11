package raven.api.common.schematic

import net.minecraft.block.Block
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import raven.api.common.math.matrix.Mat4
import raven.api.common.math.vector.Vec4

class Schematic(compound: NBTTagCompound) {

    val width: Short
    val height: Short
    val length: Short
    val blocks: ByteArray
    val data: ByteArray
    val entities: NBTTagList
    val tileEntities: NBTTagList

    init {
        width = compound.getShort("Width")
        height = compound.getShort("Height")
        length = compound.getShort("Length")
        blocks = compound.getByteArray("Blocks")
        data = compound.getByteArray("Data")
        entities = compound.getTagList("Entities", 10)
        tileEntities = compound.getTagList("TileEntities", 10)
    }

    @Deprecated("It isn't stable")
    fun build(w: World, mat: Mat4) {
        fun spawnTile(tag: NBTTagCompound) {
            val tile = TileEntity.createAndLoadEntity(tag)
            val pos = mat * Vec4(tile.xCoord, tile.yCoord, tile.zCoord, 1)
            tile.xCoord = pos.x.toInt()
            tile.yCoord = pos.y.toInt()
            tile.zCoord = pos.z.toInt()
            w.addTileEntity(tile)
        }

        fun spawnBlock(x: Int, y: Int, z: Int) {
            val address = y * width * length + z * width + x
            val id = blocks[address].toInt()
            val data = data[address].toInt()
            val pos = mat * Vec4(x, y, z, 1)
            val buildX = pos.x.toInt()
            val buildY = pos.y.toInt()
            val buildZ = pos.z.toInt()


            val block = Block.getBlockById(id)
            w.setBlock(buildX, buildY, buildZ, block)
            w.setBlockMetadataWithNotify(buildX, buildY, buildZ, data, 2)
        }

        val height = height.toInt()
        val width = width.toInt()
        val length = length.toInt()
        for (y in 0..height - 1) {
            for (z in 0..length - 1) {
                for (x in 0..width - 1) {
                    spawnBlock(x, y, z)
                }
            }
        }
        val tiles = tileEntities
        for (i in 0..tiles.tagCount() - 1) {
            spawnTile(tiles.getCompoundTagAt(i))
        }
    }
}