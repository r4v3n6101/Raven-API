package raven.api.common.math.matrix

import raven.api.common.math.vector.Vec3

/**
 * Created by r4v3n6101 on 26.05.2016.
 */
class Mat3
@JvmOverloads constructor(data: Array<Array<Float>> = arrayOf(
        arrayOf(1F, 0F, 0F),
        arrayOf(0F, 1F, 0F),
        arrayOf(0F, 0F, 1F)
)) : Mat<Vec3>(data) {

    override fun get(row: Int): Vec3 {
        val vec = data[row]
        return Vec3(vec[0], vec[1], vec[2])
    }

    @Deprecated("Write")
    override fun determinant(): Float {
        throw UnsupportedOperationException()
    }

    override fun rotate(vec: Vec3): Mat3 {
        if (vec.x != 0F) {
            val angle = vec.x.toDouble()
            val cos = Math.cos(angle).toFloat()
            val sin = Math.sin(angle).toFloat()
            val mat = Mat3(arrayOf(
                    arrayOf(cos, -sin, 0F),
                    arrayOf(sin, cos, 0F),
                    arrayOf(0F, 0F, 1F)
            ))
            this * mat
        }
        return this
    }

    override fun times(mat: Mat<Vec3>): Mat3 {
        val out = Mat3()
        mat.transpose()//switch to column major(to get column instead of row)
        for (row in 0..width - 1) {
            for (col in 0..height - 1) {
                val vec1 = this[row]
                val vec2 = mat[col]
                out[row, col] = vec1(vec2)
            }
        }
        data = out.data
        return this
    }

    override fun scale(vec: Vec3): Mat3 {
        val mat = Mat3(arrayOf(
                arrayOf(vec.x, 0F, 0F),
                arrayOf(0F, vec.y, 0F),
                arrayOf(0F, 0F, 1F)
        ))
        return this * mat
    }

    override fun times(vec: Vec3): Vec3 {
        val out = Vec3()
        for (row in 0..width - 1) {
            val vec1 = this[row]
            out[row] = vec1(vec)
        }
        return out
    }

    override fun translate(vec: Vec3): Mat3 {
        val mat = Mat3(arrayOf(
                arrayOf(1F, 0F, vec.x),
                arrayOf(0F, 1F, vec.y),
                arrayOf(0F, 0F, 1F)
        ))
        return this * mat
    }

    override fun transform(): Vec3 {
        return this * Vec3()
    }

    /**
     * To return generic type of `this`
     */
    override fun times(scalar: Float): Mat3 {
        return super.times(scalar) as Mat3
    }

    override fun transpose(): Mat3 {
        return super.transpose() as Mat3
    }

    override fun mod(vec: Vec3): Mat3 {
        return super.mod(vec) as Mat3
    }

    override fun invoke(vec: Vec3): Mat3 {
        return super.invoke(vec) as Mat3
    }
}