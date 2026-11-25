package crafttweaker.api.tooltip;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.formatting.IFormattedText;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.util.IngredientMap;
import java.util.List;
import java.util.regex.Pattern;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.util.Pair;

@ZenExpansion("crafttweaker.item.IIngredient")
@ZenRegister
public class IngredientTooltips {
   private static final IngredientMap<Pair<IFormattedText, IFormattedText>> TOOLTIPS = new IngredientMap<Pair<IFormattedText, IFormattedText>>();
   private static final IngredientMap<Pair<IFormattedText, IFormattedText>> SHIFT_TOOLTIPS = new IngredientMap<Pair<IFormattedText, IFormattedText>>();
   private static final IngredientMap<Boolean> CLEARED_TOOLTIPS = new IngredientMap<Boolean>();
   private static final IngredientMap<Pair<ITooltipFunction, ITooltipFunction>> TOOLTIP_FUNCTIONS = new IngredientMap<Pair<ITooltipFunction, ITooltipFunction>>();
   private static final IngredientMap<Pair<ITooltipFunction, ITooltipFunction>> SHIFT_TOOLTIP_FUNCTIONS = new IngredientMap<Pair<ITooltipFunction, ITooltipFunction>>();
   private static final IngredientMap<Pattern> REMOVED_TOOLTIPS = new IngredientMap<Pattern>();
   private static final IngredientMap<Integer> REMOVED_TOOLTIPS_LINE = new IngredientMap<Integer>();

   public IngredientTooltips() {
   }

   @ZenMethod
   public static void addTooltip(IIngredient ingredient, IFormattedText tooltip) {
      CraftTweakerAPI.apply(new IngredientTooltips.AddTooltipAction(ingredient, tooltip, false, (IFormattedText)null));
   }

   @ZenMethod
   public static void addAdvancedTooltip(IIngredient ingredient, ITooltipFunction function) {
      CraftTweakerAPI.apply(new IngredientTooltips.AddAdvancedTooltipAction(ingredient, function, false, (ITooltipFunction)null));
   }

   @ZenMethod
   public static void addShiftTooltip(IIngredient ingredient, IFormattedText tooltip, @Optional IFormattedText showMessage) {
      CraftTweakerAPI.apply(new IngredientTooltips.AddTooltipAction(ingredient, tooltip, true, showMessage));
   }

   @ZenMethod
   public static void addShiftTooltip(IIngredient ingredient, ITooltipFunction function, @Optional ITooltipFunction showMessage) {
      CraftTweakerAPI.apply(new IngredientTooltips.AddAdvancedTooltipAction(ingredient, function, true, showMessage));
   }

   @ZenMethod
   public static void clearTooltip(IIngredient ingredient) {
      clearTooltip(ingredient, false);
   }

   @ZenMethod
   public static void clearTooltip(IIngredient ingredient, boolean leaveName) {
      CraftTweakerAPI.apply(new IngredientTooltips.ClearTooltipAction(ingredient, leaveName));
   }

   @ZenMethod
   public static void removeTooltip(IIngredient ingredient, String regex) {
      CraftTweakerAPI.apply(new IngredientTooltips.RemoveTooltipAction(ingredient, regex));
   }

   @ZenMethod
   public static void removeTooltipLine(IIngredient ingredient, int line) {
      CraftTweakerAPI.apply(new IngredientTooltips.RemoveTooltipLineAction(ingredient, line));
   }

   public static List<Pair<IFormattedText, IFormattedText>> getTooltips(IItemStack item) {
      return TOOLTIPS.getEntries(item);
   }

   public static List<Pair<IFormattedText, IFormattedText>> getShiftTooltips(IItemStack item) {
      return SHIFT_TOOLTIPS.getEntries(item);
   }

   public static List<Pair<ITooltipFunction, ITooltipFunction>> getAdvancedTooltips(IItemStack item) {
      return TOOLTIP_FUNCTIONS.getEntries(item);
   }

   public static List<Pair<ITooltipFunction, ITooltipFunction>> getAdvancedShiftTooltips(IItemStack item) {
      return SHIFT_TOOLTIP_FUNCTIONS.getEntries(item);
   }

   public static List<Pattern> getTooltipsToRemove(IItemStack item) {
      return REMOVED_TOOLTIPS.getEntries(item);
   }

   public static List<Integer> getTooltipLinesToRemove(IItemStack item) {
      return REMOVED_TOOLTIPS_LINE.getEntries(item);
   }

   public static Boolean shouldClearToolTip(IItemStack item) {
      List<Boolean> entries = CLEARED_TOOLTIPS.getEntries(item);
      return entries.isEmpty() ? null : (Boolean)entries.get(0);
   }

   private static class AddAdvancedTooltipAction implements IAction {
      private final IIngredient ingredient;
      private final ITooltipFunction function;
      private final boolean shift;
      private final ITooltipFunction showMessage;

      public AddAdvancedTooltipAction(IIngredient ingredient, ITooltipFunction function, boolean shift, ITooltipFunction showMessage) {
         this.ingredient = ingredient;
         this.function = function;
         this.shift = shift;
         this.showMessage = showMessage;
      }

      public void apply() {
         (this.shift ? IngredientTooltips.SHIFT_TOOLTIP_FUNCTIONS : IngredientTooltips.TOOLTIP_FUNCTIONS).register(this.ingredient, new Pair(this.function, this.showMessage));
      }

      public String describe() {
         return "Adding advanced tooltip for " + this.ingredient;
      }
   }

   private static class AddTooltipAction implements IAction {
      private final IIngredient ingredient;
      private final IFormattedText tooltip;
      private final boolean shift;
      private final IFormattedText showMessage;

      public AddTooltipAction(IIngredient ingredient, IFormattedText tooltip, boolean shift, IFormattedText showMessage) {
         this.ingredient = ingredient;
         this.tooltip = tooltip;
         this.shift = shift;
         this.showMessage = showMessage;
      }

      public void apply() {
         (this.shift ? IngredientTooltips.SHIFT_TOOLTIPS : IngredientTooltips.TOOLTIPS).register(this.ingredient, new Pair(this.tooltip, this.showMessage));
      }

      public String describe() {
         return "Adding tooltip for " + this.ingredient + ": \"" + this.tooltip.getText() + "\"";
      }
   }

   private static class ClearTooltipAction implements IAction {
      private final IIngredient ingredient;
      private final boolean leaveName;

      public ClearTooltipAction(IIngredient ingredient) {
         this(ingredient, false);
      }

      public ClearTooltipAction(IIngredient ingredient, boolean leaveItemName) {
         this.ingredient = ingredient;
         this.leaveName = leaveItemName;
      }

      public void apply() {
         IngredientTooltips.CLEARED_TOOLTIPS.register(this.ingredient, Boolean.valueOf(this.leaveName));
      }

      public String describe() {
         return "Clearing tooltip for " + this.ingredient;
      }
   }

   private static class RemoveTooltipAction implements IAction {
      private final IIngredient ingredient;
      private final String regex;

      public RemoveTooltipAction(IIngredient ingredient, String regex) {
         this.ingredient = ingredient;
         this.regex = regex;
      }

      public void apply() {
         IngredientTooltips.REMOVED_TOOLTIPS.register(this.ingredient, Pattern.compile(this.regex));
      }

      public String describe() {
         return "Removing tooltip for " + this.ingredient + ": \"" + this.regex + "\"";
      }
   }

   private static class RemoveTooltipLineAction implements IAction {
      private final IIngredient ingredient;
      private final int line;

      public RemoveTooltipLineAction(IIngredient ingredient, int line) {
         this.ingredient = ingredient;
         this.line = line;
      }

      public void apply() {
         IngredientTooltips.REMOVED_TOOLTIPS_LINE.register(this.ingredient, Integer.valueOf(this.line));
      }

      public String describe() {
         return "Removing tooltip for " + this.ingredient + " at line " + this.line;
      }
   }
}
