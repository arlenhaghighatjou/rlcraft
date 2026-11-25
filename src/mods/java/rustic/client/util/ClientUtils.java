package rustic.client.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ClientUtils {
   public ClientUtils() {
   }

   public static void drawTexturedColoredRect(float x, float y, float width, float height, float minU, float minV, float maxU, float maxV, int color) {
      int blue = color & 255;
      int green = color >> 8 & 255;
      int red = color >> 16 & 255;
      int alpha = color >> 24 & 255;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer vertexBuffer = tessellator.getBuffer();
      vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      vertexBuffer.pos((double)x, (double)(y + height), 0.0D).tex((double)minU, (double)maxV).color(red, green, blue, alpha).endVertex();
      vertexBuffer.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)maxU, (double)maxV).color(red, green, blue, alpha).endVertex();
      vertexBuffer.pos((double)(x + width), (double)y, 0.0D).tex((double)maxU, (double)minV).color(red, green, blue, alpha).endVertex();
      vertexBuffer.pos((double)x, (double)y, 0.0D).tex((double)minU, (double)minV).color(red, green, blue, alpha).endVertex();
      tessellator.draw();
   }

   public static IBakedModel getItemModel(ItemStack stack) {
      return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, (World)null, (EntityLivingBase)null);
   }

   public static EaglerTextureAtlasSprite getItemTexture(ItemStack stack) {
      IBakedModel model = getItemModel(stack);
      EaglerTextureAtlasSprite sprite = model.getParticleTexture();
      if (!sprite.getIconName().equals("missingno")) {
         return sprite;
      } else {
         EnumFacing[] faces = new EnumFacing[]{null, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.UP, EnumFacing.DOWN};

         for(EnumFacing face : faces) {
            for(BakedQuad quad : model.getQuads((IBlockState)null, face, 0L)) {
               sprite = quad.getSprite();
               if (!sprite.getIconName().equals("missingno")) {
                  return sprite;
               }
            }
         }

         return sprite;
      }
   }

   public static int getItemColor(ItemStack stack) {
      return getTextureColor(getItemTexture(stack));
   }

   public static int getTextureColor(EaglerTextureAtlasSprite sprite) {
      if (sprite == null) {
         return -65281;
      } else {
         int numPixels = 0;
         int[] data = sprite.getFrameTextureData(0)[0];
         int width = sprite.getIconWidth();
         int height = sprite.getIconHeight();
         int a = 255;
         int r = 0;
         int g = 0;
         int b = 0;

         for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
               int c = data[y * width + x];
               if ((c >> 24 & 255) > 0) {
                  r += c >> 16 & 255;
                  g += c >> 8 & 255;
                  b += c & 255;
                  ++numPixels;
               }
            }
         }

         r = MathHelper.clamp(Math.round((float)r / (float)numPixels), 0, 255);
         g = MathHelper.clamp(Math.round((float)g / (float)numPixels), 0, 255);
         b = MathHelper.clamp(Math.round((float)b / (float)numPixels), 0, 255);
         int color = a << 24 | r << 16 | g << 8 | b;
         return color;
      }
   }
}
