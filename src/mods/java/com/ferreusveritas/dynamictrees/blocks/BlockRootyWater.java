package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.properties.UnlistedPropertyBool;
import com.ferreusveritas.dynamictrees.blocks.properties.UnlistedPropertyFloat;
import com.ferreusveritas.dynamictrees.tileentity.TileEntitySpecies;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRootyWater extends BlockRooty {
   protected final boolean isSmoothWaterInstalled;
   protected static final AxisAlignedBB WATER_ROOTS_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 1.0D, 0.9D);
   public static final UnlistedPropertyBool[] RENDER_SIDES = new UnlistedPropertyBool[]{new UnlistedPropertyBool("render_d"), new UnlistedPropertyBool("render_u"), new UnlistedPropertyBool("render_n"), new UnlistedPropertyBool("render_s"), new UnlistedPropertyBool("render_w"), new UnlistedPropertyBool("render_e")};
   public static final UnlistedPropertyFloat[] CORNER_HEIGHTS = new UnlistedPropertyFloat[]{new UnlistedPropertyFloat("level_nw"), new UnlistedPropertyFloat("level_sw"), new UnlistedPropertyFloat("level_se"), new UnlistedPropertyFloat("level_ne")};
   private static final Vec3d acceleration_modifier = new Vec3d(0.0D, 0.0D, 0.0D);

   public BlockRootyWater(boolean isTileEntity) {
      this(isTileEntity, "rootywater");
   }

   public BlockRootyWater(boolean isTileEntity, String name) {
      super(name, Material.WATER, isTileEntity);
      this.setSoundType(SoundType.PLANT);
      this.setDefaultState(super.getDefaultState());
      this.isSmoothWaterInstalled = Loader.isModLoaded("smoothwater");
   }

   protected BlockStateContainer createBlockState() {
      return new ExtendedBlockStateWater(this, new IProperty[]{LIFE}, new IUnlistedProperty[]{MimicProperty.MIMIC, RENDER_SIDES[0], RENDER_SIDES[1], RENDER_SIDES[2], RENDER_SIDES[3], RENDER_SIDES[4], RENDER_SIDES[5], CORNER_HEIGHTS[0], CORNER_HEIGHTS[1], CORNER_HEIGHTS[2], CORNER_HEIGHTS[3]});
   }

   public IBlockState getExtendedState(IBlockState state, IBlockAccess access, BlockPos pos) {
      int avgLvl = 0;
      int total = 0;

      for(EnumFacing dir : EnumFacing.HORIZONTALS) {
         IBlockState st = access.getBlockState(pos.offset(dir));
         if (st.getProperties().containsKey(BlockLiquid.LEVEL)) {
            avgLvl += ((Integer)st.getValue(BlockLiquid.LEVEL)).intValue();
            ++total;
         }
      }

      avgLvl = total == 0 ? 7 : avgLvl / total;
      if (!(state instanceof IExtendedBlockState)) {
         return state;
      } else {
         IExtendedBlockState extState = (IExtendedBlockState)state;

         for(EnumFacing dir : EnumFacing.VALUES) {
            extState = extState.withProperty(RENDER_SIDES[dir.ordinal()], Boolean.valueOf(this.shouldSideBeRendered(state, access, pos, dir)));
         }

         float defaultHeight = 1.0F - BlockLiquid.getLiquidHeightPercent(avgLvl);
         float c0 = this.getFluidHeight(access, pos, Material.WATER);
         float c1 = this.getFluidHeight(access, pos.south(), Material.WATER);
         float c2 = this.getFluidHeight(access, pos.east().south(), Material.WATER);
         float c3 = this.getFluidHeight(access, pos.east(), Material.WATER);
         if (this.isSmoothWaterInstalled) {
            if (c0 > 0.88F && c0 < 0.89F) {
               c0 = 0.875F;
            }

            if (c1 > 0.88F && c1 < 0.89F) {
               c1 = 0.875F;
            }

            if (c2 > 0.88F && c2 < 0.89F) {
               c2 = 0.875F;
            }

            if (c3 > 0.88F && c3 < 0.89F) {
               c3 = 0.875F;
            }
         }

         float avg = 0.0F;
         int i = 0;
         if (c0 > 0.0F) {
            avg += c0;
            ++i;
         }

         if (c1 > 0.0F) {
            avg += c1;
            ++i;
         }

         if (c2 > 0.0F) {
            avg += c2;
            ++i;
         }

         if (c3 > 0.0F) {
            avg += c3;
            ++i;
         }

         if (i > 0) {
            avg /= (float)i;
         }

         extState = extState.withProperty(CORNER_HEIGHTS[0], Float.valueOf(c0 < 0.0F ? avg : c0));
         extState = extState.withProperty(CORNER_HEIGHTS[1], Float.valueOf(c1 < 0.0F ? avg : c1));
         extState = extState.withProperty(CORNER_HEIGHTS[2], Float.valueOf(c2 < 0.0F ? avg : c2));
         extState = extState.withProperty(CORNER_HEIGHTS[3], Float.valueOf(c3 < 0.0F ? avg : c3));
         return extState;
      }
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      int y = fromPos.getY() - pos.getY();
      if (y < 1) {
         IBlockState newState = worldIn.getBlockState(fromPos);
         if (this.canFlowInto(worldIn, fromPos, newState)) {
            newState.getBlock().dropBlockAsItem(worldIn, pos, newState, 0);
            worldIn.setBlockState(fromPos, Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, Integer.valueOf(7)), 2);
         }
      }

   }

   private boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state) {
      Material material = state.getMaterial();
      return material != this.blockMaterial && material != Material.LAVA && !this.isBlocked(worldIn, pos, state);
   }

   private boolean isBlocked(World worldIn, BlockPos pos, IBlockState state) {
      Block block = state.getBlock();
      Material mat = state.getMaterial();
      if (!(block instanceof BlockDoor) && block != Blocks.STANDING_SIGN && block != Blocks.LADDER && block != Blocks.REEDS) {
         return mat == Material.PORTAL || mat == Material.STRUCTURE_VOID || mat.blocksMovement();
      } else {
         return true;
      }
   }

   private float getFluidHeight(IBlockAccess blockAccess, BlockPos blockPosIn, Material blockMaterial) {
      int i = 0;
      int w = 0;
      float f = 0.0F;

      for(int j = 0; j < 4; ++j) {
         BlockPos blockpos = blockPosIn.add(-(j & 1), 0, -(j >> 1 & 1));
         if (blockAccess.getBlockState(blockpos.up()).getMaterial() == blockMaterial) {
            return 1.0F;
         }

         IBlockState iblockstate = blockAccess.getBlockState(blockpos);
         Material material = iblockstate.getMaterial();
         if (material != blockMaterial) {
            if (!material.isSolid()) {
               ++f;
               ++i;
            }
         } else {
            int k = ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue();
            if (k >= 8 || k == 0) {
               f += BlockLiquid.getLiquidHeightPercent(k) * 10.0F;
               i += 10;
            }

            f += BlockLiquid.getLiquidHeightPercent(k);
            ++i;
         }

         if (iblockstate.isFullCube() || iblockstate.isOpaqueCube() || iblockstate.isFullBlock()) {
            ++w;
         }
      }

      if (w == 0 && i == 0) {
         return 0.0F;
      } else if (i == 0) {
         return -1.0F;
      } else {
         return 1.0F - f / (float)i;
      }
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
      super.harvestBlock(worldIn, player, pos, state, te, stack);
      worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      IBlockState upState = world.getBlockState(pos.up());
      return TreeHelper.isBranch(upState) ? new ItemStack(TreeHelper.getBranch(upState).getFamily().getDynamicBranch()) : ItemStack.EMPTY;
   }

   public IBlockState getMimic(IBlockAccess access, BlockPos pos) {
      return Blocks.WATER.getDefaultState();
   }

   public IBlockState getDecayBlockState(IBlockAccess access, BlockPos pos) {
      return Blocks.WATER.getDefaultState();
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Items.AIR;
   }

   public int quantityDropped(EaglercraftRandom random) {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   @SideOnly(Side.CLIENT)
   public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
      return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT_MIPPED;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      if (blockAccess.getBlockState(pos.offset(side)).getMaterial() == Material.WATER) {
         return false;
      } else if (side == EnumFacing.UP) {
         return true;
      } else {
         return !blockAccess.getBlockState(pos.offset(side)).doesSideBlockRendering(blockAccess, pos.offset(side), side.getOpposite());
      }
   }

   public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
      return world.getBlockState(pos.offset(face)).getMaterial() == Material.WATER;
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return this.hasTileEntity ? new TileEntitySpecies() : null;
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion) {
      return motion.add(acceleration_modifier);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return WATER_ROOTS_AABB;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   public int getRadiusForConnection(IBlockState state, IBlockAccess blockAccess, BlockPos pos, BlockBranch from, EnumFacing side, int fromRadius) {
      return 8;
   }

   @SideOnly(Side.CLIENT)
   public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
      int i = source.getCombinedLight(pos, 0);
      int j = source.getCombinedLight(pos.up(), 0);
      int k = i & 255;
      int l = j & 255;
      int i1 = i >> 16 & 255;
      int j1 = j >> 16 & 255;
      return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
   }

   @SideOnly(Side.CLIENT)
   public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks) {
      return super.getFogColor(world, pos, Blocks.WATER.getDefaultState(), entity, originalColor, partialTicks);
   }

   @SideOnly(Side.CLIENT)
   public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
      return true;
   }
}
