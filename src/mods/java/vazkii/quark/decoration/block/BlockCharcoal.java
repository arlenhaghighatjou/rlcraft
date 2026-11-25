package vazkii.quark.decoration.block;

import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMod;
import vazkii.arl.item.ItemModBlock;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockCharcoal extends BlockMod implements IQuarkBlock {
   public BlockCharcoal() {
      super("charcoal_block", Material.ROCK, new String[0]);
      this.setHardness(5.0F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public boolean isFireSource(@Nonnull World world, BlockPos pos, EnumFacing side) {
      return true;
   }

   public ItemBlock createItemBlock(ResourceLocation res) {
      return new ItemModBlock(this, res) {
         public int getItemBurnTime(ItemStack itemStack) {
            return 16000;
         }
      };
   }
}
