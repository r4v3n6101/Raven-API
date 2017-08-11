package raven.api.client

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import org.lwjgl.opengl.GL11
import raven.api.client.utils.PARTICLE_RESOURCE_LOCATION
import raven.api.client.utils.particleEngine
import raven.api.client.utils.particlesAtlas
import raven.api.common.Proxy
import net.minecraft.client.Minecraft.getMinecraft as mc

/**
 * Created by r4v3n6101 on 04.06.2016.
 */
class ClientProxy : Proxy() {

    override fun preInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(this)
        FMLCommonHandler.instance().bus().register(this)
    }

    override fun init(event: FMLInitializationEvent) {
    }

    override fun postInit(event: FMLPostInitializationEvent) {
        mc().renderEngine.loadTextureMap(ResourceLocation(PARTICLE_RESOURCE_LOCATION), particlesAtlas)
    }

    @SubscribeEvent
    fun worldLastRender(event: RenderWorldLastEvent) {
        GL11.glPushMatrix()
        particleEngine.render(event.partialTicks)
        GL11.glPopMatrix()
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (mc().theWorld != null && !mc().isGamePaused) {
            particleEngine.tick()
        }
    }
}