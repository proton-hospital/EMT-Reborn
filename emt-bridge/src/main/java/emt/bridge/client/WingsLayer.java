package emt.bridge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Wings and the infused quantum chestplate's jetpack/wings, drawn on the 1.20.1 player.
 * Geometry is a 1:1 port of EMT's ModelWings / ModelSpecialArmor (MFCL only renders legacy
 * armour as a plain chestplate, so the legacy getArmorModel is never used).
 */
public final class WingsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final Pattern WING = Pattern.compile("[{,]wing:(\\d+)b");
    private static final Map<String, String> WING_ITEMS = Map.of(
            "feather_wing", "featherwing",
            "thaumium_wing", "thaumiumwing",
            "nanosuit_wing", "nanowing",
            "quantum_wing", "quantumwing");
    private static final String QUANTUM_CHEST = "item_armor_quantum_chestplate";

    private final ModelPart wings;       // ModelWings (wing items)
    private final ModelPart chestWings;  // ModelSpecialArmor type 2 (chestplate with wings)
    private final ModelPart jetpack;     // ModelSpecialArmor type 1 (chestplate with jetpack)

    private WingsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
        wings = wings(24, 0, 0, 0, 0, 14, -1.5f, -0.5f);
        chestWings = wings(24, 32, 0, 32, 0, 46, 0f, 1f);
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("jetpack",
                CubeListBuilder.create().texOffs(16, 69).addBox(-4f, -12f, -2f, 8, 24, 4, new CubeDeformation(2f)), PartPose.ZERO);
        jetpack = LayerDefinition.create(mesh, 128, 96).bakeRoot();
    }

    private static ModelPart wings(int ru, int rv, int lu, int lv, int cu, int cv, float wingZ, float centerZ) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("right", CubeListBuilder.create().texOffs(ru, rv).addBox(-11f, -1f, wingZ, 11, 13, 1),
                PartPose.offsetAndRotation(-2f, 0f, 3f, 0f, 0.1570796f, 0f));
        root.addOrReplaceChild("left", CubeListBuilder.create().texOffs(lu, lv).mirror().addBox(0f, -1f, wingZ, 11, 13, 1),
                PartPose.offsetAndRotation(2f, 0f, 3f, 0f, -0.1570796f, 0f));
        root.addOrReplaceChild("center", CubeListBuilder.create().texOffs(cu, cv).mirror().addBox(0f, 0f, centerZ, 8, 14, 1),
                PartPose.offset(-4f, 0f, 3f));
        return LayerDefinition.create(mesh, 64, 64).bakeRoot();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void install(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            LivingEntityRenderer renderer = event.getSkin(skin);
            if (renderer instanceof PlayerRenderer player) {
                player.addLayer(new WingsLayer(player));
            }
        }
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, AbstractClientPlayer player,
                       float swing, float amount, float partial, float age, float yaw, float pitch) {
        if (player.isInvisible()) {
            return;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ResourceLocation id = chest.isEmpty() ? null : ForgeRegistries.ITEMS.getKey(chest.getItem());
        if (id == null || !"emt".equals(id.getNamespace())) {
            return;
        }
        ModelPart model;
        String texture;
        if (WING_ITEMS.containsKey(id.getPath())) {
            model = wings;
            texture = WING_ITEMS.get(id.getPath());
        } else if (QUANTUM_CHEST.equals(id.getPath())) {
            int wing = wingMode(chest);
            switch (wing) {
                case 1 -> { model = jetpack; texture = "quantum_jetpack"; }
                case 2 -> { model = chestWings; texture = "quantum_wings_t"; }
                case 3 -> { model = chestWings; texture = "quantum_wings_n"; }
                case 4 -> { model = chestWings; texture = "quantum_wings_q"; }
                default -> { return; }
            }
        } else {
            return;
        }
        boolean spread = !player.onGround() && player.getDeltaMovement().y > 0;
        if (model != jetpack) {
            model.getChild("right").yRot = spread ? 0.5f : 0.1570796f;
            model.getChild("left").yRot = spread ? -0.5f : -0.1570796f;
            float fold = player.isCrouching() ? 0.5f : 0f;
            model.getChild("right").xRot = fold;
            model.getChild("left").xRot = fold;
            model.getChild("center").xRot = fold;
        }
        pose.pushPose();
        getParentModel().body.translateAndRotate(pose);
        VertexConsumer out = buffers.getBuffer(RenderType.entityCutoutNoCull(
                ResourceLocation.fromNamespaceAndPath("emt", "textures/models/" + texture + ".png")));
        model.render(pose, out, light, OverlayTexture.NO_OVERLAY);
        pose.popPose();
    }

    /** "wing" byte of the chestplate's legacy NBT (0 none, 1 jetpack, 2 thaumium, 3 nano, 4 quantum). */
    static int wingMode(ItemStack chest) {
        CompoundTag legacy = chest.getTagElement("mfcl_legacy");
        if (legacy == null) {
            return 0;
        }
        Matcher m = WING.matcher(legacy.getString("snbt"));
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }
}
