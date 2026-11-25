package yalter.mousetweaks;

import java.io.File;
import java.util.List;
import org.lwjgl.input.Keyboard;
import yalter.mousetweaks.api.IMTModGuiContainer;
import yalter.mousetweaks.api.IMTModGuiContainer2;
import yalter.mousetweaks.api.IMTModGuiContainer2Ex;
import yalter.mousetweaks.forge.ForgeMouseState;
import yalter.mousetweaks.handlers.GuiContainerCreativeHandler;
import yalter.mousetweaks.handlers.GuiContainerHandler;
import yalter.mousetweaks.handlers.IMTModGuiContainer2ExHandler;
import yalter.mousetweaks.handlers.IMTModGuiContainer2Handler;
import yalter.mousetweaks.handlers.IMTModGuiContainerHandler;

public class Main {
   private static boolean liteLoader = false;
   private static boolean forge = false;
   public static Config config;
   public static OnTickMethod onTickMethod;
   private static bib mc;
   private static IMouseState mouseState = new SimpleMouseState();
   private static blk oldGuiScreen = null;
   private static agr oldSelectedSlot = null;
   private static agr firstRightClickedSlot = null;
   private static boolean oldRMBDown = false;
   private static boolean disableForThisContainer = false;
   private static boolean disableWheelForThisContainer = false;
   private static IGuiScreenHandler handler = null;
   private static boolean readConfig = false;
   private static boolean initialized = false;
   private static boolean disabled = false;

   public Main() {
   }

   public static boolean initialize(Constants.EntryPoint entryPoint) {
      Logger.Log("A call to initialize, entry point: " + entryPoint.toString() + ".");
      if (disabled) {
         return false;
      } else if (initialized) {
         return true;
      } else {
         initialized = true;
         mc = bib.z();
         config = new Config(mc.w + File.separator + "config" + File.separator + "MouseTweaks.cfg");
         config.read();
         Reflection.reflectGuiContainer();
         forge = entryPoint == Constants.EntryPoint.FORGE || Reflection.doesClassExist("net.minecraftforge.client.MinecraftForgeClient");
         if (forge) {
            Logger.Log("Minecraft Forge is installed.");
         } else {
            Logger.Log("Minecraft Forge is not installed.");
         }

         liteLoader = entryPoint == Constants.EntryPoint.LITELOADER || Reflection.doesClassExist("com.mumfrey.liteloader.core.LiteLoader");
         if (liteLoader) {
            Logger.Log("LiteLoader is installed.");
         } else {
            Logger.Log("LiteLoader is not installed.");
         }

         if (!findOnTickMethod(true)) {
            disabled = true;
            return false;
         } else {
            Logger.Log("Mouse Tweaks has been initialized.");
            return true;
         }
      }
   }

   public static boolean findOnTickMethod(boolean print_always) {
      OnTickMethod previous_method = onTickMethod;

      for(OnTickMethod method : config.onTickMethodOrder) {
         switch(method) {
         case FORGE:
            if (forge) {
               onTickMethod = OnTickMethod.FORGE;
               if (mouseState.getClass() != ForgeMouseState.class) {
                  Logger.DebugLog("Switching to ForgeMouseState.");
                  mouseState = new ForgeMouseState();
               }

               ((ForgeMouseState)mouseState).simpleScrolling = config.scrollHandling == ScrollHandling.SIMPLE;
               if (print_always || onTickMethod != previous_method) {
                  Logger.Log("Using Forge for the mod operation.");
               }

               return true;
            }
            break;
         case LITELOADER:
            if (liteLoader) {
               onTickMethod = OnTickMethod.LITELOADER;
               if (mouseState.getClass() != SimpleMouseState.class) {
                  Logger.DebugLog("Switching to SimpleMouseState.");
                  mouseState = new SimpleMouseState();
               }

               if (print_always || onTickMethod != previous_method) {
                  Logger.Log("Using LiteLoader for the mod operation.");
               }

               return true;
            }
         }
      }

      return false;
   }

   public static void onUpdateInGame() {
      blk currentScreen = mc.m;
      if (currentScreen == null) {
         oldGuiScreen = null;
         oldSelectedSlot = null;
         firstRightClickedSlot = null;
         disableForThisContainer = false;
         disableWheelForThisContainer = false;
         readConfig = true;
         handler = null;
      } else {
         if (readConfig) {
            readConfig = false;
            config.read();
            findOnTickMethod(false);
         }

         onUpdateInGui(currentScreen);
      }

      oldRMBDown = mouseState.isButtonPressed(MouseButton.RIGHT);
   }

   public static void onMouseInput() {
      mouseState.update();
   }

   private static void onUpdateInGui(blk currentScreen) {
      if (oldGuiScreen != currentScreen) {
         oldGuiScreen = currentScreen;
         Logger.DebugLog("You have just opened " + currentScreen.getClass().getSimpleName() + ".");
         handler = findHandler(currentScreen);
         mouseState.clear();
         if (handler == null) {
            disableForThisContainer = true;
            Logger.DebugLog("No valid handler found; MT is disabled.");
            return;
         }

         disableForThisContainer = handler.isMouseTweaksDisabled();
         disableWheelForThisContainer = handler.isWheelTweakDisabled();
         Logger.DebugLog("Handler: " + handler.getClass().getSimpleName() + "; MT is " + (disableForThisContainer ? "disabled" : "enabled") + "; wheel tweak is " + (disableWheelForThisContainer ? "disabled" : "enabled") + ".");
      }

      if (config.rmbTweak || config.lmbTweakWithItem || config.lmbTweakWithoutItem || config.wheelTweak) {
         if (!disableForThisContainer) {
            agr selectedSlot = handler.getSlotUnderMouse();
            if (mouseState.isButtonPressed(MouseButton.RIGHT)) {
               if (!oldRMBDown) {
                  firstRightClickedSlot = selectedSlot;
               }

               if (config.rmbTweak && handler.disableRMBDraggingFunctionality() && firstRightClickedSlot != null && (firstRightClickedSlot != selectedSlot || oldSelectedSlot == selectedSlot) && !handler.isIgnored(firstRightClickedSlot) && !handler.isCraftingOutput(firstRightClickedSlot)) {
                  aip targetStack = firstRightClickedSlot.d();
                  aip stackOnMouse = mc.h.bv.q();
                  if (!stackOnMouse.b() && areStacksCompatible(stackOnMouse, targetStack) && firstRightClickedSlot.a(stackOnMouse)) {
                     handler.clickSlot(firstRightClickedSlot, MouseButton.RIGHT, false);
                  }
               }
            } else {
               firstRightClickedSlot = null;
            }

            if (oldSelectedSlot != selectedSlot) {
               oldSelectedSlot = selectedSlot;
               if (selectedSlot == null) {
                  return;
               }

               if (firstRightClickedSlot == selectedSlot) {
                  firstRightClickedSlot = null;
               }

               Logger.DebugLog("You have selected a new slot, it's slot number is " + selectedSlot.e);
               aip targetStack = selectedSlot.d().l();
               aip stackOnMouse = mc.h.bv.q().l();
               boolean shiftIsDown = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
               if (mouseState.isButtonPressed(MouseButton.RIGHT)) {
                  if (config.rmbTweak && !handler.isIgnored(selectedSlot) && !handler.isCraftingOutput(selectedSlot) && !stackOnMouse.b() && areStacksCompatible(stackOnMouse, targetStack) && selectedSlot.a(stackOnMouse)) {
                     handler.clickSlot(selectedSlot, MouseButton.RIGHT, false);
                  }
               } else if (mouseState.isButtonPressed(MouseButton.LEFT)) {
                  if (!stackOnMouse.b()) {
                     if (config.lmbTweakWithItem && !handler.isIgnored(selectedSlot) && !targetStack.b() && areStacksCompatible(stackOnMouse, targetStack)) {
                        if (shiftIsDown) {
                           handler.clickSlot(selectedSlot, MouseButton.LEFT, true);
                        } else if (stackOnMouse.E() + targetStack.E() <= stackOnMouse.d()) {
                           handler.clickSlot(selectedSlot, MouseButton.LEFT, false);
                           if (!handler.isCraftingOutput(selectedSlot)) {
                              handler.clickSlot(selectedSlot, MouseButton.LEFT, false);
                           }
                        }
                     }
                  } else if (config.lmbTweakWithoutItem && !targetStack.b() && shiftIsDown && !handler.isIgnored(selectedSlot)) {
                     handler.clickSlot(selectedSlot, MouseButton.LEFT, true);
                  }
               }
            }

            handleWheel(selectedSlot);
         }
      }
   }

   private static void handleWheel(agr selectedSlot) {
      if (config.wheelTweak && !disableWheelForThisContainer) {
         int wheel = mouseState.consumeScrollAmount();
         int numItemsToMove = Math.abs(wheel);
         if (numItemsToMove != 0 && selectedSlot != null && !handler.isIgnored(selectedSlot)) {
            aip stackOnMouse = mc.h.bv.q().l();
            aip originalStack = selectedSlot.d().l();
            boolean isCraftingOutput = handler.isCraftingOutput(selectedSlot);
            if (!originalStack.b()) {
               if (!stackOnMouse.b()) {
                  if (isCraftingOutput) {
                     if (!areStacksCompatible(originalStack, stackOnMouse)) {
                        return;
                     }
                  } else if (areStacksCompatible(originalStack, stackOnMouse)) {
                     return;
                  }
               }

               List<agr> slots = handler.getSlots();
               boolean pushItems = wheel < 0;
               if (config.wheelScrollDirection.isPositionAware() && otherInventoryIsAbove(selectedSlot, slots)) {
                  pushItems = !pushItems;
               }

               if (config.wheelScrollDirection.isInverted()) {
                  pushItems = !pushItems;
               }

               if (isCraftingOutput) {
                  if (pushItems) {
                     if (originalStack.b()) {
                        return;
                     }

                     agr applicableSlot = findWheelApplicableSlot(slots, selectedSlot, pushItems);

                     for(int i = 0; i < numItemsToMove; ++i) {
                        handler.clickSlot(selectedSlot, MouseButton.LEFT, false);
                     }

                     if (applicableSlot != null && stackOnMouse.b()) {
                        handler.clickSlot(applicableSlot, MouseButton.LEFT, false);
                     }
                  }

               } else {
                  while(true) {
                     agr applicableSlot = findWheelApplicableSlot(slots, selectedSlot, pushItems);
                     if (applicableSlot == null) {
                        break;
                     }

                     if (pushItems) {
                        agr slotTo = applicableSlot;
                        aip stackTo = applicableSlot.d().l();
                        aip stackFrom = selectedSlot.d().l();
                        numItemsToMove = Math.min(numItemsToMove, stackFrom.E());
                        if (stackTo.b() || stackTo.d() - stackTo.E() > numItemsToMove) {
                           handler.clickSlot(selectedSlot, MouseButton.LEFT, false);
                           if (stackFrom.E() <= numItemsToMove) {
                              handler.clickSlot(applicableSlot, MouseButton.LEFT, false);
                           } else {
                              for(int i = 0; i < numItemsToMove; ++i) {
                                 handler.clickSlot(slotTo, MouseButton.RIGHT, false);
                              }
                           }

                           handler.clickSlot(selectedSlot, MouseButton.LEFT, false);
                           break;
                        }

                        handler.clickSlot(selectedSlot, MouseButton.LEFT, false);
                        handler.clickSlot(applicableSlot, MouseButton.LEFT, false);
                        handler.clickSlot(selectedSlot, MouseButton.LEFT, false);
                        numItemsToMove = numItemsToMove - (stackTo.d() - stackTo.E());
                     } else {
                        agr slotTo = selectedSlot;
                        aip stackTo = selectedSlot.d().l();
                        aip stackFrom = applicableSlot.d().l();
                        if (stackTo.E() == stackTo.d()) {
                           break;
                        }

                        if (stackTo.d() - stackTo.E() <= numItemsToMove) {
                           handler.clickSlot(applicableSlot, MouseButton.LEFT, false);
                           handler.clickSlot(selectedSlot, MouseButton.LEFT, false);
                           if (!handler.isCraftingOutput(applicableSlot)) {
                              handler.clickSlot(applicableSlot, MouseButton.LEFT, false);
                           }
                        } else {
                           handler.clickSlot(applicableSlot, MouseButton.LEFT, false);
                           if (handler.isCraftingOutput(applicableSlot)) {
                              handler.clickSlot(selectedSlot, MouseButton.LEFT, false);
                              --numItemsToMove;
                           } else if (stackFrom.E() <= numItemsToMove) {
                              handler.clickSlot(selectedSlot, MouseButton.LEFT, false);
                              numItemsToMove -= stackFrom.E();
                           } else {
                              for(int i = 0; i < numItemsToMove; ++i) {
                                 handler.clickSlot(slotTo, MouseButton.RIGHT, false);
                              }

                              numItemsToMove = 0;
                           }

                           if (!handler.isCraftingOutput(applicableSlot)) {
                              handler.clickSlot(applicableSlot, MouseButton.LEFT, false);
                           }
                        }
                     }

                     if (numItemsToMove <= 0) {
                        break;
                     }
                  }

               }
            }
         }
      }
   }

   private static boolean otherInventoryIsAbove(agr selectedSlot, List<agr> slots) {
      boolean selectedIsInPlayerInventory = selectedSlot.d == mc.h.bv;

      for(agr slot : slots) {
         if (slot.d == mc.h.bv != selectedIsInPlayerInventory && slot.g < selectedSlot.g) {
            return true;
         }
      }

      return false;
   }

   private static IGuiScreenHandler findHandler(blk currentScreen) {
      if (currentScreen instanceof IMTModGuiContainer2Ex) {
         return new IMTModGuiContainer2ExHandler((IMTModGuiContainer2Ex)currentScreen);
      } else if (currentScreen instanceof IMTModGuiContainer2) {
         return new IMTModGuiContainer2Handler((IMTModGuiContainer2)currentScreen);
      } else if (currentScreen instanceof IMTModGuiContainer) {
         return new IMTModGuiContainerHandler((IMTModGuiContainer)currentScreen);
      } else if (currentScreen instanceof bmp) {
         return new GuiContainerCreativeHandler((bmp)currentScreen);
      } else {
         return currentScreen instanceof bmg ? new GuiContainerHandler((bmg)currentScreen) : null;
      }
   }

   private static boolean areStacksCompatible(aip a, aip b) {
      return a.b() || b.b() || a.a(b) && aip.a(a, b);
   }

   private static agr findWheelApplicableSlot(List<agr> slots, agr selectedSlot, boolean pushItems) {
      int startIndex;
      int endIndex;
      int direction;
      if (!pushItems && config.wheelSearchOrder != WheelSearchOrder.FIRST_TO_LAST) {
         startIndex = slots.size() - 1;
         endIndex = -1;
         direction = -1;
      } else {
         startIndex = 0;
         endIndex = slots.size();
         direction = 1;
      }

      aip originalStack = selectedSlot.d();
      boolean findInPlayerInventory = selectedSlot.d != mc.h.bv;
      agr rv = null;

      for(int i = startIndex; i != endIndex; i += direction) {
         agr slot = (agr)slots.get(i);
         if (!handler.isIgnored(slot)) {
            if (findInPlayerInventory) {
               if (slot.d != mc.h.bv) {
                  continue;
               }
            } else if (slot.d == mc.h.bv) {
               continue;
            }

            aip stack = slot.d();
            if (stack.b()) {
               if (rv == null && pushItems && slot.a(originalStack) && !handler.isCraftingOutput(slot)) {
                  rv = slot;
               }
            } else if (areStacksCompatible(originalStack, stack)) {
               if (!pushItems) {
                  return slot;
               }

               if (!handler.isCraftingOutput(slot) && stack.E() < stack.d()) {
                  return slot;
               }
            }
         }
      }

      return rv;
   }
}
