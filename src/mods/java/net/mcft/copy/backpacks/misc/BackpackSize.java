package net.mcft.copy.backpacks.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

public class BackpackSize implements INBTSerializable<NBTTagByteArray> {
   private static final Pattern PATTERN = Pattern.compile("^\\[([1-9]\\d?)x([1-9])\\]$");
   public static final BackpackSize MIN = new BackpackSize(1, 1);
   public static final BackpackSize MAX = new BackpackSize(17, 6);
   private int _columns = -1;
   private int _rows = -1;

   public int getColumns() {
      return this._columns;
   }

   public int getRows() {
      return this._rows;
   }

   public BackpackSize() {
   }

   public BackpackSize(int columns, int rows) {
      this._columns = columns;
      this._rows = rows;
   }

   public static BackpackSize parse(String str) {
      Matcher matcher = PATTERN.matcher(str);
      if (!matcher.matches()) {
         throw new IllegalArgumentException("Invalid backpack size value '" + str + "'");
      } else {
         int columns = Integer.parseInt(matcher.group(1));
         int rows = Integer.parseInt(matcher.group(2));
         if (columns <= MAX.getColumns() && rows <= MAX.getRows()) {
            return new BackpackSize(columns, rows);
         } else {
            throw new IllegalArgumentException("Backpack size value " + str + " over maximum (" + MAX + ")");
         }
      }
   }

   public static BackpackSize parse(NBTBase tag) {
      int columns;
      int rows;
      if (tag instanceof NBTTagByteArray) {
         NBTTagByteArray array = (NBTTagByteArray)tag;
         columns = array.getByteArray()[0];
         rows = array.getByteArray()[1];
      } else if (tag instanceof NBTTagIntArray) {
         NBTTagIntArray array = (NBTTagIntArray)tag;
         columns = array.getIntArray()[0];
         rows = array.getIntArray()[1];
      } else {
         if (!(tag instanceof NBTTagList)) {
            throw new RuntimeException("Invalid tag type " + NBTBase.NBT_TYPES[tag.getId()]);
         }

         NBTTagList list = (NBTTagList)tag;
         columns = ((NBTPrimitive)list.get(0)).getInt();
         rows = ((NBTPrimitive)list.get(1)).getInt();
      }

      return new BackpackSize(columns, rows);
   }

   public NBTTagByteArray serializeNBT() {
      return new NBTTagByteArray(new byte[]{(byte)this._columns, (byte)this._rows});
   }

   public void deserializeNBT(NBTTagByteArray nbt) {
      this._columns = nbt.getByteArray()[0];
      this._rows = nbt.getByteArray()[1];
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof BackpackSize)) {
         return false;
      } else if (obj == this) {
         return true;
      } else {
         BackpackSize size = (BackpackSize)obj;
         return size._columns == this._columns && size._rows == this._rows;
      }
   }

   public String toString() {
      return "[" + this._columns + "x" + this._rows + "]";
   }
}
