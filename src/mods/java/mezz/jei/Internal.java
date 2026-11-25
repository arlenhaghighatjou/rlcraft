package mezz.jei;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import mezz.jei.color.ColorNamer;
import mezz.jei.gui.GuiEventHandler;
import mezz.jei.ingredients.IngredientFilter;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.input.InputHandler;
import mezz.jei.runtime.JeiHelpers;
import mezz.jei.runtime.JeiRuntime;
import mezz.jei.startup.StackHelper;
import net.minecraftforge.common.MinecraftForge;

public final class Internal {
   @Nullable
   private static StackHelper stackHelper;
   @Nullable
   private static JeiHelpers helpers;
   @Nullable
   private static JeiRuntime runtime;
   @Nullable
   private static IngredientRegistry ingredientRegistry;
   @Nullable
   private static ColorNamer colorNamer;
   @Nullable
   private static IngredientFilter ingredientFilter;
   @Nullable
   private static GuiEventHandler guiEventHandler;
   @Nullable
   private static InputHandler inputHandler;

   private Internal() {
   }

   public static StackHelper getStackHelper() {
      Preconditions.checkState(stackHelper != null, "StackHelper has not been created yet.");
      return stackHelper;
   }

   public static void setStackHelper(StackHelper stackHelper) {
      stackHelper = stackHelper;
   }

   public static JeiHelpers getHelpers() {
      Preconditions.checkState(helpers != null, "JeiHelpers has not been created yet.");
      return helpers;
   }

   public static void setHelpers(JeiHelpers helpers) {
      helpers = helpers;
   }

   @Nullable
   public static JeiRuntime getRuntime() {
      return runtime;
   }

   public static void setRuntime(JeiRuntime runtime) {
      JeiRuntime jeiRuntime = runtime;
      if (jeiRuntime != null) {
         jeiRuntime.close();
      }

      runtime = runtime;
   }

   public static IngredientRegistry getIngredientRegistry() {
      Preconditions.checkState(ingredientRegistry != null, "Ingredient Registry has not been created yet.");
      return ingredientRegistry;
   }

   public static void setIngredientRegistry(IngredientRegistry ingredientRegistry) {
      ingredientRegistry = ingredientRegistry;
   }

   public static ColorNamer getColorNamer() {
      Preconditions.checkState(colorNamer != null, "Color Namer has not been created yet.");
      return colorNamer;
   }

   public static void setColorNamer(ColorNamer colorNamer) {
      colorNamer = colorNamer;
   }

   public static IngredientFilter getIngredientFilter() {
      Preconditions.checkState(ingredientFilter != null, "Ingredient Filter has not been created yet.");
      return ingredientFilter;
   }

   public static void setIngredientFilter(IngredientFilter ingredientFilter) {
      if (ingredientFilter != null) {
         MinecraftForge.EVENT_BUS.unregister(ingredientFilter);
      }

      ingredientFilter = ingredientFilter;
      MinecraftForge.EVENT_BUS.register(ingredientFilter);
   }

   public static void setGuiEventHandler(GuiEventHandler guiEventHandler) {
      if (guiEventHandler != null) {
         MinecraftForge.EVENT_BUS.unregister(guiEventHandler);
      }

      guiEventHandler = guiEventHandler;
      MinecraftForge.EVENT_BUS.register(guiEventHandler);
   }

   public static void setInputHandler(InputHandler inputHandler) {
      if (inputHandler != null) {
         MinecraftForge.EVENT_BUS.unregister(inputHandler);
      }

      inputHandler = inputHandler;
      MinecraftForge.EVENT_BUS.register(inputHandler);
   }
}
