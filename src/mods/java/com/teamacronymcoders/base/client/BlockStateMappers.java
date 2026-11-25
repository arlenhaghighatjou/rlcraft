package com.teamacronymcoders.base.client;

import com.teamacronymcoders.base.blocks.IHasBlockStateMapper;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockStateMappers {
   public BlockStateMappers() {
   }

   public static void registerStateMapper(final IHasBlockStateMapper stateMapper) {
      ModelLoader.setCustomStateMapper(stateMapper.getBlock(), new StateMapperBase() {
         @Nonnull
         protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
            return new ModelResourceLocation(stateMapper.getResourceLocation(state), stateMapper.getVariant(state));
         }
      });
   }
}
