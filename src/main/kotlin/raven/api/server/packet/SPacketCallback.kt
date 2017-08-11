package raven.api.server.packet

import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.World

/**
 * Created by r4v3n6101 on 30.01.2016.
 */
interface SPacketCallback {
    fun handleServerSide(buf: ByteBuf, world: World, player: EntityPlayerMP)
}