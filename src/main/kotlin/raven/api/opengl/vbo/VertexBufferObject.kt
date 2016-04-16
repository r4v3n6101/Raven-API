package raven.api.opengl.vbo

import org.lwjgl.opengl.GL15
import java.nio.Buffer

/**
 * Created by Raven6101 on 16.04.2016.
 */
data class VertexBufferObject(val id: Int, val attributeIndex: Int, val size: Int, val data: Buffer) {
    /**
     * For indices VBO
     */
    constructor(id: Int, data: Buffer) : this(id, -1, -1, data)

    fun free() {
        GL15.glDeleteBuffers(id)
        println("--Free VBO: $id")
    }
}