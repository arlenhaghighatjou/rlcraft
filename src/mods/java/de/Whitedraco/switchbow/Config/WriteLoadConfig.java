package de.Whitedraco.switchbow.Config;

import java.io.StringReader;
import java.io.Reader;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class WriteLoadConfig {

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

   public static void readConfig(VFile2 filepath, int flag) throws IOException {
      if (flag == 3) {
         CraftingConfig.writeConfigCrafting();
      }

      if (flag != 3) {
         if (!filepath.exists()) {
            filepath.setAllChars("");
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

         Reader fr = new StringReader(filepath.getAllChars());
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
