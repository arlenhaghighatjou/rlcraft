package de.Whitedraco.switchbow.Config;

import com.google.common.collect.Lists;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BlackListBurialBlockConfig {
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static File path = new File("config/SwitchBow/Burial-Blocks Blacklist.cfg");
   private static final String Blocks = "<Blocks>";
   public static List<String> burialBlocksBlackList = Lists.newArrayList();

   public BlackListBurialBlockConfig() {
   }

   public static void writeConfigBlacklist() throws IOException {
      FileWriter fw = new FileWriter(path);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write("#To deactivate an spec. Block, write the Block (for example minecraft:end_portal_frame) under Blocks.#" + LINE_SEPARATOR);
      bw.write("<Blocks>" + LINE_SEPARATOR);
      if (!burialBlocksBlackList.isEmpty() && burialBlocksBlackList.size() > 0) {
         for(String written : burialBlocksBlackList) {
            bw.write(written + LINE_SEPARATOR);
         }
      }

      bw.close();
      fw.close();
   }

   public static void readConfigBlacklist(List<String> readBlackList) {
      burialBlocksBlackList.clear();
      int flag = 0;

      for(String data : readBlackList) {
         if (!data.contains("#")) {
            if (data.equals("<Blocks>")) {
               flag = 1;
            }

            if (flag == 1 && data.contains(":")) {
               burialBlocksBlackList.add(data);
            }
         }
      }

   }

   public static File getPath() {
      return path;
   }

   public static boolean isBlacklistBlock(Block block) {
      return burialBlocksBlackList.contains(getBlockRegName(block));
   }

   public static String getBlockRegName(Block eos) {
      return ForgeRegistries.BLOCKS.getKey(eos).toString();
   }
}
