package emt.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.research.ResearchPage.PageType;

public class EMTResearchItem extends ResearchItem {
   public EMTResearchItem(String par1, String par2) {
      super(par1, "EMT");
   }

   public EMTResearchItem(String researchName, AspectList tags, int x, int y, int z, ItemStack icon) {
      super(researchName, "EMT", tags, x, y, z, icon);
   }

   public EMTResearchItem(String researchName, int x, int y, int z, ItemStack icon) {
      super(researchName, "EMT", new AspectList(), x, y, z, icon);
   }

   public EMTResearchItem(String researchName, AspectList tags, int x, int y, int z, ResourceLocation icon) {
      super(researchName, "EMT", tags, x, y, z, icon);
   }

   public EMTResearchItem(String researchName, int x, int y, int z, ResourceLocation icon) {
      super(researchName, "EMT", new AspectList(), x, y, z, icon);
   }

   @SideOnly(Side.CLIENT)
   public String getName() {
      return EMTTextHelper.localize("emt.rsname." + this.key);
   }

   @SideOnly(Side.CLIENT)
   public String getText() {
      return "[EMT] " + EMTTextHelper.localize("emt.tag." + this.key);
   }

   public ResearchItem setPages(ResearchPage... pages) {
      for (ResearchPage page : pages) {
         if (page.type == PageType.TEXT) {
            page.text = "emt.text." + this.key + (page.text.equals("") ? "" : "." + page.text);
         }

         if (page.type == PageType.INFUSION_CRAFTING) {
            if (this.parentsHidden != null && this.parentsHidden.length != 0) {
               String[] newParents = new String[this.parentsHidden.length + 1];
               newParents[0] = "INFUSION";

               for (int i2 = 0; i2 < this.parentsHidden.length; i2++) {
                  newParents[i2 + 1] = this.parentsHidden[i2];
               }

               this.parentsHidden = newParents;
            } else {
               this.parentsHidden = new String[]{"INFUSION"};
            }
         }
      }

      return super.setPages(pages);
   }
}
