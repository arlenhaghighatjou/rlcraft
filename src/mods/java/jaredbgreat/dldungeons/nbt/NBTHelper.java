package jaredbgreat.dldungeons.nbt;

import jaredbgreat.dldungeons.nbt.tags.ITag;
import jaredbgreat.dldungeons.nbt.tags.Tags;
import jaredbgreat.dldungeons.util.parser.Tokenizer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTHelper {
   public NBTHelper() {
   }

   public static void setNbtTag(ItemStack item, ITag tag) {
      if (tag == null) {
         System.err.println("ERROR! null tag on item " + item.getItem().getUnlocalizedName());
      } else {
         if (!item.hasTagCompound()) {
            item.setTagCompound(new NBTTagCompound());
         }

         tag.write(item.getTagCompound());
      }

   }

   public static void setNbtTag(ItemStack item, String label) {
      Tags.getTag(label).write(item.getTagCompound());
   }

   public static ITag getTagFromLabel(String label) {
      return Tags.getTag(label);
   }

   public static ITag parseNBTLine(String line) {
      Tokenizer tokens = new Tokenizer(line, " \t");
      return Tags.makeITag(line, tokens);
   }
}
