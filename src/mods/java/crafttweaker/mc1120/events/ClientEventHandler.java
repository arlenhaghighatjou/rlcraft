package crafttweaker.mc1120.events;

import crafttweaker.api.formatting.IFormattedText;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.tooltip.ITooltipFunction;
import crafttweaker.api.tooltip.IngredientTooltips;
import crafttweaker.mc1120.formatting.IMCFormattedString;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import stanhebben.zenscript.util.Pair;

public class ClientEventHandler {
   public ClientEventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onItemTooltip(ItemTooltipEvent ev) {
      List<String> tooltipList = ev.getToolTip();
      if (!ev.getItemStack().isEmpty()) {
         IItemStack itemStack = CraftTweakerMC.getIItemStackForMatching(ev.getItemStack());
         Boolean clearTooltipFlag = IngredientTooltips.shouldClearToolTip(itemStack);
         if (clearTooltipFlag != null) {
            (clearTooltipFlag.booleanValue() ? tooltipList.subList(1, tooltipList.size()) : tooltipList).clear();
         }

         List<String> toRemove = new ArrayList();

         for(Integer line : IngredientTooltips.getTooltipLinesToRemove(itemStack)) {
            if (line.intValue() > 0 && line.intValue() < tooltipList.size()) {
               toRemove.add(tooltipList.get(line.intValue()));
            }
         }

         for(Pattern regex : IngredientTooltips.getTooltipsToRemove(itemStack)) {
            for(String s : tooltipList) {
               if (regex.matcher(s).find()) {
                  toRemove.add(s);
               }
            }
         }

         tooltipList.removeAll(toRemove);

         for(Pair<IFormattedText, IFormattedText> tooltip : IngredientTooltips.getTooltips(itemStack)) {
            tooltipList.add(((IMCFormattedString)tooltip.getKey()).getTooltipString());
         }

         for(Pair<ITooltipFunction, ITooltipFunction> tooltip : IngredientTooltips.getAdvancedTooltips(itemStack)) {
            tooltipList.add(((ITooltipFunction)tooltip.getKey()).process(itemStack));
         }

         boolean pressed = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);

         for(Pair<IFormattedText, IFormattedText> tooltip : IngredientTooltips.getShiftTooltips(itemStack)) {
            if (pressed) {
               tooltipList.add(((IMCFormattedString)tooltip.getKey()).getTooltipString());
            } else if (tooltip.getValue() != null) {
               tooltipList.add(((IMCFormattedString)tooltip.getValue()).getTooltipString());
            }
         }

         for(Pair<ITooltipFunction, ITooltipFunction> tooltip : IngredientTooltips.getAdvancedShiftTooltips(itemStack)) {
            if (pressed) {
               tooltipList.add(((ITooltipFunction)tooltip.getKey()).process(itemStack));
            } else if (tooltip.getValue() != null) {
               tooltipList.add(((ITooltipFunction)tooltip.getValue()).process(itemStack));
            }
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onGuiOpenEvent(GuiOpenEvent ev) {
   }
}
