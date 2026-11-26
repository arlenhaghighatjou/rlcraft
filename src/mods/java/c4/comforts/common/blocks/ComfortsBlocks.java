package c4.comforts.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ComfortsBlocks {
   public static BlockSleepingBag[] SLEEPING_BAGS;
   public static BlockHammock[] HAMMOCKS;
   public static BlockRope ROPE;

   public ComfortsBlocks() {
   }

   public static void preInit() {
      SLEEPING_BAGS = new BlockSleepingBag[16];
      HAMMOCKS = new BlockHammock[16];

      for(int i = 0; i < 16; ++i) {
         EnumDyeColor color = EnumDyeColor.byMetadata(i);
         SLEEPING_BAGS[i] = new BlockSleepingBag(color);
         HAMMOCKS[i] = new BlockHammock(color);
      }

   }

   @SideOnly(Side.CLIENT)
   public static void initModels() {
      ROPE.initModel();
   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(Block.class, new ResourceLocation("comforts:rope"), v -> ROPE = (BlockRope)v);
   }
}
