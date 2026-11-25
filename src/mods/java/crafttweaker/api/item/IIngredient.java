package crafttweaker.api.item;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.player.IPlayer;
import java.util.List;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenClass("crafttweaker.item.IIngredient")
@ZenRegister
public interface IIngredient {
   @ZenGetter("mark")
   String getMark();

   @ZenGetter("amount")
   int getAmount();

   @ZenGetter("items")
   List<IItemStack> getItems();

   @ZenGetter("itemArray")
   IItemStack[] getItemArray();

   @ZenGetter("liquids")
   List<ILiquidStack> getLiquids();

   @ZenOperator(OperatorType.MUL)
   @ZenMethod
   IIngredient amount(int var1);

   @ZenOperator(OperatorType.OR)
   @ZenMethod
   IIngredient or(IIngredient var1);

   @ZenMethod
   IIngredient transformNew(IItemTransformerNew var1);

   @ZenMethod
   IIngredient only(IItemCondition var1);

   @ZenMethod
   IIngredient marked(String var1);

   @ZenMethod
   boolean matches(IItemStack var1);

   @ZenMethod
   boolean matchesExact(IItemStack var1);

   @ZenMethod
   boolean matches(ILiquidStack var1);

   @ZenOperator(OperatorType.CONTAINS)
   boolean contains(IIngredient var1);

   @ZenMethod
   IItemStack applyTransform(IItemStack var1, IPlayer var2);

   @ZenMethod
   IItemStack applyNewTransform(IItemStack var1);

   @ZenGetter("hasNewTransformers")
   boolean hasNewTransformers();

   @ZenGetter("hasTransformations")
   boolean hasTransformers();

   @ZenMethod
   IIngredient transform(IItemTransformer var1);

   Object getInternal();

   @ZenGetter("commandString")
   String toCommandString();
}
