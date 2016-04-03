package raven.api.opengl.vbo.model

/**
 * Created by Raven6101 on 22.03.2016.
 */
class TexturedModel(model: RawModel, val textureId: Int) : RawModel(model.vaoId, model.vertexCount) {
    companion object {
        fun fromRawModel(model: RawModel, textureId: Int): TexturedModel {
            return TexturedModel(model, textureId)
        }
    }
}
