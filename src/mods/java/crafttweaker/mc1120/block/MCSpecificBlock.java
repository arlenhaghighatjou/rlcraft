package crafttweaker.mc1120.block;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.block.BlockPatternOr;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.block.IBlockPattern;
import crafttweaker.api.data.IData;
import crafttweaker.api.liquid.ILiquidDefinition;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

public class MCSpecificBlock implements IBlock {
   private final Block block;
   private final int meta;

   public MCSpecificBlock(Block block, int meta) {
      this.block = block;
      this.meta = meta;
   }

   private static String getBlockId(Block block) {
      return ((ResourceLocation)Block.REGISTRY.getNameForObject(block)).toString();
   }

   public IBlockDefinition getDefinition() {
      return CraftTweakerMC.getBlockDefinition(this.block);
   }

   public int getMeta() {
      return this.meta;
   }

   public IData getTileData() {
      CraftTweakerAPI.logWarning("MCSpecificBlock::getTileData always returns null! If you want to access block NBT, please use `world.getBlock(x, y, z)` to get MCWorldBlock.");
      return null;
   }

   public ILiquidDefinition getFluid() {
      return CraftTweakerMC.getILiquidDefinition(FluidRegistry.lookupFluidForBlock(this.block));
   }

   public List<IBlock> getBlocks() {
      return Collections.singletonList(this);
   }

   public boolean matches(IBlock block) {
      return block.getDefinition() == this.getDefinition() && (this.meta == 32767 || block.getMeta() == this.meta);
   }

   public IBlockPattern or(IBlockPattern pattern) {
      return new BlockPatternOr(this, pattern);
   }

   public String getDisplayName() {
      return this.block.getLocalizedName();
   }

   public String toString() {
      return "<block:" + getBlockId(this.block) + ":" + (this.meta == 32767 ? 42 : this.meta) + ">";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCSpecificBlock that = (MCSpecificBlock)o;
         return this.meta == that.meta && Objects.equals(this.block, that.block);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.block, this.meta});
   }
}
