package de.Whitedraco.switchbow.Config;

import com.google.common.collect.Lists;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class BlackListConfig {
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static File path = new File("config/SwitchBow/EffectToMob Blacklist.cfg");
   private static final String love = "<Love Effect>";
   private static final String burial = "<Burial Effect>";
   private static final String frost = "<Frost Effect>";
   public static List<String> loveBlackList = Lists.newArrayList();
   public static List<String> burialBlackList = Lists.newArrayList();
   public static List<String> frostBlackList = Lists.newArrayList();

   public BlackListConfig() {
   }

   public static void writeConfigBlacklist() throws IOException {
      FileWriter fw = new FileWriter(path);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write("#To deactivate an spec. Effect to Entitys, write the Entity (for example minecraft:ender_dragon) under the Effect.#" + LINE_SEPARATOR);
      bw.write("<Love Effect>" + LINE_SEPARATOR);
      if (!loveBlackList.isEmpty() && loveBlackList.size() > 0) {
         for(String written : loveBlackList) {
            bw.write(written + LINE_SEPARATOR);
         }
      }

      bw.write(LINE_SEPARATOR + "<Burial Effect>" + LINE_SEPARATOR);
      if (!burialBlackList.isEmpty() && burialBlackList.size() > 0) {
         for(String written : burialBlackList) {
            bw.write(written + LINE_SEPARATOR);
         }
      }

      bw.write(LINE_SEPARATOR + "<Frost Effect>" + LINE_SEPARATOR);
      if (!frostBlackList.isEmpty() && frostBlackList.size() > 0) {
         for(String written : frostBlackList) {
            bw.write(written + LINE_SEPARATOR);
         }
      }

      bw.close();
      fw.close();
   }

   public static void readConfigBlacklist(List<String> readBlackList) {
      loveBlackList.clear();
      burialBlackList.clear();
      frostBlackList.clear();
      int flag = 0;

      for(String data : readBlackList) {
         if (!data.contains("#")) {
            if (data.equals("<Love Effect>")) {
               flag = 1;
            } else if (data.equals("<Burial Effect>")) {
               flag = 2;
            } else if (data.equals("<Frost Effect>")) {
               flag = 3;
            }

            if (flag == 1 && data.contains(":")) {
               loveBlackList.add(data);
            }

            if (flag == 2 && data.contains(":")) {
               burialBlackList.add(data);
            }

            if (flag == 3 && data.contains(":")) {
               frostBlackList.add(data);
            }
         }
      }

   }

   public static File getPath() {
      return path;
   }

   public static boolean isBlacklistLove(Entity entity) {
      return entity instanceof EntityPlayer ? false : loveBlackList.contains(getEntityRegName(entity));
   }

   public static boolean isBlacklistBurial(Entity entity) {
      return entity instanceof EntityPlayer ? false : burialBlackList.contains(getEntityRegName(entity));
   }

   public static boolean isBlacklistFrost(Entity entity) {
      return entity instanceof EntityPlayer ? false : frostBlackList.contains(getEntityRegName(entity));
   }

   public static String getEntityRegName(Entity eos) {
      return EntityRegistry.getEntry(eos.getClass()).getRegistryName().toString();
   }
}
