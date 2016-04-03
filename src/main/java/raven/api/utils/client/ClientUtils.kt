package raven.api.utils.client

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.util.MathHelper
import net.minecraft.util.ResourceLocation
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import raven.api.opengl.shader.ShaderProgram
import raven.api.opengl.shader.VboShader
import raven.api.opengl.vbo.model.TexturedModel
import raven.api.opengl.vbo.model.loader.ObjLoader
import raven.api.utils.CommonUtils
import raven.api.utils.math.vector.Matrix4f
import raven.api.utils.math.vector.Vector3f
import java.awt.Color
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import java.util.stream.Collectors

/**
 * Created by Raven6101 on 19.03.2016.
 */
@SideOnly(Side.CLIENT)
object ClientUtils {

    private val SHADERS = ArrayList<ShaderProgram>()
    lateinit var DEFAULT_VBO_SHADER: VboShader

    @Throws(IOException::class)
    fun getStreamFromLocation(loc: ResourceLocation): InputStream {
        return Minecraft.getMinecraft().resourceManager.getResource(loc).inputStream
    }

    val projectionMatrix: Matrix4f
        get() {
            val projection = BufferUtils.createFloatBuffer(16)
            GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection)
            val f = Matrix4f()
            f.load(projection)
            return f
        }

    fun getViewMatrix(part: Float): Matrix4f {
        val mc = Minecraft.getMinecraft()
        val camera = mc.renderViewEntity
        val renderer = mc.entityRenderer
        val matrix4f = Matrix4f()
        matrix4f.setIdentity()
        Matrix4f.translate(Vector3f(
                CommonUtils.MathUtils.interpolate(camera.lastTickPosX.toFloat(), camera.posX.toFloat(), part),
                CommonUtils.MathUtils.interpolate(camera.lastTickPosY.toFloat(), camera.posY.toFloat(), part),
                CommonUtils.MathUtils.interpolate(camera.lastTickPosZ.toFloat(), camera.posZ.toFloat(), part)),
                matrix4f, matrix4f)
        Matrix4f.rotate(
                Math.toRadians(CommonUtils.MathUtils.interpolate(camera.prevCameraPitch, camera.cameraPitch, part).toDouble()).toFloat(),
                Vector3f(1f, 0f, 0f), matrix4f, matrix4f)
        Matrix4f.rotate(
                Math.toRadians(CommonUtils.MathUtils.interpolate(camera.prevRotationYawHead, camera.rotationYawHead, part).toDouble()).toFloat(),
                Vector3f(0f, 1f, 0f), matrix4f, matrix4f)
        Matrix4f.rotate(
                Math.toRadians(CommonUtils.MathUtils.interpolate(renderer.prevCamRoll, renderer.camRoll, part).toDouble()).toFloat(),
                Vector3f(0f, 0f, 1f), matrix4f, matrix4f)
        return matrix4f
    }

    @Throws(IOException::class)
    fun loadShader(vertex: InputStream, fragment: InputStream): VboShader {
        BufferedReader(InputStreamReader(vertex)).use { vertexReader ->
            BufferedReader(InputStreamReader(fragment)).use { fragmentReader ->
                val program = VboShader(
                        vertexReader.lines().collect(Collectors.toList<String>()),
                        fragmentReader.lines().collect(Collectors.toList<String>()))
                SHADERS.add(program)
                return program
            }
        }
    }

    @Throws(IOException::class)
    fun loadDefaultVboShader() {
        val vertex = ResourceLocation("raven", "shaders/defaultVertex.glsl")
        val fragment = ResourceLocation("raven", "shaders/defaultFragment.glsl")
        DEFAULT_VBO_SHADER = loadShader(getStreamFromLocation(vertex), getStreamFromLocation(fragment))
    }

    fun freeShaders() {
        SHADERS.forEach({ it.free() })
    }

    @Throws(IOException::class)
    fun loadObjModel(model: ResourceLocation, textureId: Int): TexturedModel {
        BufferedReader(InputStreamReader(getStreamFromLocation(model))).use { bufferedReader ->
            val rawModel = ObjLoader.load(bufferedReader.lines().collect(Collectors.toList<String>()))
            return TexturedModel.fromRawModel(rawModel, textureId)
        }
    }

    object GuiUtils {

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
    }
}
