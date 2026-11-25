package crafttweaker.mc1120.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.block.BlockStateMatcherOr;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.block.IBlockStateMatcher;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.properties.IProperty;

public class BlockStateMatcher implements IBlockStateMatcher {
   private final IBlockState blockState;
   private final Map<String, List<String>> allowedProperties;

   public BlockStateMatcher(IBlockState blockState, Map<String, List<String>> allowedProperties) {
      this.blockState = blockState;
      this.allowedProperties = ImmutableMap.copyOf(allowedProperties);
   }

   public BlockStateMatcher(IBlockState blockState) {
      this(blockState, ImmutableMap.of());
   }

   public static IBlockStateMatcher create(IBlockState... blockStates) {
      if (blockStates == null) {
         throw new IllegalArgumentException("Cannot create matcher for null blockstate");
      } else {
         for(Object o : blockStates) {
            if (o == null) {
               throw new IllegalArgumentException("Cannot create matcher for null blockstate");
            }
         }

         if (blockStates.length == 1) {
            return new BlockStateMatcher(blockStates[0]);
         } else {
            return new BlockStateMatcherOr((IBlockStateMatcher[])Arrays.copyOf(blockStates, blockStates.length));
         }
      }
   }

   public boolean matches(IBlockState stateToMatch) {
      if (stateToMatch == null) {
         return false;
      } else if (((net.minecraft.block.state.IBlockState)this.blockState.getInternal()).getBlock().equals(((net.minecraft.block.state.IBlockState)stateToMatch.getInternal()).getBlock())) {
         UnmodifiableIterator var2 = ((net.minecraft.block.state.IBlockState)stateToMatch.getInternal()).getProperties().entrySet().iterator();

         while(var2.hasNext()) {
            Entry<IProperty<?>, Comparable<?>> entry = (Entry)var2.next();
            List<String> allowed = (List)this.allowedProperties.get(((IProperty)entry.getKey()).getName());
            if (allowed != null && !allowed.contains(((Comparable)entry.getValue()).toString()) && !allowed.contains("*")) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public IBlockStateMatcher or(IBlockStateMatcher matcher) {
      return new BlockStateMatcherOr(this, matcher);
   }

   public Collection<IBlockState> getMatchingBlockStates() {
      net.minecraft.block.state.IBlockState state = (net.minecraft.block.state.IBlockState)this.blockState.getInternal();
      return (Collection)state.getBlock().getBlockState().getValidStates().stream().map(MCBlockState::new).filter(this::matches).collect(Collectors.toList());
   }

   public IBlockStateMatcher allowValuesForProperty(String name, String... values) {
      CraftTweakerAPI.logWarning("IBlockStateMatcher#allowValuesForProperty is deprecated. Please use IBlockStateMatcher#withMatchedValuesForProperty instead.");
      return this.withMatchedValuesForProperty(name, values);
   }

   public IBlockStateMatcher withMatchedValuesForProperty(String name, String... values) {
      Map<String, List<String>> newProps = new HashMap();

      for(Entry<String, List<String>> entry : this.allowedProperties.entrySet()) {
         newProps.put(entry.getKey(), ImmutableList.copyOf((Collection)entry.getValue()));
      }

      List<String> newValues = ImmutableList.copyOf(values);
      if (newValues.contains("*") && newValues.size() > 1) {
         newProps.put(name, ImmutableList.of("*"));
      } else {
         newProps.put(name, newValues);
      }

      return new BlockStateMatcher(this.blockState, newProps);
   }

   public List<String> getMatchedValuesForProperty(String name) {
      return this.allowedProperties.containsKey(name) ? ImmutableList.copyOf((Collection)this.allowedProperties.get(name)) : ImmutableList.of("*");
   }

   public Map<String, List<String>> getMatchedProperties() {
      Map<String, List<String>> props = new HashMap();

      for(Entry<String, List<String>> entry : this.allowedProperties.entrySet()) {
         props.put(entry.getKey(), ImmutableList.copyOf((Collection)entry.getValue()));
      }

      return ImmutableMap.copyOf(props);
   }

   public String toString() {
      StringBuilder builder = new StringBuilder("Matcher (");
      net.minecraft.block.state.IBlockState state = (net.minecraft.block.state.IBlockState)this.blockState.getInternal();
      builder.append(state.getBlock().getRegistryName());
      if (!this.allowedProperties.isEmpty()) {
         builder.append(":");
         StringJoiner joiner = new StringJoiner(",");

         for(Entry<String, List<String>> entry : this.allowedProperties.entrySet()) {
            joiner.add((String)entry.getKey() + "=" + entry.getValue());
         }

         builder.append(joiner);
      }

      return builder.append(")").toString();
   }

   public boolean isCompound() {
      return false;
   }

   public String toCommandString() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.blockState.toCommandString());
      if (!this.allowedProperties.isEmpty()) {
         for(Entry<String, List<String>> allowedProperty : this.allowedProperties.entrySet()) {
            builder.append(".withMatchedValuesForProperty(");
            builder.append((String)allowedProperty.getKey()).append(", ");
            builder.append(((List)allowedProperty.getValue()).toString());
            builder.append(")");
         }
      }

      return builder.toString();
   }
}
