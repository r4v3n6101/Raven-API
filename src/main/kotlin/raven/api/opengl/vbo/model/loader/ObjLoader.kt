package raven.api.opengl.vbo.model.loader

import com.google.common.primitives.Ints
import cpw.mods.fml.common.FMLLog
import raven.api.opengl.vbo.VBO
import raven.api.opengl.vbo.model.RawModel
import raven.api.utils.math.vector.Vector2f
import raven.api.utils.math.vector.Vector3f
import java.util.*

/**
 * Created by Raven6101 on 23.03.2016.
 */
object ObjLoader {

    fun load(file: List<String>): RawModel {
        val vertices = ArrayList<Vector3f>()
        val textures = ArrayList<Vector2f>()
        val indicesLines = ArrayList<String>()
        file.forEach { line ->
            val splitted = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            when (splitted[0]) {
                "v" //Vertex coordinates
                -> {
                    val vertex = Vector3f(
                            java.lang.Float.parseFloat(splitted[1]),
                            java.lang.Float.parseFloat(splitted[2]),
                            java.lang.Float.parseFloat(splitted[3]))
                    vertices.add(vertex)
                }
                "vt" //texture coordinates
                -> {
                    val texture = Vector2f(
                            java.lang.Float.parseFloat(splitted[1]),
                            java.lang.Float.parseFloat(splitted[2]))
                    textures.add(texture)
                }
                "f"//indices
                -> indicesLines.add(splitted.joinToString(" "))
            }
        }
        val indices = ArrayList<Int>()
        val positions = FloatArray(vertices.size * 3)//xyz
        val textureCoords = FloatArray(vertices.size * 2)//uv
        for (indicesLine in indicesLines) {
            val data = indicesLine.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val vertex1 = data[1].split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val vertex2 = data[2].split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val vertex3 = data[3].split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            processVertex(vertex1, textures, vertices, indices, textureCoords, positions)
            processVertex(vertex2, textures, vertices, indices, textureCoords, positions)
            processVertex(vertex3, textures, vertices, indices, textureCoords, positions)
        }
        textures.clear()
        vertices.clear()
        indicesLines.clear()
        val indicesArray = Ints.toArray(indices)
        FMLLog.fine("Load obj with positions[{}], indices[{}], textureCoords[{}]", positions, indicesArray, textureCoords)
        return VBO.loadToVAO(positions, indicesArray, textureCoords)
    }

    private fun processVertex(data: Array<String>, textures: List<Vector2f>, vertices: List<Vector3f>, indices: MutableList<Int>, textureCoords: FloatArray, verticesCoords: FloatArray) {
        val vertexIndex = Integer.parseInt(data[0]) - 1
        val textureIndex = Integer.parseInt(data[1]) - 1
        indices.add(vertexIndex)
        val texture = textures[textureIndex]
        textureCoords[vertexIndex * 2] = texture.x
        textureCoords[vertexIndex * 2 + 1] = 1 - texture.y
        val vertex = vertices[vertexIndex]
        verticesCoords[vertexIndex * 3] = vertex.x
        verticesCoords[vertexIndex * 3 + 1] = vertex.y
        verticesCoords[vertexIndex * 3 + 2] = vertex.z
    }
}