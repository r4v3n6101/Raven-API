package raven.api.common.packet

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.network.ByteBufUtils
import cpw.mods.fml.common.network.FMLEventChannel
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint
import cpw.mods.fml.common.network.internal.FMLProxyPacket
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetHandlerPlayServer
import raven.api.client.packet.CPacketCallback
import raven.api.server.packet.SPacketCallback
import java.util.*

class NetworkManager(private val channelName: String) {
    private val channel: FMLEventChannel

    init {
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(channelName)
        channel.register(this)
        channels.put(channelName, this)
    }

    @SubscribeEvent
    fun onServerPacket(e: ServerCustomPacketEvent) {
        val player = (e.handler as NetHandlerPlayServer).playerEntity
        val buf = e.packet.payload()
        serverCallbacks[e.packet.channel()]?.handleServerSide(buf, player.worldObj, player)
    }

    @SubscribeEvent
    fun onClientPacket(event: ClientCustomPacketEvent) {
        val player = Minecraft.getMinecraft().thePlayer
        val buf = event.packet.payload()
        clientCallbacks[event.packet.channel()]?.handleClientSide(buf, player.worldObj, player)
    }

    fun sendToServer(vararg data: Any) {
        channel.sendToServer(createPacket(*data))
    }

    fun sendToClients(vararg data: Any) {
        channel.sendToAll(createPacket(*data))
    }

    fun sendToPlayer(player: EntityPlayerMP, vararg data: Any) {
        channel.sendTo(createPacket(*data), player)
    }

    fun sendToArea(x: Double, y: Double, z: Double, range: Double, dim: Int, vararg data: Any) {
        channel.sendToAllAround(createPacket(*data), TargetPoint(dim, x, y, z, range))
    }

    fun sendToDimension(id: Int, vararg data: Any) {
        channel.sendToDimension(createPacket(*data), id)
    }

    private fun createPacket(vararg data: Any): FMLProxyPacket {
        return createPacket(Unpooled.buffer(32), *data)
    }

    private fun createPacket(buf: ByteBuf, vararg data: Any): FMLProxyPacket {
        data.forEach {
            when (it) {
                is Boolean -> buf.writeBoolean(it)
                is Byte -> buf.writeByte(it.toInt())
                is Short -> buf.writeShort(it.toInt())
                is Int -> buf.writeInt(it)
                is Float -> buf.writeFloat(it)
                is Double -> buf.writeDouble(it)
                is Long -> buf.writeLong(it)
                is Char -> buf.writeChar(it.toInt())
                is String -> ByteBufUtils.writeUTF8String(buf, it)
                is ItemStack -> ByteBufUtils.writeItemStack(buf, it)
                is NBTTagCompound -> ByteBufUtils.writeTag(buf, it)
            }
        }
        return FMLProxyPacket(buf, channelName)
    }

    companion object {
        private val clientCallbacks = HashMap<String, CPacketCallback>()
        private val serverCallbacks = HashMap<String, SPacketCallback>()
        private val channels = HashMap<String, NetworkManager>()

        fun registerServerCallback(id: String, callback: SPacketCallback) {
            serverCallbacks.put(id, callback)
        }

        @JvmStatic fun getManager(id: String): NetworkManager {
            return channels[id] as NetworkManager
        }

        @JvmStatic fun registerPackets(vararg ids: String) {
            for (id in ids) {
                NetworkManager(id)
            }
        }

        fun registerClientCallback(id: String, callback: CPacketCallback) {
            clientCallbacks.put(id, callback)
        }
    }
}
