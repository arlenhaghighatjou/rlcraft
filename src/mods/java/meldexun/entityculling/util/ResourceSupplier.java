package meldexun.entityculling.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class ResourceSupplier implements Supplier<String> {
   private final ResourceLocation file;

   public ResourceSupplier(ResourceLocation file) {
      this.file = file;
   }

   public String get() {
      StringBuilder sb = new StringBuilder();

      try {
         IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(this.file);
         Throwable var3 = null;

         try {
            Stream<String> stream = (new BufferedReader(new InputStreamReader(resource.getInputStream()))).lines();
            Throwable var5 = null;

            try {
               stream.forEach((s) -> {
                  sb.append(s);
                  sb.append('\n');
               });
            } catch (Throwable var30) {
               var5 = var30;
               throw var30;
            } finally {
               if (stream != null) {
                  if (var5 != null) {
                     try {
                        stream.close();
                     } catch (Throwable var29) {
                        var5.addSuppressed(var29);
                     }
                  } else {
                     stream.close();
                  }
               }

            }
         } catch (Throwable var32) {
            var3 = var32;
            throw var32;
         } finally {
            if (resource != null) {
               if (var3 != null) {
                  try {
                     resource.close();
                  } catch (Throwable var28) {
                     var3.addSuppressed(var28);
                  }
               } else {
                  resource.close();
               }
            }

         }
      } catch (IOException var34) {
         var34.printStackTrace();
      }

      return sb.toString();
   }
}
