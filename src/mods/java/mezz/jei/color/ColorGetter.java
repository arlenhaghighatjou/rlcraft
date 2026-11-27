package mezz.jei.color;

import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import com.google.common.base.Preconditions;
import com.arlenh7.eaglercraft.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public final class ColorGetter {
   private static final String[] defaultColors = new String[]{"White:EEEEEE", "LightBlue:7492cc", "Cyan:00EEEE", "Blue:2222dd", "LapisBlue:25418b", "Teal:008080", "Yellow:cacb58", "GoldenYellow:EED700", "Orange:d97634", "Pink:D1899D", "HotPink:FC0FC0", "Magenta:b24bbb", "Purple:813eb9", "JadedPurple:43324f", "EvilPurple:2e1649", "Lavender:B57EDC", "Indigo:480082", "Sand:dbd3a0", "Tan:bb9b63", "LightBrown:A0522D", "Brown:634b33", "DarkBrown:3a2d13", "LimeGreen:43b239", "SlimeGreen:83cb73", "Green:008000", "DarkGreen:224d22", "GrassGreen:548049", "Red:963430", "BrickRed:b0604b", "NetherBrick:2a1516", "Redstone:ce3e36", "Black:181515", "CharcoalGray:464646", "IronGray:646464", "Gray:808080", "Silver:C0C0C0"};

   private ColorGetter() {
   }

   public static String[] getColorDefaults() {
      return defaultColors;
   }

   public static List<Color> getColors(ItemStack itemStack, int colorCount) {
      try {
         return unsafeGetColors(itemStack, colorCount);
      } catch (LinkageError | RuntimeException var3) {
         return Collections.emptyList();
      }
   }

   private static List<Color> unsafeGetColors(ItemStack itemStack, int colorCount) {
      Item item = itemStack.getItem();
      if (itemStack.isEmpty()) {
         return Collections.emptyList();
      } else if (item instanceof ItemBlock) {
         ItemBlock itemBlock = (ItemBlock)item;
         Block block = itemBlock.getBlock();
         return block == null ? Collections.emptyList() : getBlockColors(itemStack, block, colorCount);
      } else {
         return getItemColors(itemStack, colorCount);
      }
   }

   private static List<Color> getItemColors(ItemStack itemStack, int colorCount) {
      ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
      int renderColor = itemColors.colorMultiplier(itemStack, 0);
      EaglerTextureAtlasSprite textureAtlasSprite = getTextureAtlasSprite(itemStack);
      return getColors(textureAtlasSprite, renderColor, colorCount);
   }

   private static List<Color> getBlockColors(ItemStack itemStack, Block block, int colorCount) {
      int meta = itemStack.getMetadata();

      IBlockState blockState;
      try {
         blockState = block.getStateFromMeta(meta);
      } catch (LinkageError | RuntimeException var8) {
         blockState = block.getDefaultState();
      }

      BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
      int renderColor = blockColors.colorMultiplier(blockState, (IBlockAccess)null, (BlockPos)null, 0);
      EaglerTextureAtlasSprite textureAtlasSprite = getTextureAtlasSprite(blockState);
      return textureAtlasSprite == null ? Collections.emptyList() : getColors(textureAtlasSprite, renderColor, colorCount);
   }

   public static List<Color> getColors(EaglerTextureAtlasSprite textureAtlasSprite, int renderColor, int colorCount) {
      ImageData bufferedImage = getBufferedImage(textureAtlasSprite);
      if (bufferedImage == null) {
         return Collections.emptyList();
      } else {
         List<Color> colors = new ArrayList(colorCount);
         int[][] palette = ColorThief.getPalette(bufferedImage, colorCount);
         if (palette != null) {
            for(int[] colorInt : palette) {
               int red = (int)((float)(colorInt[0] - 1) * (float)(renderColor >> 16 & 255) / 255.0F);
               int green = (int)((float)(colorInt[1] - 1) * (float)(renderColor >> 8 & 255) / 255.0F);
               int blue = (int)((float)(colorInt[2] - 1) * (float)(renderColor & 255) / 255.0F);
               red = MathUtil.clamp(red, 0, 255);
               green = MathUtil.clamp(green, 0, 255);
               blue = MathUtil.clamp(blue, 0, 255);
               Color color = new Color(red, green, blue);
               colors.add(color);
            }
         }

         return colors;
      }
   }

   @Nullable
   private static ImageData getBufferedImage(EaglerTextureAtlasSprite textureAtlasSprite) {
      int iconWidth = textureAtlasSprite.getIconWidth();
      int iconHeight = textureAtlasSprite.getIconHeight();
      int frameCount = textureAtlasSprite.getFrameCount();
      if (iconWidth > 0 && iconHeight > 0 && frameCount > 0) {
         ImageData bufferedImage = new ImageData(iconWidth, iconHeight * frameCount, true);

         for(int i = 0; i < frameCount; ++i) {
            int[][] frameTextureData = textureAtlasSprite.getFrameTextureData(i);
            int[] largestMipMapTextureData = frameTextureData[0];
            System.arraycopy(largestMipMapTextureData, 0, bufferedImage.pixels, i * iconHeight * iconWidth, iconWidth * iconHeight);
         }

         return bufferedImage;
      } else {
         return null;
      }
   }

   @Nullable
   private static EaglerTextureAtlasSprite getTextureAtlasSprite(IBlockState blockState) {
      Minecraft minecraft = Minecraft.getMinecraft();
      BlockRendererDispatcher blockRendererDispatcher = minecraft.getBlockRendererDispatcher();
      BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
      EaglerTextureAtlasSprite textureAtlasSprite = blockModelShapes.getTexture(blockState);
      return textureAtlasSprite == minecraft.getTextureMapBlocks().getMissingSprite() ? null : textureAtlasSprite;
   }

   private static EaglerTextureAtlasSprite getTextureAtlasSprite(ItemStack itemStack) {
      RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
      ItemModelMesher itemModelMesher = renderItem.getItemModelMesher();
      IBakedModel itemModel = itemModelMesher.getItemModel(itemStack);
      EaglerTextureAtlasSprite particleTexture = itemModel.getParticleTexture();
      return (EaglerTextureAtlasSprite)Preconditions.checkNotNull(particleTexture);
   }
}
