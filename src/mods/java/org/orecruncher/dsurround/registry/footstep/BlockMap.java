package org.orecruncher.dsurround.registry.footstep;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.acoustics.AcousticRegistry;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.dsurround.registry.blockstate.BlockStateMatcher;
import org.orecruncher.lib.BlockNameUtil;
import org.orecruncher.lib.BlockNameUtil.NameResult;

@SideOnly(Side.CLIENT)
public class BlockMap {
   private final AcousticRegistry acousticsManager;
   private final BlockAcousticMap metaMap;
   private final Map<Substrate, BlockAcousticMap> substrateMap = new EnumMap(Substrate.class);
   private static final Map<String, List<BlockMap.MacroEntry>> macros = new Object2ObjectOpenHashMap();

   public BlockMap(@Nonnull AcousticRegistry manager) {
      this.acousticsManager = manager;
      this.metaMap = new BlockAcousticMap((bs) -> {
         return RegistryManager.FOOTSTEPS.resolve(bs);
      });
   }

   public boolean hasAcoustics(@Nonnull IBlockState state) {
      return this.metaMap.getBlockAcoustics(state) != AcousticRegistry.EMPTY;
   }

   @Nonnull
   public IAcoustic[] getBlockAcoustics(@Nonnull IBlockState state) {
      return this.getBlockAcoustics(state, (Substrate)null);
   }

   @Nonnull
   public IAcoustic[] getBlockAcoustics(@Nonnull IBlockState state, @Nullable Substrate substrate) {
      if (state == Blocks.AIR.getDefaultState()) {
         return AcousticRegistry.NOT_EMITTER;
      } else if (substrate != null) {
         BlockAcousticMap sub = (BlockAcousticMap)this.substrateMap.get(substrate);
         return sub != null ? sub.getBlockAcoustics(state) : AcousticRegistry.EMPTY;
      } else {
         return this.metaMap.getBlockAcoustics(state);
      }
   }

   private void put(@Nonnull BlockStateMatcher info, @Nullable String substrate, @Nonnull String value) {
      Substrate s = substrate != null ? Substrate.get(substrate) : null;
      IAcoustic[] acoustics = this.acousticsManager.compileAcoustics(value);
      if (s == null) {
         this.metaMap.put(info, acoustics);
      } else {
         BlockAcousticMap sub = (BlockAcousticMap)this.substrateMap.get(s);
         if (sub == null) {
            this.substrateMap.put(s, sub = new BlockAcousticMap());
         }

         sub.put(info, acoustics);
      }

   }

   private void register0(@Nonnull String key, @Nonnull String value) {
      NameResult name = BlockNameUtil.parseBlockName(key);
      if (name != null) {
         String blockName = name.getBlockName();
         Block block = name.getBlock();
         if (block == null) {
            ModBase.log().debug("Unable to locate block for blockMap '%s'", new Object[]{blockName});
         } else {
            BlockStateMatcher matcher = BlockStateMatcher.create(name);
            if (matcher != null) {
               String substrate = name.getExtras();
               this.put(matcher, substrate, value);
            } else {
               ModBase.log().warn("Unable to create matcher: key '%s', value '%s'", new Object[]{key, value});
            }
         }
      } else {
         ModBase.log().warn("Malformed key in blockMap '%s'", new Object[]{key});
      }

   }

   public void register(@Nonnull String key, @Nonnull String value) {
      if (value.startsWith("#")) {
         List<BlockMap.MacroEntry> macro = (List)macros.get(value);
         if (macro != null) {
            macro.stream().map((m) -> {
               return m.expand(key);
            }).forEach((t) -> {
               this.register0((String)t.getFirst(), (String)t.getSecond());
            });
         } else {
            ModBase.log().debug("Unknown macro '%s'", new Object[]{value});
         }
      } else {
         this.register0(key, value);
      }

   }

   @Nonnull
   private static String combine(@Nonnull IAcoustic[] acoustics) {
      return (String)Arrays.stream(acoustics).map(IAcoustic::getName).collect(Collectors.joining(","));
   }

   public void collectData(@Nonnull IBlockState state, @Nonnull List<String> data) {
      IAcoustic[] temp = this.getBlockAcoustics(state);
      if (temp != AcousticRegistry.EMPTY) {
         data.add(combine(temp));
      }

      for(Entry<Substrate, BlockAcousticMap> e : this.substrateMap.entrySet()) {
         IAcoustic[] acoustics = ((BlockAcousticMap)e.getValue()).getBlockAcoustics(state);
         if (acoustics != AcousticRegistry.EMPTY) {
            data.add(e.getKey() + ":" + combine(acoustics));
         }
      }

   }

   public void clear() {
      this.metaMap.clear();
      this.substrateMap.clear();
   }

   static {
      BlockMap.MacroEntry MESSY = new BlockMap.MacroEntry("messy", "MESSY_GROUND");
      List<BlockMap.MacroEntry> entries = new ArrayList();
      entries.add(new BlockMap.MacroEntry((String)null, "NOT_EMITTER"));
      entries.add(MESSY);
      entries.add(new BlockMap.MacroEntry("foliage", "straw"));
      macros.put("#sapling", entries);
      macros.put("#reed", entries);
      entries = new ArrayList();
      entries.add(new BlockMap.MacroEntry((String)null, "leaves"));
      entries.add(MESSY);
      entries.add(new BlockMap.MacroEntry("foliage", "brush"));
      macros.put("#plant", entries);
      entries = new ArrayList();
      entries.add(new BlockMap.MacroEntry((String)null, "leaves"));
      entries.add(MESSY);
      entries.add(new BlockMap.MacroEntry("foliage", "brush_straw_transition"));
      macros.put("#bush", entries);
      entries = new ArrayList();
      entries.add(new BlockMap.MacroEntry((String)null, "NOT_EMITTER"));
      entries.add(new BlockMap.MacroEntry("bigger", "bluntwood"));
      macros.put("#fence", entries);
      entries = new ArrayList();
      entries.add(new BlockMap.MacroEntry((String)null, "straw"));
      entries.add(MESSY);
      entries.add(new BlockMap.MacroEntry("foliage", "straw"));
      macros.put("#vine", entries);
      entries = new ArrayList();
      entries.add(new BlockMap.MacroEntry((String)null, "NOT_EMITTER"));
      entries.add(new BlockMap.MacroEntry("carpet", "rug"));
      macros.put("#moss", entries);
      entries = new ArrayList();
      entries.add(new BlockMap.MacroEntry((String)null, "NOT_EMITTER"));
      entries.add(MESSY);
      entries.add(new BlockMap.MacroEntry("age", "0", "foliage", "NOT_EMITTER"));
      entries.add(new BlockMap.MacroEntry("age", "1", "foliage", "NOT_EMITTER"));
      entries.add(new BlockMap.MacroEntry("age", "2", "foliage", "brush"));
      entries.add(new BlockMap.MacroEntry("age", "3", "foliage", "brush"));
      entries.add(new BlockMap.MacroEntry("age", "4", "foliage", "brush_straw_transition"));
      entries.add(new BlockMap.MacroEntry("age", "5", "foliage", "brush_straw_transition"));
      entries.add(new BlockMap.MacroEntry("age", "6", "foliage", "straw"));
      entries.add(new BlockMap.MacroEntry("age", "7", "foliage", "straw"));
      macros.put("#wheat", entries);
      entries = new ArrayList();
      entries.add(new BlockMap.MacroEntry((String)null, "NOT_EMITTER"));
      entries.add(MESSY);
      entries.add(new BlockMap.MacroEntry("age", "0", "foliage", "NOT_EMITTER"));
      entries.add(new BlockMap.MacroEntry("age", "1", "foliage", "NOT_EMITTER"));
      entries.add(new BlockMap.MacroEntry("age", "2", "foliage", "NOT_EMITTER"));
      entries.add(new BlockMap.MacroEntry("age", "3", "foliage", "NOT_EMITTER"));
      entries.add(new BlockMap.MacroEntry("age", "4", "foliage", "brush"));
      entries.add(new BlockMap.MacroEntry("age", "5", "foliage", "brush"));
      entries.add(new BlockMap.MacroEntry("age", "6", "foliage", "brush"));
      entries.add(new BlockMap.MacroEntry("age", "7", "foliage", "brush"));
      macros.put("#crop", entries);
      entries = new ArrayList();
      entries.add(new BlockMap.MacroEntry((String)null, "NOT_EMITTER"));
      entries.add(MESSY);
      entries.add(new BlockMap.MacroEntry("age", "0", "foliage", "NOT_EMITTER"));
      entries.add(new BlockMap.MacroEntry("age", "1", "foliage", "NOT_EMITTER"));
      entries.add(new BlockMap.MacroEntry("age", "2", "foliage", "brush"));
      entries.add(new BlockMap.MacroEntry("age", "3", "foliage", "brush"));
      macros.put("#beets", entries);
   }

   private static class MacroEntry {
      public final String propertyName;
      public final String propertyValue;
      public final String substrate;
      public final String value;

      public MacroEntry(@Nullable String substrate, @Nonnull String value) {
         this((String)null, (String)null, substrate, value);
      }

      public MacroEntry(@Nullable String propertyName, @Nullable String propertyValue, @Nullable String substrate, @Nonnull String value) {
         this.propertyName = propertyName;
         this.propertyValue = propertyValue;
         this.substrate = substrate;
         this.value = value;
      }

      @Nonnull
      public Tuple<String, String> expand(@Nonnull String base) {
         StringBuilder builder = new StringBuilder();
         builder.append(base);
         if (this.propertyName != null) {
            builder.append('[');
            builder.append(this.propertyName).append('=').append(this.propertyValue);
            builder.append(']');
         }

         if (this.substrate != null) {
            builder.append('+').append(this.substrate);
         }

         return new Tuple(builder.toString(), this.value);
      }
   }
}
