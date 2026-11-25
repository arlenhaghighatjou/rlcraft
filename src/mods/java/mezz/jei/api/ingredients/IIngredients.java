package mezz.jei.api.ingredients;

import java.util.List;
import mezz.jei.api.recipe.IIngredientType;

public interface IIngredients {
   <T> void setInput(IIngredientType<T> var1, T var2);

   <T> void setInputs(IIngredientType<T> var1, List<T> var2);

   <T> void setInputLists(IIngredientType<T> var1, List<List<T>> var2);

   <T> void setOutput(IIngredientType<T> var1, T var2);

   <T> void setOutputs(IIngredientType<T> var1, List<T> var2);

   <T> void setOutputLists(IIngredientType<T> var1, List<List<T>> var2);

   <T> List<List<T>> getInputs(IIngredientType<T> var1);

   <T> List<List<T>> getOutputs(IIngredientType<T> var1);

   /** @deprecated */
   @Deprecated
   <T> void setInput(Class<? extends T> var1, T var2);

   /** @deprecated */
   @Deprecated
   <T> void setInputs(Class<? extends T> var1, List<T> var2);

   /** @deprecated */
   @Deprecated
   <T> void setInputLists(Class<? extends T> var1, List<List<T>> var2);

   /** @deprecated */
   @Deprecated
   <T> void setOutput(Class<? extends T> var1, T var2);

   /** @deprecated */
   @Deprecated
   <T> void setOutputs(Class<? extends T> var1, List<T> var2);

   /** @deprecated */
   @Deprecated
   <T> void setOutputLists(Class<? extends T> var1, List<List<T>> var2);

   /** @deprecated */
   @Deprecated
   <T> List<List<T>> getInputs(Class<? extends T> var1);

   /** @deprecated */
   @Deprecated
   <T> List<List<T>> getOutputs(Class<? extends T> var1);
}
