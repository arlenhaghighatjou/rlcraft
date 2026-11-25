package org.orecruncher.dsurround.registry.config.packs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.lib.JsonUtils;

public final class ResourcePacks {
   public static final ResourceLocation MANIFEST_RESOURCE = new ResourceLocation("dsurround", "manifest.json");
   public static final ResourceLocation CONFIGURE_RESOURCE = new ResourceLocation("dsurround", "configure.json");

   private ResourcePacks() {
   }

   @Nonnull
   public static List<IMyResourcePack> findResourcePacks() {
      List<IMyResourcePack> foundEntries = new ArrayList();
      IMyResourcePack p = new ResourcePacks.Pack("dsurround");
      if (!p.hasManifest()) {
         throw new RuntimeException("Missing configuration!");
      } else {
         foundEntries.add(p);

         for(ModContainer mod : Loader.instance().getActiveModList()) {
            if (!mod.getModId().equals("dsurround")) {
               p = new ResourcePacks.Pack(mod.getModId());
               if (p.hasManifest()) {
                  foundEntries.add(p);
               }
            }
         }

         if (!ModBase.proxy().isRunningAsServer()) {
            for(Entry pack : Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries()) {
               IMyResourcePack var6 = new ResourcePacks.ResourcePack(pack.getResourcePack());
               if (var6.hasManifest()) {
                  foundEntries.add(var6);
               }
            }
         }

         return foundEntries;
      }
   }

   public static class Pack implements IMyResourcePack {
      protected final String modName;
      protected final String packPath;
      protected Manifest manifest;

      public Pack(@Nonnull String modName) {
         this.modName = modName;
         this.packPath = "/assets/" + modName + "/";
      }

      public boolean hasManifest() {
         if (this.manifest == null) {
            try {
               InputStream stream = this.getInputStream(ResourcePacks.MANIFEST_RESOURCE);
               Throwable var2 = null;

               try {
                  if (stream != null) {
                     this.manifest = (Manifest)JsonUtils.load(stream, Manifest.class);
                  }
               } catch (Throwable var12) {
                  var2 = var12;
                  throw var12;
               } finally {
                  if (stream != null) {
                     if (var2 != null) {
                        try {
                           stream.close();
                        } catch (Throwable var11) {
                           var2.addSuppressed(var11);
                        }
                     } else {
                        stream.close();
                     }
                  }

               }
            } catch (Throwable var14) {
               ;
            }
         }

         return this.manifest != null;
      }

      @Nonnull
      public String getModName() {
         return this.modName;
      }

      public InputStream getInputStream(@Nonnull ResourceLocation loc) {
         StringBuilder builder = new StringBuilder();
         builder.append(this.packPath);
         builder.append(loc.getResourceDomain()).append('/').append(loc.getResourcePath());
         return ModBase.class.getResourceAsStream(builder.toString());
      }

      public boolean resourceExists(@Nonnull ResourceLocation loc) {
         try {
            InputStream stream = this.getInputStream(loc);
            Throwable var3 = null;

            boolean var4;
            try {
               var4 = stream != null;
            } catch (Throwable var14) {
               var3 = var14;
               throw var14;
            } finally {
               if (stream != null) {
                  if (var3 != null) {
                     try {
                        stream.close();
                     } catch (Throwable var13) {
                        var3.addSuppressed(var13);
                     }
                  } else {
                     stream.close();
                  }
               }

            }

            return var4;
         } catch (Throwable var16) {
            return false;
         }
      }

      public String toString() {
         StringBuilder builder = new StringBuilder();
         builder.append("Resource pack ").append(this.getModName()).append(": ");
         if (this.manifest != null) {
            builder.append(String.format("%s by %s (%s)", this.manifest.getName(), this.manifest.getAuthor(), this.manifest.getWebsite()));
         } else {
            builder.append("No manifest");
         }

         return builder.toString();
      }
   }

   private static class ResourcePack implements IMyResourcePack {
      protected Manifest manifest;
      protected final IResourcePack pack;

      public ResourcePack(@Nonnull IResourcePack resource) {
         this.pack = resource;
      }

      @Nonnull
      public String getModName() {
         return this.pack.getPackName();
      }

      public boolean hasManifest() {
         if (this.manifest == null) {
            try {
               InputStream stream = this.getInputStream(ResourcePacks.MANIFEST_RESOURCE);
               Throwable var2 = null;

               try {
                  if (stream != null) {
                     this.manifest = (Manifest)JsonUtils.load(stream, Manifest.class);
                  }
               } catch (Throwable var12) {
                  var2 = var12;
                  throw var12;
               } finally {
                  if (stream != null) {
                     if (var2 != null) {
                        try {
                           stream.close();
                        } catch (Throwable var11) {
                           var2.addSuppressed(var11);
                        }
                     } else {
                        stream.close();
                     }
                  }

               }
            } catch (Throwable var14) {
               ;
            }
         }

         return this.manifest != null;
      }

      public InputStream getInputStream(@Nonnull ResourceLocation loc) throws IOException {
         return this.pack.getInputStream(loc);
      }

      public boolean resourceExists(@Nonnull ResourceLocation loc) {
         return this.pack.resourceExists(loc);
      }
   }
}
