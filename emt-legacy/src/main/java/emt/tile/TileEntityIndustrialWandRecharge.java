package emt.tile;

import cofh.api.energy.IEnergyReceiver;
import net.minecraftforge.common.util.ForgeDirection;

import emt.init.EMTBlocks;
import emt.util.EMTConfigHandler;
import ic2.api.energy.prefab.BasicSink;
import ic2.api.tile.IWrenchable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.wands.ItemWandCasting;

public class TileEntityIndustrialWandRecharge extends TileEntityEMT implements IInventory, IWrenchable, IEnergyReceiver {
   private BasicSink ic2EnergySink = new BasicSink(this, 100000, 4);
   ItemStack[] ItemStacks = new ItemStack[1];

   public int getSizeInventory() {
      return 1;
   }

   public ItemStack getStackInSlot(int i) {
      return this.ItemStacks[i];
   }

   public ItemStack decrStackSize(int i, int j) {
      if (this.ItemStacks[i] != null) {
         if (this.ItemStacks[i].stackSize <= j) {
            ItemStack itemstack = this.ItemStacks[i];
            this.ItemStacks[i] = null;
            return itemstack;
         } else {
            ItemStack itemstack1 = this.ItemStacks[i].splitStack(j);
            if (this.ItemStacks[i].stackSize == 0) {
               this.ItemStacks[i] = null;
            }

            return itemstack1;
         }
      } else {
         return null;
      }
   }

   public ItemStack getStackInSlotOnClosing(int i) {
      if (this.ItemStacks[i] != null) {
         ItemStack itemstack = this.ItemStacks[i];
         this.ItemStacks[i] = null;
         return itemstack;
      } else {
         return null;
      }
   }

   public void updateEntity() {
      this.ic2EnergySink.updateEntity();
      if (!this.worldObj.isRemote && this.getStackInSlot(0) != null) {
         ItemStack wand = this.getStackInSlot(0);
         if (wand != null && wand.getItem() instanceof ItemWandCasting) {
            ItemWandCasting wandItem = (ItemWandCasting)wand.getItem();
            if (this.ic2EnergySink.useEnergy(EMTConfigHandler.wandChargerConsumption) && wandItem.getAspectsWithRoom(wand) != null) {
               wandItem.addVis(wand, Aspect.ORDER, 1, true);
               wandItem.addVis(wand, Aspect.FIRE, 1, true);
               wandItem.addVis(wand, Aspect.ENTROPY, 1, true);
               wandItem.addVis(wand, Aspect.WATER, 1, true);
               wandItem.addVis(wand, Aspect.EARTH, 1, true);
               wandItem.addVis(wand, Aspect.AIR, 1, true);
            }
         }

         if (this.worldObj.getTotalWorldTime() % 10L == 0L) { // was every tick
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
         }
      }
   }

   public void setInventorySlotContents(int i, ItemStack itemstack) {
      this.ItemStacks[i] = itemstack;
      if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
         itemstack.stackSize = this.getInventoryStackLimit();
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound p_145839_1_) {
      super.readFromNBT(p_145839_1_);
      NBTTagList nbttaglist = p_145839_1_.getTagList("Items", 10);
      this.ItemStacks = new ItemStack[this.getSizeInventory()];

      for (int i = 0; i < nbttaglist.tagCount(); i++) {
         NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
         byte b0 = nbttagcompound1.getByte("Slot");
         if (b0 >= 0 && b0 < this.ItemStacks.length) {
            this.ItemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
         }
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound p_145841_1_) {
      super.writeToNBT(p_145841_1_);
      NBTTagList nbttaglist = new NBTTagList();

      for (int i = 0; i < this.ItemStacks.length; i++) {
         if (this.ItemStacks[i] != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setByte("Slot", (byte)i);
            this.ItemStacks[i].writeToNBT(nbttagcompound1);
            nbttaglist.appendTag(nbttagcompound1);
         }
      }

      p_145841_1_.setTag("Items", nbttaglist);
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this
         ? false
         : player.getDistanceSq((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5) <= 64.0;
   }

   public boolean isItemValidForSlot(int i, ItemStack itemstack) {
      return true;
   }

   public void invalidate() {
      this.ic2EnergySink.invalidate();
      super.invalidate();
   }

   public void onChunkUnload() {
      this.ic2EnergySink.onChunkUnload();
   }

   public String getInventoryName() {
      return "Industrial Wand Charging Station";
   }

   public boolean hasCustomInventoryName() {
      return false;
   }

   public void openInventory() {
   }

   public void closeInventory() {
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
      return 0.8F;
   }

   public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
      return new ItemStack(EMTBlocks.emtMachines, 1, 0);
   }
   // ---- Redstone Flux (MFCL maps it to Forge Energy for IC2 cables), 1 RF == 1 EU ----
   public boolean canConnectEnergy(ForgeDirection from) {
      return true;
   }

   public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
      return this.ic2EnergySink.receive(maxReceive, simulate);
   }

   public int getEnergyStored(ForgeDirection from) {
      return (int)Math.min(Integer.MAX_VALUE, this.ic2EnergySink.getEnergyStored());
   }

   public int getMaxEnergyStored(ForgeDirection from) {
      return (int)Math.min(Integer.MAX_VALUE, this.ic2EnergySink.getCapacity());
   }
}
