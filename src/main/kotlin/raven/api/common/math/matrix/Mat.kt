package raven.api.common.math.matrix

import raven.api.common.math.vector.Vec
import java.nio.FloatBuffer

/**
 * Created by r4v3n6101 on 14.05.2016.
 * Row major matrix
 * Do action with `this` matrix
 */
abstract class Mat<V : Vec>(var data: Array<Array<Float>>) {

    val width: Int
        get() = data.size

    val height: Int
        get() = data[0].size

    operator fun get(row: Int, col: Int): Float {
        return data[row][col]
    }

    operator fun set(row: Int, col: Int, value: Float) {
        data[row][col] = value
    }

    /**
     * @return row
     */
    abstract operator fun get(row: Int): V

    /**
     * Set row data from vector data
     */
    operator fun set(row: Int, value: V) {
        data[row] = value.data
    }

    /**
     * Read transpose matrix from buffer
     */
    fun readTranspose(buffer: FloatBuffer) {
        for (r in 0..width - 1) {
            for (c in 0..height - 1) {
                this[c, r] = buffer.get()
            }
        }
    }

    /**
     * Write transpose matrix into buffer
     */
    fun writeTranspose(buffer: FloatBuffer) {
        for (r in 0..width - 1) {
            for (c in 0..height - 1) {
                buffer.put(this[c, r])
            }
        }
    }

    /**
     * Read from buffer
     */
    fun read(buffer: FloatBuffer) {
        for (r in 0..width - 1) {
            for (c in 0..height - 1) {
                this[r, c] = buffer.get()
            }
        }
    }

    /**
     * Write into buffer
     */
    fun write(buffer: FloatBuffer) {
        for (r in 0..width - 1) {
            for (c in 0..height - 1) {
                buffer.put(this[r, c])
            }
        }
    }

    /**
     * Transpose matrix
     * @return this
     */
    open fun transpose(): Mat<V> {
        for (i in 0..width - 1) {
            IntProgression.fromClosedRange(i + 1, height - 1, 1).forEach {
                val temp = this[i, it]
                this[i, it] = this[it, i]
                this[it, i] = temp
            }
        }
        return this
    }

    /**
     * Scale by vector
     * @return this
     */
    abstract infix fun scale(vec: V): Mat<V>

    /**
     * Multiply by vector
     */
    abstract infix operator fun times(vec: V): V

    /**
     * Multiply by another matrix
     * @return this
     */
    abstract infix operator fun times(mat: Mat<V>): Mat<V>

    /**
     * Multiply by scalar
     * @return this
     */
    open infix operator fun times(scalar: Float): Mat<V> {
        for (row in 0..width - 1) {
            for (col in 0..height - 1) {
                this[row, col] *= scalar
            }
        }
        return this
    }

    abstract infix fun rotate(vec: V): Mat<V>

    /**
     * Rotate by angle
     * @return this
     * @sample mat1(vec) = rotated matrix
     */
    open operator infix fun invoke(vec: V): Mat<V> = rotate(vec)

    abstract infix fun translate(vec: V): Mat<V>

    /**
     * Translate by vec
     * @return this
     * @sample mat % vec = translated matrix
     */
    open infix operator fun mod(vec: V): Mat<V> = translate(vec)

    abstract fun transform(): V

    /**
     * Transform to vector
     * @sample mat() = vector
     */
    open operator fun invoke(): V = transform()

    /**
     * Calculate determinant of matrix
     */
    abstract fun determinant(): Float

    override fun toString(): String {
        val sb = StringBuilder()
        data.forEach {
            it.forEach { sb.append(it).append(" ") }
            sb.append("\n")
        }
        return sb.toString()
    }
}