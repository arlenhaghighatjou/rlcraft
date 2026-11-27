package materialtweaker.handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import materialtweaker.core.MaterialTweaker;
import materialtweaker.util.ArmorAttributeEntry;
import materialtweaker.util.ToolAttributeEntry;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class CustomConfigHandler {
   private static VFile2 configFolder = null;
   private static VFile2 toolAttributeFile = null;
   private static VFile2 toolRepairFile = null;
   private static VFile2 armorAttributeFile = null;
   private static VFile2 armorRepairFile = null;
   private static VFile2 itemOverrideRepairFile = null;
   private static final Map<String, ToolAttributeEntry> toolAttributeMap = new HashMap();
   private static final Map<String, String[]> toolRepairMap = new HashMap();
   private static final Map<String, ArmorAttributeEntry> armorAttributeMap = new HashMap();
   private static final Map<String, String[]> armorRepairMap = new HashMap();
   private static final Map<String, String[]> itemOverrideRepairMap = new HashMap();

   public CustomConfigHandler() {
   }

   public static void forceInitializeLazyFiles() {
      getToolAttributes("foo");
      getToolRepairs("foo");
      getArmorAttributes("foo");
      getArmorRepairs("foo");
      getItemOverrideRepairs("foo");
   }

   @Nullable
   public static ToolAttributeEntry getToolAttributes(String name) {
      return toolAttributeFile == null && !initToolAttributes() ? null : (ToolAttributeEntry)toolAttributeMap.get(name);
   }

   @Nullable
   public static String[] getToolRepairs(String name) {
      return toolRepairFile == null && !initToolRepairs() ? null : (String[])toolRepairMap.get(name);
   }

   @Nullable
   public static ArmorAttributeEntry getArmorAttributes(String name) {
      return armorAttributeFile == null && !initArmorAttributes() ? null : (ArmorAttributeEntry)armorAttributeMap.get(name);
   }

   @Nullable
   public static String[] getArmorRepairs(String name) {
      return armorRepairFile == null && !initArmorRepairs() ? null : (String[])armorRepairMap.get(name);
   }

   @Nullable
   public static String[] getItemOverrideRepairs(String name) {
      return itemOverrideRepairFile == null && !initItemOverrideRepairs() ? null : (String[])itemOverrideRepairMap.get(name);
   }

   private static boolean initDirectory() {
      configFolder = new VFile2("config", "materialtweaker");
      return true;
   }

   private static boolean initToolAttributes() {
      if (configFolder == null && !initDirectory()) {
         return false;
      } else {
         toolAttributeFile = new VFile2(configFolder, "toolattributes.cfg");

         try {
            if (!toolAttributeFile.exists()) {
               toolAttributeFile.setAllChars("//List of Tool Materials and their attributes to be changed, one entry per line.\n//String materialName, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability\n//Example:\n//DIAMOND,5,2000,10.0,6.0,15\n");
            } else {
               for(String entry : (List<String>)Arrays.stream(toolAttributeFile.getAllChars().split("\\r\\n|\\r|\\n")).map(String::trim).filter((s) -> {
                  return !s.startsWith("//");
               }).filter((s) -> {
                  return !s.isEmpty();
               }).collect(Collectors.toList())) {
                  String[] entryArray = (String[])Arrays.stream(entry.split(",")).map(String::trim).toArray((x$0) -> {
                     return new String[x$0];
                  });
                  toolAttributeMap.put(entryArray[0], new ToolAttributeEntry(Integer.parseInt(entryArray[1]), Integer.parseInt(entryArray[2]), Float.parseFloat(entryArray[3]), Float.parseFloat(entryArray[4]), Integer.parseInt(entryArray[5])));
               }
            }

            return true;
         } catch (Exception var4) {
            MaterialTweaker.LOGGER.log(Level.ERROR, "materialtweaker: Failed to initialize tool attribute file: " + var4);
            return false;
         }
      }
   }

   private static boolean initToolRepairs() {
      if (configFolder == null && !initDirectory()) {
         return false;
      } else {
         toolRepairFile = new VFile2(configFolder, "toolrepairs.cfg");

         try {
            if (!toolRepairFile.exists()) {
               toolRepairFile.setAllChars("//List of Tool Materials and their replacement repair material, one entry per line.\n//String materialName, String repairItem, int repairItemMetadata (* for any)\n//Example:\n//IRON,minecraft:stone,*\n");
            } else {
               for(String entry : (List<String>)Arrays.stream(toolRepairFile.getAllChars().split("\\r\\n|\\r|\\n")).map(String::trim).filter((s) -> {
                  return !s.startsWith("//");
               }).filter((s) -> {
                  return !s.isEmpty();
               }).collect(Collectors.toList())) {
                  String[] entryArray = (String[])Arrays.stream(entry.split(",")).map(String::trim).toArray((x$0) -> {
                     return new String[x$0];
                  });
                  toolRepairMap.put(entryArray[0], new String[]{entryArray[1], entryArray[2]});
               }
            }

            return true;
         } catch (Exception var4) {
            MaterialTweaker.LOGGER.log(Level.ERROR, "materialtweaker: Failed to initialize tool repair file: " + var4);
            return false;
         }
      }
   }

   private static boolean initArmorAttributes() {
      if (configFolder == null && !initDirectory()) {
         return false;
      } else {
         armorAttributeFile = new VFile2(configFolder, "armorattributes.cfg");

         try {
            if (!armorAttributeFile.exists()) {
               armorAttributeFile.setAllChars("//List of Armor Materials and their attributes to be changed, one entry per line.\n//String materialName, int durabilityFactor, int damageReductionFeet, int damageReductionLegs, int damageReductionChest, int damageReductionHead, int enchantability, float toughness\n//Example:\n//LEATHER,5,1,3,5,4,5,1.0\n");
            } else {
               for(String entry : (List<String>)Arrays.stream(armorAttributeFile.getAllChars().split("\\r\\n|\\r|\\n")).map(String::trim).filter((s) -> {
                  return !s.startsWith("//");
               }).filter((s) -> {
                  return !s.isEmpty();
               }).collect(Collectors.toList())) {
                  String[] entryArray = (String[])Arrays.stream(entry.split(",")).map(String::trim).toArray((x$0) -> {
                     return new String[x$0];
                  });
                  armorAttributeMap.put(entryArray[0], new ArmorAttributeEntry(Integer.parseInt(entryArray[1]), Integer.parseInt(entryArray[2]), Integer.parseInt(entryArray[3]), Integer.parseInt(entryArray[4]), Integer.parseInt(entryArray[5]), Integer.parseInt(entryArray[6]), Float.parseFloat(entryArray[7])));
               }
            }

            return true;
         } catch (Exception var4) {
            MaterialTweaker.LOGGER.log(Level.ERROR, "materialtweaker: Failed to initialize armor attribute file: " + var4);
            return false;
         }
      }
   }

   private static boolean initArmorRepairs() {
      if (configFolder == null && !initDirectory()) {
         return false;
      } else {
         armorRepairFile = new VFile2(configFolder, "armorrepairs.cfg");

         try {
            if (!armorRepairFile.exists()) {
               armorRepairFile.setAllChars("//List of Armor Materials and their replacement repair material, one entry per line.\n//String materialName, String repairItem, int repairItemMetadata (* for any)\n//Example:\n//DIAMOND,minecraft:leather,*\n");
            } else {
               for(String entry : (List<String>)Arrays.stream(armorRepairFile.getAllChars().split("\\r\\n|\\r|\\n")).map(String::trim).filter((s) -> {
                  return !s.startsWith("//");
               }).filter((s) -> {
                  return !s.isEmpty();
               }).collect(Collectors.toList())) {
                  String[] entryArray = (String[])Arrays.stream(entry.split(",")).map(String::trim).toArray((x$0) -> {
                     return new String[x$0];
                  });
                  armorRepairMap.put(entryArray[0], new String[]{entryArray[1], entryArray[2]});
               }
            }

            return true;
         } catch (Exception var4) {
            MaterialTweaker.LOGGER.log(Level.ERROR, "materialtweaker: Failed to initialize armor repair file: " + var4);
            return false;
         }
      }
   }

   private static boolean initItemOverrideRepairs() {
      if (configFolder == null && !initDirectory()) {
         return false;
      } else {
         itemOverrideRepairFile = new VFile2(configFolder, "itemoverriderepairs.cfg");

         try {
            if (!itemOverrideRepairFile.exists()) {
               itemOverrideRepairFile.setAllChars("//List of individual items and their repair item, one entry per line.\n//String targetItemName, String repairItemName, int repairItemMetadata (* for any)\n//Example:\n//minecraft:wooden_sword,minecraft:iron_ingot,*\n");
            } else {
               for(String entry : (List<String>)Arrays.stream(itemOverrideRepairFile.getAllChars().split("\\r\\n|\\r|\\n")).map(String::trim).filter((s) -> {
                  return !s.startsWith("//");
               }).filter((s) -> {
                  return !s.isEmpty();
               }).collect(Collectors.toList())) {
                  String[] entryArray = (String[])Arrays.stream(entry.split(",")).map(String::trim).toArray((x$0) -> {
                     return new String[x$0];
                  });
                  itemOverrideRepairMap.put(entryArray[0], new String[]{entryArray[1], entryArray[2]});
               }
            }

            return true;
         } catch (Exception var4) {
            MaterialTweaker.LOGGER.log(Level.ERROR, "materialtweaker: Failed to initialize item override repair file: " + var4);
            return false;
         }
      }
   }
}
