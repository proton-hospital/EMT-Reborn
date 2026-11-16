package emt.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityEMT extends TileEntity {
   public int facing;
   public boolean isOn;

   public void readFromNBT(NBTTagCompound tagCompound) {
      super.readFromNBT(tagCompound);
      this.facing = tagCompound.getInteger("facing");
      this.isOn = tagCompound.getBoolean("isOn");
   }

   public void writeToNBT(NBTTagCompound tagCompound) {
      super.writeToNBT(tagCompound);
      tagCompound.setInteger("facing", this.facing);
      tagCompound.setBoolean("isOn", this.isOn);
   }

   public final Packet getDescriptionPacket() {
      NBTTagCompound nbt = new NBTTagCompound();
      this.writeToNBT(nbt);
      return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
   }

   public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
      NBTTagCompound nbt = pkt.func_148857_g();
      this.readFromNBT(nbt);
   }

   public void markDirty() {
      super.markDirty();
      if (!this.worldObj.isRemote) {
         this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
      }
   }
}
