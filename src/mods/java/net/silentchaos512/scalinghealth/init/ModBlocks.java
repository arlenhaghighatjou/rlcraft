package net.silentchaos512.scalinghealth.init;

import java.util.Random;
import net.minecraft.item.ItemBlock;
import net.silentchaos512.lib.block.BlockOreSL;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.scalinghealth.config.Config;

public class ModBlocks {
   public static final BlockOreSL crystalOre = new BlockOreSL(ModItems.crystalShard, 2, 1, 1, 1, 5) {
      public int quantityDropped(Random random) {
         return Config.HEART_CRYSTAL_ORE_QUANTITY_DROPPED;
      }

      public float bonusAmount(int fortune, Random random) {
         return (float)(fortune - 1) * random.nextFloat() - 1.0F;
      }
   };

   public ModBlocks() {
   }

   public static void registerAll(SRegistry reg) {
      reg.registerBlock(crystalOre, "crystalore", new ItemBlock(crystalOre));
   }
}
