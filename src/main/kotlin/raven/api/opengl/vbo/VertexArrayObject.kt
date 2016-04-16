package raven.api.opengl.vbo

import org.lwjgl.opengl.GL30
import java.util.*

/**
 * Created by Raven6101 on 16.04.2016.
 */
data class VertexArrayObject(val id: Int) {
    val vbos: ArrayList<VertexBufferObject> = ArrayList()

    fun free() {
        GL30.glDeleteVertexArrays(id)
        println("-Free VAO: $id")
        vbos.forEach { it.free() }
    }
}