package raven.api.opengl.fbo

import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL32
import java.nio.ByteBuffer
import java.util.*

/**
 * Created by Raven6101 on 28.03.2016.
 */
object FBO {
    private val framebuffers = ArrayList<Framebuffer>()

    fun switchToDefault() {
        Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)
    }

    fun free() {
        framebuffers.forEach({ it.free() })
    }

    fun bindFramebuffer(framebuffer: Framebuffer) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.fboId)
        GL11.glViewport(0, 0, framebuffer.width, framebuffer.height)
    }

    private fun createFramebuffer(): Int {
        val framebuffer = GL30.glGenFramebuffers()
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer)
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0)
        return framebuffer
    }

    fun renderToFramebuffer(framebuffer: Framebuffer, body: () -> Unit) {
        bindFramebuffer(framebuffer)
        body()
        switchToDefault()
    }

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

    fun createFramebuffer(width: Int, height: Int): Framebuffer {
        val fboId = createFramebuffer()//create a new
        val textureId = createTextureAttachment(width, height)//attach texture
        val depthBufferId = createDepthBufferAttachment(width, height)//attach renderbuffer
        switchToDefault()//reset to mc framebuffer(id is 1)
        val framebuffer = Framebuffer(width, height, fboId, textureId, depthBufferId)
        framebuffers.add(framebuffer)
        println("Create a new FBO, $framebuffer")
        return framebuffer
    }

    private fun createDepthBufferAttachment(width: Int, height: Int): Int {
        val renderbuffer = GL30.glGenRenderbuffers()
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderbuffer)//bind renderbuffer
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width,
                height)//allocate data
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                GL30.GL_RENDERBUFFER, renderbuffer)//fill with data
        return renderbuffer
    }
}