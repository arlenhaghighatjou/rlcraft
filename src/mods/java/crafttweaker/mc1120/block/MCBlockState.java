package crafttweaker.mc1120.block;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.block.BlockStateMatcherOr;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.block.IBlockStateMatcher;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class MCBlockState extends MCBlockProperties implements IBlockState {
   private final net.minecraft.block.state.IBlockState blockState;
   private IBlock block;

   public MCBlockState(net.minecraft.block.state.IBlockState blockState) {
      super(blockState);
      this.blockState = blockState;
      Block block = blockState.getBlock();
      int meta = block.getMetaFromState(blockState);
      this.block = new MCSpecificBlock(block, meta);
   }

   public IBlock getBlock() {
      return this.block;
   }

   public int getMeta() {
      return this.block.getMeta();
   }

   public boolean isReplaceable(IWorld world, IBlockPos blockPos) {
      return this.blockState.getBlock().isReplaceable((World)world.getInternal(), (BlockPos)blockPos.getInternal());
   }

   public int compare(IBlockState other) {
      int result = 0;
      if (!this.getInternal().equals(other.getInternal())) {
         if (this.blockState.getBlock().equals(((net.minecraft.block.state.IBlockState)other.getInternal()).getBlock())) {
            result = Integer.compare(this.getMeta(), other.getMeta());
         } else {
            int blockId = ((ForgeRegistry)ForgeRegistries.BLOCKS).getID(((net.minecraft.block.state.IBlockState)this.getInternal()).getBlock());
            int otherBlockId = ((ForgeRegistry)ForgeRegistries.BLOCKS).getID(((net.minecraft.block.state.IBlockState)other.getInternal()).getBlock());
            result = Integer.compare(blockId, otherBlockId);
         }
      }

      return result;
   }

   public IBlockState withProperty(String name, String value) {
      IProperty property = this.blockState.getBlock().getBlockState().getProperty(name);
      if (property == null) {
         CraftTweakerAPI.logWarning("Invalid property name");
      } else {
         Optional<? extends Comparable> propValue = property.parseValue(value);
         if (propValue.isPresent()) {
            return new MCBlockState(this.blockState.withProperty(property, (Comparable)propValue.get()));
         }

         CraftTweakerAPI.logWarning("Invalid property value");
      }

      return this;
   }

   public List<String> getPropertyNames() {
      List<String> props = new ArrayList();

      for(IProperty<?> prop : this.blockState.getPropertyKeys()) {
         props.add(prop.getName());
      }

      return ImmutableList.copyOf(props);
   }

   public String getPropertyValue(String name) {
      IProperty<?> prop = this.blockState.getBlock().getBlockState().getProperty(name);
      if (prop != null) {
         return this.blockState.getValue(prop).toString();
      } else {
         CraftTweakerAPI.logWarning("Invalid property name");
         return "";
      }
   }

   public List<String> getAllowedValuesForProperty(String name) {
      IProperty<?> prop = this.blockState.getBlock().getBlockState().getProperty(name);
      if (prop != null) {
         List<String> values = new ArrayList();
         prop.getAllowedValues().forEach((v) -> {
            values.add(v.toString());
         });
         return values;
      } else {
         CraftTweakerAPI.logWarning("Invalid property name");
         return ImmutableList.of();
      }
   }

   public Map<String, String> getProperties() {
      Map<String, String> props = new HashMap();
      UnmodifiableIterator var2 = this.blockState.getProperties().entrySet().iterator();

      while(var2.hasNext()) {
         Entry<IProperty<?>, Comparable<?>> entry = (Entry)var2.next();
         props.put(((IProperty)entry.getKey()).getName(), ((Comparable)entry.getValue()).toString());
      }

      return ImmutableMap.copyOf(props);
   }

   public IBlockStateMatcher matchBlock() {
      return new BlockStateMatcher(this);
   }

   public boolean matches(IBlockState other) {
      return this.compare(other) == 0;
   }

   public IBlockStateMatcher or(IBlockStateMatcher matcher) {
      return new BlockStateMatcherOr(this, matcher);
   }

   public Collection<IBlockState> getMatchingBlockStates() {
      return ImmutableList.of(this);
   }

   public IBlockStateMatcher allowValuesForProperty(String name, String... values) {
      CraftTweakerAPI.logWarning("IBlockStateMatcher#allowValuesForProperty is deprecated. Please use IBlockStateMatcher#withMatchedValuesForProperty instead.");
      return this.withMatchedValuesForProperty(name, values);
   }

   public IBlockStateMatcher withMatchedValuesForProperty(String name, String... values) {
      Map<String, List<String>> newProps = new HashMap();
      List<String> newValues = ImmutableList.copyOf(values);
      if (newValues.contains("*") && newValues.size() > 1) {
         newProps.put(name, ImmutableList.of("*"));
      } else {
         newProps.put(name, newValues);
      }

      return new BlockStateMatcher(this, newProps);
   }

   public List<String> getMatchedValuesForProperty(String name) {
      return ImmutableList.of(this.getPropertyValue(name));
   }

   public Map<String, List<String>> getMatchedProperties() {
      Map<String, List<String>> props = new HashMap();

      for(Entry<String, String> entry : this.getProperties().entrySet()) {
         props.put(entry.getKey(), ImmutableList.of(entry.getValue()));
      }

      return ImmutableMap.copyOf(props);
   }

   public String toString() {
      StringBuilder builder = new StringBuilder("<blockstate:");
      builder.append(this.blockState.getBlock().getRegistryName());
      if (!this.blockState.getProperties().isEmpty()) {
         builder.append(":");
         StringJoiner joiner = new StringJoiner(",");
         UnmodifiableIterator var3 = this.blockState.getProperties().entrySet().iterator();

         while(var3.hasNext()) {
            Entry<IProperty<?>, Comparable<?>> entry = (Entry)var3.next();
            joiner.add(((IProperty)entry.getKey()).getName() + "=" + entry.getValue());
         }

         builder.append(joiner);
      }

      return builder.append(">").toString();
   }

   public boolean isCompound() {
      return false;
   }

   public String toCommandString() {
      return this.toString();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         if (!super.equals(o)) {
            return false;
         } else {
            MCBlockState that = (MCBlockState)o;
            return Objects.equals(this.blockState, that.blockState) && Objects.equals(this.block, that.block);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{super.hashCode(), this.blockState, this.block});
   }
}
