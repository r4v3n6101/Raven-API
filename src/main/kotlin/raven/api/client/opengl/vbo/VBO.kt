package raven.api.client.opengl.vbo

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.texture.TextureUtil
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import raven.api.client.opengl.shader.ShaderProgram
import raven.api.client.opengl.vbo.loader.Vertex
import raven.api.client.utils.*
import raven.api.common.math.matrix.Mat4
import raven.api.common.math.vector.Vec2
import java.nio.FloatBuffer
import java.nio.IntBuffer

/**
 * Created by r4v3n6101 on 22.03.2016.
 */
object VBO {

    private val vaos = mutableListOf<Int>()
    private val vbos = mutableListOf<Int>()

    @JvmOverloads
    fun render(
            model: Model,
            textureId: Int = TextureUtil.missingTexture.glTextureId,
            shader: VboShader = DEFAULT_VBO_SHADER,
            matrix: Mat4 = projectionMat * viewMat,
            lightmapCoords: Vec2 = Minecraft.getMinecraft().theWorld.getLightmapCoordinates(renderPosition)
    ) {
        val entityRenderer = Minecraft.getMinecraft().entityRenderer
        entityRenderer.enableLightmap(0.0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)

        render(model, shader, {
            loadModelViewProjectionMatrix(matrix)
            connectTextureUnits()
            loadLightmapCoords(lightmapCoords)
            loadCameraPos(renderPosition)
        }, intArrayOf(0, 1))

        entityRenderer.disableLightmap(0.0)
    }

    fun renderWithNormalMap(
            model: Model,
            normalMapId: Int,
            textureId: Int = TextureUtil.missingTexture.glTextureId,
            shader: NormalShader = DEFAULT_NORMAL_SHADER,
            matrix: Mat4 = projectionMat * viewMat,
            lightmapCoords: Vec2 = Minecraft.getMinecraft().theWorld.getLightmapCoordinates(renderPosition)
    ) {
        val entityRenderer = Minecraft.getMinecraft().entityRenderer
        entityRenderer.enableLightmap(0.0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)

        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit + 2)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMapId)

        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit)

        render(model, shader, {
            loadModelViewProjectionMatrix(matrix)
            connectTextureUnits()
            loadLightmapCoords(lightmapCoords)
            loadCameraPos(renderPosition)
        }, intArrayOf(0, 1, 2, 3, 4))

        entityRenderer.disableLightmap(0.0)
    }

    inline fun <T : ShaderProgram> render(model: Model, shader: T, loadShader: T.() -> Unit, attributesIndices: IntArray) {
        shader.useShader {
            shader.loadShader()
            useVao(model.vaoId) {
                attributesIndices.forEach { GL20.glEnableVertexAttribArray(it) }
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.indicesCount, GL11.GL_UNSIGNED_INT, 0)
                attributesIndices.forEach { GL20.glDisableVertexAttribArray(it) }
            }
        }
    }

    fun free() {
        vaos.forEach({ GL30.glDeleteVertexArrays(it) })
        vbos.forEach { GL15.glDeleteBuffers(it) }

        vaos.clear()
        vbos.clear()
    }

    fun createModel(vertices: Collection<Vertex>, indices: IntArray, useNormals: Boolean = false): Model {
        fun loadNormals(attributes: MutableList<Attribute>) {
            val normalsBuffer = BufferUtils.createFloatBuffer(vertices.count { it.normal != null } * 3)
            val tangentsBuffer = BufferUtils.createFloatBuffer(vertices.count { it.tangent != null } * 3)
            val bitangentsBuffer = BufferUtils.createFloatBuffer(vertices.count { it.bitangent != null } * 3)
            vertices.forEach {
                it.normal?.write(normalsBuffer)
                it.tangent?.write(tangentsBuffer)
                it.bitangent?.write(bitangentsBuffer)
            }
            normalsBuffer.flip()
            tangentsBuffer.flip()
            bitangentsBuffer.flip()
            attributes += Attribute(2, 3, normalsBuffer)
            attributes += Attribute(3, 3, tangentsBuffer)
            attributes += Attribute(4, 3, bitangentsBuffer)
        }

        val indicesBuffer = BufferUtils.createIntBuffer(indices.size)
        indicesBuffer.put(indices)
        indicesBuffer.flip()

        val positionsBuffer = BufferUtils.createFloatBuffer(vertices.size * 3)
        val uvsBuffer = BufferUtils.createFloatBuffer(vertices.count { it.uv != null } * 2)

        vertices.forEach {
            it.position.write(positionsBuffer)
            it.uv?.write(uvsBuffer)
        }
        positionsBuffer.flip()
        uvsBuffer.flip()

        val attributes = mutableListOf(
                Attribute(0, 3, positionsBuffer),
                Attribute(1, 2, uvsBuffer)
        )

        if (useNormals) {
            loadNormals(attributes)
        }

        val model = createModel(indicesBuffer, *attributes.toTypedArray())
        return model
    }

    /**
     * Make a new
     */
    fun createModel(indicesBuffer: IntBuffer, vararg attributes: Attribute): Model {
        val vaoId = GL30.glGenVertexArrays()
        vaos.add(vaoId)
        useVao(vaoId) {
            bindIndices(indicesBuffer)//Put indices instead of 3 vertices coordinates(1 vs 3)
            attributes.forEach {
                storeDataInAttributeList(it.index, it.size, it.buffer)
            }
        }
        return Model(vaoId, indicesBuffer.capacity())
    }

    inline fun useVao(id: Int, body: () -> Unit) {
        GL30.glBindVertexArray(id)
        body()
        GL30.glBindVertexArray(0)
    }

    /**
     * Store data in attribute list(such as positions, texture coordinates, normals)
     */
    private fun storeDataInAttributeList(index: Int, size: Int, data: FloatBuffer) {
        val vboId = GL15.glGenBuffers()
        vbos.add(vboId)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW)
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    /**
     * Bind indices buffer
     */
    private fun bindIndices(indices: IntBuffer) {
        val vboId = GL15.glGenBuffers()
        vbos.add(vboId)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId)
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW)
    }
}
