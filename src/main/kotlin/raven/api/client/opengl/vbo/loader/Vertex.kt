package raven.api.client.opengl.vbo.loader

import raven.api.common.math.vector.Vec2
import raven.api.common.math.vector.Vec3

/**
 * Created by r4v3n6101 on 29.06.2016.
 */
data class Vertex(
        val position: Vec3,
        val uv: Vec2?,
        var normal: Vec3?,
        var tangent: Vec3?,
        var bitangent: Vec3?
)