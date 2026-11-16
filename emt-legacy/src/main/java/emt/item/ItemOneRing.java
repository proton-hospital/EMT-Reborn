package emt.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;

public class ItemOneRing extends ItemBase implements IBauble {
   public IIcon[] icon = new IIcon[16];
   public Random random = new Random();

   public ItemOneRing() {
      super("bauble");
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
      this.setMaxStackSize(1);
   }

   public String getUnlocalizedName(ItemStack itemstack) {
      String name = "";
      switch (itemstack.getItemDamage()) {
         case 0:
            name = "oneRing";
            break;
         default:
            name = "nothing";
      }

      return "item.EMT.bauble." + name;
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister ri) {
      this.icon[0] = ri.registerIcon("emt:onering");
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int meta) {
      return this.icon[meta];
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list) {
      list.add(new ItemStack(this, 1, 0));
   }

   public BaubleType getBaubleType(ItemStack stack) {
      return stack.getItemDamage() <= 0 ? BaubleType.RING : null;
   }

   public void onWornTick(ItemStack stack, EntityLivingBase player) {
      if (!player.isInvisible()) {
         player.setInvisible(true);
      }

      NBTTagCompound tag = new NBTTagCompound();
      ((EntityPlayer)player).writeToNBT(tag);
      NBTTagCompound forgeTag = tag.getCompoundTag("ForgeData");
      int corruption = 0;
      if (forgeTag.hasKey("MindCorruption")) {
         corruption = forgeTag.getInteger("MindCorruption");
      } else {
         forgeTag.setInteger("MindCorruption", 0);
      }

      ((EntityPlayer)player).capabilities.disableDamage = true;
      if (!player.worldObj.isRemote) {
         if (corruption <= 0) {
            ((EntityPlayer)player)
               .addChatMessage(
                  new ChatComponentText(
                     "§5You have worn the Ring. Your soul has now been forever §5tainted. §4§oBeware of wearing the ring. The tainting will only §4§oincrease, and strange things will start happening."
                  )
               );
         } else if (corruption > 6000 && corruption < 24000 && this.random.nextInt(2000) == 0) {
            player.addPotionEffect(new PotionEffect(Potion.blindness.id, 500, 2, false));
         } else if (corruption >= 6000 && corruption < 24000 && this.random.nextInt(2000) == 0) {
            player.addPotionEffect(new PotionEffect(Potion.confusion.id, 500, 2, false));
         } else if (corruption >= 24000 && corruption < 72000 && this.random.nextInt(2000) == 0) {
            ((EntityPlayer)player).capabilities.disableDamage = false;
            player.attackEntityFrom(DamageSource.magic, 5.0F);
         } else if (corruption >= 72000 && corruption < 120000 && this.random.nextInt(4000) == 0) {
            ((EntityPlayer)player).capabilities.disableDamage = false;
            player.motionY += 2.0;
         } else if (corruption >= 120000 && this.random.nextInt(10000) == 0) {
            ((EntityPlayer)player).capabilities.disableDamage = false;
            player.addPotionEffect(new PotionEffect(Potion.wither.id, 5000, 4, false));
         } else if (corruption + 100 >= Integer.MAX_VALUE) {
            player.isDead = true;
         }
      }

      forgeTag.setInteger("MindCorruption", ++corruption);
      tag.setTag("ForgeData", forgeTag);
      ((EntityPlayer)player).readFromNBT(tag);
   }

   public void onEquipped(ItemStack stack, EntityLivingBase player) {
      NBTTagCompound tag = new NBTTagCompound();
      ((EntityPlayer)player).writeToNBT(tag);
      tag.getCompoundTag("ForgeData").setInteger("MindCorruption", 0);
      ((EntityPlayer)player).readFromNBT(tag);
   }

   public void onUnequipped(ItemStack stack, EntityLivingBase player) {
      player.setInvisible(false);
      if (!((EntityPlayer)player).capabilities.isCreativeMode) {
         ((EntityPlayer)player).capabilities.disableDamage = false;
      }

      NBTTagCompound tag = new NBTTagCompound();
      ((EntityPlayer)player).writeToNBT(tag);
      tag.removeTag("ForgeData");
      ((EntityPlayer)player).readFromNBT(tag);
      player.addPotionEffect(new PotionEffect(Potion.confusion.id, 500, 2, false));
   }

   public boolean canEquip(ItemStack stack, EntityLivingBase player) {
      return stack.getItemDamage() == 0 && player instanceof EntityPlayer;
   }

   public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
      NBTTagCompound tag = new NBTTagCompound();
      ((EntityPlayer)player).writeToNBT(tag);
      return tag.getCompoundTag("ForgeData").getInteger("MindCorruption") > 600;
   }
}
