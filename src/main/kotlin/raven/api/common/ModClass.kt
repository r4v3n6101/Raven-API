package raven.api.common

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.SidedProxy
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.Mod.EventHandler as handler

/**
 * Created by r4v3n6101 on 04.06.2016.
 */
@Mod(modid = "raven-api", name = "Raven-API", version = "1.0")
class ModClass {
    companion object {
        @SidedProxy(clientSide = "raven.api.client.ClientProxy", serverSide = "raven.api.server.ServerProxy")
        lateinit var proxy: Proxy
    }

    @Mod.EventHandler fun preInit(event: FMLPreInitializationEvent) = proxy.preInit(event)

    @Mod.EventHandler fun init(event: FMLInitializationEvent) = proxy.init(event)

    @Mod.EventHandler fun postInit(event: FMLPostInitializationEvent) = proxy.postInit(event)
}