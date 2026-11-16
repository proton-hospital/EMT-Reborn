package emt.item.block;

import emt.block.BlockBaseContainer;
import emt.util.EMTConfigHandler;
import emt.util.EMTTextHelper;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockSolars extends ItemBlock {
   int instance;

   public ItemBlockSolars(Block block) {
      super(block);
      this.setHasSubtypes(true);
      this.instance = ((BlockBaseContainer)block).instance;
   }

   public String getUnlocalizedName(ItemStack itemstack) {
      String name = "";
      if (this.instance == 0) {
         switch (itemstack.getItemDamage()) {
            case 0:
               name = "compressed";
               break;
            case 1:
               name = "doublecompressed";
               break;
            case 2:
               name = "triplecompressed";
               break;
            case 3:
               name = "water";
               break;
            case 4:
               name = "doublewater";
               break;
            case 5:
               name = "triplewater";
               break;
            case 6:
               name = "dark";
               break;
            case 7:
               name = "doubledark";
               break;
            case 8:
               name = "tripledark";
               break;
            case 9:
               name = "order";
               break;
            case 10:
               name = "doubleorder";
               break;
            case 11:
               name = "tripleorder";
               break;
            case 12:
               name = "fire";
               break;
            case 13:
               name = "doublefire";
               break;
            case 14:
               name = "triplefire";
               break;
            case 15:
               name = "air";
               break;
            default:
               name = "nothing";
         }
      }

      if (this.instance == 1) {
         switch (itemstack.getItemDamage()) {
            case 0:
               name = "doubleair";
               break;
            case 1:
               name = "tripleair";
               break;
            case 2:
               name = "earth";
               break;
            case 3:
               name = "doubleearth";
               break;
            case 4:
               name = "tripleearth";
               break;
            default:
               name = "nothing";
         }
      }

      return this.getUnlocalizedName() + "." + name;
   }

   public int getMetadata(int meta) {
      return meta;
   }

   public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advancedItemTooltips) {
      if (this.instance == 0) {
         switch (itemstack.getItemDamage()) {
            case 0:
               list.add(EMTConfigHandler.compressedSolarOutput + EMTTextHelper.localize("tooltip.EMT.euPerTick"));
               break;
            case 1:
               list.add(EMTConfigHandler.doubleCompressedSolarOutput + EMTTextHelper.localize("tooltip.EMT.euPerTick"));
               break;
            case 2:
               list.add(EMTConfigHandler.tripleCompressedSolarOutput + EMTTextHelper.localize("tooltip.EMT.euPerTick"));
               break;
            case 3:
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.compressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.raining")
               );
               break;
            case 4:
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.doubleCompressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.raining")
               );
               break;
            case 5:
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.tripleCompressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.raining")
               );
               break;
            case 6:
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.compressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.night")
               );
               break;
            case 7:
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.doubleCompressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.night")
               );
               break;
            case 8:
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.tripleCompressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.night")
               );
               break;
            case 9:
               list.add(
                  "§b"
                     + EMTTextHelper.localize("tooltip.EMT.normalConditions")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.compressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
               );
               list.add("§a" + EMTTextHelper.localize("tooltip.EMT.specialEffect") + ": " + "§7" + EMTTextHelper.localize("tooltip.EMT.output.triple"));
               break;
            case 10:
               list.add(
                  "§b"
                     + EMTTextHelper.localize("tooltip.EMT.normalConditions")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.doubleCompressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
               );
               list.add("§a" + EMTTextHelper.localize("tooltip.EMT.specialEffect") + ": " + "§7" + EMTTextHelper.localize("tooltip.EMT.output.triple"));
               break;
            case 11:
               list.add(
                  "§b"
                     + EMTTextHelper.localize("tooltip.EMT.normalConditions")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.tripleCompressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
               );
               list.add("§a" + EMTTextHelper.localize("tooltip.EMT.specialEffect") + ": " + "§7" + EMTTextHelper.localize("tooltip.EMT.output.triple"));
               break;
            case 12:
               list.add(
                  "§b"
                     + EMTTextHelper.localize("tooltip.EMT.normalConditions")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.compressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
               );
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTTextHelper.localize("tooltip.EMT.output.double")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.output.double.nether")
               );
               break;
            case 13:
               list.add(
                  "§b"
                     + EMTTextHelper.localize("tooltip.EMT.normalConditions")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.doubleCompressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
               );
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTTextHelper.localize("tooltip.EMT.output.double")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.output.double.nether")
               );
               break;
            case 14:
               list.add(
                  "§b"
                     + EMTTextHelper.localize("tooltip.EMT.normalConditions")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.tripleCompressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
               );
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTTextHelper.localize("tooltip.EMT.output.double")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.output.double.lava")
               );
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTTextHelper.localize("tooltip.EMT.output.double")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.output.double.nether")
               );
               break;
            case 15:
               list.add(
                  "§b"
                     + EMTTextHelper.localize("tooltip.EMT.normalConditions")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.compressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
               );
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTTextHelper.localize("tooltip.EMT.output.double.half")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.output.double.half.height")
               );
               break;
            default:
               list.add(EMTTextHelper.localize("tooltip.EMT.forget"));
         }
      }

      if (this.instance == 1) {
         switch (itemstack.getItemDamage()) {
            case 0:
               list.add(
                  "§b"
                     + EMTTextHelper.localize("tooltip.EMT.normalConditions")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.doubleCompressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
               );
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTTextHelper.localize("tooltip.EMT.output.double.half")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.output.double.half.height")
               );
               break;
            case 1:
               list.add(
                  "§b"
                     + EMTTextHelper.localize("tooltip.EMT.normalConditions")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.tripleCompressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
               );
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTTextHelper.localize("tooltip.EMT.output.double.half")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.output.double.half.height")
               );
               break;
            case 2:
               list.add(
                  "§b"
                     + EMTTextHelper.localize("tooltip.EMT.normalConditions")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.compressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
               );
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTTextHelper.localize("tooltip.EMT.output.double.half")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.output.double.half.low")
               );
               break;
            case 3:
               list.add(
                  "§b"
                     + EMTTextHelper.localize("tooltip.EMT.normalConditions")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.doubleCompressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
               );
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTTextHelper.localize("tooltip.EMT.output.double.half")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.output.double.half.low")
               );
               break;
            case 4:
               list.add(
                  "§b"
                     + EMTTextHelper.localize("tooltip.EMT.normalConditions")
                     + ": "
                     + "§7"
                     + EMTConfigHandler.tripleCompressedSolarOutput
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.euPerTick")
               );
               list.add(
                  "§a"
                     + EMTTextHelper.localize("tooltip.EMT.specialEffect")
                     + ": "
                     + "§7"
                     + EMTTextHelper.localize("tooltip.EMT.output.double.half")
                     + " "
                     + EMTTextHelper.localize("tooltip.EMT.output.double.half.low")
               );
               break;
            default:
               list.add(EMTTextHelper.localize("tooltip.EMT.forget"));
         }
      }
   }
}
