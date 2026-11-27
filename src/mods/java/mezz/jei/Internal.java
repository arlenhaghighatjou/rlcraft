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
      Internal.stackHelper = stackHelper;
   }

   public static JeiHelpers getHelpers() {
      Preconditions.checkState(helpers != null, "JeiHelpers has not been created yet.");
      return helpers;
   }

   public static void setHelpers(JeiHelpers helpers) {
      Internal.helpers = helpers;
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

      Internal.runtime = runtime;
   }

   public static IngredientRegistry getIngredientRegistry() {
      Preconditions.checkState(ingredientRegistry != null, "Ingredient Registry has not been created yet.");
      return ingredientRegistry;
   }

   public static void setIngredientRegistry(IngredientRegistry ingredientRegistry) {
      Internal.ingredientRegistry = ingredientRegistry;
   }

   public static ColorNamer getColorNamer() {
      Preconditions.checkState(colorNamer != null, "Color Namer has not been created yet.");
      return colorNamer;
   }

   public static void setColorNamer(ColorNamer colorNamer) {
      Internal.colorNamer = colorNamer;
   }

   public static IngredientFilter getIngredientFilter() {
      Preconditions.checkState(ingredientFilter != null, "Ingredient Filter has not been created yet.");
      return ingredientFilter;
   }

   public static void setIngredientFilter(IngredientFilter ingredientFilter) {
      if (ingredientFilter != null) {
         MinecraftForge.EVENT_BUS.unregister(ingredientFilter);
      }

      Internal.ingredientFilter = ingredientFilter;
      ingredientFilter.register(MinecraftForge.EVENT_BUS);
   }

   public static void setGuiEventHandler(GuiEventHandler guiEventHandler) {
      if (guiEventHandler != null) {
         MinecraftForge.EVENT_BUS.unregister(guiEventHandler);
      }

      Internal.guiEventHandler = guiEventHandler;
      guiEventHandler.register(MinecraftForge.EVENT_BUS);
   }

   public static void setInputHandler(InputHandler inputHandler) {
      if (inputHandler != null) {
         MinecraftForge.EVENT_BUS.unregister(inputHandler);
      }

      Internal.inputHandler = inputHandler;
      inputHandler.register(MinecraftForge.EVENT_BUS);
   }
}
