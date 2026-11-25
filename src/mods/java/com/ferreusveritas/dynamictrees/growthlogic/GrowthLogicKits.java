package com.ferreusveritas.dynamictrees.growthlogic;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import net.minecraft.util.ResourceLocation;

public class GrowthLogicKits {
   public static final NullLogic nullLogic = new NullLogic();

   public static void preInit() {
      new GrowthLogicKits();
   }

   public GrowthLogicKits() {
      TreeRegistry.registerGrowthLogicKit(new ResourceLocation("dynamictrees", "null"), new NullLogic());
      TreeRegistry.registerGrowthLogicKit(new ResourceLocation("dynamictrees", "darkoak"), new DarkOakLogic());
      TreeRegistry.registerGrowthLogicKit(new ResourceLocation("dynamictrees", "conifer"), new ConiferLogic());
      TreeRegistry.registerGrowthLogicKit(new ResourceLocation("dynamictrees", "jungle"), new JungleLogic());
   }
}
