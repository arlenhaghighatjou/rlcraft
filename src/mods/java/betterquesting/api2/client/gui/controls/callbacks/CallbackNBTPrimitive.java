package betterquesting.api2.client.gui.controls.callbacks;

import betterquesting.api.misc.ICallback;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;

public class CallbackNBTPrimitive<T extends Number> implements ICallback<T> {
   private final NBTBase tag;
   private final String sKey;
   private final int iKey;
   private final int tagID;

   public CallbackNBTPrimitive(NBTTagCompound tag, String key, Class<T> c) {
      this.tag = tag;
      this.sKey = key;
      this.iKey = -1;
      this.tagID = this.getTagID(c);
   }

   public CallbackNBTPrimitive(NBTTagList tag, int key, Class<T> c) {
      this.tag = tag;
      this.sKey = null;
      this.iKey = key;
      this.tagID = this.getTagID(c);
   }

   public void setValue(T value) {
      if (this.tag.getId() == 10) {
         this.setCompoundTag((NBTTagCompound)this.tag, value);
      } else {
         this.setListTag((NBTTagList)this.tag, value);
      }

   }

   private void setCompoundTag(NBTTagCompound compound, T value) {
      switch(this.tagID) {
      case 1:
         compound.setByte(this.sKey, value.byteValue());
         break;
      case 2:
         compound.setShort(this.sKey, value.shortValue());
         break;
      case 3:
         compound.setInteger(this.sKey, value.intValue());
         break;
      case 4:
         compound.setLong(this.sKey, value.longValue());
         break;
      case 5:
         compound.setFloat(this.sKey, value.floatValue());
         break;
      case 6:
         compound.setDouble(this.sKey, value.doubleValue());
      }

   }

   private void setListTag(NBTTagList list, T value) {
      switch(this.tagID) {
      case 1:
         list.set(this.iKey, new NBTTagByte(value.byteValue()));
         break;
      case 2:
         list.set(this.iKey, new NBTTagShort(value.shortValue()));
         break;
      case 3:
         list.set(this.iKey, new NBTTagInt(value.intValue()));
         break;
      case 4:
         list.set(this.iKey, new NBTTagLong(value.longValue()));
         break;
      case 5:
         list.set(this.iKey, new NBTTagFloat(value.floatValue()));
         break;
      case 6:
         list.set(this.iKey, new NBTTagDouble(value.doubleValue()));
      }

   }

   private int getTagID(Class<T> c) {
      if (c == Byte.class) {
         return 1;
      } else if (c == Short.class) {
         return 2;
      } else if (c == Integer.class) {
         return 3;
      } else if (c == Long.class) {
         return 4;
      } else if (c == Float.class) {
         return 5;
      } else {
         return c == Double.class ? 6 : 0;
      }
   }
}
