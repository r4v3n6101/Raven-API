package raven.api.client.opengl.shader

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import raven.api.common.math.matrix.Mat4
import raven.api.common.math.vector.Vec2
import raven.api.common.math.vector.Vec3
import raven.api.common.math.vector.Vec4

/**
 * Created by r4v3n6101 on 24.03.2016.
 */
abstract class ShaderProgram(vertexShader: String, fragmentShader: String) {
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

    /**
     * Load shader from list's data
     * @param line shader file
     * @param type type of shader(geometry, vertex, fragment)
     */
    private fun loadShader(line: String, type: Int): Int {
        val shaderId = GL20.glCreateShader(type)
        GL20.glShaderSource(shaderId, line)
        GL20.glCompileShader(shaderId)
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Shader error: ${GL20.glGetShaderInfoLog(shaderId, Short.MAX_VALUE.toInt())}")
        } else {
            println("Shader loaded, id is $shaderId")
        }
        return shaderId
    }

    /**
     * Load matrix to uniform location
     * @param location id of uniform location
     * @param mat4 matrix to load
     */
    fun loadMatrix(location: Int, mat4: Mat4) {
        val buffer = BufferUtils.createFloatBuffer(16)
        mat4.writeTranspose(buffer)
        buffer.flip()
        GL20.glUniformMatrix4(location, false, buffer)
    }

    /**
     * Load vec2 to uniform location
     */
    fun loadVec2(location: Int, vec2: Vec2) {
        val buffer = BufferUtils.createFloatBuffer(2)
        vec2.write(buffer)
        buffer.flip()
        GL20.glUniform2(location, buffer)
    }

    /**
     * Load bool as int to uniform location
     */
    fun loadBool(location: Int, bool: Boolean) {
        loadInt(location, if (!bool) 0 else 1)
    }

    /**
     * Load vec3 to uniform location
     */
    fun loadVec3(location: Int, vec3: Vec3) {
        val buffer = BufferUtils.createFloatBuffer(3)
        vec3.write(buffer)
        buffer.flip()
        GL20.glUniform3(location, buffer)
    }

    /**
     * Load vec4 to uniform location
     */
    fun loadVec4(location: Int, vec4: Vec4) {
        val buffer = BufferUtils.createFloatBuffer(4)
        vec4.write(buffer)
        buffer.flip()
        GL20.glUniform4(location, buffer)
    }

    /**
     * Load single int
     */
    fun loadInt(location: Int, value: Int) {
        GL20.glUniform1i(location, value)
    }

    /**
     * Link attribute list with names of variables
     */
    protected abstract fun bindAttributes()

    /**
     * Get id of uniform variable
     */
    protected abstract fun getAllUniformLocations()

    protected fun getUniformLocation(name: String): Int {
        return GL20.glGetUniformLocation(programId, name)
    }

    protected fun bindAttribute(id: Int, name: String) {
        GL20.glBindAttribLocation(programId, id, name)
    }

    /**
     * Free memory
     * Fired when game is gonna close
     */
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

    inline fun useShader(body: () -> Unit) {
        start()
        body()
        stop()
    }
}