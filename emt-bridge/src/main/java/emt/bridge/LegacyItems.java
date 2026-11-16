package emt.bridge;

import github.com.gengyoubo.mfcl.api.LegacyObject;
import github.com.gengyoubo.mfcl.api.LegacyRuntime;
import github.com.gengyoubo.mfcl.api.LegacyStack;
import github.com.gengyoubo.mfcl.api.MfclApi;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Converts between 1.20.1 mirror stacks created by MFCL and legacy EMT stacks.
 * MFCL keeps the legacy NBT as an SNBT string in the "mfcl_legacy" compound ("snbt" key).
 */
public final class LegacyItems {
    public static final String LEGACY_MOD = "EMT";
    public static final String MODERN_NAMESPACE = "emt";
    private static final String MFCL_TAG = "mfcl_legacy";

    private static final Map<Item, String> KEYS = new IdentityHashMap<>();
    private static Map<ResourceLocation, String> byModernId;
    private static FeBridgeAccess bridge;
    private static boolean bridgeFailed;

    private LegacyItems() {
    }

    public static Optional<LegacyRuntime> runtime() {
        Optional<LegacyRuntime> runtime = MfclApi.runtime();
        return runtime.isPresent() && runtime.get().isOpen() ? runtime : Optional.empty();
    }

    /** Quick, allocation free test used when capabilities are attached. */
    public static boolean isEmtItem(Item item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        return id != null && MODERN_NAMESPACE.equals(id.getNamespace());
    }

    /** Lets the legacy side fill recipe display stacks of 1.20.1 items (needs MFCL's host item). */
    public static void hostReady() {
        runtime().ifPresent(rt -> {
            FeBridgeAccess access = bridge(rt);
            if (access != null) {
                try {
                    // a real MFCL host stack, so the legacy side learns which Item represents 1.20.1 items
                    LegacyObject host = rt.stack(new LegacyStack("mfcl_host:inventory/stack", 0, 1,
                            "{mfcl_inventory_stack:{id:\"minecraft:stone\",Count:1b},mfcl_stack_limit:64}"));
                    access.setHostStack(host);
                    EmtBridge.LOGGER.info("EMT recipes with IndustrialCraft 2 items prepared: {}", access.onHostReady());
                } catch (RuntimeException e) {
                    EmtBridge.warnOnce("host-ready", "Could not prepare EMT recipe displays", e);
                }
            }
        });
    }

    public static synchronized FeBridgeAccess bridge(LegacyRuntime runtime) {
        if (bridge == null && !bridgeFailed) {
            try {
                FeBridgeAccess access = runtime.accessor(FeBridgeAccess.class);
                int version = access.version();
                EmtBridge.LOGGER.info("Connected to legacy EMT energy bridge v{}", version);
                bridge = access;
            } catch (RuntimeException e) {
                bridgeFailed = true;
                EmtBridge.LOGGER.error("Legacy EMT energy bridge is unavailable; EMT items will not accept Forge Energy", e);
            }
        }
        return bridge;
    }

    /** Legacy registry key ("EMT:ElectricGogglesRevealing") of a mirror item, or null. */
    public static synchronized String legacyKey(LegacyRuntime runtime, Item item) {
        if (KEYS.containsKey(item)) {
            return KEYS.get(item);
        }
        if (byModernId == null) {
            byModernId = new HashMap<>();
            for (String key : runtime.registryKeys(LegacyRuntime.Registry.ITEM)) {
                if (key.startsWith(LEGACY_MOD + ":")) {
                    byModernId.put(modernId(key), key);
                }
            }
        }
        String key = byModernId.get(ForgeRegistries.ITEMS.getKey(item));
        KEYS.put(item, key);
        return key;
    }

    /** Same conversion as MFCL's LegacyRegistryIds (CamelCase -> snake_case, lower case). */
    static ResourceLocation modernId(String key) {
        ResourceLocation parsed = ResourceLocation.tryParse(key);
        if (parsed != null) {
            return parsed;
        }
        int colon = key.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : key.substring(0, colon);
        String path = colon < 0 ? key : key.substring(colon + 1);
        return ResourceLocation.fromNamespaceAndPath(normalize(namespace, false), normalize(path, true));
    }

    private static String normalize(String value, boolean path) {
        if (path) {
            value = value.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2").replaceAll("([a-z0-9])([A-Z])", "$1_$2");
        }
        StringBuilder result = new StringBuilder();
        value.toLowerCase(Locale.ROOT).codePoints().forEach(c -> {
            boolean ok = (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '-' || c == '.' || (path && c == '/');
            if (ok) {
                result.appendCodePoint(c);
            } else {
                result.append('_').append(Integer.toHexString(c)).append('_');
            }
        });
        return result.toString();
    }

    public static LegacyStack toLegacy(String key, ItemStack stack) {
        CompoundTag legacy = stack.getTagElement(MFCL_TAG);
        int metadata = stack.isDamageableItem() ? stack.getDamageValue() : legacy == null ? 0 : legacy.getInt("metadata");
        String snbt = legacy == null ? "" : legacy.getString("snbt");
        return new LegacyStack(key, metadata, Math.max(1, stack.getCount()), snbt);
    }

    /** Writes the legacy NBT of an updated legacy stack back into the 1.20.1 mirror stack. */
    public static void writeBack(ItemStack stack, LegacyStack updated) {
        // Electric tools show their charge as durability (IC2 1.7.10 style), so metadata changes too.
        if (stack.isDamageableItem()) {
            if (stack.getDamageValue() != updated.metadata()) {
                stack.setDamageValue(updated.metadata());
            }
        } else if (updated.metadata() != 0 || stack.getTagElement(MFCL_TAG) != null) {
            stack.getOrCreateTagElement(MFCL_TAG).putInt("metadata", updated.metadata());
        }
        String snbt = updated.snbt() == null ? "" : updated.snbt();
        if (snbt.isEmpty()) {
            CompoundTag legacy = stack.getTagElement(MFCL_TAG);
            if (legacy != null) {
                legacy.remove("snbt");
            }
            return;
        }
        stack.getOrCreateTagElement(MFCL_TAG).putString("snbt", snbt);
    }

    public static LegacyObject materialize(LegacyRuntime runtime, LegacyStack stack) {
        return runtime.stack(stack);
    }
}
