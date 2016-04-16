package example

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.culling.ClippingHelperImpl
import net.minecraft.client.renderer.culling.Frustrum
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.common.MinecraftForge
import org.lwjgl.opengl.GL11
import raven.api.events.ExitEvent
import raven.api.opengl.fbo.FBO
import raven.api.opengl.fbo.Framebuffer
import raven.api.opengl.vbo.VBO
import raven.api.opengl.vbo.model.RawModel
import raven.api.utils.client.ClientUtils
import java.io.IOException

/**
 * Created by Raven6101 on 09.04.2016.
 */
@Mod(name = "test", modid = "test", version = "1.0")
class TestMod {
    private lateinit var model: RawModel
    @Mod.EventHandler
    fun onStart(event: FMLPostInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(this)
        FMLCommonHandler.instance().bus().register(this)
        try {
            ClientUtils.loadDefaultVboShader()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val vertices = floatArrayOf(
                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,

                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,

                0.5f, 0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,

                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,

                -0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, 0.5f,

                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, 0.5f
        )

        val textureCoords = floatArrayOf(
                0F, 0F,
                0F, 1F,
                1F, 1F,
                1F, 0F,
                0F, 0F,
                0F, 1F,
                1F, 1F,
                1F, 0F,
                0F, 0F,
                0F, 1F,
                1F, 1F,
                1F, 0F,
                0F, 0F,
                0F, 1F,
                1F, 1F,
                1F, 0F,
                0F, 0F,
                0F, 1F,
                1F, 1F,
                1F, 0F,
                0F, 0F,
                0F, 1F,
                1F, 1F,
                1F, 0F
        )
        val indices = intArrayOf(
                0, 1, 3,
                3, 1, 2,
                4, 5, 7,
                7, 5, 6,
                8, 9, 11,
                11, 9, 10,
                12, 13, 15,
                15, 13, 14,
                16, 17, 19,
                19, 17, 18,
                20, 21, 23,
                23, 21, 22
        )
        model = VBO.loadToVAO(vertices, indices, textureCoords)
    }

    @SubscribeEvent
    fun onRender(event: TickEvent.RenderTickEvent) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            GL11.glPushMatrix()
            FBO.renderToFramebuffer(fbo) {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT);
                GL11.glColor4f(1f, 1f, 1f, 1f);
                Minecraft.getMinecraft().renderViewEntity = fboEnt
                renderWorld(event.renderTickTime, System.nanoTime())
            }
            GL11.glPopMatrix()

            Minecraft.getMinecraft().renderViewEntity = Minecraft.getMinecraft().thePlayer

            //TODO : shader
            GL11.glPushMatrix()
            GL11.glColorMask(true, true, true, false)
            GL11.glDisable(2929)
            GL11.glDepthMask(false)
            GL11.glMatrixMode(5889)
            GL11.glLoadIdentity()
            GL11.glOrtho(0.0, fbo.width.toDouble(), fbo.height.toDouble(), 0.0, 1000.0, 3000.0)
            GL11.glMatrixMode(5888)
            GL11.glLoadIdentity()
            GL11.glTranslatef(0.0f, 0.0f, -2000.0f)
            GL11.glViewport(0, 0, fbo.width, fbo.height)
            GL11.glEnable(3553)
            GL11.glDisable(2896)
            GL11.glDisable(3008)
            GL11.glDisable(3042)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            GL11.glEnable(2903)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbo.textureId)
            val f = fbo.width.toFloat()
            val f1 = fbo.height.toFloat()
            val f2 = 1
            val f3 = 1
            val tessellator = Tessellator.instance
            tessellator.startDrawingQuads()
            tessellator.setColorOpaque_I(-1)
            tessellator.addVertexWithUV(0.0, f1.toDouble(), 0.0, 0.0, 0.0)
            tessellator.addVertexWithUV(f.toDouble(), f1.toDouble(), 0.0, f2.toDouble(), 0.0)
            tessellator.addVertexWithUV(f.toDouble(), 0.0, 0.0, f2.toDouble(), f3.toDouble())
            tessellator.addVertexWithUV(0.0, 0.0, 0.0, 0.0, f3.toDouble())
            tessellator.draw()
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
            GL11.glDepthMask(true)
            GL11.glColorMask(true, true, true, true)
            GL11.glPopMatrix()
        }
    }

    @SubscribeEvent
    fun onExit(event: ExitEvent) {
        FBO.free()
        VBO.free()
    }

    val fbo: Framebuffer by lazy {
        FBO.createFramebuffer(600, 600)
    }

    companion object {
        var fboEnt: EntityLivingBase? = null
    }

    // <editor-fold defaultstate="collapsed" desc="COPY-PASTE CODE FROM EntityRenderer">
    fun renderWorld(p_78471_1_: Float, p_78471_2_: Long) {
        Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.startSection("lightTex")

        if (Minecraft.getMinecraft().entityRenderer.lightmapUpdateNeeded) {
            Minecraft.getMinecraft().entityRenderer.updateLightmap(p_78471_1_)
        }

        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.5f)

        if (Minecraft.getMinecraft().entityRenderer.mc.renderViewEntity == null) {
            Minecraft.getMinecraft().entityRenderer.mc.renderViewEntity = Minecraft.getMinecraft().entityRenderer.mc.thePlayer
        }

        Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("pick")
        Minecraft.getMinecraft().entityRenderer.getMouseOver(p_78471_1_)
        val entitylivingbase = Minecraft.getMinecraft().entityRenderer.mc.renderViewEntity
        val renderglobal = Minecraft.getMinecraft().entityRenderer.mc.renderGlobal
        val effectrenderer = Minecraft.getMinecraft().entityRenderer.mc.effectRenderer
        val d0 = entitylivingbase.lastTickPosX + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * p_78471_1_.toDouble()
        val d1 = entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * p_78471_1_.toDouble()
        val d2 = entitylivingbase.lastTickPosZ + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * p_78471_1_.toDouble()
        Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("center")

        for (j in 0..1) {
            if (Minecraft.getMinecraft().entityRenderer.mc.gameSettings.anaglyph) {
                EntityRenderer.anaglyphField = j

                if (EntityRenderer.anaglyphField == 0) {
                    GL11.glColorMask(false, true, true, false)
                } else {
                    GL11.glColorMask(true, false, false, false)
                }
            }

            Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("clear")
            GL11.glViewport(0, 0, fbo.width, fbo.height)
            Minecraft.getMinecraft().entityRenderer.updateFogColor(p_78471_1_)
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
            GL11.glEnable(GL11.GL_CULL_FACE)
            Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("camera")
            Minecraft.getMinecraft().entityRenderer.setupCameraTransform(p_78471_1_, j)
            ActiveRenderInfo.updateRenderInfo(Minecraft.getMinecraft().entityRenderer.mc.thePlayer, Minecraft.getMinecraft().entityRenderer.mc.gameSettings.thirdPersonView == 2)
            Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("frustrum")
            ClippingHelperImpl.getInstance()

            if (Minecraft.getMinecraft().entityRenderer.mc.gameSettings.renderDistanceChunks >= 4) {
                Minecraft.getMinecraft().entityRenderer.setupFog(-1, p_78471_1_)
                Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("sky")
                renderglobal.renderSky(p_78471_1_)
            }

            GL11.glEnable(GL11.GL_FOG)
            Minecraft.getMinecraft().entityRenderer.setupFog(1, p_78471_1_)

            if (Minecraft.getMinecraft().entityRenderer.mc.gameSettings.ambientOcclusion != 0) {
                GL11.glShadeModel(GL11.GL_SMOOTH)
            }

            Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("culling")
            val frustrum = Frustrum()
            frustrum.setPosition(d0, d1, d2)
            Minecraft.getMinecraft().entityRenderer.mc.renderGlobal.clipRenderersByFrustum(frustrum, p_78471_1_)

            if (j == 0) {
                Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("updatechunks")

                while (!Minecraft.getMinecraft().entityRenderer.mc.renderGlobal.updateRenderers(entitylivingbase, false) && p_78471_2_ != 0L) {
                    val k = p_78471_2_ - System.nanoTime()

                    if (k < 0L || k > 1000000000L) {
                        break
                    }
                }
            }

            if (entitylivingbase.posY < 128.0) {
                Minecraft.getMinecraft().entityRenderer.renderCloudsCheck(renderglobal, p_78471_1_)
            }

            Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("prepareterrain")
            Minecraft.getMinecraft().entityRenderer.setupFog(0, p_78471_1_)
            GL11.glEnable(GL11.GL_FOG)
            Minecraft.getMinecraft().entityRenderer.mc.textureManager.bindTexture(TextureMap.locationBlocksTexture)
            RenderHelper.disableStandardItemLighting()
            Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("terrain")
            GL11.glMatrixMode(GL11.GL_MODELVIEW)
            GL11.glPushMatrix()
            renderglobal.sortAndRender(entitylivingbase, 0, p_78471_1_.toDouble())
            GL11.glShadeModel(GL11.GL_FLAT)
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f)
            var entityplayer: EntityPlayer

            if (Minecraft.getMinecraft().entityRenderer.debugViewDirection == 0) {
                GL11.glMatrixMode(GL11.GL_MODELVIEW)
                GL11.glPopMatrix()
                GL11.glPushMatrix()
                RenderHelper.enableStandardItemLighting()
                Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("entities")
                net.minecraftforge.client.ForgeHooksClient.setRenderPass(0)
                renderglobal.renderEntities(entitylivingbase, frustrum, p_78471_1_)
                net.minecraftforge.client.ForgeHooksClient.setRenderPass(0)
                RenderHelper.disableStandardItemLighting()
                Minecraft.getMinecraft().entityRenderer.disableLightmap(p_78471_1_.toDouble())
                GL11.glMatrixMode(GL11.GL_MODELVIEW)
                GL11.glPopMatrix()
                GL11.glPushMatrix()
            }

            GL11.glMatrixMode(GL11.GL_MODELVIEW)
            GL11.glPopMatrix()

            Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("destroyProgress")
            GL11.glEnable(GL11.GL_BLEND)
            OpenGlHelper.glBlendFunc(770, 1, 1, 0)
            renderglobal.drawBlockDamageTexture(Tessellator.instance, entitylivingbase, p_78471_1_)
            GL11.glDisable(GL11.GL_BLEND)

            if (Minecraft.getMinecraft().entityRenderer.debugViewDirection == 0) {
                Minecraft.getMinecraft().entityRenderer.enableLightmap(p_78471_1_.toDouble())
                Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("litParticles")
                effectrenderer.renderLitParticles(entitylivingbase, p_78471_1_)
                RenderHelper.disableStandardItemLighting()
                Minecraft.getMinecraft().entityRenderer.setupFog(0, p_78471_1_)
                Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("particles")
                effectrenderer.renderParticles(entitylivingbase, p_78471_1_)
                Minecraft.getMinecraft().entityRenderer.disableLightmap(p_78471_1_.toDouble())
            }

            GL11.glDepthMask(false)
            GL11.glEnable(GL11.GL_CULL_FACE)
            Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("weather")
            Minecraft.getMinecraft().entityRenderer.renderRainSnow(p_78471_1_)
            GL11.glDepthMask(true)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_CULL_FACE)
            OpenGlHelper.glBlendFunc(770, 771, 1, 0)
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f)
            Minecraft.getMinecraft().entityRenderer.setupFog(0, p_78471_1_)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDepthMask(false)
            Minecraft.getMinecraft().entityRenderer.mc.textureManager.bindTexture(TextureMap.locationBlocksTexture)

            if (Minecraft.getMinecraft().entityRenderer.mc.gameSettings.fancyGraphics) {
                Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("water")

                if (Minecraft.getMinecraft().entityRenderer.mc.gameSettings.ambientOcclusion != 0) {
                    GL11.glShadeModel(GL11.GL_SMOOTH)
                }

                GL11.glEnable(GL11.GL_BLEND)
                OpenGlHelper.glBlendFunc(770, 771, 1, 0)

                if (Minecraft.getMinecraft().entityRenderer.mc.gameSettings.anaglyph) {
                    if (EntityRenderer.anaglyphField == 0) {
                        GL11.glColorMask(false, true, true, true)
                    } else {
                        GL11.glColorMask(true, false, false, true)
                    }

                    renderglobal.sortAndRender(entitylivingbase, 1, p_78471_1_.toDouble())
                } else {
                    renderglobal.sortAndRender(entitylivingbase, 1, p_78471_1_.toDouble())
                }

                GL11.glDisable(GL11.GL_BLEND)
                GL11.glShadeModel(GL11.GL_FLAT)
            } else {
                Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("water")
                renderglobal.sortAndRender(entitylivingbase, 1, p_78471_1_.toDouble())
            }

            if (Minecraft.getMinecraft().entityRenderer.debugViewDirection == 0)
            //Only render if render pass 0 happens as well.
            {
                RenderHelper.enableStandardItemLighting()
                Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("entities")
                ForgeHooksClient.setRenderPass(1)
                renderglobal.renderEntities(entitylivingbase, frustrum, p_78471_1_)
                ForgeHooksClient.setRenderPass(-1)
                RenderHelper.disableStandardItemLighting()
            }

            GL11.glDepthMask(true)
            GL11.glEnable(GL11.GL_CULL_FACE)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_FOG)

            if (entitylivingbase.posY >= 128.0) {
                Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("aboveClouds")
                Minecraft.getMinecraft().entityRenderer.renderCloudsCheck(renderglobal, p_78471_1_)
            }

            Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endStartSection("FRenderLast")
            ForgeHooksClient.dispatchRenderLast(renderglobal, p_78471_1_)

            if (!Minecraft.getMinecraft().entityRenderer.mc.gameSettings.anaglyph) {
                Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endSection()
                return
            }
        }

        GL11.glColorMask(true, true, true, false)
        Minecraft.getMinecraft().entityRenderer.mc.mcProfiler.endSection()
    }
    // </editor-fold>
}
