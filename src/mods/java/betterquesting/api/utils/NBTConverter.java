package betterquesting.api.utils;

import betterquesting.api.api.QuestingAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class NBTConverter {
   public NBTConverter() {
   }

   private static JsonElement NBTtoJSON_Base(NBTBase tag, boolean format) {
      if (tag == null) {
         return new JsonObject();
      } else if (tag.getId() >= 1 && tag.getId() <= 6) {
         return new JsonPrimitive(getNumber(tag));
      } else if (tag instanceof NBTTagString) {
         return new JsonPrimitive(((NBTTagString)tag).getString());
      } else if (tag instanceof NBTTagCompound) {
         return NBTtoJSON_Compound((NBTTagCompound)tag, new JsonObject(), format);
      } else if (tag instanceof NBTTagList) {
         if (format) {
            JsonObject jAry = new JsonObject();
            NBTTagList tagList = (NBTTagList)tag;

            for(int i = 0; i < tagList.tagCount(); ++i) {
               jAry.add(i + ":" + tagList.get(i).getId(), NBTtoJSON_Base(tagList.get(i), true));
            }

            return jAry;
         } else {
            JsonArray jAry = new JsonArray();

            for(NBTBase t : (NBTTagList)tag) {
               jAry.add(NBTtoJSON_Base(t, false));
            }

            return jAry;
         }
      } else if (tag instanceof NBTTagByteArray) {
         JsonArray jAry = new JsonArray();

         for(byte b : ((NBTTagByteArray)tag).getByteArray()) {
            jAry.add(new JsonPrimitive(b));
         }

         return jAry;
      } else if (tag instanceof NBTTagIntArray) {
         JsonArray jAry = new JsonArray();

         for(int i : ((NBTTagIntArray)tag).getIntArray()) {
            jAry.add(new JsonPrimitive(i));
         }

         return jAry;
      } else if (!(tag instanceof NBTTagLongArray)) {
         return new JsonObject();
      } else {
         JsonArray jAry = new JsonArray();

         for(long l : readLongArray((NBTTagLongArray)tag)) {
            jAry.add(new JsonPrimitive(l));
         }

         return jAry;
      }
   }

   public static long[] readLongArray(NBTTagLongArray tag) {
      if (tag == null) {
         return new long[0];
      } else {
         String[] entry = tag.toString().replaceAll("[\\[\\]L;]", "").split(",");
         long[] ary = new long[entry.length];

         for(int i = 0; i < entry.length; ++i) {
            try {
               ary[i] = Long.parseLong(entry[i]);
            } catch (Exception var5) {
               ;
            }
         }

         return ary;
      }
   }

   public static JsonObject NBTtoJSON_Compound(NBTTagCompound parent, JsonObject jObj, boolean format) {
      if (parent == null) {
         return jObj;
      } else {
         for(String key : parent.getKeySet()) {
            NBTBase tag = parent.getTag(key);
            if (format) {
               jObj.add(key + ":" + tag.getId(), NBTtoJSON_Base(tag, true));
            } else {
               jObj.add(key, NBTtoJSON_Base(tag, false));
            }
         }

         return jObj;
      }
   }

   public static NBTTagCompound JSONtoNBT_Object(JsonObject jObj, NBTTagCompound tags, boolean format) {
      if (jObj == null) {
         return tags;
      } else {
         for(Entry<String, JsonElement> entry : jObj.entrySet()) {
            String key = (String)entry.getKey();
            if (!format) {
               tags.setTag(key, JSONtoNBT_Element((JsonElement)entry.getValue(), (byte)0, false));
            } else {
               String[] s = key.split(":");
               byte id = 0;

               try {
                  id = Byte.parseByte(s[s.length - 1]);
                  key = key.substring(0, key.lastIndexOf(":" + id));
               } catch (Exception var9) {
                  if (tags.hasKey(key)) {
                     QuestingAPI.getLogger().log(Level.WARN, "JSON/NBT formatting conflict on key '" + key + "'. Skipping...");
                     continue;
                  }
               }

               tags.setTag(key, JSONtoNBT_Element((JsonElement)entry.getValue(), id, true));
            }
         }

         return tags;
      }
   }

   private static NBTBase JSONtoNBT_Element(JsonElement jObj, byte id, boolean format) {
      if (jObj == null) {
         return new NBTTagString();
      } else {
         byte tagID = id <= 0 ? fallbackTagID(jObj) : id;

         try {
            if (tagID == 1 && (id <= 0 || jObj.getAsJsonPrimitive().isBoolean())) {
               return new NBTTagByte((byte)(jObj.getAsBoolean() ? 1 : 0));
            }

            if (tagID >= 1 && tagID <= 6) {
               return instanceNumber(jObj.getAsNumber(), tagID);
            }

            if (tagID == 8) {
               return new NBTTagString(jObj.getAsString());
            }

            if (tagID == 10) {
               return JSONtoNBT_Object(jObj.getAsJsonObject(), new NBTTagCompound(), format);
            }

            if (tagID == 7) {
               JsonArray jAry = jObj.getAsJsonArray();
               byte[] bAry = new byte[jAry.size()];

               for(int i = 0; i < jAry.size(); ++i) {
                  bAry[i] = jAry.get(i).getAsByte();
               }

               return new NBTTagByteArray(bAry);
            }

            if (tagID == 11) {
               JsonArray jAry = jObj.getAsJsonArray();
               int[] iAry = new int[jAry.size()];

               for(int i = 0; i < jAry.size(); ++i) {
                  iAry[i] = jAry.get(i).getAsInt();
               }

               return new NBTTagIntArray(iAry);
            }

            if (tagID == 12) {
               JsonArray jAry = jObj.getAsJsonArray();
               long[] lAry = new long[jAry.size()];

               for(int i = 0; i < jAry.size(); ++i) {
                  lAry[i] = jAry.get(i).getAsLong();
               }

               return new NBTTagLongArray(lAry);
            }

            if (tagID == 9) {
               NBTTagList tList = new NBTTagList();
               if (jObj.isJsonArray()) {
                  JsonArray jAry = jObj.getAsJsonArray();

                  for(int i = 0; i < jAry.size(); ++i) {
                     JsonElement jElm = jAry.get(i);
                     tList.appendTag(JSONtoNBT_Element(jElm, (byte)0, format));
                  }
               } else if (jObj.isJsonObject()) {
                  JsonObject jAry = jObj.getAsJsonObject();

                  for(Entry<String, JsonElement> entry : jAry.entrySet()) {
                     try {
                        String[] s = ((String)entry.getKey()).split(":");
                        byte id2 = Byte.parseByte(s[s.length - 1]);
                        tList.appendTag(JSONtoNBT_Element((JsonElement)entry.getValue(), id2, format));
                     } catch (Exception var10) {
                        tList.appendTag(JSONtoNBT_Element((JsonElement)entry.getValue(), (byte)0, format));
                     }
                  }
               }

               return tList;
            }
         } catch (Exception var11) {
            QuestingAPI.getLogger().log(Level.ERROR, "An error occured while parsing JsonElement to NBTBase (" + tagID + "):", var11);
         }

         QuestingAPI.getLogger().log(Level.WARN, "Unknown NBT representation for " + jObj.toString() + " (ID: " + tagID + ")");
         return new NBTTagString();
      }
   }

   public static Number getNumber(NBTBase tag) {
      if (tag instanceof NBTTagByte) {
         return ((NBTTagByte)tag).getByte();
      } else if (tag instanceof NBTTagShort) {
         return ((NBTTagShort)tag).getShort();
      } else if (tag instanceof NBTTagInt) {
         return ((NBTTagInt)tag).getInt();
      } else if (tag instanceof NBTTagFloat) {
         return ((NBTTagFloat)tag).getFloat();
      } else if (tag instanceof NBTTagDouble) {
         return ((NBTTagDouble)tag).getDouble();
      } else {
         return (Number)(tag instanceof NBTTagLong ? ((NBTTagLong)tag).getLong() : Integer.valueOf(0));
      }
   }

   public static NBTBase instanceNumber(Number num, byte type) {
      switch(type) {
      case 1:
         return new NBTTagByte(num.byteValue());
      case 2:
         return new NBTTagShort(num.shortValue());
      case 3:
         return new NBTTagInt(num.intValue());
      case 4:
         return new NBTTagLong(num.longValue());
      case 5:
         return new NBTTagFloat(num.floatValue());
      default:
         return new NBTTagDouble(num.doubleValue());
      }
   }

   private static byte fallbackTagID(JsonElement jObj) {
      byte tagID = 0;
      if (jObj.isJsonPrimitive()) {
         JsonPrimitive prim = jObj.getAsJsonPrimitive();
         if (prim.isNumber()) {
            if (prim.getAsString().contains(".")) {
               tagID = 6;
            } else {
               tagID = 4;
            }
         } else if (prim.isBoolean()) {
            tagID = 1;
         } else {
            tagID = 8;
         }
      } else if (jObj.isJsonArray()) {
         JsonArray array = jObj.getAsJsonArray();

         for(JsonElement entry : array) {
            if (entry.isJsonPrimitive() && tagID == 0) {
               try {
                  for(JsonElement element : array) {
                     if (element.getAsLong() != (long)element.getAsByte()) {
                        throw new ClassCastException();
                     }
                  }

                  tagID = 7;
               } catch (Exception var9) {
                  try {
                     for(JsonElement element : array) {
                        if (element.getAsLong() != (long)element.getAsInt()) {
                           throw new ClassCastException();
                        }
                     }

                     tagID = 11;
                  } catch (Exception var8) {
                     tagID = 9;
                  }
               }
            } else if (!entry.isJsonPrimitive()) {
               break;
            }
         }

         tagID = 9;
      } else {
         tagID = 10;
      }

      return tagID;
   }
}
