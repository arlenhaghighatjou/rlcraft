package com.endertech.minecraft.forge;

import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.units.UnitId;
import java.util.Locale;
import net.minecraft.util.ResourceLocation;

public final class ForgeLocating {
   public static final String PATH_DELIMITER = "/";

   public ForgeLocating() {
   }

   protected static ResourceLocation texture(String modId, ForgeLocating.Folders folder, String name, String... subFolders) {
      String folderName = folder != ForgeLocating.Folders.TEXTURES ? folder.getName() : "";
      String subDir = joinPath(subFolders);
      String path = joinPath(ForgeLocating.Folders.TEXTURES.getName(), folderName, subDir, name.toLowerCase(Locale.ROOT) + ForgeLocating.Extensions.PNG.getWithDot());
      return new ResourceLocation(modId, path);
   }

   public static String joinPath(String... parts) {
      String path = String.join("/", parts);

      for(String doubleDelimiter = "//"; path.contains(doubleDelimiter); path = path.replace(doubleDelimiter, "/")) {
         ;
      }

      return path;
   }

   public static String joinName(String... words) {
      return UnitId.buildRegName(words);
   }

   public static ResourceLocation armorTexture(String modId, String name) {
      return texture(modId, ForgeLocating.Folders.MODELS, name, ForgeLocating.Folders.ARMOR.getName());
   }

   public static ResourceLocation blockTexture(String modId, String name, String... subFolders) {
      return texture(modId, ForgeLocating.Folders.BLOCKS, name, subFolders);
   }

   public static ResourceLocation entityTexture(String modId, String name, String... subFolders) {
      return texture(modId, ForgeLocating.Folders.ENTITY, name, subFolders);
   }

   public static ResourceLocation environmentTexture(String modId, String name, String... subFolders) {
      return texture(modId, ForgeLocating.Folders.ENVIRONMENT, name, subFolders);
   }

   public static ResourceLocation guiTexture(String modId, String name, String... subFolders) {
      return texture(modId, ForgeLocating.Folders.GUI, name, subFolders);
   }

   public static ResourceLocation itemTexture(String modId, String name, String... subFolders) {
      return texture(modId, ForgeLocating.Folders.ITEMS, name, subFolders);
   }

   public static ResourceLocation particleTexture(String modId, String name, String... subFolders) {
      return texture(modId, ForgeLocating.Folders.PARTICLE, name, subFolders);
   }

   public static ResourceLocation potionTexture(String modId, String name, String... subFolders) {
      return texture(modId, ForgeLocating.Folders.POTIONS, name, subFolders);
   }

   public static ResourceLocation fluidTexture(String modId, String fluidName, boolean flowing, String... subFolders) {
      String name = joinName(fluidName, flowing ? "flow" : "still");
      String path = joinPath(ForgeLocating.Folders.FLUIDS.getName(), joinPath(subFolders), name);
      return new ResourceLocation(modId, path);
   }

   public static enum Extensions implements IForgeEnum {
      PNG;

      private Extensions() {
      }

      public String getWithDot() {
         return "." + this.getName();
      }

      public String getName() {
         return this.getLowerCase(this.name());
      }
   }

   public static enum Folders implements IForgeEnum {
      ARMOR,
      BLOCKS,
      ENTITY,
      ENVIRONMENT,
      FLUIDS,
      ITEMS,
      GUI,
      MODELS,
      PARTICLE,
      POTIONS,
      TEXTURES;

      private Folders() {
      }

      public String getName() {
         return this.getLowerCase(this.name());
      }
   }
}
