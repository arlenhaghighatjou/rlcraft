package org.orecruncher.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

public final class ForgeUtils {
   private ForgeUtils() {
   }

   @Nullable
   public static ModContainer findModContainer(@Nonnull String modId) {
      if (!StringUtils.isEmpty(modId)) {
         for(ModContainer mod : Loader.instance().getActiveModList()) {
            if (mod.getModId().equalsIgnoreCase(modId)) {
               return mod;
            }
         }
      }

      return null;
   }

   @Nullable
   public static ModMetadata getModMetadata(@Nonnull String modId) {
      ModContainer container = findModContainer(modId);
      return container != null ? container.getMetadata() : null;
   }

   @Nonnull
   public static String getModName(@Nonnull String modId) {
      if ("minecraft".equalsIgnoreCase(modId)) {
         return "Minecraft";
      } else {
         ModContainer cont = findModContainer(modId);
         return cont != null ? cont.getName() : "UNKNOWN";
      }
   }

   @Nonnull
   public static String getModName(@Nonnull ResourceLocation resource) {
      return getModName(resource.getResourceDomain());
   }

   @Nonnull
   public static String getForgeVersion() {
      ModContainer mod = findModContainer("forge");
      return mod != null ? mod.getVersion() : "";
   }

   @Nullable
   public static Item getItem(@Nonnull String resourceName) {
      return (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(resourceName));
   }
}
