package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import java.util.ArrayList;
import java.util.StringTokenizer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;

public class NBTIntArray extends ITag {
   public final int[] data;

   NBTIntArray(String label, String name, String data) {
      super(label, name);
      ArrayList<Integer> tmp = this.parseData(data);
      this.data = new int[tmp.size()];

      for(int i = 0; i < this.data.length; ++i) {
         this.data[i] = ((Integer)tmp.get(i)).intValue();
      }

   }

   NBTIntArray(String label, String name, int[] data) {
      super(label, name);
      this.data = data;
   }

   public void write(NBTTagCompound cmp) {
      cmp.setIntArray(this.name, this.data);
   }

   public void write(NBTTagList cmp) {
      cmp.appendTag(new NBTTagIntArray(this.data));
   }

   private ArrayList<Integer> parseData(String in) {
      StringTokenizer tokens = new StringTokenizer(in, "{,}");
      ArrayList<Integer> tmp = new ArrayList();

      while(tokens.hasMoreTokens()) {
         tmp.add(Integer.getInteger(tokens.nextToken()));
      }

      return tmp;
   }

   public NBTType getType() {
      return NBTType.INT_ARRAY;
   }
}
