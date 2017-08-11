package raven.api.common.utils

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.World
import raven.api.common.math.vector.Vec3

/**
 * @throws NullPointerException if target is out of reach
 */
@Throws(NullPointerException::class)
fun Entity.huntFor(target: Entity, maxDistance: Float): Boolean {
    if (target.getDistanceSqToEntity(this) > maxDistance * maxDistance) {
        throw NullPointerException("Entity is out of reach")
    }
    val targetVec = Vec3(target.posX, target.posY + target.eyeHeight, target.posZ)
    val currentVec = Vec3(posX, posY + eyeHeight, posZ)

    var diff = (targetVec - currentVec) / maxDistance
    val length = !diff
    var d5 = 1.0 - length

    if (d5 > 0.0) {
        d5 *= d5
        diff /= length * d5 * 0.1
        motionX += diff.x
        motionY += diff.y
        motionZ += diff.z
        return true
    }
    return false
}

fun String.countSymbols(): Map<Char, Int> {
    var map = emptyMap<Char, Int>()
    forEach {
        if (map.containsKey(it)) {
            val oldValue = map[it] as Int
            map += it to (oldValue + 1)
        } else {
            map += it to 1
        }
    }
    return map
}

fun Vec3.toDefVec() = net.minecraft.util.Vec3.createVectorHelper(x.toDouble(), y.toDouble(), z.toDouble())

fun net.minecraft.util.Vec3.toVec3() = Vec3(xCoord, yCoord, zCoord)

fun Float.factorial(): Float = if (this <= 1F) 1F else this * (this - 1F).factorial()

fun Int.factorial(): Int = if (this <= 1) 1 else this * (this - 1).factorial()

fun Double.factorial(): Double = if (this <= 1) 1.0 else this * (this - 1.0).factorial()

/**
 * Look at pos
 * @author nikita488
 */
fun EntityLivingBase.lookAt(pos: Vec3) {
    val diff = pos - getPosition(1F).toVec3()
    val dist = Math.sqrt(diff.z.toDouble() * diff.z + diff.x * diff.x)
    val yaw = Math.toDegrees(Math.atan2(diff.x.toDouble(), diff.z.toDouble())).toFloat()
    val pitch = Math.toDegrees(Math.atan2(diff.y.toDouble(), dist)).toFloat()

    rotationYaw = -yaw
    prevRotationYaw = -yaw
    rotationPitch = -pitch
    prevRotationPitch = -pitch
}

fun World.getBlockWithData(pos: Vec3) = BlockData(this, pos)

/**
 * Create expanded AABB from vec3
 * By default create 1x1x1 cube
 */
@JvmOverloads
fun Vec3.toAxisAlignedBB(radius: Double = 0.5): AxisAlignedBB {
    return AxisAlignedBB.getBoundingBox(
            this.x.toDouble(),
            this.y.toDouble(),
            this.z.toDouble(),
            this.x.toDouble(),
            this.y.toDouble(),
            this.z.toDouble()
    ).expand(
            radius, radius, radius
    )
}

val AxisAlignedBB.roundedValues: List<Vec3>
    get() {
        val list = mutableListOf<Vec3>()
        for (x in minX.toInt()..maxX.toInt()) {
            for (y in minY.toInt()..maxY.toInt()) {
                for (z in minZ.toInt()..maxZ.toInt()) {
                    list += Vec3(x, y, z)
                }
            }
        }
        return list
    }

operator fun AxisAlignedBB.iterator() = roundedValues.listIterator()