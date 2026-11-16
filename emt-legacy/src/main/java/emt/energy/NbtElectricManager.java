package emt.energy;

import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Stores EU in the "charge" NBT double, exactly like IndustrialCraft 2 for 1.7.10 did.
 * 1 EU == 1 FE: the 1.20.1 IC2 port uses the same numbers (nano armour = 1 000 000 FE).
 */
public final class NbtElectricManager implements IElectricItemManager {
    public static final String CHARGE = "charge";

    private static NBTTagCompound tag(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    @Override
    public double charge(ItemStack stack, double amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
        if (stack == null || !(stack.getItem() instanceof IElectricItem) || amount <= 0) {
            return 0;
        }
        IElectricItem item = (IElectricItem) stack.getItem();
        if (item.getTier(stack) > tier) {
            return 0;
        }
        double max = item.getMaxCharge(stack);
        double current = getCharge(stack);
        double transfer = Math.min(amount, max - current);
        if (!ignoreTransferLimit) {
            transfer = Math.min(transfer, item.getTransferLimit(stack));
        }
        if (transfer <= 0) {
            return 0;
        }
        if (!simulate) {
            setCharge(stack, current + transfer);
        }
        return transfer;
    }

    @Override
    public double discharge(ItemStack stack, double amount, int tier, boolean ignoreTransferLimit, boolean externally, boolean simulate) {
        if (stack == null || !(stack.getItem() instanceof IElectricItem) || amount <= 0) {
            return 0;
        }
        IElectricItem item = (IElectricItem) stack.getItem();
        if (externally && !item.canProvideEnergy(stack)) {
            return 0;
        }
        if (item.getTier(stack) > tier) {
            return 0;
        }
        double current = getCharge(stack);
        double transfer = Math.min(amount, current);
        if (!ignoreTransferLimit) {
            transfer = Math.min(transfer, item.getTransferLimit(stack));
        }
        if (transfer <= 0) {
            return 0;
        }
        if (!simulate) {
            setCharge(stack, current - transfer);
        }
        return transfer;
    }

    @Override
    public double getCharge(ItemStack stack) {
        if (stack == null || stack.getTagCompound() == null) {
            return 0;
        }
        return stack.getTagCompound().getDouble(CHARGE);
    }

    public void setCharge(ItemStack stack, double value) {
        if (!(stack.getItem() instanceof IElectricItem)) {
            return;
        }
        double max = ((IElectricItem) stack.getItem()).getMaxCharge(stack);
        value = Math.max(0, Math.min(max, value));
        tag(stack).setDouble(CHARGE, value);
        updateDamage(stack, value, max);
    }

    /** Like IC2 1.7.10: tools with max damage 27 show their charge as durability. */
    private static void updateDamage(ItemStack stack, double charge, double max) {
        if (stack.getItem() instanceof net.minecraft.item.ItemArmor || max <= 0) {
            return;
        }
        int maxDamage = stack.getMaxDamage();
        if (maxDamage > 2) {
            stack.setItemDamage(1 + (int) Math.round((1.0 - charge / max) * (maxDamage - 2)));
        }
    }

    @Override
    public boolean canUse(ItemStack stack, double amount) {
        return getCharge(stack) >= amount;
    }

    @Override
    public boolean use(ItemStack stack, double amount, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode) {
            return true;
        }
        chargeFromArmor(stack, entity);
        if (!canUse(stack, amount)) {
            return false;
        }
        discharge(stack, amount, Integer.MAX_VALUE, true, false, false);
        return true;
    }

    @Override
    public void chargeFromArmor(ItemStack stack, EntityLivingBase entity) {
        // Battery packs live in the 1.20.1 world (IC2 port); topping up tools from them
        // is handled by the emtbridge companion mod, not from the legacy side.
    }

    @Override
    public String getToolTip(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof IElectricItem)) {
            return null;
        }
        long charge = (long) getCharge(stack);
        long max = (long) ((IElectricItem) stack.getItem()).getMaxCharge(stack);
        return charge + "/" + max + " EU";
    }
}
