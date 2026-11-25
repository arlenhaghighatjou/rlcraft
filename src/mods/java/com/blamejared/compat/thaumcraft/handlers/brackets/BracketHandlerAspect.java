package com.blamejared.compat.thaumcraft.handlers.brackets;

import com.blamejared.compat.thaumcraft.handlers.aspects.CTAspect;
import com.blamejared.compat.thaumcraft.handlers.aspects.CTAspectStack;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.zenscript.IBracketHandler;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import thaumcraft.api.aspects.Aspect;

@BracketHandler(
   priority = 100
)
@ZenRegister
@ModOnly("thaumcraft")
public class BracketHandlerAspect implements IBracketHandler {
   private static final Map<String, Aspect> aspects = new HashMap();
   private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(BracketHandlerAspect.class, "getAspect", new Class[]{String.class});

   public BracketHandlerAspect() {
   }

   public static void rebuildRegistry() {
      aspects.clear();
      LinkedHashMap var10000 = Aspect.aspects;
      Map var10001 = aspects;
      aspects.getClass();
      var10000.forEach(var10001::put);
   }

   public static CTAspectStack getAspect(String name) {
      return new CTAspectStack(new CTAspect((Aspect)Aspect.aspects.get(name)), 1);
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      int fromIndex = 0;
      int toIndex = tokens.size();
      if (tokens.size() > 2 && ((Token)tokens.get(0)).getValue().equals("aspect") && ((Token)tokens.get(1)).getValue().equals(":")) {
         fromIndex = 2;
      }

      return this.find(environment, tokens, fromIndex, toIndex);
   }

   private IZenSymbol find(IEnvironmentGlobal environment, List<Token> tokens, int startIndex, int endIndex) {
      StringBuilder valueBuilder = new StringBuilder();

      for(int i = startIndex; i < endIndex; ++i) {
         Token token = (Token)tokens.get(i);
         valueBuilder.append(token.getValue());
      }

      String name = valueBuilder.toString();
      if (!aspects.containsKey(name)) {
         rebuildRegistry();
      }

      return aspects.containsKey(name) ? (position) -> {
         return new ExpressionCallStatic(position, environment, this.method, new Expression[]{new ExpressionString(position, valueBuilder.toString())});
      } : null;
   }
}
