package gigaherz.toolbelt.shadow.commons.client;

import net.minecraft.block.state.IBlockState;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class StackRenderingHelper {
   public StackRenderingHelper() {
   }

   public static void renderItemStack(ItemModelMesher mesher, TextureManager renderEngine, int xPos, int yPos, ItemStack stack, int color) {
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      GlStateManager.pushMatrix();
      IBakedModel model = mesher.getItemModel(stack);
      setupGuiTransform(xPos, yPos, model.isGui3d());
      model = ForgeHooksClient.handleCameraTransforms(model, TransformType.GUI, false);
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      renderItem(model, color);
      GlStateManager.popMatrix();
      renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      RenderHelper.disableStandardItemLighting();
   }

   private static void renderItem(IBakedModel model, int color) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getBuffer();
      worldrenderer.begin(7, DefaultVertexFormats.ITEM);

      for(BakedQuad bakedquad : model.getQuads((IBlockState)null, (EnumFacing)null, 0L)) {
         LightUtil.renderQuadColor(worldrenderer, bakedquad, color);
      }

      tessellator.draw();
   }

   private static void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d) {
      GlStateManager.translate((float)xPosition, (float)yPosition, 150.0F);
      GlStateManager.translate(8.0F, 8.0F, 0.0F);
      GlStateManager.scale(1.0F, -1.0F, 1.0F);
      GlStateManager.scale(16.0F, 16.0F, 16.0F);
      if (isGui3d) {
         GlStateManager.enableLighting();
      } else {
         GlStateManager.disableLighting();
      }

   }
}
