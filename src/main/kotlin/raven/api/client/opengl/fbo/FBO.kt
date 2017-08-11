package raven.api.client.opengl.fbo

import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL32
import java.nio.ByteBuffer

/**
 * Created by r4v3n6101 on 28.03.2016.
 */
object FBO {
    private val framebuffers = mutableListOf<Framebuffer>()

    /**
     * Reset to default
     */
    fun switchToDefault(window: Boolean) {
        if (window) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
        } else {
            Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)
        }
    }

    fun free() {
        framebuffers.forEach { it.free() }
        framebuffers.clear()
    }

    /**
     * Switch to custom framebuffer
     */
    fun bindFramebuffer(framebuffer: Framebuffer) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.fboId)
        GL11.glViewport(0, 0, framebuffer.width, framebuffer.height)
    }

    /**
     * Generate id and bind it
     */
    private fun createFramebuffer(): Int {
        val framebuffer = GL30.glGenFramebuffers()
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer)
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0)
        return framebuffer
    }

    /**
     * Render into framebuffer
     */
    @JvmOverloads
    inline fun renderToFramebuffer(framebuffer: Framebuffer, switchToWindow: Boolean = false, body: () -> Unit) {
        bindFramebuffer(framebuffer)
        body()
        switchToDefault(switchToWindow)
    }

    /**
     * Create color texture for framebuffer
     */
    private fun createTextureAttachment(width: Int, height: Int): Int {
        val texture = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height,
                0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, null as ByteBuffer?)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
                texture, 0)
        return texture
    }

    /**
     * Create depth texture for framebuffer
     */
    private fun createDepthTextureAttachment(width: Int, height: Int): Int {
        val texture = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height,
                0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, null as ByteBuffer?)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                texture, 0)
        return texture
    }

    /**
     * Make a new
     * @param width width of framebuffer
     * @param height height of framebuffer
     * @param switchToWindow if true buffer will be switched to window(0) after all operation
     * @return object of custom framebuffer
     */
    @JvmOverloads
    fun createFramebuffer(width: Int, height: Int, switchToWindow: Boolean = false, depthTexture: Boolean = false): Framebuffer {
        val fboId = createFramebuffer()
        val textureId =
                if (depthTexture)
                    createDepthTextureAttachment(width, height)
                else
                    createTextureAttachment(width, height)
        val renderbuffer = createRenderbufferAttachment(width, height)
        switchToDefault(switchToWindow)
        val framebuffer = Framebuffer(width, height, fboId, textureId, renderbuffer)
        framebuffers.add(framebuffer)
        println("Create new FBO, $framebuffer")
        return framebuffer
    }

    /**
     * Create renderbuffer
     * @return id of renderbuffer
     */
    private fun createRenderbufferAttachment(width: Int, height: Int): Int {
        val renderbuffer = GL30.glGenRenderbuffers()
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderbuffer)
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width,
                height)
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                GL30.GL_RENDERBUFFER, renderbuffer)
        return renderbuffer
    }
}