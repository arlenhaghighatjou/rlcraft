package com.teamacronymcoders.contenttweaker.modules.vanilla.resources;

import com.teamacronymcoders.contenttweaker.zen.symbol.StringIntSymbol;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.zenscript.IBracketHandler;
import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;

@BracketHandler(
   priority = 100
)
@ZenRegister
public class ItemBracketHandler implements IBracketHandler {
   private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(this.getClass(), "getItem", new Class[]{String.class, Integer.TYPE});

   public ItemBracketHandler() {
   }

   public static IItemStack getItem(String name, int meta) {
      return new GhostItemStack(name, meta);
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      IZenSymbol zenSymbol = null;
      if (tokens.size() > 2 && "item".equalsIgnoreCase(((Token)tokens.get(0)).getValue())) {
         int meta = 0;
         String blockName;
         if (tokens.size() >= 5) {
            blockName = ((Token)tokens.get(2)).getValue() + ":" + ((Token)tokens.get(4)).getValue();
            if (tokens.size() >= 7) {
               meta = Integer.parseInt(((Token)tokens.get(6)).getValue());
            }
         } else {
            blockName = ((Token)tokens.get(2)).getValue();
         }

         zenSymbol = new StringIntSymbol(environment, blockName, meta, this.method);
      }

      return zenSymbol;
   }
}
