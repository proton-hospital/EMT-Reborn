package emt.tile;

import ic2.api.tile.IWrenchable;

import emt.init.EMTBlocks;
import emt.util.EMTConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.common.config.ConfigItems;

public class TileEntityEtherealMacerator extends TileEntityEMT implements ISidedInventory, IWrenchable {
   private static final int[] slots_top = new int[]{0};
   private static final int[] slots_bottom = new int[]{2, 1};
   private static final int[] slots_sides = new int[]{1};
   private ItemStack[] slots = new ItemStack[3];
   public int maceratingSpeed = EMTConfigHandler.etherealProcessorBaseSpeed;
   public int cookTime;

   public int getSizeInventory() {
      return this.slots.length;
   }

   public ItemStack getStackInSlot(int par1) {
      return this.slots[par1];
   }

   public ItemStack decrStackSize(int par1, int par2) {
      if (this.slots[par1] != null) {
         if (this.slots[par1].stackSize <= par2) {
            ItemStack itemstack = this.slots[par1];
            this.slots[par1] = null;
            return itemstack;
         } else {
            ItemStack itemstack = this.slots[par1].splitStack(par2);
            if (this.slots[par1].stackSize == 0) {
               this.slots[par1] = null;
            }

            return itemstack;
         }
      } else {
         return null;
      }
   }

   public ItemStack getStackInSlotOnClosing(int par1) {
      if (this.slots[par1] != null) {
         ItemStack itemstack = this.slots[par1];
         this.slots[par1] = null;
         return itemstack;
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
      this.slots[par1] = par2ItemStack;
      if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
         par2ItemStack.stackSize = this.getInventoryStackLimit();
      }
   }

   public String getInvName() {
      return "Ethereal Processor";
   }

   public boolean isInvNameLocalized() {
      return false;
   }

   @Override
   public void readFromNBT(NBTTagCompound tagCompound) {
      super.readFromNBT(tagCompound);
      NBTTagList nbttaglist = tagCompound.getTagList("Items", 10);
      this.slots = new ItemStack[this.getSizeInventory()];

      for (int i = 0; i < nbttaglist.tagCount(); i++) {
         NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
         byte b0 = nbttagcompound1.getByte("Slot");
         if (b0 >= 0 && b0 < this.slots.length) {
            this.slots[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
         }
      }

      this.cookTime = tagCompound.getShort("CookTime");
   }

   @Override
   public void writeToNBT(NBTTagCompound tagCompound) {
      super.writeToNBT(tagCompound);
      tagCompound.setShort("CookTime", (short)this.cookTime);
      NBTTagList nbttaglist = new NBTTagList();

      for (int i = 0; i < this.slots.length; i++) {
         if (this.slots[i] != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setByte("Slot", (byte)i);
            this.slots[i].writeToNBT(nbttagcompound1);
            nbttaglist.appendTag(nbttagcompound1);
         }
      }

      tagCompound.setTag("Items", nbttaglist);
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void updateEntity() {
      if (this.canSmelt() && !this.isOverLimit(1) && !this.isOverLimit(2) && !this.isOP()) {
         this.cookTime++;
         this.isOn = true;
         this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
         if (this.cookTime == this.maceratingSpeed) {
            this.cookTime = 0;
            this.smeltItem();
         }
      } else {
         this.cookTime = 0;
         this.isOn = false;
         this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
      }
   }

   private boolean canSmelt() {
      if (this.slots[0] == null) {
         return false;
      } else {
         ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.slots[0]);
         if (itemstack == null) {
            return false;
         } else if (this.slots[2] == null) {
            return true;
         } else if (!this.slots[2].isItemEqual(itemstack)) {
            return false;
         } else {
            int result = this.slots[2].stackSize + itemstack.stackSize * 2;
            return result <= this.getInventoryStackLimit() && result <= itemstack.getMaxStackSize();
         }
      }
   }

   public void smeltItem() {
      if (this.canSmelt()) {
         ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.slots[0]);
         if (this.slots[2] == null) {
            this.slots[2] = itemstack.copy();
            this.slots[2].stackSize *= 2;
         } else if (this.slots[2].isItemEqual(itemstack)) {
            this.slots[2].stackSize = this.slots[2].stackSize + itemstack.stackSize * 2;
         }

         this.slots[0].stackSize--;
         if (this.slots[0].stackSize <= 0) {
            this.slots[0] = null;
         }

         this.addBonus();
      }
   }

   public void addBonus() {
      if (this.canSmelt() && this.worldObj.rand.nextInt(EMTConfigHandler.etherealProcessorBonus) == 0) {
         if (this.slots[1] == null) {
            this.setInventorySlotContents(1, new ItemStack(ConfigItems.itemNugget, 1, 6));
         } else if (this.slots[1].isItemEqual(new ItemStack(ConfigItems.itemNugget, 1, 6)) && !this.isOverLimit(1) && !this.isOverLimit(2)) {
            this.slots[1] = new ItemStack(this.getStackInSlot(1).getItem(), this.slots[1].stackSize + 1, 6); // was meta 2 (wrong nugget)
         }
      }
   }

   public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
      return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this
         ? false
         : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5) <= 64.0;
   }

   public void openChest() {
   }

   public void closeChest() {
   }

   public boolean isItemValidForSlot(int slot, ItemStack stack) {
      return slot != 0 ? false : FurnaceRecipes.smelting().getSmeltingResult(stack) != null;
   }

   public int[] getAccessibleSlotsFromSide(int par1) {
      return par1 == 0 ? slots_bottom : (par1 == 1 ? slots_top : slots_sides);
   }

   public boolean canInsertItem(int par1, ItemStack par2ItemStack, int par3) {
      return this.isItemValidForSlot(par1, par2ItemStack);
   }

   public boolean canExtractItem(int par1, ItemStack par2ItemStack, int par3) {
      return true;
   }

   public boolean isOverLimit(int i) {
      if (this.slots[i] != null) {
         if (this.slots[i].stackSize >= 63) {
            return true;
         } else {
            return this.slots[i].stackSize < 63 ? false : false;
         }
      } else {
         return false;
      }
   }

   public boolean isOP() {
      int cobblestoneId = OreDictionary.getOreID("blockCobble");
      return OreDictionary.getOreID(this.slots[0]) == cobblestoneId
         || this.slots[0].getItem() == Item.getItemFromBlock(Blocks.sand)
         || this.slots[0].getItem() == Items.potato
         || this.slots[0].getItem() == Items.porkchop
         || this.slots[0].getItem() == Items.beef
         || this.slots[0].getItem() == Items.chicken
         || this.slots[0].getItem() == Items.fish
         || emt.compat.IC2Parts.ing("coalDust").matches(this.slots[0])
         || OreDictionary.getOreName(OreDictionary.getOreID(this.slots[0])).toLowerCase().contains("dust")
         || this.slots[0].getItem().getUnlocalizedName(this.slots[0]).toLowerCase().contains("armour")
         || this.slots[0].getItem().getUnlocalizedName(this.slots[0]).toLowerCase().contains("armor")
         || this.slots[0].getItem().getUnlocalizedName(this.slots[0]).toLowerCase().contains("helm")
         || this.slots[0].getItem().getUnlocalizedName(this.slots[0]).toLowerCase().contains("legging")
         || this.slots[0].getItem().getUnlocalizedName(this.slots[0]).toLowerCase().contains("boot")
         || this.slots[0].getItem().getUnlocalizedName(this.slots[0]).toLowerCase().contains("chestp")
         || this.slots[0].getItem().getUnlocalizedName(this.slots[0]).toLowerCase().contains("pick")
         || this.slots[0].getItem().getUnlocalizedName(this.slots[0]).toLowerCase().contains("shovel")
         || this.slots[0].getItem().getUnlocalizedName(this.slots[0]).toLowerCase().contains("axe")
         || this.slots[0].getItem().getUnlocalizedName(this.slots[0]).toLowerCase().contains("pick");
   }

   public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
      return false;
   }

   public short getFacing() {
      return 0;
   }

   public void setFacing(short facing) {
   }

   public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
      return true;
   }

   public float getWrenchDropRate() {
      return 1.0F;
   }

   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(EMTBlocks.emtMachines, 1, 1);
   }

   public String getInventoryName() {
      return "Ethereal Macerator";
   }

   public boolean hasCustomInventoryName() {
      return false;
   }

   public void openInventory() {
   }

   public void closeInventory() {
   }
}
