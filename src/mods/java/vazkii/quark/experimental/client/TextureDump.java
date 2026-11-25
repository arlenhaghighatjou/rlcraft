package vazkii.quark.experimental.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent.Post;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;

public class TextureDump extends Feature {
   public TextureDump() {
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void postTextureStitch(Post e) {
      String basePath = (String)ObfuscationReflectionHelper.getPrivateValue(TextureMap.class, e.getMap(), "basePath");
      int mipMapLevel = ((Integer)ObfuscationReflectionHelper.getPrivateValue(TextureMap.class, e.getMap(), "mipmapLevels")).intValue();
      this.saveGlTexture(basePath, e.getMap().getGlTextureId(), mipMapLevel);
   }

   @SideOnly(Side.CLIENT)
   private void saveGlTexture(String name, int textureId, int mipmapLevels) {
      GL11.glBindTexture(3553, textureId);
      GL11.glPixelStorei(3333, 1);
      GL11.glPixelStorei(3317, 1);
      File instanceDirectory = Minecraft.getMinecraft().mcDataDir;

      for(int level = 0; level < mipmapLevels; ++level) {
         int width = GL11.glGetTexLevelParameteri(3553, level, 4096);
         int height = GL11.glGetTexLevelParameteri(3553, level, 4097);
         int size = width * height;
         BufferedImage image = new BufferedImage(width, height, 2);
         IntBuffer buffer = BufferUtils.createIntBuffer(size);
         int[] data = new int[size];
         GL11.glGetTexImage(3553, level, 32993, 33639, buffer);
         buffer.get(data);
         image.setRGB(0, 0, width, height, data, 0, width);

         try {
            File output = (new File(instanceDirectory, name + "_" + level + ".png")).getCanonicalFile();
            ImageIO.write(image, "png", output);
            Quark.LOG.info("[TextureDump] Exported atlas to: " + output.getAbsolutePath());
         } catch (IOException var13) {
            Quark.LOG.info("[TextureDump] Unable to write: ", var13);
         }
      }

   }
}
