package emt.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Crafting-table recipe that uses items of the 1.20.1 world. It is only shown in the
 * Thaumonomicon; the working recipe is a 1.20.1 JSON recipe shipped in emtbridge
 * (MFCL cannot convert legacy recipes whose ingredients only exist in 1.20.1).
 */
public class EMTDisplayShapedRecipe extends ShapedOreRecipe {
    private final Object[] matchers;
    private final Object[] shown;

    public EMTDisplayShapedRecipe(ItemStack result, Object... recipe) {
        super(result, placeholderPattern(recipe));
        List<String> rows = new ArrayList<String>();
        int idx = 0;
        while (idx < recipe.length && recipe[idx] instanceof String) {
            rows.add((String) recipe[idx++]);
        }
        Map<Character, Object> keys = new HashMap<Character, Object>();
        for (; idx + 1 < recipe.length; idx += 2) {
            keys.put((Character) recipe[idx], recipe[idx + 1]);
        }
        int width = 0;
        for (String row : rows) {
            width = Math.max(width, row.length());
        }
        matchers = new Object[width * rows.size()];
        for (int y = 0; y < rows.size(); y++) {
            for (int x = 0; x < width; x++) {
                String row = rows.get(y);
                matchers[y * width + x] = x < row.length() ? keys.get(row.charAt(x)) : null;
            }
        }
        shown = super.getInput();
    }

    /** Same pattern with every HostIngredient replaced by a stick, so ShapedOreRecipe can parse it. */
    private static Object[] placeholderPattern(Object[] recipe) {
        Object[] copy = recipe.clone();
        for (int i = 0; i < copy.length; i++) {
            if (copy[i] instanceof HostIngredient) {
                copy[i] = new ItemStack(net.minecraft.init.Items.stick);
            }
        }
        return copy;
    }

    @Override
    public Object[] getInput() {
        for (int i = 0; i < matchers.length && i < shown.length; i++) {
            if (matchers[i] instanceof HostIngredient) {
                ItemStack display = ((HostIngredient) matchers[i]).display();
                if (display != null) {
                    shown[i] = display;
                }
            }
        }
        return shown;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        return false; // never crafts in the legacy world, see class comment
    }
}
