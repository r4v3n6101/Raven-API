package raven.api.client.opengl.vbo.loader

import raven.api.client.opengl.vbo.Model

/**
 * Created by r4v3n6101 on 30.04.2016.
 */
interface Loader {
    fun loadModel(file: ByteArray): Model
}