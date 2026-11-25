package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import java.util.ArrayList;
import java.util.StringTokenizer;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTByteArray extends ITag {
   public final byte[] data;

   NBTByteArray(String label, String name, String data) {
      super(label, name);
      ArrayList<Byte> tmp = this.parseData(data);
      this.data = new byte[tmp.size()];

      for(int i = 0; i < this.data.length; ++i) {
         this.data[i] = ((Byte)tmp.get(i)).byteValue();
      }

   }

   NBTByteArray(String label, String name, byte[] data) {
      super(label, name);
      this.data = data;
   }

   public void write(NBTTagCompound cmp) {
      cmp.setByteArray(this.name, this.data);
   }

   public void write(NBTTagList cmp) {
      cmp.appendTag(new NBTTagByteArray(this.data));
   }

   private ArrayList<Byte> parseData(String in) {
      StringTokenizer tokens = new StringTokenizer(in, "{,}");
      ArrayList<Byte> tmp = new ArrayList();

      while(tokens.hasMoreTokens()) {
         tmp.add(Byte.valueOf(Byte.parseByte(tokens.nextToken())));
      }

      return tmp;
   }

   public NBTType getType() {
      return NBTType.BYTE_ARRAY;
   }
}
