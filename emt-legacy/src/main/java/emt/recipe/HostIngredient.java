package emt.recipe;

import emt.compat.HostItems;
import java.util.List;
import net.minecraft.item.ItemStack;

/**
 * Recipe ingredient (or output) that stands for an item of the 1.20.1 world, by registry id.
 * In the legacy world MFCL shows such items as "mfcl_host:inventory/stack" stacks.
 */
public final class HostIngredient {
    public final String[] ids;
    public final String name;
    public final int count;
    private ItemStack display;

    public HostIngredient(String name, String... ids) {
        this(name, 1, ids);
    }

    private HostIngredient(String name, int count, String... ids) {
        this.name = name;
        this.ids = ids;
        this.count = count;
    }

    public HostIngredient withCount(int count) {
        return new HostIngredient(name, count, ids);
    }

    public boolean matches(ItemStack stack) {
        String id = HostItems.hostId(stack);
        if (id == null) {
            return false;
        }
        for (String candidate : ids) {
            if (candidate.equals(id)) {
                return true;
            }
        }
        return false;
    }

    /** A legacy stack showing (or producing) this item. Null until MFCL has registered its host item. */
    public ItemStack display() {
        if (display == null) {
            display = HostItems.display(ids[0], name);
            if (display != null) {
                display.stackSize = count;
            }
        }
        return display == null ? null : display;
    }

    /** Resolves recipe objects: HostIngredient to its display stack, everything else unchanged. */
    public static Object resolve(Object value) {
        return value instanceof HostIngredient ? ((HostIngredient) value).display() : value;
    }

    /** Generic matcher for recipe inputs (ItemStack with wildcard, ore dictionary list, HostIngredient). */
    @SuppressWarnings("unchecked")
    public static boolean accepts(Object expected, ItemStack actual) {
        if (expected == null) {
            return actual == null;
        }
        if (actual == null) {
            return false;
        }
        if (expected instanceof HostIngredient) {
            return ((HostIngredient) expected).matches(actual);
        }
        if (expected instanceof ItemStack) {
            ItemStack target = (ItemStack) expected;
            if (target.getItem() != actual.getItem()) {
                return false;
            }
            int damage = target.getItemDamage();
            return damage == 32767 || damage == actual.getItemDamage();
        }
        if (expected instanceof List) {
            for (Object option : (List<Object>) expected) {
                if (option instanceof ItemStack && accepts(option, actual)) {
                    return true;
                }
            }
        }
        return false;
    }
}
