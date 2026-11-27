package com.charles445.simpledifficulty.config;

import com.google.gson.JsonParser;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.config.json.JsonItemIdentity;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyValue;
import com.charles445.simpledifficulty.api.config.json.JsonTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureIdentity;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonConsumableTemperatureMigrate;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonConsumableThirstMigrate;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonTemperatureMetadataMigrate;
import com.charles445.simpledifficulty.api.temperature.TemporaryModifierGroupEnum;
import com.charles445.simpledifficulty.compat.JsonCompatDefaults;
import com.charles445.simpledifficulty.config.json.ExtraItem;
import com.charles445.simpledifficulty.config.json.MaterialTemperature;
import com.charles445.simpledifficulty.item.ItemJuice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class JsonConfigInternal {
   public static MaterialTemperature materialTemperature = new MaterialTemperature();
   public static Map<String, ExtraItem> extraItems = new HashMap();
   public static List<String> jsonErrors = new ArrayList();
   public static final JsonItemIdentity DEFAULT_ITEM_IDENTITY = new JsonItemIdentity(-1);

   public JsonConfigInternal() {
   }

   public static void preInit(VFile2 jsonDirectory) {
      SimpleDifficulty.logger.info("Extra Items Initialization");
      makeExtraItem("frost_rod", "Frost Rod - For recipes");
      makeExtraItem("frost_powder", "Frost Powder - For recipes");
      makeExtraItem("dragon_canteen", "Dragon Canteen - Automatically purifies water").put("capacity", "8");
      extraItems = (Map)processJson(JsonFileName.extraItems, extraItems, jsonDirectory, false);
   }

   public static void postInit(VFile2 jsonDirectory) {
      JsonConfig.registerArmorTemperature(new ItemStack(SDItems.wool_helmet), 2.0F);
      JsonConfig.registerArmorTemperature(new ItemStack(SDItems.wool_chestplate), 2.0F);
      JsonConfig.registerArmorTemperature(new ItemStack(SDItems.wool_leggings), 2.0F);
      JsonConfig.registerArmorTemperature(new ItemStack(SDItems.wool_boots), 2.0F);
      JsonConfig.registerArmorTemperature(new ItemStack(SDItems.ice_helmet), -2.0F);
      JsonConfig.registerArmorTemperature(new ItemStack(SDItems.ice_chestplate), -2.0F);
      JsonConfig.registerArmorTemperature(new ItemStack(SDItems.ice_leggings), -2.0F);
      JsonConfig.registerArmorTemperature(new ItemStack(SDItems.ice_boots), -2.0F);
      JsonConfig.registerBlockTemperature(SDBlocks.campfire, 7.0F, new JsonPropertyValue("burning", "true"));
      JsonConfig.registerBlockTemperature(SDBlocks.campfire, 0.0F, new JsonPropertyValue("burning", "false"));
      JsonConfig.registerBlockTemperature(Blocks.TORCH, 1.0F);
      JsonConfig.registerBlockTemperature(Blocks.LIT_FURNACE, 4.0F);
      JsonConfig.registerBlockTemperature(Blocks.LAVA, 12.5F);
      JsonConfig.registerBlockTemperature(Blocks.FLOWING_LAVA, 12.5F);
      JsonConfig.registerBlockTemperature(Blocks.MAGMA, 10.0F);
      JsonConfig.registerConsumableTemperature(TemporaryModifierGroupEnum.FOOD.group(), new ItemStack(Items.MUSHROOM_STEW), 1.0F, 1200);
      JsonConfig.registerConsumableTemperature(TemporaryModifierGroupEnum.DRINK.group(), new ItemStack(SDItems.juice, 1, ItemJuice.JuiceEnum.CACTUS.ordinal()), 1.0F, 1200);
      JsonConfig.registerConsumableTemperature(TemporaryModifierGroupEnum.DRINK.group(), new ItemStack(SDItems.juice, 1, ItemJuice.JuiceEnum.CHORUS_FRUIT.ordinal()), 1.0F, 2400);
      JsonConfig.registerConsumableThirst(new ItemStack(Items.MILK_BUCKET), 4, 1.0F, 0.2F);
      JsonConfig.registerDimensionTemperature(0, 0.0F);
      JsonConfig.registerHeldItem(new ItemStack(Blocks.MAGMA), 3.0F);
      JsonConfig.registerHeldItem(new ItemStack(Blocks.TORCH), 1.0F);
      JsonCompatDefaults.instance.populate();
      processAllJson(jsonDirectory);
   }

   public static void clearContainers() {
      JsonConfig.armorTemperatures.clear();
      JsonConfig.blockTemperatures.clear();
      JsonConfig.consumableTemperature.clear();
      JsonConfig.consumableThirst.clear();
      JsonConfig.dimensionTemperature.clear();
      JsonConfig.fluidTemperatures.clear();
      JsonConfig.heldItemTemperatures.clear();
   }

   public static void processAllJson(VFile2 jsonDirectory) {
      String jsonFileName = JsonFileName.armorTemperatures.get();
      Map<String, List<JsonTemperatureIdentity>> jsonArmorTemperatures = null;

      try {
         jsonArmorTemperatures = (Map)processUncaughtJson(JsonFileName.armorTemperatures, JsonConfig.armorTemperatures, jsonDirectory, true);
      } catch (Exception var30) {
         Map<String, JsonTemperature> migrateMap = new HashMap();
         migrateMap = (Map)processJson(JsonFileName.armorTemperatures_MIGRATE, migrateMap, jsonDirectory, true);
         if (migrateMap != null) {
            boolean migrate = false;

            for(JsonTemperature jt : migrateMap.values()) {
               if (jt.temperature != 0.0F) {
                  migrate = true;
                  break;
               }
            }

            if (migrate) {
               SimpleDifficulty.logger.info("Attempting to migrate " + jsonFileName + " to new format");

               try {
                  for(Entry<String, JsonTemperature> registryEntry : migrateMap.entrySet()) {
                     JsonConfig.registerArmorTemperature((String)registryEntry.getKey(), ((JsonTemperature)registryEntry.getValue()).temperature, new JsonItemIdentity(-1));
                  }

                  try {
                     manuallyWriteToJson(JsonFileName.armorTemperatures, JsonConfig.armorTemperatures, jsonDirectory);
                     SimpleDifficulty.logger.info("Migrated " + jsonFileName);
                  } catch (Exception var25) {
                     logMerge(jsonFileName, var25);
                  }
               } catch (Exception var29) {
                  SimpleDifficulty.logger.error("Migration failed, JSON of " + jsonFileName + " was likely already invalid");
                  logMerge(jsonFileName, var29);
               }
            } else {
               jsonArmorTemperatures = (Map)processJson(JsonFileName.armorTemperatures, JsonConfig.armorTemperatures, jsonDirectory, true);
            }
         }
      }

      if (jsonArmorTemperatures != null) {
         for(Entry<String, List<JsonTemperatureIdentity>> entry : jsonArmorTemperatures.entrySet()) {
            for(JsonTemperatureIdentity jtm : (List<JsonTemperatureIdentity>)entry.getValue()) {
               if (jtm.identity != null) {
                  jtm.identity.tryPopulateCompound();
               }

               JsonConfig.registerArmorTemperature((String)entry.getKey(), jtm.temperature, jtm.identity == null ? DEFAULT_ITEM_IDENTITY : jtm.identity);
            }
         }

         try {
            manuallyWriteToJson(JsonFileName.armorTemperatures, JsonConfig.armorTemperatures, jsonDirectory);
         } catch (Exception var24) {
            logMerge(jsonFileName, var24);
         }
      }

      jsonFileName = JsonFileName.blockTemperatures.get();
      Map<String, List<JsonPropertyTemperature>> jsonBlockTemperatures = (Map)processJson(JsonFileName.blockTemperatures, JsonConfig.blockTemperatures, jsonDirectory, true);
      if (jsonBlockTemperatures != null) {
         for(Entry<String, List<JsonPropertyTemperature>> entry : jsonBlockTemperatures.entrySet()) {
            for(JsonPropertyTemperature propTemp : (List<JsonPropertyTemperature>)entry.getValue()) {
               JsonConfig.registerBlockTemperature((String)entry.getKey(), propTemp.temperature, propTemp.getAsPropertyArray());
            }
         }

         try {
            manuallyWriteToJson(JsonFileName.blockTemperatures, JsonConfig.blockTemperatures, jsonDirectory);
         } catch (Exception var23) {
            logMerge(jsonFileName, var23);
         }
      }

      jsonFileName = JsonFileName.consumableTemperature.get();
      Map<String, List<JsonConsumableTemperature>> jsonConsumableTemperature = (Map)processJson(JsonFileName.consumableTemperature, JsonConfig.consumableTemperature, jsonDirectory, true);
      if (jsonConsumableTemperature != null) {
         boolean migrate = true;

         label353:
         for(List<JsonConsumableTemperature> mvalues : jsonConsumableTemperature.values()) {
            Iterator jsonHeldItemTemperatures = mvalues.iterator();

            while(true) {
               if (!jsonHeldItemTemperatures.hasNext()) {
                  continue label353;
               }

               JsonConsumableTemperature value = (JsonConsumableTemperature)jsonHeldItemTemperatures.next();
               if (value.identity != null) {
                  break;
               }
            }

            migrate = false;
         }

         if (migrate) {
            SimpleDifficulty.logger.info("Attempting to migrate " + jsonFileName + " to new format");
            Map<String, List<JsonConsumableTemperatureMigrate>> migrateMap = new HashMap();

            try {
               migrateMap = (Map)processUncaughtJson(JsonFileName.consumableTemperature_MIGRATE, migrateMap, jsonDirectory, true);

               for(Entry<String, List<JsonConsumableTemperatureMigrate>> registryEntry : migrateMap.entrySet()) {
                  for(JsonConsumableTemperatureMigrate jctm : (List<JsonConsumableTemperatureMigrate>)registryEntry.getValue()) {
                     JsonConfig.registerConsumableTemperature(jctm.group, (String)registryEntry.getKey(), jctm.temperature, jctm.duration, new JsonItemIdentity(jctm.metadata));
                  }
               }

               try {
                  manuallyWriteToJson(JsonFileName.consumableTemperature, JsonConfig.consumableTemperature, jsonDirectory);
                  SimpleDifficulty.logger.info("Migrated " + jsonFileName);
               } catch (Exception var22) {
                  logMerge(jsonFileName, var22);
               }
            } catch (Exception var28) {
               SimpleDifficulty.logger.error("Migration failed, JSON of " + jsonFileName + " was likely already invalid");
               logMerge(jsonFileName, var28);
            }
         } else {
            for(Entry<String, List<JsonConsumableTemperature>> entry : jsonConsumableTemperature.entrySet()) {
               for(JsonConsumableTemperature jct : (List<JsonConsumableTemperature>)entry.getValue()) {
                  if (jct.identity != null) {
                     jct.identity.tryPopulateCompound();
                  }

                  JsonConfig.registerConsumableTemperature(jct.group, (String)entry.getKey(), jct.temperature, jct.duration, jct.identity == null ? DEFAULT_ITEM_IDENTITY : jct.identity);
               }
            }

            try {
               manuallyWriteToJson(JsonFileName.consumableTemperature, JsonConfig.consumableTemperature, jsonDirectory);
            } catch (Exception var21) {
               logMerge(jsonFileName, var21);
            }
         }
      }

      jsonFileName = JsonFileName.consumableThirst.get();
      Map<String, List<JsonConsumableThirst>> jsonConsumableThirst = (Map)processJson(JsonFileName.consumableThirst, JsonConfig.consumableThirst, jsonDirectory, true);
      if (jsonConsumableThirst != null) {
         boolean migrate = true;

         label312:
         for(List<JsonConsumableThirst> mvalues : jsonConsumableThirst.values()) {
            Iterator var76 = mvalues.iterator();

            while(true) {
               if (!var76.hasNext()) {
                  continue label312;
               }

               JsonConsumableThirst value = (JsonConsumableThirst)var76.next();
               if (value.identity != null) {
                  break;
               }
            }

            migrate = false;
         }

         if (migrate) {
            SimpleDifficulty.logger.info("Attempting to migrate " + jsonFileName + " to new format");
            Map<String, List<JsonConsumableThirstMigrate>> migrateMap = new HashMap();

            try {
               migrateMap = (Map)processUncaughtJson(JsonFileName.consumableThirst_MIGRATE, migrateMap, jsonDirectory, true);

               for(Entry<String, List<JsonConsumableThirstMigrate>> registryEntry : migrateMap.entrySet()) {
                  for(JsonConsumableThirstMigrate jctm : (List<JsonConsumableThirstMigrate>)registryEntry.getValue()) {
                     JsonConfig.registerConsumableThirst((String)registryEntry.getKey(), jctm.amount, jctm.saturation, jctm.thirstyChance, new JsonItemIdentity(jctm.metadata));
                  }
               }

               try {
                  manuallyWriteToJson(JsonFileName.consumableThirst, JsonConfig.consumableThirst, jsonDirectory);
                  SimpleDifficulty.logger.info("Migrated " + jsonFileName);
               } catch (Exception var20) {
                  logMerge(jsonFileName, var20);
               }
            } catch (Exception var27) {
               SimpleDifficulty.logger.error("Migration failed, JSON of " + jsonFileName + " was likely already invalid");
               logMerge(jsonFileName, var27);
            }
         } else {
            for(Entry<String, List<JsonConsumableThirst>> entry : jsonConsumableThirst.entrySet()) {
               for(JsonConsumableThirst jct : (List<JsonConsumableThirst>)entry.getValue()) {
                  if (jct.identity != null) {
                     jct.identity.tryPopulateCompound();
                  }

                  JsonConfig.registerConsumableThirst((String)entry.getKey(), jct.amount, jct.saturation, jct.thirstyChance, jct.identity == null ? DEFAULT_ITEM_IDENTITY : jct.identity);
               }
            }

            try {
               manuallyWriteToJson(JsonFileName.consumableThirst, JsonConfig.consumableThirst, jsonDirectory);
            } catch (Exception var19) {
               logMerge(jsonFileName, var19);
            }
         }
      }

      jsonFileName = JsonFileName.dimensionTemperature.get();
      Map<String, JsonTemperature> jsonDimensionTemperature = (Map)processJson(JsonFileName.dimensionTemperature, JsonConfig.dimensionTemperature, jsonDirectory, true);
      if (jsonDimensionTemperature != null) {
         for(Entry<String, JsonTemperature> entry : jsonDimensionTemperature.entrySet()) {
            JsonConfig.registerDimensionTemperature((String)entry.getKey(), ((JsonTemperature)entry.getValue()).temperature);
         }

         try {
            manuallyWriteToJson(JsonFileName.dimensionTemperature, JsonConfig.dimensionTemperature, jsonDirectory);
         } catch (Exception var18) {
            logMerge(jsonFileName, var18);
         }
      }

      jsonFileName = JsonFileName.fluidTemperatures.get();
      Map<String, JsonTemperature> jsonFluidTemperatures = (Map)processJson(JsonFileName.fluidTemperatures, JsonConfig.fluidTemperatures, jsonDirectory, true);
      if (jsonFluidTemperatures != null) {
         for(Entry<String, JsonTemperature> entry : jsonFluidTemperatures.entrySet()) {
            JsonConfig.registerFluidTemperature((String)entry.getKey(), ((JsonTemperature)entry.getValue()).temperature);
         }

         try {
            manuallyWriteToJson(JsonFileName.fluidTemperatures, JsonConfig.fluidTemperatures, jsonDirectory);
         } catch (Exception var17) {
            logMerge(jsonFileName, var17);
         }
      }

      jsonFileName = JsonFileName.heldItemTemperatures.get();
      Map<String, List<JsonTemperatureIdentity>> jsonHeldItemTemperatures = (Map)processJson(JsonFileName.heldItemTemperatures, JsonConfig.heldItemTemperatures, jsonDirectory, true);
      if (jsonHeldItemTemperatures != null) {
         boolean migrate = true;

         label255:
         for(List<JsonTemperatureIdentity> mvalues : jsonHeldItemTemperatures.values()) {
            Iterator registryEntry = mvalues.iterator();

            while(true) {
               if (!registryEntry.hasNext()) {
                  continue label255;
               }

               JsonTemperatureIdentity value = (JsonTemperatureIdentity)registryEntry.next();
               if (value.identity != null) {
                  break;
               }
            }

            migrate = false;
         }

         if (migrate) {
            SimpleDifficulty.logger.info("Attempting to migrate " + jsonFileName + " to new format");
            Map<String, List<JsonTemperatureMetadataMigrate>> migrateMap = new HashMap();

            try {
               migrateMap = (Map)processUncaughtJson(JsonFileName.heldItemTemperatures_MIGRATE, migrateMap, jsonDirectory, true);

               for(Entry<String, List<JsonTemperatureMetadataMigrate>> registryEntry : migrateMap.entrySet()) {
                  for(JsonTemperatureMetadataMigrate jctm : (List<JsonTemperatureMetadataMigrate>)registryEntry.getValue()) {
                     JsonConfig.registerHeldItem((String)registryEntry.getKey(), jctm.temperature, new JsonItemIdentity(jctm.metadata));
                  }
               }

               try {
                  manuallyWriteToJson(JsonFileName.heldItemTemperatures, JsonConfig.heldItemTemperatures, jsonDirectory);
                  SimpleDifficulty.logger.info("Migrated " + jsonFileName);
               } catch (Exception var16) {
                  logMerge(jsonFileName, var16);
               }
            } catch (Exception var26) {
               SimpleDifficulty.logger.error("Migration failed, JSON of " + jsonFileName + " was likely already invalid");
               logMerge(jsonFileName, var26);
            }
         } else {
            for(Entry<String, List<JsonTemperatureIdentity>> entry : jsonHeldItemTemperatures.entrySet()) {
               for(JsonTemperatureIdentity jtm : (List<JsonTemperatureIdentity>)entry.getValue()) {
                  if (jtm.identity != null) {
                     jtm.identity.tryPopulateCompound();
                  }

                  JsonConfig.registerHeldItem((String)entry.getKey(), jtm.temperature, jtm.identity == null ? DEFAULT_ITEM_IDENTITY : jtm.identity);
               }
            }

            try {
               manuallyWriteToJson(JsonFileName.heldItemTemperatures, JsonConfig.heldItemTemperatures, jsonDirectory);
            } catch (Exception var15) {
               logMerge(jsonFileName, var15);
            }
         }
      }

      materialTemperature = (MaterialTemperature)processJson(JsonFileName.materialTemperature, materialTemperature, jsonDirectory, false);
   }

   private static void logMerge(String jsonFileName, Exception e) {
      SimpleDifficulty.logger.error("Error writing merged JSON File: " + jsonFileName, e);
      jsonErrors.add("config/simpledifficulty/" + jsonFileName + " failed to load!");
   }

   public static String manuallyExportAll() {
      VFile2 jsonDirectory = SimpleDifficulty.jsonDirectory;

      try {
         manuallyWriteToJson(JsonFileName.armorTemperatures, JsonConfig.armorTemperatures, jsonDirectory);
         manuallyWriteToJson(JsonFileName.blockTemperatures, JsonConfig.blockTemperatures, jsonDirectory);
         manuallyWriteToJson(JsonFileName.consumableTemperature, JsonConfig.consumableTemperature, jsonDirectory);
         manuallyWriteToJson(JsonFileName.consumableThirst, JsonConfig.consumableThirst, jsonDirectory);
         manuallyWriteToJson(JsonFileName.dimensionTemperature, JsonConfig.dimensionTemperature, jsonDirectory);
         manuallyWriteToJson(JsonFileName.fluidTemperatures, JsonConfig.fluidTemperatures, jsonDirectory);
         manuallyWriteToJson(JsonFileName.heldItemTemperatures, JsonConfig.heldItemTemperatures, jsonDirectory);
         manuallyWriteToJson(JsonFileName.materialTemperature, materialTemperature, jsonDirectory);
         return "Successfully exported SimpleDifficulty configuration to JSON";
      } catch (Exception var2) {
         SimpleDifficulty.logger.error("Export to JSON Failure Details", var2);
         return "Export to JSON FAILED! See log for details.";
      }
   }

   @Nullable
   public static <T> T processJson(JsonFileName jfn, T container, VFile2 jsonDirectory, boolean forMerging) {
      try {
         return (T)processUncaughtJson(jfn, container, jsonDirectory, forMerging);
      } catch (Exception var5) {
         SimpleDifficulty.logger.error("Error managing JSON File: " + jfn.get(), var5);
         jsonErrors.add("config/simpledifficulty/" + jfn.get() + " failed to load!");
         return (T)(forMerging ? null : container);
      }
   }

   @Nullable
   public static <T> T processUncaughtJson(JsonFileName jfn, T container, VFile2 jsonDirectory, boolean forMerging) throws Exception {
      String jsonFileName = jfn.get();
      VFile2 jsonFile = new VFile2(jsonDirectory, jsonFileName);
      if (jsonFile.exists()) {
         return (T)JsonConfigCodec.fromJson(jfn, (new JsonParser()).parse(jsonFile.getAllChars()));
      } else {
         Gson gson = buildNewGson();
         jsonFile.setAllChars(gson.toJson(JsonConfigCodec.toJson(jfn, container)));
         return (T)(forMerging ? null : container);
      }
   }

   private static <T> void manuallyWriteToJson(JsonFileName jfn, T container, VFile2 jsonDirectory) throws Exception {
      String jsonFileName = jfn.get();
      Gson gson = buildNewGson();
      VFile2 jsonFile = new VFile2(jsonDirectory, jsonFileName);
      jsonFile.setAllChars(gson.toJson(JsonConfigCodec.toJson(jfn, container)));

   }

   private static Gson buildNewGson() {
      return (new GsonBuilder()).setPrettyPrinting().create();
   }

   private static ExtraItem makeExtraItem(String itemName, String description) {
      ExtraItem extra = new ExtraItem(description, false);
      extraItems.put(itemName, extra);
      return extra;
   }
}
