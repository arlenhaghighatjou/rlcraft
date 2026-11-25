package reborncore.client.gui.builder.slot;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.builder.slot.elements.ConfigFluidElement;
import reborncore.client.gui.builder.slot.elements.ElementBase;
import reborncore.client.gui.builder.slot.elements.SlotType;
import reborncore.common.tile.RebornMachineTile;

public class GuiFluidConfiguration {
   static ConfigFluidElement fluidConfigElement;

   public GuiFluidConfiguration() {
   }

   public static void init(GuiBase guiBase) {
      fluidConfigElement = new ConfigFluidElement(((RebornMachineTile)Objects.requireNonNull(guiBase.getMachine())).getTank(), SlotType.NORMAL, 35 - guiBase.guiLeft + 50, 35 - guiBase.guiTop - 25, guiBase);
   }

   public static void draw(GuiBase guiBase, int mouseX, int mouseY) {
      fluidConfigElement.draw(guiBase);
   }

   @SubscribeEvent
   public static void keyboardEvent(KeyboardInputEvent event) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.FLUIDS && Keyboard.getEventKey() == 1) {
         GuiBase.slotConfigType = GuiBase.SlotConfigType.NONE;
         event.setCanceled(true);
      }

   }

   public static List<ConfigFluidElement> getVisibleElements() {
      return Collections.singletonList(fluidConfigElement);
   }

   public static boolean mouseClicked(int mouseX, int mouseY, int mouseButton, GuiBase guiBase) throws IOException {
      if (mouseButton == 0) {
         for(ConfigFluidElement configFluidElement : getVisibleElements()) {
            for(ElementBase element : configFluidElement.elements) {
               if (element.isInRect(guiBase, element.x, element.y, element.getWidth(guiBase.getMachine()), element.getHeight(guiBase.getMachine()), mouseX, mouseY)) {
                  element.isPressing = true;
                  boolean action = element.onStartPress(guiBase.getMachine(), guiBase, mouseX, mouseY);

                  for(ElementBase e : getVisibleElements()) {
                     if (e != element) {
                        e.isPressing = false;
                     }
                  }

                  if (action) {
                     break;
                  }
               } else {
                  element.isPressing = false;
               }
            }
         }
      }

      return !getVisibleElements().isEmpty();
   }

   public static void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick, GuiBase guiBase) {
      if (mouseButton == 0) {
         for(ConfigFluidElement configFluidElement : getVisibleElements()) {
            for(ElementBase element : configFluidElement.elements) {
               if (element.isInRect(guiBase, element.x, element.y, element.getWidth(guiBase.getMachine()), element.getHeight(guiBase.getMachine()), mouseX, mouseY)) {
                  element.isDragging = true;
                  boolean action = element.onDrag(guiBase.getMachine(), guiBase, mouseX, mouseY);

                  for(ElementBase e : getVisibleElements()) {
                     if (e != element) {
                        e.isDragging = false;
                     }
                  }

                  if (action) {
                     break;
                  }
               } else {
                  element.isDragging = false;
               }
            }
         }
      }

   }

   public static boolean mouseReleased(int mouseX, int mouseY, int mouseButton, GuiBase guiBase) {
      boolean clicked = false;
      if (mouseButton == 0) {
         for(ConfigFluidElement configFluidElement : getVisibleElements()) {
            if (configFluidElement.isInRect(guiBase, configFluidElement.x, configFluidElement.y, configFluidElement.getWidth(guiBase.getMachine()), configFluidElement.getHeight(guiBase.getMachine()), mouseX, mouseY)) {
               clicked = true;
            }

            for(ElementBase element : Lists.reverse(configFluidElement.elements)) {
               if (element.isInRect(guiBase, element.x, element.y, element.getWidth(guiBase.getMachine()), element.getHeight(guiBase.getMachine()), mouseX, mouseY)) {
                  element.isReleasing = true;
                  boolean action = element.onRelease(guiBase.getMachine(), guiBase, mouseX, mouseY);

                  for(ElementBase e : getVisibleElements()) {
                     if (e != element) {
                        e.isReleasing = false;
                     }
                  }

                  if (action) {
                     clicked = true;
                  }
                  break;
               }

               element.isReleasing = false;
            }
         }
      }

      return clicked;
   }

   @Nullable
   private static RebornMachineTile getMachine() {
      if (!(Minecraft.getMinecraft().currentScreen instanceof GuiBase)) {
         return null;
      } else {
         GuiBase base = (GuiBase)Minecraft.getMinecraft().currentScreen;
         if (!(base.tile instanceof RebornMachineTile)) {
            return null;
         } else {
            RebornMachineTile machineBase = (RebornMachineTile)base.tile;
            return machineBase;
         }
      }
   }
}
