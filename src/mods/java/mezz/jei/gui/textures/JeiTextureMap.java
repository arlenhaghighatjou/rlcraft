package mezz.jei.gui.textures;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import mezz.jei.util.Log;
import net.lax1dude.eaglercraft.v1_8.HString;
import net.lax1dude.eaglercraft.v1_8.IOUtils;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureMapPopulator;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

      for(Entry<String, EaglerTextureAtlasSprite> entry : this.mapRegisteredSprites.entrySet()) {
         EaglerTextureAtlasSprite textureatlassprite = (EaglerTextureAtlasSprite)entry.getValue();
         ResourceLocation resourcelocation = this.getResourceLocation(textureatlassprite);
         bar.step(resourcelocation.getResourcePath());
         if (textureatlassprite.hasCustomLoader(resourceManager, resourcelocation)) {
            if (textureatlassprite.load(resourceManager, resourcelocation, (l) -> {
               return (EaglerTextureAtlasSprite)this.mapRegisteredSprites.get(l.toString());
            })) {
               continue;
            }
         } else {
            IResource iresource = null;

            try {
               iresource = resourceManager.getResource(resourcelocation);
               ImageData image = TextureUtil.readBufferedImage(iresource.getInputStream());
               textureatlassprite.loadSprite(new ImageData[]{image}, iresource.getMetadata("animation"));
            } catch (RuntimeException var13) {
               FMLClientHandler.instance().trackBrokenTexture(resourcelocation, var13.getMessage());
               continue;
            } catch (IOException var14) {
               FMLClientHandler.instance().trackMissingTexture(resourcelocation);
               continue;
            } finally {
               if (iresource != null) {
                  IOUtils.closeQuietly(iresource);
               }

            }
         }

         stitcher.addSprite(textureatlassprite);
      }

      ProgressManager.pop(bar);
      this.missingImage.generateMipmaps(0);
      stitcher.addSprite(this.missingImage);
      bar = ProgressManager.push("Texture creation", 2);
      bar.step("Stitching");
      stitcher.doStitch();
      Log.get().info("Created: {}x{} {}-atlas", Integer.valueOf(stitcher.getCurrentWidth()), Integer.valueOf(stitcher.getCurrentHeight()), this.basePath);
      bar.step("Allocating GL texture");
      TextureUtil.allocateTextureImpl(this.getGlTextureId(), 0, stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
      this.width = stitcher.getCurrentWidth();
      this.height = stitcher.getCurrentHeight();
      Map<String, EaglerTextureAtlasSprite> map = Maps.newHashMap(this.mapRegisteredSprites);
      ProgressManager.pop(bar);
      bar = ProgressManager.push("Texture mipmap and upload", stitcher.getStichSlots().size());

      for(EaglerTextureAtlasSprite textureatlassprite1 : stitcher.getStichSlots()) {
         bar.step(textureatlassprite1.getIconName());
         if (textureatlassprite1 == this.missingImage || this.generateMipmaps(textureatlassprite1)) {
            String s = textureatlassprite1.getIconName();
            map.remove(s);
            this.mapUploadedSprites.put(s, textureatlassprite1);

            try {
               GlStateManager.bindTexture(this.getGlTextureId());
               TextureUtil.uploadTextureMipmap(textureatlassprite1.getFrameTextureData(textureatlassprite1.getInitialFrameIndex()), textureatlassprite1.getIconWidth(), textureatlassprite1.getIconHeight(), textureatlassprite1.getOriginX(), textureatlassprite1.getOriginY(), false, false);
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

      for(EaglerTextureAtlasSprite textureatlassprite2 : map.values()) {
         textureatlassprite2.copyFrom(this.missingImage);
      }

      GlStateManager.bindTexture(this.getGlTextureId());
      ProgressManager.pop(bar);
   }

   private boolean generateMipmaps(EaglerTextureAtlasSprite texture) {
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

   private ResourceLocation getResourceLocation(EaglerTextureAtlasSprite p_184396_1_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_184396_1_.getIconName());
      return new ResourceLocation(resourcelocation.getResourceDomain(), HString.format("%s/%s%s", this.basePath, resourcelocation.getResourcePath(), ".png"));
   }
}
