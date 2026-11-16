package emt.item.focus;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;

public abstract class ItemBaseFocus extends ItemFocusBasic {
   public IIcon ornament;
   String textureName = "";

   public ItemBaseFocus(String unlocName) {
      this.setUnlocalizedName("EMT.focus." + unlocName);
      this.setCreativeTab(EMT.TAB);
      this.setMaxDamage(1);
      this.setNoRepair();
      this.setMaxStackSize(1);
      this.textureName = unlocName;
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister ir) {
      this.icon = ir.registerIcon("emt:focus_" + this.textureName);
   }

   boolean hasDepth() {
      return false;
   }

   public int getFocusColor(ItemStack stack) {
      return 0;
   }

   public boolean isItemTool(ItemStack par1ItemStack) {
      return true;
   }

   public EnumRarity getRarity(ItemStack itemstack) {
      return EnumRarity.rare;
   }

   public IIcon getOrnament(ItemStack focusstack) {
      return this.ornament;
   }

   public AspectList getVisCost(ItemStack focusstack) {
      return new AspectList();
   }

   public boolean isUseItem(ItemStack stack) {
      return this.isVisCostPerTick(stack);
   }

   public boolean isVisCostPerTick() {
      return false;
   }

   public ItemStack onFocusRightClick(
      ItemStack paramItemStack, World paramWorld, EntityPlayer paramEntityPlayer, MovingObjectPosition paramMovingObjectPosition
   ) {
      if (this.isUseItem(paramItemStack)) {
         paramEntityPlayer.setItemInUse(paramItemStack, Integer.MAX_VALUE);
      }

      return paramItemStack;
   }

   public void onUsingFocusTick(ItemStack itemstack, EntityPlayer player, int count) {
   }

   public void onPlayerStoppedUsingFocus(ItemStack itemstack, World world, EntityPlayer player, int count) {
   }

   public String getSortingHelper(ItemStack itemstack) {
      return "00";
   }

   public boolean onFocusBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
      return false;
   }

   public int getItemEnchantability() {
      return 5;
   }

   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
      AspectList cost = this.getVisCost(stack);
      if (cost != null && cost.size() > 0) {
         list.add(StatCollector.translateToLocal(this.isVisCostPerTick() ? "item.Focus.cost2" : "item.Focus.cost1"));

         for (Aspect aspect : cost.getAspectsSorted()) {
            float amount = (float)cost.getAmount(aspect) / 100.0F;
            list.add(" §" + aspect.getChatcolor() + aspect.getName() + '§' + "r x " + amount);
         }
      }
   }
}
