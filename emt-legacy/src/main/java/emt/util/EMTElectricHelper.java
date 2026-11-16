package emt.util;

import ic2.api.item.ElectricItem;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class EMTElectricHelper {
    private EMTElectricHelper() {
    }

    public static ItemStack charged(Item item, double charge) {
        ItemStack stack = new ItemStack(item);
        ElectricItem.manager.charge(stack, charge, Integer.MAX_VALUE, true, false);
        return stack;
    }

    /** Adds the fully charged and the empty variant, like IC2 did for its creative tabs. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void addChargedAndEmpty(Item item, List list) {
        list.add(charged(item, Double.MAX_VALUE));
        list.add(new ItemStack(item));
    }
}
