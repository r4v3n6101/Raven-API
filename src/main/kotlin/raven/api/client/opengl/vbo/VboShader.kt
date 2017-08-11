package raven.api.client.opengl.vbo

import raven.api.client.opengl.shader.ShaderProgram
import raven.api.common.math.matrix.Mat4
import raven.api.common.math.vector.Vec2
import raven.api.common.math.vector.Vec3

/**
 * Created by r4v3n6101 on 24.03.2016.
 */
open class VboShader(vertexShader: String, fragmentShader: String) : ShaderProgram(vertexShader, fragmentShader) {
    private var location_modelViewProjectionMatrix = 0
    private var location_textureSampler = 0
    private var location_lightmapSampler = 0
    private var location_lightmapCoords = 0
    private var location_camPos = 0


    override fun bindAttributes() {
        bindAttribute(0, "position")
        bindAttribute(1, "textureCoords")
    }

    override fun getAllUniformLocations() {
        location_modelViewProjectionMatrix = getUniformLocation("modelViewProjectionMatrix")
        location_textureSampler = getUniformLocation("textureSampler")
        location_lightmapSampler = getUniformLocation("lightmapSampler")
        location_lightmapCoords = getUniformLocation("lightmapCoords")
        location_camPos = getUniformLocation("camPos")
    }

    fun connectTextureUnits() {
        loadInt(location_textureSampler, 0)
        loadInt(location_lightmapSampler, 1)
    }

    fun loadModelViewProjectionMatrix(mat: Mat4) {
        loadMatrix(location_modelViewProjectionMatrix, mat)
    }

    fun loadLightmapCoords(coords: Vec2) {
        loadVec2(location_lightmapCoords, coords)
    }

    fun loadCameraPos(pos: Vec3) {
        loadVec3(location_camPos, pos)
    }
}