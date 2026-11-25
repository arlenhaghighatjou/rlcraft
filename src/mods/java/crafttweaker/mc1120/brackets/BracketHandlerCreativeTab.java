package crafttweaker.mc1120.brackets;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.creativetabs.ICreativeTab;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.zenscript.IBracketHandler;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;

@BracketHandler(
   priority = 100
)
@ZenRegister
public class BracketHandlerCreativeTab implements IBracketHandler {
   private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(BracketHandlerCreativeTab.class, "getTabFromString", String.class);

   public BracketHandlerCreativeTab() {
   }

   public static ICreativeTab getTabFromString(String name) {
      ICreativeTab tab = (ICreativeTab)CraftTweakerMC.creativeTabs.get(name);
      if (tab == null) {
         CraftTweakerAPI.logError("Couldn't find creative tab with name " + name);
      }

      return tab;
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      if (tokens.size() < 3) {
         return null;
      } else {
         return ((Token)tokens.get(0)).getValue().equalsIgnoreCase("creativetab") && ((Token)tokens.get(1)).getValue().equals(":") ? (position) -> {
            return new ExpressionCallStatic(position, environment, this.method, new Expression[]{new ExpressionString(position, (String)tokens.subList(2, tokens.size()).stream().map(Token::getValue).collect(Collectors.joining()))});
         } : null;
      }
   }

   public String getRegexMatchingString() {
      return "creativetab:.*";
   }

   public Class<?> getReturnedClass() {
      return ICreativeTab.class;
   }
}
