package betterquesting.api.utils;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.placeholders.ItemPlaceholder;
import betterquesting.api.placeholders.PlaceholderConverter;
import betterquesting.api2.utils.BQThreadedIO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;

public class JsonHelper {
   public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

   public JsonHelper() {
   }

   public static JsonArray GetArray(@Nonnull JsonObject json, @Nonnull String id) {
      return json.get(id) instanceof JsonArray ? json.get(id).getAsJsonArray() : new JsonArray();
   }

   public static JsonObject GetObject(@Nonnull JsonObject json, @Nonnull String id) {
      return json.get(id) instanceof JsonObject ? json.get(id).getAsJsonObject() : new JsonObject();
   }

   public static String GetString(@Nonnull JsonObject json, @Nonnull String id, String def) {
      return json.get(id) instanceof JsonPrimitive && json.get(id).getAsJsonPrimitive().isString() ? json.get(id).getAsString() : def;
   }

   public static Number GetNumber(@Nonnull JsonObject json, @Nonnull String id, Number def) {
      if (json.get(id) instanceof JsonPrimitive) {
         try {
            return json.get(id).getAsNumber();
         } catch (Exception var4) {
            return def;
         }
      } else {
         return def;
      }
   }

   public static boolean GetBoolean(@Nonnull JsonObject json, @Nonnull String id, boolean def) {
      if (json.get(id) instanceof JsonPrimitive) {
         try {
            return json.get(id).getAsBoolean();
         } catch (Exception var4) {
            return def;
         }
      } else {
         return def;
      }
   }

   public static ArrayList<JsonElement> GetUnderlyingArray(JsonArray array) {
      try {
         Field field = JsonArray.class.getDeclaredField("elements");
         field.setAccessible(true);
         return (ArrayList)field.get(array);
      } catch (Exception var2) {
         QuestingAPI.getLogger().log(Level.ERROR, "Unable to retrieve underlying JsonArray:", var2);
         return null;
      }
   }

   public static void ClearCompoundTag(@Nonnull NBTTagCompound tag) {
      ArrayList<String> list = new ArrayList(tag.getKeySet());
      list.forEach(tag::removeTag);
   }

   public static JsonObject ReadFromFile(File file) {
      if (file != null && file.exists()) {
         Future<JsonObject> task = BQThreadedIO.INSTANCE.enqueue(() -> {
            try {
               FileInputStream fis = new FileInputStream(file);
               Throwable var36 = null;

               JsonObject var6;
               try {
                  InputStreamReader fr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                  Throwable var4 = null;

                  try {
                     JsonObject json = (JsonObject)GSON.fromJson(fr, JsonObject.class);
                     fr.close();
                     var6 = json;
                  } catch (Throwable var31) {
                     var4 = var31;
                     throw var31;
                  } finally {
                     if (fr != null) {
                        if (var4 != null) {
                           try {
                              fr.close();
                           } catch (Throwable var30) {
                              var4.addSuppressed(var30);
                           }
                        } else {
                           fr.close();
                        }
                     }

                  }
               } catch (Throwable var33) {
                  var36 = var33;
                  throw var33;
               } finally {
                  if (fis != null) {
                     if (var36 != null) {
                        try {
                           fis.close();
                        } catch (Throwable var29) {
                           var36.addSuppressed(var29);
                        }
                     } else {
                        fis.close();
                     }
                  }

               }

               return var6;
            } catch (Exception var35) {
               QuestingAPI.getLogger().log(Level.ERROR, "An error occured while loading JSON from file:", var35);
               int i = 0;

               File bkup;
               String var10002;
               StringBuilder var10003;
               for(bkup = new File(file.getParent(), "malformed_" + file.getName() + i + ".json"); bkup.exists(); bkup = new File(var10002, var10003.append(i).append(".json").toString())) {
                  var10002 = file.getParent();
                  var10003 = (new StringBuilder()).append("malformed_").append(file.getName());
                  ++i;
               }

               QuestingAPI.getLogger().log(Level.ERROR, "Creating backup at: " + bkup.getAbsolutePath());
               CopyPaste(file, bkup);
               return new JsonObject();
            }
         });

         try {
            return (JsonObject)task.get();
         } catch (Exception var3) {
            QuestingAPI.getLogger().error("Unable to read from file " + file, var3);
            return new JsonObject();
         }
      } else {
         return new JsonObject();
      }
   }

   public static void WriteToFile(File file, JsonObject jObj) {
      File tmp = new File(file.getAbsolutePath() + ".tmp");
      BQThreadedIO.INSTANCE.enqueue(() -> {
         try {
            if (tmp.exists()) {
               tmp.delete();
            } else if (tmp.getParentFile() != null) {
               tmp.getParentFile().mkdirs();
            }

            tmp.createNewFile();
         } catch (Exception var97) {
            QuestingAPI.getLogger().error("An error occured while saving JSON to file (Directory setup):", var97);
            return;
         }

         try {
            FileOutputStream fos = new FileOutputStream(tmp);
            Throwable var4 = null;

            try {
               OutputStreamWriter fw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
               Throwable var6 = null;

               try {
                  GSON.toJson(jObj, fw);
                  fw.flush();
               } catch (Throwable var96) {
                  var6 = var96;
                  throw var96;
               } finally {
                  if (fw != null) {
                     if (var6 != null) {
                        try {
                           fw.close();
                        } catch (Throwable var93) {
                           var6.addSuppressed(var93);
                        }
                     } else {
                        fw.close();
                     }
                  }

               }
            } catch (Throwable var103) {
               var4 = var103;
               throw var103;
            } finally {
               if (fos != null) {
                  if (var4 != null) {
                     try {
                        fos.close();
                     } catch (Throwable var92) {
                        var4.addSuppressed(var92);
                     }
                  } else {
                     fos.close();
                  }
               }

            }
         } catch (Exception var105) {
            QuestingAPI.getLogger().error("An error occured while saving JSON to file (File write):", var105);
            return;
         }

         try {
            FileInputStream fis = new FileInputStream(tmp);
            Throwable var107 = null;

            try {
               InputStreamReader fr = new InputStreamReader(fis, StandardCharsets.UTF_8);
               Throwable var109 = null;

               try {
                  GSON.fromJson(fr, JsonObject.class);
               } catch (Throwable var95) {
                  var109 = var95;
                  throw var95;
               } finally {
                  if (fr != null) {
                     if (var109 != null) {
                        try {
                           fr.close();
                        } catch (Throwable var91) {
                           var109.addSuppressed(var91);
                        }
                     } else {
                        fr.close();
                     }
                  }

               }
            } catch (Throwable var99) {
               var107 = var99;
               throw var99;
            } finally {
               if (fis != null) {
                  if (var107 != null) {
                     try {
                        fis.close();
                     } catch (Throwable var90) {
                        var107.addSuppressed(var90);
                     }
                  } else {
                     fis.close();
                  }
               }

            }
         } catch (Exception var101) {
            QuestingAPI.getLogger().error("An error occured while saving JSON to file (Validation check):", var101);
            return;
         }

         try {
            if (file.exists()) {
               file.delete();
            }

            tmp.renameTo(file);
         } catch (Exception var94) {
            QuestingAPI.getLogger().error("An error occured while saving JSON to file (Temp copy):", var94);
         }

      });
   }

   public static void CopyPaste(File fileIn, File fileOut) {
      if (fileIn.exists()) {
         try {
            if (fileOut.getParentFile() != null) {
               fileOut.getParentFile().mkdirs();
            }

            Files.copy(fileIn.toPath(), fileOut.toPath(), StandardCopyOption.REPLACE_EXISTING);
         } catch (Exception var3) {
            QuestingAPI.getLogger().log(Level.ERROR, "Failed copy paste", var3);
         }

      }
   }

   public static String makeFileNameSafe(String s) {
      for(char c : ChatAllowedCharacters.ILLEGAL_FILE_CHARACTERS) {
         s = s.replace(c, '_');
      }

      return s;
   }

   public static boolean isItem(NBTTagCompound json) {
      if (json != null && json.hasKey("id") && json.hasKey("Count", 99) && json.hasKey("Damage", 99)) {
         if (json.hasKey("id", 8)) {
            return Item.REGISTRY.containsKey(new ResourceLocation(json.getString("id")));
         } else {
            return Item.REGISTRY.getObjectById(json.getInteger("id")) != null;
         }
      } else {
         return false;
      }
   }

   public static boolean isFluid(NBTTagCompound json) {
      return json != null && json.hasKey("FluidName", 8) && json.hasKey("Amount", 99) && FluidRegistry.getFluid(json.getString("FluidName")) != null;
   }

   public static boolean isEntity(NBTTagCompound tags) {
      return tags.hasKey("id") && EntityList.isRegistered(new ResourceLocation(tags.getString("id")));
   }

   public static BigItemStack JsonToItemStack(NBTTagCompound nbt) {
      Item preCheck = Item.getByNameOrId(nbt.hasKey("id", 99) ? "" + nbt.getShort("id") : nbt.getString("id"));
      return preCheck != null && preCheck != ItemPlaceholder.placeholder ? new BigItemStack(nbt) : PlaceholderConverter.convertItem(preCheck, nbt.getString("id"), nbt.getInteger("Count"), nbt.getShort("Damage"), nbt.getString("OreDict"), !nbt.hasKey("tag", 10) ? null : nbt.getCompoundTag("tag"));
   }

   public static NBTTagCompound ItemStackToJson(BigItemStack stack, NBTTagCompound nbt) {
      if (stack != null) {
         stack.writeToNBT(nbt);
      }

      return nbt;
   }

   public static FluidStack JsonToFluidStack(NBTTagCompound json) {
      String name = json.hasKey("FluidName", 8) ? json.getString("FluidName") : "water";
      int amount = json.getInteger("Amount");
      NBTTagCompound tags = !json.hasKey("Tag", 10) ? null : json.getCompoundTag("Tag");
      Fluid fluid = FluidRegistry.getFluid(name);
      return PlaceholderConverter.convertFluid(fluid, name, amount, tags);
   }

   public static NBTTagCompound FluidStackToJson(FluidStack stack, NBTTagCompound json) {
      if (stack == null) {
         return json;
      } else {
         json.setString("FluidName", FluidRegistry.getFluidName(stack));
         json.setInteger("Amount", stack.amount);
         if (stack.tag != null) {
            json.setTag("Tag", stack.tag);
         }

         return json;
      }
   }

   public static Entity JsonToEntity(NBTTagCompound tags, World world) {
      Entity entity = null;
      if (tags.hasKey("id") && EntityList.isRegistered(new ResourceLocation(tags.getString("id")))) {
         entity = EntityList.createEntityFromNBT(tags, world);
      }

      return PlaceholderConverter.convertEntity(entity, world, tags);
   }

   public static NBTTagCompound EntityToJson(Entity entity, NBTTagCompound json) {
      if (entity == null) {
         return json;
      } else {
         NBTTagCompound tags = new NBTTagCompound();
         entity.writeToNBTOptional(tags);
         String id = EntityList.getEntityString(entity);
         tags.setString("id", id != null ? id : "");
         json.merge(tags);
         return json;
      }
   }
}
