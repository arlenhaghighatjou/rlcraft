package com.blamejared.compat.tconstruct.brackets;

import com.blamejared.compat.tconstruct.materials.ITICMaterial;
import com.blamejared.compat.tconstruct.materials.TICMaterial;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IngredientAny;
import crafttweaker.zenscript.IBracketHandler;
import java.util.List;
import slimeknights.tconstruct.library.TinkerRegistry;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.util.ZenPosition;

@BracketHandler(
   priority = 100
)
@ZenRegister
public class MaterialBracketHandler implements IBracketHandler {
   private final IZenSymbol symbolAny = CraftTweakerAPI.getJavaStaticFieldSymbol(IngredientAny.class, "INSTANCE");
   private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(MaterialBracketHandler.class, "getMaterial", new Class[]{String.class});

   public static ITICMaterial getMaterial(String name) {
      return new TICMaterial(TinkerRegistry.getMaterial(name));
   }

   public MaterialBracketHandler() {
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      if (tokens.size() == 1 && ((Token)tokens.get(0)).getValue().equals("*")) {
         return this.symbolAny;
      } else {
         return tokens.size() > 2 && ((Token)tokens.get(0)).getValue().equals("ticmat") && ((Token)tokens.get(1)).getValue().equals(":") ? this.find(environment, tokens, 2, tokens.size()) : null;
      }
   }

   private IZenSymbol find(IEnvironmentGlobal environment, List<Token> tokens, int startIndex, int endIndex) {
      StringBuilder valueBuilder = new StringBuilder();

      for(int i = startIndex; i < endIndex; ++i) {
         Token token = (Token)tokens.get(i);
         valueBuilder.append(token.getValue());
      }

      ITICMaterial material = getMaterial(valueBuilder.toString());
      if (!material.getName().equals("unknown")) {
         CraftTweakerAPI.logInfo("Material wasn't null");
         return new MaterialBracketHandler.MaterialReferenceSymbol(environment, valueBuilder.toString());
      } else {
         CraftTweakerAPI.logInfo("Material was null");
         return null;
      }
   }

   private class MaterialReferenceSymbol implements IZenSymbol {
      private final IEnvironmentGlobal environment;
      private final String name;

      public MaterialReferenceSymbol(IEnvironmentGlobal environment, String name) {
         this.environment = environment;
         this.name = name;
      }

      public IPartialExpression instance(ZenPosition position) {
         return new ExpressionCallStatic(position, this.environment, MaterialBracketHandler.this.method, new Expression[]{new ExpressionString(position, this.name)});
      }
   }
}
