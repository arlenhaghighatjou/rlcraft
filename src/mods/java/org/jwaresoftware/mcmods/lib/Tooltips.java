package org.jwaresoftware.mcmods.lib;

import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public final class Tooltips {
   private static final String _MISSING = "\\o/";
   private static final int _TOOWIDE = 200;
   static final AtomicBoolean _defaultFlag = new AtomicBoolean(false);
   static final ConcurrentMap<ModIntegration, Boolean> _hiddenFlags = new ConcurrentHashMap();
   private static boolean _SIMPLE_FORM_SENTENCE = false;
   private static int _GRAY_LEN = 1;
   private static int _RESET_LEN = 1;

   private static boolean hideDocTooltips(ModIntegration modid) {
      return modid == null ? _defaultFlag.get() : ((Boolean)_hiddenFlags.getOrDefault(modid, Boolean.valueOf(false))).booleanValue();
   }

   public static void setHidden(ModIntegration modid, boolean flag) {
      if (modid != null) {
         _hiddenFlags.put(modid, Boolean.valueOf(flag));
      } else {
         _defaultFlag.set(flag);
      }

   }

   public static void setHidden(boolean flag) {
      setHidden((ModIntegration)null, flag);
   }

   public static final boolean hasUiTip(@Nonnull Block block) {
      String i18n_key = block.getUnlocalizedName();
      String uitip = Strings.translateDefault("tooltip." + i18n_key, "\\o/");
      if (!"\\o/".equals(uitip)) {
         return true;
      } else {
         uitip = Strings.translateDefault("tooltip_summ." + i18n_key, "\\o/");
         return !"\\o/".equals(uitip);
      }
   }

   @SideOnly(Side.CLIENT)
   private static void addWrapped(ItemStack stack, String displayName, String uitip, int maxlinewidth, List<String> tooltip) {
      FontRenderer fontRenderer = stack.getItem().getFontRenderer(stack);
      if (fontRenderer == null) {
         fontRenderer = Minecraft.getMinecraft().fontRenderer;
      }

      int width = fontRenderer.getStringWidth(displayName);
      List<String> lines = fontRenderer.listFormattedStringToWidth(uitip, Math.max(width, maxlinewidth));
      ListIterator<String> litr = lines.listIterator();

      while(litr.hasNext()) {
         String line = (String)litr.next();
         litr.set("" + TextFormatting.GRAY + line + TextFormatting.RESET);
      }

      tooltip.addAll(lines);
   }

   @SideOnly(Side.CLIENT)
   public static final boolean addDocTipLine(ItemStack stack, List<String> tooltip, String tip_id_prefix) {
      boolean did = false;
      String uitip = Strings.translateDefault(tip_id_prefix + "." + stack.getItem().getUnlocalizedName(stack), "\\o/");
      if (!"\\o/".equals(uitip)) {
         tooltip.add("" + TextFormatting.GRAY + uitip + TextFormatting.RESET);
         did = true;
      }

      return did;
   }

   @SideOnly(Side.CLIENT)
   public static final boolean addDocTipLine(@Nonnull ModIntegration modid, ItemStack stack, List<String> tooltip, String tip_id_prefix) {
      return hideDocTooltips(modid) ? false : addDocTipLine(stack, tooltip, tip_id_prefix);
   }

   static final void initLanguageSupport() {
      Locale SPANISH = new Locale("es");
      String lang = Locale.getDefault().getLanguage();
      _SIMPLE_FORM_SENTENCE = lang.isEmpty() || lang.equals(Locale.ENGLISH.getLanguage()) || lang.equals(SPANISH.getLanguage()) || lang.equals(Locale.FRENCH.getLanguage()) || lang.equals(Locale.GERMAN.getLanguage());
      _GRAY_LEN = TextFormatting.GRAY.toString().length();
      _RESET_LEN = TextFormatting.RESET.toString().length();
   }

   @SideOnly(Side.CLIENT)
   public static final boolean addDocTip(ItemStack stack, List<String> tooltip, String tip_id_summ, String tip_id_full) {
      boolean want_full = wantsFullDescriptionTip();
      boolean has_full = tip_id_full != null && Strings.hasTranslation(tip_id_full);
      String tip_id = tip_id_summ;
      if (want_full && has_full) {
         tip_id = tip_id_full;
      }

      boolean did = false;
      String uitip = Strings.translateDefault(tip_id, "\\o/");
      if (!"\\o/".equals(uitip)) {
         if (uitip.startsWith("{SUMM}") && tip_id == tip_id_full) {
            String uitip_summ = Strings.translateDefault(tip_id_summ, "\\o/");
            if (!"\\o/".equals(uitip_summ)) {
               uitip = uitip_summ + uitip.substring(6);
            }
         }

         addWrapped(stack, (String)tooltip.get(0), uitip, 200, tooltip);
         if (!want_full && has_full) {
            if (_SIMPLE_FORM_SENTENCE) {
               StringBuilder ll = new StringBuilder(300);
               String last = (String)tooltip.get(tooltip.size() - 1);
               ll.append(last.substring(_GRAY_LEN, last.length() - _RESET_LEN));
               char ch = ll.charAt(ll.length() - 1);
               if (ch != '.' && ch != '!' && Character.isLetterOrDigit(ch)) {
                  ll.append('.');
               }

               ll.append(" ").append(Strings.translateTooltip("full.description.howto"));
               tooltip.remove(tooltip.size() - 1);
               addWrapped(stack, (String)tooltip.get(0), ll.toString(), 200, tooltip);
            } else {
               tooltip.add(Strings.translateTooltip("full.description.howto"));
            }
         }

         if (Enchants.isEnchanted(stack, false)) {
            tooltip.add("");
         }

         did = true;
      }

      return did;
   }

   @SideOnly(Side.CLIENT)
   public static final boolean addDocTip(ModIntegration modid, ItemStack stack, List<String> tooltip, String tip_id_summ, String tip_id_full) {
      return hideDocTooltips(modid) ? false : addDocTip(stack, tooltip, tip_id_summ, tip_id_full);
   }

   @SideOnly(Side.CLIENT)
   public static final boolean addDocTipDefault(@Nonnull ItemStack stack, List<String> tooltip) {
      String i18n = stack.getItem().getUnlocalizedName(stack);
      return addDocTip(stack, tooltip, "tooltip_summ." + i18n, "tooltip_full." + i18n);
   }

   @SideOnly(Side.CLIENT)
   public static final boolean addDocTipDefault(ModIntegration modid, @Nonnull ItemStack stack, List<String> tooltip) {
      return hideDocTooltips(modid) ? false : addDocTipDefault(stack, tooltip);
   }

   @SideOnly(Side.CLIENT)
   public static final boolean addDocTipSharedSummary(@Nonnull ItemStack stack, List<String> tooltip, String tip_id_summ) {
      String i18n = stack.getItem().getUnlocalizedName(stack);
      return addDocTip(stack, tooltip, tip_id_summ, "tooltip_full." + i18n);
   }

   @SideOnly(Side.CLIENT)
   public static final boolean addDocTipSharedSummary(ModIntegration modid, @Nonnull ItemStack stack, List<String> tooltip, String tip_id_summ) {
      return hideDocTooltips(modid) ? false : addDocTipSharedSummary(stack, tooltip, tip_id_summ);
   }

   @SideOnly(Side.CLIENT)
   public static final void addHowToMoreItemDetailsTip(List<String> tooltip, boolean spacing) {
      if (spacing && !tooltip.isEmpty() && !((String)tooltip.get(tooltip.size() - 1)).isEmpty()) {
         tooltip.add("");
      }

      tooltip.add(Strings.translateTooltip("more.itemdetails.howto"));
   }

   @SideOnly(Side.CLIENT)
   public static final boolean extendI18nDocTip(String tip_id, ItemStack stack, List<String> tooltip) {
      boolean did = false;
      String uitip = Strings.translateDefault("" + tip_id + "." + stack.getItem().getUnlocalizedName(stack), "\\o/");
      if (!"\\o/".equals(uitip)) {
         uitip = "" + TextFormatting.GRAY + uitip + TextFormatting.RESET;
         if (tooltip.isEmpty()) {
            tooltip.add(uitip);
         } else if (((String)tooltip.get(tooltip.size() - 1)).isEmpty()) {
            tooltip.set(tooltip.size() - 1, uitip);
            tooltip.add("");
         } else {
            tooltip.add((String)tooltip.get(tooltip.size() - 1) + uitip);
         }

         did = true;
      }

      return did;
   }

   @SideOnly(Side.CLIENT)
   public static final boolean extendDocTip(@Nonnull ItemStack stack, List<String> tooltip, String uitip) {
      boolean shift = false;
      if (!tooltip.isEmpty() && ((String)tooltip.get(tooltip.size() - 1)).isEmpty()) {
         tooltip.set(tooltip.size() - 1, uitip);
         tooltip.add("");
         shift = true;
      }

      if (!shift) {
         tooltip.add(uitip);
      }

      return shift;
   }

   @SideOnly(Side.CLIENT)
   public static final boolean addOwnerTip(@Nonnull ItemStack stack, List<String> tooltip) {
      boolean did = false;
      OwnerDef owner = Instructions.getOwnerDef(stack);
      if (owner != null) {
         String name = owner.name;
         if (name == null) {
            name = Strings.translate("entity.HIDDEN.name");
         }

         addTipLine(tooltip, Strings.tooltipKey("owner"), TextFormatting.GOLD, (TextFormatting)null, name);
         did = true;
      }

      return did;
   }

   @SideOnly(Side.CLIENT)
   public static final void addTipLine(List<String> tooltip, String labelid, TextFormatting labelcolor, TextFormatting valuecolor, Object value) {
      if (valuecolor == null) {
         valuecolor = TextFormatting.GRAY;
      }

      String displayString;
      if (labelcolor == null) {
         displayString = Strings.translate(labelid) + value;
      } else {
         displayString = "" + labelcolor + Strings.translate(labelid) + valuecolor + value;
      }

      tooltip.add(displayString);
   }

   @SideOnly(Side.CLIENT)
   public static final void addTipLine(List<String> tooltip, String labelid, TextFormatting labelcolor, Object line) {
      addTipLine(tooltip, labelid, labelcolor, (TextFormatting)null, line);
   }

   @SideOnly(Side.CLIENT)
   public static final boolean wantsFullDescriptionTip() {
      return Keyboard.isKeyDown(54);
   }

   @SideOnly(Side.CLIENT)
   public static final boolean wantsMoreItemDetailsTip(boolean advancedFlag) {
      return advancedFlag || Keyboard.isKeyDown(42);
   }

   @SideOnly(Side.CLIENT)
   public static final String getFullDescriptionLabel() {
      return Strings.translateTooltip("full.description.howto", "+RSHIFT for full description...");
   }

   @SideOnly(Side.CLIENT)
   public static final String getMoreItemDetailsLabel() {
      return Strings.translateTooltip("more.itemdetails.howto", "+LSHIFT for more details...");
   }

   @SideOnly(Side.CLIENT)
   public static final String getFullDescriptionKeyLabel() {
      return Strings.translateTooltip("key.RSHIFT", "+RSHIFT");
   }

   @SideOnly(Side.CLIENT)
   public static final String getMoreItemDetailsKeyLabel() {
      return Strings.translateTooltip("key.LSHIFT", "+LSHIFT");
   }

   @SideOnly(Side.CLIENT)
   public static final String getUseKeyLabel() {
      KeyBinding keyClickBinding = Minecraft.getMinecraft().gameSettings.keyBindUseItem;
      return keyClickBinding.getDisplayName();
   }

   @SideOnly(Side.CLIENT)
   public static final String getSneakKeyLabel() {
      KeyBinding keyClickBinding = Minecraft.getMinecraft().gameSettings.keyBindSneak;
      return keyClickBinding.getDisplayName();
   }

   private Tooltips() {
   }
}
