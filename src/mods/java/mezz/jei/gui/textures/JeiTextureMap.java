package mezz.jei.gui.textures;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Function;
import mezz.jei.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureMapPopulator;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

@SideOnly(Side.CLIENT)
public class JeiTextureMap extends TextureMap {
   private final ResourceLocation location;

   public JeiTextureMap(String basePathIn) {
      super(basePathIn, (ITextureMapPopulator)null, true);
      this.location = new ResourceLocation("jei", basePathIn);
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   public void loadTexture(IResourceManager resourceManager) {
      this.initMissingImage();
      this.deleteGlTexture();
      this.loadTextureAtlas(resourceManager);
   }

   public void loadTextureAtlas(IResourceManager resourceManager) {
      int i = Minecraft.getGLMaximumTextureSize();
      Stitcher stitcher = new Stitcher(i, i, 0, 0);
      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
      ProgressBar bar = ProgressManager.push("Texture stitching", this.mapRegisteredSprites.size());
      Iterator map = this.mapRegisteredSprites.entrySet().iterator();

      while(true) {
         TextureAtlasSprite textureatlassprite;
         while(true) {
            if (!map.hasNext()) {
               ProgressManager.pop(bar);
               this.missingImage.generateMipmaps(0);
               stitcher.addSprite(this.missingImage);
               bar = ProgressManager.push("Texture creation", 2);
               bar.step("Stitching");
               stitcher.doStitch();
               Log.get().info("Created: {}x{} {}-atlas", Integer.valueOf(stitcher.getCurrentWidth()), Integer.valueOf(stitcher.getCurrentHeight()), this.basePath);
               bar.step("Allocating GL texture");
               TextureUtil.allocateTextureImpl(this.getGlTextureId(), 0, stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
               map = Maps.newHashMap(this.mapRegisteredSprites);
               ProgressManager.pop(bar);
               bar = ProgressManager.push("Texture mipmap and upload", stitcher.getStichSlots().size());

               for(TextureAtlasSprite textureatlassprite1 : stitcher.getStichSlots()) {
                  bar.step(textureatlassprite1.getIconName());
                  if (textureatlassprite1 == this.missingImage || this.generateMipmaps(resourceManager, textureatlassprite1)) {
                     String s = textureatlassprite1.getIconName();
                     map.remove(s);
                     this.mapUploadedSprites.put(s, textureatlassprite1);

                     try {
                        TextureUtil.uploadTextureMipmap(textureatlassprite1.getFrameTextureData(0), textureatlassprite1.getIconWidth(), textureatlassprite1.getIconHeight(), textureatlassprite1.getOriginX(), textureatlassprite1.getOriginY(), false, false);
                     } catch (Throwable var17) {
                        CrashReport crashreport = CrashReport.makeCrashReport(var17, "Stitching texture atlas");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
                        crashreportcategory.addCrashSection("Atlas path", this.basePath);
                        crashreportcategory.addCrashSection("Sprite", textureatlassprite1);
                        throw new ReportedException(crashreport);
                     }

                     if (textureatlassprite1.hasAnimationMetadata()) {
                        this.listAnimatedSprites.add(textureatlassprite1);
                     }
                  }
               }

               for(TextureAtlasSprite textureatlassprite2 : map.values()) {
                  textureatlassprite2.copyFrom(this.missingImage);
               }

               ProgressManager.pop(bar);
               return;
            }

            Entry<String, TextureAtlasSprite> entry = (Entry)map.next();
            textureatlassprite = (TextureAtlasSprite)entry.getValue();
            ResourceLocation resourcelocation = this.getResourceLocation(textureatlassprite);
            bar.step(resourcelocation.getResourcePath());
            IResource iresource = null;
            if (textureatlassprite.hasCustomLoader(resourceManager, resourcelocation)) {
               if (textureatlassprite.load(resourceManager, resourcelocation, (l) -> {
                  return (TextureAtlasSprite)this.mapRegisteredSprites.get(l.toString());
               })) {
                  continue;
               }
               break;
            } else {
               try {
                  PngSizeInfo pngsizeinfo = PngSizeInfo.makeFromResource(resourceManager.getResource(resourcelocation));
                  iresource = resourceManager.getResource(resourcelocation);
                  boolean flag = iresource.getMetadata("animation") != null;
                  textureatlassprite.loadSprite(pngsizeinfo, flag);
                  break;
               } catch (RuntimeException var18) {
                  FMLClientHandler.instance().trackBrokenTexture(resourcelocation, var18.getMessage());
               } catch (IOException var19) {
                  FMLClientHandler.instance().trackMissingTexture(resourcelocation);
               } finally {
                  IOUtils.closeQuietly(iresource);
               }
            }
         }

         stitcher.addSprite(textureatlassprite);
      }
   }

   private boolean generateMipmaps(IResourceManager resourceManager, TextureAtlasSprite texture) {
      ResourceLocation resourcelocation = this.getResourceLocation(texture);
      IResource iresource = null;
      if (!texture.hasCustomLoader(resourceManager, resourcelocation)) {
         label60: {
            boolean flag;
            try {
               iresource = resourceManager.getResource(resourcelocation);
               texture.loadSpriteFrames(iresource, 1);
               break label60;
            } catch (RuntimeException var14) {
               Log.get().error("Unable to parse metadata from {}", resourcelocation, var14);
               flag = false;
            } catch (IOException var15) {
               Log.get().error("Using missing texture, unable to load {}", resourcelocation, var15);
               flag = false;
               boolean crashreportcategory = flag;
               return crashreportcategory;
            } finally {
               IOUtils.closeQuietly(iresource);
            }

            return flag;
         }
      }

      try {
         texture.generateMipmaps(0);
         return true;
      } catch (Throwable var13) {
         CrashReport crashreport = CrashReport.makeCrashReport(var13, "Applying mipmap");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
         crashreportcategory.addDetail("Sprite name", texture::getIconName);
         crashreportcategory.addDetail("Sprite size", () -> {
            return texture.getIconWidth() + " x " + texture.getIconHeight();
         });
         crashreportcategory.addDetail("Sprite frames", () -> {
            return texture.getFrameCount() + " frames";
         });
         crashreportcategory.addCrashSection("Mipmap levels", Integer.valueOf(0));
         throw new ReportedException(crashreport);
      }
   }

   private ResourceLocation getResourceLocation(TextureAtlasSprite p_184396_1_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_184396_1_.getIconName());
      return new ResourceLocation(resourcelocation.getResourceDomain(), String.format("%s/%s%s", this.basePath, resourcelocation.getResourcePath(), ".png"));
   }
}
