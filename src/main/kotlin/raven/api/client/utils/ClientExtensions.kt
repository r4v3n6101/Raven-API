package raven.api.client.utils

import net.minecraft.client.Minecraft
import net.minecraft.util.MathHelper
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import raven.api.common.math.vector.Vec2
import raven.api.common.math.vector.Vec3
import java.io.InputStream

fun World.getLightmapCoordinates(pos: Vec3): Vec2 {
    val x = MathHelper.floor_float(pos.x)
    val z = MathHelper.floor_float(pos.z)
    if (blockExists(x, 0, z)) {
        val y = MathHelper.floor_float(pos.y)
        val worldLight = getLightBrightnessForSkyBlocks(x, y, z, 0)
        val j = worldLight % 65536
        val k = worldLight / 65536
        return (Vec2(j, k) + 8) / 256
    } else {
        return Vec2(0)
    }
}

val ResourceLocation.inputStream: InputStream?
    get() = Minecraft.getMinecraft().resourceManager.getResource(this).inputStream