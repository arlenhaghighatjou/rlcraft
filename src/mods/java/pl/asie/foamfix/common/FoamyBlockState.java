package pl.asie.foamfix.common;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;

public class FoamyBlockState extends StateImplementation {
   protected final PropertyValueMapper owner;
   protected int value;

   public FoamyBlockState(PropertyValueMapper owner, Block blockIn, ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn) {
      super(blockIn, propertiesIn);
      this.owner = owner;
   }

   public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
      IBlockState state = this.owner.withProperty(this.value, property, value);
      if (state == null) {
         Comparable<?> comparable = (Comparable)this.properties.get(property);
         if (comparable == null) {
            throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.getBlock().getBlockState());
         } else {
            throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on block " + Block.REGISTRY.getNameForObject(this.getBlock()) + ", it is not an allowed value");
         }
      } else {
         return state;
      }
   }

   public void buildPropertyValueTable(Map<Map<IProperty<?>, Comparable<?>>, StateImplementation> map) {
      this.value = this.owner.generateValue(this);
   }
}
