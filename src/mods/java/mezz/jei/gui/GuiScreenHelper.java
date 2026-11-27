package mezz.jei.gui;

import com.arlenh7.eaglercraft.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.gui.IGhostIngredientHandler;
import mezz.jei.api.gui.IGlobalGuiHandler;
import mezz.jei.api.gui.IGuiProperties;
import mezz.jei.api.gui.IGuiScreenHandler;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.input.ClickedIngredient;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GuiScreenHelper {
   private final IngredientRegistry ingredientRegistry;
   private final List<IGlobalGuiHandler> globalGuiHandlers;
   private final List<IAdvancedGuiHandler<?>> advancedGuiHandlers;
   private final Map<Class, IGhostIngredientHandler> ghostIngredientHandlers;
   private final Map<Class, IGuiScreenHandler> guiScreenHandlers;
   private Set<Rectangle> guiExclusionAreas = Collections.emptySet();

   public GuiScreenHelper(IngredientRegistry ingredientRegistry, List<IGlobalGuiHandler> globalGuiHandlers, List<IAdvancedGuiHandler<?>> advancedGuiHandlers, Map<Class, IGhostIngredientHandler> ghostIngredientHandlers, Map<Class, IGuiScreenHandler> guiScreenHandlers) {
      this.ingredientRegistry = ingredientRegistry;
      this.globalGuiHandlers = globalGuiHandlers;
      this.advancedGuiHandlers = advancedGuiHandlers;
      this.ghostIngredientHandlers = ghostIngredientHandlers;
      this.guiScreenHandlers = guiScreenHandlers;
   }

   @Nullable
   public <T extends GuiScreen> IGuiProperties getGuiProperties(@Nullable T guiScreen) {
      if (guiScreen == null) {
         return null;
      } else {
         IGuiScreenHandler<T> handler = (IGuiScreenHandler)this.guiScreenHandlers.get(guiScreen.getClass());
         if (handler != null) {
            return handler.apply(guiScreen);
         } else {
            for(Entry<Class, IGuiScreenHandler> entry : this.guiScreenHandlers.entrySet()) {
               Class guiScreenClass = (Class)entry.getKey();
               if (guiScreenClass.isInstance(guiScreen)) {
                  IGuiScreenHandler<T> superHandler = (IGuiScreenHandler)entry.getValue();
                  if (superHandler != null) {
                     return superHandler.apply(guiScreen);
                  }
               }
            }

            return null;
         }
      }
   }

   public boolean updateGuiExclusionAreas() {
      Set<Rectangle> guiAreas = this.getPluginsExclusionAreas();
      if (!guiAreas.equals(this.guiExclusionAreas)) {
         this.guiExclusionAreas = (Set)guiAreas.stream().map(Rectangle::new).collect(Collectors.toSet());
         return true;
      } else {
         return false;
      }
   }

   public Set<Rectangle> getGuiExclusionAreas() {
      return this.guiExclusionAreas;
   }

   public boolean isInGuiExclusionArea(int mouseX, int mouseY) {
      return MathUtil.contains(this.guiExclusionAreas, mouseX, mouseY);
   }

   private Set<Rectangle> getPluginsExclusionAreas() {
      GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
      if (guiScreen == null) {
         return Collections.emptySet();
      } else {
         Set<Rectangle> allGuiExtraAreas = new HashSet();
         if (guiScreen instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer)guiScreen;

            for(IAdvancedGuiHandler<GuiContainer> advancedGuiHandler : this.getActiveAdvancedGuiHandlers(guiContainer)) {
               List<Rectangle> guiExtraAreas = advancedGuiHandler.getGuiExtraAreas(guiContainer);
               if (guiExtraAreas != null) {
                  allGuiExtraAreas.addAll(guiExtraAreas);
               }
            }
         }

         for(IGlobalGuiHandler globalGuiHandler : this.globalGuiHandlers) {
            Collection<Rectangle> guiExtraAreas = globalGuiHandler.getGuiExtraAreas();
            allGuiExtraAreas.addAll(guiExtraAreas);
         }

         return allGuiExtraAreas;
      }
   }

   @Nullable
   public <T extends GuiContainer> IClickedIngredient<?> getPluginsIngredientUnderMouse(T guiContainer, int mouseX, int mouseY) {
      for(IAdvancedGuiHandler<T> advancedGuiHandler : this.getActiveAdvancedGuiHandlers(guiContainer)) {
         Object clicked = advancedGuiHandler.getIngredientUnderMouse(guiContainer, mouseX, mouseY);
         IClickedIngredient<?> clickedIngredient = this.createClickedIngredient(clicked, guiContainer);
         if (clickedIngredient != null) {
            return clickedIngredient;
         }
      }

      for(IGlobalGuiHandler globalGuiHandler : this.globalGuiHandlers) {
         Object clicked = globalGuiHandler.getIngredientUnderMouse(mouseX, mouseY);
         IClickedIngredient<?> clickedIngredient = this.createClickedIngredient(clicked, guiContainer);
         if (clickedIngredient != null) {
            return clickedIngredient;
         }
      }

      return null;
   }

   @Nullable
   public <T extends GuiScreen> IGhostIngredientHandler<T> getGhostIngredientHandler(T guiScreen) {
      IGhostIngredientHandler<T> handler = (IGhostIngredientHandler)this.ghostIngredientHandlers.get(guiScreen.getClass());
      if (handler != null) {
         return handler;
      } else {
         for(Entry<Class, IGhostIngredientHandler> entry : this.ghostIngredientHandlers.entrySet()) {
            Class guiScreenClass = (Class)entry.getKey();
            if (guiScreenClass.isInstance(guiScreen)) {
               IGhostIngredientHandler<T> superHandler = (IGhostIngredientHandler)entry.getValue();
               if (superHandler != null) {
                  return superHandler;
               }
            }
         }

         return null;
      }
   }

   @Nullable
   private <T> IClickedIngredient<T> createClickedIngredient(@Nullable T ingredient, GuiContainer guiContainer) {
      if (ingredient != null && this.ingredientRegistry.isValidIngredient(ingredient)) {
         Rectangle area = null;
         Slot slotUnderMouse = guiContainer.getSlotUnderMouse();
         if (ingredient instanceof ItemStack && slotUnderMouse != null && ItemStack.areItemStacksEqual(slotUnderMouse.getStack(), (ItemStack)ingredient)) {
            area = new Rectangle(slotUnderMouse.xPos, slotUnderMouse.yPos, 16, 16);
         }

         return ClickedIngredient.<T>create(ingredient, area);
      } else {
         return null;
      }
   }

   private <T extends GuiContainer> List<IAdvancedGuiHandler<T>> getActiveAdvancedGuiHandlers(T guiContainer) {
      List<IAdvancedGuiHandler<T>> activeAdvancedGuiHandler = new ArrayList();

      for(IAdvancedGuiHandler<T> advancedGuiHandler : (List<IAdvancedGuiHandler<T>>)(List)this.advancedGuiHandlers) {
         Class<?> guiContainerClass = advancedGuiHandler.getGuiContainerClass();
         if (guiContainerClass.isInstance(guiContainer)) {
            activeAdvancedGuiHandler.add(advancedGuiHandler);
         }
      }

      return activeAdvancedGuiHandler;
   }
}
