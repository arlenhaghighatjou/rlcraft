package betterquesting.api.utils;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StringUtils;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;

public class ItemComparison {
   public ItemComparison() {
   }

   public static boolean StackMatch(ItemStack stack1, ItemStack stack2, boolean nbtCheck, boolean partialNBT) {
      if (stack1 == stack2) {
         return true;
      } else if (stack1 != null && stack2 != null) {
         if (nbtCheck) {
            if (!partialNBT && !stack1.areCapsCompatible(stack2)) {
               return false;
            }

            if (!CompareNBTTag(stack1.getTagCompound(), stack2.getTagCompound(), partialNBT)) {
               return false;
            }
         }

         return stack1.getItem() == stack2.getItem() && (stack1.getItemDamage() == stack2.getItemDamage() || stack1.getItem().isDamageable() || stack1.getItemDamage() == 32767);
      } else {
         return false;
      }
   }

   public static boolean CompareNBTTag(NBTBase tag1, NBTBase tag2, boolean partial) {
      if (isEmptyNBT(tag1) != isEmptyNBT(tag2)) {
         return false;
      } else if (isEmptyNBT(tag1)) {
         return true;
      } else if ((!(tag1 instanceof NBTPrimitive) || !(tag2 instanceof NBTPrimitive)) && tag1.getId() != tag2.getId()) {
         return false;
      } else if (tag1 instanceof NBTTagCompound && tag2 instanceof NBTTagCompound) {
         return CompareNBTTagCompound((NBTTagCompound)tag1, (NBTTagCompound)tag2, partial);
      } else {
         if (tag1 instanceof NBTTagList && tag2 instanceof NBTTagList) {
            NBTTagList list1 = (NBTTagList)tag1;
            NBTTagList list2 = (NBTTagList)tag2;
            if (list1.tagCount() > list2.tagCount() || !partial && list1.tagCount() != list2.tagCount()) {
               return false;
            }

            label182:
            for(int i = 0; i < list1.tagCount(); ++i) {
               NBTBase lt1 = list1.get(i);

               for(int j = 0; j < list2.tagCount(); ++j) {
                  if (CompareNBTTag(lt1, list2.get(j), partial)) {
                     continue label182;
                  }
               }

               return false;
            }
         } else {
            if (tag1 instanceof NBTTagIntArray && tag2 instanceof NBTTagIntArray) {
               NBTTagIntArray list1 = (NBTTagIntArray)tag1;
               NBTTagIntArray list2 = (NBTTagIntArray)tag2;
               if (list1.getIntArray().length <= list2.getIntArray().length && (partial || list1.getIntArray().length == list2.getIntArray().length)) {
                  List<Integer> usedIdxs = new ArrayList();

                  label134:
                  for(int i = 0; i < list1.getIntArray().length; ++i) {
                     for(int j = 0; j < list2.getIntArray().length; ++j) {
                        if (!usedIdxs.contains(Integer.valueOf(j)) && list1.getIntArray()[i] == list2.getIntArray()[j]) {
                           usedIdxs.add(Integer.valueOf(j));
                           continue label134;
                        }
                     }

                     return false;
                  }

                  return true;
               }

               return false;
            }

            if (tag1 instanceof NBTTagByteArray && tag2 instanceof NBTTagByteArray) {
               NBTTagByteArray list1 = (NBTTagByteArray)tag1;
               NBTTagByteArray list2 = (NBTTagByteArray)tag2;
               if (list1.getByteArray().length > list2.getByteArray().length || !partial && list1.getByteArray().length != list2.getByteArray().length) {
                  return false;
               }

               List<Integer> usedIdxs = new ArrayList();

               label170:
               for(int i = 0; i < list1.getByteArray().length; ++i) {
                  for(int j = 0; j < list2.getByteArray().length; ++j) {
                     if (!usedIdxs.contains(Integer.valueOf(j)) && list1.getByteArray()[i] == list2.getByteArray()[j]) {
                        usedIdxs.add(Integer.valueOf(j));
                        continue label170;
                     }
                  }

                  return false;
               }
            } else {
               if (!(tag1 instanceof NBTTagLongArray) || !(tag2 instanceof NBTTagLongArray)) {
                  if (tag1 instanceof NBTTagString && tag2 instanceof NBTTagString) {
                     return tag1.equals(tag2);
                  }

                  if (tag1 instanceof NBTPrimitive && tag2 instanceof NBTPrimitive) {
                     Number num1 = NBTConverter.getNumber(tag1);
                     Number num2 = NBTConverter.getNumber(tag2);
                     if (!(tag1 instanceof NBTTagFloat) && !(tag1 instanceof NBTTagDouble) && !(tag2 instanceof NBTTagFloat) && !(tag2 instanceof NBTTagDouble)) {
                        return num1.longValue() == num2.longValue();
                     }

                     return num1.doubleValue() == num2.doubleValue();
                  }

                  return tag1.equals(tag2);
               }

               NBTTagLongArray list1 = (NBTTagLongArray)tag1;
               NBTTagLongArray list2 = (NBTTagLongArray)tag2;
               long[] la1 = NBTConverter.readLongArray(list1);
               long[] la2 = NBTConverter.readLongArray(list2);
               if (la1.length > la2.length || !partial && la1.length != la2.length) {
                  return false;
               }

               List<Integer> usedIdxs = new ArrayList();

               label157:
               for(long l : la1) {
                  for(int j = 0; j < la2.length; ++j) {
                     if (!usedIdxs.contains(Integer.valueOf(j)) && l == la2[j]) {
                        usedIdxs.add(Integer.valueOf(j));
                        continue label157;
                     }
                  }

                  return false;
               }
            }
         }

         return true;
      }
   }

   private static boolean CompareNBTTagCompound(NBTTagCompound reqTags, NBTTagCompound sample, boolean partial) {
      if (isEmptyNBT(reqTags) != isEmptyNBT(sample)) {
         return false;
      } else if (isEmptyNBT(reqTags)) {
         return true;
      } else {
         for(String key : reqTags.getKeySet()) {
            if (!sample.hasKey(key)) {
               return false;
            }

            if (!CompareNBTTag(reqTags.getTag(key), sample.getTag(key), partial)) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isEmptyNBT(NBTBase tag) {
      return tag == null || tag.hasNoTags();
   }

   /** @deprecated */
   @Deprecated
   public static boolean OreDictionaryMatch(String name, ItemStack stack) {
      return stack != null && !StringUtils.isNullOrEmpty(name) && (new OreIngredient(name)).apply(stack);
   }

   /** @deprecated */
   @Deprecated
   public static boolean OreDictionaryMatch(String name, NBTTagCompound tags, ItemStack stack, boolean nbtCheck, boolean partialNBT) {
      if (nbtCheck) {
         return OreDictionaryMatch(new OreIngredient(name), tags, stack, nbtCheck, partialNBT);
      } else {
         return stack != null && !StringUtils.isNullOrEmpty(name) && (new OreIngredient(name)).apply(stack);
      }
   }

   public static boolean OreDictionaryMatch(OreIngredient ore, NBTTagCompound tags, ItemStack stack, boolean nbtCheck, boolean partialNBT) {
      if (stack != null && ore != null) {
         return ore.apply(stack) && (!nbtCheck || CompareNBTTagCompound(stack.getTagCompound(), tags, partialNBT));
      } else {
         return false;
      }
   }

   /** @deprecated */
   @Deprecated
   public static boolean AllMatch(ItemStack stack1, ItemStack stack2) {
      return AllMatch(stack1, stack2, false, false);
   }

   public static boolean AllMatch(ItemStack stack1, ItemStack stack2, boolean nbtCheck, boolean partialNBT) {
      if (stack1 == stack2) {
         return true;
      } else if (stack1 == null) {
         return false;
      } else if (nbtCheck && !CompareNBTTagCompound(stack1.getTagCompound(), stack2.getTagCompound(), partialNBT)) {
         return false;
      } else if (StackMatch(stack1, stack2, false, false)) {
         return true;
      } else {
         int[] oreIDs1 = OreDictionary.getOreIDs(stack1);
         int[] oreIDs2 = OreDictionary.getOreIDs(stack2);

         for(int i1 : oreIDs1) {
            for(int i2 : oreIDs2) {
               if (i1 == i2) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
