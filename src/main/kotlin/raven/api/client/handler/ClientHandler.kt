package raven.api.client.handler

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.relauncher.Side
import net.minecraft.client.multiplayer.WorldClient
import net.minecraftforge.common.MinecraftForge
import raven.api.client.events.ExitEvent

/**
 * Created by r4v3n6101 on 19.03.2016.
 */
object ClientHandler {

    fun onPreClientTick(worldClient: WorldClient): Boolean {
        return FMLCommonHandler.instance().bus().post(TickEvent.WorldTickEvent(Side.CLIENT, TickEvent.Phase.START, worldClient))
    }

    fun onPostClientTick(worldClient: WorldClient): Boolean {
        return FMLCommonHandler.instance().bus().post(TickEvent.WorldTickEvent(Side.CLIENT, TickEvent.Phase.END, worldClient))
    }

    fun onExit(): Boolean {
        return MinecraftForge.EVENT_BUS.post(ExitEvent())
    }
}
