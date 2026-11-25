package com.endertech.minecraft.forge.messages;

import com.endertech.common.Args;
import com.endertech.minecraft.forge.configs.ForgeKeys;
import com.endertech.minecraft.forge.units.IForgeUnit;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Tooltip {
   private final Class<?> clazz;
   private final List<String> lines;

   protected Tooltip(Class<?> clazz, List<String> lines) {
      this.clazz = clazz;
      this.lines = lines;
   }

   public static Tooltip from(Class<?> clazz, List<String> lines) {
      return new Tooltip(clazz, lines);
   }

   protected String expandLangKey(String langKey) {
      return Args.joinDelim(".", "tooltip", IForgeUnit.getClassRegName(this.clazz), langKey);
   }

   public Tooltip add(TextFormatting textFormat, String langKey) {
      return this.add(textFormat, langKey, (Object)null);
   }

   public Tooltip add(TextFormatting textFormat, String langKey, @Nullable Object value) {
      String key = this.expandLangKey(langKey);
      String postfix = value != null ? ": " + value : "";
      this.lines.add(textFormat + I18n.format(key, new Object[0]) + postfix);
      return this;
   }

   public Tooltip add(String langKey, Object... parameters) {
      this.lines.add(I18n.format(this.expandLangKey(langKey), parameters));
      return this;
   }

   public String button(String langKey, KeyBinding button) {
      String action = langKey.isEmpty() ? "" : I18n.format(this.expandLangKey(langKey), new Object[0]) + " ";
      return action + ForgeKeys.getName(button);
   }
}
