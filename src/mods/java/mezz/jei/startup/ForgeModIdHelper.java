package mezz.jei.startup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.config.Config;
import mezz.jei.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.lang3.StringUtils;

public class ForgeModIdHelper extends AbstractModIdHelper {
   private static final String MOD_NAME_FORMAT_CODE = "%MODNAME%";
   @Nullable
   private static ForgeModIdHelper INSTANCE;
   private final Map<String, ModContainer> modMap = Loader.instance().getIndexedModList();

   public static IModIdHelper getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ForgeModIdHelper();
      }

      return INSTANCE;
   }

   private ForgeModIdHelper() {
   }

   public String getModNameForModId(String modId) {
      ModContainer modContainer = (ModContainer)this.modMap.get(modId);
      return modContainer == null ? modId : modContainer.getName();
   }

   public String getFormattedModNameForModId(String modId) {
      String modNameFormat = Config.getModNameFormat();
      if (modNameFormat.isEmpty()) {
         return null;
      } else {
         String modName = this.getModNameForModId(modId);
         modName = removeChatFormatting(modName);
         return modNameFormat.contains("%MODNAME%") ? StringUtils.replaceOnce(modNameFormat, "%MODNAME%", modName) : modNameFormat + modName;
      }
   }

   private static String removeChatFormatting(String string) {
      String withoutFormattingCodes = TextFormatting.getTextWithoutFormattingCodes(string);
      return withoutFormattingCodes == null ? "" : withoutFormattingCodes;
   }

   @Nullable
   public String getModNameTooltipFormatting() {
      try {
         ItemStack itemStack = new ItemStack(Items.APPLE);
         EntityPlayerSP player = Minecraft.getMinecraft().player;
         List<String> tooltip = new ArrayList();
         tooltip.add("JEI Tooltip Testing for mod name formatting");
         ItemTooltipEvent tooltipEvent = ForgeEventFactory.onItemTooltip(itemStack, player, tooltip, TooltipFlags.NORMAL);
         tooltip = tooltipEvent.getToolTip();
         if (tooltip.size() > 1) {
            for(int lineNum = 1; lineNum < tooltip.size(); ++lineNum) {
               String line = (String)tooltip.get(lineNum);
               if (line.contains("Minecraft")) {
                  String withoutFormatting = TextFormatting.getTextWithoutFormattingCodes(line);
                  if (withoutFormatting != null) {
                     if (line.equals(withoutFormatting)) {
                        return "";
                     }

                     if (line.contains(withoutFormatting)) {
                        return StringUtils.replaceOnce(line, "Minecraft", "%MODNAME%");
                     }
                  }
               }
            }
         }
      } catch (RuntimeException | LinkageError var8) {
         Log.get().error("Error while Testing for mod name formatting", var8);
      }

      return null;
   }

   public <T> List<String> addModNameToIngredientTooltip(List<String> tooltip, T ingredient, IIngredientHelper<T> ingredientHelper) {
      if (Config.isDebugModeEnabled() && Minecraft.getMinecraft().gameSettings.advancedItemTooltips) {
         tooltip = new ArrayList(tooltip);
         tooltip.add(TextFormatting.GRAY + "JEI Debug:");
         tooltip.add(TextFormatting.GRAY + "info: " + ingredientHelper.getErrorInfo(ingredient));
         tooltip.add(TextFormatting.GRAY + "uid: " + ingredientHelper.getUniqueId(ingredient));
      }

      return !Config.isModNameFormatOverrideActive() || !(ingredient instanceof ItemStack) && !(ingredient instanceof EnchantmentData) ? super.addModNameToIngredientTooltip(tooltip, ingredient, ingredientHelper) : tooltip;
   }
}
