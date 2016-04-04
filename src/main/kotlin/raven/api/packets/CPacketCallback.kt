package raven.api.packets

import io.netty.buffer.ByteBuf
import net.minecraft.client.entity.EntityClientPlayerMP
import net.minecraft.world.World

/**
 * Created by Raven6101 on 30.01.2016.
 */
interface CPacketCallback {
    fun handleClientSide(buf: ByteBuf, world: World, player: EntityClientPlayerMP)
}