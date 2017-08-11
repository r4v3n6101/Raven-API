package raven.api.client.opengl.instanced

import net.minecraft.client.renderer.texture.ITickable
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL33
import raven.api.client.opengl.shader.ShaderProgram
import raven.api.client.opengl.vbo.Model
import raven.api.client.opengl.vbo.VBO
import java.nio.FloatBuffer
import java.util.*

/**
 * Created by r4v3n6101 on 29.04.2016.
 */
abstract class Render<T>(val maxSize: Int,
                         val bytesPerOne: Int,
                         val model: Model,
                         val shader: ShaderProgram) : ITickable {
    protected val buffer: FloatBuffer
    private val emptyVbo: Int
    protected val elements = ArrayList<T>(maxSize)

    init {
        emptyVbo = createEmptyVbo(bytesPerOne * maxSize.toLong())
        buffer = BufferUtils.createFloatBuffer(maxSize * (bytesPerOne / 4))
        VBO.useVao(model.vaoId) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, emptyVbo)
            addAttributes()
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        }
        println("Create new instanced rendering engine. ${this.javaClass.name}, max elements: $maxSize")
    }

    /**
     * Add new element, remove if there's no enough space
     */
    operator infix fun plusAssign(element: T) {
        with(elements) {
            if (size >= maxSize) {
                removeAt(0)
            }
            this += element
        }
    }

    protected fun updateVbo() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, emptyVbo)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4.toLong(), GL15.GL_STREAM_DRAW)
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    private fun createEmptyVbo(bytes: Long): Int {
        val id = GL15.glGenBuffers()
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bytes, GL15.GL_STREAM_DRAW)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        return id
    }

    abstract fun addAttributes()

    abstract fun prepare()

    abstract fun finish()

    fun render(partialTicks: Float) {
        if (elements.size > 0) {
            prepare()
            shader.useShader {
                VBO.useVao(model.vaoId) {
                    processElements(partialTicks)
                }
            }
            finish()
        }
    }

    protected fun addAtribute(index: Int, size: Int, offset: Long) {
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, bytesPerOne, offset * 4)
        GL33.glVertexAttribDivisor(index, 1)
    }

    abstract fun processElements(partialTicks: Float)

    abstract fun collectData(element: T, partialTicks: Float)

    override abstract fun tick()
}