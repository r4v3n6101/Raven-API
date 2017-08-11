package raven.api.client.world

import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Project
import raven.api.common.math.MathUtils
import raven.api.common.math.vector.Vec3

/**
 * Created by r4v3n6101 on 13.06.2016.
 */
class Camera(var width: Int, var height: Int, var nearPlane: Float, var farPlane: Float) {

    fun lookAt(objectPos: Vec3) {
        val diff = objectPos - pos
        val dist = Math.sqrt(diff.z.toDouble() * diff.z + diff.x * diff.x)
        val yaw = Math.toDegrees(Math.atan2(diff.x.toDouble(), diff.z.toDouble())).toFloat()
        val pitch = Math.toDegrees(Math.atan2(diff.y.toDouble(), dist)).toFloat()

        rotation.x = -pitch
        rotation.y = -yaw
    }

    fun setupTransform(partialTicks: Float) {
        val lerpPos = MathUtils.lerp(prevPos, pos, partialTicks)
        val lerpRotation = MathUtils.lerp(prevRotation, rotation, partialTicks)
        val lerpFov = MathUtils.lerp(prevFov, fov, partialTicks)
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glLoadIdentity()
        Project.gluPerspective(lerpFov, width / height.toFloat(), nearPlane, farPlane)

        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glLoadIdentity()

        GL11.glRotatef(lerpRotation.x, -1F, 0F, 0F)
        GL11.glRotatef(lerpRotation.y, 0F, -1F, 0F)
        GL11.glRotatef(lerpRotation.z, 0F, 0F, -1F)

        val invVec = -lerpPos
        GL11.glTranslatef(invVec.x, invVec.y, invVec.z)
    }

    var fov: Float = 60F
        set(value) {
            prevFov = field
            field = value
        }
    var rotation = Vec3()
        set(value) {
            prevRotation = field
            field = value
        }
    var pos = Vec3()
        set(value) {
            prevPos = field
            field = value
        }
    var prevFov = fov
    var prevRotation = rotation
    var prevPos = pos
}