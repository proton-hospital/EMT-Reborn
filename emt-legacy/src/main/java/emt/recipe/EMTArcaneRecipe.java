package emt.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;

/**
 * Shaped arcane recipe that also accepts {@link HostIngredient}s (IC2 items from 1.20.1).
 * Extends ShapedArcaneRecipe so the Thaumonomicon draws it as a normal arcane recipe.
 */
public class EMTArcaneRecipe extends ShapedArcaneRecipe {
    private final Object[] matchers;

    public EMTArcaneRecipe(String research, ItemStack result, AspectList aspects, Object... recipe) {
        // The parent constructor needs a valid pattern; the real one is parsed below.
        super(research, result, aspects, "#", '#', new ItemStack(net.minecraft.init.Items.stick));
        int idx = 0;
        List<String> rows = new ArrayList<String>();
        while (idx < recipe.length && recipe[idx] instanceof String) {
            rows.add((String) recipe[idx++]);
        }
        Map<Character, Object> keys = new HashMap<Character, Object>();
        for (; idx + 1 < recipe.length; idx += 2) {
            keys.put((Character) recipe[idx], normalize(recipe[idx + 1]));
        }
        this.height = rows.size();
        this.width = 0;
        for (String row : rows) {
            this.width = Math.max(this.width, row.length());
        }
        this.matchers = new Object[width * height];
        this.input = new Object[width * height];
        for (int y = 0; y < height; y++) {
            String row = rows.get(y);
            for (int x = 0; x < width; x++) {
                char c = x < row.length() ? row.charAt(x) : ' ';
                Object m = keys.get(c);
                matchers[y * width + x] = m;
                input[y * width + x] = displayOf(m);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static EMTArcaneRecipe register(String research, ItemStack result, AspectList aspects, Object... recipe) {
        EMTArcaneRecipe r = new EMTArcaneRecipe(research, result, aspects, recipe);
        ThaumcraftApi.getCraftingRecipes().add(r);
        return r;
    }

    private static Object normalize(Object value) {
        if (value instanceof Item) {
            return new ItemStack((Item) value, 1, OreDictionary.WILDCARD_VALUE);
        }
        if (value instanceof Block) {
            return new ItemStack((Block) value, 1, OreDictionary.WILDCARD_VALUE);
        }
        if (value instanceof String) {
            return OreDictionary.getOres((String) value);
        }
        return value;
    }

    private static Object displayOf(Object m) {
        if (m instanceof HostIngredient) {
            return ((HostIngredient) m).display();
        }
        return m;
    }

    @Override
    public Object[] getInput() {
        // Host display stacks can only be built after MFCL registered its host item.
        for (int i = 0; i < matchers.length; i++) {
            if (input[i] == null && matchers[i] instanceof HostIngredient) {
                input[i] = ((HostIngredient) matchers[i]).display();
            }
        }
        return input;
    }

    @Override
    public boolean matches(IInventory inv, World world, EntityPlayer player) {
        if (research != null && research.length() > 0 && player != null
                && !ThaumcraftApiHelper.isResearchComplete(player.getCommandSenderName(), research)) {
            return false;
        }
        for (int ox = 0; ox <= 3 - width; ox++) {
            for (int oy = 0; oy <= 3 - height; oy++) {
                if (check(inv, ox, oy, false) || check(inv, ox, oy, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean check(IInventory inv, int ox, int oy, boolean mirror) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int sx = x - ox;
                int sy = y - oy;
                Object m = null;
                if (sx >= 0 && sy >= 0 && sx < width && sy < height) {
                    m = mirror ? matchers[width - sx - 1 + sy * width] : matchers[sx + sy * width];
                }
                ItemStack slot = ThaumcraftApiHelper.getStackInRowAndColumn(inv, x, y);
                if (!accepts(m, slot)) {
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private static boolean accepts(Object m, ItemStack slot) {
        if (m == null) {
            return slot == null;
        }
        if (slot == null) {
            return false;
        }
        if (m instanceof HostIngredient) {
            return ((HostIngredient) m).matches(slot);
        }
        if (m instanceof ItemStack) {
            return itemEquals((ItemStack) m, slot);
        }
        if (m instanceof List) {
            for (ItemStack option : (List<ItemStack>) m) {
                if (itemEquals(option, slot)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean itemEquals(ItemStack target, ItemStack input) {
        if (target.getItem() != input.getItem()) {
            return false;
        }
        int damage = target.getItemDamage();
        return damage == OreDictionary.WILDCARD_VALUE || damage == input.getItemDamage();
    }
}
