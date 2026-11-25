package crafttweaker.mc1120.brackets;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.mc1120.oredict.MCOreDictEntry;
import crafttweaker.zenscript.GlobalRegistry;
import crafttweaker.zenscript.IBracketHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;

@BracketHandler(
   priority = 100
)
@ZenRegister
public class BracketHandlerOre implements IBracketHandler {
   public BracketHandlerOre() {
   }

   public static IOreDictEntry getOre(String name) {
      return new MCOreDictEntry(name);
   }

   public static List<IOreDictEntry> getOreList(String wildcardName) {
      List<IOreDictEntry> result = new ArrayList();
      Pattern wildcardPattern = Pattern.compile(wildcardName.replaceAll("\\*", ".+"));

      for(IOreDictEntry someOreDict : CraftTweakerAPI.oreDict.getEntries()) {
         String oreDictName = someOreDict.getName();
         if (wildcardPattern.matcher(oreDictName).matches()) {
            result.add(getOre(oreDictName));
         }
      }

      return result;
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      return tokens.size() > 2 && ((Token)tokens.get(0)).getValue().equals("ore") && ((Token)tokens.get(1)).getValue().equals(":") ? this.find(environment, tokens, 2, tokens.size()) : null;
   }

   private IZenSymbol find(IEnvironmentGlobal environment, List<Token> tokens, int startIndex, int endIndex) {
      StringBuilder valueBuilder = new StringBuilder();

      for(int i = startIndex; i < endIndex; ++i) {
         Token token = (Token)tokens.get(i);
         valueBuilder.append(token.getValue());
      }

      IJavaMethod method = JavaMethod.get(GlobalRegistry.getTypes(), BracketHandlerOre.class, valueBuilder.toString().contains("*") ? "getOreList" : "getOre", String.class);
      return (position) -> {
         return new ExpressionCallStatic(position, environment, method, new Expression[]{new ExpressionString(position, valueBuilder.toString())});
      };
   }

   public Class<?> getReturnedClass() {
      return IOreDictEntry.class;
   }

   public String getRegexMatchingString() {
      return "ore:.*";
   }
}
