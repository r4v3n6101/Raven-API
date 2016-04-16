package raven.api.opengl.vbo

import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import raven.api.opengl.shader.VboShader
import raven.api.opengl.vbo.model.RawModel
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
@Deprecated("Rewrite to ARB")
object VBO {
    private val vaos = ArrayList<VertexArrayObject>()
    private val textures = ArrayList<Int>()

    fun render(model: RawModel, textureId: Int, shader: VboShader, transformationMatrix: Matrix4f, part: Float) {
        shader.start()
        GL30.glBindVertexArray(model.vao.id)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        shader.loadTransformationMatrix(transformationMatrix)
        shader.loadViewMatrix(ClientUtils.getViewMatrix(part))
        shader.loadProjectionMatrix(ClientUtils.projectionMatrix)//TODO : check
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
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
        vaos.forEach({ it.free() })
        textures.forEach({ GL11.glDeleteTextures(it) })
    }

    fun loadToVAO(positions: FloatArray, indices: IntArray, textureCoords: FloatArray): RawModel {
        val vao = createVAO()
        bindIndices(vao, indices)
        storeDataInAttributeList(vao, 0, 3, positions)
        storeDataInAttributeList(vao, 1, 2, textureCoords)
        unbindVAO()
        return RawModel(vao, indices.size)
    }

    private fun createVAO(): VertexArrayObject {
        val vaoId = GL30.glGenVertexArrays()
        val vao = VertexArrayObject(vaoId)
        vaos.add(vao)
        GL30.glBindVertexArray(vaoId)
        return vao
    }

    private fun unbindVAO() {
        GL30.glBindVertexArray(0)
    }

    private fun storeDataInAttributeList(vao: VertexArrayObject, index: Int, size: Int, data: FloatArray) {
        val vboId = GL15.glGenBuffers()
        val buffer = createFloatBuffer(data)
        val vbo = VertexBufferObject(vboId, index, size, buffer)
        vao.vbos.add(vbo)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    private fun bindIndices(vao: VertexArrayObject, indices: IntArray) {
        val vboId = GL15.glGenBuffers()
        val buffer = createIntBuffer(indices)
        val vbo = VertexBufferObject(vboId, buffer)
        vao.vbos.add(vbo)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId)
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
