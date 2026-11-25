package knightminer.inspirations.tweaks.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.registries.IRegistryDelegate;

public abstract class BlockBlockCrop extends BlockCrops {
   private IRegistryDelegate<Block> block;
   private EnumPlantType type;
   private final AxisAlignedBB[] bounds;
   public static final PropertyInteger SMALL_AGE = PropertyInteger.create("age", 0, 6);

   public BlockBlockCrop(Block block, EnumPlantType type, AxisAlignedBB[] bounds) {
      this.block = block.delegate;
      this.bounds = bounds;
      this.type = type;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{this.getAgeProperty()});
   }

   protected PropertyInteger getAgeProperty() {
      return SMALL_AGE;
   }

   public IBlockState withAge(int age) {
      return age == this.getMaxAge() ? ((Block)this.block.get()).getDefaultState() : super.withAge(age);
   }

   public boolean isMaxAge(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return this.bounds[this.getAge(state)];
   }

   public abstract Item getSeed();

   protected Item getCrop() {
      return Item.getItemFromBlock((Block)this.block.get());
   }

   public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
      return this.type;
   }
}
