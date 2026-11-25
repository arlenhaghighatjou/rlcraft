package com.endertech.minecraft.forge.world;

import com.endertech.common.CommonCollect;
import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.IHaveConfig;
import com.endertech.minecraft.forge.units.UnitId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum Dimensions {
   nether(-1, "netherrack"),
   overworld(0, "stone:*"),
   end(1, "end_stone");

   public final int ID;
   public final UnitId replaceableBlockId;
   public static final String DIM_LIST_SYNTAX = "Dimension is specified by its ID [-1 - Nether, 0 - Overworld, 1 - The End, etc.]\nEach ID must be on a separate line without any delimiters.\nIf the whitelist is set, the blacklist will be ignored.";

   private Dimensions(int dimensionId, String replaceableBlockId) {
      this.ID = dimensionId;
      this.replaceableBlockId = UnitId.from(replaceableBlockId);

      assert replaceableBlockId != null;

   }

   public static CommonCollect.BlackWhiteList<Integer> from(ForgeConfig config, String category, String comment) {
      category = IHaveConfig.expandClassCategory(Dimensions.class, category);
      if (config != null) {
         config.setCategoryComment(category, comment + "\n" + "Dimension is specified by its ID [-1 - Nether, 0 - Overworld, 1 - The End, etc.]\nEach ID must be on a separate line without any delimiters.\nIf the whitelist is set, the blacklist will be ignored.");
      }

      List<Integer> black = readFrom(config, category, "blackList", "");
      List<Integer> white = readFrom(config, category, "whiteList", "");
      return new CommonCollect.BlackWhiteList<Integer>(black, white);
   }

   public static List<Integer> readFrom(ForgeConfig config, String category, String key, String comment) {
      if (config == null) {
         return Collections.emptyList();
      } else {
         String[] strArray = config.getStrArray(category, key, new String[0], comment);
         List<Integer> list = new ArrayList();

         for(String str : strArray) {
            str = str.trim();
            if (!str.isEmpty()) {
               try {
                  Integer dim = Integer.valueOf(str);
                  list.add(dim);
               } catch (Exception var11) {
                  ForgeMain.instance.getLogger().error("Parsing dimension list from " + config, var11);
               }
            }
         }

         return list;
      }
   }
}
