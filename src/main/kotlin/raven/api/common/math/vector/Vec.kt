package raven.api.common.math.vector

import java.lang.Math.min
import java.nio.FloatBuffer
import java.util.*

/**
 * Created by r4v3n6101 on 13.05.2016.
 * Create new vector after operation
 */
abstract class Vec(var data: Array<Float>) {

    val size: Int
        get() = data.size

    val length: Float
        get() {
            return Math.sqrt((this dot this).toDouble()).toFloat()
        }

    /**
     * Find length of vector
     * @sample !vec = length
     */
    operator fun not() = length

    infix fun distanceTo(vec: Vec): Float {
        val out = copy()
        return !(out - vec)
    }

    open fun negate(): Vec {
        val vec = copy()
        for (i in 0..size - 1) {
            vec[i] = -vec[i]
        }
        return vec
    }

    /**
     * Negate vector
     */
    open operator fun unaryMinus() = negate()

    /**
     * Distance between 2 vectors
     * @sample vec1 ..out = distance
     */
    infix operator fun rangeTo(vec: Vec) = distanceTo(vec)

    /**
     * Calculate dot of 2 vectors
     * @sample vec1(out) = dot product
     */
    infix operator fun invoke(vec: Vec) = this dot vec


    infix fun dot(vec: Vec): Float {
        var a = 0F
        for (i in 0..min(size, vec.size) - 1) {
            a += this[i] * vec[i]
        }
        return a
    }

    /**
     * Read from buffer
     */
    fun read(buffer: FloatBuffer) {
        for (i in 0..size - 1) {
            this[i] = buffer.get()
        }
    }

    /**
     * Write into buffer
     */
    fun write(buffer: FloatBuffer) {
        data.forEach { buffer.put(it) }
    }

    /**
     * Find angle between 2 vectors
     * @sample vec1 % vec2 = angle(in radians)
     */
    infix operator fun mod(vec: Vec) = this angle vec

    infix fun angle(vec: Vec): Float = Math.acos(this(vec) / (!this * !vec).toDouble()).toFloat()

    /**
     * Return normalised vector
     * @sample out() = normalised vec
     */
    open fun invoke() = normalize()

    open fun normalize(): Vec {
        val l = 1F / !this
        return this * l
    }

    operator fun get(index: Int): Float {
        return data[index]
    }

    operator fun set(index: Int, value: Number) {
        data[index] = value.toFloat()
    }

    fun processElement(size: Int, action: (element: Float, index: Int) -> Float) {
        for (i in 0..size - 1) {
            this[i] = action(this[i], i)
        }
    }

    open infix operator fun times(float: Number): Vec {
        val vec = copy()
        vec.processElement(size) { element, i -> element * float.toFloat() }
        return vec
    }

    open infix operator fun plus(float: Number): Vec {
        val vec = copy()
        vec.processElement(size) { element, i -> element + float.toFloat() }
        return vec
    }

    open infix operator fun minus(float: Number): Vec {
        val vec = copy()
        vec.processElement(size) { element, i -> element - float.toFloat() }
        return vec
    }

    open infix operator fun div(float: Number): Vec {
        val vec = copy()
        vec.processElement(size) { element, i -> element / float.toFloat() }
        return vec
    }

    open infix operator fun times(vec: Vec): Vec {
        val out = copy()
        out.processElement(min(size, vec.size)) { element, i -> element * vec[i] }
        return out
    }

    open infix operator fun plus(vec: Vec): Vec {
        val out = copy()
        out.processElement(min(size, vec.size)) { element, i -> element + vec[i] }
        return out
    }

    open infix operator fun minus(vec: Vec): Vec {
        val out = copy()
        out.processElement(min(size, vec.size)) { element, i -> element - vec[i] }
        return out
    }

    open infix operator fun div(vec: Vec): Vec {
        val out = copy()
        out.processElement(min(size, vec.size)) { element, i -> element / vec[i] }
        return out
    }

    override fun toString(): String {
        return data.joinToString()
    }

    abstract fun copy(): Vec

    override fun equals(other: Any?): Boolean {
        if (other !is Vec) {
            return false
        }
        return Arrays.equals(data, other.data)
    }

    override fun hashCode(): Int {
        return Objects.hash(*data)
    }
}
