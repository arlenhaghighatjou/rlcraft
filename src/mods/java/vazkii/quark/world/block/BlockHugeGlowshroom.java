package vazkii.quark.world.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockHugeMushroom.EnumType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.world.feature.UndergroundBiomes;

public class BlockHugeGlowshroom extends BlockMod implements IQuarkBlock {
   public static final PropertyEnum<EnumType> VARIANT = BlockHugeMushroom.VARIANT;

   public BlockHugeGlowshroom() {
      super("glowshroom_block", Material.WOOD, new String[0]);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumType.ALL_OUTSIDE));
      this.setHardness(0.2F);
      this.setSoundType(SoundType.WOOD);
      this.setLightLevel(0.9375F);
      this.setLightOpacity(0);
      this.setTickRandomly(true);
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      super.randomDisplayTick(stateIn, worldIn, pos, rand);
      if (rand.nextInt(10) == 0) {
         worldIn.spawnParticle(EnumParticleTypes.END_ROD, (double)((float)pos.getX() + rand.nextFloat()), (double)((float)pos.getY() + rand.nextFloat()), (double)((float)pos.getZ() + rand.nextFloat()), 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
      IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
      Block block = iblockstate.getBlock();
      return block == blockState.getBlock() ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public static boolean setInPosition(World worldIn, EaglercraftRandom rand, BlockPos position, boolean update) {
      Block block = UndergroundBiomes.glowshroom_block;
      int i = rand.nextInt(3) + 4;
      if (rand.nextInt(12) == 0) {
         i *= 2;
      }

      boolean canPlace = true;
      int flags = update ? 3 : 0;
      if (position.getY() >= 1 && position.getY() + i + 1 < 256) {
         for(int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
            int k = 3;
            if (j <= position.getY() + 3) {
               k = 0;
            }

            MutableBlockPos pos = new MutableBlockPos();

            for(int l = position.getX() - k; l <= position.getX() + k && canPlace; ++l) {
               for(int i1 = position.getZ() - k; i1 <= position.getZ() + k && canPlace; ++i1) {
                  if (j >= 0 && j < 256) {
                     IBlockState state = worldIn.getBlockState(pos.setPos(l, j, i1));
                     if (!state.getBlock().isAir(state, worldIn, pos) && !state.getBlock().isLeaves(state, worldIn, pos)) {
                        canPlace = false;
                     }
                  } else {
                     canPlace = false;
                  }
               }
            }
         }

         if (!canPlace) {
            return false;
         } else {
            Block block1 = worldIn.getBlockState(position.down()).getBlock();
            if (block1 != UndergroundBiomes.glowcelium) {
               return false;
            } else {
               int k2 = position.getY() + i - 3;

               for(int l2 = k2; l2 <= position.getY() + i; ++l2) {
                  int j3 = 1;
                  if (l2 < position.getY() + i) {
                     ++j3;
                  }

                  int k3 = position.getX() - j3;
                  int l3 = position.getX() + j3;
                  int j1 = position.getZ() - j3;
                  int k1 = position.getZ() + j3;

                  for(int l1 = k3; l1 <= l3; ++l1) {
                     for(int i2 = j1; i2 <= k1; ++i2) {
                        int j2 = 5;
                        if (l1 == k3) {
                           --j2;
                        } else if (l1 == l3) {
                           ++j2;
                        }

                        if (i2 == j1) {
                           j2 -= 3;
                        } else if (i2 == k1) {
                           j2 += 3;
                        }

                        EnumType type = EnumType.byMetadata(j2);
                        if (type == EnumType.CENTER && l2 < position.getY() + i) {
                           type = EnumType.ALL_INSIDE;
                        }

                        if (position.getY() >= position.getY() + i - 1 || type != EnumType.ALL_INSIDE) {
                           BlockPos blockpos = new BlockPos(l1, l2, i2);
                           IBlockState state = worldIn.getBlockState(blockpos);
                           if (state.getBlock().canBeReplacedByLeaves(state, worldIn, blockpos)) {
                              worldIn.setBlockState(blockpos, block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, type), flags);
                           }
                        }
                     }
                  }
               }

               for(int i3 = 0; i3 < i; ++i3) {
                  IBlockState iblockstate = worldIn.getBlockState(position.up(i3));
                  if (iblockstate.getBlock().canBeReplacedByLeaves(iblockstate, worldIn, position.up(i3))) {
                     worldIn.setBlockState(position.up(i3), block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, EnumType.STEM), flags);
                  }
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public int quantityDropped(EaglercraftRandom random) {
      return Math.max(0, random.nextInt(10) - 7);
   }

   @Nonnull
   public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      switch((EnumType)state.getValue(VARIANT)) {
      case ALL_STEM:
         return MapColor.CLOTH;
      case ALL_INSIDE:
         return MapColor.SAND;
      case STEM:
         return MapColor.SAND;
      default:
         return MapColor.WATER;
      }
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(UndergroundBiomes.glowshroom);
   }

   @Nonnull
   public ItemStack getItem(World worldIn, BlockPos pos, @Nonnull IBlockState state) {
      return new ItemStack(UndergroundBiomes.glowshroom);
   }

   @Nonnull
   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState();
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumType)state.getValue(VARIANT)).getMetadata();
   }

   @Nonnull
   public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
      switch(rot) {
      case CLOCKWISE_180:
         switch((EnumType)state.getValue(VARIANT)) {
         case STEM:
         default:
            break;
         case NORTH_WEST:
            return state.withProperty(VARIANT, EnumType.SOUTH_EAST);
         case NORTH:
            return state.withProperty(VARIANT, EnumType.SOUTH);
         case NORTH_EAST:
            return state.withProperty(VARIANT, EnumType.SOUTH_WEST);
         case WEST:
            return state.withProperty(VARIANT, EnumType.EAST);
         case EAST:
            return state.withProperty(VARIANT, EnumType.WEST);
         case SOUTH_WEST:
            return state.withProperty(VARIANT, EnumType.NORTH_EAST);
         case SOUTH:
            return state.withProperty(VARIANT, EnumType.NORTH);
         case SOUTH_EAST:
            return state.withProperty(VARIANT, EnumType.NORTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch((EnumType)state.getValue(VARIANT)) {
         case STEM:
         default:
            break;
         case NORTH_WEST:
            return state.withProperty(VARIANT, EnumType.SOUTH_WEST);
         case NORTH:
            return state.withProperty(VARIANT, EnumType.WEST);
         case NORTH_EAST:
            return state.withProperty(VARIANT, EnumType.NORTH_WEST);
         case WEST:
            return state.withProperty(VARIANT, EnumType.SOUTH);
         case EAST:
            return state.withProperty(VARIANT, EnumType.NORTH);
         case SOUTH_WEST:
            return state.withProperty(VARIANT, EnumType.SOUTH_EAST);
         case SOUTH:
            return state.withProperty(VARIANT, EnumType.EAST);
         case SOUTH_EAST:
            return state.withProperty(VARIANT, EnumType.NORTH_EAST);
         }
      case CLOCKWISE_90:
         switch((EnumType)state.getValue(VARIANT)) {
         case STEM:
         default:
            break;
         case NORTH_WEST:
            return state.withProperty(VARIANT, EnumType.NORTH_EAST);
         case NORTH:
            return state.withProperty(VARIANT, EnumType.EAST);
         case NORTH_EAST:
            return state.withProperty(VARIANT, EnumType.SOUTH_EAST);
         case WEST:
            return state.withProperty(VARIANT, EnumType.NORTH);
         case EAST:
            return state.withProperty(VARIANT, EnumType.SOUTH);
         case SOUTH_WEST:
            return state.withProperty(VARIANT, EnumType.NORTH_WEST);
         case SOUTH:
            return state.withProperty(VARIANT, EnumType.WEST);
         case SOUTH_EAST:
            return state.withProperty(VARIANT, EnumType.SOUTH_WEST);
         }
      default:
         return state;
      }
   }

   @Nonnull
   public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn) {
      EnumType mushroomType = (EnumType)state.getValue(VARIANT);
      switch(mirrorIn) {
      case LEFT_RIGHT:
         switch(mushroomType) {
         case NORTH_WEST:
            return state.withProperty(VARIANT, EnumType.SOUTH_WEST);
         case NORTH:
            return state.withProperty(VARIANT, EnumType.SOUTH);
         case NORTH_EAST:
            return state.withProperty(VARIANT, EnumType.SOUTH_EAST);
         case WEST:
         case EAST:
         default:
            return state;
         case SOUTH_WEST:
            return state.withProperty(VARIANT, EnumType.NORTH_WEST);
         case SOUTH:
            return state.withProperty(VARIANT, EnumType.NORTH);
         case SOUTH_EAST:
            return state.withProperty(VARIANT, EnumType.NORTH_EAST);
         }
      case FRONT_BACK:
         switch(mushroomType) {
         case NORTH_WEST:
            return state.withProperty(VARIANT, EnumType.NORTH_EAST);
         case NORTH:
         case SOUTH:
         default:
            break;
         case NORTH_EAST:
            return state.withProperty(VARIANT, EnumType.NORTH_WEST);
         case WEST:
            return state.withProperty(VARIANT, EnumType.EAST);
         case EAST:
            return state.withProperty(VARIANT, EnumType.WEST);
         case SOUTH_WEST:
            return state.withProperty(VARIANT, EnumType.SOUTH_EAST);
         case SOUTH_EAST:
            return state.withProperty(VARIANT, EnumType.SOUTH_WEST);
         }
      default:
         return state;
      }
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT});
   }

   public boolean rotateBlock(World world, @Nonnull BlockPos pos, @Nonnull EnumFacing axis) {
      IBlockState state = world.getBlockState(pos);
      world.setBlockState(pos, state.cycleProperty(VARIANT));
      return true;
   }
}
