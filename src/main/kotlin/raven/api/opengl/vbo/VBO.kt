package raven.api.opengl.vbo

import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import raven.api.opengl.shader.VboShader
import raven.api.opengl.vbo.model.RawModel
import raven.api.opengl.vbo.model.TexturedModel
import raven.api.utils.client.ClientUtils
import raven.api.utils.math.vector.Matrix4f
import java.io.IOException
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.*
import javax.imageio.ImageIO

/**
 * Created by Raven6101 on 22.03.2016.
 */
object VBO {
    private val vbos = ArrayList<Int>()
    private val vaos = ArrayList<Int>()
    private val textures = ArrayList<Int>()

    fun render(model: TexturedModel, shader: VboShader, transformationMatrix: Matrix4f, part: Float) {
        shader.start()
        GL30.glBindVertexArray(model.vaoId)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        shader.loadTransformationMatrix(transformationMatrix)
        shader.loadViewMatrix(ClientUtils.getViewMatrix(part))
        shader.loadProjectionMatrix(ClientUtils.projectionMatrix)//TODO : check
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.textureId)
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0)// FIXME: 24.03.2016
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL30.glBindVertexArray(0)
        shader.stop()
    }

    @Throws(IOException::class)
    fun loadTexture(location: ResourceLocation): Int {
        val image = ImageIO.read(ClientUtils.getStreamFromLocation(location))
        val texture = DynamicTexture(image)
        textures.add(texture.glTextureId)
        return texture.glTextureId
    }

    fun free() {
        vbos.forEach({ GL15.glDeleteBuffers(it) })
        vaos.forEach({ GL30.glDeleteVertexArrays(it) })
        textures.forEach({ GL11.glDeleteTextures(it) })
    }

    fun loadToVAO(positions: FloatArray, indices: IntArray, textureCoords: FloatArray): RawModel {
        val vaoId = createVAO()
        bindIndices(indices)
        storeDataInAttributeList(0, 3, positions)
        storeDataInAttributeList(1, 2, textureCoords)
        unbindVAO()
        return RawModel(vaoId, indices.size)
    }

    private fun createVAO(): Int {
        val vaoId = GL30.glGenVertexArrays()
        GL30.glBindVertexArray(vaoId)
        vaos.add(vaoId)
        return vaoId
    }

    private fun unbindVAO() {
        GL30.glBindVertexArray(0)
    }

    private fun storeDataInAttributeList(index: Int, size: Int, data: FloatArray) {
        val vboId = GL15.glGenBuffers()
        vbos.add(vboId)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
        val buffer = createFloatBuffer(data)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    private fun bindIndices(indices: IntArray) {
        val vboId = GL15.glGenBuffers()
        vbos.add(vboId)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId)
        val buffer = createIntBuffer(indices)
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
    }

    private fun createIntBuffer(indices: IntArray): IntBuffer {
        val buffer = BufferUtils.createIntBuffer(indices.size)
        buffer.put(indices)
        buffer.flip()
        return buffer
    }

    private fun createFloatBuffer(data: FloatArray): FloatBuffer {
        val buffer = BufferUtils.createFloatBuffer(data.size)
        buffer.put(data)
        buffer.flip()
        return buffer
    }
}
