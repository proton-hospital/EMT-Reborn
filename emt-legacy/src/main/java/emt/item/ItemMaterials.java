package emt.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.utils.InventoryUtils;

public class ItemMaterials extends Item {
   public IIcon[] icon = new IIcon[16];

   public ItemMaterials() {
      this.setCreativeTab(EMT.TAB);
      this.setMaxDamage(0);
      this.setMaxStackSize(64);
      this.setHasSubtypes(true);
   }

   public String getUnlocalizedName(ItemStack itemstack) {
      String name = null;
      switch (itemstack.getItemDamage()) {
         case 0:
            name = "oreClusterUranium";
            break;
         case 1:
            name = "crushedOreAmber";
            break;
         case 2:
            name = "purifiedOreAmber";
            break;
         case 3:
            name = "crushedOreCinnabar";
            break;
         case 4:
            name = "purifiedOreCinnabar";
            break;
         case 5:
            name = "thaumiumPlate";
            break;
         case 6:
            name = "lightningSummoner";
            break;
         case 7:
            name = "featherMesh";
            break;
         case 8:
            name = "glue";
            break;
         case 9:
            name = "ductTape";
            break;
         case 10:
            name = "rubberBall";
            break;
         case 11:
            name = "cardboard";
            break;
         case 12:
            name = "featherGluedCardboardWing";
            break;
         case 13:
            name = "taintedFeather";
            break;
         case 14:
            name = "thaumiumWing";
            break;
         case 15:
            name = "uumatterDrop";
            break;
         default:
            name = "nothing";
      }

      return "item.EMT." + name;
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister ri) {
      this.icon[0] = ri.registerIcon("emt:materials/clusteruranium");
      this.icon[1] = ri.registerIcon("emt:materials/crushedamber");
      this.icon[2] = ri.registerIcon("emt:materials/purifiedamber");
      this.icon[3] = ri.registerIcon("emt:materials/crushedcinnabar");
      this.icon[4] = ri.registerIcon("emt:materials/purifiedcinnabar");
      this.icon[5] = ri.registerIcon("emt:materials/thaumiumplate");
      this.icon[6] = ri.registerIcon("emt:materials/lightningsummoner");
      this.icon[7] = ri.registerIcon("emt:materials/feathermesh");
      this.icon[8] = ri.registerIcon("emt:materials/glue");
      this.icon[9] = ri.registerIcon("emt:materials/ducttape");
      this.icon[10] = ri.registerIcon("emt:materials/rubberball");
      this.icon[11] = ri.registerIcon("emt:materials/cardboard");
      this.icon[12] = ri.registerIcon("emt:materials/cardboardsheet");
      this.icon[13] = ri.registerIcon("emt:materials/taintedfeather");
      this.icon[14] = ri.registerIcon("emt:materials/thaumiumWing");
      this.icon[15] = ri.registerIcon("emt:materials/uumatterdrop");
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int meta) {
      return this.icon[meta];
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list) {
      for (int i = 1; i <= 15; i++) {
         list.add(new ItemStack(this, 1, i));
      }
   }

   public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
      if (stack != null && stack.getItemDamage() == 6 && this.getMaxItemUseDuration(stack) - count >= 40) {
         World world = player.worldObj;
         player.swingItem();
         float f = 1.0F;
         float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
         float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
         double playerX = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
         double playerY = player.prevPosY + (player.posY - player.prevPosY) * (double)f + 1.62 - (double)player.yOffset;
         double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
         Vec3 playerLoc = Vec3.createVectorHelper(playerX, playerY, playerZ);
         float f3 = MathHelper.cos(-f2 * 0.01745329F - 3.141593F);
         float f4 = MathHelper.sin(-f2 * 0.01745329F - 3.141593F);
         float f5 = -MathHelper.cos(-f1 * 0.01745329F);
         float f6 = MathHelper.sin(-f1 * 0.01745329F);
         float f7 = f4 * f5;
         float f9 = f3 * f5;
         float d3 = 5000.0F;
         Vec3 vec3d1 = playerLoc.addVector((double)(f7 * d3), (double)(f6 * d3), (double)(f9 * d3));
         MovingObjectPosition movingobjectposition = player.worldObj.rayTraceBlocks(playerLoc, vec3d1, true);
         if (movingobjectposition != null) {
            if (movingobjectposition.typeOfHit == MovingObjectType.BLOCK) {
               int x2 = movingobjectposition.blockX;
               int y2 = movingobjectposition.blockY;
               int z2 = movingobjectposition.blockZ;
               emt.compat.FeBridge.queueLightning(world, player, (double)x2, (double)y2, (double)z2);
            } else if (movingobjectposition.typeOfHit == MovingObjectType.ENTITY) {
               Entity entityhit = movingobjectposition.entityHit;
               double x = entityhit.posX;
               double y = entityhit.posY;
               double z = entityhit.posZ;
               emt.compat.FeBridge.queueLightning(world, player, x, y, z);
            }

            if (!player.capabilities.isCreativeMode) {
               emt.compat.FeBridge.queueConsume(world, player); // MFCL: the legacy inventory does not hold the 1.20.1 stacks
            }
         }

         player.stopUsingItem();
      }
   }

   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      if (stack.getItemDamage() == 6) {
         ArrowNockEvent event = new ArrowNockEvent(player, stack);
         MinecraftForge.EVENT_BUS.post(event);
         if (event.isCanceled()) {
            return event.result;
         }

         player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
      }

      return stack;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return stack.getItemDamage() == 6 ? 72000 : super.getMaxItemUseDuration(stack);
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return stack.getItemDamage() == 6 ? EnumAction.bow : EnumAction.none;
   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
      super.onUpdate(stack, world, entity, par4, par5);
      if (!entity.worldObj.isRemote
         && stack.getItemDamage() == 13
         && entity instanceof EntityLivingBase
         && !((EntityLivingBase)entity).isEntityUndead()
         && !((EntityLivingBase)entity).isPotionActive(Config.potionTaintPoisonID)
         && world.rand.nextInt(4321) <= stack.stackSize) {
         ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Config.potionTaintPoisonID, 120, 0, false));
         if (entity instanceof EntityPlayer) {
            InventoryUtils.consumeInventoryItem((EntityPlayer)entity, stack.getItem(), stack.getItemDamage());
         }
      }
   }
}
