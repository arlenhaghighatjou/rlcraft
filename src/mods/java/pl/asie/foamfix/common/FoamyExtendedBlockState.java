package pl.asie.foamfix.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Collection;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class FoamyExtendedBlockState extends FoamyBlockState implements IExtendedBlockState {
   private final ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties;
   private final boolean hasUnlistedProperty;

   public FoamyExtendedBlockState(PropertyValueMapper owner, Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties, boolean hasUnlistedProperty) {
      super(owner, block, properties);
      this.unlistedProperties = unlistedProperties;
      this.hasUnlistedProperty = hasUnlistedProperty;
   }

   public FoamyExtendedBlockState(PropertyValueMapper owner, Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties, boolean hasUnlistedProperty, int value) {
      super(owner, block, properties);
      this.unlistedProperties = unlistedProperties;
      this.hasUnlistedProperty = hasUnlistedProperty;
      this.value = value;
   }

   public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V propertyValue) {
      int newValue = this.owner.withPropertyValue(this.value, property, propertyValue);
      if (newValue == this.value) {
         return this;
      } else if (newValue < 0) {
         if (!this.getProperties().containsKey(property)) {
            throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.getBlock().getBlockState());
         } else if (!property.getAllowedValues().contains(propertyValue)) {
            throw new IllegalArgumentException("Cannot set property " + property + " to " + this.value + " on block " + Block.REGISTRY.getNameForObject(this.getBlock()) + ", it is not an allowed value");
         } else {
            throw new IllegalArgumentException("Cannot set property " + property + " because FoamFix could not find a mapping for it! Please reproduce without FoamFix first!");
         }
      } else {
         IBlockState state = this.owner.getPropertyByValue(newValue);
         if (state == null) {
            throw new IllegalArgumentException("Incomplete? list of values when trying to set property " + property + "! Please reproduce without FoamFix first! (Info: " + this.getBlock().getRegistryName() + " " + this.value + " -> " + newValue + ")");
         } else {
            return (IBlockState)(this.hasUnlistedProperty ? new FoamyExtendedBlockState(this.owner, this.getBlock(), state.getProperties(), this.unlistedProperties, true, newValue) : state);
         }
      }
   }

   public <V> IExtendedBlockState withProperty(IUnlistedProperty<V> property, V value) {
      if (!property.isValid(value)) {
         throw new IllegalArgumentException("Cannot set unlisted property " + property + " to " + value + " on block " + Block.REGISTRY.getNameForObject(this.getBlock()) + ", it is not an allowed value");
      } else {
         boolean hasOpt = false;
         boolean setValue = false;
         Builder<IUnlistedProperty<?>, Optional<?>> newMap = new Builder();
         UnmodifiableIterator var6 = this.unlistedProperties.entrySet().iterator();

         while(var6.hasNext()) {
            Entry<IUnlistedProperty<?>, Optional<?>> entry = (Entry)var6.next();
            IUnlistedProperty<?> entryKey = (IUnlistedProperty)entry.getKey();
            if (!setValue && entryKey.equals(property)) {
               newMap.put(entryKey, Optional.ofNullable(value));
               setValue = true;
            } else {
               Optional<?> entryValue = (Optional)entry.getValue();
               newMap.put(entryKey, entryValue);
               hasOpt |= entryValue.isPresent();
            }
         }

         if (!setValue) {
            throw new IllegalArgumentException("Cannot set unlisted property " + property + " as it does not exist in " + this.getBlock().getBlockState());
         } else if (value == null && !hasOpt) {
            return (IExtendedBlockState)this.owner.getPropertyByValue(this.value);
         } else {
            return new FoamyExtendedBlockState(this.owner, this.getBlock(), this.getProperties(), newMap.build(), true, this.value);
         }
      }
   }

   public Collection<IUnlistedProperty<?>> getUnlistedNames() {
      return this.unlistedProperties.keySet();
   }

   public <V> V getValue(IUnlistedProperty<V> property) {
      Optional optional = (Optional)this.unlistedProperties.get(property);
      if (optional == null) {
         throw new IllegalArgumentException("Cannot get unlisted property " + property + " as it does not exist in " + this.getBlock().getBlockState());
      } else {
         return (V)property.getType().cast(optional.orElse((Object)null));
      }
   }

   public ImmutableMap<IUnlistedProperty<?>, Optional<?>> getUnlistedProperties() {
      return this.unlistedProperties;
   }

   public IBlockState getClean() {
      return this.owner.getPropertyByValue(this.value);
   }
}
