package com.ferreusveritas.dynamictrees.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class ExtendedBlockStateWater extends BlockStateContainer {
   private final ImmutableSet<IUnlistedProperty<?>> unlistedProperties;

   public ExtendedBlockStateWater(Block blockIn, IProperty<?>[] properties, IUnlistedProperty<?>[] unlistedProperties) {
      super(blockIn, properties, buildUnlistedMap(unlistedProperties));
      ImmutableSet.Builder<IUnlistedProperty<?>> builder = ImmutableSet.builder();

      for(IUnlistedProperty<?> property : unlistedProperties) {
         builder.add(property);
      }

      this.unlistedProperties = builder.build();
   }

   public Collection<IUnlistedProperty<?>> getUnlistedProperties() {
      return this.unlistedProperties;
   }

   private static ImmutableMap<IUnlistedProperty<?>, Optional<?>> buildUnlistedMap(IUnlistedProperty<?>[] unlistedProperties) {
      com.google.common.collect.ImmutableMap.Builder<IUnlistedProperty<?>, Optional<?>> builder = ImmutableMap.builder();

      for(IUnlistedProperty<?> p : unlistedProperties) {
         builder.put(p, Optional.empty());
      }

      return builder.build();
   }

   @Nonnull
   protected StateImplementation createState(@Nonnull Block block, @Nonnull ImmutableMap<IProperty<?>, Comparable<?>> properties, @Nullable ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
      return (StateImplementation)(unlistedProperties != null && !unlistedProperties.isEmpty() ? new ExtendedBlockStateWater.ExtendedStateImplementationWater(block, properties, unlistedProperties, (ImmutableTable)null, (IBlockState)null) : super.createState(block, properties, unlistedProperties));
   }

   protected static class ExtendedStateImplementationWater extends StateImplementation implements IExtendedBlockState {
      private final ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties;
      private final IBlockState cleanState;

      protected ExtendedStateImplementationWater(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties, @Nullable ImmutableTable<IProperty<?>, Comparable<?>, IBlockState> table, IBlockState clean) {
         super(block, properties, table);
         this.unlistedProperties = unlistedProperties;
         this.cleanState = (IBlockState)(clean == null ? this : clean);
      }

      @Nonnull
      public <T extends Comparable<T>, V extends T> IBlockState withProperty(@Nonnull IProperty<T> property, @Nonnull V value) {
         IBlockState clean = super.withProperty(property, value);
         if (clean == this.cleanState) {
            return this;
         } else {
            return (IBlockState)(this == this.cleanState ? clean : new ExtendedBlockStateWater.ExtendedStateImplementationWater(this.getBlock(), clean.getProperties(), this.unlistedProperties, ((StateImplementation)clean).getPropertyValueTable(), this.cleanState));
         }
      }

      public <V> IExtendedBlockState withProperty(IUnlistedProperty<V> property, @Nullable V value) {
         Optional<?> oldValue = (Optional)this.unlistedProperties.get(property);
         if (oldValue == null) {
            throw new IllegalArgumentException("Cannot set unlisted property " + property + " as it does not exist in " + this.getBlock().getBlockState());
         } else if (Objects.equals(oldValue.orElse(null), value)) {
            return this;
         } else if (!property.isValid(value)) {
            throw new IllegalArgumentException("Cannot set unlisted property " + property + " to " + value + " on block " + Block.REGISTRY.getNameForObject(this.getBlock()) + ", it is not an allowed value");
         } else {
            boolean clean = true;
            com.google.common.collect.ImmutableMap.Builder<IUnlistedProperty<?>, Optional<?>> builder = ImmutableMap.builder();

            IUnlistedProperty<?> key;
            Optional<?> newValue;
            for(UnmodifiableIterator var6 = this.unlistedProperties.entrySet().iterator(); var6.hasNext(); builder.put(key, newValue)) {
               Entry<IUnlistedProperty<?>, Optional<?>> entry = (Entry)var6.next();
               key = (IUnlistedProperty)entry.getKey();
               newValue = key.equals(property) ? Optional.ofNullable(value) : (Optional)entry.getValue();
               if (newValue.isPresent()) {
                  clean = false;
               }
            }

            if (clean) {
               return (IExtendedBlockState)this.cleanState;
            } else {
               return new ExtendedBlockStateWater.ExtendedStateImplementationWater(this.getBlock(), this.getProperties(), builder.build(), this.propertyValueTable, this.cleanState);
            }
         }
      }

      public Collection<IUnlistedProperty<?>> getUnlistedNames() {
         return this.unlistedProperties.keySet();
      }

      public <T extends Comparable<T>> T getValue(IProperty<T> property) {
         return (T)(property == BlockLiquid.LEVEL ? (Comparable)property.getValueClass().cast(Integer.valueOf(15)) : super.getValue(property));
      }

      @Nullable
      public <V> V getValue(IUnlistedProperty<V> property) {
         Optional<?> value = (Optional)this.unlistedProperties.get(property);
         if (value == null) {
            throw new IllegalArgumentException("Cannot get unlisted property " + property + " as it does not exist in " + this.getBlock().getBlockState());
         } else {
            return (V)property.getType().cast(value.orElse(null));
         }
      }

      public ImmutableMap<IUnlistedProperty<?>, Optional<?>> getUnlistedProperties() {
         return this.unlistedProperties;
      }

      public IBlockState getClean() {
         return this.cleanState;
      }
   }
}
