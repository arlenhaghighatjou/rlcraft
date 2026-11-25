package com.creativemd.creativecore.common.utils.sorting;

import com.creativemd.creativecore.common.utils.mc.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public abstract class BlockSelector {
   public BlockSelector() {
   }

   public abstract boolean is(Block var1, int var2);

   public static class BlockSelectorAnd extends BlockSelector {
      public final BlockSelector[] selectors;

      public BlockSelectorAnd(BlockSelector... selectors) {
         this.selectors = selectors;
      }

      public boolean is(Block block, int meta) {
         for(BlockSelector selector : this.selectors) {
            if (!selector.is(block, meta)) {
               return false;
            }
         }

         return true;
      }
   }

   public static class BlockSelectorBlock extends BlockSelector {
      public final Block block;

      public BlockSelectorBlock(Block block) {
         this.block = block;
      }

      public boolean is(Block block, int meta) {
         return this.block == block;
      }
   }

   public static class BlockSelectorBlocks extends BlockSelector {
      public Block[] blocks;

      public BlockSelectorBlocks(Block... blocks) {
         this.blocks = blocks;
      }

      public boolean is(Block block, int meta) {
         for(Block searchBlock : this.blocks) {
            if (searchBlock == block) {
               return true;
            }
         }

         return false;
      }
   }

   public static class BlockSelectorClass extends BlockSelector {
      public final Class<? extends Block>[] classes;

      public BlockSelectorClass(Class<? extends Block>... classes) {
         this.classes = classes;
      }

      public boolean is(Block block, int meta) {
         for(Class<? extends Block> clazz : this.classes) {
            if (clazz.isInstance(block)) {
               return true;
            }
         }

         return false;
      }
   }

   public static class BlockSelectorMaterial extends BlockSelector {
      public final Material material;

      public BlockSelectorMaterial(Material material) {
         this.material = material;
      }

      public boolean is(Block block, int meta) {
         return block.getStateFromMeta(meta).getMaterial() == this.material;
      }
   }

   public static class BlockSelectorOr extends BlockSelector {
      public final BlockSelector[] selectors;

      public BlockSelectorOr(BlockSelector... selectors) {
         this.selectors = selectors;
      }

      public boolean is(Block block, int meta) {
         for(BlockSelector selector : this.selectors) {
            if (selector.is(block, meta)) {
               return true;
            }
         }

         return false;
      }
   }

   public static class BlockSelectorProperty extends BlockSelector {
      public final IProperty[] properties;

      public BlockSelectorProperty(IProperty... properties) {
         this.properties = properties;
      }

      public boolean is(Block block, int meta) {
         IBlockState state = BlockUtils.getState(block, meta);

         for(IProperty iProperty : this.properties) {
            if (state.getPropertyKeys().contains(iProperty)) {
               return true;
            }
         }

         return false;
      }
   }

   public static class BlockSelectorState extends BlockSelector {
      public final Block block;
      public final int meta;

      public BlockSelectorState(Block block, int meta) {
         this.block = block;
         this.meta = meta;
      }

      public boolean is(Block block, int meta) {
         return this.block == block && this.meta == meta;
      }
   }
}
