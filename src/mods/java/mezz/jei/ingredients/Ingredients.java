package mezz.jei.ingredients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import mezz.jei.Internal;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IIngredientType;

public class Ingredients implements IIngredients {
   private final Map<IIngredientType, List<List>> inputs = new IdentityHashMap();
   private final Map<IIngredientType, List<List>> outputs = new IdentityHashMap();

   public Ingredients() {
   }

   public <T> void setInput(IIngredientType<T> ingredientType, T input) {
      this.setInputs(ingredientType, Collections.singletonList(input));
   }

   /** @deprecated */
   @Deprecated
   public <T> void setInput(Class<? extends T> ingredientClass, T input) {
      IngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientType<T> ingredientType = ingredientRegistry.getIngredientType(ingredientClass);
      this.setInput(ingredientType, input);
   }

   public <T> void setInputLists(IIngredientType<T> ingredientType, List<List<T>> inputs) {
      IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientHelper<T> ingredientHelper = ingredientRegistry.getIngredientHelper(ingredientType);
      List<List> expandedInputs = new ArrayList();

      for(List<T> input : inputs) {
         List<T> itemStacks = ingredientHelper.expandSubtypes(input);
         expandedInputs.add(itemStacks);
      }

      this.inputs.put(ingredientType, expandedInputs);
   }

   /** @deprecated */
   @Deprecated
   public <T> void setInputLists(Class<? extends T> ingredientClass, List<List<T>> inputs) {
      IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientType<T> ingredientType = ingredientRegistry.getIngredientType(ingredientClass);
      this.setInputLists(ingredientType, inputs);
   }

   public <T> void setInputs(IIngredientType<T> ingredientType, List<T> inputs) {
      IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientHelper<T> ingredientHelper = ingredientRegistry.getIngredientHelper(ingredientType);
      List<List> expandedInputs = new ArrayList();

      for(T input : inputs) {
         List<T> expandedInput = ingredientHelper.expandSubtypes(Collections.singletonList(input));
         expandedInputs.add(expandedInput);
      }

      this.inputs.put(ingredientType, expandedInputs);
   }

   /** @deprecated */
   @Deprecated
   public <T> void setInputs(Class<? extends T> ingredientClass, List<T> input) {
      IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientType<T> ingredientType = ingredientRegistry.getIngredientType(ingredientClass);
      this.setInputs(ingredientType, input);
   }

   public <T> void setOutput(IIngredientType<T> ingredientType, T output) {
      this.setOutputs(ingredientType, Collections.singletonList(output));
   }

   /** @deprecated */
   @Deprecated
   public <T> void setOutput(Class<? extends T> ingredientClass, T output) {
      IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientType<T> ingredientType = ingredientRegistry.getIngredientType(ingredientClass);
      this.setOutput(ingredientType, output);
   }

   public <T> void setOutputs(IIngredientType<T> ingredientType, List<T> outputs) {
      IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientHelper<T> ingredientHelper = ingredientRegistry.getIngredientHelper(ingredientType);
      List<List> expandedOutputs = new ArrayList();

      for(T output : outputs) {
         List<T> expandedOutput = ingredientHelper.expandSubtypes(Collections.singletonList(output));
         expandedOutputs.add(expandedOutput);
      }

      this.outputs.put(ingredientType, expandedOutputs);
   }

   /** @deprecated */
   @Deprecated
   public <T> void setOutputs(Class<? extends T> ingredientClass, List<T> outputs) {
      IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientType<T> ingredientType = ingredientRegistry.getIngredientType(ingredientClass);
      this.setOutputs(ingredientType, outputs);
   }

   public <T> void setOutputLists(IIngredientType<T> ingredientType, List<List<T>> outputs) {
      IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientHelper<T> ingredientHelper = ingredientRegistry.getIngredientHelper(ingredientType);
      List<List> expandedOutputs = new ArrayList();

      for(List<T> output : outputs) {
         List<T> itemStacks = ingredientHelper.expandSubtypes(output);
         expandedOutputs.add(itemStacks);
      }

      this.outputs.put(ingredientType, expandedOutputs);
   }

   /** @deprecated */
   @Deprecated
   public <T> void setOutputLists(Class<? extends T> ingredientClass, List<List<T>> outputs) {
      IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientType<T> ingredientType = ingredientRegistry.getIngredientType(ingredientClass);
      this.setOutputLists(ingredientType, outputs);
   }

   public <T> List<List<T>> getInputs(IIngredientType<T> ingredientType) {
      List<List<T>> inputs = (List)this.inputs.get(ingredientType);
      return inputs == null ? Collections.emptyList() : inputs;
   }

   /** @deprecated */
   @Deprecated
   public <T> List<List<T>> getInputs(Class<? extends T> ingredientClass) {
      IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientType<T> ingredientType = ingredientRegistry.getIngredientType(ingredientClass);
      return this.getInputs(ingredientType);
   }

   public <T> List<List<T>> getOutputs(IIngredientType<T> ingredientType) {
      List<List<T>> outputs = (List)this.outputs.get(ingredientType);
      return outputs == null ? Collections.emptyList() : outputs;
   }

   /** @deprecated */
   @Deprecated
   public <T> List<List<T>> getOutputs(Class<? extends T> ingredientClass) {
      IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientType<T> ingredientType = ingredientRegistry.getIngredientType(ingredientClass);
      return this.getOutputs(ingredientType);
   }

   public Map<IIngredientType, List> getInputIngredients() {
      Map<IIngredientType, List> inputIngredients = new IdentityHashMap();

      for(Entry<IIngredientType, List<List>> entry : this.inputs.entrySet()) {
         List<Object> flatIngredients = new ArrayList();

         for(List ingredients : (List)entry.getValue()) {
            flatIngredients.addAll(ingredients);
         }

         inputIngredients.put(entry.getKey(), flatIngredients);
      }

      return inputIngredients;
   }

   public Map<IIngredientType, List> getOutputIngredients() {
      Map<IIngredientType, List> outputIngredients = new IdentityHashMap();

      for(Entry<IIngredientType, List<List>> entry : this.outputs.entrySet()) {
         List<Object> flatIngredients = new ArrayList();

         for(List ingredients : (List)entry.getValue()) {
            flatIngredients.addAll(ingredients);
         }

         outputIngredients.put(entry.getKey(), flatIngredients);
      }

      return outputIngredients;
   }
}
