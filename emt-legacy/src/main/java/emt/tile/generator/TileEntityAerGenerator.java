package emt.tile.generator;

import emt.util.EMTEssentiasOutputs;
import thaumcraft.api.aspects.Aspect;

public class TileEntityAerGenerator extends TileEntityBaseGenerator {
   public TileEntityAerGenerator() {
      this.aspect = Aspect.AIR;
      this.output = EMTEssentiasOutputs.outputs.get(this.aspect.getTag());
   }
}
