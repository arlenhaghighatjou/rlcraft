package codersafterdark.reskillable.api.data;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;

public abstract class NBTLockKey implements FuzzyLockKey {
   protected NBTTagCompound tag;

   protected NBTLockKey(NBTTagCompound tag) {
      this.tag = tag != null && !tag.hasNoTags() ? tag : null;
   }

   protected static boolean similarNBT(NBTBase full, NBTBase partial) {
      if (full == null) {
         return partial == null;
      } else if (partial == null) {
         return true;
      } else if (full.getId() != partial.getId()) {
         return false;
      } else if (full.equals(partial)) {
         return true;
      } else {
         switch(full.getId()) {
         case 7:
            byte[] fByteArray = ((NBTTagByteArray)full).getByteArray();
            byte[] pByteArray = ((NBTTagByteArray)partial).getByteArray();
            List<Integer> hits = new ArrayList();

            for(byte pByte : pByteArray) {
               boolean hasMatch = false;

               for(int i = 0; i < fByteArray.length; ++i) {
                  if (!hits.contains(Integer.valueOf(i)) && pByte == fByteArray[i]) {
                     hits.add(Integer.valueOf(i));
                     hasMatch = true;
                     break;
                  }
               }

               if (!hasMatch) {
                  return false;
               }
            }

            return true;
         case 8:
         default:
            return false;
         case 9:
            NBTTagList fTagList = (NBTTagList)full;
            NBTTagList pTagList = (NBTTagList)partial;
            if ((!fTagList.hasNoTags() || pTagList.hasNoTags()) && fTagList.getTagType() == pTagList.getTagType()) {
               for(int i = 0; i < pTagList.tagCount(); ++i) {
                  NBTBase pTag = pTagList.get(i);
                  boolean hasTag = false;

                  for(int j = 0; j < fTagList.tagCount(); ++j) {
                     if (similarNBT(fTagList.get(j), pTag)) {
                        hasTag = true;
                        break;
                     }
                  }

                  if (!hasTag) {
                     return false;
                  }
               }

               return true;
            }

            return false;
         case 10:
            NBTTagCompound fullTag = (NBTTagCompound)full;
            NBTTagCompound partialTag = (NBTTagCompound)partial;

            for(String partialKey : partialTag.getKeySet()) {
               if (!fullTag.hasKey(partialKey, partialTag.getTagId(partialKey)) || !similarNBT(fullTag.getTag(partialKey), partialTag.getTag(partialKey))) {
                  return false;
               }
            }

            return true;
         case 11:
            int[] fIntArray = ((NBTTagIntArray)full).getIntArray();
            int[] pIntArray = ((NBTTagIntArray)partial).getIntArray();
            hits = new ArrayList();

            for(int pInt : pIntArray) {
               boolean hasMatch = false;

               for(int i = 0; i < fIntArray.length; ++i) {
                  if (!hits.contains(Integer.valueOf(i)) && pInt == fIntArray[i]) {
                     hits.add(Integer.valueOf(i));
                     hasMatch = true;
                     break;
                  }
               }

               if (!hasMatch) {
                  return false;
               }
            }

            return true;
         case 12:
            long[] fLongArray = getLongArray((NBTTagLongArray)full);
            long[] pLongArray = getLongArray((NBTTagLongArray)partial);
            hits = new ArrayList();

            for(long pLong : pLongArray) {
               boolean hasMatch = false;

               for(int i = 0; i < fLongArray.length; ++i) {
                  if (!hits.contains(Integer.valueOf(i)) && pLong == fLongArray[i]) {
                     hits.add(Integer.valueOf(i));
                     hasMatch = true;
                     break;
                  }
               }

               if (!hasMatch) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static long[] getLongArray(NBTTagLongArray tag) {
      String t = tag.toString();
      String[] entries = t.substring(3, t.length() - 1).split(",");
      long[] data = new long[entries.length];

      for(int i = 0; i < entries.length; ++i) {
         try {
            data[i] = Long.parseLong(entries[i].substring(0, entries[i].length() - 1));
         } catch (Exception var6) {
            ;
         }
      }

      return data;
   }

   public NBTTagCompound getTag() {
      return this.tag;
   }

   public boolean isNotFuzzy() {
      return this.tag == null;
   }

   public boolean fuzzyEquals(FuzzyLockKey other) {
      if (other == this) {
         return true;
      } else {
         return other instanceof NBTLockKey ? similarNBT(this.getTag(), ((NBTLockKey)other).getTag()) : false;
      }
   }
}
