package ivorius.ivtoolkit.tools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import ivorius.ivtoolkit.math.IvBytePacker;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class IvNBTHelper {
   public IvNBTHelper() {
   }

   public static byte readByte(NBTTagCompound compound, String key, byte defaultValue) {
      return compound != null && compound.hasKey(key, 1) ? compound.getByte(key) : defaultValue;
   }

   public static byte[] readByteArray(NBTTagCompound compound, String key, byte[] defaultValue) {
      return compound != null && compound.hasKey(key, 7) ? compound.getByteArray(key) : defaultValue;
   }

   public static double readDouble(NBTTagCompound compound, String key, double defaultValue) {
      return compound != null && compound.hasKey(key, 6) ? compound.getDouble(key) : defaultValue;
   }

   public static float readFloat(NBTTagCompound compound, String key, float defaultValue) {
      return compound != null && compound.hasKey(key, 5) ? compound.getFloat(key) : defaultValue;
   }

   public static int readInt(NBTTagCompound compound, String key, int defaultValue) {
      return compound != null && compound.hasKey(key, 3) ? compound.getInteger(key) : defaultValue;
   }

   public static int[] readIntArray(NBTTagCompound compound, String key, int[] defaultValue) {
      return compound != null && compound.hasKey(key, 11) ? compound.getIntArray(key) : defaultValue;
   }

   public static long readLong(NBTTagCompound compound, String key, long defaultValue) {
      return compound != null && compound.hasKey(key, 4) ? compound.getLong(key) : defaultValue;
   }

   public static short readShort(NBTTagCompound compound, String key, short defaultValue) {
      return compound != null && compound.hasKey(key, 2) ? compound.getShort(key) : defaultValue;
   }

   public static String readString(NBTTagCompound compound, String key, String defaultValue) {
      return compound != null && compound.hasKey(key, 8) ? compound.getString(key) : defaultValue;
   }

   public static double[] readDoubleArray(String key, NBTTagCompound compound) {
      if (!compound.hasKey(key)) {
         return null;
      } else {
         NBTTagList list = compound.getTagList(key, 6);
         double[] array = new double[list.tagCount()];

         for(int i = 0; i < array.length; ++i) {
            array[i] = list.getDoubleAt(i);
         }

         return array;
      }
   }

   public static void writeDoubleArray(String key, double[] array, NBTTagCompound compound) {
      if (array != null) {
         NBTTagList list = new NBTTagList();

         for(double d : array) {
            list.appendTag(new NBTTagDouble(d));
         }

         compound.setTag(key, list);
      }

   }

   public static String[] readNBTStrings(String id, NBTTagCompound compound) {
      if (!compound.hasKey(id)) {
         return null;
      } else {
         NBTTagList nbtTagList = compound.getTagList(id, 8);
         String[] strings = new String[nbtTagList.tagCount()];

         for(int i = 0; i < strings.length; ++i) {
            strings[i] = nbtTagList.getStringTagAt(i);
         }

         return strings;
      }
   }

   public static void writeNBTStrings(String id, String[] strings, NBTTagCompound compound) {
      if (strings != null) {
         NBTTagList nbtTagList = new NBTTagList();

         for(String s : strings) {
            nbtTagList.appendTag(new NBTTagString(s));
         }

         compound.setTag(id, nbtTagList);
      }

   }

   public static ItemStack[] readNBTStacks(String id, NBTTagCompound compound) {
      if (!compound.hasKey(id)) {
         return null;
      } else {
         NBTTagList nbtTagList = compound.getTagList(id, 10);
         ItemStack[] itemStacks = new ItemStack[nbtTagList.tagCount()];

         for(int i = 0; i < itemStacks.length; ++i) {
            itemStacks[i] = new ItemStack(nbtTagList.getCompoundTagAt(i));
         }

         return itemStacks;
      }
   }

   public static void writeNBTStacks(String id, ItemStack[] stacks, NBTTagCompound compound) {
      if (stacks != null) {
         NBTTagList nbtTagList = new NBTTagList();

         for(ItemStack stack : stacks) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            stack.writeToNBT(tagCompound);
            nbtTagList.appendTag(tagCompound);
         }

         compound.setTag(id, nbtTagList);
      }

   }

   public static Block[] readNBTBlocks(String id, NBTTagCompound compound, MCRegistry registry) {
      if (!compound.hasKey(id)) {
         return null;
      } else {
         NBTTagList nbtTagList = compound.getTagList(id, 8);
         Block[] blocks = new Block[nbtTagList.tagCount()];

         for(int i = 0; i < blocks.length; ++i) {
            blocks[i] = registry.blockFromID(new ResourceLocation(nbtTagList.getStringTagAt(i)));
         }

         return blocks;
      }
   }

   public static void writeNBTBlocks(String id, Block[] blocks, NBTTagCompound compound) {
      if (blocks != null) {
         NBTTagList nbtTagList = new NBTTagList();

         for(Block b : blocks) {
            nbtTagList.appendTag(new NBTTagString(((ResourceLocation)Block.REGISTRY.getNameForObject(b)).toString()));
         }

         compound.setTag(id, nbtTagList);
      }

   }

   public static long[] readNBTLongs(String id, NBTTagCompound compound) {
      if (!compound.hasKey(id)) {
         return null;
      } else {
         ByteBuf bytes = Unpooled.copiedBuffer(compound.getByteArray(id));
         long[] longs = new long[bytes.capacity() / 8];

         for(int i = 0; i < longs.length; ++i) {
            longs[i] = bytes.readLong();
         }

         return longs;
      }
   }

   public static void writeNBTLongs(String id, long[] longs, NBTTagCompound compound) {
      if (longs != null) {
         ByteBuf bytes = Unpooled.buffer(longs.length * 8);

         for(long aLong : longs) {
            bytes.writeLong(aLong);
         }

         compound.setByteArray(id, bytes.array());
      }

   }

   public static PotionEffect[] readNBTPotions(String id, NBTTagCompound compound) {
      if (!compound.hasKey(id)) {
         return null;
      } else {
         NBTTagList nbtTagList = compound.getTagList(id, 8);
         PotionEffect[] potions = new PotionEffect[nbtTagList.tagCount()];

         for(int i = 0; i < potions.length; ++i) {
            potions[i] = PotionEffect.readCustomPotionEffectFromNBT(nbtTagList.getCompoundTagAt(i));
         }

         return potions;
      }
   }

   public static void writeNBTPotions(String id, PotionEffect[] potions, NBTTagCompound compound) {
      if (potions != null) {
         NBTTagList nbtTagList = new NBTTagList();

         for(PotionEffect p : potions) {
            nbtTagList.appendTag(p.writeCustomPotionEffectToNBT(new NBTTagCompound()));
         }

         compound.setTag(id, nbtTagList);
      }

   }

   public static int[] readIntArrayFixedSize(String id, int length, NBTTagCompound compound) {
      int[] array = compound.getIntArray(id);
      return array.length != length ? new int[length] : array;
   }

   public static void writeCompressed(String idBase, int[] intArray, int maxValueInArray, NBTTagCompound compound) {
      byte bitLength = IvBytePacker.getRequiredBitLength(maxValueInArray);
      byte[] bytes = IvBytePacker.packValues(intArray, bitLength);
      compound.setByteArray(idBase + "_bytes", bytes);
      compound.setByte(idBase + "_bitLength", bitLength);
      compound.setInteger(idBase + "_length", intArray.length);
   }

   public static int[] readCompressed(String idBase, NBTTagCompound compound) {
      byte[] bytes = compound.getByteArray(idBase + "_bytes");
      byte bitLength = compound.getByte(idBase + "_bitLength");
      int intArrayLength = compound.getInteger(idBase + "_length");
      return IvBytePacker.unpackValues(bytes, bitLength, intArrayLength);
   }
}
