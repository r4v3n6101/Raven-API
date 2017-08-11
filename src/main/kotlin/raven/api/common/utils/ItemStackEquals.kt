package raven.api.common.utils

import com.google.common.base.Equivalence
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.util.*

/**
 * Make equals, hashCode methods in item stack(for sets, maps and etc)
 */
object ItemStackEquals : Equivalence<ItemStack>() {

    override fun doEquivalent(a: ItemStack, b: ItemStack): Boolean {
        return ItemStack.areItemStacksEqual(a, b)
    }

    override fun doHash(stack: ItemStack): Int {
        return Objects.hash(
                Item.getIdFromItem(stack.item),
                stack.itemDamage,
                stack.stackSize,
                if (stack.hasTagCompound()) stack.stackTagCompound.hashCode() else 0)
    }

    fun getWrapper(stack: ItemStack): Wrapper<ItemStack> {
        return wrap(stack)
    }
}