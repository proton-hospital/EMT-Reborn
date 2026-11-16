package emt.item.tool.omnitool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.util.EMTConfigHandler;
import ic2.api.item.ElectricItem;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ItemOmnitoolThaumium extends ItemOmnitoolDiamond {
   public ItemOmnitoolThaumium() {
      this.efficiencyOnProperMaterial = 21.0F;
      this.setCreativeTab(EMT.TAB);
      this.setMaxStackSize(1);
      if (!EMTConfigHandler.toolsInBore) {
         this.setMaxDamage(27);
      } else {
         this.setMaxDamage(467);
      }

      this.maxCharge = 140000;
      this.hitCost = 650;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void registerIcons(IIconRegister iconRegister) {
      this.itemIcon = iconRegister.registerIcon("emt:tools/omnitool_thaumium");
   }

   @Override
   public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int par4, int par5, int par6, EntityLivingBase entityLiving) {
      if (!EMTConfigHandler.toolsInBore) {
         this.cost = 300;
      } else {
         this.cost = 1;
      }

      ElectricItem.manager.use(stack, (double)this.cost, entityLiving);
      return true;
   }

   @Override
   public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase attacker) {
      if (ElectricItem.manager.use(itemstack, (double)this.hitCost, attacker)) {
         entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 10.0F);
      }

      return false;
   }

   @Override
   public double getTransferLimit(ItemStack itemStack) {
      return 800.0;
   }
}
