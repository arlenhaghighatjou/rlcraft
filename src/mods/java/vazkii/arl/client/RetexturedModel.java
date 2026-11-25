package vazkii.arl.client;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import vazkii.arl.interf.IStateMapperProvider;

public class RetexturedModel extends BakedModelWrapper<IBakedModel> {
   private IModel model;
   private final VertexFormat format;
   private final String textureKey;

   public RetexturedModel(IBakedModel originalModel, IModel model, VertexFormat format, String textureKey) {
      super(originalModel);
      this.model = model;
      this.format = format;
      this.textureKey = textureKey;
   }

   @Nonnull
   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      IBakedModel bakedModel = this.originalModel;
      if (state instanceof IExtendedBlockState) {
         IExtendedBlockState extendedState = (IExtendedBlockState)state;
         String texture = (String)extendedState.getValue(IStateMapperProvider.TEXTURE);
         if (texture != null) {
            IModel retextured = this.model.retexture(ImmutableMap.of(this.textureKey, texture));
            bakedModel = retextured.bake(retextured.getDefaultState(), this.format, ModelLoader.defaultTextureGetter());
         }
      }

      return bakedModel.getQuads(state, side, rand);
   }
}
