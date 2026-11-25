package mezz.jei.config;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public final class KeyBindings {
   private static final String categoryName = "jei (Just Enough Items)";
   public static final KeyBinding toggleOverlay;
   public static final KeyBinding focusSearch;
   public static final KeyBinding toggleCheatMode;
   public static final KeyBinding toggleEditMode;
   public static final KeyBinding showRecipe;
   public static final KeyBinding showUses;
   public static final KeyBinding recipeBack;
   public static final KeyBinding previousPage;
   public static final KeyBinding nextPage;
   public static final KeyBinding bookmark;
   public static final KeyBinding toggleBookmarkOverlay;
   private static final List<KeyBinding> allBindings;

   private KeyBindings() {
   }

   public static void init() {
      for(KeyBinding binding : allBindings) {
         ClientRegistry.registerKeyBinding(binding);
      }

   }

   public static boolean isInventoryToggleKey(int keyCode) {
      return Minecraft.getMinecraft().gameSettings.keyBindInventory.isActiveAndMatches(keyCode);
   }

   public static boolean isInventoryCloseKey(int keyCode) {
      return keyCode == 1;
   }

   public static boolean isEnterKey(int keyCode) {
      return keyCode == 28;
   }

   static {
      allBindings = ImmutableList.of(toggleOverlay = new KeyBinding("key.jei.toggleOverlay", KeyConflictContext.GUI, KeyModifier.CONTROL, 24, "jei (Just Enough Items)"), focusSearch = new KeyBinding("key.jei.focusSearch", KeyConflictContext.GUI, KeyModifier.CONTROL, 33, "jei (Just Enough Items)"), toggleCheatMode = new KeyBinding("key.jei.toggleCheatMode", KeyConflictContext.GUI, 0, "jei (Just Enough Items)"), toggleEditMode = new KeyBinding("key.jei.toggleEditMode", KeyConflictContext.GUI, 0, "jei (Just Enough Items)"), showRecipe = new KeyBinding("key.jei.showRecipe", KeyConflictContext.GUI, 19, "jei (Just Enough Items)"), showUses = new KeyBinding("key.jei.showUses", KeyConflictContext.GUI, 22, "jei (Just Enough Items)"), recipeBack = new KeyBinding("key.jei.recipeBack", KeyConflictContext.GUI, 14, "jei (Just Enough Items)"), previousPage = new KeyBinding("key.jei.previousPage", KeyConflictContext.GUI, 201, "jei (Just Enough Items)"), nextPage = new KeyBinding("key.jei.nextPage", KeyConflictContext.GUI, 209, "jei (Just Enough Items)"), bookmark = new KeyBinding("key.jei.bookmark", KeyConflictContext.GUI, 30, "jei (Just Enough Items)"), toggleBookmarkOverlay = new KeyBinding("key.jei.toggleBookmarkOverlay", KeyConflictContext.GUI, 0, "jei (Just Enough Items)"));
   }
}
