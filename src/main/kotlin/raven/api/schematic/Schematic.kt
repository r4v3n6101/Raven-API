package raven.api.schematic

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

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
}