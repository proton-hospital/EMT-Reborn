package emt.recipe;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;

/**
 * Infusion recipe whose central item, components and output may be items of the 1.20.1 world
 * ({@link HostIngredient}, e.g. IndustrialCraft 2 parts).
 *
 * The infusion matrix copies {@link #getComponents()} right after a successful {@link #matches}
 * and then looks for those exact stacks on the pedestals, so for that one call the matched
 * pedestal stacks are returned instead of the display stacks.
 */
public class EMTInfusionRecipe extends InfusionRecipe {
    private final Object central;
    private final Object[] parts;
    private final Object output;
    private ItemStack[] lastMatch;

    public EMTInfusionRecipe(String research, Object output, int instability, AspectList aspects, Object central, Object[] parts) {
        super(research, output instanceof HostIngredient ? null : output, instability, aspects, null, new ItemStack[0]);
        this.output = output;
        this.central = central;
        this.parts = parts;
    }

    @SuppressWarnings("unchecked")
    public static EMTInfusionRecipe register(String research, Object output, int instability, AspectList aspects, Object central, Object[] parts) {
        EMTInfusionRecipe recipe = new EMTInfusionRecipe(research, output, instability, aspects, central, parts);
        ThaumcraftApi.getCraftingRecipes().add(recipe);
        return recipe;
    }

    @Override
    public boolean matches(ArrayList<ItemStack> input, ItemStack centralItem, World world, EntityPlayer player) {
        if (centralItem == null || central == null) {
            return false;
        }
        if (research.length() > 0 && player != null && !ThaumcraftApiHelper.isResearchComplete(player.getCommandSenderName(), research)) {
            return false;
        }
        if (!HostIngredient.accepts(central, centralItem)) {
            return false;
        }
        ArrayList<ItemStack> remaining = new ArrayList<ItemStack>(input);
        ItemStack[] matched = new ItemStack[parts.length];
        for (int p = 0; p < parts.length; p++) {
            boolean found = false;
            for (int i = 0; i < remaining.size(); i++) {
                if (HostIngredient.accepts(parts[p], remaining.get(i))) {
                    matched[p] = remaining.remove(i).copy();
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        if (!remaining.isEmpty()) {
            return false;
        }
        lastMatch = matched;
        return true;
    }

    @Override
    public ItemStack[] getComponents() {
        if (lastMatch != null) {
            ItemStack[] result = lastMatch;
            lastMatch = null;
            return result;
        }
        ItemStack[] result = new ItemStack[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = displayOf(parts[i]);
        }
        return result;
    }

    @Override
    public ItemStack getRecipeInput() {
        return displayOf(central);
    }

    @Override
    public Object getRecipeOutput(ItemStack input) {
        Object resolved = HostIngredient.resolve(output);
        return resolved instanceof ItemStack ? ((ItemStack) resolved).copy() : resolved;
    }

    @SuppressWarnings("unchecked")
    private static ItemStack displayOf(Object value) {
        Object resolved = HostIngredient.resolve(value);
        if (resolved instanceof ItemStack) {
            return (ItemStack) resolved;
        }
        if (resolved instanceof java.util.List && !((java.util.List<Object>) resolved).isEmpty()) {
            return (ItemStack) ((java.util.List<Object>) resolved).get(0);
        }
        return null;
    }
}
