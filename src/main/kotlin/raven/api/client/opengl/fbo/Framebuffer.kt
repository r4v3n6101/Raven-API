package raven.api.client.opengl.fbo

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

/**
 * Created by r4v3n6101 on 28.03.2016.
 */
data class Framebuffer(val width: Int, val height: Int, val fboId: Int, val textureId: Int, val renderbufferId: Int) {

    fun free() {
        GL11.glDeleteTextures(textureId)
        GL30.glDeleteRenderbuffers(renderbufferId)
        GL30.glDeleteFramebuffers(fboId)
        println("Free FBO: $fboId")
    }
}