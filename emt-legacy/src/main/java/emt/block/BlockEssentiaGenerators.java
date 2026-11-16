package emt.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emt.tile.generator.TileEntityAerGenerator;
import emt.tile.generator.TileEntityArborGenerator;
import emt.tile.generator.TileEntityAuramGenerator;
import emt.tile.generator.TileEntityBaseGenerator;
import emt.tile.generator.TileEntityIgnisGenerator;
import emt.tile.generator.TileEntityPotentiaGenerator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEssentiaGenerators extends BlockBaseContainer {
   public BlockEssentiaGenerators(String name) {
      super(name, Material.iron, soundTypeMetal, 5, 4.0F);
   }

   @SideOnly(Side.CLIENT)
   public void registerBlockIcons(IIconRegister ir) {
      super.registerBlockIcons(ir);
      this.blockIcon = ir.registerIcon("emt:machines/top");

      for (int meta = 0; meta < 5; meta++) {
         this.iconSets[meta].top = ir.registerIcon("emt:essentiagenerator/top");
         this.iconSets[meta].bottom = ir.registerIcon("emt:essentiagenerator/top");
         this.iconSets[meta].side = ir.registerIcon("emt:essentiagenerator/side");
      }

      this.iconSets[0].frontOff = ir.registerIcon("emt:essentiagenerator/potentiafront");
      this.iconSets[1].frontOff = ir.registerIcon("emt:essentiagenerator/ignisfront");
      this.iconSets[2].frontOff = ir.registerIcon("emt:essentiagenerator/auramfront");
      this.iconSets[3].frontOff = ir.registerIcon("emt:essentiagenerator/arborfront");
      this.iconSets[4].frontOff = ir.registerIcon("emt:essentiagenerator/aerfront");
   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(Item id, CreativeTabs tab, List list) {
      list.add(new ItemStack(id, 1, 0));
      list.add(new ItemStack(id, 1, 1));
      list.add(new ItemStack(id, 1, 2));
      list.add(new ItemStack(id, 1, 3));
      list.add(new ItemStack(id, 1, 4));
   }

   public TileEntity createTileEntity(World world, int meta) {
      switch (meta) {
         case 0:
            return new TileEntityPotentiaGenerator();
         case 1:
            return new TileEntityIgnisGenerator();
         case 2:
            return new TileEntityAuramGenerator();
         case 3:
            return new TileEntityArborGenerator();
         case 4:
            return new TileEntityAerGenerator();
         default:
            return super.createTileEntity(world, meta);
      }
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(World world, int x, int y, int z, Random random) {
      TileEntityBaseGenerator te = (TileEntityBaseGenerator)world.getTileEntity(x, y, z);
      if (te.isActive) {
         int color = te.aspect.getColor();
         float r = (float)(color >> 16 & 0xFF) / 255.0F;
         float g = (float)(color >> 8 & 0xFF) / 255.0F;
         float b = (float)(color & 0xFF) / 255.0F;
         EntitySmokeFX fx = new EntitySmokeFX(world, (double)((float)x + 0.5F), (double)y + 1.1, (double)((float)z + 0.5F), 0.0, 0.1, 0.0, 3.0F);
         fx.setRBGColorF(r, g, b);
         Minecraft.getMinecraft().effectRenderer.addEffect(fx);
      }
   }
}
