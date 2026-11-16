package emt.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thaumcraft.common.Thaumcraft;

public class EntityEnergyBall extends Entity implements IProjectile, IEntityAdditionalSpawnData {
   public static final int RADIUS = 8;
   public static final int MAX_TICKS_IN_TILE = 60;
   public EntityLivingBase shootingEntity;
   public boolean inTile = false;
   public int ticksAlive = 1;
   public int ticksInAir = 1;
   public double accelerationX = 0.0;
   public double accelerationY = 0.0;
   public double accelerationZ = 0.0;

   public EntityEnergyBall(World world, EntityLivingBase shootingEntity, double rotX, double rotY, double rotZ) {
      this(world, shootingEntity);
      rotX += this.rand.nextGaussian() * 0.4;
      rotY += this.rand.nextGaussian() * 0.4;
      rotZ += this.rand.nextGaussian() * 0.4;
      double len = (double)MathHelper.sqrt_double(rotX * rotX + rotY * rotY + rotZ * rotZ);
      this.accelerationX = rotX / len * 0.1;
      this.accelerationY = rotY / len * 0.1;
      this.accelerationZ = rotZ / len * 0.1;
   }

   public EntityEnergyBall(World world, EntityLivingBase shootingEntity) {
      this(world);
      this.shootingEntity = shootingEntity;
      this.setLocationAndAngles(
         shootingEntity.posX,
         shootingEntity.posY + (double)shootingEntity.getEyeHeight(),
         shootingEntity.posZ,
         shootingEntity.rotationYaw,
         shootingEntity.rotationPitch
      );
   }

   public EntityEnergyBall(World world) {
      super(world);
      this.setSize(1.0F, 1.0F);
      this.ignoreFrustumCheck = true;
      this.setPosition(this.posX, this.posY, this.posZ);
      this.yOffset = 0.0F;
      this.motionX = this.motionY = this.motionZ = 0.0;
   }

   public void writeSpawnData(ByteBuf buffer) {
      buffer.writeDouble(this.accelerationX);
      buffer.writeDouble(this.accelerationY);
      buffer.writeDouble(this.accelerationZ);
   }

   public void readSpawnData(ByteBuf buffer) {
      this.accelerationX = buffer.readDouble();
      this.accelerationY = buffer.readDouble();
      this.accelerationZ = buffer.readDouble();
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.inTile) {
         this.ticksAlive++;
         if (this.ticksAlive >= 60) {
            this.setDead();
         }

         // MFCL answers getEntitiesWithinAABBExcludingEntity with the real 1.20.1 entities;
         // the chunk entity lists of the legacy world only hold legacy entities.
         java.util.List<?> near = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(8.0, 8.0, 8.0));
         for (Object o : near) {
            if (!(o instanceof Entity) || o == this.shootingEntity) {
               continue;
            }
            Entity entity = (Entity)o;
            double width = entity.posX - this.posX;
            double height = entity.posY - this.posY;
            double length = entity.posZ - this.posZ;
            double hyp = (double)MathHelper.sqrt_double(width * width + length * length + height * height);
            if (hyp <= 8.0 && entity instanceof net.minecraft.entity.EntityLivingBase) {
               if (!this.worldObj.isRemote) {
                  entity.attackEntityFrom(DamageSource.magic, (float)(8.0 - hyp));
               } else {
                  Thaumcraft.proxy.bolt(this.worldObj, this, entity);
               }
            }
         }
      } else {
         Vec3 vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
         Vec3 vec3next = Vec3.createVectorHelper(
            this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ
         );
         MovingObjectPosition mop = this.worldObj.rayTraceBlocks(vec3, vec3next);
         this.ticksInAir++;
         if (mop != null) {
            this.inTile = true;
         }

         this.posX = this.posX + this.motionX;
         this.posY = this.posY + this.motionY;
         this.posZ = this.posZ + this.motionZ;
         float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.rotationYaw = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0 / Math.PI) + 90.0F;
         this.rotationPitch = (float)(Math.atan2((double)f1, this.motionY) * 180.0 / Math.PI) - 90.0F;

         while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
            this.prevRotationPitch -= 360.0F;
         }

         while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
         }

         while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
         }

         while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
         }

         this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
         this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
         double factor = (double)this.getMotionFactor();
         this.motionX = this.motionX + this.accelerationX;
         this.motionY = this.motionY + this.accelerationY;
         this.motionZ = this.motionZ + this.accelerationZ;
         this.motionX *= factor;
         this.motionY *= factor;
         this.motionZ *= factor;
         this.setPosition(this.posX, this.posY, this.posZ);
      }

      for (int i = 0; i < 3 && this.worldObj.isRemote; i++) {
         float rndX = (float)((double)((this.worldObj.rand.nextFloat() - 0.5F) * 2.0F * 4.0F) + this.posX);
         float rndY = (float)((double)((this.worldObj.rand.nextFloat() - 0.5F) * 2.0F * 4.0F) + this.posY);
         float rndZ = (float)((double)((this.worldObj.rand.nextFloat() - 0.5F) * 2.0F * 4.0F) + this.posZ);
         Thaumcraft.proxy.nodeBolt(this.worldObj, (float)this.posX, (float)this.posY, (float)this.posZ, rndX, rndY, rndZ);
      }
   }

   protected float getMotionFactor() {
      return 0.85F;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender(float p_70070_1_) {
      return 240;
   }

   @SideOnly(Side.CLIENT)
   public boolean isInRangeToRender3d(double x, double y, double z) {
      return true;
   }

   protected void entityInit() {
   }

   public void writeEntityToNBT(NBTTagCompound nbt) {
      nbt.setByte("inTile", (byte)(this.inTile ? 1 : 0));
      nbt.setTag("direction", this.newDoubleNBTList(new double[]{this.motionX, this.motionY, this.motionZ}));
   }

   public void readEntityFromNBT(NBTTagCompound nbt) {
      if (!this.inTile) {
         this.inTile = nbt.getByte("inTile") == 1;
      }

      if (nbt.hasKey("direction", 9)) {
         NBTTagList nbttaglist = nbt.getTagList("direction", 6);
         this.motionX = nbttaglist.func_150309_d(0);
         this.motionY = nbttaglist.func_150309_d(1);
         this.motionZ = nbttaglist.func_150309_d(2);
      } else {
         this.setDead();
      }
   }

   public void setThrowableHeading(double x, double y, double z, float p1, float p2) {
   }
}
