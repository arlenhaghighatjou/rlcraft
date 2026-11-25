package com.teamacronymcoders.contenttweaker.modules.vanilla.resources;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.zenscript.IBracketHandler;
import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;

@BracketHandler
public abstract class ResourceBracketHandler implements IBracketHandler {
   private final IJavaMethod method;
   private String resourceType;
   private int startIndex;

   public ResourceBracketHandler(String resourceType, Class clazz) {
      this(resourceType, clazz, 2);
   }

   public ResourceBracketHandler(String resourceType, Class clazz, int startIndex) {
      this.resourceType = resourceType;
      this.startIndex = startIndex;
      this.method = CraftTweakerAPI.getJavaMethod(clazz, "get" + resourceType, new Class[]{String.class});
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      IZenSymbol zenSymbol = null;
      if (tokens.size() > this.startIndex && ((Token)tokens.get(0)).getValue().equalsIgnoreCase(this.resourceType) && ((Token)tokens.get(1)).getValue().equals(":")) {
         zenSymbol = this.find(environment, tokens, this.startIndex, tokens.size());
      }

      return zenSymbol;
   }

   private IZenSymbol find(IEnvironmentGlobal environment, List<Token> tokens, int startIndex, int endIndex) {
      StringBuilder valueBuilder = new StringBuilder();

      for(int i = startIndex; i < endIndex; ++i) {
         Token token = (Token)tokens.get(i);
         valueBuilder.append(token.getValue());
      }

      return new ResourceReferenceSymbol(environment, valueBuilder.toString(), this);
   }

   public IJavaMethod getMethod() {
      return this.method;
   }
}
