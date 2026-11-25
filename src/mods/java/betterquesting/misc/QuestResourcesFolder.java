package betterquesting.misc;

import betterquesting.core.BetterQuesting;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackFileNotFoundException;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class QuestResourcesFolder implements IResourcePack {
   private static final ResourceLocation UNKNOWN_PACK_TEXTURE = new ResourceLocation("textures/misc/unknown_pack.png");
   private static final File rootFolder = new File("config/betterquesting/resources/");
   private BufferedImage bufferedImage = null;

   public QuestResourcesFolder() {
   }

   @Nonnull
   public InputStream getInputStream(@Nonnull ResourceLocation location) throws IOException {
      if (!this.resourceExists(location)) {
         throw new ResourcePackFileNotFoundException(rootFolder, location.toString());
      } else {
         return new FileInputStream(new File(rootFolder.getPath() + "/" + location.getResourceDomain(), location.getResourcePath()));
      }
   }

   public boolean resourceExists(@Nonnull ResourceLocation location) {
      File res = new File(rootFolder.getPath() + "/" + location.getResourceDomain(), location.getResourcePath());
      return res.exists();
   }

   @Nonnull
   public Set<String> getResourceDomains() {
      if (!rootFolder.exists() && !rootFolder.mkdirs()) {
         return Collections.emptySet();
      } else {
         String[] content = rootFolder.list();
         if (content != null && content.length > 0) {
            HashSet<String> folders = new HashSet();

            for(String s : content) {
               File f = new File(rootFolder, s);
               if (f.exists() && f.isDirectory()) {
                  if (!f.getName().equals(f.getName().toLowerCase())) {
                     this.logNameNotLowercase(f.getName(), f.toString());
                  } else {
                     folders.add(f.getName());
                  }
               }
            }

            return folders;
         } else {
            return Collections.emptySet();
         }
      }
   }

   public <T extends IMetadataSection> T getPackMetadata(@Nonnull MetadataSerializer meta, @Nonnull String s) {
      return (T)null;
   }

   @Nonnull
   public BufferedImage getPackImage() {
      if (this.bufferedImage != null) {
         return this.bufferedImage;
      } else {
         try {
            this.bufferedImage = TextureUtil.readBufferedImage(Minecraft.getMinecraft().getResourceManager().getResource(UNKNOWN_PACK_TEXTURE).getInputStream());
         } catch (IOException var2) {
            throw new Error("Couldn't bind resource pack icon", var2);
         }

         return this.bufferedImage;
      }
   }

   @Nonnull
   public String getPackName() {
      return "BetterQuesting_folders";
   }

   private void logNameNotLowercase(String name, String file) {
      BetterQuesting.logger.log(Level.WARN, "ResourcePack: ignored non-lowercase namespace: {} in {}", new Object[]{name, file});
   }
}
