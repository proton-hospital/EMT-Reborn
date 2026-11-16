package emt.tile.generator;

import emt.util.EMTEssentiasOutputs;
import thaumcraft.api.aspects.Aspect;

public class TileEntityIgnisGenerator extends TileEntityBaseGenerator {
   public TileEntityIgnisGenerator() {
      this.aspect = Aspect.FIRE;
      this.output = EMTEssentiasOutputs.outputs.get(this.aspect.getTag());
   }
}
