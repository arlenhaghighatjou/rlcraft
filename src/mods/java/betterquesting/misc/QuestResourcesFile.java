package betterquesting.misc;

import betterquesting.core.BetterQuesting;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackFileNotFoundException;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class QuestResourcesFile implements IResourcePack, Closeable {
   private static final ResourceLocation UNKNOWN_PACK_TEXTURE = new ResourceLocation("textures/misc/unknown_pack.png");
   private static final File rootFolder = new File("config/betterquesting/resources/");
   private static final Splitter entryNameSplitter = Splitter.on('/').omitEmptyStrings().limit(3);
   private List<ZipFile> zipList = null;
   private BufferedImage bufferedImage = null;

   public QuestResourcesFile() {
   }

   @Nonnull
   public InputStream getInputStream(@Nonnull ResourceLocation loc) throws IOException {
      String locName = locationToName(loc);

      for(ZipFile zipfile : this.getZipFiles()) {
         ZipEntry zipentry = zipfile.getEntry(locName);
         if (zipentry != null) {
            return zipfile.getInputStream(zipentry);
         }
      }

      throw new ResourcePackFileNotFoundException(rootFolder, locName);
   }

   public boolean resourceExists(@Nonnull ResourceLocation loc) {
      String locName = locationToName(loc);

      try {
         for(ZipFile zipfile : this.getZipFiles()) {
            ZipEntry zipentry = zipfile.getEntry(locName);
            if (zipentry != null) {
               return true;
            }
         }
      } catch (Exception var6) {
         ;
      }

      return false;
   }

   @Nonnull
   public Set<String> getResourceDomains() {
      HashSet<String> hashset = Sets.newHashSet();

      try {
         for(ZipFile f : this.getZipFiles()) {
            hashset.addAll(this.GetZipDomains(f));
         }
      } catch (Exception var4) {
         ;
      }

      return hashset;
   }

   private Set<String> GetZipDomains(ZipFile zipfile) {
      Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
      HashSet<String> hashset = Sets.newHashSet();

      while(enumeration.hasMoreElements()) {
         ZipEntry zipentry = (ZipEntry)enumeration.nextElement();
         String s = zipentry.getName();
         if (s.startsWith("assets/")) {
            ArrayList<String> arraylist = Lists.newArrayList(entryNameSplitter.split(s));
            if (arraylist.size() > 1) {
               String s1 = (String)arraylist.get(1);
               if (!s1.equals(s1.toLowerCase())) {
                  this.logNameNotLowercase(s1, zipfile.getName());
               } else {
                  hashset.add(s1);
               }
            }
         }
      }

      return hashset;
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
      return "BetterQuesting_files";
   }

   private List<ZipFile> getZipFiles() {
      if (this.zipList != null) {
         return this.zipList;
      } else if (!rootFolder.exists() && !rootFolder.mkdirs()) {
         return Collections.emptyList();
      } else {
         File[] files = rootFolder.listFiles();
         if (files != null && files.length > 0) {
            this.zipList = new ArrayList();

            for(File f : files) {
               if (f.exists() && f.isFile()) {
                  try {
                     this.zipList.add(new ZipFile(f));
                  } catch (Exception var7) {
                     ;
                  }
               }
            }

            return this.zipList;
         } else {
            return Collections.emptyList();
         }
      }
   }

   private static String locationToName(ResourceLocation loc) {
      return String.format("%s/%s/%s", "assets", loc.getResourceDomain(), loc.getResourcePath());
   }

   private void logNameNotLowercase(String name, String file) {
      BetterQuesting.logger.log(Level.WARN, "ResourcePack: ignored non-lowercase namespace: {} in {}", new Object[]{name, file});
   }

   public void finalize() throws Throwable {
      this.close();
      super.finalize();
   }

   public void close() throws IOException {
      if (this.zipList != null) {
         for(ZipFile zip : this.zipList) {
            if (zip != null) {
               zip.close();
            }
         }

         this.zipList = null;
      }

   }
}
