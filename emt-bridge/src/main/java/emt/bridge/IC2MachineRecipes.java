package emt.bridge;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * EMT 1.2.6.2 added recipes to IndustrialCraft 2 machines (macerator, ore washing plant,
 * thermal centrifuge, metal former). Each machine of the 1.20.1 IC2 port reads exactly one recipe
 * set (e.g. "ic2:macerator/recipes"), so EMT's entries are appended to those sets after every
 * datapack (re)load. Works by reflection: no compile dependency on IC2.
 */
final class IC2MachineRecipes {
    private static final String PKG = "sviluppatore.ic2.core.recipe.";
    private static final Set<Object> DONE = Collections.newSetFromMap(new WeakHashMap<>());
    private static ClassLoader ic2Loader;

    private IC2MachineRecipes() {
    }

    private record Out(String id, int count) {
    }

    static void inject(RecipeManager manager) {
        int added = 0;
        for (Recipe<?> recipe : manager.getRecipes()) {
            String id = recipe.getId().toString();
            if (id.startsWith("ic2:")) {
                ic2Loader = recipe.getClass().getClassLoader();
            }
            try {
                switch (id) {
                    case "ic2:macerator/recipes" -> added += macerator(recipe);
                    case "ic2:ore_washer/recipes" -> added += oreWasher(recipe);
                    case "ic2:thermal_centrifuge/recipes" -> added += centrifuge(recipe);
                    case "ic2:metal_former/rolling-recipes" -> added += rolling(recipe);
                    default -> {
                    }
                }
            } catch (ReflectiveOperationException | RuntimeException e) {
                EmtBridge.warnOnce("ic2-recipes-" + id, "Could not add EMT recipes to " + id, e);
            }
        }
        if (added > 0) {
            EmtBridge.LOGGER.info("Added {} EMT recipes to IndustrialCraft 2 machines", added);
        }
    }

    // ---- recipe content, identical to EMT 1.2.6.2 ----

    private static int macerator(Recipe<?> set) throws ReflectiveOperationException {
        if (!DONE.add(set)) {
            return 0;
        }
        List<Object> entries = new ArrayList<>();
        // Thaumcraft native clusters -> 22 tiny piles of dust
        addMachine(entries, "thaumcraft:item_nugget/meta_16", 1, "ic2:tiny_iron_dust", 22);
        addMachine(entries, "thaumcraft:item_nugget/meta_31", 1, "ic2:tiny_gold_dust", 22);
        addMachine(entries, "thaumcraft:item_nugget/meta_17", 1, "ic2:tiny_copper_dust", 22);
        addMachine(entries, "thaumcraft:item_nugget/meta_18", 1, "ic2:tiny_tin_dust", 22);
        addMachine(entries, "thaumcraft:item_nugget/meta_19", 1, "ic2:tiny_silver_dust", 22);
        addMachine(entries, "thaumcraft:item_nugget/meta_20", 1, "ic2:tiny_lead_dust", 22);
        // amber / cinnabar ore -> 2 crushed amber / cinnabar
        addMachine(entries, "thaumcraft:block_custom_ore/meta_7", 1, "emt:emt_items/meta_1", 2);
        addMachine(entries, "thaumcraft:block_custom_ore/meta_0", 1, "emt:emt_items/meta_3", 2);
        return prepend(set, "MachineRecipes$Entry", entries);
    }

    private static int oreWasher(Recipe<?> set) throws ReflectiveOperationException {
        if (!DONE.add(set)) {
            return 0;
        }
        List<Object> entries = new ArrayList<>();
        addMulti(entries, "OreWashRecipes$Entry", "emt:emt_items/meta_1", 1000,
                new Out("emt:emt_items/meta_2", 1), new Out("ic2:tiny_copper_dust", 2), new Out("ic2:stone_dust", 1));
        addMulti(entries, "OreWashRecipes$Entry", "emt:emt_items/meta_3", 1000,
                new Out("emt:emt_items/meta_4", 1), new Out("ic2:tiny_tin_dust", 2), new Out("ic2:stone_dust", 1));
        return prepend(set, "OreWashRecipes$Entry", entries);
    }

    private static int centrifuge(Recipe<?> set) throws ReflectiveOperationException {
        if (!DONE.add(set)) {
            return 0;
        }
        List<Object> entries = new ArrayList<>();
        addMulti(entries, "CentrifugeRecipes$Entry", "emt:emt_items/meta_2", 1000,
                new Out("ic2:tiny_copper_dust", 1), new Out("thaumcraft:item_resource/meta_6", 1));
        addMulti(entries, "CentrifugeRecipes$Entry", "emt:emt_items/meta_4", 1000,
                new Out("ic2:tiny_tin_dust", 1), new Out("thaumcraft:item_resource/meta_3", 1));
        return prepend(set, "CentrifugeRecipes$Entry", entries);
    }

    private static int rolling(Recipe<?> set) throws ReflectiveOperationException {
        if (!DONE.add(set)) {
            return 0;
        }
        List<Object> entries = new ArrayList<>();
        Item input = item("thaumcraft:item_resource/meta_2"); // thaumium ingot
        Item output = item("emt:emt_items/meta_5");          // thaumium plate
        if (input != null && output != null) {
            Constructor<?> c = entryClass("MetalFormerRecipe$Entry").getDeclaredConstructor(Ingredient.class, ItemStack.class);
            c.setAccessible(true);
            entries.add(c.newInstance(ing("thaumcraft:item_resource/meta_2", input), stack("emt:emt_items/meta_5", output, 1)));
        }
        return prepend(set, "MetalFormerRecipe$Entry", entries);
    }

    // ---- reflection helpers ----

    private static Item item(String id) {
        ResourceLocation key = ResourceLocation.tryParse(id);
        Item item = key == null ? null : ForgeRegistries.ITEMS.getValue(key);
        return item == null || item == net.minecraft.world.item.Items.AIR ? null : item;
    }

    /** Metadata variant of a split legacy item ("x/meta_N"), 0 when the id has none. */
    static int meta(String id) {
        int i = id.lastIndexOf("/meta_");
        return i < 0 ? 0 : Integer.parseInt(id.substring(i + 6));
    }

    /**
     * MFCL only knows the variant of a split item from its mfcl_legacy.metadata tag: a bare
     * "emt:emt_items/meta_5" stack is read as metadata 0 (the uranium cluster), so results carry it.
     */
    static ItemStack stack(String id, Item item, int count) {
        ItemStack stack = new ItemStack(item, count);
        int meta = meta(id);
        if (meta != 0) {
            stack.getOrCreateTagElement("mfcl_legacy").putInt("metadata", meta);
        }
        return stack;
    }

    /** Ingredient that also shows (and requires) the right variant. */
    static Ingredient ing(String id, Item item) {
        int meta = meta(id);
        if (meta == 0) {
            return Ingredient.of(item);
        }
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.put("mfcl_legacy", new net.minecraft.nbt.CompoundTag());
        tag.getCompound("mfcl_legacy").putInt("metadata", meta);
        return net.minecraftforge.common.crafting.PartialNBTIngredient.of(item, tag);
    }

    private static Class<?> entryClass(String name) throws ClassNotFoundException {
        ClassLoader loader = ic2Loader != null ? ic2Loader : IC2MachineRecipes.class.getClassLoader();
        return Class.forName(PKG + name, true, loader);
    }

    private static void addMachine(List<Object> entries, String in, int inCount, String out, int outCount) throws ReflectiveOperationException {
        Item input = item(in);
        Item output = item(out);
        if (input == null || output == null) {
            return; // e.g. Thaumcraft only has copper/tin clusters when those ores exist
        }
        Constructor<?> c = entryClass("MachineRecipes$Entry").getDeclaredConstructor(Ingredient.class, int.class, ItemStack.class);
        c.setAccessible(true);
        entries.add(c.newInstance(ing(in, input), inCount, stack(out, output, outCount)));
    }

    private static void addMulti(List<Object> entries, String type, String in, int amount, Out... outs) throws ReflectiveOperationException {
        Item input = item(in);
        if (input == null) {
            return;
        }
        NonNullList<ItemStack> results = NonNullList.create();
        for (Out out : outs) {
            Item item = item(out.id());
            if (item == null) {
                return;
            }
            results.add(stack(out.id(), item, out.count()));
        }
        Constructor<?> c = entryClass(type).getDeclaredConstructor(Ingredient.class, int.class, int.class, NonNullList.class);
        c.setAccessible(true);
        entries.add(c.newInstance(ing(in, input), 1, amount, results));
    }

    /** Puts EMT's entries in front of the set's own entries (EMT's amber/cinnabar recipes win). */
    private static int prepend(Recipe<?> set, String entryType, List<Object> extra) throws ReflectiveOperationException {
        if (extra.isEmpty()) {
            return 0;
        }
        Field field = set.getClass().getDeclaredField("entries");
        field.setAccessible(true);
        Object old = field.get(set);
        int oldLength = Array.getLength(old);
        Object merged = Array.newInstance(entryClass(entryType), oldLength + extra.size());
        for (int i = 0; i < extra.size(); i++) {
            Array.set(merged, i, extra.get(i));
        }
        System.arraycopy(old, 0, merged, extra.size(), oldLength);
        field.set(set, merged);
        return extra.size();
    }

}
