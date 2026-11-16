package emt.recipe;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;

/** Shapeless arcane recipe that accepts {@link HostIngredient}s (IC2 items from 1.20.1). */
public class EMTShapelessArcaneRecipe extends ShapelessArcaneRecipe {
    private final Object[] matchers;
    private final Object result;

    public EMTShapelessArcaneRecipe(String research, Object result, AspectList aspects, Object... inputs) {
        super(research, result instanceof ItemStack ? (ItemStack) result : new ItemStack(net.minecraft.init.Items.stick), aspects, new ItemStack(net.minecraft.init.Items.stick));
        this.result = result;
        this.matchers = new Object[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            Object value = inputs[i];
            if (value instanceof net.minecraft.item.Item) {
                value = new ItemStack((net.minecraft.item.Item) value, 1, 32767);
            } else if (value instanceof net.minecraft.block.Block) {
                value = new ItemStack((net.minecraft.block.Block) value, 1, 32767);
            } else if (value instanceof String) {
                value = net.minecraftforge.oredict.OreDictionary.getOres((String) value);
            }
            matchers[i] = value;
        }
    }

    @SuppressWarnings("unchecked")
    public static EMTShapelessArcaneRecipe register(String research, Object result, AspectList aspects, Object... inputs) {
        EMTShapelessArcaneRecipe recipe = new EMTShapelessArcaneRecipe(research, result, aspects, inputs);
        ThaumcraftApi.getCraftingRecipes().add(recipe);
        return recipe;
    }

    @Override
    public ItemStack getRecipeOutput() {
        Object resolved = HostIngredient.resolve(result);
        return resolved instanceof ItemStack ? (ItemStack) resolved : null;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        ItemStack out = getRecipeOutput();
        return out == null ? null : out.copy();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ArrayList getInput() {
        ArrayList list = new ArrayList();
        for (Object m : matchers) {
            Object resolved = HostIngredient.resolve(m);
            if (resolved != null) {
                list.add(resolved);
            }
        }
        return list;
    }

    @Override
    public boolean matches(IInventory inv, World world, EntityPlayer player) {
        if (research != null && research.length() > 0 && player != null
                && !ThaumcraftApiHelper.isResearchComplete(player.getCommandSenderName(), research)) {
            return false;
        }
        ArrayList<Object> required = new ArrayList<Object>();
        for (Object m : matchers) {
            required.add(m);
        }
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                ItemStack slot = ThaumcraftApiHelper.getStackInRowAndColumn(inv, x, y);
                if (slot == null) {
                    continue;
                }
                boolean found = false;
                for (int i = 0; i < required.size(); i++) {
                    if (HostIngredient.accepts(required.get(i), slot)) {
                        required.remove(i);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        return required.isEmpty();
    }
}
