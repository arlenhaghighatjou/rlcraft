package org.jwaresoftware.mcmods.lib;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import org.apache.commons.lang3.StringUtils;

public final class Strings {
   private static final String _TT = "carrots.tooltip.";
   public static final String __DEFAULT_VAL = "*DEFAULT*";
   public static final String __NONE_VAL = "*NONE*";
   public static final int MAX_MENU_WIDTH = 33;
   public static final int MAX_DISPLAY_ITEM_COUNT = 10;

   public static final String translate(String key) {
      return I18n.translateToLocal(key);
   }

   public static final String translateDefault(String key, String defaultValue) {
      String out = translate(key);
      return !key.equals(out) && !StringUtils.isBlank(out) ? out : defaultValue;
   }

   public static final String translateFormatted(String key, Object... format) {
      return I18n.translateToLocalFormatted(key, format);
   }

   public static final String translateFormattedDefault(String key, String defaultValue, Object... format) {
      String out = I18n.translateToLocalFormatted(key, format);
      return !key.equals(out) && !StringUtils.isBlank(out) ? out : defaultValue;
   }

   public static final boolean hasTranslation(String key) {
      return I18n.canTranslate(key);
   }

   public static final String tooltipKey(String key) {
      return "carrots.tooltip." + key;
   }

   public static final String translateTooltip(String key) {
      return translate("carrots.tooltip." + key);
   }

   public static final String translateTooltip(String key, String defaultValue) {
      return translateDefault("carrots.tooltip." + key, defaultValue);
   }

   public static final boolean isDefaultValue(@Nullable String s, boolean emptyIsTrue) {
      s = StringUtils.trimToNull(s);
      return s == null ? emptyIsTrue : "DEFAULT".equals(StringUtils.strip(s.toUpperCase(Locale.US), "*_"));
   }

   public static final boolean isDefaultValue(@Nullable String s) {
      return isDefaultValue(s, true);
   }

   public static final String[] __DEFAULT_VAL_SETTINGS() {
      return new String[]{"*DEFAULT*", Boolean.TRUE.toString(), Boolean.FALSE.toString()};
   }

   public static final boolean isNoneValue(@Nullable String s, boolean emptyIsTrue) {
      s = StringUtils.trimToNull(s);
      return s == null ? emptyIsTrue : "NONE".equals(StringUtils.strip(s.toUpperCase(Locale.US), "*_"));
   }

   public static final boolean isNoneValue(@Nullable String s) {
      return isNoneValue(s, false);
   }

   public static final String boundedDisplayString(@Nonnull ItemStack item, int reserved_width) {
      return StringUtils.abbreviate(item.getDisplayName(), 33 - reserved_width);
   }

   public static final String boundedUiTipString(@Nonnull ItemStack item, int reserved_width) {
      String s = item.getDisplayName() + (ItemStacks.isSingle(item) ? "" : " (" + ItemStacks.getSize(item) + ")");
      return StringUtils.abbreviate(s, 33 - reserved_width);
   }

   private Strings() {
   }

   public static final void init() {
   }
}
