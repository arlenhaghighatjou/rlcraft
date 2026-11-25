package c4.comforts.common.blocks;

import c4.comforts.common.ConfigHandler;
import c4.comforts.common.entities.EntityRest;
import c4.comforts.common.items.ComfortsItems;
import c4.comforts.common.tileentities.TileEntityHammock;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHammock extends BlockBase {
   protected static final AxisAlignedBB HAMMOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);

   public BlockHammock(EnumDyeColor color) {
      super("hammock", color);
      this.setExplosivePower(1.0F);
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (ConfigHandler.restHammocks && !playerIn.isSneaking()) {
         if (worldIn.isRemote) {
            return true;
         } else {
            if (state.getValue(BlockBed.PART) != EnumPartType.HEAD) {
               BlockPos blockpos = pos.offset((EnumFacing)state.getValue(FACING));
               IBlockState blockstate = worldIn.getBlockState(blockpos);
               if (blockstate.getBlock() != this) {
                  return true;
               }

               if (((Boolean)blockstate.getValue(OCCUPIED)).booleanValue()) {
                  EntityPlayer entityplayer = this.getPlayerInComfort(worldIn, blockpos);
                  if (entityplayer != null) {
                     playerIn.sendStatusMessage(new TextComponentTranslation(this.textOccupied, new Object[0]), true);
                     return true;
                  }

                  blockstate = blockstate.withProperty(OCCUPIED, Boolean.valueOf(false));
                  worldIn.setBlockState(blockpos, blockstate, 4);
               }
            }

            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityHammock) {
               TileEntityHammock tileentityhammock = (TileEntityHammock)tileentity;
               if (tileentityhammock.isOccupied()) {
                  playerIn.sendStatusMessage(new TextComponentTranslation(this.textOccupied, new Object[0]), true);
                  return true;
               }

               EntityRest rest = new EntityRest(worldIn, pos);
               worldIn.spawnEntity(rest);
               playerIn.startRiding(rest);
               tileentityhammock.setOccupied(true);
            }

            return true;
         }
      } else {
         return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
      }
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
      if (state.getValue(BlockBed.PART) == EnumPartType.FOOT) {
         IBlockState blockstate = worldIn.getBlockState(pos.offset(enumfacing.getOpposite()));
         if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() != this) {
            worldIn.setBlockToAir(pos);
            if (blockstate.getBlock() instanceof BlockRope && ((Boolean)blockstate.getValue(BlockRope.SUPPORTING)).booleanValue() && blockstate.getValue(FACING) == enumfacing) {
               worldIn.setBlockState(pos.offset(enumfacing.getOpposite()), blockstate.withProperty(BlockRope.SUPPORTING, Boolean.valueOf(false)));
            }
         } else if (!(blockstate.getBlock() instanceof BlockRope)) {
            worldIn.setBlockToAir(pos);
         }
      } else if (worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock() != this) {
         if (!worldIn.isRemote) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
         }

         worldIn.setBlockToAir(pos);
         IBlockState blockstate1 = worldIn.getBlockState(pos.offset(enumfacing));
         if (blockstate1.getBlock() instanceof BlockRope && ((Boolean)blockstate1.getValue(BlockRope.SUPPORTING)).booleanValue() && blockstate1.getValue(FACING) == enumfacing.getOpposite()) {
            worldIn.setBlockState(pos.offset(enumfacing), blockstate1.withProperty(BlockRope.SUPPORTING, Boolean.valueOf(false)));
         }
      } else if (!(worldIn.getBlockState(pos.offset(enumfacing)).getBlock() instanceof BlockRope)) {
         if (!worldIn.isRemote) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
         }

         worldIn.setBlockToAir(pos);
      }

   }

   @Nonnull
   public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
      return new ItemStack(ComfortsItems.HAMMOCK, 1, this.color);
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return (Item)(state.getValue(BlockBed.PART) == EnumPartType.FOOT ? Items.AIR : ComfortsItems.HAMMOCK);
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return HAMMOCK_AABB;
   }

   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
      return new TileEntityHammock();
   }
}
