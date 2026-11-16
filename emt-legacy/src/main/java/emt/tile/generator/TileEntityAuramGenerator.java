package emt.tile.generator;

import emt.util.EMTEssentiasOutputs;
import thaumcraft.api.aspects.Aspect;

public class TileEntityAuramGenerator extends TileEntityBaseGenerator {
   public TileEntityAuramGenerator() {
      this.aspect = Aspect.AURA;
      this.output = EMTEssentiasOutputs.outputs.get(this.aspect.getTag());
   }
}
