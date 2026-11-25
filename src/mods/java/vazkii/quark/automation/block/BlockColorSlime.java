package vazkii.quark.automation.block;

import java.util.Locale;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.api.INonSticky;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.tweaks.feature.SpringySlime;

public class BlockColorSlime extends BlockMetaVariants<BlockColorSlime.Variants> implements IQuarkBlock, INonSticky {
   public BlockColorSlime() {
      super("color_slime", Material.CLAY, BlockColorSlime.Variants.class);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setSoundType(SoundType.SLIME);
      this.setDefaultSlipperiness(0.8F);
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      if (entityIn.isSneaking()) {
         super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
      } else {
         entityIn.fall(fallDistance, 0.0F);
      }

   }

   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (ModuleLoader.isFeatureEnabled(SpringySlime.class)) {
         SpringySlime.collideWithSlimeBlock(pos, entityIn);
      }

   }

   public void onLanded(World worldIn, Entity entityIn) {
      if (ModuleLoader.isFeatureEnabled(SpringySlime.class)) {
         entityIn.motionY = 0.0D;
      } else if (entityIn.isSneaking()) {
         super.onLanded(worldIn, entityIn);
      } else if (entityIn.motionY < 0.0D) {
         entityIn.motionY = -entityIn.motionY;
         if (!(entityIn instanceof EntityLivingBase)) {
            entityIn.motionY *= 0.8D;
         }
      }

   }

   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
      if (Math.abs(entityIn.motionY) < 0.1D && !entityIn.isSneaking()) {
         double d0 = 0.4D + Math.abs(entityIn.motionY) * 0.2D;
         entityIn.motionX *= d0;
         entityIn.motionZ *= d0;
      }

      super.onEntityWalk(worldIn, pos, entityIn);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
      IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
      Block block = iblockstate.getBlock();
      return block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
   }

   public boolean isStickyBlock(IBlockState state) {
      return true;
   }

   public boolean canStickToBlock(World world, BlockPos pistonPos, BlockPos pos, BlockPos slimePos, IBlockState state, IBlockState slimeState, EnumFacing direction) {
      BlockColorSlime.Variants ourVariant = (BlockColorSlime.Variants)state.getValue(this.variantProp);
      Block block = slimeState.getBlock();
      if (block == this) {
         BlockColorSlime.Variants otherVariant = (BlockColorSlime.Variants)slimeState.getValue(this.variantProp);
         if (!ourVariant.sticksTo(otherVariant) && ourVariant != otherVariant) {
            return false;
         }
      }

      return block == Blocks.SLIME_BLOCK ? ourVariant.sticksToGreen : true;
   }

   public static enum Variants implements IStringSerializable {
      SLIME_RED(false, new int[]{3, 4}),
      SLIME_BLUE(false, new int[]{2, 3}),
      SLIME_CYAN(true, new int[]{1}),
      SLIME_MAGENTA(false, new int[]{0, 1}),
      SLIME_YELLOW(true, new int[]{0});

      public final boolean sticksToGreen;
      private final int[] sticksTo;

      private Variants(boolean sticksToGreen, int[] sticksTo) {
         this.sticksToGreen = sticksToGreen;
         this.sticksTo = sticksTo;
      }

      boolean sticksTo(BlockColorSlime.Variants otherVariant) {
         int ord = otherVariant.ordinal();

         for(int i : this.sticksTo) {
            if (i == ord) {
               return true;
            }
         }

         return false;
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
