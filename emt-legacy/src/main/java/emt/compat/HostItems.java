package emt.compat;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Items that exist only in the 1.20.1 host world (for example IndustrialCraft 2 parts)
 * are shown to legacy code by MFCL as "mfcl_host:inventory/stack" with the modern
 * stack stored in the "mfcl_inventory_stack" compound. This class builds and matches them.
 */
public final class HostItems {
    public static final String HOST_ITEM = "mfcl_host:inventory/stack";
    public static final String PAYLOAD = "mfcl_inventory_stack";

    private HostItems() {
    }

    /** Modern registry id ("ic2:partcircuit") of a host stack, or null for normal legacy stacks. */
    public static String hostId(ItemStack stack) {
        if (stack == null || stack.getTagCompound() == null || !stack.getTagCompound().hasKey(PAYLOAD, 10)) {
            return null;
        }
        String id = stack.getTagCompound().getCompoundTag(PAYLOAD).getString("id");
        return id.isEmpty() ? null : id;
    }

    private static Item hostItem;

    /**
     * MFCL keeps its host item in a private registry, so emtbridge hands us a real host stack
     * (see FeBridge#setHostStack) and we remember its item.
     */
    public static Item hostItem() {
        if (hostItem == null) {
            Object item = GameData.getItemRegistry().getObject(HOST_ITEM);
            if (item instanceof Item) {
                hostItem = (Item) item;
            }
        }
        return hostItem;
    }

    public static void setHostItem(Item item) {
        if (item != null) {
            hostItem = item;
        }
    }

    /** A stack used only for display (Thaumonomicon, NEI). Null until MFCL has registered its host item. */
    public static ItemStack display(String modernId, String name) {
        Item item = hostItem();
        if (item == null) {
            return null;
        }
        ItemStack stack = new ItemStack(item);
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound payload = new NBTTagCompound();
        payload.setString("id", modernId);
        payload.setByte("Count", (byte) 1);
        tag.setTag(PAYLOAD, payload);
        tag.setInteger("mfcl_stack_limit", 64);
        NBTTagCompound display = new NBTTagCompound();
        display.setString("Name", name);
        tag.setTag("display", display);
        stack.setTagCompound(tag);
        return stack;
    }
}
