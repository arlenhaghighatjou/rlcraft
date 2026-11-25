package crafttweaker.mc1120.block;

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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidRegistry;

public class MCWorldBlock implements IBlock {
   private final IBlockAccess blocks;
   private final BlockPos pos;

   public MCWorldBlock(IBlockAccess blocks, int x, int y, int z) {
      this.blocks = blocks;
      this.pos = new BlockPos(x, y, z);
   }

   public IBlockDefinition getDefinition() {
      return CraftTweakerMC.getBlockDefinition(this.blocks.getBlockState(this.pos).getBlock());
   }

   public int getMeta() {
      return this.blocks.getBlockState(this.pos).getBlock().getMetaFromState(this.blocks.getBlockState(this.pos));
   }

   public IData getTileData() {
      TileEntity tileEntity = this.blocks.getTileEntity(this.pos);
      if (tileEntity == null) {
         return null;
      } else {
         NBTTagCompound nbt = new NBTTagCompound();
         tileEntity.writeToNBT(nbt);
         return CraftTweakerMC.getIData(nbt);
      }
   }

   public ILiquidDefinition getFluid() {
      return CraftTweakerMC.getILiquidDefinition(FluidRegistry.lookupFluidForBlock(this.blocks.getBlockState(this.pos).getBlock()));
   }

   public String getDisplayName() {
      Block block = this.blocks.getBlockState(this.pos).getBlock();
      Item item = Item.getItemFromBlock(block);
      return (new ItemStack(item, 1, this.getMeta())).getDisplayName();
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
         MCWorldBlock that = (MCWorldBlock)o;
         return Objects.equals(this.blocks, that.blocks) && Objects.equals(this.pos, that.pos);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.blocks, this.pos});
   }
}
