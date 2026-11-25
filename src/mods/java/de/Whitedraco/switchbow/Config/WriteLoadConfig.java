package de.Whitedraco.switchbow.Config;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class WriteLoadConfig {
   private static File Ordner = new File("config/SwitchBow");

   public WriteLoadConfig() {
   }

   public static void startConfigs() throws IOException {
      readConfig(SwitchBowConfig.getPath(), 1);
      readConfig(EnchantmentConfig.getPath(), 2);
      readConfig(CraftingConfig.getPath(), 3);
      readConfig(ArrowConfig.getPath(), 4);
      readConfig(BlackListConfig.getPath(), 5);
      readConfig(BlackListBurialBlockConfig.getPath(), 6);
   }

   public static void readConfig(File filepath, int flag) throws IOException {
      if (!Ordner.exists()) {
         Ordner.mkdirs();
      }

      if (flag == 3) {
         filepath.mkdirs();

         try {
            CraftingConfig.writeConfigCrafting();
         } catch (URISyntaxException var8) {
            var8.printStackTrace();
         }
      }

      if (flag != 3) {
         if (!filepath.exists()) {
            filepath.createNewFile();
            switch(flag) {
            case 1:
               SwitchBowConfig.writeConfigSwitchBow();
               break;
            case 2:
               EnchantmentConfig.writeConfigEnchantment();
            case 3:
            default:
               break;
            case 4:
               ArrowConfig.writeConfigArrow();
               break;
            case 5:
               BlackListConfig.writeConfigBlacklist();
               break;
            case 6:
               BlackListBurialBlockConfig.writeConfigBlacklist();
            }
         }

         FileReader fr = new FileReader(filepath);
         BufferedReader br = new BufferedReader(fr);
         String zeile = "";
         List<String[]> readData = Lists.newArrayList();
         List<String> readBlackList = Lists.newArrayList();
         List<String> readBlackBlockList = Lists.newArrayList();

         while((zeile = br.readLine()) != null) {
            if (zeile.contains("<") && zeile.contains(">") && flag != 5 && flag != 6) {
               readData.add(zeile.split("="));
            }

            if (flag == 5) {
               readBlackList.add(zeile);
            }

            if (flag == 6) {
               readBlackBlockList.add(zeile);
            }
         }

         br.close();
         fr.close();
         switch(flag) {
         case 1:
            SwitchBowConfig.readConfigSwitchBow(readData);
            SwitchBowConfig.writeConfigSwitchBow();
            break;
         case 2:
            EnchantmentConfig.readConfigEnchantment(readData);
            EnchantmentConfig.writeConfigEnchantment();
         case 3:
         default:
            break;
         case 4:
            ArrowConfig.readConfigArrow(readData);
            ArrowConfig.writeConfigArrow();
            break;
         case 5:
            BlackListConfig.readConfigBlacklist(readBlackList);
            BlackListConfig.writeConfigBlacklist();
            break;
         case 6:
            BlackListBurialBlockConfig.readConfigBlacklist(readBlackBlockList);
            BlackListBurialBlockConfig.writeConfigBlacklist();
         }
      }

   }

   public static void rewriteConfigSwitchBow() {
      try {
         SwitchBowConfig.writeConfigSwitchBow();
      } catch (IOException var1) {
         var1.printStackTrace();
      }

   }

   public static void reloadBlacklistConfig() {
      try {
         readConfig(BlackListConfig.getPath(), 5);
      } catch (IOException var1) {
         var1.printStackTrace();
      }

   }

   public static void reloadBlacklistBlocksConfig() {
      try {
         readConfig(BlackListBurialBlockConfig.getPath(), 6);
      } catch (IOException var1) {
         var1.printStackTrace();
      }

   }
}
