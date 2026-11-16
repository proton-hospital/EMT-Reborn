package emt.compat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/** Small helpers replacing ic2.core.IC2.platform and ic2.core.util.StackUtil. */
public final class EMTPlatform {
    private EMTPlatform() {
    }

    public static boolean isSimulating(World world) {
        return world != null && !world.isRemote;
    }

    public static boolean isSimulating(Entity entity) {
        return entity != null && isSimulating(entity.worldObj);
    }

    public static boolean isRendering(Entity entity) {
        return entity != null && entity.worldObj != null && entity.worldObj.isRemote;
    }

    public static void messagePlayer(EntityPlayer player, String message, Object... args) {
        if (player != null && isSimulating(player)) {
            player.addChatComponentMessage(new ChatComponentTranslation(message, args));
        }
    }

    public static void removePotion(EntityLivingBase entity, int potion) {
        if (entity != null) {
            entity.removePotionEffect(potion);
        }
    }

    public static NBTTagCompound getOrCreateNbtData(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    /**
     * Biome lookup that survives MFCL worlds, where World#getWorldChunkManager() can be null.
     * Falls back to plains (rain, sky) when the biome is unknown.
     */
    public static net.minecraft.world.biome.BiomeGenBase biome(World world, int x, int z) {
        try {
            net.minecraft.world.biome.BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
            if (biome != null) {
                return biome;
            }
        } catch (RuntimeException ignored) {
        }
        return net.minecraft.world.biome.BiomeGenBase.plains;
    }

    public static int getWorldHeight(World world) {
        return world.getHeight();
    }
}
