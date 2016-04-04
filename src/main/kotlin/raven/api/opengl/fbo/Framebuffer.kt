package raven.api.opengl.fbo

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

/**
 * Created by Raven6101 on 28.03.2016.
 */
class Framebuffer(val width: Int, val height: Int, val fboId: Int, val textureId: Int,
                  /**
                   * for free memory
                   */
                  val depthBufferId: Int) {

    fun free() {
        GL11.glDeleteTextures(textureId)
        GL30.glDeleteRenderbuffers(depthBufferId)
        GL30.glDeleteFramebuffers(fboId)
    }

    fun bind() {
        FBO.bindFrameBuffer(fboId, width, height)
    }
}