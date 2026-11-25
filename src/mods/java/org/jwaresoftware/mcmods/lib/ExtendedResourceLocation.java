package org.jwaresoftware.mcmods.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

public final class ExtendedResourceLocation extends ModelResourceLocation {
   public static final String VARIANT_DELIMITER = "#";

   public ExtendedResourceLocation(String path) {
      super(path);
   }

   public ExtendedResourceLocation(ResourceLocation location, String variant) {
      super(location, variant);
   }

   public ExtendedResourceLocation(String location, String variant) {
      super(location, variant);
   }

   public ExtendedResourceLocation(@Nonnull String domain, String path, String variant) {
      super(domain + ":" + path, variant);
   }

   public final String getDefaultResourcePath() {
      return super.getResourcePath();
   }

   public String getResourcePath() {
      String s = super.getResourcePath();
      String v = this.getVariant();
      if (!"normal".equals(s)) {
         s = s + "#" + v;
      }

      return s;
   }

   public static final boolean isa(@Nullable ResourceLocation r) {
      return r instanceof ExtendedResourceLocation;
   }

   @Nonnull
   public static final ResourceLocation narrow(@Nonnull ResourceLocation r) {
      ExtendedResourceLocation xr = (ExtendedResourceLocation)r;
      return new ResourceLocation(xr.getResourceDomain(), xr.getDefaultResourcePath());
   }

   public static final boolean isa(@Nullable String rid) {
      int j = 0;
      return rid != null && rid.length() > 5 && (j = rid.indexOf(":")) > 0 && rid.indexOf(35) > j + 1;
   }
}
