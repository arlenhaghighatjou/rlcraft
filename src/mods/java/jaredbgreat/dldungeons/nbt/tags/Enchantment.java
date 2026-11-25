package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Enchantment extends ITag {
   private short id;
   private short lvl;

   protected Enchantment(String label, String name, String data) {
      super(label, name);
      this.id = Short.parseShort(name);
      this.lvl = Short.parseShort(data);
   }

   public void write(NBTTagCompound cmp) {
      if (!cmp.hasKey("ench", 9)) {
         cmp.setTag("ench", new NBTTagList());
      }

      NBTTagList list = cmp.getTagList("ench", 10);
      NBTTagCompound sub = new NBTTagCompound();
      sub.setShort("id", this.id);
      sub.setShort("lvl", this.lvl);
      list.appendTag(sub);
   }

   public void write(NBTTagList in) {
      NBTTagCompound sub = new NBTTagCompound();
      sub.setShort("id", this.id);
      sub.setShort("lvl", this.lvl);
      in.appendTag(sub);
   }

   public NBTType getType() {
      return NBTType.COMPOUND;
   }
}
