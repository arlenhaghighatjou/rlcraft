package com.teamacronymcoders.base.recipesystem.loader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.json.factory.IObjectFactory;
import com.teamacronymcoders.base.recipesystem.Recipe;
import com.teamacronymcoders.base.recipesystem.RecipeSystem;
import com.teamacronymcoders.base.recipesystem.condition.ICondition;
import com.teamacronymcoders.base.recipesystem.event.RegisterRecipeFactoriesEvent;
import com.teamacronymcoders.base.recipesystem.input.IInput;
import com.teamacronymcoders.base.recipesystem.output.IOutput;
import com.teamacronymcoders.base.recipesystem.type.RecipeType;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import org.apache.commons.io.FilenameUtils;

public abstract class JsonRecipeLoader implements ILoader {
   private static boolean loadedFactories = false;
   private static final Map<String, IObjectFactory<? extends IInput>> inputFactories = Maps.newHashMap();
   private static final Map<String, IObjectFactory<? extends IOutput>> outputFactories = Maps.newHashMap();
   private static final Map<String, IObjectFactory<? extends ICondition>> conditionFactories = Maps.newHashMap();
   private final Gson GSON = new Gson();

   protected JsonRecipeLoader() {
      this.loadFactories();
   }

   private void loadFactories() {
      if (!loadedFactories) {
         RegisterRecipeFactoriesEvent<IInput> inputEvent = new RegisterRecipeFactoriesEvent<IInput>(IInput.class);
         MinecraftForge.EVENT_BUS.post(inputEvent);
         inputFactories.putAll(inputEvent.getFactories());
         RegisterRecipeFactoriesEvent<IOutput> outputEvent = new RegisterRecipeFactoriesEvent<IOutput>(IOutput.class);
         MinecraftForge.EVENT_BUS.post(outputEvent);
         outputFactories.putAll(outputEvent.getFactories());
         RegisterRecipeFactoriesEvent<ICondition> conditionEvent = new RegisterRecipeFactoriesEvent<ICondition>(ICondition.class);
         MinecraftForge.EVENT_BUS.post(conditionEvent);
         conditionFactories.putAll(conditionEvent.getFactories());
         loadedFactories = true;
      }

   }

   public abstract List<Recipe> loadRecipes();

   protected Recipe loadRecipe(JsonContext ctx, Path root, Path file) {
      String relative = root.relativize(file).toString();
      if ("json".equals(FilenameUtils.getExtension(file.toString())) && !relative.startsWith("_")) {
         String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
         ResourceLocation key = new ResourceLocation(ctx.getModId(), name);

         try {
            BufferedReader bufferedReader = Files.newBufferedReader(file);
            Throwable var8 = null;

            Recipe var20;
            try {
               JsonObject jsonObject = (JsonObject)JsonUtils.fromJson(this.GSON, bufferedReader, JsonObject.class);
               if (jsonObject == null || !jsonObject.has("type")) {
                  return null;
               }

               String typeName = jsonObject.get("type").getAsString();
               RecipeType recipeType = RecipeSystem.getRecipeType(typeName);
               if (recipeType == null) {
                  throw new JsonParseException("No Recipe Type found for: " + typeName);
               }

               boolean loadRecipe = true;
               if (jsonObject.has("load_conditions")) {
                  loadRecipe = CraftingHelper.processConditions(JsonUtils.getJsonArray(jsonObject, "load_conditions"), ctx);
               }

               if (!loadRecipe) {
                  return null;
               }

               int priority = JsonUtils.getInt(jsonObject, "priority", 0);
               JsonArray inputsJson = JsonUtils.getJsonArray(jsonObject, "inputs");
               JsonArray outputsJson = JsonUtils.getJsonArray(jsonObject, "outputs");
               JsonArray conditionsJson = JsonUtils.getJsonArray(jsonObject, "conditions", new JsonArray());
               Map var10002 = inputFactories;
               inputFactories.getClass();
               List<IInput> inputList = processJsonArray(inputsJson, ctx, var10002::get, "Input");
               List<IOutput> outputList = processOutputs(outputsJson, ctx);
               var10002 = conditionFactories;
               conditionFactories.getClass();
               List<ICondition> conditionList = processJsonArray(conditionsJson, ctx, var10002::get, "Conditions");
               var20 = new Recipe(key, priority, this.getRecipeSource(), recipeType, inputList, outputList, conditionList);
            } catch (Throwable var31) {
               var8 = var31;
               throw var31;
            } finally {
               if (bufferedReader != null) {
                  if (var8 != null) {
                     try {
                        bufferedReader.close();
                     } catch (Throwable var30) {
                        var8.addSuppressed(var30);
                     }
                  } else {
                     bufferedReader.close();
                  }
               }

            }

            return var20;
         } catch (JsonParseException | IOException var33) {
            Base.instance.getLogger().getLogger().warn("Error in recipe: " + key.toString(), var33);
            return null;
         }
      } else {
         return null;
      }
   }

   public static List<IOutput> processOutputs(JsonArray jsonElements, JsonContext context) {
      Map var10002 = outputFactories;
      outputFactories.getClass();
      return processJsonArray(jsonElements, context, var10002::get, "Output");
   }

   private static <T> List<T> processJsonArray(JsonArray jsonElements, JsonContext context, Function<String, IObjectFactory<? extends T>> factoryFunction, String name) {
      List<T> processedList = Lists.newArrayList();

      for(JsonElement element : jsonElements) {
         if (!element.isJsonObject()) {
            throw new JsonParseException(name + "s must be JSON Objects");
         }

         JsonObject jsonObject = element.getAsJsonObject();
         String typeName = JsonUtils.getString(jsonObject, "type");
         IObjectFactory<? extends T> factory = (IObjectFactory)factoryFunction.apply(typeName);
         jsonObject.remove("type");
         if (factory == null) {
            throw new JsonParseException("No " + name + " found for type: " + typeName);
         }

         processedList.add(factory.parse(context, jsonObject));
      }

      return processedList;
   }
}
