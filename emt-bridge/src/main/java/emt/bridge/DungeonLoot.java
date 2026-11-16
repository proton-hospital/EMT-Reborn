package emt.bridge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * EMT 1.2.6.2 put the tainted Mjolnir and the One Ring into dungeon chests through the 1.7.10
 * ChestGenHooks, which do not reach 1.20.1 loot tables. Added here to the vanilla dungeon chest,
 * one roll per item with chance = weight / 200 (EMT.cfg defaults: 25 -> 12.5 %, 15 -> 7.5 %).
 */
public final class DungeonLoot {
    private static final ResourceLocation DUNGEON = ResourceLocation.fromNamespaceAndPath("minecraft", "chests/simple_dungeon");

    private DungeonLoot() {
    }

    public static void onLootLoad(LootTableLoadEvent event) {
        if (!DUNGEON.equals(event.getName())) {
            return;
        }
        add(event, "emt_tainted_mjolnir", "emt:tainted_mjolnir", weight(0, 25));
        add(event, "emt_one_ring", "emt:base_baubles", weight(1, 15));
    }

    private static int weight(int which, int fallback) {
        return LegacyItems.runtime().map(LegacyItems::bridge).map(b -> {
            try {
                return b.lootWeight(which);
            } catch (RuntimeException e) {
                return fallback;
            }
        }).orElse(fallback);
    }

    private static void add(LootTableLoadEvent event, String pool, String id, int weight) {
        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(id));
        if (item == null || weight <= 0 || !ResourceLocation.tryParse(id).equals(ForgeRegistries.ITEMS.getKey(item))) {
            return;
        }
        int w = Math.min(weight, 200);
        LootPool.Builder builder = LootPool.lootPool().name(pool).setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item).setWeight(w));
        if (w < 200) {
            builder.add(EmptyLootItem.emptyItem().setWeight(200 - w));
        }
        event.getTable().addPool(builder.build());
        EmtBridge.LOGGER.info("Dungeon chests: {} with chance {}%", id, w / 2.0);
    }
}
