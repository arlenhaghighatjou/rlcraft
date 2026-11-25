package crafttweaker.mods.jei;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CTChatCommand;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import crafttweaker.mods.jei.commands.ConflictCommand;
import crafttweaker.mods.jei.recipeWrappers.BrewingRecipeCWrapper;
import crafttweaker.mods.jei.recipeWrappers.CraftingRecipeWrapperShaped;
import crafttweaker.mods.jei.recipeWrappers.CraftingRecipeWrapperShapeless;
import java.util.function.Consumer;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(
   modid = "crafttweakerjei",
   name = "CraftTweaker JEI Support",
   version = "2.0.3",
   dependencies = "after:jei@[4.12.0,);",
   acceptedMinecraftVersions = "[1.12, 1.13)"
)
public class JEIMod {
   public JEIMod() {
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      if (Loader.isModLoaded("jei")) {
         CTChatCommand.registerCommand(new ConflictCommand());
         CTChatCommand.registerCommand(new CraftTweakerCommand("jeiCategories") {
            protected void init() {
               this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("\u00a72/ct jeiCategories", "/ct jeiCategories", true), SpecialMessagesChat.getNormalMessage(" \u00a73Lists all JEI recipe categories in the game")});
            }

            public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
               CraftTweakerAPI.logCommand("--- JEI Categories ---");

               for(IRecipeCategory<?> category : JEIAddonPlugin.recipeRegistry.getRecipeCategories()) {
                  CraftTweakerAPI.logCommand(category.getUid());
               }

               sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List of categories generated;", sender));
            }
         });
      } else {
         CTChatCommand.registerCommand(new CraftTweakerCommand("conflict") {
            protected void init() {
               this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("\u00a72/ct conflict", "/ct conflict", true), SpecialMessagesChat.getNormalMessage(" \u00a73Lists all conflicting crafting recipes in the game"), SpecialMessagesChat.getNormalMessage(" \u00a73Might take a bit of time depending on the size of the pack"), SpecialMessagesChat.getNormalMessage(" \u00a73This needs to be run on a client and with JEI installed")});
            }

            public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
               sender.sendMessage(SpecialMessagesChat.getNormalMessage("\u00a74This Command needs to be run with JEI installed"));
            }
         });
      }

   }

   public static void applyActions() {
      if (Loader.isModLoaded("jei")) {
         try {
            JEI.LATE_ACTIONS_PRE.forEach(CraftTweakerAPI::apply);
            JEI.LATE_ACTIONS_POST.forEach(CraftTweakerAPI::apply);
         } catch (Exception var1) {
            var1.printStackTrace();
         }

         if (JEIAddonPlugin.itemRegistry != null) {
            if (!JEI.HIDDEN_ITEMS.isEmpty()) {
               JEIAddonPlugin.itemRegistry.removeIngredientsAtRuntime(VanillaTypes.ITEM, JEI.HIDDEN_ITEMS);
            }

            if (!JEI.HIDDEN_LIQUIDS.isEmpty()) {
               JEIAddonPlugin.itemRegistry.removeIngredientsAtRuntime(VanillaTypes.FLUID, JEI.HIDDEN_LIQUIDS);
            }
         }

         if (JEIAddonPlugin.recipeRegistry != null && !JEI.HIDDEN_CATEGORIES.isEmpty()) {
            JEI.HIDDEN_CATEGORIES.forEach((str) -> {
               try {
                  JEIAddonPlugin.recipeRegistry.hideRecipeCategory(str);
               } catch (RuntimeException var2) {
                  CraftTweakerAPI.logError("Failed to hide recipe category: " + str, var2);
               }

            });
         }
      }

   }

   public static void onRegistered() {
      try {
         JEI.DESCRIPTIONS.forEach(CraftTweakerAPI::apply);
      } catch (Exception var1) {
         var1.printStackTrace();
      }

      BrewingRecipeCWrapper.registerBrewingRecipe();
      CraftingRecipeWrapperShapeless.registerCraftingRecipes();
      CraftingRecipeWrapperShaped.registerCraftingRecipes();
   }
}
