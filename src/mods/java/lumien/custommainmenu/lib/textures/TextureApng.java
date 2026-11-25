package lumien.custommainmenu.lib.textures;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.ellerton.japng.Png;
import net.ellerton.japng.argb8888.Argb8888Bitmap;
import net.ellerton.japng.argb8888.Argb8888BitmapSequence;
import net.ellerton.japng.argb8888.Argb8888BitmapSequence.Frame;
import net.ellerton.japng.chunks.PngAnimationControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TextureApng implements ITexture {
   ResourceLocation rl;
   HashMap<Frame, Integer> frameTextureID;
   List<Frame> frames;
   PngAnimationControl animationControl;
   int currentFrame;
   boolean loaded;
   boolean errored;
   long lastTimeStamp;
   int currentFrameDelay;

   public TextureApng(String textureString) {
      this.rl = new ResourceLocation(textureString);
      this.errored = false;
   }

   public void bind() {
      if (!this.loaded) {
         this.load();
         this.loaded = true;
      }

      if (this.errored) {
         GlStateManager.bindTexture(TextureUtil.MISSING_TEXTURE.getGlTextureId());
      } else {
         while(System.currentTimeMillis() - this.lastTimeStamp >= (long)this.currentFrameDelay) {
            ++this.currentFrame;
            if (this.currentFrame > this.animationControl.numFrames - 1) {
               this.currentFrame = 0;
            }

            Frame f = (Frame)this.frames.get(this.currentFrame);
            float numerator = (float)f.control.delayNumerator;
            float denominator = f.control.delayDenominator > 0 ? (float)f.control.delayDenominator : 100.0F;
            this.lastTimeStamp += (long)this.currentFrameDelay;
            this.currentFrameDelay = (int)(numerator / denominator * 1000.0F);
         }

         GlStateManager.bindTexture(((Integer)this.frameTextureID.get(this.frames.get(this.currentFrame))).intValue());
      }
   }

   private void load() {
      this.frameTextureID = new HashMap();

      try {
         InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(this.rl).getInputStream();
         Argb8888BitmapSequence pngContainer = Png.readArgb8888BitmapSequence(inputStream);
         this.animationControl = pngContainer.getAnimationControl();
         this.frames = Collections.synchronizedList(pngContainer.getAnimationFrames());
         BufferedImage canvas = new BufferedImage(pngContainer.defaultImage.width, pngContainer.defaultImage.height, 2);
         Graphics2D graphics = canvas.createGraphics();
         graphics.setBackground(new Color(0, 0, 0, 0));
         BufferedImage frameBackup = new BufferedImage(pngContainer.defaultImage.width, pngContainer.defaultImage.height, 2);
         Graphics2D graphicsBackup = frameBackup.createGraphics();
         graphicsBackup.setBackground(new Color(0, 0, 0, 0));

         for(int i = 0; i < this.frames.size(); ++i) {
            Frame f = (Frame)this.frames.get(i);
            switch(f.control.blendOp) {
            case 0:
               graphics.setComposite(AlphaComposite.getInstance(2));
               break;
            case 1:
               graphics.setComposite(AlphaComposite.getInstance(3));
            }

            Argb8888Bitmap bitmap = f.bitmap;
            BufferedImage buffered = new BufferedImage(bitmap.width, bitmap.height, 2);
            buffered.setRGB(0, 0, bitmap.width, bitmap.height, bitmap.getPixelArray(), 0, bitmap.width);
            graphicsBackup.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            graphicsBackup.drawImage(canvas, 0, 0, canvas.getWidth(), canvas.getHeight(), (ImageObserver)null);
            graphics.drawImage(buffered, f.control.xOffset, f.control.yOffset, f.control.xOffset + f.control.width, f.control.yOffset + f.control.height, 0, 0, buffered.getWidth(), buffered.getHeight(), (ImageObserver)null);
            this.frameTextureID.put(f, Integer.valueOf(TextureUtil.uploadTextureImageAllocate(GL11.glGenTextures(), canvas, false, false)));
            switch(f.control.disposeOp) {
            case 0:
            default:
               break;
            case 1:
               graphics.clearRect(f.control.xOffset, f.control.yOffset, f.control.width, f.control.height);
               break;
            case 2:
               graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
               graphics.drawImage(frameBackup, 0, 0, canvas.getWidth(), canvas.getHeight(), (ImageObserver)null);
            }
         }

         graphics.dispose();
         graphicsBackup.dispose();
      } catch (Exception var11) {
         var11.printStackTrace();
         this.errored = true;
         return;
      }

      Frame frame = (Frame)this.frames.get(0);
      float numerator = (float)frame.control.delayNumerator;
      float denominator = frame.control.delayDenominator > 0 ? (float)frame.control.delayDenominator : 100.0F;
      this.currentFrameDelay = (int)(numerator / denominator * 1000.0F);
      this.lastTimeStamp = System.currentTimeMillis();
   }
}
