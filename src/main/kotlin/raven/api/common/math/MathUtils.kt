package raven.api.common.math

import raven.api.common.math.vector.Vec

object MathUtils {

    fun lerp(a: Float, b: Float, k: Float): Float {
        return a + (b - a) * k
    }

    fun cosIntrpl(a: Float, b: Float, k: Float): Float {
        val ft = k * Math.PI
        val f = (1 - Math.cos(ft)) * .5
        return (a * (1 - f) + b * f).toFloat()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Vec> lerp(a: T, b: T, k: Float): T {
        val out: T = a.copy() as T
        val size = Math.min(a.size, b.size)
        for (i in 0..size - 1) {
            out[i] = lerp(a[i], b[i], k)
        }
        return out
    }

    /**
     * Fast inverted square
     *
     * Read more:
     * https://en.wikipedia.org/wiki/Fast_inverse_square_root
     */
    fun invSqrt(num: Float): Float {
        val half = num / 2
        val i = 0x5f3759df - (java.lang.Float.floatToIntBits(half) shr 1)
        val x = java.lang.Float.intBitsToFloat(i)
        return x * (1.5f - half * x * x)
    }
}