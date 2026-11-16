package emt.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityShield extends Entity {
   /**
    * Last world tick the shield focus was used per player. Under MFCL the legacy
    * EntityPlayer#isUsingItem is not reliable, so the shield lives only while the focus keeps
    * ticking (ItemShieldFocus#onUsingFocusTick / onFocusRightClick).
    */
   private static final java.util.Map<String, Long> HEARTBEAT = new java.util.concurrent.ConcurrentHashMap<String, Long>();
   private int age;

   public static void heartbeat(EntityPlayer player) {
      HEARTBEAT.put(player.getCommandSenderName(), player.worldObj.getTotalWorldTime());
   }

   public EntityPlayer owner;
   public boolean needCheck = true;

   public EntityShield(World world) {
      super(world);
      this.ignoreFrustumCheck = true;
   }

   public EntityShield(World world, EntityPlayer player) {
      super(world);
      this.setSize(4.0F, 4.0F);
      this.owner = player;
      this.dataWatcher.updateObject(11, this.owner.getDisplayName());
      this.setPosition(player.posX, player.posY, player.posZ);
      this.ignoreFrustumCheck = true;
   }

   protected void entityInit() {
      this.dataWatcher.addObject(11, "");
   }

   protected void readEntityFromNBT(NBTTagCompound nbt) {
   }

   protected void writeEntityToNBT(NBTTagCompound nbt) {
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public boolean canBePushed() {
      return true;
   }

   public boolean canBeCollidedWith() {
      return false; // never catch the player's block clicks
   }

   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y + 0.5;
      this.posZ = z;
      float f = this.width / 2.0F;
      float f1 = this.height;
      this.boundingBox
         .setBounds(
            x - (double)f,
            y - (double)this.yOffset - 2.0 + (double)this.ySize,
            z - (double)f,
            x + (double)f,
            y - (double)this.yOffset - 2.0 + (double)this.ySize + (double)f1,
            z + (double)f
         );
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.needCheck && this.owner == null) {
         this.owner = this.worldObj.getPlayerEntityByName(this.dataWatcher.getWatchableObjectString(11));
         this.needCheck = false;
      }

      if (!this.needCheck && this.owner == null) {
         this.setDead();
      } else {
         if (!this.worldObj.isRemote && this.owner != null) {
            this.setPosition(this.owner.posX, this.owner.posY, this.owner.posZ);
            Long last = HEARTBEAT.get(this.owner.getCommandSenderName());
            long now = this.worldObj.getTotalWorldTime();
            if (++this.age > 5 && (last == null || now - last > 5 || this.owner.isDead)) {
               this.setDead();
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender(float p_70070_1_) {
      return 240;
   }

   public void applyEntityCollision(Entity entity) {
      if (entity.riddenByEntity != this && entity.ridingEntity != this) {
         double ePosX = entity.posX - this.posX;
         double ePosZ = entity.posZ - this.posZ;
         entity.addVelocity(ePosX / 5.0, 0.0, ePosZ / 5.0);
      }
   }

   @SideOnly(Side.CLIENT)
   public boolean isInRangeToRender3d(double x, double y, double z) {
      return true;
   }
}
