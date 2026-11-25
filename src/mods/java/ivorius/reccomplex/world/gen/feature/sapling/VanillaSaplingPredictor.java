package ivorius.reccomplex.world.gen.feature.sapling;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VanillaSaplingPredictor implements RCSaplingGenerator.Predictor {
   public VanillaSaplingPredictor() {
   }

   public static VanillaSaplingPredictor.Type vanillaType(World worldIn, BlockPos pos, IBlockState state) {
      if (state.getBlock() != Blocks.SAPLING) {
         return null;
      } else {
         int i = 0;
         int j = 0;
         boolean flag = false;
         switch((EnumType)state.getValue(BlockSapling.TYPE)) {
         case SPRUCE:
            label60:
            for(int var9 = 0; var9 >= -1; --var9) {
               for(int var14 = 0; var14 >= -1; --var14) {
                  if (isTwoByTwoOfType(worldIn, pos, var9, var14, EnumType.SPRUCE)) {
                     flag = true;
                     break label60;
                  }
               }
            }

            if (!flag) {
               i = 0;
               j = 0;
            }
         case BIRCH:
         case ACACIA:
         case OAK:
         default:
            break;
         case JUNGLE:
            label74:
            for(int var7 = 0; var7 >= -1; --var7) {
               for(int var12 = 0; var12 >= -1; --var12) {
                  if (isTwoByTwoOfType(worldIn, pos, var7, var12, EnumType.JUNGLE)) {
                     flag = true;
                     break label74;
                  }
               }
            }

            if (!flag) {
               i = 0;
               j = 0;
            }
            break;
         case DARK_OAK:
            label88:
            for(int var6 = 0; var6 >= -1; --var6) {
               for(int var11 = 0; var11 >= -1; --var11) {
                  if (isTwoByTwoOfType(worldIn, pos, var6, var11, EnumType.DARK_OAK)) {
                     flag = true;
                     break label88;
                  }
               }
            }

            if (!flag) {
               return VanillaSaplingPredictor.Type.NONE;
            }
         }

         return flag ? VanillaSaplingPredictor.Type.BIG : VanillaSaplingPredictor.Type.NORMAL;
      }
   }

   private static boolean isTwoByTwoOfType(World worldIn, BlockPos pos, int p_181624_3_, int p_181624_4_, EnumType type) {
      BlockSapling sapling = (BlockSapling)Blocks.SAPLING;
      return sapling.isTypeAt(worldIn, pos.add(p_181624_3_, 0, p_181624_4_), type) && sapling.isTypeAt(worldIn, pos.add(p_181624_3_ + 1, 0, p_181624_4_), type) && sapling.isTypeAt(worldIn, pos.add(p_181624_3_, 0, p_181624_4_ + 1), type) && sapling.isTypeAt(worldIn, pos.add(p_181624_3_ + 1, 0, p_181624_4_ + 1), type);
   }

   public int complexity(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      VanillaSaplingPredictor.Type type = vanillaType(worldIn, pos, state);
      return type == VanillaSaplingPredictor.Type.NORMAL ? 1 : (type == VanillaSaplingPredictor.Type.BIG ? 4 : -1);
   }

   static enum Type {
      NORMAL,
      BIG,
      NONE;

      private Type() {
      }
   }
}
