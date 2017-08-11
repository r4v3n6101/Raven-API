package raven.api.common.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.MovingObjectPosition
import raven.api.common.math.vector.Vec3
import java.time.LocalTime

/**
 * Ray trace from player's eyes
 */
fun rayTrace(player: EntityPlayer, length: Float): MovingObjectPosition? {
    val posVec = Vec3(player.posX, player.posY + 1.62 - player.yOffset, player.posZ)
    val look = player.lookVec.toVec3() * length
    val lookVec = posVec + look
    return player.worldObj.rayTraceBlocks(posVec.toDefVec(), lookVec.toDefVec(), true)
}

/**
 * Add item stack to inventory. If there is no empty slot in inventory, drop item
 */
fun addToInventory(stack: ItemStack, player: EntityPlayer) {
    if (!player.inventory.addItemStackToInventory(stack)) {
        player.entityDropItem(stack, 0f)
    }
}

/**
 * Remove item stack from inventory
 */
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

/**
 * Convert time into ticks
 */
fun timeToTicks(time: LocalTime): Int {
    var time2 = time
    time2 = time2.minusHours(6)//minecraft shifting
    val minutes = (time2.hour * 60 + time2.minute).toFloat()
    return (minutes / 1440f * 24000 % 24000).toInt()
}

/**
 * Convert ticks into time
 */
fun ticksToTime(time: Int): LocalTime {
    var time2 = time
    time2 %= 24000
    val hour = time2 / 1000
    val minutes = time2 % 1000.0 / 16.666666666666668
    return LocalTime.of(hour, Math.round(minutes).toInt()).plusHours(6)
}

/**
 * Instance of gson
 */
val gsonInstance: Gson = GsonBuilder().setPrettyPrinting().create()

inline fun <reified T : Any> Gson.fromJson(json: String): T = fromJson(json, T::class.java)