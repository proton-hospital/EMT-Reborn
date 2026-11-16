package emt.bridge;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

/**
 * World requests queued by legacy EMT code (emt.compat.FeBridge) and carried out with 1.20.1
 * entities, because legacy mobs and weather entities hosted by MFCL render badly and lag:
 * Christmas focus snow golems, Mjolnir / lightning crystal bolts, the supercharged Mjolnir's
 * archers (snow golems that shoot arrows, 40 HP, explode on death, like EntityArcher of 1.2.6.2).
 */
public final class Spawns {
    public static final String ARCHER_TAG = "emt_archer";

    private Spawns() {
    }

    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        MinecraftServer server = event.getServer();
        LegacyItems.runtime().ifPresent(rt -> {
            FeBridgeAccess bridge = LegacyItems.bridge(rt);
            if (bridge == null) {
                return;
            }
            try {
                for (int i = 0; i < 64; i++) {
                    String request = bridge.pollSpawn();
                    if (request == null || request.isEmpty()) {
                        return;
                    }
                    handle(server, request.split(" "));
                }
            } catch (RuntimeException e) {
                EmtBridge.warnOnce("spawn-poll", "Could not read EMT spawn requests", e);
            }
        });
    }

    private static void handle(MinecraftServer server, String[] p) {
        if (p.length != 5) {
            return;
        }
        ServerPlayer player = server.getPlayerList().getPlayerByName(p[1]);
        if (player == null) {
            return;
        }
        ServerLevel level = player.serverLevel();
        double x = Double.parseDouble(p[2]);
        double y = Double.parseDouble(p[3]);
        double z = Double.parseDouble(p[4]);
        switch (p[0]) {
            case "snowman" -> EntityType.SNOW_GOLEM.spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
            case "lightning" -> {
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                if (bolt != null) {
                    bolt.moveTo(x, y, z);
                    bolt.setCause(player);
                    level.addFreshEntity(bolt);
                }
            }
            case "archer" -> archer(level, x, y, z);
            case "consume" -> consume(player);
            default -> {
            }
        }
    }

    static SnowGolem archer(ServerLevel level, double x, double y, double z) {
        SnowGolem golem = EntityType.SNOW_GOLEM.create(level);
        if (golem == null) {
            return null;
        }
        golem.moveTo(x, y, z, level.random.nextFloat() * 360f, 0f);
        golem.addTag(ARCHER_TAG);
        golem.setPumpkin(false);
        golem.setCustomName(Component.translatable("entity.emtbridge.archer"));
        golem.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW));
        golem.setDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND, 0f);
        if (golem.getAttribute(Attributes.MAX_HEALTH) != null) {
            golem.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0);
        }
        golem.setHealth(40f);
        golem.finalizeSpawn(level, level.getCurrentDifficultyAt(golem.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
        level.addFreshEntity(golem);
        return golem;
    }

    private static void consume(ServerPlayer player) {
        ItemStack used = player.getUseItem();
        if (used.isEmpty() || !LegacyItems.isEmtItem(used.getItem())) {
            used = player.getMainHandItem();
        }
        if (!used.isEmpty() && LegacyItems.isEmtItem(used.getItem())) {
            used.shrink(1);
        }
    }

    private static boolean isArcher(Object entity) {
        return entity instanceof SnowGolem golem && golem.getTags().contains(ARCHER_TAG);
    }

    /** Archers shoot arrows instead of snowballs. */
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof Snowball ball) || !isArcher(ball.getOwner())) {
            return;
        }
        event.setCanceled(true);
        SnowGolem golem = (SnowGolem) ball.getOwner();
        Level level = event.getLevel();
        Arrow arrow = new Arrow(level, golem);
        Vec3 dir = ball.getDeltaMovement().normalize();
        arrow.shoot(dir.x, dir.y, dir.z, 1.6f, 14 - level.getDifficulty().getId() * 4);
        arrow.setBaseDamage(2.0 + level.getDifficulty().getId() * 0.11);
        arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
        level.addFreshEntity(arrow);
        golem.playSound(SoundEvents.SKELETON_SHOOT, 1.0f, 1.0f / (golem.getRandom().nextFloat() * 0.4f + 0.8f));
    }

    public static void onDeath(LivingDeathEvent event) {
        if (isArcher(event.getEntity()) && !event.getEntity().level().isClientSide) {
            SnowGolem golem = (SnowGolem) event.getEntity();
            golem.level().explode(golem, golem.getX(), golem.getY(), golem.getZ(), 1.0f, Level.ExplosionInteraction.MOB);
        }
    }

    public static void onDrops(LivingDropsEvent event) {
        if (!isArcher(event.getEntity())) {
            return;
        }
        SnowGolem golem = (SnowGolem) event.getEntity();
        event.getDrops().clear();
        int looting = event.getLootingLevel();
        int arrows = golem.getRandom().nextInt(3 + looting);
        int bones = golem.getRandom().nextInt(3 + looting);
        if (arrows > 0) {
            event.getDrops().add(new ItemEntity(golem.level(), golem.getX(), golem.getY(), golem.getZ(), new ItemStack(Items.ARROW, arrows)));
        }
        if (bones > 0) {
            event.getDrops().add(new ItemEntity(golem.level(), golem.getX(), golem.getY(), golem.getZ(), new ItemStack(Items.BONE, bones)));
        }
    }
}
