package crafttweaker.api.item;

import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.player.IPlayer;
import crafttweaker.util.ArrayUtil;
import java.util.List;

public class IngredientAny implements IIngredient {
   public static final IngredientAny INSTANCE = new IngredientAny();
   public static Object INTERNAL_ANY = null;

   private IngredientAny() {
   }

   public String getMark() {
      return null;
   }

   public int getAmount() {
      return -1;
   }

   public List<IItemStack> getItems() {
      return null;
   }

   public IItemStack[] getItemArray() {
      return new IItemStack[0];
   }

   public List<ILiquidStack> getLiquids() {
      return null;
   }

   public IIngredient amount(int amount) {
      return new IngredientStack(this, amount);
   }

   public IIngredient transformNew(IItemTransformerNew transformer) {
      return new IngredientAnyAdvanced((String)null, ArrayUtil.EMPTY_CONDITIONS, new IItemTransformerNew[]{transformer}, ArrayUtil.EMPTY_TRANSFORMERS_NEW);
   }

   public IIngredient only(IItemCondition condition) {
      return new IngredientAnyAdvanced((String)null, new IItemCondition[]{condition}, ArrayUtil.EMPTY_TRANSFORMERS, ArrayUtil.EMPTY_TRANSFORMERS_NEW);
   }

   public IIngredient marked(String mark) {
      return new IngredientAnyAdvanced(mark, ArrayUtil.EMPTY_CONDITIONS, ArrayUtil.EMPTY_TRANSFORMERS, ArrayUtil.EMPTY_TRANSFORMERS_NEW);
   }

   public IIngredient or(IIngredient ingredient) {
      return this;
   }

   public boolean matches(IItemStack item) {
      return true;
   }

   public boolean matchesExact(IItemStack item) {
      return true;
   }

   public boolean matches(ILiquidStack liquid) {
      return true;
   }

   public boolean contains(IIngredient ingredient) {
      return true;
   }

   public IItemStack applyTransform(IItemStack item, IPlayer byPlayer) {
      return item;
   }

   public IItemStack applyNewTransform(IItemStack item) {
      return item;
   }

   public boolean hasNewTransformers() {
      return false;
   }

   public boolean hasTransformers() {
      return false;
   }

   public IIngredient transform(IItemTransformer transformer) {
      return new IngredientAnyAdvanced((String)null, ArrayUtil.EMPTY_CONDITIONS, ArrayUtil.EMPTY_TRANSFORMERS, new IItemTransformer[]{transformer});
   }

   public Object getInternal() {
      return INTERNAL_ANY;
   }

   public String toString() {
      return "<*>";
   }

   public String toCommandString() {
      return this.toString();
   }
}
