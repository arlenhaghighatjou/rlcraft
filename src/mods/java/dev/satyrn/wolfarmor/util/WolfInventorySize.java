package dev.satyrn.wolfarmor.util;

import net.lax1dude.eaglercraft.v1_8.HString;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraftforge.common.util.INBTSerializable;

public final class WolfInventorySize implements INBTSerializable<NBTTagByteArray> {
   private static final int ROW = 1;
   private static final int COL = 0;
   private static final int MIN = 0;
   private static final int MAX = 1;
   private static final int MATCH_ROW = 2;
   private static final int MATCH_COL = 1;
   private static final byte[][] DIMS = new byte[][]{{1, 5}, {1, 3}};
   byte rows;
   byte columns;

   public WolfInventorySize() {
      this.rows = DIMS[1][0];
      this.columns = DIMS[0][0];
   }

   public WolfInventorySize(byte columns, byte rows) {
      this.rows = DIMS[1][0];
      this.columns = DIMS[0][0];
      this.setColumns(columns);
      this.setRows(rows);
   }

   public int getColumns() {
      return this.columns;
   }

   public void setColumns(byte value) {
      if (value >= DIMS[0][0] && value <= DIMS[0][1]) {
         this.columns = value;
      } else {
         throw new IllegalArgumentException(HString.format("The desired column count '%d' was outside the bounds [%d..%d]", value, DIMS[0][0], DIMS[0][1]));
      }
   }

   public byte getRows() {
      return this.rows;
   }

   public void setRows(byte value) {
      if (value >= DIMS[1][0] && value <= DIMS[1][1]) {
         this.rows = value;
      } else {
         throw new IllegalArgumentException(HString.format("The desired row count '%d' was outside the bounds [%d..%d]", value, DIMS[1][0], DIMS[1][1]));
      }
   }

   public NBTTagByteArray serializeNBT() {
      byte[] dimensions = new byte[]{this.columns, this.rows};
      return new NBTTagByteArray(dimensions);
   }

   public void deserializeNBT(NBTTagByteArray nbtTagByteArray) {
      byte[] dimensions = nbtTagByteArray.getByteArray();
      this.setColumns(dimensions[0]);
      this.setRows(dimensions[1]);
   }

   public String toString() {
      return HString.format("%dx%d", this.columns, this.rows);
   }

   public static WolfInventorySize parseWolfInventorySize(String s) {
      Pattern pattern = Pattern.compile(HString.format("([%d-%d])x([%d-%d])", DIMS[0][0], DIMS[0][1], DIMS[1][0], DIMS[1][1]));
      Matcher matcher = pattern.matcher(s);
      if (!matcher.matches()) {
         throw new IllegalArgumentException(HString.format("The input string was not in the proper format."));
      } else {
         return new WolfInventorySize(Byte.parseByte(matcher.group(1)), Byte.parseByte(matcher.group(2)));
      }
   }
}
