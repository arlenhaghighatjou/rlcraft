package rustic.common.blocks;

import java.lang.reflect.Field;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.core.Rustic;

public class BlockChandelier extends BlockFallingBase {
   public static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);

   public BlockChandelier() {
      this("chandelier");
   }

   public BlockChandelier(String name) {
      this(Material.IRON, name, true);
   }

   public BlockChandelier(Material mat, String name, boolean register) {
      super(mat, name, register);
      this.setHardness(2.0F);
      this.setSoundType(SoundType.ANVIL);
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean isSideSolid(IBlockState state, IBlockAccess ba, BlockPos pos, EnumFacing side) {
      return side != EnumFacing.UP && side != EnumFacing.DOWN;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState blockState) {
      return false;
   }

   protected void onStartFalling(EntityFallingBlock fallingEntity) {
      fallingEntity.setHurtEntities(true);

      for(Field field : fallingEntity.getClass().getDeclaredFields()) {
         if (!field.getName().equals("fallHurtMax") && !field.getName().equals("fallHurtMax")) {
            if (field.getName().equals("fallHurtAmount") || field.getName().equals("fallHurtAmount")) {
               field.setAccessible(true);

               try {
                  field.setFloat(fallingEntity, 6.0F);
               } catch (IllegalAccessException | IllegalArgumentException var7) {
                  Rustic.logger.warn("Error setting falling chandelier damage with reflection", var7);
               }
            }
         } else {
            field.setAccessible(true);

            try {
               field.setInt(fallingEntity, 400);
            } catch (IllegalAccessException | IllegalArgumentException var8) {
               Rustic.logger.warn("Error setting falling chandelier maximum damage with reflection", var8);
            }
         }
      }

   }

   public void onEndFalling(World worldIn, BlockPos pos, IBlockState p_176502_3_, IBlockState p_176502_4_) {
      worldIn.playEvent(1031, pos, 0);
   }

   public void onBroken(World worldIn, BlockPos pos) {
      worldIn.playEvent(1029, pos, 0);
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (!worldIn.isRemote) {
         this.checkFallable(worldIn, pos);
      }

   }

   private boolean suspended(World worldIn, BlockPos pos) {
      IBlockState state = worldIn.getBlockState(pos.up());
      if (worldIn.isSideSolid(pos.up(), EnumFacing.DOWN, false)) {
         return true;
      } else {
         return state.getBlock() instanceof BlockRopeBase && state.getValue(BlockRopeBase.AXIS) == Axis.Y;
      }
   }

   private void checkFallable(World worldIn, BlockPos pos) {
      if ((worldIn.isAirBlock(pos.down()) || canFallThrough(worldIn.getBlockState(pos.down()))) && pos.getY() >= 0 && !this.suspended(worldIn, pos)) {
         if (!fallInstantly && worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
            if (!worldIn.isRemote) {
               EntityFallingBlock entityfallingblock = new EntityFallingBlock(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos));
               this.onStartFalling(entityfallingblock);
               worldIn.spawnEntity(entityfallingblock);
            }
         } else {
            IBlockState state = worldIn.getBlockState(pos);
            worldIn.setBlockToAir(pos);

            BlockPos blockpos;
            for(blockpos = pos.down(); (worldIn.isAirBlock(blockpos) || canFallThrough(worldIn.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down()) {
               ;
            }

            if (blockpos.getY() > 0) {
               worldIn.setBlockState(blockpos.up(), state);
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      if (side == EnumFacing.DOWN) {
         return BlockFaceShape.BOWL;
      } else {
         return side == EnumFacing.UP ? BlockFaceShape.CENTER : BlockFaceShape.SOLID;
      }
   }
}
