package net.silentchaos512.lib.util;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.silentchaos512.lib.SilentLib;

@ParametersAreNonnullByDefault
public class I18nHelper {
   private final String modId;
   private final boolean clientSide;
   private final LogHelper log;
   private final boolean logServerTranslationAttempts;
   private final Set<String> triedToTranslateOnServer = new HashSet();
   private boolean replacesColons = true;

   public I18nHelper(String modId, LogHelper log, boolean logServerTranslationAttempts) {
      this.modId = modId;
      this.log = log;
      this.logServerTranslationAttempts = logServerTranslationAttempts;
      this.clientSide = GameUtil.isClient();
   }

   public String getKey(String prefix, String key) {
      return prefix + "." + this.modId + "." + key;
   }

   public String getKey(String prefix, String key, String suffix) {
      return prefix + "." + this.modId + "." + key + "." + suffix;
   }

   public String getKey(String prefix, ResourceLocation name) {
      return prefix + "." + name.getResourceDomain() + "." + name.getResourcePath();
   }

   public String getKey(IForgeRegistryEntry<?> object, String key) {
      String prefix = this.getPrefixFor(object);
      ResourceLocation name = (ResourceLocation)Objects.requireNonNull(object.getRegistryName());
      return prefix + "." + name.getResourceDomain() + "." + name.getResourcePath() + "." + key;
   }

   public boolean hasKey(String key) {
      return I18n.hasKey(key);
   }

   public String translate(String key, Object... params) {
      if (this.replacesColons) {
         key = key.replace(':', '.');
      }

      if (!this.clientSide) {
         if (this.logServerTranslationAttempts && !this.triedToTranslateOnServer.contains(key)) {
            this.log.warn("Tried to translate text on server side: {}", key);
            if (SilentLib.instance.isDevBuild()) {
               this.log.catching(new RuntimeException());
            }

            this.triedToTranslateOnServer.add(key);
         }

         return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(key, params);
      } else {
         return I18n.format(key, params);
      }
   }

   public String translate(String prefix, String key, Object... params) {
      return this.translate(this.getKey(prefix, key), params);
   }

   public String translatedName(Block block) {
      return this.translate(block.getUnlocalizedName() + ".name");
   }

   public String translatedName(Item item) {
      return this.translate(item.getUnlocalizedName() + ".name");
   }

   public String translatedName(ItemStack stack) {
      return this.translate(stack.getUnlocalizedName() + ".name");
   }

   public String subText(IForgeRegistryEntry<?> object, String key, Object... params) {
      return this.translate(this.getKey(object, key), params);
   }

   public String subText(String objName, String prefix, String key, Object... params) {
      return this.translate(this.getKey(prefix, objName, key), params);
   }

   public String blockSubText(String blockName, String key, Object... params) {
      return this.subText(blockName, "tile", key, params);
   }

   public String itemSubText(String itemName, String key, Object... params) {
      return this.subText(itemName, "item", key, params);
   }

   public String miscText(String key, Object... params) {
      return this.translate("misc", key, params);
   }

   public ITextComponent textComponent(String prefix, String key, Object... params) {
      return new TextComponentTranslation(this.getKey(prefix, key), params);
   }

   public ITextComponent textComponent(String prefix, String key, String suffix, Object... params) {
      return new TextComponentTranslation(this.getKey(prefix, key, suffix), params);
   }

   private String getPrefixFor(IForgeRegistryEntry<?> object) {
      if (object instanceof Item) {
         return "item";
      } else {
         return object instanceof Block ? "tile" : object.getClass().getName().toLowerCase(Locale.ROOT);
      }
   }

   public void setReplacesColons(boolean replacesColons) {
      this.replacesColons = replacesColons;
   }
}
