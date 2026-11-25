package crafttweaker.api.formatting;

import stanhebben.zenscript.annotations.ZenMethod;

public interface IFormatter {
   IFormattedText string(String var1);

   @ZenMethod
   IFormattedText black(IFormattedText var1);

   @ZenMethod
   IFormattedText darkBlue(IFormattedText var1);

   @ZenMethod
   IFormattedText darkGreen(IFormattedText var1);

   @ZenMethod
   IFormattedText darkAqua(IFormattedText var1);

   @ZenMethod
   IFormattedText darkRed(IFormattedText var1);

   @ZenMethod
   IFormattedText darkPurple(IFormattedText var1);

   @ZenMethod
   IFormattedText gold(IFormattedText var1);

   @ZenMethod
   IFormattedText gray(IFormattedText var1);

   @ZenMethod
   IFormattedText darkGray(IFormattedText var1);

   @ZenMethod
   IFormattedText blue(IFormattedText var1);

   @ZenMethod
   IFormattedText green(IFormattedText var1);

   @ZenMethod
   IFormattedText aqua(IFormattedText var1);

   @ZenMethod
   IFormattedText red(IFormattedText var1);

   @ZenMethod
   IFormattedText lightPurple(IFormattedText var1);

   @ZenMethod
   IFormattedText yellow(IFormattedText var1);

   @ZenMethod
   IFormattedText white(IFormattedText var1);

   @ZenMethod
   IFormattedText obfuscated(IFormattedText var1);

   @ZenMethod
   IFormattedText bold(IFormattedText var1);

   @ZenMethod
   IFormattedText strikethrough(IFormattedText var1);

   @ZenMethod
   IFormattedText underline(IFormattedText var1);

   @ZenMethod
   IFormattedText italic(IFormattedText var1);
}
