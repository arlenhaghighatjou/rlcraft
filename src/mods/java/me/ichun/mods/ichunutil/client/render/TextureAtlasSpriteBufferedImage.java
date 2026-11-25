package me.ichun.mods.ichunutil.client.render;

import java.awt.image.BufferedImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class TextureAtlasSpriteBufferedImage extends TextureAtlasSprite {
   public BufferedImage image;

   public TextureAtlasSpriteBufferedImage(ResourceLocation rl, BufferedImage image) {
      super(rl.toString());
      this.image = image;
   }

   public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
      return true;
   }

   public boolean load(IResourceManager manager, ResourceLocation location) {
      this.width = this.image.getWidth();
      this.height = this.image.getHeight();
      int[][] aint = new int[Minecraft.getMinecraft().getTextureMapBlocks().getMipmapLevels() + 1][];
      aint[0] = new int[this.image.getWidth() * this.image.getHeight()];
      this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), aint[0], 0, this.image.getWidth());
      this.framesTextureData.add(aint);
      this.initSprite(this.width, this.height, 0, 0, false);
      return false;
   }
}
