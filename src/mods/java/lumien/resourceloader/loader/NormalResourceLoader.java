package lumien.resourceloader.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.minecraft.ResourceIndex;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class NormalResourceLoader implements IResourcePack {
   private final String root;
   private final String name;
   private final ResourceIndex index = new ResourceIndex() {
      @Override
      protected Collection<String> getPropertiesFiles0() {
         return listMinecraftAssets(".properties", "optifine", "mcpatcher");
      }

      @Override
      protected Collection<String> getCITPotionsFiles0() {
         return listMinecraftAssets(".png", "optifine/cit/potion", "mcpatcher/cit/potion");
      }
   };

   public NormalResourceLoader() {
      this("resources", "CustomResources");
   }

   protected NormalResourceLoader(String root, String name) {
      this.root = root;
      this.name = name;
   }

   @Override
   public InputStream getInputStream(ResourceLocation location) throws IOException {
      VFile2 path = this.resolve(location);
      InputStream stream = path == null ? null : path.getInputStream();
      if (stream == null) {
         throw new FileNotFoundException(location.toString());
      }
      return stream;
   }

   @Override
   public boolean resourceExists(ResourceLocation location) {
      VFile2 path = this.resolve(location);
      return path != null && path.exists();
   }

   private VFile2 resolve(ResourceLocation location) {
      String domain = location.getResourceDomain();
      if (domain.contains("/") || domain.contains("\\") || domain.equals(".") || domain.equals("..")) {
         return null;
      }
      String parent = this.root + "/" + domain + "/";
      VFile2 path = new VFile2(parent, location.getResourcePath());
      return path.getPath().startsWith(parent) ? path : null;
   }

   @Override
   public Set<String> getResourceDomains() {
      Set<String> domains = new LinkedHashSet<>();
      int prefixLength = this.root.length() + 1;
      for (String file : new VFile2(this.root).listFilenames(true)) {
         int separator = file.indexOf('/', prefixLength);
         if (separator > prefixLength) {
            domains.add(file.substring(prefixLength, separator));
         }
      }
      return domains;
   }

   private List<String> listMinecraftAssets(String suffix, String... folders) {
      String prefix = this.root + "/minecraft/";
      List<String> matches = new ArrayList<>();
      for (String folder : folders) {
         for (String file : new VFile2(prefix, folder).listFilenames(true)) {
            if (file.endsWith(suffix)) {
               matches.add(file.substring(prefix.length()));
            }
         }
      }
      return matches;
   }

   @Override
   public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer serializer, String section) {
      return null;
   }

   @Override
   public ImageData getPackImage() {
      return null;
   }

   @Override
   public String getPackName() {
      return this.name;
   }

   @Override
   public ResourceIndex getEaglerFileIndex() {
      return this.index;
   }
}
