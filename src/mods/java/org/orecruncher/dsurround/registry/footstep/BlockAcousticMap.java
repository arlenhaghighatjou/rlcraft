package org.orecruncher.dsurround.registry.footstep;

import com.google.common.base.MoreObjects;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.acoustics.AcousticRegistry;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.dsurround.registry.blockstate.BlockStateMatcher;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public final class BlockAcousticMap {
   protected final Map<IBlockState, IAcoustic[]> cache;
   protected final BlockAcousticMap.IAcousticResolver resolver;
   protected final Map<Block, ObjectArray<BlockAcousticMap.BlockMapEntry>> data;

   public BlockAcousticMap() {
      this((state) -> {
         return AcousticRegistry.EMPTY;
      });
   }

   public BlockAcousticMap(@Nonnull BlockAcousticMap.IAcousticResolver resolver) {
      this.cache = new Reference2ObjectOpenHashMap();
      this.data = new Reference2ObjectOpenHashMap();
      this.resolver = resolver;
      this.put(BlockStateMatcher.AIR, AcousticRegistry.NOT_EMITTER);
   }

   @Nonnull
   protected IAcoustic[] cacheMiss(@Nonnull IBlockState state) {
      IAcoustic[] result = null;
      ObjectArray<BlockAcousticMap.BlockMapEntry> entries = (ObjectArray)this.data.get(state.getBlock());
      if (entries != null) {
         BlockStateMatcher matcher = BlockStateMatcher.create(state);
         result = this.find(entries, matcher);
         if (result != null) {
            return result;
         }

         if (matcher.hasSubtypes()) {
            result = this.find(entries, BlockStateMatcher.asGeneric(state));
         }

         if (result != null) {
            return result;
         }
      }

      if (this.resolver != null) {
         result = this.resolver.resolve(state);
      }

      return (IAcoustic[])MoreObjects.firstNonNull(result, AcousticRegistry.EMPTY);
   }

   @Nullable
   private IAcoustic[] find(@Nonnull ObjectArray<BlockAcousticMap.BlockMapEntry> entries, @Nonnull BlockStateMatcher matcher) {
      for(int i = entries.size() - 1; i >= 0; --i) {
         BlockAcousticMap.BlockMapEntry e = (BlockAcousticMap.BlockMapEntry)entries.get(i);
         if (matcher.equals(e.matcher)) {
            return e.acoustics;
         }
      }

      return null;
   }

   @Nonnull
   public IAcoustic[] getBlockAcoustics(@Nonnull IBlockState state) {
      IAcoustic[] result = (IAcoustic[])this.cache.get(state);
      if (result == null) {
         result = this.cacheMiss(state);
         this.cache.put(state, result);
      }

      return result;
   }

   public void put(@Nonnull BlockStateMatcher info, @Nonnull IAcoustic[] acoustics) {
      ObjectArray<BlockAcousticMap.BlockMapEntry> entry = (ObjectArray)this.data.get(info.getBlock());
      if (entry == null) {
         this.data.put(info.getBlock(), entry = new ObjectArray(2));
      }

      entry.add(new BlockAcousticMap.BlockMapEntry(info, acoustics));
   }

   public void clear() {
      this.data.clear();
      this.cache.clear();
   }

   private static class BlockMapEntry {
      public final BlockStateMatcher matcher;
      public final IAcoustic[] acoustics;

      public BlockMapEntry(@Nonnull BlockStateMatcher matcher, @Nonnull IAcoustic[] acoustics) {
         this.matcher = matcher;
         this.acoustics = acoustics;
      }
   }

   public interface IAcousticResolver {
      @Nullable
      IAcoustic[] resolve(@Nonnull IBlockState var1);
   }
}
