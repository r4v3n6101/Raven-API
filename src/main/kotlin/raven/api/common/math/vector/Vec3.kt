package raven.api.common.math.vector

/**
 * Created by r4v3n6101 on 13.05.2016.
 */
class Vec3
constructor(x: Number, y: Number, z: Number) : Vec(arrayOf(x.toFloat(), y.toFloat(), z.toFloat())) {

    @JvmOverloads constructor(value: Number = 1) : this(value, value, value)

    constructor(vec: Vec4) : this(vec.x, vec.y, vec.z)

    constructor(vec: Vec3) : this(vec.x, vec.y, vec.z)

    @JvmOverloads constructor(vec: Vec2, z: Number = 1) : this(vec.x, vec.y, z)

    constructor(x: Number, vec: Vec2) : this(x, vec.x, vec.y)


    var x: Float
        get() = this[0]
        set(value) {
            this[0] = value
        }

    var y: Float
        get() = this[1]
        set(value) {
            this[1] = value
        }

    var z: Float
        get() = this[2]
        set(value) {
            this[2] = value
        }

    /**
     * To return generic type of `this`
     */
    override fun times(float: Number) = super.times(float) as Vec3

    override fun plus(float: Number) = super.plus(float) as Vec3

    override fun invoke() = super.invoke() as Vec3

    override fun minus(float: Number) = super.minus(float) as Vec3

    override fun div(float: Number) = super.div(float) as Vec3

    override fun times(vec: Vec) = super.times(vec) as Vec3

    override fun plus(vec: Vec) = super.plus(vec) as Vec3

    override fun minus(vec: Vec) = super.minus(vec) as Vec3

    override fun div(vec: Vec) = super.div(vec) as Vec3

    override fun copy() = Vec3(this)

    override fun normalize() = super.normalize() as Vec3

    override fun unaryMinus() = super.unaryMinus() as Vec3

    override fun negate() = super.negate() as Vec3

    operator fun component0() = x
    operator fun component1() = y
    operator fun component2() = z
}