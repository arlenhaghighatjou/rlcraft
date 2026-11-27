package mezz.jei.gui;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import mezz.jei.config.Config;
import mezz.jei.config.OverlayToggleEvent;
import mezz.jei.gui.overlay.IngredientListOverlay;
import mezz.jei.gui.overlay.bookmarks.LeftAreaDispatcher;
import mezz.jei.recipes.RecipeRegistry;
import mezz.jei.util.LimitedLogger;
import mezz.jei.util.Log;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiContainerEvent.DrawForeground;
import net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent;
import net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class GuiEventHandler {
   private final IngredientListOverlay ingredientListOverlay;
   private final GuiScreenHelper guiScreenHelper;
   private final LeftAreaDispatcher leftAreaDispatcher;
   private final RecipeRegistry recipeRegistry;
   private final LimitedLogger missingBackgroundLogger = new LimitedLogger(Log.get(), 3600000L);
   private boolean drawnOnBackground = false;

   public GuiEventHandler(GuiScreenHelper guiScreenHelper, LeftAreaDispatcher leftAreaDispatcher, IngredientListOverlay ingredientListOverlay, RecipeRegistry recipeRegistry) {
      this.guiScreenHelper = guiScreenHelper;
      this.leftAreaDispatcher = leftAreaDispatcher;
      this.ingredientListOverlay = ingredientListOverlay;
      this.recipeRegistry = recipeRegistry;
   }

   public void register(EventBus bus) {
      bus.register(OverlayToggleEvent.class, this::onOverlayToggle);
      bus.register(Post.class, this::onGuiInit);
      bus.register(GuiOpenEvent.class, this::onGuiOpen);
      bus.register(BackgroundDrawnEvent.class, this::onDrawBackgroundEventPost);
      bus.register(DrawForeground.class, this::onDrawForegroundEvent);
      bus.register(net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent.Post.class, this::onDrawScreenEventPost);
      bus.register(ClientTickEvent.class, this::onClientTick);
      bus.register(PotionShiftEvent.class, this::onPotionShiftEvent);
   }

   public void onOverlayToggle(OverlayToggleEvent event) {
      GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
      this.ingredientListOverlay.updateScreen(currentScreen, true);
      this.leftAreaDispatcher.updateScreen(currentScreen, false);
   }

   public void onGuiInit(Post event) {
      GuiScreen gui = event.getGui();
      this.ingredientListOverlay.updateScreen(gui, false);
      this.leftAreaDispatcher.updateScreen(gui, false);
   }

   public void onGuiOpen(GuiOpenEvent event) {
      GuiScreen gui = event.getGui();
      this.ingredientListOverlay.updateScreen(gui, false);
      this.leftAreaDispatcher.updateScreen(gui, false);
   }

   public void onDrawBackgroundEventPost(BackgroundDrawnEvent event) {
      GuiScreen gui = event.getGui();
      Minecraft minecraft = gui.mc;
      if (minecraft != null) {
         boolean exclusionAreasChanged = this.guiScreenHelper.updateGuiExclusionAreas();
         this.ingredientListOverlay.updateScreen(gui, exclusionAreasChanged);
         this.leftAreaDispatcher.updateScreen(gui, exclusionAreasChanged);
         this.drawnOnBackground = true;
         this.ingredientListOverlay.drawScreen(minecraft, event.getMouseX(), event.getMouseY(), minecraft.getRenderPartialTicks());
         this.leftAreaDispatcher.drawScreen(minecraft, event.getMouseX(), event.getMouseY(), minecraft.getRenderPartialTicks());
      }
   }

   public void onDrawForegroundEvent(DrawForeground event) {
      GuiContainer gui = event.getGuiContainer();
      Minecraft minecraft = gui.mc;
      if (minecraft != null) {
         this.ingredientListOverlay.drawOnForeground(minecraft, gui, event.getMouseX(), event.getMouseY());
         this.leftAreaDispatcher.drawOnForeground(gui, event.getMouseX(), event.getMouseY());
      }
   }

   public void onDrawScreenEventPost(net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent.Post event) {
      GuiScreen gui = event.getGui();
      Minecraft minecraft = gui.mc;
      if (minecraft != null) {
         this.ingredientListOverlay.updateScreen(gui, false);
         this.leftAreaDispatcher.updateScreen(gui, false);
         if (!this.drawnOnBackground) {
            if (gui instanceof GuiContainer) {
               String guiName = gui.getClass().getName();
               this.missingBackgroundLogger.log(Level.WARN, guiName, "GUI did not draw the dark background layer behind itself, this may result in display issues: {}", guiName);
            }

            this.ingredientListOverlay.drawScreen(minecraft, event.getMouseX(), event.getMouseY(), minecraft.getRenderPartialTicks());
            this.leftAreaDispatcher.drawScreen(minecraft, event.getMouseX(), event.getMouseY(), minecraft.getRenderPartialTicks());
         }

         this.drawnOnBackground = false;
         if (gui instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer)gui;
            if (this.recipeRegistry.getRecipeClickableArea(guiContainer, event.getMouseX() - guiContainer.getGuiLeft(), event.getMouseY() - guiContainer.getGuiTop()) != null) {
               String showRecipesText = Translator.translateToLocal("jei.tooltip.show.recipes");
               TooltipRenderer.drawHoveringText(minecraft, showRecipesText, event.getMouseX(), event.getMouseY());
            }
         }

         this.ingredientListOverlay.drawTooltips(minecraft, event.getMouseX(), event.getMouseY());
         this.leftAreaDispatcher.drawTooltips(minecraft, event.getMouseX(), event.getMouseY());
      }
   }

   public void onClientTick(ClientTickEvent event) {
      if (event.phase != Phase.END) {
         this.ingredientListOverlay.handleTick();
      }
   }

   public void onPotionShiftEvent(PotionShiftEvent event) {
      if (Config.isOverlayEnabled()) {
         event.setCanceled(true);
      }

   }
}
