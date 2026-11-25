package sereneseasons.item;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.api.ISSBlock;
import sereneseasons.util.BlockStateUtils;

public class ItemSSBlock extends ItemBlock {
   public ISSBlock tanBlock;

   public ItemSSBlock(Block block) {
      super(block);
      if (block instanceof ISSBlock) {
         this.tanBlock = (ISSBlock)block;
         this.setHasSubtypes(true);
      } else {
         throw new IllegalArgumentException("ItemBOPBlock must be created with a block implementing IBOPBlock");
      }
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         ImmutableSet<IBlockState> presets = BlockStateUtils.getBlockPresets(this.block);
         if (presets.isEmpty()) {
            subItems.add(new ItemStack(this.block, 1, 0));
         } else {
            UnmodifiableIterator var4 = presets.iterator();

            while(var4.hasNext()) {
               IBlockState state = (IBlockState)var4.next();
               subItems.add(new ItemStack(this.block, 1, this.block.getMetaFromState(state)));
            }
         }
      }

   }

   public int getMetadata(int metadata) {
      return metadata;
   }

   public String getUnlocalizedName(ItemStack stack) {
      ImmutableSet<IBlockState> presets = BlockStateUtils.getBlockPresets(this.block);
      if (presets.isEmpty()) {
         return super.getUnlocalizedName();
      } else {
         int meta = stack.getMetadata();
         IBlockState oldState = this.block.getStateFromMeta(meta);
         IBlockState newState = BlockStateUtils.getPresetState(oldState);
         return super.getUnlocalizedName() + "." + this.tanBlock.getStateName(newState);
      }
   }
}
