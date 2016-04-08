package raven.api.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import raven.api.utils.math.vector.Matrix4f
import raven.api.utils.math.vector.Vector3f
import java.time.LocalTime

/**
 * Created by Raven6101 on 22.03.2016.
 */
object CommonUtils {

    fun rayTrace(player: EntityPlayer, length: Double): MovingObjectPosition {
        val posVec = Vec3.createVectorHelper(player.posX, player.posY + 1.62 - player.yOffset, player.posZ)
        val look = player.lookVec
        val lookVec = posVec.addVector(look.xCoord * length, look.yCoord * length, look.zCoord * length)
        return player.worldObj.rayTraceBlocks(posVec, lookVec, true)
    }

    val gsonInstance: Gson = GsonBuilder().setPrettyPrinting().create()

    object TimeUtils {

        fun timeToTicks(time: LocalTime): Int {
            var time2 = time
            time2 = time2.minusHours(6)//minecraft shifting
            val minutes = (time2.hour * 60 + time2.minute).toFloat()
            return (minutes / 1440f * 24000 % 24000).toInt()
        }

        fun ticksToTime(time: Int): LocalTime {
            var time2 = time
            time2 %= 24000
            val hour = time2 / 1000
            val minutes = time2 % 1000.0 / 16.666666666666668
            return LocalTime.of(hour, Math.round(minutes).toInt()).plusHours(6)
        }
    }

    object MathUtils {

        fun toVec3(v: Vector3f): Vec3 {
            return Vec3.createVectorHelper(v.x.toDouble(), v.y.toDouble(), v.z.toDouble())
        }

        fun interpolate(prev: Float, current: Float, k: Float): Float {
            return prev + (current - prev) * k
        }

        fun createTransformationMatrix(scale: Float, translate: Vector3f, rotate: Vector3f): Matrix4f {
            val matrix4f = Matrix4f()
            matrix4f.translate(translate)
            matrix4f.rotate(Math.toRadians(rotate.x.toDouble()).toFloat(), Vector3f(1f, 0f, 0f))
            matrix4f.rotate(Math.toRadians(rotate.y.toDouble()).toFloat(), Vector3f(0f, 1f, 0f))
            matrix4f.rotate(Math.toRadians(rotate.z.toDouble()).toFloat(), Vector3f(0f, 0f, 1f))
            matrix4f.scale(Vector3f(scale, scale, scale))
            return matrix4f
        }
    }

    object GameUtils {

        fun addToInventory(stack: ItemStack, player: EntityPlayer) {
            if (!player.inventory.addItemStackToInventory(stack)) {
                player.entityDropItem(stack, 0f)
            }
        }

        fun consumeItemStack(player: EntityPlayer, stack: ItemStack) {
            for (i in player.inventory.mainInventory.indices) {
                val invStack = player.inventory.mainInventory[i]
                if (invStack != null) {
                    if (ItemStackEquals.getWrapper(stack).equals(ItemStackEquals.getWrapper(invStack))) {
                        if (invStack.stackSize > 1) {
                            invStack.stackSize--
                        } else {
                            player.inventory.setInventorySlotContents(i, null)
                        }
                        break
                    }
                }
            }
        }
    }
}
