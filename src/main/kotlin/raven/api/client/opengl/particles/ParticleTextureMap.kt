package raven.api.client.opengl.particles

import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import raven.api.client.utils.PARTICLE_TEXTURE_PATH
import raven.api.client.utils.PARTICLE_TEXTURE_TYPE

/**
 * Created by r4v3n6101 on 24.05.2016.
 */

class ParticleTextureMap : TextureMap(PARTICLE_TEXTURE_TYPE, PARTICLE_TEXTURE_PATH) {

    override fun registerIcons() {
        particleTextures.forEach { registerIcon(it.iconName) }
    }

    companion object {
        private val particleTextures = mutableListOf<TextureAtlasSprite>()

        fun registerIcon(name: String) {
            particleTextures += ParticleIcon(name)
        }
    }
}