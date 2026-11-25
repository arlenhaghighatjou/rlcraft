package invtweaks;

import invtweaks.api.container.ContainerSection;
import invtweaks.container.IContainerManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InvTweaksHandlerShortcuts extends InvTweaksObfuscation {
   private static final int DROP_SLOT = -999;
   private InvTweaksConfig config;
   private IContainerManager container;
   private Map<Integer, Boolean> pressedKeys;
   private Map<InvTweaksShortcutType, List<InvTweaksShortcutMapping>> shortcuts;

   public InvTweaksHandlerShortcuts(Minecraft mc_, InvTweaksConfig config_) {
      super(mc_);
      this.config = config_;
      this.pressedKeys = new HashMap();
      this.shortcuts = new HashMap();
   }

   public void loadShortcuts() {
      this.pressedKeys.clear();
      this.shortcuts.clear();
      Map<String, String> keys = this.config.getProperties("shortcutKey");

      for(String key : keys.keySet()) {
         String[] keyMappings = ((String)keys.get(key)).split("[ ]*,[ ]*");
         InvTweaksShortcutType shortcutType = InvTweaksShortcutType.fromConfigKey(key);
         if (shortcutType != null) {
            for(String keyMapping : keyMappings) {
               String[] keysToHold = keyMapping.split("\\+");
               this.registerShortcutMapping(shortcutType, new InvTweaksShortcutMapping(keysToHold));
            }
         }
      }

      int upKeyCode = this.getKeyBindingForwardKeyCode();
      int downKeyCode = this.getKeyBindingBackKeyCode();
      this.registerShortcutMapping(InvTweaksShortcutType.MOVE_UP, new InvTweaksShortcutMapping(upKeyCode));
      this.registerShortcutMapping(InvTweaksShortcutType.MOVE_DOWN, new InvTweaksShortcutMapping(downKeyCode));
      int[] hotbarKeys = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 79, 80, 81, 75, 76, 77, 71, 72, 73};

      for(int i : hotbarKeys) {
         this.registerShortcutMapping(InvTweaksShortcutType.MOVE_TO_SPECIFIC_HOTBAR_SLOT, new InvTweaksShortcutMapping(i));
      }

      this.pressedKeys.put(Integer.valueOf(42), Boolean.valueOf(false));
      this.pressedKeys.put(Integer.valueOf(54), Boolean.valueOf(false));
   }

   private void registerShortcutMapping(InvTweaksShortcutType type, @NotNull InvTweaksShortcutMapping mapping) {
      if (this.shortcuts.containsKey(type)) {
         ((List)this.shortcuts.get(type)).add(mapping);
      } else {
         List<InvTweaksShortcutMapping> newMappingList = new LinkedList();
         newMappingList.add(mapping);
         this.shortcuts.put(type, newMappingList);
      }

      Iterator var5 = mapping.getKeyCodes().iterator();

      while(var5.hasNext()) {
         int keyCode = ((Integer)var5.next()).intValue();
         this.pressedKeys.put(Integer.valueOf(keyCode), Boolean.valueOf(false));
      }

   }

   public void handleShortcut() {
      try {
         InvTweaksHandlerShortcuts.ShortcutConfig shortcutToTrigger = this.computeShortcutToTrigger();
         if (shortcutToTrigger != null) {
            int ex = Mouse.getEventX();
            int ey = Mouse.getEventY();
            this.runShortcut(shortcutToTrigger);
            Mouse.destroy();
            Mouse.create();
            Mouse.setCursorPosition(ex, ey);
         }
      } catch (Exception var4) {
         InvTweaks.logInGameErrorStatic("invtweaks.shortcut.error", var4);
      }

   }

   @Nullable
   public ShortcutSpecification computeCurrentShortcut() {
      ShortcutSpecification.Action action = ShortcutSpecification.Action.MOVE;
      ShortcutSpecification.Target target = ShortcutSpecification.Target.UNSPECIFIED;
      ShortcutSpecification.Scope scope = ShortcutSpecification.Scope.ONE_STACK;
      this.updatePressedKeys();
      boolean validAction = false;
      if (this.isShortcutDown(InvTweaksShortcutType.DROP) != null) {
         action = ShortcutSpecification.Action.DROP;
         validAction = true;
      }

      if (action != ShortcutSpecification.Action.DROP) {
         if (this.isShortcutDown(InvTweaksShortcutType.MOVE_TO_SPECIFIC_HOTBAR_SLOT) != null) {
            target = ShortcutSpecification.Target.HOTBAR_SLOT;
            validAction = true;
         } else if (this.isShortcutDown(InvTweaksShortcutType.MOVE_UP) != null) {
            target = ShortcutSpecification.Target.UP;
            validAction = true;
         } else if (this.isShortcutDown(InvTweaksShortcutType.MOVE_DOWN) != null) {
            target = ShortcutSpecification.Target.DOWN;
            validAction = true;
         }
      }

      if (this.isShortcutDown(InvTweaksShortcutType.MOVE_ALL_ITEMS) != null) {
         scope = ShortcutSpecification.Scope.ALL_ITEMS;
         validAction = true;
      } else if (this.isShortcutDown(InvTweaksShortcutType.MOVE_EVERYTHING) != null) {
         scope = ShortcutSpecification.Scope.EVERYTHING;
         validAction = true;
      } else if (this.isShortcutDown(InvTweaksShortcutType.MOVE_ONE_ITEM) != null) {
         scope = ShortcutSpecification.Scope.ONE_ITEM;
         validAction = true;
      }

      return validAction ? new ShortcutSpecification(action, target, scope) : null;
   }

   private InvTweaksHandlerShortcuts.ShortcutConfig computeShortcutToTrigger() {
      ShortcutSpecification shortcut = this.computeCurrentShortcut();
      InvTweaksHandlerShortcuts.ShortcutConfig shortcutConfig = new InvTweaksHandlerShortcuts.ShortcutConfig();
      this.container = InvTweaks.getCurrentContainerManager();
      Slot slot = InvTweaksObfuscation.getSlotAtMousePosition((GuiContainer)this.getCurrentScreen());
      if (shortcut != null && slot != null && (slot.getHasStack() || !this.getHeldStack().isEmpty())) {
         int slotNumber = getSlotNumber(slot);
         shortcutConfig.fromSection = this.container.getSlotSection(slotNumber);
         shortcutConfig.fromIndex = this.container.getSlotIndex(slotNumber);
         shortcutConfig.fromStack = slot.getHasStack() ? slot.getStack().copy() : this.getHeldStack().copy();
         if (!slot.isItemValid(slot.getStack()) && shortcut.getScope() == ShortcutSpecification.Scope.ONE_ITEM) {
            shortcut.setScope(ShortcutSpecification.Scope.ONE_STACK);
         }

         if (shortcutConfig.fromSection != null && shortcutConfig.fromIndex != -1) {
            if (shortcut.getAction() != ShortcutSpecification.Action.DROP) {
               if (shortcut.getTarget() == ShortcutSpecification.Target.HOTBAR_SLOT) {
                  shortcutConfig.toSection = ContainerSection.INVENTORY_HOTBAR;
                  InvTweaksShortcutMapping hotbarShortcut = this.isShortcutDown(InvTweaksShortcutType.MOVE_TO_SPECIFIC_HOTBAR_SLOT);
                  if (hotbarShortcut != null && !hotbarShortcut.getKeyCodes().isEmpty()) {
                     String keyName = Keyboard.getKeyName(((Integer)hotbarShortcut.getKeyCodes().get(0)).intValue());
                     shortcutConfig.toIndex = -1 + Integer.parseInt(keyName.replace("NUMPAD", ""));
                  }
               } else {
                  List<ContainerSection> orderedSections = new ArrayList();
                  if (this.container.hasSection(ContainerSection.CHEST)) {
                     orderedSections.add(ContainerSection.CHEST);
                  } else if (this.container.hasSection(ContainerSection.CRAFTING_IN)) {
                     orderedSections.add(ContainerSection.CRAFTING_IN);
                  } else if (this.container.hasSection(ContainerSection.CRAFTING_IN_PERSISTENT)) {
                     orderedSections.add(ContainerSection.CRAFTING_IN_PERSISTENT);
                  } else if (this.container.hasSection(ContainerSection.FURNACE_IN)) {
                     orderedSections.add(ContainerSection.FURNACE_IN);
                  } else if (this.container.hasSection(ContainerSection.BREWING_INGREDIENT)) {
                     if (!shortcutConfig.fromStack.isEmpty()) {
                        if (shortcutConfig.fromStack.getItem() == Item.REGISTRY.getObject(new ResourceLocation("potion"))) {
                           orderedSections.add(ContainerSection.BREWING_BOTTLES);
                        } else {
                           orderedSections.add(ContainerSection.BREWING_INGREDIENT);
                        }
                     }
                  } else if (this.container.hasSection(ContainerSection.ENCHANTMENT)) {
                     orderedSections.add(ContainerSection.ENCHANTMENT);
                  }

                  orderedSections.add(ContainerSection.INVENTORY_NOT_HOTBAR);
                  orderedSections.add(ContainerSection.INVENTORY_HOTBAR);
                  if (shortcut.getTarget() != ShortcutSpecification.Target.UNSPECIFIED) {
                     int sectionOffset = 0;
                     if (shortcut.getTarget() == ShortcutSpecification.Target.UP) {
                        --sectionOffset;
                     } else if (shortcut.getTarget() == ShortcutSpecification.Target.DOWN) {
                        ++sectionOffset;
                     }

                     int fromSectionIndex = orderedSections.indexOf(shortcutConfig.fromSection);
                     if (fromSectionIndex != -1) {
                        shortcutConfig.toSection = (ContainerSection)orderedSections.get((orderedSections.size() + fromSectionIndex + sectionOffset) % orderedSections.size());
                     } else {
                        shortcutConfig.toSection = ContainerSection.INVENTORY;
                     }
                  } else {
                     switch(shortcutConfig.fromSection) {
                     case CHEST:
                        shortcutConfig.toSection = ContainerSection.INVENTORY;
                        break;
                     case INVENTORY_HOTBAR:
                        if (orderedSections.contains(ContainerSection.CHEST)) {
                           shortcutConfig.toSection = ContainerSection.CHEST;
                        } else {
                           shortcutConfig.toSection = ContainerSection.INVENTORY_NOT_HOTBAR;
                        }
                        break;
                     case CRAFTING_IN:
                     case CRAFTING_IN_PERSISTENT:
                     case FURNACE_IN:
                        shortcutConfig.toSection = ContainerSection.INVENTORY_NOT_HOTBAR;
                        break;
                     default:
                        if (orderedSections.contains(ContainerSection.CHEST)) {
                           shortcutConfig.toSection = ContainerSection.CHEST;
                        } else {
                           shortcutConfig.toSection = ContainerSection.INVENTORY_HOTBAR;
                        }
                     }
                  }
               }
            }

            shortcutConfig.forceEmptySlot = Mouse.isButtonDown(1);
            shortcutConfig.action = shortcut.getAction();
            shortcutConfig.scope = shortcut.getScope();
            return shortcutConfig;
         }
      }

      return null;
   }

   public void updatePressedKeys() {
      if (this.haveControlsChanged()) {
         this.loadShortcuts();
      }

      Iterator var1 = this.pressedKeys.keySet().iterator();

      while(var1.hasNext()) {
         int keyCode = ((Integer)var1.next()).intValue();
         if (keyCode > 0 && Keyboard.isKeyDown(keyCode)) {
            if (!((Boolean)this.pressedKeys.get(Integer.valueOf(keyCode))).booleanValue()) {
               this.pressedKeys.put(Integer.valueOf(keyCode), Boolean.valueOf(true));
            }
         } else {
            this.pressedKeys.put(Integer.valueOf(keyCode), Boolean.valueOf(false));
         }
      }

   }

   private boolean haveControlsChanged() {
      return !this.pressedKeys.containsKey(Integer.valueOf(this.getKeyBindingForwardKeyCode())) || !this.pressedKeys.containsKey(Integer.valueOf(this.getKeyBindingBackKeyCode()));
   }

   private void runShortcut(@NotNull InvTweaksHandlerShortcuts.ShortcutConfig shortcut) throws TimeoutException {
      if (!this.getHeldStack().isEmpty()) {
         Slot slot = InvTweaksObfuscation.getSlotAtMousePosition((GuiContainer)this.getCurrentScreen());
         if (slot == null) {
            return;
         }

         int slotNumber = getSlotNumber(slot);
         this.container.putHoldItemDown(this.container.getSlotSection(slotNumber), this.container.getSlotIndex(slotNumber));
         if (!this.getHeldStack().isEmpty()) {
            return;
         }
      }

      synchronized(this) {
         if (shortcut.toSection == ContainerSection.INVENTORY_HOTBAR && shortcut.toIndex != -1) {
            this.container.move(shortcut.fromSection, shortcut.fromIndex, shortcut.toSection, shortcut.toIndex);
         } else {
            switch(shortcut.action) {
            case DROP:
               switch(shortcut.scope) {
               case ONE_ITEM:
                  this.container.dropSome(shortcut.fromSection, shortcut.fromIndex, 1);
                  break;
               case ONE_STACK:
                  this.container.drop(shortcut.fromSection, shortcut.fromIndex);
                  break;
               case ALL_ITEMS:
                  this.dropAll(shortcut, shortcut.fromStack);
                  break;
               case EVERYTHING:
                  this.dropAll(shortcut, ItemStack.EMPTY);
               }
            case MOVE:
               switch(shortcut.scope) {
               case ONE_ITEM:
                  Slot slot = this.container.getSlot(shortcut.fromSection, shortcut.fromIndex);
                  if (slot.getHasStack()) {
                     int toIndex = this.getNextTargetIndex(shortcut, slot.getStack());
                     this.container.moveSome(shortcut.fromSection, shortcut.fromIndex, shortcut.toSection, toIndex, 1);
                  }
                  break;
               case ONE_STACK:
                  Slot slot = this.container.getSlot(shortcut.fromSection, shortcut.fromIndex);
                  if (slot.getHasStack()) {
                     int toIndex = this.getNextTargetIndex(shortcut, slot.getStack());
                     if (shortcut.fromSection != ContainerSection.CRAFTING_OUT && shortcut.toSection != ContainerSection.ENCHANTMENT) {
                        while(slot.getHasStack() && toIndex != -1) {
                           boolean success = this.container.move(shortcut.fromSection, shortcut.fromIndex, shortcut.toSection, toIndex);
                           int newIndex = this.getNextTargetIndex(shortcut, slot.getStack());
                           toIndex = !success && shortcut.action != ShortcutSpecification.Action.DROP && newIndex == toIndex ? -1 : newIndex;
                        }
                     } else {
                        this.container.move(shortcut.fromSection, shortcut.fromIndex, shortcut.toSection, toIndex);
                     }
                  }
                  break;
               case ALL_ITEMS:
                  this.moveAll(shortcut, shortcut.fromStack);
                  if (shortcut.fromSection == ContainerSection.INVENTORY_NOT_HOTBAR && shortcut.toSection == ContainerSection.CHEST) {
                     shortcut.fromSection = ContainerSection.INVENTORY_HOTBAR;
                     this.moveAll(shortcut, shortcut.fromStack);
                  }
                  break;
               case EVERYTHING:
                  this.moveAll(shortcut, ItemStack.EMPTY);
                  if (shortcut.fromSection == ContainerSection.INVENTORY_HOTBAR && shortcut.toSection == ContainerSection.CHEST) {
                     shortcut.fromSection = ContainerSection.INVENTORY_HOTBAR;
                     this.moveAll(shortcut, ItemStack.EMPTY);
                  }
               }
            }
         }

      }
   }

   private void dropAll(@NotNull InvTweaksHandlerShortcuts.ShortcutConfig shortcut, @NotNull ItemStack stackToMatch) {
      this.container.getSlots(shortcut.fromSection).stream().filter((slot) -> {
         return slot.getHasStack() && (stackToMatch.isEmpty() || areSameItemType(stackToMatch, slot.getStack()));
      }).forEach((slot) -> {
         int fromIndex = this.container.getSlotIndex(getSlotNumber(slot));

         while(slot.getHasStack()) {
            this.container.drop(shortcut.fromSection, fromIndex);
         }

      });
   }

   private void moveAll(@NotNull InvTweaksHandlerShortcuts.ShortcutConfig shortcut, @NotNull ItemStack stackToMatch) {
      int toIndex = Integer.MIN_VALUE;

      for(Slot slot : this.container.getSlots(shortcut.fromSection)) {
         if (slot.getHasStack() && (stackToMatch.isEmpty() || areSameItemType(stackToMatch, slot.getStack()))) {
            int fromIndex = this.container.getSlotIndex(getSlotNumber(slot));
            toIndex = this.getNextTargetIndex(shortcut, slot.getStack());

            while(slot.getHasStack() && toIndex != -1 && (shortcut.fromSection != shortcut.toSection || fromIndex != toIndex)) {
               boolean success = this.container.move(shortcut.fromSection, fromIndex, shortcut.toSection, toIndex);
               int newIndex = this.getNextTargetIndex(shortcut, slot.getStack());
               if (success && newIndex == toIndex && slot.getHasStack()) {
                  throw new RuntimeException("Inventory in invalid sate after move");
               }

               if (!success && newIndex == toIndex && shortcut.action != ShortcutSpecification.Action.DROP) {
                  toIndex = -1;
               } else {
                  toIndex = newIndex;
               }
            }
         }

         if (toIndex == -1) {
            break;
         }
      }

   }

   private int getNextTargetIndex(@NotNull InvTweaksHandlerShortcuts.ShortcutConfig shortcut, @NotNull ItemStack current) {
      if (shortcut.action == ShortcutSpecification.Action.DROP) {
         return -999;
      } else {
         int result = -1;
         if (!shortcut.forceEmptySlot) {
            int i = 0;

            for(Slot slot : this.container.getSlots(shortcut.toSection)) {
               if (slot.getHasStack()) {
                  ItemStack stack = slot.getStack();
                  if (InvTweaksObfuscation.areItemsStackable(current, stack) && stack.getCount() < stack.getMaxStackSize()) {
                     result = i;
                     break;
                  }
               }

               ++i;
            }
         }

         if (result == -1) {
            result = this.container.getFirstEmptyIndex(shortcut.toSection);
         }

         if (result == -1 && shortcut.toSection == ContainerSection.FURNACE_IN) {
            shortcut.toSection = ContainerSection.FURNACE_FUEL;
            result = this.container.getFirstEmptyIndex(shortcut.toSection);
         }

         return result;
      }
   }

   @Nullable
   public InvTweaksShortcutMapping isShortcutDown(InvTweaksShortcutType type) {
      List<InvTweaksShortcutMapping> mappings = (List)this.shortcuts.get(type);
      if (mappings != null) {
         for(InvTweaksShortcutMapping mapping : mappings) {
            if (mapping.isTriggered(this.pressedKeys)) {
               return mapping;
            }
         }
      }

      return null;
   }

   private static class ShortcutConfig {
      @NotNull
      public ShortcutSpecification.Action action;
      @NotNull
      public ShortcutSpecification.Scope scope;
      @Nullable
      public ContainerSection fromSection;
      public int fromIndex;
      @NotNull
      public ItemStack fromStack;
      @Nullable
      public ContainerSection toSection;
      public int toIndex;
      public boolean forceEmptySlot;

      private ShortcutConfig() {
         this.action = ShortcutSpecification.Action.MOVE;
         this.scope = ShortcutSpecification.Scope.ONE_STACK;
         this.fromSection = null;
         this.fromIndex = -1;
         this.fromStack = ItemStack.EMPTY;
         this.toSection = null;
         this.toIndex = -1;
         this.forceEmptySlot = false;
      }
   }
}
