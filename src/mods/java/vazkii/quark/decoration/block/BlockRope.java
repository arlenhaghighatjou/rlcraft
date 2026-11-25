package vazkii.quark.decoration.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.arl.item.ItemModBlock;
import vazkii.quark.automation.feature.PistonsMoveTEs;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.decoration.feature.Rope;

public class BlockRope extends BlockMod implements IQuarkBlock {
   private static final AxisAlignedBB AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);

   public BlockRope() {
      super("rope", Material.CLOTH, new String[0]);
      this.setHardness(0.5F);
      this.setSoundType(SoundType.CLOTH);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
   }

   public ItemBlock createItemBlock(ResourceLocation res) {
      return new ItemModBlock(this, res) {
         public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
            return world.getBlockState(pos).getBlock() == BlockRope.this;
         }
      };
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (hand == EnumHand.MAIN_HAND) {
         ItemStack stack = playerIn.getHeldItem(hand);
         if (stack.getItem() == Item.getItemFromBlock(this) && !playerIn.isSneaking()) {
            if (this.pullDown(worldIn, pos)) {
               if (!playerIn.isCreative()) {
                  stack.shrink(1);
               }

               worldIn.playSound((EntityPlayer)null, pos, this.blockSoundType.getPlaceSound(), SoundCategory.BLOCKS, 0.5F, 1.0F);
               return true;
            }
         } else {
            if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (EnumFacing)null)) {
               return FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, this.getBottomPos(worldIn, pos), EnumFacing.UP);
            }

            if (stack.getItem() == Items.GLASS_BOTTLE) {
               BlockPos bottomPos = this.getBottomPos(worldIn, pos);
               IBlockState stateAt = worldIn.getBlockState(bottomPos);
               if (stateAt.getMaterial() == Material.WATER) {
                  worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                  stack.shrink(1);
                  ItemStack bottleStack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
                  playerIn.addStat(StatList.getObjectUseStats(stack.getItem()));
                  if (stack.isEmpty()) {
                     playerIn.setHeldItem(hand, bottleStack);
                  } else if (!playerIn.inventory.addItemStackToInventory(bottleStack)) {
                     playerIn.dropItem(bottleStack, false);
                  }

                  return true;
               }

               return false;
            }

            if (this.pullUp(worldIn, pos)) {
               if (!playerIn.isCreative() && !playerIn.addItemStackToInventory(new ItemStack(this))) {
                  playerIn.dropItem(new ItemStack(this), false);
               }

               worldIn.playSound((EntityPlayer)null, pos, this.blockSoundType.getBreakSound(), SoundCategory.BLOCKS, 0.5F, 1.0F);
               return true;
            }
         }
      }

      return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
   }

   public boolean pullUp(World world, BlockPos pos) {
      BlockPos basePos = pos;

      while(true) {
         pos = pos.down();
         IBlockState state = world.getBlockState(pos);
         if (state.getBlock() != this) {
            break;
         }
      }

      BlockPos ropePos = pos.up();
      if (ropePos.equals(basePos)) {
         return false;
      } else {
         world.setBlockToAir(ropePos);
         this.moveBlock(world, pos, ropePos);
         return true;
      }
   }

   public boolean pullDown(World world, BlockPos pos) {
      boolean endRope = false;
      boolean wasAirAtEnd = false;

      while(true) {
         pos = pos.down();
         IBlockState state = world.getBlockState(pos);
         Block block = state.getBlock();
         if (block != this) {
            if (endRope) {
               boolean can = wasAirAtEnd || world.isAirBlock(pos) || block.isReplaceable(world, pos);
               if (can) {
                  BlockPos ropePos = pos.up();
                  this.moveBlock(world, ropePos, pos);
                  IBlockState ropePosState = world.getBlockState(ropePos);
                  Block ropePosBlock = ropePosState.getBlock();
                  if (world.isAirBlock(ropePos) || ropePosBlock.isReplaceable(world, ropePos)) {
                     world.setBlockState(ropePos, this.getDefaultState());
                     return true;
                  }
               }

               return false;
            }

            endRope = true;
            wasAirAtEnd = world.isAirBlock(pos);
         }
      }
   }

   private BlockPos getBottomPos(World worldIn, BlockPos pos) {
      IBlockState state;
      for(Block block = this; block == this; block = state.getBlock()) {
         pos = pos.down();
         state = worldIn.getBlockState(pos);
      }

      return pos;
   }

   private void moveBlock(World world, BlockPos srcPos, BlockPos dstPos) {
      IBlockState state = world.getBlockState(srcPos);
      Block block = state.getBlock();
      if (state.getBlockHardness(world, srcPos) != -1.0F && block.canPlaceBlockAt(world, dstPos) && !block.isAir(state, world, srcPos) && state.getMobilityFlag() == EnumPushReaction.NORMAL && block != Blocks.OBSIDIAN) {
         TileEntity tile = world.getTileEntity(srcPos);
         if (tile != null) {
            if (Rope.forceEnableMoveTEs) {
               if (PistonsMoveTEs.shouldMoveTE(state)) {
                  return;
               }
            } else if (PistonsMoveTEs.shouldMoveTE(true, state)) {
               return;
            }

            tile.invalidate();
         }

         world.setBlockToAir(srcPos);
         world.setBlockState(dstPos, state);
         if (tile != null) {
            tile.setPos(dstPos);
            TileEntity target = TileEntity.create(world, tile.writeToNBT(new NBTTagCompound()));
            if (target != null) {
               world.setTileEntity(dstPos, target);
               target.updateContainingBlockInfo();
               if (block instanceof BlockChest) {
                  ((BlockChest)block).checkForSurroundingChests(world, dstPos, state);
               }
            }
         }

         world.notifyNeighborsOfStateChange(dstPos, block, true);
      }
   }

   public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos) {
      BlockPos posUp = pos.up();
      IBlockState stateUp = worldIn.getBlockState(posUp);
      BlockFaceShape shape = stateUp.getBlockFaceShape(worldIn, posUp, EnumFacing.DOWN);
      return (shape == BlockFaceShape.SOLID || shape == BlockFaceShape.CENTER || shape == BlockFaceShape.CENTER_BIG) && !isExceptionBlockForAttaching(stateUp.getBlock());
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!this.canPlaceBlockAt(worldIn, pos)) {
         worldIn.playEvent(2001, pos, Block.getStateId(worldIn.getBlockState(pos)));
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

   }

   public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
      return true;
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 30;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 60;
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.CENTER;
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }
}
