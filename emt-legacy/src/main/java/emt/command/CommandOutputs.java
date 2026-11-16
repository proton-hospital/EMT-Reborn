package emt.command;

import emt.util.EMTEssentiasOutputs;
import emt.util.EMTTextHelper;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import thaumcraft.api.aspects.Aspect;

public class CommandOutputs extends CommandBase {
   public String text;

   public String getCommandName() {
      return "emt_outputs";
   }

   public String getCommandUsage(ICommandSender p_71518_1_) {
      return "/emt_outputs";
   }

   public boolean canCommandSenderUseCommand(ICommandSender par1iCommandSender) {
      return true;
   }

   public List addTabCompletionOptions(ICommandSender command, String[] stringArray) {
      return null;
   }

   public int compareTo(Object object) {
      return object instanceof ICommand ? this.compareTo((ICommand)object) : 0;
   }

   public void processCommand(ICommandSender command, String[] astring) {
      this.text = EMTTextHelper.localize("gui.EMT.book.aspect.output.essentia.eu") + ": ";

      for (Aspect aspect : Aspect.aspects.values()) {
         String text2 = this.addStrings(this.text, aspect.getTag() + ": " + EMTEssentiasOutputs.outputs.get(aspect.getTag()) + "\n");
         this.text = text2;
      }

      this.addOutputsBook(EMTTextHelper.localize("gui.EMT.book.aspect.output.eu.title"), this.text, command, astring);
   }

   public void addOutputsBook(String title, String text, ICommandSender command, String[] astring) {
      ItemStack book = new ItemStack(Items.written_book);
      book.setTagInfo("author", new NBTTagString("Tombenpotter"));
      book.setTagInfo("title", new NBTTagString(title));
      NBTTagCompound nbttagcompound = book.getTagCompound();
      NBTTagList bookPages = new NBTTagList();
      bookPages.appendTag(new NBTTagString(text.substring(0, 237)));
      bookPages.appendTag(new NBTTagString(text.substring(237, 476)));
      bookPages.appendTag(new NBTTagString(text.substring(476, 709)));
      bookPages.appendTag(new NBTTagString(text.substring(709)));
      nbttagcompound.setTag("pages", bookPages);
      System.out.println(text.length());
      if (!command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()).inventory.addItemStackToInventory(book)) {
         command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()).entityDropItem(book, 0.0F);
      }
   }

   public String addStrings(String string1, String string2) {
      return string1 + string2;
   }
}
