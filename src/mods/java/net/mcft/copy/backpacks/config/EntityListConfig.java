package net.mcft.copy.backpacks.config;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.mcft.copy.backpacks.WearableBackpacks;
import net.mcft.copy.backpacks.api.BackpackRegistry;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class EntityListConfig {
   private static VFile2 configFolder = null;
   private static VFile2 entityListFile = null;
   private static VFile2 backpackListFile = null;
   private static final Map<String, BackpackRegistry.RenderOptions> entityList = new HashMap();
   private static final Map<String, ArrayList<String[]>> backpackList = new HashMap();

   public EntityListConfig() {
   }

   @Nullable
   public static Map<String, BackpackRegistry.RenderOptions> getEntityList() {
      return entityListFile == null && !initEntityList() ? null : entityList;
   }

   @Nullable
   public static Map<String, ArrayList<String[]>> getBackpackList() {
      return backpackListFile == null && !initBackpackList() ? null : backpackList;
   }

   private static boolean initDirectory() {
      configFolder = new VFile2("config", "wearablebackpacks");
      return true;
   }

   private static boolean initEntityList() {
      if (configFolder == null && !initDirectory()) {
         return false;
      } else {
         entityListFile = new VFile2(configFolder, "wearablebackpacksentities.cfg");

         try {
            if (!entityListFile.exists()) {
               entityListFile.setAllChars("//List of Entity entries and their backpack render options, one entry per line.\n//String entityName, double Y, double Z, double rotation, double scale\n//Defaults:\nminecraft:zombie,0.0,2.5,0.0,0.8\nminecraft:skeleton,0.0,2.5,0.0,0.8\nminecraft:zombie_pigman,0.0,2.5,0.0,0.8\n");
            }

            for(String entry : (List<String>)Arrays.stream(entityListFile.getAllChars().split("\\r\\n|\\r|\\n")).map(String::trim).filter((s) -> {
               return !s.startsWith("//");
            }).filter((s) -> {
               return !s.isEmpty();
            }).collect(Collectors.toList())) {
               String[] entryArray = (String[])Arrays.stream(entry.split(",")).map(String::trim).toArray((x$0) -> {
                  return new String[x$0];
               });
               entityList.put(entryArray[0], new BackpackRegistry.RenderOptions(Double.parseDouble(entryArray[1]), Double.parseDouble(entryArray[2]), Double.parseDouble(entryArray[3]), Double.parseDouble(entryArray[4])));
            }

            return true;
         } catch (Exception var4) {
            WearableBackpacks.LOG.log(Level.ERROR, "wearablebackpacks: Failed to initialize entity list file: " + var4);
            return false;
         }
      }
   }

   private static boolean initBackpackList() {
      if (configFolder == null && !initDirectory()) {
         return false;
      } else {
         backpackListFile = new VFile2(configFolder, "wearablebackpacksentitybackpacks.cfg");

         try {
            if (!backpackListFile.exists()) {
               backpackListFile.setAllChars("//List of Entities and their backpack entries, one entry per line.\n//Entities in this file must first be declared in wearablebackpacksentities.cfg.\n//Chance is 1 in X of appearing on entity.\n//String entityName, boolean colored, int chance, String loottable\n//Defaults:\nminecraft:zombie,false,800,wearablebackpacks:backpack/default\nminecraft:zombie,true,8000,wearablebackpacks:backpack/default\nminecraft:skeleton,false,1200,wearablebackpacks:backpack/default\nminecraft:skeleton,true,12000,wearablebackpacks:backpack/default\nminecraft:zombie_pigman,true,1000,wearablebackpacks:backpack/default\n");
            }

            for(String entry : (List<String>)Arrays.stream(backpackListFile.getAllChars().split("\\r\\n|\\r|\\n")).map(String::trim).filter((s) -> {
               return !s.startsWith("//");
            }).filter((s) -> {
               return !s.isEmpty();
            }).collect(Collectors.toList())) {
               String[] entryArray = (String[])Arrays.stream(entry.split(",")).map(String::trim).toArray((x$0) -> {
                  return new String[x$0];
               });
               if (backpackList.get(entryArray[0]) != null) {
                  ((ArrayList)backpackList.get(entryArray[0])).add(new String[]{entryArray[1], entryArray[2], entryArray[3]});
               } else {
                  ArrayList<String[]> array = new ArrayList();
                  array.add(new String[]{entryArray[1], entryArray[2], entryArray[3]});
                  backpackList.put(entryArray[0], array);
               }
            }

            return true;
         } catch (Exception var5) {
            WearableBackpacks.LOG.log(Level.ERROR, "wearablebackpacks: Failed to initialize entity backpack list file: " + var5);
            return false;
         }
      }
   }
}
