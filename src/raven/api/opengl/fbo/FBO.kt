package raven.api.opengl.fbo

import org.lwjgl.opengl.*
import java.nio.ByteBuffer
import java.util.*

/**
 * Created by Raven6101 on 28.03.2016.
 */
object FBO {
    private val framebuffers = ArrayList<Framebuffer>()

    fun unbindCurrentFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight())
    }

    fun free() {
        framebuffers.forEach({ it.free() })
    }

    fun bindFrameBuffer(frameBuffer: Int, width: Int, height: Int) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer)
        GL11.glViewport(0, 0, width, height)
    }

    private fun createFrameBuffer(): Int {
        val frameBuffer = GL30.glGenFramebuffers()
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer)
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0)
        return frameBuffer
    }

    private fun createTextureAttachment(width: Int, height: Int): Int {
        val texture = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height,
                0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, null as ByteBuffer)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
                texture, 0)
        return texture
    }

    private fun createDepthTextureAttachment(width: Int, height: Int): Int {
        val texture = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height,
                0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, null as ByteBuffer)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                texture, 0)
        return texture
    }

    fun createFBO(width: Int, height: Int): Framebuffer {
        val fboId = createFrameBuffer()
        val textureId = createTextureAttachment(width, height)
        val depthBufferId = createDepthBufferAttachment(width, height)
        unbindCurrentFrameBuffer()
        val framebuffer = Framebuffer(width, height, fboId, textureId, depthBufferId)
        framebuffers.add(framebuffer)
        return framebuffer
    }

    private fun createDepthBufferAttachment(width: Int, height: Int): Int {
        val depthBuffer = GL30.glGenRenderbuffers()
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer)
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width,
                height)
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                GL30.GL_RENDERBUFFER, depthBuffer)
        return depthBuffer
    }
}
