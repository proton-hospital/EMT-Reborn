package emt.recipe;

import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;

/** Crucible recipe whose catalyst may be an item of the 1.20.1 world (for example an IC2 UU-Matter cell). */
public class EMTCrucibleRecipe extends CrucibleRecipe {
    private final HostIngredient host;

    public EMTCrucibleRecipe(String research, ItemStack result, HostIngredient catalyst, AspectList aspects) {
        super(research, result, new ItemStack(net.minecraft.init.Items.stick), aspects);
        this.host = catalyst;
        this.catalyst = null;
    }

    @SuppressWarnings("unchecked")
    public static EMTCrucibleRecipe register(String research, ItemStack result, HostIngredient catalyst, AspectList aspects) {
        EMTCrucibleRecipe recipe = new EMTCrucibleRecipe(research, result, catalyst, aspects);
        ThaumcraftApi.getCraftingRecipes().add(recipe);
        return recipe;
    }

    @Override
    public boolean catalystMatches(ItemStack stack) {
        if (catalyst == null) {
            catalyst = host.display(); // shown in the Thaumonomicon once MFCL is ready
        }
        return host.matches(stack);
    }

    /** Display catalyst for books and JEI. */
    public Object getCatalyst() {
        if (catalyst == null) {
            catalyst = host.display();
        }
        return catalyst;
    }
}
