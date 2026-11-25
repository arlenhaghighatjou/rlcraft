package pl.asie.foamfix.client;

import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.ARBCopyImage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import pl.asie.foamfix.FoamFix;
import pl.asie.foamfix.api.IFoamFixSprite;
import pl.asie.foamfix.shared.FoamFixShared;

public class FastTextureAtlasSprite extends TextureAtlasSprite implements IFoamFixSprite {
   private int textureId = -1;
   private int mipLevels = 0;

   public FastTextureAtlasSprite(String p_i1282_1_) {
      super(p_i1282_1_);
   }

   public void updateAnimation() {
      if (!FoamFixShared.config.clDisableTextureAnimations) {
         ++this.tickCounter;
         if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
            int i = this.animationMetadata.getFrameIndex(this.frameCounter);
            int j = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
            this.frameCounter = (this.frameCounter + 1) % j;
            this.tickCounter = 0;
            int k = this.animationMetadata.getFrameIndex(this.frameCounter);
            if (i != k && k >= 0 && k < this.framesTextureData.size()) {
               if (this.textureId != -1) {
                  int destTex = GL11.glGetInteger(32873);
                  checkGLError("updateAnimation | fastPath getPreviousTexture");
                  GL11.glBindTexture(3553, 0);
                  checkGLError("updateAnimation | fastPath unbindTex");

                  for(int mip = 0; mip <= this.mipLevels; ++mip) {
                     ARBCopyImage.glCopyImageSubData(this.textureId, 3553, mip, this.width * k >> mip, 0, 0, destTex, 3553, mip, this.originX >> mip, this.originY >> mip, 0, this.width >> mip, this.height >> mip, 1);
                     checkGLError("updateAnimation | fastPath mip=" + mip);
                  }

                  GL11.glBindTexture(3553, destTex);
                  checkGLError("updateAnimation | fastPath rebindTex");
               } else {
                  int[][] frameData = (int[][])this.framesTextureData.get(k);
                  uploadTextureMaxMips(this.mipLevels, frameData, this.width, this.height, this.originX, this.originY, false, false, frameData.length > 1);
                  checkGLError("updateAnimation | slowPath");
               }
            }
         } else if (this.animationMetadata.isInterpolate()) {
            this.updateAnimationInterpolated();
         }

      }
   }

   private int interpolateColor(double ratio, int from, int to) {
      return (int)(ratio * (double)from + (1.0D - ratio) * (double)to);
   }

   private boolean interpolateFrame(int[] to, int[] from1, int[] from2, double ratio) {
      if (from1.length != from2.length) {
         return false;
      } else {
         for(int i = 0; i < from1.length; ++i) {
            int color1 = from1[i];
            int color2 = from2[i];
            int colorRed = this.interpolateColor(ratio, color1 >> 16 & 255, color2 >> 16 & 255);
            int colorGreen = this.interpolateColor(ratio, color1 >> 8 & 255, color2 >> 8 & 255);
            int colorBlue = this.interpolateColor(ratio, color1 & 255, color2 & 255);
            to[i] = color1 & -16777216 | colorRed << 16 | colorGreen << 8 | colorBlue;
         }

         return true;
      }
   }

   protected void updateAnimationInterpolated() {
      double ratio = 1.0D - (double)this.tickCounter / (double)this.animationMetadata.getFrameTimeSingle(this.frameCounter);
      int currentFrameIndex = this.animationMetadata.getFrameIndex(this.frameCounter);
      int frameCount = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
      int nextFrameIndex = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % frameCount);
      if (currentFrameIndex != nextFrameIndex && nextFrameIndex >= 0 && nextFrameIndex < this.framesTextureData.size()) {
         int[][] frame1 = (int[][])this.framesTextureData.get(currentFrameIndex);
         int[][] frame2 = (int[][])this.framesTextureData.get(nextFrameIndex);
         if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != this.mipLevels + 1) {
            this.interpolatedFrameData = new int[this.mipLevels + 1][];
         }

         int mipLvl;
         for(mipLvl = 0; mipLvl <= this.mipLevels && mipLvl < frame1.length && mipLvl < frame2.length; ++mipLvl) {
            if (this.interpolatedFrameData[mipLvl] == null) {
               this.interpolatedFrameData[mipLvl] = new int[frame1[mipLvl].length];
            }

            this.interpolateFrame(this.interpolatedFrameData[mipLvl], frame1[mipLvl], frame2[mipLvl], ratio);
         }

         uploadTextureMaxMips(mipLvl, this.interpolatedFrameData, this.width, this.height, this.originX, this.originY, false, false, this.interpolatedFrameData.length > 1);
      }

   }

   public void setFramesTextureData(List<int[][]> textureData) {
      if (this.textureId != -1) {
         GL11.glDeleteTextures(this.textureId);
         this.textureId = -1;
         checkGLError("setFramesTextureData | deleteTexture");
      }

      super.setFramesTextureData(textureData);
      if (textureData.size() > 1 && textureData.size() <= FoamFixShared.config.txCacheAnimationMaxFrames && FoamFix.shouldFasterAnimation) {
         this.textureId = GL11.glGenTextures();
         checkGLError("setFramesTextureData | createTexture");
         int prevTex = GL11.glGetInteger(32873);
         checkGLError("setFramesTextureData | getPreviousTexture");
         GL11.glBindTexture(3553, this.textureId);
         checkGLError("setFramesTextureData | bindTexture");
         GL11.glTexParameteri(3553, 33085, this.mipLevels);
         checkGLError("setFramesTextureData | setTextureProperties[Mip Levels]");
         GL11.glTexParameterf(3553, 33082, 0.0F);
         checkGLError("setFramesTextureData | setTextureProperties[LOD Min]");
         GL11.glTexParameterf(3553, 33083, (float)this.mipLevels);
         checkGLError("setFramesTextureData | setTextureProperties[LOD Max]");
         GL11.glTexParameterf(3553, 34049, 0.0F);
         checkGLError("setFramesTextureData | setTextureProperties[LOD Bias]");

         for(int i = 0; i <= this.mipLevels; ++i) {
            GL11.glTexImage2D(3553, i, 6408, this.width * this.framesTextureData.size() >> i, this.height >> i, 0, 32993, 5121, (ByteBuffer)null);
            checkGLError("setFramesTextureData | createMip " + i);
         }

         for(int i = 0; i < this.framesTextureData.size(); ++i) {
            uploadTextureMaxMips(this.mipLevels, (int[][])this.framesTextureData.get(i), this.width, this.height, this.width * i, 0, false, false, this.mipLevels > 0);
            checkGLError("setFramesTextureData | uploadFrame " + i);
         }

         GL11.glBindTexture(3553, prevTex);
         checkGLError("setFramesTextureData | restoreTexture");
      }

   }

   private static void uploadTextureMaxMips(int maxMips, int[][] data, int width, int height, int originX, int originY, boolean linearFiltering, boolean clamped, boolean mipFiltering) {
      int mips = maxMips >= 0 ? Math.min(maxMips, data.length - 1) : data.length - 1;

      for(int mip = 0; mip <= mips; ++mip) {
         if (width >> mip > 0 & height >> mip > 0) {
            TextureUtil.uploadTextureSub(mip, data[mip], width >> mip, height >> mip, originX >> mip, originY >> mip, linearFiltering, clamped, mipFiltering);
            checkGLError("uploadTextureMaxMips mip=" + mip);
         }
      }

   }

   private static void checkGLError(String desc) {
      for(int error = GL11.glGetError(); error != 0; error = GL11.glGetError()) {
         String errorString = GLU.gluErrorString(error);
         FoamFix.getLogger().error("GL Error: " + errorString + "(" + error + ") @ " + desc);
      }

   }

   public void clearFramesTextureData() {
      super.clearFramesTextureData();
      if (this.textureId != -1) {
         GL11.glDeleteTextures(this.textureId);
         this.textureId = -1;
      }

   }

   public void generateMipmaps(int p_147963_1_) {
      this.mipLevels = FoamFixShared.config.txMaxAnimationMipLevel >= 0 ? Math.min(FoamFixShared.config.txMaxAnimationMipLevel, p_147963_1_) : p_147963_1_;
      super.generateMipmaps(p_147963_1_);
   }

   public boolean isStoredOnGPU() {
      return this.textureId != -1 || !this.hasAnimationMetadata();
   }
}
