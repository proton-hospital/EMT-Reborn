package emt.bridge;

import github.com.gengyoubo.mfcl.api.LegacyObject;
import github.com.gengyoubo.mfcl.api.mla.LegacyMethod;
import github.com.gengyoubo.mfcl.api.mla.LegacyType;

/** MLA contract for the static methods of emt.compat.FeBridge in the legacy (1.7.10) EMT jar. */
@LegacyType("emt.compat.FeBridge")
public interface FeBridgeAccess {
    String STACK = "Lnet/minecraft/item/ItemStack;";

    @LegacyMethod(name = "version", isStatic = true, descriptor = "()I")
    int version();

    @LegacyMethod(name = "onHostReady", isStatic = true, descriptor = "()I")
    int onHostReady();

    @LegacyMethod(name = "setHostStack", isStatic = true, descriptor = "(" + STACK + ")I")
    int setHostStack(LegacyObject stack);

    @LegacyMethod(name = "bootsSpeedMicros", isStatic = true, descriptor = "(" + STACK + ")I")
    int bootsSpeedMicros(LegacyObject stack);

    @LegacyMethod(name = "bootsJumpMicros", isStatic = true, descriptor = "(" + STACK + ")I")
    int bootsJumpMicros(LegacyObject stack);

    @LegacyMethod(name = "isElectric", isStatic = true, descriptor = "(" + STACK + ")Z")
    boolean isElectric(LegacyObject stack);

    @LegacyMethod(name = "stored", isStatic = true, descriptor = "(" + STACK + ")I")
    int stored(LegacyObject stack);

    @LegacyMethod(name = "capacity", isStatic = true, descriptor = "(" + STACK + ")I")
    int capacity(LegacyObject stack);

    @LegacyMethod(name = "canExtract", isStatic = true, descriptor = "(" + STACK + ")Z")
    boolean canExtract(LegacyObject stack);

    @LegacyMethod(name = "receive", isStatic = true, descriptor = "(" + STACK + "IZ)I")
    int receive(LegacyObject stack, int amount, boolean simulate);

    @LegacyMethod(name = "extract", isStatic = true, descriptor = "(" + STACK + "IZ)I")
    int extract(LegacyObject stack, int amount, boolean simulate);

    @LegacyMethod(name = "use", isStatic = true, descriptor = "(" + STACK + "I)I")
    int use(LegacyObject stack, int amount);

    @LegacyMethod(name = "drain", isStatic = true, descriptor = "(" + STACK + "I)I")
    int drain(LegacyObject stack, int amount);

    // ---- armour (slot: 0 boots .. 3 helmet) ----

    @LegacyMethod(name = "isSpecialArmor", isStatic = true, descriptor = "(" + STACK + ")Z")
    boolean isSpecialArmor(LegacyObject stack);

    @LegacyMethod(name = "armorDisplay", isStatic = true, descriptor = "(" + STACK + "I)I")
    int armorDisplay(LegacyObject stack, int slot);

    @LegacyMethod(name = "armorRatioMicros", isStatic = true, descriptor = "(" + STACK + "FI)I")
    int armorRatioMicros(LegacyObject stack, float damage, int slot);

    @LegacyMethod(name = "armorMaxMilli", isStatic = true, descriptor = "(" + STACK + "FI)I")
    int armorMaxMilli(LegacyObject stack, float damage, int slot);

    @LegacyMethod(name = "armorPriority", isStatic = true, descriptor = "(" + STACK + "FI)I")
    int armorPriority(LegacyObject stack, float damage, int slot);

    @LegacyMethod(name = "armorDamage", isStatic = true, descriptor = "(" + STACK + "II)I")
    int armorDamage(LegacyObject stack, int absorbedMilli, int slot);

    // ---- infused quantum chestplate / world requests (bridge v3) ----

    @LegacyMethod(name = "chestAttach", isStatic = true, descriptor = "(" + STACK + "IILjava/lang/String;)I")
    int chestAttach(LegacyObject stack, int wing, int charge, String jetpack);

    @LegacyMethod(name = "chestCharge", isStatic = true, descriptor = "(" + STACK + ")I")
    int chestCharge(LegacyObject stack);

    @LegacyMethod(name = "chestJetpack", isStatic = true, descriptor = "(" + STACK + ")Ljava/lang/String;")
    String chestJetpack(LegacyObject stack);

    @LegacyMethod(name = "chestDetach", isStatic = true, descriptor = "(" + STACK + ")I")
    int chestDetach(LegacyObject stack);

    @LegacyMethod(name = "chestToggleHover", isStatic = true, descriptor = "(" + STACK + ")I")
    int chestToggleHover(LegacyObject stack);

    @LegacyMethod(name = "testHole", isStatic = true, descriptor = "(III)I")
    int testHole(int x, int y, int z);

    @LegacyMethod(name = "testMachines", isStatic = true, descriptor = "(IIII)Ljava/lang/String;")
    String testMachines(int x, int y, int z, int phase);

    @LegacyMethod(name = "lootWeight", isStatic = true, descriptor = "(I)I")
    int lootWeight(int which);

    @LegacyMethod(name = "pollSpawn", isStatic = true, descriptor = "()Ljava/lang/String;")
    String pollSpawn();
}
