package raven.api.client.opengl.vbo.loader

import raven.api.client.opengl.vbo.Model
import raven.api.client.opengl.vbo.VBO
import raven.api.common.math.vector.Vec2
import raven.api.common.math.vector.Vec3

/**
 * Created by r4v3n6101 on 05.06.2016.
 */
object ObjLoader : Loader {

    override fun loadModel(file: ByteArray): Model {
        val vertices = mutableListOf<Vec3>()
        val textureCoords = mutableListOf<Vec2>()
        val normals = mutableListOf<Vec3>()
        val faces: MutableList<Triple<VertexId, VertexId, VertexId>> = mutableListOf()

        String(file).lines().forEach {
            val splittedLine = it.split(" ")
            val type = splittedLine[0]
            when (type) {
                "v" -> {
                    vertices += Vec3(splittedLine[1].toFloat(), splittedLine[2].toFloat(), splittedLine[3].toFloat())
                }
                "vt" -> {
                    textureCoords += Vec2(splittedLine[1].toFloat(), 1 - splittedLine[2].toFloat())
                }
                "vn" -> {
                    normals += Vec3(splittedLine[1].toFloat(), splittedLine[2].toFloat(), splittedLine[3].toFloat())
                }
                "f" -> {
                    val first = splittedLine[1]
                    val second = splittedLine[2]
                    val third = splittedLine[3]
                    faces += Triple(
                            loadFace(first),
                            loadFace(second),
                            loadFace(third)
                    )
                }
            }
        }
        val normalMapping = textureCoords.isNotEmpty() && normals.isNotEmpty()

        val indices = mutableListOf<Int>()
        val outVertices = arrayOfNulls<Vertex>(vertices.size)

        faces.forEach {
            val first = processVertex(it.first, vertices, textureCoords, normals)
            val second = processVertex(it.second, vertices, textureCoords, normals)
            val third = processVertex(it.third, vertices, textureCoords, normals)
            val tnb = arrayOfNulls<Vec3>(2)

            if (normalMapping) {
                val tangentAndBitangent = calculateTangentAndBitangent(Triple(first.second, second.second, third.second))
                tnb[0] = tangentAndBitangent[0]
                tnb[1] = tangentAndBitangent[1]
            }

            putVertex(first.first, first.second, indices, outVertices, tnb)
            putVertex(second.first, second.second, indices, outVertices, tnb)
            putVertex(third.first, third.second, indices, outVertices, tnb)
        }

        return VBO.createModel(outVertices.map { it!! }, indices.toIntArray(), normalMapping)
    }

    private fun putVertex(index: Int, vertex: Vertex, indices: MutableList<Int>, vertices: Array<in Vertex>, tnb: Array<Vec3?>) {
        vertex.tangent = tnb[0]
        vertex.bitangent = tnb[1]
        vertices[index] = vertex
        indices += index
    }

    private fun calculateTangentAndBitangent(triangle: Triple<Vertex, Vertex, Vertex>): Array<Vec3> {
        val v0 = triangle.first.position
        val v1 = triangle.second.position
        val v2 = triangle.third.position

        val uv0 = triangle.first.uv!!
        val uv1 = triangle.second.uv!!
        val uv2 = triangle.third.uv!!

        val deltaPos1 = v1 - v0
        val deltaPos2 = v2 - v0

        val deltaUV1 = uv1 - uv0
        val deltaUV2 = uv2 - uv0

        val r = 1 / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x)
        val tangent = (deltaPos1 * deltaUV2.y - deltaPos2 * deltaUV1.y) * r
        val bitangent = (deltaPos2 * deltaUV1.x - deltaPos1 * deltaUV2.x) * r

        return arrayOf(tangent, bitangent)
    }

    private fun processVertex(vertexId: VertexId, vertices: List<Vec3>, uvs: List<Vec2>, normals: List<Vec3>): Pair<Int, Vertex> {
        val index = vertexId.vertex
        val uvIndex = vertexId.textureCoords
        val normalIndex = vertexId.normal

        return index to Vertex(
                vertices[index],
                if (uvIndex != null) uvs[uvIndex] else null,
                if (normalIndex != null) normals[normalIndex] else null,
                null,
                null
        )
    }

    private fun loadFace(data: String): VertexId {
        val splitted = data.split("/")

        return VertexId(
                splitted[0].toInt() - 1,
                if (splitted.size >= 2) splitted[1].toInt() - 1 else null,
                if (splitted.size > 2) splitted[2].toInt() - 1 else null
        )
    }

    private class VertexId(val vertex: Int, val textureCoords: Int?, val normal: Int?)
}