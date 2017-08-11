package raven.api.common.nbt

import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import kotlin.reflect.KProperty

fun compound(vararg data: Pair<String, Any>): NBTTagCompound {
    val tag = NBTTagCompound()
    data.forEach { tag[it.first] = it.second }
    return tag
}

fun itemStack(stack: ItemStack): NBTTagCompound {
    val tag = compound()
    stack.writeToNBT(tag)
    return tag
}

operator fun NBTTagCompound.set(key: String, value: Any) {
    setTag(key, value.nbtBase)
}

operator fun NBTTagCompound.plusAssign(pair: Pair<String, Any>) {
    this[pair.first] = pair.second
}

operator fun NBTTagCompound.minusAssign(key: String) {
    removeTag(key)
}

fun nbtListOf(vararg values: Any): NBTTagList {
    val nbtList = NBTTagList()
    values.forEach { nbtList += it }
    return nbtList
}

inline operator fun <reified T : Any> NBTTagCompound.get(key: String) = when (T::class.java) {
    Int::class.java -> getInteger(key) as T
    Float::class.java -> getFloat(key) as T
    Double::class.java -> getDouble(key) as T
    Byte::class.java -> getByte(key) as T
    String::class.java -> getString(key) as T
    Short::class.java -> getShort(key) as T
    Long::class.java -> getLong(key) as T
    Boolean::class.java -> getBoolean(key) as T
    NBTBase::class.java -> getTag(key) as T
    ItemStack::class.java -> ItemStack.loadItemStackFromNBT(getCompoundTag(key)) as T
    else -> throw IllegalArgumentException("Wrong class, ${T::class.java}")
}

operator fun NBTTagList.plusAssign(obj: Any) {
    appendTag(obj.nbtBase)
}

operator fun NBTTagList.minusAssign(index: Int) {
    removeTag(index)
}

inline operator fun <reified T : Any> NBTTagList.get(index: Int) = when (T::class.java) {
    Float::class.java -> func_150308_e(index) as T
    Double::class.java -> func_150309_d(index) as T
    String::class.java -> getStringTagAt(index) as T
    NBTTagCompound::class.java -> getCompoundTagAt(index) as T
    else -> throw IllegalArgumentException("Wrong class, ${T::class.java}")
}

inline operator fun <reified T : Any> NBTTagCompound.getValue(thisRef: Any?, property: KProperty<*>): T {
    return this[property.name]
}

operator fun <T : Any> NBTTagCompound.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this[property.name] = value
}

/**
 * May produce exception
 */
val Any.nbtBase: NBTBase
    get() = when (this) {
        is Int -> NBTTagInt(this)
        is Float -> NBTTagFloat(this)
        is Double -> NBTTagDouble(this)
        is Byte -> NBTTagByte(this)
        is String -> NBTTagString(this)
        is Short -> NBTTagShort(this)
        is Long -> NBTTagLong(this)
        is Boolean -> NBTTagByte(if (this) 1 else 0)
        is IntArray -> NBTTagIntArray(this)
        is ByteArray -> NBTTagByteArray(this)
        is NBTBase -> this
        is ItemStack -> itemStack(this)
        else -> throw IllegalArgumentException("Wrong value, $this")
    }