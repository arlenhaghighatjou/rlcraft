package org.orecruncher.dsurround.registry.blockstate;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.lib.BlockNameUtil;
import org.orecruncher.lib.BlockNameUtil.NameResult;

@SideOnly(Side.CLIENT)
public final class BlockStateMatcher {
   private static final Reference2ObjectOpenHashMap<IProperty<?>, Object> EMPTY = new Reference2ObjectOpenHashMap(0);
   public static final BlockStateMatcher AIR = create(Blocks.AIR.getDefaultState());
   protected final Block block;
   protected final Reference2ObjectOpenHashMap<IProperty<?>, Object> props;

   protected BlockStateMatcher(@Nonnull IBlockState state) {
      this.block = state.getBlock();
      this.props = this.getPropsFromState(state);
   }

   protected BlockStateMatcher(@Nonnull Block block) {
      this(block, EMPTY);
   }

   protected BlockStateMatcher(@Nonnull Block block, @Nonnull Reference2ObjectOpenHashMap<IProperty<?>, Object> props) {
      this.block = block;
      this.props = props;
   }

   @Nonnull
   public Block getBlock() {
      return this.block;
   }

   @Nonnull
   public List<IBlockState> asBlockStates() {
      return (List)this.block.getBlockState().getValidStates().stream().filter(this::matchProps).collect(Collectors.toList());
   }

   protected boolean matchProps(@Nonnull IBlockState state) {
      if (this.props.isEmpty()) {
         return true;
      } else {
         ObjectIterator var2 = this.props.reference2ObjectEntrySet().iterator();

         while(var2.hasNext()) {
            Entry<IProperty<?>, Object> entry = (Entry)var2.next();
            Object result = state.getValue((IProperty)entry.getKey());
            if (!entry.getValue().equals(result)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean hasSubtypes() {
      return this.block.getBlockState().getValidStates().size() > 1;
   }

   public int hashCode() {
      return this.block.hashCode();
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof BlockStateMatcher)) {
         return false;
      } else {
         BlockStateMatcher m = (BlockStateMatcher)obj;
         if (this.block != m.block) {
            return false;
         } else if (this.props.isEmpty() && m.props.isEmpty()) {
            return true;
         } else if (this.props.size() < m.props.size()) {
            return false;
         } else {
            ObjectIterator var3 = m.props.reference2ObjectEntrySet().iterator();

            while(var3.hasNext()) {
               Entry<IProperty<?>, Object> entry = (Entry)var3.next();
               Object v = this.props.get(entry.getKey());
               if (v == null || !v.equals(entry.getValue())) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Nullable
   protected Reference2ObjectOpenHashMap<IProperty<?>, Object> getPropsFromState(@Nonnull IBlockState state) {
      Reference2ObjectOpenHashMap<IProperty<?>, Object> result = new Reference2ObjectOpenHashMap(1);

      for(IProperty<?> prop : state.getPropertyKeys()) {
         Object o = state.getValue(prop);
         result.put(prop, o);
      }

      return result.size() == 0 ? EMPTY : result;
   }

   @Nonnull
   public static BlockStateMatcher asGeneric(@Nonnull IBlockState state) {
      return new BlockStateMatcher(state.getBlock());
   }

   @Nonnull
   public static BlockStateMatcher create(@Nonnull IBlockState state) {
      return new BlockStateMatcher(state);
   }

   @Nullable
   public static BlockStateMatcher create(@Nonnull String blockId) {
      return create(BlockNameUtil.parseBlockName(blockId));
   }

   @Nullable
   public static BlockStateMatcher create(@Nullable NameResult result) {
      if (result != null) {
         Block block = result.getBlock();
         if (block != null) {
            IBlockState defaultState = block.getDefaultState();
            BlockStateContainer container = block.getBlockState();
            if (container.getValidStates().size() == 1) {
               return new BlockStateMatcher(defaultState);
            }

            if (!result.hasProperties()) {
               return new BlockStateMatcher(block);
            }

            Map<String, String> properties = result.getProperties();
            if (properties == null) {
               properties = ImmutableMap.of();
            }

            Reference2ObjectOpenHashMap<IProperty<?>, Object> props = new Reference2ObjectOpenHashMap(properties.size());

            for(java.util.Map.Entry<String, String> entry : properties.entrySet()) {
               String s = (String)entry.getKey();
               IProperty<?> prop = container.getProperty(s);
               if (prop != null) {
                  Optional<?> optional = prop.parseValue((String)entry.getValue());
                  if (optional.isPresent()) {
                     props.put(prop, optional.get());
                  } else {
                     String allowed = getAllowedValues(block, s);
                     ModBase.log().warn("Property value '%s' for property '%s' not found for block '%s'", new Object[]{entry.getValue(), s, result.getBlockName()});
                     ModBase.log().warn("Allowed values: %s", new Object[]{allowed});
                  }
               } else {
                  ModBase.log().warn("Property %s not found for block %s", new Object[]{s, result.getBlockName()});
               }
            }

            if (props.size() > 0) {
               return new BlockStateMatcher(defaultState.getBlock(), props);
            }

            return new BlockStateMatcher(defaultState);
         }

         ModBase.log().warn("Unable to locate block '%s' in the Forge registry", new Object[]{result.getBlockName()});
      }

      return null;
   }

   @Nonnull
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(Block.REGISTRY.getNameForObject(this.block));
      if (!this.props.isEmpty()) {
         String txt = (String)this.props.reference2ObjectEntrySet().stream().map((e) -> {
            return ((IProperty)e.getKey()).getName() + "=" + getValue(this.block, ((IProperty)e.getKey()).getName(), e.getValue());
         }).collect(Collectors.joining(","));
         builder.append('[').append(txt).append(']');
      }

      return builder.toString();
   }

   protected static <T extends Comparable<T>> String getValue(@Nonnull Block block, @Nonnull String propName, @Nonnull Object val) {
      BlockStateContainer container = block.getBlockState();
      IProperty<T> prop = container.getProperty(propName);
      return prop != null ? prop.getName((Comparable)val) : null;
   }

   protected static <T extends Comparable<T>> String getAllowedValues(Block block, String propName) {
      BlockStateContainer container = block.getBlockState();
      IProperty<T> prop = container.getProperty(propName);
      List<String> result = new ArrayList();
      if (prop != null) {
         for(T v : prop.getAllowedValues()) {
            result.add(prop.getName(v));
         }
      }

      return String.join(",", result);
   }
}
