package com.teamacronymcoders.contenttweaker.modules.materials.brackethandler;

import com.teamacronymcoders.base.materialsystem.MaterialSystem;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.zenscript.IBracketHandler;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.util.ZenPosition;

@BracketHandler
public class MaterialPartBracketHandler implements IBracketHandler {
   private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(MaterialPartBracketHandler.class, "getMaterialPart", new Class[]{String.class});

   public MaterialPartBracketHandler() {
   }

   public static MaterialPartDefinition getMaterialPart(String name) {
      MaterialPart materialPart = MaterialSystem.getMaterialPart(name.replace(":", "_"));
      MaterialPartDefinition zenMaterialPart = null;
      if (materialPart != null) {
         zenMaterialPart = new MaterialPartDefinition(materialPart);
      }

      return zenMaterialPart;
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      IZenSymbol zenSymbol = null;
      if (tokens.size() == 5 && "materialpart".equalsIgnoreCase(((Token)tokens.get(0)).getValue())) {
         String partName = (String)tokens.subList(2, 5).stream().map(Token::getValue).collect(Collectors.joining());
         zenSymbol = new MaterialPartBracketHandler.MaterialPartReferenceSymbol(environment, partName);
      }

      return zenSymbol;
   }

   private class MaterialPartReferenceSymbol implements IZenSymbol {
      private final IEnvironmentGlobal environment;
      private final String name;

      public MaterialPartReferenceSymbol(IEnvironmentGlobal environment, String name) {
         this.environment = environment;
         this.name = name;
      }

      public IPartialExpression instance(ZenPosition position) {
         return new ExpressionCallStatic(position, this.environment, MaterialPartBracketHandler.this.method, new Expression[]{new ExpressionString(position, this.name)});
      }
   }
}
