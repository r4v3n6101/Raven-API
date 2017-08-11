package raven.api.client.utils

import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.MathHelper
import net.minecraft.util.ResourceLocation
import org.apache.commons.io.IOUtils
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import raven.api.client.opengl.fbo.FBO
import raven.api.client.opengl.particles.ParticleShader
import raven.api.client.opengl.particles.ParticleTextureMap
import raven.api.client.opengl.particles.ParticlesRender
import raven.api.client.opengl.shader.ShaderProgram
import raven.api.client.opengl.vbo.Model
import raven.api.client.opengl.vbo.NormalShader
import raven.api.client.opengl.vbo.VBO
import raven.api.client.opengl.vbo.VboShader
import raven.api.client.opengl.vbo.loader.Loader
import raven.api.client.opengl.vbo.loader.Vertex
import raven.api.common.math.matrix.Mat4
import raven.api.common.math.vector.Vec2
import raven.api.common.math.vector.Vec3
import raven.api.common.math.vector.Vec4
import java.awt.Color
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.stream.Collectors

/**
 * free al allocated memory
 */
fun freeAll() {
    SHADERS.forEach({ it.free() })
    SHADERS.clear()
    VBO.free()
    FBO.free()
}

fun drawFilledBar(startX: Int, startY: Int, cur: Float, max: Float, color: Color) {
    val col = color.rgb
    val height = MathHelper.ceiling_float_int(cur / max * 58)
    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    Gui.drawRect(startX, startY, startX + 1, startY + 60, Color.BLACK.rgb)
    Gui.drawRect(startX + 7, startY, startX + 1 + 7, startY + 60, Color.BLACK.rgb)
    Gui.drawRect(startX, startY, startX + 1 + 7, startY + 1, Color.BLACK.rgb)
    Gui.drawRect(startX, startY + 59, startX + 7 + 1, startY + 60, Color.BLACK.rgb)
    Gui.drawRect(startX + 1, startY + 59, startX + 7, startY + 59 - height, col)
}

fun loadModel(loader: Loader, location: ResourceLocation): Model {
    return loader.loadModel(IOUtils.toByteArray(location.inputStream))
}

fun newQuad() = VBO.createModel(
        listOf(
                Vertex(Vec3(-0.5, 0.5, 0), Vec2(0, 0), Vec3(0, 0, 1), Vec3(1, 0, 0), Vec3(0, 1, 0)),
                Vertex(Vec3(-0.5, -0.5, 0), Vec2(0, 1), Vec3(0, 0, 1), Vec3(1, 0, 0), Vec3(0, 1, 0)),
                Vertex(Vec3(0.5, -0.5, 0), Vec2(1, 1), Vec3(0, 0, 1), Vec3(1, 0, 0), Vec3(0, 1, 0)),
                Vertex(Vec3(0.5, 0.5, 0), Vec2(1, 0), Vec3(0, 0, 1), Vec3(1, 0, 0), Vec3(0, 1, 0))
        ),
        intArrayOf(0, 1, 3, 3, 1, 2), true
)

/**
 * @param facedToCamera If true model will be faced to camera
 */
@JvmOverloads
fun makeMVPMatrix(
        worldPos: Vec3,
        rotation: Vec3 = Vec3(0),
        scale: Vec3 = Vec3(1),
        facedToCamera: Boolean = false
): Mat4 {
    val localPos = worldPos - renderPosition

    val modelMatrix = Mat4()
    modelMatrix % Vec4(localPos)
    modelMatrix(Vec4(rotation))
    modelMatrix.scale(Vec4(scale))

    if (facedToCamera) {
        Camera.faceMatrixToCamera(modelMatrix)
    }

    return projectionMat * viewMat * modelMatrix
}

private val SHADERS = mutableListOf<ShaderProgram>()
val DEFAULT_VBO_SHADER: VboShader by lazy {
    val vertex = ResourceLocation("raven", "shaders/vbo_vert.glsl")
    val fragment = ResourceLocation("raven", "shaders/vbo_frag.glsl")
    BufferedReader(InputStreamReader(vertex.inputStream)).use { vertexReader ->
        BufferedReader(InputStreamReader(fragment.inputStream)).use { fragmentReader ->
            val program = VboShader(
                    vertexReader.lines().collect(Collectors.toList<String>()).joinToString("\n"),
                    fragmentReader.lines().collect(Collectors.toList<String>()).joinToString("\n"))
            SHADERS.add(program)
            program
        }
    }
}
val DEFAULT_PARTICLE_SHADER: ParticleShader by lazy {
    val vertex = ResourceLocation("raven", "shaders/particle_vert.glsl")
    val fragment = ResourceLocation("raven", "shaders/particle_frag.glsl")
    BufferedReader(InputStreamReader(vertex.inputStream)).use { vertexReader ->
        BufferedReader(InputStreamReader(fragment.inputStream)).use { fragmentReader ->
            val program = ParticleShader(
                    vertexReader.lines().collect(Collectors.toList<String>()).joinToString("\n"),
                    fragmentReader.lines().collect(Collectors.toList<String>()).joinToString("\n"))
            SHADERS.add(program)
            program
        }
    }
}

val DEFAULT_NORMAL_SHADER: NormalShader by lazy {
    val vertex = ResourceLocation("raven", "shaders/normal_vert.glsl")
    val fragment = ResourceLocation("raven", "shaders/normal_frag.glsl")
    BufferedReader(InputStreamReader(vertex.inputStream)).use { vertexReader ->
        BufferedReader(InputStreamReader(fragment.inputStream)).use { fragmentReader ->
            val program = NormalShader(
                    vertexReader.lines().collect(Collectors.toList<String>()).joinToString("\n"),
                    fragmentReader.lines().collect(Collectors.toList<String>()).joinToString("\n"))
            SHADERS.add(program)
            program
        }
    }
}

val particleEngine: ParticlesRender by lazy {
    ParticlesRender()
}

val particlesAtlas: ParticleTextureMap by lazy {
    ParticleTextureMap()
}

val projectionMat: Mat4
    get() {
        projectionBuf.clear()
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionBuf)
        val mat = Mat4()
        mat.readTranspose(projectionBuf)
        projectionBuf.clear()
        return mat
    }

val viewMat: Mat4
    get() {
        modelviewBuf.clear()
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelviewBuf)
        val mat = Mat4()
        mat.readTranspose(modelviewBuf)
        modelviewBuf.clear()
        return mat
    }

val viewport: Vec4
    get() {
        viewportBuf.clear()
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewportBuf)
        val vec = Vec4()
        for (i in 0..vec.size - 1) {
            vec[i] = viewportBuf.get()
        }
        return vec
    }

val renderPosition: Vec3
    get() = Vec3(
            RenderManager.renderPosX,
            RenderManager.renderPosY,
            RenderManager.renderPosZ
    )

val viewportBuf: IntBuffer = BufferUtils.createIntBuffer(16)
val modelviewBuf: FloatBuffer = BufferUtils.createFloatBuffer(16)
val projectionBuf: FloatBuffer = BufferUtils.createFloatBuffer(16)