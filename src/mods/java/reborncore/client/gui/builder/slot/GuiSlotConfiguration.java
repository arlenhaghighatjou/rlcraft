package reborncore.client.gui.builder.slot;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.client.gui.GuiUtil;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.builder.slot.elements.ConfigSlotElement;
import reborncore.client.gui.builder.slot.elements.ElementBase;
import reborncore.client.gui.builder.slot.elements.SlotType;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.packet.PacketConfigSave;
import reborncore.common.tile.RebornMachineTile;

public class GuiSlotConfiguration {
   static HashMap<Integer, ConfigSlotElement> slotElementMap = new HashMap();
   public static int selectedSlot = 0;

   public GuiSlotConfiguration() {
   }

   public static void reset() {
      selectedSlot = -1;
   }

   public static void init(GuiBase guiBase) {
      reset();
      slotElementMap.clear();
      BuiltContainer container = guiBase.container;

      for(Slot slot : container.inventorySlots) {
         if (guiBase.tile == slot.inventory) {
            ConfigSlotElement slotElement = new ConfigSlotElement(guiBase.getMachine(), slot.getSlotIndex(), SlotType.NORMAL, slot.xPos - guiBase.guiLeft + 50, slot.yPos - guiBase.guiTop - 25, guiBase);
            slotElementMap.put(Integer.valueOf(slot.getSlotIndex()), slotElement);
         }
      }

   }

   public static void draw(GuiBase guiBase, int mouseX, int mouseY) {
      BuiltContainer container = guiBase.container;

      for(Slot slot : container.inventorySlots) {
         if (guiBase.tile == slot.inventory) {
            GlStateManager.color(255.0F, 0.0F, 0.0F);
            Color color = new Color(255, 0, 0, 128);
            GuiUtil.drawGradientRect(slot.xPos - 1, slot.yPos - 1, 18, 18, color.getRGB(), color.getRGB());
            GlStateManager.color(255.0F, 255.0F, 255.0F);
         }
      }

      if (selectedSlot != -1) {
         ((ConfigSlotElement)slotElementMap.get(Integer.valueOf(selectedSlot))).draw(guiBase);
      }

   }

   public static List<ConfigSlotElement> getVisibleElements() {
      return selectedSlot == -1 ? Collections.emptyList() : (List)slotElementMap.values().stream().filter((configSlotElement) -> {
         return configSlotElement.getId() == selectedSlot;
      }).collect(Collectors.toList());
   }

   @SubscribeEvent
   public static void keyboardEvent(KeyboardInputEvent event) {
      if (!getVisibleElements().isEmpty() && Keyboard.getEventKey() == 1) {
         selectedSlot = -1;
         event.setCanceled(true);
      }

   }

   public static void copyToClipboard() {
      RebornMachineTile machine = getMachine();
      if (machine != null && machine.slotConfiguration != null) {
         String json = machine.slotConfiguration.toJson(machine.getClass().getCanonicalName());
         GuiScreen.setClipboardString(json);
         Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Slot configuration copyied to clipboard"));
      }
   }

   public static void pasteFromClipboard() {
      RebornMachineTile machine = getMachine();
      if (machine != null && machine.slotConfiguration != null) {
         String json = GuiScreen.getClipboardString();

         try {
            machine.slotConfiguration.readJson(json, machine.getClass().getCanonicalName());
            NetworkManager.sendToServer(new PacketConfigSave(machine.getPos(), machine.slotConfiguration));
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Slot configuration loaded from clipboard"));
         } catch (UnsupportedOperationException var3) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(var3.getMessage()));
         }

      }
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

   public static boolean mouseClicked(int mouseX, int mouseY, int mouseButton, GuiBase guiBase) throws IOException {
      if (mouseButton == 0) {
         for(ConfigSlotElement configSlotElement : getVisibleElements()) {
            for(ElementBase element : configSlotElement.elements) {
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

      BuiltContainer container = guiBase.container;
      if (getVisibleElements().isEmpty()) {
         for(Slot slot : container.inventorySlots) {
            if (guiBase.tile == slot.inventory && guiBase.isPointInRect(slot.xPos, slot.yPos, 18, 18, mouseX, mouseY)) {
               selectedSlot = slot.getSlotIndex();
               return true;
            }
         }
      }

      return !getVisibleElements().isEmpty();
   }

   public static void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick, GuiBase guiBase) {
      if (mouseButton == 0) {
         for(ConfigSlotElement configSlotElement : getVisibleElements()) {
            for(ElementBase element : configSlotElement.elements) {
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
         for(ConfigSlotElement configSlotElement : getVisibleElements()) {
            if (configSlotElement.isInRect(guiBase, configSlotElement.x, configSlotElement.y, configSlotElement.getWidth(guiBase.getMachine()), configSlotElement.getHeight(guiBase.getMachine()), mouseX, mouseY)) {
               clicked = true;
            }

            for(ElementBase element : Lists.reverse(configSlotElement.elements)) {
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

   public static List<Rectangle> getExtraSpace(GuiBase guiBase) {
      List<Rectangle> list = new ArrayList();
      if (guiBase.upgrades) {
         list.add(new Rectangle(guiBase.getGuiLeft() - 25, guiBase.getGuiTop(), 20, 120));
      }

      list.addAll(getExtraSpaceForConfig(guiBase));
      return list;
   }

   public static List<Rectangle> getExtraSpaceForConfig(GuiBase guiBase) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.ITEMS && selectedSlot != -1) {
         List<Rectangle> list = new ArrayList();
         ConfigSlotElement slotElement = (ConfigSlotElement)slotElementMap.get(Integer.valueOf(selectedSlot));
         if (slotElement != null && guiBase != null) {
            list.add(new Rectangle(slotElement.adjustX(guiBase, slotElement.getX()) + guiBase.getGuiLeft() - 25, slotElement.adjustY(guiBase, 0) - 10, slotElement.getWidth() - 5, slotElement.getHeight() + 15));
            return list;
         } else {
            return Collections.emptyList();
         }
      } else {
         return Collections.emptyList();
      }
   }
}
