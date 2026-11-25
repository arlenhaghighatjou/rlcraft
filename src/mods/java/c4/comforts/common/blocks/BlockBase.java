package c4.comforts.common.blocks;

import c4.comforts.common.ConfigHandler;
import c4.comforts.common.tileentities.TileEntityHammock;
import c4.comforts.common.util.ComfortsUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider.WorldSleepResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBase extends BlockHorizontal {
   public static final PropertyBool OCCUPIED = PropertyBool.create("occupied");
   protected float explosivePower = 5.0F;
   protected String textOccupied;
   protected String textNoSleep;
   protected String textNotSafe;
   protected String textTooFar;
   protected int color;

   public BlockBase(String name, EnumDyeColor color) {
      super(Material.CLOTH);
      this.setDefaultState(this.blockState.getBaseState().withProperty(BlockBed.PART, EnumPartType.FOOT).withProperty(OCCUPIED, Boolean.valueOf(false)));
      this.setSoundType(SoundType.CLOTH);
      this.setHardness(0.2F);
      this.color = color.getMetadata();
      this.setRegistryName(name + "_" + color.getName());
      this.setUnlocalizedName("comforts." + name + "." + color.getName());
      this.setTextComponents(name);
      this.disableStats();
   }

   private void setTextComponents(String name) {
      this.textOccupied = "tile." + name + ".occupied";
      this.textNoSleep = "tile." + name + ".noSleep";
      this.textNotSafe = "tile." + name + ".notSafe";
      this.textTooFar = "tile." + name + ".tooFarAway";
   }

   protected void setExplosivePower(float power) {
      this.explosivePower = power;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote) {
         this.doSleep(worldIn, pos, state, playerIn);
      }

      return true;
   }

   public SleepResult doSleep(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn) {
      BlockPos originalPos = pos;
      if (state.getValue(BlockBed.PART) != EnumPartType.HEAD) {
         pos = pos.offset((EnumFacing)state.getValue(FACING));
         state = worldIn.getBlockState(pos);
         if (state.getBlock() != this) {
            return SleepResult.OTHER_PROBLEM;
         }
      }

      WorldSleepResult sleepResult = worldIn.provider.canSleepAt(playerIn, pos);
      if (sleepResult != WorldSleepResult.BED_EXPLODES) {
         if (sleepResult == WorldSleepResult.DENY) {
            return SleepResult.OTHER_PROBLEM;
         } else {
            if (state.getBlock() instanceof BlockHammock) {
               TileEntity tileentity = worldIn.getTileEntity(originalPos);
               TileEntity tileentity2 = worldIn.getTileEntity(pos);
               if (tileentity instanceof TileEntityHammock && tileentity2 instanceof TileEntityHammock && (((TileEntityHammock)tileentity).isOccupied() || ((TileEntityHammock)tileentity2).isOccupied())) {
                  playerIn.sendStatusMessage(new TextComponentTranslation(this.textOccupied, new Object[0]), true);
                  return SleepResult.OTHER_PROBLEM;
               }
            }

            if (((Boolean)state.getValue(OCCUPIED)).booleanValue()) {
               EntityPlayer entityplayer = this.getPlayerInComfort(worldIn, pos);
               if (entityplayer != null) {
                  playerIn.sendStatusMessage(new TextComponentTranslation(this.textOccupied, new Object[0]), true);
                  return SleepResult.OTHER_PROBLEM;
               }

               state = state.withProperty(OCCUPIED, Boolean.valueOf(false));
               worldIn.setBlockState(pos, state, 4);
            }

            SleepResult entityplayer$sleepresult = playerIn.trySleep(pos);
            if (entityplayer$sleepresult == SleepResult.OK) {
               state = state.withProperty(OCCUPIED, Boolean.valueOf(true));
               worldIn.setBlockState(pos, state, 4);
            } else if (entityplayer$sleepresult == SleepResult.NOT_POSSIBLE_NOW) {
               if (this instanceof BlockHammock && ConfigHandler.nightHammocks) {
                  playerIn.sendStatusMessage(new TextComponentTranslation(this.textNoSleep + "Custom", new Object[0]), true);
               } else {
                  playerIn.sendStatusMessage(new TextComponentTranslation(this.textNoSleep, new Object[0]), true);
               }
            } else if (entityplayer$sleepresult == SleepResult.NOT_SAFE) {
               playerIn.sendStatusMessage(new TextComponentTranslation(this.textNotSafe, new Object[0]), true);
            } else if (entityplayer$sleepresult == SleepResult.TOO_FAR_AWAY) {
               playerIn.sendStatusMessage(new TextComponentTranslation(this.textTooFar, new Object[0]), true);
            }

            return entityplayer$sleepresult;
         }
      } else {
         worldIn.setBlockToAir(pos);
         BlockPos blockpos = pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite());
         if (worldIn.getBlockState(blockpos).getBlock() == this) {
            worldIn.setBlockToAir(blockpos);
         }

         worldIn.newExplosion((Entity)null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, this.explosivePower, true, true);
         return SleepResult.OTHER_PROBLEM;
      }
   }

   public int damageDropped(IBlockState state) {
      return this.color;
   }

   @Nullable
   protected EntityPlayer getPlayerInComfort(World worldIn, BlockPos pos) {
      for(EntityPlayer entityplayer : worldIn.playerEntities) {
         if (entityplayer.isPlayerSleeping() && entityplayer.bedLocation != null && entityplayer.bedLocation.equals(pos)) {
            return entityplayer;
         }
      }

      return null;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public boolean hasCustomBreakingProgress(IBlockState state) {
      return true;
   }

   public void dropBlockAsItemWithChance(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, float chance, int fortune) {
      if (state.getValue(BlockBed.PART) == EnumPartType.HEAD) {
         super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
      }

   }

   @Nonnull
   public EnumPushReaction getMobilityFlag(IBlockState state) {
      return EnumPushReaction.DESTROY;
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (player.capabilities.isCreativeMode && state.getValue(BlockBed.PART) == EnumPartType.FOOT) {
         BlockPos blockpos = pos.offset((EnumFacing)state.getValue(FACING));
         if (worldIn.getBlockState(blockpos).getBlock() == this) {
            worldIn.setBlockToAir(blockpos);
         }
      }

   }

   @Nonnull
   public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      if (state.getValue(BlockBed.PART) == EnumPartType.FOOT) {
         IBlockState iblockstate = worldIn.getBlockState(pos.offset((EnumFacing)state.getValue(FACING)));
         if (iblockstate.getBlock() == this) {
            state = state.withProperty(OCCUPIED, iblockstate.getValue(OCCUPIED));
         }
      }

      return state;
   }

   @Nonnull
   public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   @Nonnull
   public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn) {
      return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      EnumFacing enumfacing = EnumFacing.getHorizontal(meta);
      return (meta & 8) > 0 ? this.getDefaultState().withProperty(BlockBed.PART, EnumPartType.HEAD).withProperty(FACING, enumfacing).withProperty(OCCUPIED, Boolean.valueOf((meta & 4) > 0)) : this.getDefaultState().withProperty(BlockBed.PART, EnumPartType.FOOT).withProperty(FACING, enumfacing);
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
      if (state.getValue(BlockBed.PART) == EnumPartType.HEAD) {
         i |= 8;
         if (((Boolean)state.getValue(OCCUPIED)).booleanValue()) {
            i |= 4;
         }
      }

      return i;
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, BlockBed.PART, OCCUPIED});
   }

   public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity player) {
      return true;
   }

   public void setBedOccupied(IBlockAccess world, @Nonnull BlockPos pos, EntityPlayer player, boolean occupied) {
      if (world instanceof World) {
         IBlockState state = world.getBlockState(pos);
         state = state.getBlock().getActualState(state, world, pos);
         state = state.withProperty(OCCUPIED, Boolean.valueOf(occupied));
         ((World)world).setBlockState(pos, state, 4);
      }

   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @SideOnly(Side.CLIENT)
   public IBlockColor colorMultiplier() {
      return (state, worldIn, pos, tintIndex) -> {
         return ComfortsUtil.getColor(this.color);
      };
   }
}
