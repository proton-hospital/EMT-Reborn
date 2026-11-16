package emt.tile.generator;

import emt.util.EMTEssentiasOutputs;
import thaumcraft.api.aspects.Aspect;

public class TileEntityArborGenerator extends TileEntityBaseGenerator {
   public TileEntityArborGenerator() {
      this.aspect = Aspect.TREE;
      this.output = EMTEssentiasOutputs.outputs.get(this.aspect.getTag());
   }
}
