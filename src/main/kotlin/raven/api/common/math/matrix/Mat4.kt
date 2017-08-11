package raven.api.common.math.matrix

import raven.api.common.math.vector.Vec4

/**
 * Created by r4v3n6101 on 14.05.2016.
 */
class Mat4
@JvmOverloads constructor(data: Array<Array<Float>> = arrayOf(
        arrayOf(1F, 0F, 0F, 0F),
        arrayOf(0F, 1F, 0F, 0F),
        arrayOf(0F, 0F, 1F, 0F),
        arrayOf(0F, 0F, 0F, 1F)
)) : Mat<Vec4>(data) {

    override fun get(row: Int): Vec4 {
        val vec = data[row]
        return Vec4(vec[0], vec[1], vec[2], vec[3])
    }

    @Deprecated("Write")
    override fun determinant(): Float {
        throw UnsupportedOperationException()
    }

    override fun rotate(vec: Vec4): Mat4 {
        if (vec.x != 0F) {
            val angle = vec.x.toDouble()
            val cos = Math.cos(angle).toFloat()
            val sin = Math.sin(angle).toFloat()
            val mat = Mat4(arrayOf(
                    arrayOf(1F, 0F, 0F, 0F),
                    arrayOf(0F, cos, -sin, 0F),
                    arrayOf(0F, sin, cos, 0F),
                    arrayOf(0F, 0F, 0F, 1F)
            ))
            this * mat
        }
        if (vec.y != 0F) {
            val angle = vec.y.toDouble()
            val cos = Math.cos(angle).toFloat()
            val sin = Math.sin(angle).toFloat()
            val mat = Mat4(arrayOf(
                    arrayOf(cos, 0F, sin, 0F),
                    arrayOf(0F, 1F, 0F, 0F),
                    arrayOf(-sin, 0F, cos, 0F),
                    arrayOf(0F, 0F, 0F, 1F)
            ))
            this * mat
        }
        if (vec.z != 0F) {
            val angle = vec.z.toDouble()
            val cos = Math.cos(angle).toFloat()
            val sin = Math.sin(angle).toFloat()
            val mat = Mat4(arrayOf(
                    arrayOf(cos, -sin, 0F, 0F),
                    arrayOf(sin, cos, 0F, 0F),
                    arrayOf(0F, 0F, 1F, 0F),
                    arrayOf(0F, 0F, 0F, 1F)
            ))
            this * mat
        }
        return this
    }

    override fun times(mat: Mat<Vec4>): Mat4 {
        val out = Mat4()
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

    override fun scale(vec: Vec4): Mat4 {
        val mat = Mat4(arrayOf(
                arrayOf(vec.x, 0F, 0F, 0F),
                arrayOf(0F, vec.y, 0F, 0F),
                arrayOf(0F, 0F, vec.z, 0F),
                arrayOf(0F, 0F, 0F, 1F)
        ))
        return this * mat
    }

    override fun times(vec: Vec4): Vec4 {
        val out = Vec4()
        for (row in 0..width - 1) {
            val vec1 = this[row]
            out[row] = vec1(vec)
        }
        return out
    }

    override fun translate(vec: Vec4): Mat4 {
        val mat = Mat4(arrayOf(
                arrayOf(1F, 0F, 0F, vec.x),
                arrayOf(0F, 1F, 0F, vec.y),
                arrayOf(0F, 0F, 1F, vec.z),
                arrayOf(0F, 0F, 0F, 1F)
        ))
        return this * mat
    }

    override fun transform(): Vec4 {
        return this * Vec4()
    }

    /**
     * Cut mat3 from mat4
     * @param rowOffset offset by row
     * @param colOffset offset by col
     * @throws ArrayIndexOutOfBoundsException if row or col > 1
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    operator fun invoke(rowOffset: Int, colOffset: Int): Mat3 {
        val mat = Mat3()
        for (row in 0..mat.width - 1) {
            for (col in 0..mat.height - 1) {
                mat[row, col] = this[row + rowOffset, col + colOffset]
            }
        }
        return mat
    }

    /**
     * Paste mat3 to mat4
     * @param rowOffset offset by row
     * @param colOffset offset by col
     * @throws ArrayIndexOutOfBoundsException if row or col > 1
     */
    @JvmOverloads
    @Throws(ArrayIndexOutOfBoundsException::class)
    operator fun invoke(mat: Mat3, rowOffset: Int = 0, colOffset: Int = 0): Mat4 {
        for (row in 0..mat.width - 1) {
            for (col in 0..mat.height - 1) {
                this[row + rowOffset, col + colOffset] = mat[row, col]
            }
        }
        return this
    }

    /**
     * To return generic type of `this`
     */
    override fun times(scalar: Float): Mat4 {
        return super.times(scalar) as Mat4
    }

    override fun transpose(): Mat4 {
        return super.transpose() as Mat4
    }

    override fun mod(vec: Vec4): Mat4 {
        return super.mod(vec) as Mat4
    }

    override fun invoke(vec: Vec4): Mat4 {
        return super.invoke(vec) as Mat4
    }
}