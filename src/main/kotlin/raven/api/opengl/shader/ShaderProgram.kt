package raven.api.opengl.shader

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import raven.api.utils.math.vector.Matrix4f

/**
 * Created by Raven6101 on 24.03.2016.
 */
abstract class ShaderProgram(vertexShader: List<String>, fragmentShader: List<String>) {
    private val programId: Int
    private val vertexShaderId: Int
    private val fragmentShaderId: Int

    init {
        vertexShaderId = loadShader(vertexShader, GL20.GL_VERTEX_SHADER)
        fragmentShaderId = loadShader(fragmentShader, GL20.GL_FRAGMENT_SHADER)
        programId = GL20.glCreateProgram()
        GL20.glAttachShader(programId, vertexShaderId)
        GL20.glAttachShader(programId, fragmentShaderId)
        bindAttributes()
        GL20.glLinkProgram(programId)
        GL20.glValidateProgram(programId)
        getAllUniformLocations()
    }

    private fun loadShader(shader: List<String>, type: Int): Int {
        val builder = StringBuilder()
        shader.forEach { builder.append(it).append("\n") }
        val shaderId = GL20.glCreateShader(type)
        GL20.glShaderSource(shaderId, builder)
        GL20.glCompileShader(shaderId)
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Shader error: ${GL20.glGetShaderInfoLog(shaderId, java.lang.Short.MAX_VALUE.toInt())}")
        } else {
            println("Shader loaded, id is $shaderId")
        }
        return shaderId
    }

    fun loadMatrix(location: Int, matrix4f: Matrix4f) {
        val buffer = BufferUtils.createFloatBuffer(16)
        matrix4f.store(buffer)
        buffer.flip()
        GL20.glUniformMatrix4(location, false, buffer)
    }

    protected abstract fun bindAttributes()

    protected abstract fun getAllUniformLocations()

    protected fun getUniformLocation(name: String): Int {
        return GL20.glGetUniformLocation(programId, name)
    }

    protected fun bindAttribute(id: Int, name: String) {
        GL20.glBindAttribLocation(programId, id, name)
    }

    fun free() {
        stop()
        GL20.glDetachShader(programId, vertexShaderId)
        GL20.glDetachShader(programId, fragmentShaderId)
        GL20.glDeleteShader(fragmentShaderId)
        GL20.glDeleteShader(vertexShaderId)
        GL20.glDeleteProgram(programId)
    }

    fun stop() {
        GL20.glUseProgram(0)
    }

    fun start() {
        GL20.glUseProgram(programId)
    }
}