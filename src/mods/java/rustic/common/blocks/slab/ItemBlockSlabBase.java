package rustic.common.blocks.slab;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.core.Rustic;

public class ItemBlockSlabBase extends ItemBlock {
   Block doubleSlab;
   CreativeTabs tab;

   public ItemBlockSlabBase(Block block, Block doubleSlab) {
      super(block);
      this.doubleSlab = doubleSlab;
      this.setRegistryName(block.getRegistryName() + "_item");
      this.tab = Rustic.decorTab;
      GameRegistry.findRegistry(Item.class).register(this);
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.block.getRegistryName().toString()));
   }

   public CreativeTabs getCreativeTab() {
      return this.tab;
   }

   public void decrementHeldStack(EntityPlayer player, ItemStack stack, EnumHand hand) {
      if (!player.capabilities.isCreativeMode) {
         stack.shrink(1);
         if (stack.getCount() == 0) {
            player.setItemStackToSlot(hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
         }
      }

   }

   public int getMetadata(int damage) {
      return damage;
   }

   public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack stack = playerIn.getHeldItem(hand);
      if (stack.getCount() == 0) {
         return EnumActionResult.FAIL;
      } else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
         return EnumActionResult.FAIL;
      } else {
         IBlockState iblockstate = worldIn.getBlockState(pos);
         if (iblockstate.getBlock() == this.getBlock()) {
            EnumBlockHalf enumblockhalf = (EnumBlockHalf)iblockstate.getValue(BlockSlab.HALF);
            if (side == EnumFacing.UP && enumblockhalf == EnumBlockHalf.BOTTOM || side == EnumFacing.DOWN && enumblockhalf == EnumBlockHalf.TOP) {
               IBlockState iblockstate1 = this.doubleSlab.getDefaultState();
               if (worldIn.checkNoEntityCollision(this.doubleSlab.getBoundingBox(iblockstate1, worldIn, pos)) && worldIn.setBlockState(pos, iblockstate1, 3)) {
                  worldIn.playSound((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), this.doubleSlab.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (this.doubleSlab.getSoundType().getVolume() + 1.0F) / 2.0F, this.doubleSlab.getSoundType().getPitch() * 0.8F, true);
                  stack.shrink(1);
               }

               return EnumActionResult.SUCCESS;
            }
         }

         return !this.tryPlace(stack, worldIn, pos.offset(side)) && super.onItemUse(playerIn, worldIn, pos, hand, side, hitX, hitY, hitZ) != EnumActionResult.SUCCESS ? EnumActionResult.FAIL : EnumActionResult.SUCCESS;
      }
   }

   @SideOnly(Side.CLIENT)
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos p_179222_2_, EnumFacing p_179222_3_, EntityPlayer p_179222_4_, ItemStack p_179222_5_) {
      BlockPos blockpos1 = p_179222_2_;
      IBlockState iblockstate = worldIn.getBlockState(p_179222_2_);
      if (iblockstate.getBlock() == this.getBlock()) {
         boolean flag = iblockstate.getValue(BlockSlab.HALF) == EnumBlockHalf.TOP;
         if (p_179222_3_ == EnumFacing.UP && !flag || p_179222_3_ == EnumFacing.DOWN && flag) {
            return true;
         }
      }

      p_179222_2_ = p_179222_2_.offset(p_179222_3_);
      IBlockState iblockstate1 = worldIn.getBlockState(p_179222_2_);
      return iblockstate1.getBlock() == this.getBlock() || super.canPlaceBlockOnSide(worldIn, blockpos1, p_179222_3_, p_179222_4_, p_179222_5_);
   }

   private boolean tryPlace(ItemStack p_180615_1_, World worldIn, BlockPos p_180615_3_) {
      IBlockState iblockstate = worldIn.getBlockState(p_180615_3_);
      if (iblockstate.getBlock() == this.getBlock()) {
         IBlockState iblockstate1 = this.doubleSlab.getDefaultState();
         if (worldIn.checkNoEntityCollision(this.doubleSlab.getBoundingBox(iblockstate1, worldIn, p_180615_3_)) && worldIn.setBlockState(p_180615_3_, iblockstate1, 3)) {
            worldIn.playSound((double)((float)p_180615_3_.getX() + 0.5F), (double)((float)p_180615_3_.getY() + 0.5F), (double)((float)p_180615_3_.getZ() + 0.5F), this.doubleSlab.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (this.doubleSlab.getSoundType().getVolume() + 1.0F) / 2.0F, this.doubleSlab.getSoundType().getPitch() * 0.8F, true);
            p_180615_1_.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }
}
