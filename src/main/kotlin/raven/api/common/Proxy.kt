package raven.api.common

import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent

/**
 * Created by r4v3n6101 on 04.06.2016.
 */
open class Proxy {
    open fun preInit(event: FMLPreInitializationEvent) {
    }

    open fun init(event: FMLInitializationEvent) {
    }

    open fun postInit(event: FMLPostInitializationEvent) {
    }
}