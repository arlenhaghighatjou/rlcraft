package com.teamacronymcoders.contenttweaker.modules.vanilla.resources;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.util.ZenPosition;

public class ResourceReferenceSymbol implements IZenSymbol {
   private IEnvironmentGlobal environment;
   private String name;
   private ResourceBracketHandler resourceBracketHandler;

   public ResourceReferenceSymbol(IEnvironmentGlobal environment, String name, ResourceBracketHandler handler) {
      this.environment = environment;
      this.name = name;
      this.resourceBracketHandler = handler;
   }

   public IPartialExpression instance(ZenPosition position) {
      return new ExpressionCallStatic(position, this.environment, this.resourceBracketHandler.getMethod(), new Expression[]{new ExpressionString(position, this.name)});
   }
}
