package slimeknights.mantle.client.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;

public class BakedCompositeModel extends BakedWrapper {
   protected final ImmutableMap<Optional<EnumFacing>, ImmutableList<BakedQuad>> parts;

   public BakedCompositeModel(IBakedModel parent, ImmutableMap<Optional<EnumFacing>, ImmutableList<BakedQuad>> parts) {
      super(parent);
      this.parts = parts;
   }

   @Nonnull
   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      return (List)this.parts.get(Optional.fromNullable(side));
   }

   public static class Builder {
      com.google.common.collect.ImmutableList.Builder<BakedQuad>[] builders = new com.google.common.collect.ImmutableList.Builder[7];
      IBakedModel parent;

      public Builder() {
         for(int i = 0; i < 7; ++i) {
            this.builders[i] = ImmutableList.builder();
         }

      }

      public void add(IBakedModel model, IBlockState state, long rand) {
         this.add(model, state, (EnumFacing)null, rand);

         for(EnumFacing side : EnumFacing.values()) {
            this.add(model, state, side, rand);
         }

      }

      public void add(IBakedModel model, IBlockState state, EnumFacing side, long rand) {
         int index;
         if (side == null) {
            index = 6;
         } else {
            index = side.getIndex();
         }

         this.builders[index].addAll(model.getQuads(state, side, rand));
      }

      public BakedCompositeModel build(IBakedModel parent) {
         com.google.common.collect.ImmutableMap.Builder<Optional<EnumFacing>, ImmutableList<BakedQuad>> map = ImmutableMap.builder();
         map.put(Optional.absent(), this.builders[6].build());

         for(EnumFacing side : EnumFacing.values()) {
            map.put(Optional.of(side), this.builders[side.getIndex()].build());
         }

         return new BakedCompositeModel(parent, map.build());
      }
   }
}
