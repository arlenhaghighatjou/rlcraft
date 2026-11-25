package com.teamacronymcoders.base.util.files.templates;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.teamacronymcoders.base.Base;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;

public class TemplateManager {
   private static Cache<String, TemplateFile> templates = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build();

   public TemplateManager() {
   }

   public static TemplateFile getTemplateFile(String name) {
      return getTemplateFile(new ResourceLocation("base", name));
   }

   @Nonnull
   public static TemplateFile getTemplateFile(ResourceLocation resourceLocation) {
      TemplateFile file = null;

      try {
         file = (TemplateFile)templates.get(resourceLocation.toString(), () -> {
            return new TemplateFile(resourceLocation, Base.instance.getLibProxy().getFileContents(resourceLocation));
         });
      } catch (ExecutionException var6) {
         Base.instance.getLogger().getLogger().error(var6);
      } finally {
         if (file == null) {
            file = new TemplateFile(new ResourceLocation("base:empty"), "");
         }

      }

      return file.copy();
   }
}
