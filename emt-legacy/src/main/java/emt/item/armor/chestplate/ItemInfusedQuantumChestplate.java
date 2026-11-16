package emt.item.armor.chestplate;

import emt.compat.EMTKeyboard;
import emt.compat.EMTPlatform;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.EMT;
import emt.client.model.ModelSpecialArmor;
import emt.init.EMTItems;
import emt.util.EMTConfigHandler;
import ic2.api.item.ElectricItem;
import emt.compat.IC2Parts;
import emt.util.EMTElectricHelper;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;

/**
 * Infused Quantum Chestplate. In EMT 1.2.6.2 it extended the internal IC2 ItemArmorElectric; the
 * IC2 quantum suit behaviour (ISpecialArmor + electric item) is reproduced here with the same numbers.
 */
public class ItemInfusedQuantumChestplate extends ItemArmor implements IElectricItem, ISpecialArmor, IMetalArmor {
   public final double maxCharge = 2000000.0;
   public final double transferLimit = 12000.0;
   public final int tier = 4;
   public final byte NONE = 0;
   public final byte JETPACK = 1;
   public final byte THAUMIUM = 2;
   public final byte NANO = 3;
   public final byte QUANTUM = 4;
   private IIcon iconNONE;
   private IIcon iconJETPACK;
   private IIcon iconTHAUMIUM;
   private IIcon iconNANO;
   private IIcon iconQUANTUM;
   protected static ArrayList<Integer> potionRemovalCost = new ArrayList<>();

   public ItemInfusedQuantumChestplate() {
      super(ArmorMaterial.DIAMOND, 0, 1);
      MinecraftForge.EVENT_BUS.register(this);
      this.setCreativeTab(EMT.TAB);
      this.setMaxStackSize(1);
      this.setMaxDamage(0);
   }

   // ---- IC2 electric item ----
   public boolean canProvideEnergy(ItemStack stack) { return false; }
   public Item getChargedItem(ItemStack stack) { return this; }
   public Item getEmptyItem(ItemStack stack) { return this; }
   public double getMaxCharge(ItemStack stack) { return this.maxCharge; }
   public int getTier(ItemStack stack) { return this.tier; }
   public double getTransferLimit(ItemStack stack) { return this.transferLimit; }
   public boolean isMetalArmor(ItemStack stack, EntityPlayer player) { return true; }
   public boolean isRepairable() { return false; }

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs tab, List list) {
      EMTElectricHelper.addChargedAndEmpty(this, list);
   }

   public boolean showDurabilityBar(ItemStack stack) { return true; }

   public double getDurabilityForDisplay(ItemStack stack) {
      return 1.0 - ElectricItem.manager.getCharge(stack) / this.maxCharge;
   }

   // ---- ISpecialArmor, as IC2 ItemArmorElectric (chestplate base absorption 0.4) ----
   private double getBaseAbsorptionRatio() { return 0.4; }

   public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
      if (source.isUnblockable()) {
         return new ArmorProperties(0, 0.0, 0);
      }
      double absorptionRatio = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
      int energyPerDamage = this.getEnergyPerDamage();
      int damageLimit = (int)(energyPerDamage > 0 ? 25.0 * ElectricItem.manager.getCharge(armor) / energyPerDamage : 0.0);
      return new ArmorProperties(0, absorptionRatio, damageLimit);
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      return ElectricItem.manager.getCharge(armor) >= this.getEnergyPerDamage()
         ? (int)Math.round(20.0 * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio())
         : 0;
   }

   public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
      ElectricItem.manager.discharge(stack, (double)(damage * this.getEnergyPerDamage()), Integer.MAX_VALUE, true, false, false);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister iconRegister) {
      this.iconNONE = iconRegister.registerIcon("emt:armor/armor_quantum_chest");
      this.iconJETPACK = iconRegister.registerIcon("emt:armor/armor_quantum_chest_jetpack");
      this.iconTHAUMIUM = iconRegister.registerIcon("emt:armor/armor_quantum_chest_wing_t");
      this.iconNANO = iconRegister.registerIcon("emt:armor/armor_quantum_chest_wing_n");
      this.iconQUANTUM = iconRegister.registerIcon("emt:armor/armor_quantum_chest_wing_q");
      this.itemIcon = this.iconNONE;
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int meta) {
      return this.iconNONE; // MFCL builds the inventory model from this
   }

   public String getUnlocalizedName(ItemStack itemstack) {
      return "item.EMT.chestplates.infusedQuantum";
   }

   public String getItemStackDisplayName(ItemStack stack) {
      return StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".name").trim();
   }

   @SubscribeEvent
   public void onEntityLivingFallEvent(LivingFallEvent event) {
      if (EMTPlatform.isSimulating(event.entity) && event.entity instanceof EntityLivingBase) {
         EntityLivingBase entity = (EntityLivingBase)event.entity;
         ItemStack armor = entity.getEquipmentInSlot(1);
         if (armor != null && armor.getItem() == this) {
            int fallDamage = Math.max((int)event.distance - 10, 0);
            double energyCost = (double)(this.getEnergyPerDamage() * fallDamage);
            if (energyCost <= ElectricItem.manager.getCharge(armor)) {
               ElectricItem.manager.discharge(armor, energyCost, Integer.MAX_VALUE, true, false, false);
               event.setCanceled(true);
            }
         }
      }
   }

   public double getDamageAbsorptionRatio() {
      return 1.1;
   }

   public int getEnergyPerDamage() {
      return 20000;
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.rare;
   }

   public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
      NBTTagCompound nbt = EMTPlatform.getOrCreateNbtData(itemStack);
      byte toggleTimer = nbt.getByte("toggleTimer");
      boolean ret = false;
      boolean hoverMode = nbt.getBoolean("hoverMode");
      boolean jetpackUsed = false;
      byte wing = nbt.getByte("wing");
      if (!world.isRemote) {
         for (PotionEffect effect : new ArrayList<PotionEffect>((java.util.Collection<PotionEffect>) player.getActivePotionEffects())) {
            if (potionRemovalCost.contains(effect.getPotionID())) {
               EMTPlatform.removePotion(player, effect.getPotionID());
            }
         }

         ItemStack currentStack = player.inventory.getCurrentItem();
         Item currentItem = currentStack == null ? null : currentStack.getItem();
         if (nbt.hasKey("useother")) {
            String useother = nbt.getString("useother");
            if (useother.equals("Jetpack")) {
               nbt.setByte("wing", (byte)1);
            }

            if (useother.equals("TW")) {
               nbt.setByte("wing", (byte)2);
            }

            if (useother.equals("NW")) {
               nbt.setByte("wing", (byte)3);
            }

            if (useother.equals("QW")) {
               nbt.setByte("wing", (byte)4);
            }

            wing = nbt.getByte("wing");
            nbt.removeTag("useother");
         }

         if (wing == 0) {
            nbt.setByte("wing", (byte)0);
         }

         // hover toggle, attaching and removing the jetpack / wings are handled by emtbridge (ChestParts)
      }

      // jetpack thrust: emtbridge JetpackFlight (client) + ArmorPower (energy), see useJetpack for the original

      if (wing == 2) {
         this.useWings(player, itemStack, world, 0.15F, 0.7F, 0.5F, 0, false);
      }

      if (wing == 3) {
         this.useWings(player, itemStack, world, 0.25F, 0.6F, 0.3F, 5, true);
      }

      if (wing == 4) {
         this.useWings(player, itemStack, world, 0.33F, 0.5F, 0.2F, 7, true);
      }

      if (EMTPlatform.isSimulating(world) && toggleTimer > 0) {
         nbt.setByte("toggleTimer", --toggleTimer);
      }

      player.extinguish();
      // (1.2.6.2 called inventoryContainer.detectAndSendChanges() here; MFCL's armour-tick player has no container)
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconIndex(ItemStack stack) {
      try {
         byte wing = stack.stackTagCompound.getByte("wing");
         switch (wing) {
            case 1:
               return this.iconJETPACK;
            case 2:
               return this.iconTHAUMIUM;
            case 3:
               return this.iconNANO;
            case 4:
               return this.iconQUANTUM;
            default:
               return this.iconNONE;
         }
      } catch (Exception var3) {
         return this.iconNONE;
      }
   }

   public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
      try {
         byte wing = stack.stackTagCompound.getByte("wing");
         switch (wing) {
            case 1:
               return this.iconJETPACK;
            case 2:
               return this.iconTHAUMIUM;
            case 3:
               return this.iconNANO;
            case 4:
               return this.iconQUANTUM;
            default:
               return this.iconNONE;
         }
      } catch (Exception var7) {
         return this.iconNONE;
      }
   }

   public int getItemEnchantability() {
      return !EMTConfigHandler.enchanting ? 0 : 4;
   }

   @SideOnly(Side.CLIENT)
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      if (entity == null || stack.stackTagCompound == null) {
         return "emt:textures/models/quantum.png"; // jetpack / wings are drawn by emtbridge
      }
      byte wing = stack.stackTagCompound.getByte("wing");
      switch (wing) {
         case 1:
            return "emt:textures/models/quantum_jetpack.png";
         case 2:
            return "emt:textures/models/quantum_wings_t.png";
         case 3:
            return "emt:textures/models/quantum_wings_n.png";
         case 4:
            return "emt:textures/models/quantum_wings_q.png";
         default:
            return "emt:textures/models/quantum.png";
      }
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, int armorSlot) {
      try {
         if (entity instanceof EntityPlayer) {
            byte wing = stack.stackTagCompound.getByte("wing");
            if (wing == 1) {
               return new ModelSpecialArmor(1.0F, 1);
            }

            if (wing != 0) {
               ModelSpecialArmor mbm = new ModelSpecialArmor(1.0F, 2);
               mbm.isJumping = stack.stackTagCompound.getBoolean("isJumping");
               return mbm;
            }
         }
      } catch (NullPointerException var6) {
         new ModelSpecialArmor(1.0F, 0);
      }

      return new ModelSpecialArmor(1.0F, 0);
   }

   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
      try {
         list.add(StatCollector.translateToLocal("ic2.item.tooltip.PowerTier") + " " + this.tier);
      } catch (NullPointerException var6) {
      }
   }

   public boolean useJetpack(EntityPlayer player, boolean hoverMode, ItemStack stack) {
      int jetpackMaxCharge = 30000;
      float power = 1.0F;
      float dropPercentage = 0.05F;
      if (ElectricItem.manager.getCharge(stack) <= 0.0) {
         return false;
      } else {
         if ((float)ElectricItem.manager.getCharge(stack) / (float)jetpackMaxCharge <= dropPercentage) {
            power = (float)((double)power * (ElectricItem.manager.getCharge(stack) / (double)((float)jetpackMaxCharge)) * (double)dropPercentage);
         }

         if (EMTKeyboard.isForwardKeyDown(player)) {
            float retruster = 3.5F;
            if (hoverMode) {
               retruster = 0.5F;
            }

            float forwardpower = power * retruster * 2.0F;
            if (forwardpower > 0.0F) {
               player.moveFlying(0.0F, 0.4F * forwardpower, 0.02F);
            }
         }

         int worldHeight = EMTPlatform.getWorldHeight(player.worldObj);
         double y = player.posY;
         if (y > (double)(worldHeight - 25)) {
            if (y > (double)worldHeight) {
               y = (double)worldHeight;
            }

            power = (float)((double)power * (((double)worldHeight - y) / 25.0));
         }

         double prevmotion = player.motionY;
         player.motionY = Math.min(player.motionY + (double)(power * 0.2F), 0.6F);
         if (hoverMode) {
            float maxHoverY = -0.025F;
            if (EMTKeyboard.isSneakKeyDown(player)) {
               maxHoverY = -0.1F;
            }

            if (EMTKeyboard.isJumpKeyDown(player)) {
               maxHoverY = 0.1F;
            }

            if (player.motionY > (double)maxHoverY) {
               player.motionY = (double)maxHoverY;
               if (prevmotion > player.motionY) {
                  player.motionY = prevmotion;
               }
            }
         }

         double consume = 8.0;
         if (hoverMode) {
            consume = 10.0;
         }

         ElectricItem.manager.use(stack, consume, player);
         player.fallDistance = 0.0F;
         player.distanceWalkedModified = 0.0F;
         return true;
      }
   }

   void useWings(EntityPlayer player, ItemStack stack, World world, float motionY, float motionXZ, float f1, int amount, boolean isElectric) {
      NBTTagCompound nbt = EMTPlatform.getOrCreateNbtData(stack);
      boolean isJumping = EMTKeyboard.isJumpKeyDown(player);
      nbt.setBoolean("isJumping", isJumping);
      if (isJumping) {
         byte f = nbt.getByte("f");
         nbt.setBoolean("isHolding", true);
         nbt.setByte("f", (byte)(f + 1));
         if (f > 7) {
            nbt.setByte("f", (byte)7);
         }
      } else if (nbt.getBoolean("isHolding")) {
         byte f = nbt.getByte("f");
         nbt.setBoolean("isHolding", false);
         player.motionY = (double)(motionY * (float)f);
         if (isElectric) {
            ElectricItem.manager.use(stack, (double)(motionY * (float)f * 10.0F * (float)amount), player);
         }

         if (player.motionX < 0.5 && player.motionZ < 0.5 && player.motionX > -0.5 && player.motionZ > -0.5) {
            player.motionX /= (double)motionXZ;
            player.motionZ /= (double)motionXZ;
         }

         world.playSoundEffect(player.posX, player.posY, player.posZ, "mob.ghast.fireball", 1.0F, 1.0F);

         for (int i = 0; i < 4; i++) {
            world.spawnParticle(
               "cloud",
               player.posX - 1.0 + (double)world.rand.nextInt(100) / 50.0,
               player.posY - 1.0,
               player.posZ - 1.0 + (double)world.rand.nextInt(100) / 50.0,
               0.0,
               -0.5,
               0.0
            );
         }

         nbt.setByte("f", (byte)0);
      }

      if (isJumping && !player.onGround && player.motionY < 0.0) {
         player.motionY *= (double)f1;
      }

      if (player.isInWater() && !player.capabilities.isCreativeMode) {
         player.motionY += -0.05;
      }

      if (EMTConfigHandler.impactOfRain) {
         int playerX = MathHelper.floor_double(player.posX);
         int playerY = MathHelper.floor_double(player.posY);
         int playerZ = MathHelper.floor_double(player.posZ);
         if (emt.compat.EMTPlatform.biome(world, playerX, playerZ).canSpawnLightningBolt()
            && world.canBlockSeeTheSky(playerX, playerY, playerZ)
            && world.isRaining()
            && !player.capabilities.isCreativeMode) {
            player.motionY += -0.05;
         }
      }

      if (player.isSneaking() && !player.onGround && player.motionY < 0.0) {
         player.motionY *= 0.6;
      }

      if (player.fallDistance > 0.0F) {
         player.fallDistance = 0.0F;
      }
   }
}
