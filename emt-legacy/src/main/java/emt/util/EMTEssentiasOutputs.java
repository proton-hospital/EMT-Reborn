package emt.util;

import java.util.HashMap;
import thaumcraft.api.aspects.Aspect;

public class EMTEssentiasOutputs {
   public static HashMap<String, Double> outputs = new HashMap<>();

   public static void addOutput(Aspect aspect) {
      if (!aspect.isPrimal()) {
         Aspect[] parents = aspect.getComponents();
         if (EMTConfigHandler.outputCap == -1.0) {
            outputs.put(aspect.getTag(), outputs.get(parents[0].getTag()) + outputs.get(parents[1].getTag()));
         } else {
            double originalOutput = outputs.get(parents[0].getTag()) + outputs.get(parents[1].getTag());
            if (originalOutput > EMTConfigHandler.outputCap) {
               outputs.put(aspect.getTag(), EMTConfigHandler.outputCap);
            }
         }
      }
   }

   public static void addOutputs() {
      for (Aspect aspect : Aspect.aspects.values()) {
         if (!aspect.isPrimal()) {
            addOutput(aspect);
         }
      }
   }

   public static void addPrimalOutputs() {
      outputs.put(Aspect.FIRE.getTag(), EMTConfigHandler.fireOutput);
      outputs.put(Aspect.WATER.getTag(), EMTConfigHandler.waterOutput);
      outputs.put(Aspect.ORDER.getTag(), EMTConfigHandler.orderOutput);
      outputs.put(Aspect.ENTROPY.getTag(), EMTConfigHandler.entropyOutput);
      outputs.put(Aspect.EARTH.getTag(), EMTConfigHandler.earthOutput);
      outputs.put(Aspect.AIR.getTag(), EMTConfigHandler.airOutput);
   }
}
