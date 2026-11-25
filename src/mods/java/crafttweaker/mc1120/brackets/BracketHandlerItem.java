package crafttweaker.mc1120.brackets;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IngredientAny;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.item.MCItemUtils;
import crafttweaker.zenscript.IBracketHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionInt;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;

@BracketHandler(
   priority = 100
)
@ZenRegister
public class BracketHandlerItem implements IBracketHandler {
   private static final Map<String, Item> itemNames = new HashMap();
   private static final Map<String, Block> blockNames = new HashMap();
   private final IZenSymbol symbolAny = CraftTweakerAPI.getJavaStaticFieldSymbol(IngredientAny.class, "INSTANCE");
   private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(BracketHandlerItem.class, "getItem", String.class, Integer.TYPE);

   public BracketHandlerItem() {
   }

   public static Map<String, Item> getItemNames() {
      return itemNames;
   }

   public static Map<String, Block> getBlockNames() {
      return blockNames;
   }

   public static void rebuildItemRegistry() {
      itemNames.clear();
      blockNames.clear();

      for(ResourceLocation itemName : Item.REGISTRY.getKeys()) {
         String domain = itemName.toString().replace(" ", "").replace("'", "");
         itemNames.put(domain, Item.REGISTRY.getObject(itemName));
      }

      for(ResourceLocation blockName : Block.REGISTRY.getKeys()) {
         String domain = blockName.toString().replace(" ", "").replace("'", "");
         blockNames.put(domain, Block.REGISTRY.getObject(blockName));
      }

      MCItemUtils.createItemList();
   }

   public static IItemStack getItem(String name, int meta) {
      Item item = (Item)itemNames.get(name);
      if (item != null) {
         return CraftTweakerMC.getIItemStackWildcardSize(item, meta);
      } else {
         return blockNames.containsKey(name) ? CraftTweakerMC.getIItemStackWildcardSize(new ItemStack((Block)blockNames.get(name), 1, meta)) : null;
      }
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      if (tokens.size() == 1 && ((Token)tokens.get(0)).getValue().equals("*")) {
         return this.symbolAny;
      } else {
         int fromIndex = 0;
         int toIndex = tokens.size();
         int meta = 0;
         if (tokens.size() > 2) {
            if (((Token)tokens.get(0)).getValue().equals("item") && ((Token)tokens.get(1)).getValue().equals(":")) {
               fromIndex = 2;
            }

            if (((Token)tokens.get(tokens.size() - 1)).getType() == 2 && ((Token)tokens.get(tokens.size() - 2)).getValue().equals(":")) {
               toIndex = tokens.size() - 2;
               meta = Integer.parseInt(((Token)tokens.get(tokens.size() - 1)).getValue());
            } else if (((Token)tokens.get(tokens.size() - 1)).getValue().equals("*") && ((Token)tokens.get(tokens.size() - 2)).getValue().equals(":")) {
               toIndex = tokens.size() - 2;
               meta = 32767;
            }
         }

         return this.find(environment, tokens, fromIndex, toIndex, meta);
      }
   }

   private IZenSymbol find(IEnvironmentGlobal environment, List<Token> tokens, int startIndex, int endIndex, int meta) {
      StringBuilder valueBuilder = new StringBuilder();

      for(int i = startIndex; i < endIndex; ++i) {
         Token token = (Token)tokens.get(i);
         valueBuilder.append(token.getValue());
      }

      String itemName = valueBuilder.toString();
      return !itemNames.containsKey(itemName) && !blockNames.containsKey(itemName) ? null : (position) -> {
         return new ExpressionCallStatic(position, environment, this.method, new Expression[]{new ExpressionString(position, valueBuilder.toString()), new ExpressionInt(position, (long)meta, ZenType.INT)});
      };
   }

   public Class<?> getReturnedClass() {
      return IItemStack.class;
   }
}
