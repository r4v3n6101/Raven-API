package raven.api.utils

import com.google.common.base.Equivalence
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.util.*

class ItemStackEquals : Equivalence<ItemStack>() {

    override fun doEquivalent(a: ItemStack, b: ItemStack): Boolean {
        return ItemStack.areItemStacksEqual(a, b)
    }

    override fun doHash(stack: ItemStack): Int {
        return Objects.hash(Item.getIdFromItem(stack.item),
                stack.itemDamage,
                stack.stackSize,
                if (stack.hasTagCompound()) stack.stackTagCompound.hashCode() else 0)
    }

    companion object {
        var INSTANCE = ItemStackEquals()

        fun getWrapper(stack: ItemStack): Equivalence.Wrapper<ItemStack> {
            return INSTANCE.wrap(stack)
        }
    }
}