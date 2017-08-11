package raven.api.client.opengl.particles

import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.World
import raven.api.client.utils.particlesAtlas
import raven.api.common.math.MathUtils
import raven.api.common.math.vector.Vec3
import raven.api.common.math.vector.Vec4
import raven.api.common.utils.toAxisAlignedBB

/**
 * Created by r4v3n6101 on 27.04.2016.
 */
class Particle
@JvmOverloads constructor(var world: World,
                          var pos: Vec3, //World position
                          var scale: Vec3 = Vec3(),
                          var rotation: Vec3 = Vec3(0),
                          var size: Vec3 = Vec3(),
                          var update: (part: Particle) -> Unit = {},
                          var name: String = "missingno",
                          var color: Vec4 = Vec4(1),
                          var lifetime: Int = 20) {

    val icon: TextureAtlasSprite
        get() = particlesAtlas.getAtlasSprite(name)

    val collisionBox: AxisAlignedBB
        get() {
            return pos.toAxisAlignedBB()
        }

    val onGround: Boolean
        get() = world.checkBlockCollision(collisionBox)


    var isDead = false
    var ticksExisted = 0
    var motion: Vec3 = Vec3(0)

    fun shouldRender(currentWorld: World): Boolean {
        return currentWorld.provider.dimensionId == world.provider.dimensionId
    }

    fun update() {
        if (ticksExisted++ >= lifetime) {
            isDead = true
        }

        if (!onGround) {
            motion -= Vec3(0, 0.004, 0)
        } else {
            motion.y = 0F
        }

        prevPos = pos
        prevScale = scale
        prevRotation = rotation

        pos += motion
        update(this)
    }

    fun interpolate(partialTicks: Float) {
        rotation = MathUtils.lerp(prevRotation, rotation, partialTicks)
        pos = MathUtils.lerp(prevPos, pos, partialTicks)
        scale = MathUtils.lerp(prevScale, scale, partialTicks)
    }

    var prevPos = pos
    var prevScale = scale
    var prevRotation = rotation
}