package crafttweaker.mc1120.block;

import crafttweaker.api.block.BlockPatternOr;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.block.IBlockPattern;
import crafttweaker.api.data.IData;
import crafttweaker.api.liquid.ILiquidDefinition;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.item.MCItemStack;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

public class MCItemBlock implements IBlock {
   private final ItemStack item;

   public MCItemBlock(ItemStack item) {
      this.item = item;
   }

   public IBlockDefinition getDefinition() {
      return CraftTweakerMC.getBlockDefinition(Block.getBlockFromItem(this.item.getItem()));
   }

   public int getMeta() {
      return this.item.getItemDamage();
   }

   public IData getTileData() {
      return !this.item.isEmpty() && this.item.getTagCompound() != null ? CraftTweakerMC.getIData(this.item.getTagCompound()) : null;
   }

   public ILiquidDefinition getFluid() {
      return CraftTweakerMC.getILiquidDefinition(FluidRegistry.lookupFluidForBlock(Block.getBlockFromItem(this.item.getItem())));
   }

   public String getDisplayName() {
      return this.item.getDisplayName();
   }

   public List<IBlock> getBlocks() {
      return Collections.singletonList(this);
   }

   public boolean matches(IBlock block) {
      return this.getDefinition() == block.getDefinition() && (this.getMeta() == 32767 || this.getMeta() == block.getMeta()) && (this.getTileData() == null || block.getTileData() != null && block.getTileData().contains(this.getTileData()));
   }

   public IBlockPattern or(IBlockPattern pattern) {
      return new BlockPatternOr(this, pattern);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCItemBlock that = (MCItemBlock)o;
         return (new MCItemStack(this.item)).matches(new MCItemStack(that.item));
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.item.getItem(), this.item.getItemDamage(), this.item.getCount(), this.item.getTagCompound()});
   }
}
