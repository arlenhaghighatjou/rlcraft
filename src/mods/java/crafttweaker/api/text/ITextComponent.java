package crafttweaker.api.text;

import crafttweaker.annotations.ZenRegister;
import java.util.List;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenRegister
@ZenClass("crafttweaker.text.ITextComponent")
public interface ITextComponent {
   @ZenOperator(OperatorType.ADD)
   ITextComponent append(ITextComponent var1);

   @ZenOperator(OperatorType.CAT)
   default ITextComponent cat(ITextComponent text) {
      return this.append(text);
   }

   @ZenSetter("style")
   void setStyle(IStyle var1);

   @ZenGetter("style")
   IStyle getStyle();

   @ZenGetter("unformattedComponentText")
   String getUnformattedComponentText();

   @ZenGetter("unformattedText")
   String getUnformattedText();

   @ZenGetter("formattedText")
   String getFormattedText();

   @ZenGetter("siblings")
   List<ITextComponent> getSiblings();

   @ZenMethod
   ITextComponent createCopy();

   Object getInternal();
}
