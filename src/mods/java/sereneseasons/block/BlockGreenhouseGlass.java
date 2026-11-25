package sereneseasons.block;

import java.util.Random;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.api.ISSBlock;
import sereneseasons.item.ItemSSBlock;

public class BlockGreenhouseGlass extends BlockBreakable implements ISSBlock {
   public Class<? extends ItemBlock> getItemClass() {
      return ItemSSBlock.class;
   }

   public IProperty[] getPresetProperties() {
      return new IProperty[0];
   }

   public IProperty[] getNonRenderingProperties() {
      return null;
   }

   public String getStateName(IBlockState state) {
      return "";
   }

   public BlockGreenhouseGlass() {
      super(Material.GLASS, false);
      this.setHardness(0.3F);
      this.setHarvestLevel("pickaxe", 0);
      this.setSoundType(SoundType.GLASS);
   }

   public int quantityDropped(Random random) {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   protected boolean canSilkHarvest() {
      return true;
   }
}
