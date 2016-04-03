package raven.api.opengl.shader

import raven.api.utils.math.vector.Matrix4f

/**
 * Created by Raven6101 on 24.03.2016.
 */
class VboShader(vertexShader: List<String>, fragmentShader: List<String>) : ShaderProgram(vertexShader, fragmentShader) {
    private var location_transformationMatrix: Int = 0
    private var location_projectionMatrix: Int = 0
    private var location_viewMatrix: Int = 0

    override fun bindAttributes() {
        bindAttribute(0, "position")
        bindAttribute(1, "textureCoords")
    }

    override fun getAllUniformLocations() {
        location_projectionMatrix = getUniformLocation("projectionMatrix")
        location_transformationMatrix = getUniformLocation("transformationMatrix")
        location_viewMatrix = getUniformLocation("viewMatrix")
    }

    fun loadTransformationMatrix(matrix4f: Matrix4f) {
        loadMatrix(location_transformationMatrix, matrix4f)
    }

    fun loadProjectionMatrix(matrix4f: Matrix4f) {
        loadMatrix(location_projectionMatrix, matrix4f)
    }

    fun loadViewMatrix(matrix4f: Matrix4f) {
        loadMatrix(location_viewMatrix, matrix4f)
    }
}