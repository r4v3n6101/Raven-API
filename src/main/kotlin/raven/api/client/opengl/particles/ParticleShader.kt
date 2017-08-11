package raven.api.client.opengl.particles

import raven.api.client.opengl.shader.ShaderProgram
import raven.api.common.math.matrix.Mat4

/**
 * Created by r4v3n6101 on 24.03.2016.
 */
class ParticleShader(vertexShader: String, fragmentShader: String) : ShaderProgram(vertexShader, fragmentShader) {
    private var location_textureSampler: Int = 0
    private var location_lightmapSampler: Int = 0
    private var location_modelViewProjectionMatrix: Int = 0

    override fun getAllUniformLocations() {
        location_textureSampler = getUniformLocation("textureSampler")
        location_lightmapSampler = getUniformLocation("lightmapSampler")
        location_modelViewProjectionMatrix = getUniformLocation("modelViewProjectionMatrix")
    }

    fun connectTextureUnits() {
        loadInt(location_textureSampler, 0)
        loadInt(location_lightmapSampler, 1)
    }

    fun loadModelViewProjectionMatrix(mat: Mat4) {
        loadMatrix(location_modelViewProjectionMatrix, mat)
    }

    override fun bindAttributes() {
        bindAttribute(0, "position")
        bindAttribute(1, "textureCoords")
        bindAttribute(2, "translate")
        bindAttribute(3, "rotation")
        bindAttribute(4, "scale")
        bindAttribute(5, "billboardRotation")
        bindAttribute(6, "lightmapCoords")
        bindAttribute(7, "color")
    }
}