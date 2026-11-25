package slimeknights.mantle.client.book.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.SectionData;

public class FileRepository extends BookRepository {
   public final String location;

   public FileRepository(String location) {
      this.location = location;
   }

   public List<SectionData> getSections() {
      return new ArrayList(Arrays.asList(BookLoader.GSON.fromJson(this.resourceToString(this.getResource(this.getResourceLocation("index.json"))), SectionData[].class)));
   }

   public ResourceLocation getResourceLocation(String path, boolean safe) {
      if (path == null) {
         return safe ? new ResourceLocation("") : null;
      } else if (!path.contains(":")) {
         String langPath = null;
         if (Minecraft.getMinecraft().getLanguageManager() != null && Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage() != null) {
            langPath = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
         }

         String defaultLangPath = "en_US";
         if (langPath != null) {
            ResourceLocation res = new ResourceLocation(this.location + "/" + langPath + "/" + path);
            if (this.resourceExists(res)) {
               return res;
            }
         }

         ResourceLocation res = new ResourceLocation(this.location + "/" + defaultLangPath + "/" + path);
         if (this.resourceExists(res)) {
            return res;
         } else {
            res = new ResourceLocation(this.location + "/" + path);
            if (this.resourceExists(res)) {
               return res;
            } else {
               return safe ? new ResourceLocation("") : null;
            }
         }
      } else {
         ResourceLocation res = new ResourceLocation(path);
         if (this.resourceExists(res)) {
            return res;
         } else {
            return safe ? new ResourceLocation("") : null;
         }
      }
   }

   public IResource getResource(ResourceLocation loc) {
      if (loc == null) {
         return null;
      } else {
         try {
            return Minecraft.getMinecraft().getResourceManager().getResource(loc);
         } catch (IOException var3) {
            return null;
         }
      }
   }

   public boolean resourceExists(ResourceLocation location) {
      if (location == null) {
         return false;
      } else {
         try {
            Minecraft.getMinecraft().getResourceManager().getResource(location);
            return true;
         } catch (IOException var3) {
            return false;
         }
      }
   }

   public String resourceToString(IResource resource, boolean skipCommments) {
      if (resource == null) {
         return "";
      } else {
         try {
            Iterator iterator = IOUtils.readLines(resource.getInputStream(), Charsets.UTF_8).iterator();
            StringBuilder builder = new StringBuilder();
            boolean isLongComment = false;

            while(iterator.hasNext()) {
               String s = ((String)iterator.next()).trim() + "\n";
               if (skipCommments) {
                  if (isLongComment) {
                     if (s.endsWith("*/")) {
                        isLongComment = false;
                     }
                     continue;
                  }

                  if (s.startsWith("/*")) {
                     isLongComment = true;
                     continue;
                  }

                  if (s.startsWith("//")) {
                     continue;
                  }
               }

               builder.append(s);
            }

            return builder.toString().trim();
         } catch (IOException var7) {
            var7.printStackTrace();
            return "";
         }
      }
   }
}
