package raven.api.client.opengl.particles

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL31
import raven.api.client.opengl.instanced.Render
import raven.api.client.utils.*
import raven.api.common.math.vector.Vec2

/**
 * Created by r4v3n6101 on 29.04.2016.
 */
class ParticlesRender : Render<Particle>
(
        Integer.getInteger("property.max.particle", 4000),
        84,
        newQuad(),
        DEFAULT_PARTICLE_SHADER
) {

    override fun addAttributes() {
        addAtribute(1, 4, 0)//textureCoords
        addAtribute(2, 3, 4)//translate
        addAtribute(3, 3, 7)//rotation
        addAtribute(4, 3, 10)//scale
        addAtribute(5, 2, 13)//billboardRotation
        addAtribute(6, 2, 15)//lightmap coordinates
        addAtribute(7, 4, 17)//color
    }

    override fun prepare() {
        Minecraft.getMinecraft().entityRenderer.enableLightmap(0.0)
        Minecraft.getMinecraft().renderEngine.bindTexture(ResourceLocation(PARTICLE_RESOURCE_LOCATION))
        GL11.glDisable(GL11.GL_CULL_FACE)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
    }

    override fun finish() {
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        Minecraft.getMinecraft().entityRenderer.disableLightmap(0.0)
    }

    override fun processElements(partialTicks: Float) {
        if (shader !is ParticleShader) {
            throw RuntimeException("Shader error")
        }
        shader.connectTextureUnits()
        shader.loadModelViewProjectionMatrix(projectionMat * viewMat)
        buffer.clear()
        elements.filter { it.shouldRender(Minecraft.getMinecraft().theWorld) }.forEach { collectData(it, partialTicks) }
        buffer.flip()
        updateVbo()
        for (i in 0..7) {
            GL20.glEnableVertexAttribArray(i)
        }
        GL31.glDrawElementsInstanced(GL11.GL_TRIANGLE_STRIP, model.indicesCount, GL11.GL_UNSIGNED_INT, 0, elements.size)
        for (i in 0..7) {
            GL20.glDisableVertexAttribArray(i)
        }
    }

    override fun collectData(element: Particle, partialTicks: Float) {
        element.interpolate(partialTicks)
        val realPos = element.pos
        val particlePos = realPos - renderPosition
        val invertedRotation = Minecraft.getMinecraft().gameSettings.thirdPersonView == 2
        with(element.icon) { //textureCoords
            buffer.put(minU)
            buffer.put(maxU)
            buffer.put(minV)
            buffer.put(maxV)
        }
        particlePos.write(buffer)//translate
        element.rotation.write(buffer)//rotation
        element.scale.write(buffer)//scale
        Vec2(
                if (invertedRotation)
                    RenderManager.instance.playerViewX.toDouble()
                else
                    -RenderManager.instance.playerViewX.toDouble(),
                180 - RenderManager.instance.playerViewY.toDouble()).write(buffer)//billboardRotation
        element.world.getLightmapCoordinates(realPos).write(buffer)//lightmap coords
        element.color.write(buffer)//color
    }

    override fun tick() {
        with(elements) {
            removeIf { it.isDead }
            forEach { it.update() }
        }
    }
}