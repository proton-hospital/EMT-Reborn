package emt.compat;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;

/**
 * Static entry points called from the 1.20.1 companion mod (emtbridge) through the
 * MFCL legacy accessor API. All numbers are Forge Energy, 1 FE == 1 EU.
 * Only primitives cross the bridge. Do not rename or change signatures without updating emtbridge.
 */
public final class FeBridge {
    public static final int VERSION = 4;
    /** Ordinary, armour-respecting hit. Hits that bypass armour are filtered on the 1.20.1 side. */
    private static final DamageSource HOST_HIT = new DamageSource("emt.host");

    private FeBridge() {
    }

    public static int version() {
        return VERSION;
    }

    /**
     * Called by emtbridge once MFCL has registered its host item: fills the display stacks of
     * recipes that show 1.20.1 items (Thaumonomicon reads some recipe fields directly).
     * Returns the number of recipes updated.
     */
    /** Receives a real MFCL host stack from emtbridge. Returns 1 when its item was recorded. */
    public static int setHostStack(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return 0;
        }
        HostItems.setHostItem(stack.getItem());
        return 1;
    }

    public static int onHostReady() {
        if (HostItems.hostItem() == null) {
            return 0;
        }
        int n = 0;
        for (Object recipe : thaumcraft.api.ThaumcraftApi.getCraftingRecipes()) {
            if (recipe instanceof emt.recipe.EMTCrucibleRecipe) {
                ((emt.recipe.EMTCrucibleRecipe) recipe).getCatalyst();
                n++;
            } else if (recipe instanceof emt.recipe.EMTArcaneRecipe) {
                ((emt.recipe.EMTArcaneRecipe) recipe).getInput();
                n++;
            }
        }
        return n;
    }

    // ---------------------------------------------------------------- energy

    public static boolean isElectric(ItemStack stack) {
        return stack != null && stack.getItem() instanceof IElectricItem;
    }

    public static int stored(ItemStack stack) {
        return isElectric(stack) ? clamp(ElectricItem.manager.getCharge(stack)) : 0;
    }

    public static int capacity(ItemStack stack) {
        return isElectric(stack) ? clamp(((IElectricItem) stack.getItem()).getMaxCharge(stack)) : 0;
    }

    public static boolean canExtract(ItemStack stack) {
        return isElectric(stack) && ((IElectricItem) stack.getItem()).canProvideEnergy(stack);
    }

    public static int receive(ItemStack stack, int amount, boolean simulate) {
        if (!isElectric(stack) || amount <= 0) {
            return 0;
        }
        return (int) Math.floor(ElectricItem.manager.charge(stack, amount, Integer.MAX_VALUE, false, simulate));
    }

    public static int extract(ItemStack stack, int amount, boolean simulate) {
        if (!isElectric(stack) || amount <= 0) {
            return 0;
        }
        return (int) Math.floor(ElectricItem.manager.discharge(stack, amount, Integer.MAX_VALUE, false, true, simulate));
    }

    /** Internal use by the item itself (armour tick costs): all or nothing, returns the amount used. */
    public static int use(ItemStack stack, int amount) {
        if (!isElectric(stack) || amount <= 0) {
            return 0;
        }
        return ElectricItem.manager.use(stack, amount, null) ? amount : 0;
    }

    /** Like use, but takes whatever is left when there is not enough (jetpack running dry). */
    public static int drain(ItemStack stack, int amount) {
        if (!isElectric(stack) || amount <= 0) {
            return 0;
        }
        return (int) Math.ceil(ElectricItem.manager.discharge(stack, amount, Integer.MAX_VALUE, true, false, false));
    }

    // ---------------------------------------------------------------- armour
    // Slots follow ISpecialArmor and the 1.20.1 EquipmentSlot index: 0 boots, 1 legs, 2 chest, 3 helmet.
    // The entity is passed as null: EMT armour must not depend on it in these methods.

    public static boolean isSpecialArmor(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ISpecialArmor;
    }

    /** Armour points shown in the HUD (ISpecialArmor#getArmorDisplay). */
    public static int armorDisplay(ItemStack stack, int slot) {
        if (!isSpecialArmor(stack)) {
            return 0;
        }
        return ((ISpecialArmor) stack.getItem()).getArmorDisplay(null, stack, slot);
    }

    /** ArmorProperties.AbsorbRatio * 1 000 000 for a hit of {@code damage} HP. */
    public static int armorRatioMicros(ItemStack stack, float damage, int slot) {
        ArmorProperties p = properties(stack, damage, slot);
        return p == null ? 0 : (int) Math.round(Math.max(0, Math.min(1000, p.AbsorbRatio)) * 1000000.0);
    }

    /** Maximum absorbed damage in milli-HP (ArmorProperties.AbsorbMax is counted in 1/25 HP). */
    public static int armorMaxMilli(ItemStack stack, float damage, int slot) {
        ArmorProperties p = properties(stack, damage, slot);
        if (p == null) {
            return 0;
        }
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, (long) p.AbsorbMax) * 1000L / 25L);
    }

    /** Priority of the armour piece (ArmorProperties.Priority), higher absorbs first. */
    public static int armorPriority(ItemStack stack, float damage, int slot) {
        ArmorProperties p = properties(stack, damage, slot);
        return p == null ? 0 : p.Priority;
    }

    /** Lets the armour pay for absorbed damage (ISpecialArmor#damageArmor). absorbedMilli is in milli-HP. */
    public static int armorDamage(ItemStack stack, int absorbedMilli, int slot) {
        if (!isSpecialArmor(stack) || absorbedMilli <= 0) {
            return 0;
        }
        double absorbed = absorbedMilli / 1000.0;
        int itemDamage = absorbed < 1 ? 1 : (int) absorbed;
        ((ISpecialArmor) stack.getItem()).damageArmor(null, stack, HOST_HIT, itemDamage, slot);
        return 1;
    }

    // ---------------------------------------------------------------- boots of the traveller

    /** speedBonus * 1 000 000 of EMT traveller boots, or -1 for other items. */
    public static int bootsSpeedMicros(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof emt.item.armor.boots.ItemElectricBootsTraveller)) {
            return -1;
        }
        return Math.round(((emt.item.armor.boots.ItemElectricBootsTraveller) stack.getItem()).speedBonus * 1000000f);
    }

    /** jumpBonus * 1 000 000 of EMT traveller boots, or -1 for other items. */
    public static int bootsJumpMicros(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof emt.item.armor.boots.ItemElectricBootsTraveller)) {
            return -1;
        }
        return Math.round(((emt.item.armor.boots.ItemElectricBootsTraveller) stack.getItem()).jumpBonus * 1000000f);
    }

    private static ArmorProperties properties(ItemStack stack, float damage, int slot) {
        if (!isSpecialArmor(stack)) {
            return null;
        }
        return ((ISpecialArmor) stack.getItem()).getProperties(null, stack, HOST_HIT, damage, slot);
    }

    private static int clamp(double value) {
        return value >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.max(0, value);
    }

    // ---------------------------------------------------------------- infused quantum chestplate
    // MFCL does not keep NBT changes made in onArmorTick and the legacy player has no view of the
    // 1.20.1 hand, so emtbridge attaches / removes the jetpack and wings through these methods.

    private static net.minecraft.nbt.NBTTagCompound chestTag(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof emt.item.armor.chestplate.ItemInfusedQuantumChestplate)) {
            return null;
        }
        return EMTPlatform.getOrCreateNbtData(stack);
    }

    /** Sets the attached part (1 jetpack, 2 thaumium, 3 nano, 4 quantum wings). */
    public static int chestAttach(ItemStack stack, int wing, int charge, String jetpack) {
        net.minecraft.nbt.NBTTagCompound nbt = chestTag(stack);
        if (nbt == null || wing < 1 || wing > 4 || nbt.getByte("wing") != 0) {
            return 0;
        }
        nbt.setByte("wing", (byte) wing);
        nbt.removeTag("useother");
        if (wing == 3) {
            nbt.setInteger("NWCharge", charge);
        } else if (wing == 4) {
            nbt.setInteger("QWCharge", charge);
        } else if (wing == 1 && jetpack != null) {
            nbt.setString("jetpackHex", toHex(jetpack)); // the 1.7.10 NBT parser cannot read quotes inside strings
        }
        return 1;
    }

    public static int chestCharge(ItemStack stack) {
        net.minecraft.nbt.NBTTagCompound nbt = chestTag(stack);
        if (nbt == null) {
            return 0;
        }
        return nbt.getByte("wing") == 3 ? nbt.getInteger("NWCharge") : nbt.getByte("wing") == 4 ? nbt.getInteger("QWCharge") : 0;
    }

    public static String chestJetpack(ItemStack stack) {
        net.minecraft.nbt.NBTTagCompound nbt = chestTag(stack);
        if (nbt == null) {
            return "";
        }
        return nbt.hasKey("jetpackHex") ? fromHex(nbt.getString("jetpackHex")) : nbt.getString("jetpackModern");
    }

    private static String toHex(String text) {
        byte[] bytes = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        StringBuilder out = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            out.append(Character.forDigit((b >> 4) & 15, 16)).append(Character.forDigit(b & 15, 16));
        }
        return out.toString();
    }

    private static String fromHex(String hex) {
        try {
            byte[] bytes = new byte[hex.length() / 2];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
            }
            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        } catch (RuntimeException e) {
            return "";
        }
    }

    /** Removes the attached part; returns the old wing value (0 when nothing was attached). */
    public static int chestDetach(ItemStack stack) {
        net.minecraft.nbt.NBTTagCompound nbt = chestTag(stack);
        if (nbt == null) {
            return 0;
        }
        int wing = nbt.getByte("wing");
        nbt.setByte("wing", (byte) 0);
        nbt.removeTag("NWCharge");
        nbt.removeTag("QWCharge");
        nbt.removeTag("jetpackModern");
        nbt.removeTag("jetpackHex");
        nbt.removeTag("jetpackStack");
        nbt.removeTag("hoverMode");
        return wing;
    }

    /** Toggles the jetpack hover mode: 1 = now on, 2 = now off. */
    public static int chestToggleHover(ItemStack stack) {
        net.minecraft.nbt.NBTTagCompound nbt = chestTag(stack);
        if (nbt == null) {
            return 0;
        }
        boolean on = !nbt.getBoolean("hoverMode");
        nbt.setBoolean("hoverMode", on);
        return on ? 1 : 2;
    }

    // ---------------------------------------------------------------- world requests

    /** Dungeon chest weight from EMT.cfg: 0 = tainted Mjolnir, 1 = One Ring; 0 when disabled. */
    public static int lootWeight(int which) {
        if (which == 0) {
            return emt.util.EMTConfigHandler.thorHammerResearch ? 0 : Math.max(0, emt.util.EMTConfigHandler.chanceTaintedMjolnir);
        }
        return emt.util.EMTConfigHandler.oneRingSpawn ? 0 : Math.max(0, emt.util.EMTConfigHandler.chanceOneRing);
    }

    /** Selftest: opens a Thaumcraft portable hole at x y z of the overworld (stone is placed first). */
    public static int testHole(int x, int y, int z) {
        net.minecraft.world.World world = net.minecraftforge.common.DimensionManager.getWorld(0);
        if (world == null) {
            net.minecraft.server.MinecraftServer server = net.minecraft.server.MinecraftServer.getServer();
            world = server == null || server.worldServers == null || server.worldServers.length == 0 ? null : server.worldServers[0];
        }
        if (world == null) {
            return -1;
        }
        world.setBlock(x, y, z, net.minecraft.init.Blocks.stone, 0, 3);
        return thaumcraft.common.items.wands.foci.ItemFocusPortableHole.createHole(world, x, y, z, -1, (byte) 1, (short) 40) ? 1 : 0;
    }

    /**
     * Test of the wand charging station (at x,y,z) and ethereal processor (at x+2,y,z):
     * phase 0 places them and fills them, phase 1 reports wand vis and processor slots.
     */
    public static String testMachines(int x, int y, int z, int phase) {
        net.minecraft.world.World world = net.minecraftforge.common.DimensionManager.getWorld(0);
        if (world == null) {
            return "no world";
        }
        if (phase == 0) {
            world.setBlock(x, y, z, emt.init.EMTBlocks.emtMachines, 0, 3);
            world.setBlock(x + 2, y, z, emt.init.EMTBlocks.emtMachines, 1, 3);
        }
        net.minecraft.tileentity.TileEntity wandTile = world.getTileEntity(x, y, z);
        net.minecraft.tileentity.TileEntity procTile = world.getTileEntity(x + 2, y, z);
        if (!(wandTile instanceof emt.tile.TileEntityIndustrialWandRecharge) || !(procTile instanceof emt.tile.TileEntityEtherealMacerator)) {
            return "tiles: " + wandTile + " / " + procTile;
        }
        emt.tile.TileEntityIndustrialWandRecharge station = (emt.tile.TileEntityIndustrialWandRecharge) wandTile;
        emt.tile.TileEntityEtherealMacerator proc = (emt.tile.TileEntityEtherealMacerator) procTile;
        if (phase == 0) {
            net.minecraft.item.ItemStack wand = new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigItems.itemWandCasting);
            thaumcraft.common.items.wands.ItemWandCasting item = (thaumcraft.common.items.wands.ItemWandCasting) wand.getItem();
            item.setCap(wand, thaumcraft.common.config.ConfigItems.WAND_CAP_IRON);
            item.setRod(wand, thaumcraft.common.config.ConfigItems.WAND_ROD_WOOD);
            station.setInventorySlotContents(0, wand);
            proc.setInventorySlotContents(0, new net.minecraft.item.ItemStack(net.minecraft.init.Blocks.iron_ore, 4));
            proc.maceratingSpeed = 20;
        }
        int fed = station.receiveEnergy(net.minecraftforge.common.util.ForgeDirection.UNKNOWN, 100000, false);
        net.minecraft.item.ItemStack wand = station.getStackInSlot(0);
        String vis = wand == null ? "no wand" : String.valueOf(
                ((thaumcraft.common.items.wands.ItemWandCasting) wand.getItem()).getAllVis(wand).getAmount(thaumcraft.api.aspects.Aspect.ORDER));
        StringBuilder slots = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            net.minecraft.item.ItemStack s = proc.getStackInSlot(i);
            slots.append(' ').append(s == null ? "-" : s.stackSize + "x" + s.getUnlocalizedName() + "@" + s.getItemDamage());
        }
        return "fed=" + fed + " wandOrderCentiVis=" + vis + " processor:" + slots + " cook=" + proc.cookTime;
    }

    private static final java.util.concurrent.ConcurrentLinkedQueue<String> SPAWNS = new java.util.concurrent.ConcurrentLinkedQueue<String>();

    /** Christmas focus: a 1.20.1 snow golem is spawned by emtbridge (legacy mobs render badly in MFCL). */
    public static void queueSnowman(String player, int x, int y, int z) {
        if (SPAWNS.size() < 256) {
            SPAWNS.add("snowman " + player + " " + x + " " + y + " " + z);
        }
    }

    /** Mjolnir / lightning crystal: a 1.20.1 lightning bolt (legacy weather entities are not shown by MFCL). */
    public static void queueLightning(net.minecraft.world.World world, net.minecraft.entity.player.EntityPlayer player, double x, double y, double z) {
        if (!world.isRemote && SPAWNS.size() < 256) {
            SPAWNS.add("lightning " + player.getCommandSenderName() + " " + x + " " + y + " " + z);
        }
    }

    /** Supercharged Mjolnir: an archer ally (1.20.1 snow golem shooting arrows). */
    public static void queueArcher(net.minecraft.world.World world, net.minecraft.entity.player.EntityPlayer player, double x, double y, double z) {
        if (!world.isRemote && SPAWNS.size() < 256) {
            SPAWNS.add("archer " + player.getCommandSenderName() + " " + x + " " + y + " " + z);
        }
    }

    /** Removes one item from the stack the player is using (legacy code cannot reach the 1.20.1 inventory). */
    public static void queueConsume(net.minecraft.world.World world, net.minecraft.entity.player.EntityPlayer player) {
        if (!world.isRemote && SPAWNS.size() < 256) {
            SPAWNS.add("consume " + player.getCommandSenderName() + " 0 0 0");
        }
    }

    /** Next queued request or "" when there is none. */
    public static String pollSpawn() {
        String next = SPAWNS.poll();
        return next == null ? "" : next;
    }
}
