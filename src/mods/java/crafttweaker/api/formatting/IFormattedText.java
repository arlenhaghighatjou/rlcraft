package crafttweaker.api.formatting;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.text.ITextComponent;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenClass("crafttweaker.formatting.IFormattedText")
@ZenRegister
public interface IFormattedText {
   @ZenOperator(OperatorType.ADD)
   IFormattedText add(IFormattedText var1);

   @ZenOperator(OperatorType.CAT)
   IFormattedText cat(IFormattedText var1);

   String getText();

   @ZenCaster
   @ZenMethod
   default ITextComponent asTextComponent() {
      CraftTweakerAPI.logError(this.getClass().getCanonicalName() + " doesn't override IFormattedText#asTextComponent! It is a bug!");
      return null;
   }
}
