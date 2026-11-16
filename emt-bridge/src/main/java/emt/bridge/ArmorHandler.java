package emt.bridge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * Emulates Forge 1.7.10 ISpecialArmor for EMT armour worn in the 1.20.1 world.
 * MFCL does not forward damage to legacy armour, so absorption and the EU cost are computed here,
 * using the original getProperties / damageArmor of the legacy item (same numbers as in 1.7.10).
 * The IndustrialCraft 2 port does the same for its own nano/quantum armour.
 */
final class ArmorHandler {
    static final EquipmentSlot[] SLOTS = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

    private ArmorHandler() {
    }

    private record Piece(ItemStack stack, int slot, int priority, double ratio) {
    }

    static void onHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        float damage = event.getAmount();
        if (entity.level().isClientSide || damage <= 0 || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) {
            return;
        }
        List<Piece> pieces = new ArrayList<>(4);
        for (EquipmentSlot equipment : SLOTS) {
            ItemStack stack = entity.getItemBySlot(equipment);
            if (stack.isEmpty() || !LegacyItems.isEmtItem(stack.getItem())) {
                continue;
            }
            int slot = equipment.getIndex();
            int[] values = new int[3];
            boolean special = LegacyCall.boolOn(stack, (b, s) -> {
                if (!b.isSpecialArmor(s)) {
                    return 0;
                }
                values[0] = b.armorRatioMicros(s, damage, slot);
                values[1] = b.armorMaxMilli(s, damage, slot);
                values[2] = b.armorPriority(s, damage, slot);
                return 1;
            });
            if (!special || values[0] <= 0 || values[1] <= 0) {
                continue;
            }
            double ratio = values[0] / 1_000_000.0;
            double maxHp = values[1] / 1000.0;
            ratio = Math.min(ratio, maxHp / damage); // AbsorbMax limit, as in ISpecialArmor.StandardizeList
            pieces.add(new Piece(stack, slot, values[2], ratio));
        }
        if (pieces.isEmpty()) {
            return;
        }

        // Higher priority absorbs first; the total absorbed share never exceeds 100 %.
        pieces.sort(Comparator.comparingInt(Piece::priority).reversed());
        double remaining = 1.0;
        double totalRatio = 0;
        int i = 0;
        while (i < pieces.size() && remaining > 0) {
            int priority = pieces.get(i).priority();
            int j = i;
            double group = 0;
            while (j < pieces.size() && pieces.get(j).priority() == priority) {
                group += pieces.get(j).ratio();
                j++;
            }
            double scale = group > remaining ? remaining / group : 1.0;
            for (int k = i; k < j; k++) {
                Piece piece = pieces.get(k);
                double ratio = piece.ratio() * scale;
                int absorbedMilli = (int) Math.round(damage * ratio * 1000.0);
                if (absorbedMilli > 0) {
                    LegacyCall.on(piece.stack(), (b, s) -> b.armorDamage(s, absorbedMilli, piece.slot()), true);
                }
                totalRatio += ratio;
            }
            remaining -= group * scale;
            i = j;
        }
        event.setAmount((float) (damage * (1.0 - Math.min(1.0, totalRatio))));
    }

    /** Sum of the HUD armour points (getArmorDisplay) of worn EMT armour. */
    static int displayPoints(LivingEntity entity) {
        int total = 0;
        for (EquipmentSlot equipment : SLOTS) {
            ItemStack stack = entity.getItemBySlot(equipment);
            if (!stack.isEmpty() && LegacyItems.isEmtItem(stack.getItem())) {
                total += ArmorDisplayCache.points(stack, equipment.getIndex());
            }
        }
        return total;
    }
}
