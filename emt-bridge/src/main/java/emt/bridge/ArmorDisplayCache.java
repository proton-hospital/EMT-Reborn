package emt.bridge;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

/** Caches getArmorDisplay results per item + legacy NBT, so the HUD does not call the legacy runtime every frame. */
final class ArmorDisplayCache {
    private static final int LIMIT = 64;
    private static final Map<String, Integer> CACHE = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
            return size() > LIMIT;
        }
    };

    private ArmorDisplayCache() {
    }

    static synchronized int points(ItemStack stack, int slot) {
        CompoundTag legacy = stack.getTagElement("mfcl_legacy");
        String key = ForgeRegistries.ITEMS.getKey(stack.getItem()) + "|" + slot + "|" + (legacy == null ? "" : legacy.getString("snbt"));
        Integer cached = CACHE.get(key);
        if (cached == null) {
            cached = LegacyCall.on(stack, (b, s) -> b.isSpecialArmor(s) ? b.armorDisplay(s, slot) : 0, false).orElse(null);
            if (cached == null) {
                return 0; // runtime not ready, try again later
            }
            CACHE.put(key, cached);
        }
        return cached;
    }
}
