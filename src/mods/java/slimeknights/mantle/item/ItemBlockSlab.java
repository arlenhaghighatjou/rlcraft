package slimeknights.mantle.item;

import java.util.Collection;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.block.EnumBlockSlab;

public class ItemBlockSlab<T extends Enum<T> & EnumBlock.IEnumMeta & IStringSerializable> extends ItemBlockMeta {
   public EnumBlockSlab<T> slab;

   public ItemBlockSlab(EnumBlockSlab<T> block) {
      super(block);
      this.slab = block;
   }

   @Nonnull
   public EnumActionResult onItemUse(@Nonnull EntityPlayer player, World world, @Nonnull BlockPos pos, EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack stack = player.getHeldItem(hand);
      if (stack.getCount() != 0 && player.canPlayerEdit(pos.offset(facing), facing, stack)) {
         if (this.tryPlace(player, stack, world, pos, facing)) {
            return EnumActionResult.SUCCESS;
         } else {
            return this.tryPlace(player, stack, world, pos.offset(facing), (EnumFacing)null) ? EnumActionResult.SUCCESS : super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
         }
      } else {
         return EnumActionResult.FAIL;
      }
   }

   @SideOnly(Side.CLIENT)
   public boolean canPlaceBlockOnSide(World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side, EntityPlayer player, @Nonnull ItemStack stack) {
      BlockPos oldPos = pos;
      Comparable<?> type = this.slab.getTypeForItem(stack);
      IBlockState state = world.getBlockState(pos);
      if (state.getBlock() == this.slab) {
         boolean flag = state.getValue(BlockSlab.HALF) == EnumBlockHalf.TOP;
         if ((side == EnumFacing.UP && !flag || side == EnumFacing.DOWN && flag) && type == state.getValue(this.slab.prop) && this.slab.getFullBlock(state) != null) {
            return true;
         }
      }

      pos = pos.offset(side);
      state = world.getBlockState(pos);
      return state.getBlock() == this.slab && type == state.getValue(this.slab.prop) || super.canPlaceBlockOnSide(world, oldPos, side, player, stack);
   }

   private boolean tryPlace(EntityPlayer player, ItemStack stack, World worldIn, BlockPos pos, EnumFacing facing) {
      IBlockState state = worldIn.getBlockState(pos);
      Comparable<?> type = this.slab.getTypeForItem(stack);
      if (state.getBlock() == this.slab) {
         EnumBlockHalf half = (EnumBlockHalf)state.getValue(BlockSlab.HALF);
         if (type == state.getValue(this.slab.prop) && (facing == null || facing == EnumFacing.UP && half == EnumBlockHalf.BOTTOM || facing == EnumFacing.DOWN && half == EnumBlockHalf.TOP)) {
            IBlockState fullBlock = this.slab.getFullBlock(state);
            if (fullBlock != null) {
               AxisAlignedBB axisalignedbb = fullBlock.getCollisionBoundingBox(worldIn, pos);
               if (axisalignedbb != Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb.offset(pos)) && worldIn.setBlockState(pos, fullBlock, 11)) {
                  SoundType soundtype = fullBlock.getBlock().getSoundType();
                  worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                  stack.shrink(1);
               }

               return true;
            }
         }
      }

      return false;
   }

   @SideOnly(Side.CLIENT)
   public void registerItemModels() {
      Item item = this;
      ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(this.block);

      for(T o : (Collection<T>)this.mappingProperty.getAllowedValues()) {
         int meta = this.block.getMetaFromState(this.block.getDefaultState().withProperty(this.mappingProperty, o));
         String name = this.mappingProperty.getName(o);
         ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(loc, "half=bottom," + this.mappingProperty.getName() + "=" + name));
      }

   }
}
