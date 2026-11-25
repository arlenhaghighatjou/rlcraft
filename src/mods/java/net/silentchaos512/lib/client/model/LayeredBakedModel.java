package net.silentchaos512.lib.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IModel;

public abstract class LayeredBakedModel implements ILayeredBakedModel {
   protected final IModel parent;
   protected final ImmutableList<ImmutableList<BakedQuad>> quads;
   protected final VertexFormat format;
   private ImmutableList<BakedQuad> allQuadsCache;

   public LayeredBakedModel(IModel parent, ImmutableList<ImmutableList<BakedQuad>> immutableList, VertexFormat format) {
      this.quads = immutableList;
      this.format = format;
      this.parent = parent;
   }

   public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long indexNotRand) {
      if (side == null) {
         int index = (int)indexNotRand;
         if (index < 0) {
            if (this.allQuadsCache == null) {
               Builder<BakedQuad> builder = ImmutableList.builder();
               UnmodifiableIterator var7 = this.quads.iterator();

               while(var7.hasNext()) {
                  for(BakedQuad quad : (List)var7.next()) {
                     builder.add(quad);
                  }
               }

               this.allQuadsCache = builder.build();
            }

            return this.allQuadsCache;
         }

         if (index < this.quads.size()) {
            return (List)this.quads.get(index);
         }
      }

      return ImmutableList.of();
   }

   public boolean isBuiltInRenderer() {
      return true;
   }

   public int getLayerCount() {
      return this.quads.size();
   }

   public IModel getParent() {
      return this.parent;
   }

   public VertexFormat getVertexFormat() {
      return this.format;
   }
}
