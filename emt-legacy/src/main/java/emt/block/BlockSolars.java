package emt.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.tile.solar.air.TileEntityAirSolar;
import emt.tile.solar.air.TileEntityDoubleAirSolar;
import emt.tile.solar.air.TileEntityTripleAirSolar;
import emt.tile.solar.compressed.TileEntityCompressedSolar;
import emt.tile.solar.compressed.TileEntityDoubleCompressedSolar;
import emt.tile.solar.compressed.TileEntityTripleCompressedSolar;
import emt.tile.solar.dark.TileEntityDarkSolar;
import emt.tile.solar.dark.TileEntityDoubleDarkSolar;
import emt.tile.solar.dark.TileEntityTripleDarkSolar;
import emt.tile.solar.earth.TileEntityDoubleEarthSolar;
import emt.tile.solar.earth.TileEntityEarthSolar;
import emt.tile.solar.earth.TileEntityTripleEarthSolar;
import emt.tile.solar.fire.TileEntityDoubleFireSolar;
import emt.tile.solar.fire.TileEntityFireSolar;
import emt.tile.solar.fire.TileEntityTripleFireSolar;
import emt.tile.solar.order.TileEntityDoubleOrderSolar;
import emt.tile.solar.order.TileEntityOrderSolar;
import emt.tile.solar.order.TileEntityTripleOrderSolar;
import emt.tile.solar.water.TileEntityDoubleWaterSolar;
import emt.tile.solar.water.TileEntityTripleWaterSolar;
import emt.tile.solar.water.TileEntityWaterSolar;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockSolars extends BlockBaseContainer {
   public BlockSolars(String name, int countOfMetas, int instance) {
      super(name, Material.iron, soundTypeMetal, countOfMetas, instance, 4.0F);
   }

   @SideOnly(Side.CLIENT)
   public void registerBlockIcons(IIconRegister ir) {
      IIcon solarTop = ir.registerIcon("emt:solars/solartop");
      IIcon doubleSolarTop = ir.registerIcon("emt:solars/doublesolartop");
      IIcon tripleSolarTop = ir.registerIcon("emt:solars/triplesolartop");
      IIcon bottom = ir.registerIcon("emt:solars/bottom");
      if (this.instance == 0) {
         this.iconSets[0].top = solarTop;
         this.iconSets[1].top = doubleSolarTop;
         this.iconSets[2].top = tripleSolarTop;
         this.iconSets[3].top = solarTop;
         this.iconSets[4].top = doubleSolarTop;
         this.iconSets[5].top = tripleSolarTop;
         this.iconSets[6].top = solarTop;
         this.iconSets[7].top = doubleSolarTop;
         this.iconSets[8].top = tripleSolarTop;
         this.iconSets[9].top = solarTop;
         this.iconSets[10].top = doubleSolarTop;
         this.iconSets[11].top = tripleSolarTop;
         this.iconSets[12].top = solarTop;
         this.iconSets[13].top = doubleSolarTop;
         this.iconSets[14].top = tripleSolarTop;
         this.iconSets[15].top = solarTop;
         this.iconSets[0].bottom = bottom;
         this.iconSets[1].bottom = bottom;
         this.iconSets[2].bottom = bottom;
         this.iconSets[3].bottom = bottom;
         this.iconSets[4].bottom = bottom;
         this.iconSets[5].bottom = bottom;
         this.iconSets[6].bottom = bottom;
         this.iconSets[7].bottom = bottom;
         this.iconSets[8].bottom = bottom;
         this.iconSets[9].bottom = bottom;
         this.iconSets[10].bottom = bottom;
         this.iconSets[11].bottom = bottom;
         this.iconSets[12].bottom = bottom;
         this.iconSets[13].bottom = bottom;
         this.iconSets[14].bottom = bottom;
         this.iconSets[15].bottom = bottom;
         IIcon side = ir.registerIcon("emt:solars/side");
         IIcon waterSide = ir.registerIcon("emt:solars/water/waterside");
         IIcon darkSide = ir.registerIcon("emt:solars/dark/darkside");
         IIcon orderSide = ir.registerIcon("emt:solars/order/orderside");
         IIcon fireSide = ir.registerIcon("emt:solars/fire/fireside");
         IIcon airSide = ir.registerIcon("emt:solars/air/airside");
         this.iconSets[0].side = side;
         this.iconSets[1].side = side;
         this.iconSets[2].side = side;
         this.iconSets[3].side = waterSide;
         this.iconSets[4].side = waterSide;
         this.iconSets[5].side = waterSide;
         this.iconSets[6].side = darkSide;
         this.iconSets[7].side = darkSide;
         this.iconSets[8].side = darkSide;
         this.iconSets[9].side = orderSide;
         this.iconSets[10].side = orderSide;
         this.iconSets[11].side = orderSide;
         this.iconSets[12].side = fireSide;
         this.iconSets[13].side = fireSide;
         this.iconSets[14].side = fireSide;
         this.iconSets[15].side = airSide;
      }

      if (this.instance == 1) {
         this.iconSets[0].top = doubleSolarTop;
         this.iconSets[1].top = tripleSolarTop;
         this.iconSets[2].top = solarTop;
         this.iconSets[3].top = doubleSolarTop;
         this.iconSets[4].top = tripleSolarTop;
         this.iconSets[0].bottom = bottom;
         this.iconSets[1].bottom = bottom;
         this.iconSets[2].bottom = bottom;
         this.iconSets[3].bottom = bottom;
         this.iconSets[4].bottom = bottom;
         IIcon airSide = ir.registerIcon("emt:solars/air/airside");
         IIcon earthSide = ir.registerIcon("emt:solars/earth/earthside");
         this.iconSets[0].side = airSide;
         this.iconSets[1].side = airSide;
         this.iconSets[2].side = earthSide;
         this.iconSets[3].side = earthSide;
         this.iconSets[4].side = earthSide;
      }
   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(Item id, CreativeTabs tab, List list) {
      if (this.instance == 0) {
         for (int meta = 0; meta < 16; meta++) {
            list.add(new ItemStack(id, 1, meta));
         }
      }

      if (this.instance == 1) {
         for (int meta = 0; meta < 5; meta++) {
            list.add(new ItemStack(id, 1, meta));
         }
      }
   }

   @Override
   public IIcon getIcon(int side, int meta) {
      if (side < 1) {
         return this.iconSets[meta].bottom;
      } else {
         return side == 1 ? this.iconSets[meta].top : this.iconSets[meta].side;
      }
   }

   public TileEntity createTileEntity(World world, int meta) {
      if (this.instance == 0) {
         switch (meta) {
            case 0:
               return new TileEntityCompressedSolar();
            case 1:
               return new TileEntityDoubleCompressedSolar();
            case 2:
               return new TileEntityTripleCompressedSolar();
            case 3:
               return new TileEntityWaterSolar();
            case 4:
               return new TileEntityDoubleWaterSolar();
            case 5:
               return new TileEntityTripleWaterSolar();
            case 6:
               return new TileEntityDarkSolar();
            case 7:
               return new TileEntityDoubleDarkSolar();
            case 8:
               return new TileEntityTripleDarkSolar();
            case 9:
               return new TileEntityOrderSolar();
            case 10:
               return new TileEntityDoubleOrderSolar();
            case 11:
               return new TileEntityTripleOrderSolar();
            case 12:
               return new TileEntityFireSolar();
            case 13:
               return new TileEntityDoubleFireSolar();
            case 14:
               return new TileEntityTripleFireSolar();
            case 15:
               return new TileEntityAirSolar();
         }
      }

      if (this.instance == 1) {
         switch (meta) {
            case 0:
               return new TileEntityDoubleAirSolar();
            case 1:
               return new TileEntityTripleAirSolar();
            case 2:
               return new TileEntityEarthSolar();
            case 3:
               return new TileEntityDoubleEarthSolar();
            case 4:
               return new TileEntityTripleEarthSolar();
         }
      }

      return super.createTileEntity(world, meta);
   }

   public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
      world.removeTileEntity(x, y, z);
      super.breakBlock(world, x, y, z, block, meta);
   }
}
