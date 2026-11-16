package emt.tile.generator;

import emt.util.EMTEssentiasOutputs;
import thaumcraft.api.aspects.Aspect;

public class TileEntityPotentiaGenerator extends TileEntityBaseGenerator {
   public TileEntityPotentiaGenerator() {
      this.aspect = Aspect.ENERGY;
      this.output = EMTEssentiasOutputs.outputs.get(this.aspect.getTag());
   }
}
