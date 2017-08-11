package raven.api.client.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World
import org.lwjgl.util.glu.Project
import raven.api.common.math.matrix.Mat4
import raven.api.common.math.vector.Vec2
import raven.api.common.math.vector.Vec3
import raven.api.common.math.vector.Vec4
import raven.api.common.utils.lookAt
import raven.api.common.utils.toDefVec
import java.nio.FloatBuffer
import net.minecraft.client.Minecraft.getMinecraft as mc

/**
 * Created by r4v3n6101 on 11.06.2016.
 */
object Camera {

    fun faceMatrixToCamera(mat: Mat4) {
        val invertedRotation = Minecraft.getMinecraft().gameSettings.thirdPersonView == 2
        val rotateX = Vec4(
                if (invertedRotation)
                    Math.toRadians(RenderManager.instance.playerViewX.toDouble())
                else
                    Math.toRadians(-RenderManager.instance.playerViewX.toDouble()),
                0, 0, 0
        )
        val rotateY = Vec4(
                0, Math.toRadians(180 - RenderManager.instance.playerViewY.toDouble()), 0, 0
        )
        mat(rotateY)
        mat(rotateX)
    }

    fun lookAt(objectVec: Vec3) {
        mc().renderViewEntity.lookAt(objectVec)
    }

    /**
     * Transform screen coordinates to world coordinates and raytrace
     * @param screenCoords screen coordinates, width and height
     * @param world world to raytrace
     * @return hit result or null
     * @author GloomyFolken
     */
    fun unproject(world: World, screenCoords: Vec2): MovingObjectPosition? {
        viewMat//Gather data
        projectionMat//Gather data
        viewport//Gather data
        val resultBuf = FloatBuffer.allocate(3)
        Project.gluUnProject(screenCoords.x, screenCoords.y, -1F, modelviewBuf, projectionBuf, viewportBuf, resultBuf)
        var p1 = Vec3()
        p1.read(resultBuf)
        Project.gluUnProject(screenCoords.x, screenCoords.y, 1F, modelviewBuf, projectionBuf, viewportBuf, resultBuf)
        var p2 = Vec3()
        p2.read(resultBuf)
        resultBuf.clear()//Free memory

        p1 += renderPosition
        p2 += renderPosition

        return world.rayTraceBlocks(p1.toDefVec(), p2.toDefVec())
    }

    /**
     * Transform world coordinates to screen coordinates
     */
    fun project(worldCoordinates: Vec3): Vec3 {
        viewMat//Gather data
        projectionMat//Gather data
        viewport//Gather data
        val resultBuf = GLAllocation.createDirectFloatBuffer(3)
        Project.gluProject(
                worldCoordinates.x,
                worldCoordinates.y,
                worldCoordinates.z,
                modelviewBuf,
                projectionBuf,
                viewportBuf,
                resultBuf
        )
        val screenCoords = Vec3()
        screenCoords.read(resultBuf)
        resultBuf.clear()//Free memory
        return screenCoords
    }
}

