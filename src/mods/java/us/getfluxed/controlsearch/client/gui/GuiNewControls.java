package us.getfluxed.controlsearch.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiNewControls extends GuiControls {
   private static final Options[] OPTIONS_ARR = new Options[]{Options.INVERT_MOUSE, Options.SENSITIVITY, Options.TOUCHSCREEN, Options.AUTO_JUMP};
   private final GuiScreen parentScreen;
   private final GameSettings options;
   private GuiButton buttonReset;
   private GuiTextField search;
   private String lastFilterText = "";
   private boolean conflicts = false;
   private boolean none = false;
   private boolean toggleFreeKeys = false;
   private EnumSortingType sortingType = EnumSortingType.DEFAULT;
   public GuiButton buttonConflict;
   public GuiButton buttonNone;
   public GuiButton buttonSorting;
   public GuiButton buttonToggleKeys;
   public GuiCheckBox boxSearchCategory;
   public GuiCheckBox boxSearchKey;

   public GuiNewControls(GuiScreen screen, GameSettings settings) {
      super(screen, settings);
      this.parentScreen = screen;
      this.options = settings;
   }

   public void initGui() {
      this.conflicts = false;
      this.none = false;
      this.sortingType = EnumSortingType.DEFAULT;
      this.keyBindingList = new GuiNewKeyBindingList(this, this.mc);
      this.buttonList.add(new GuiButton(200, this.width / 2 - 155, this.height - 29, 150, 20, this.translate("gui.done")));
      this.buttonReset = this.addButton(new GuiButton(201, this.width / 2 - 155 + 160, this.height - 29, 155, 20, this.translate("controls.resetAll")));
      this.screenTitle = this.translate("controls.title");
      int i = 0;

      for(Options gamesettings$options : OPTIONS_ARR) {
         if (gamesettings$options.isFloat()) {
            this.buttonList.add(new GuiOptionSlider(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options));
         } else {
            this.buttonList.add(new GuiOptionButton(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options, this.options.getKeyBinding(gamesettings$options)));
         }

         ++i;
      }

      this.search = new GuiTextField(0, this.mc.fontRenderer, this.width / 2 - 154, this.height - 29 - 23, 148, 18);
      this.search.setCanLoseFocus(true);
      this.buttonConflict = new GuiButton(2906, this.width / 2 - 155 + 160, this.height - 29 - 24, 75, 20, this.translate("options.showConflicts"));
      this.buttonNone = new GuiButton(2907, this.width / 2 - 155 + 160 + 80, this.height - 29 - 24, 75, 20, this.translate("options.showNone"));
      this.buttonSorting = new GuiButton(2908, this.width / 2 - 155 + 160 + 80, this.height - 29 - 24 - 24, 75, 20, this.translate("options.sort") + ": " + this.sortingType.getName());
      this.boxSearchCategory = new GuiCheckBox(2909, this.width / 2 - 77 + 20, this.height - 29 - 37, this.translate("options.category"), false);
      this.boxSearchKey = new GuiCheckBox(2910, this.width / 2 - 77 + 20, this.height - 29 - 50, this.translate("options.key"), false);
      this.buttonToggleKeys = new GuiButton(2911, this.width / 2 - 155 + 160, this.height - 29 - 24 - 24, 75, 20, this.translate("options.toggleFree"));
      this.buttonList.add(this.buttonConflict);
      this.buttonList.add(this.buttonNone);
      this.buttonList.add(this.buttonSorting);
      this.buttonList.add(this.boxSearchCategory);
      this.buttonList.add(this.boxSearchKey);
      this.buttonList.add(this.buttonToggleKeys);
   }

   public void updateScreen() {
      this.search.updateCursorCounter();
      if (!this.search.getText().equals(this.lastFilterText)) {
         this.refreshKeys();
      }

   }

   public void refreshKeys() {
      LinkedList<IGuiListEntry> workingList = this.getAllEntries();
      LinkedList<IGuiListEntry> newList = this.getEmptyList();
      if (this.none) {
         LinkedList<IGuiListEntry> unbound = new LinkedList();

         for(IGuiListEntry entry : workingList) {
            if (entry instanceof GuiNewKeyBindingList.KeyEntry) {
               GuiNewKeyBindingList.KeyEntry ent = (GuiNewKeyBindingList.KeyEntry)entry;
               if (ent.getKeybinding().getKeyCode() == 0) {
                  unbound.add(ent);
               }
            }
         }

         workingList = unbound;
      } else if (this.conflicts) {
         LinkedList<IGuiListEntry> conflicts = new LinkedList();

         for(IGuiListEntry entry : workingList) {
            if (entry instanceof GuiNewKeyBindingList.KeyEntry) {
               GuiNewKeyBindingList.KeyEntry ent = (GuiNewKeyBindingList.KeyEntry)entry;
               if (ent.getKeybinding().getKeyCode() != 0) {
                  for(IGuiListEntry entry1 : ((GuiNewKeyBindingList)this.keyBindingList).getListEntriesAll()) {
                     if (!entry.equals(entry1) && entry1 instanceof GuiNewKeyBindingList.KeyEntry) {
                        GuiNewKeyBindingList.KeyEntry ent1 = (GuiNewKeyBindingList.KeyEntry)entry1;
                        if (ent1.getKeybinding().getKeyCode() != 0 && ent.getKeybinding().conflicts(ent1.getKeybinding())) {
                           if (!conflicts.contains(ent)) {
                              conflicts.add(ent);
                           }

                           if (!conflicts.contains(ent1)) {
                              conflicts.add(ent1);
                           }
                        }
                     }
                  }
               }
            }
         }

         workingList = conflicts;
      }

      boolean searched = false;
      if (!this.search.getText().isEmpty()) {
         for(IGuiListEntry entry : workingList) {
            if (this.isKeyEntry(entry)) {
               GuiNewKeyBindingList.KeyEntry ent = (GuiNewKeyBindingList.KeyEntry)entry;
               String compareStr = this.translate(ent.getKeybinding().getKeyDescription()).toLowerCase();
               if (this.boxSearchCategory.isChecked()) {
                  compareStr = this.translate(ent.getKeybinding().getKeyCategory()).toLowerCase();
               }

               if (this.boxSearchKey.isChecked()) {
                  compareStr = this.translate(ent.getKeybinding().getDisplayName()).toLowerCase();
               }

               if (compareStr.contains(this.search.getText().toLowerCase())) {
                  newList.add(entry);
               }
            }
         }

         searched = true;
      }

      this.lastFilterText = this.search.getText();
      if (!searched) {
         newList = workingList;
      }

      newList = this.sort(newList, this.sortingType);
      this.setEntries(newList);
   }

   public LinkedList<IGuiListEntry> sort(LinkedList<IGuiListEntry> list, EnumSortingType type) {
      if (this.sortingType != EnumSortingType.DEFAULT) {
         LinkedList<IGuiListEntry> filteredList = this.getEmptyList();

         for(IGuiListEntry entry : list) {
            if (this.isKeyEntry(entry)) {
               filteredList.add(entry);
            }
         }

         filteredList.sort((o1, o2) -> {
            if (o1 instanceof GuiNewKeyBindingList.KeyEntry && o2 instanceof GuiNewKeyBindingList.KeyEntry) {
               GuiNewKeyBindingList.KeyEntry ent1 = (GuiNewKeyBindingList.KeyEntry)o1;
               GuiNewKeyBindingList.KeyEntry ent2 = (GuiNewKeyBindingList.KeyEntry)o2;
               if (type == EnumSortingType.AZ) {
                  return this.translate(ent1.getKeybinding().getKeyDescription()).compareTo(this.translate(ent2.getKeybinding().getKeyDescription()));
               }

               if (type == EnumSortingType.ZA) {
                  return this.translate(ent2.getKeybinding().getKeyDescription()).compareTo(this.translate(ent1.getKeybinding().getKeyDescription()));
               }
            }

            return -1;
         });
         return filteredList;
      } else {
         return list;
      }
   }

   public LinkedList<IGuiListEntry> getAllEntries() {
      return ((GuiNewKeyBindingList)this.keyBindingList).getListEntriesAll();
   }

   public LinkedList<IGuiListEntry> getEmptyList() {
      return new LinkedList();
   }

   public void setEntries(LinkedList<IGuiListEntry> entries) {
      ((GuiNewKeyBindingList)this.keyBindingList).setListEntries(entries);
   }

   public boolean isKeyEntry(IGuiListEntry entry) {
      return entry instanceof GuiNewKeyBindingList.KeyEntry;
   }

   public String translate(String text) {
      return I18n.format(text, new Object[0]);
   }

   public void handleMouseInput() throws IOException {
      this.superSuperHandleMouseInput();
      this.keyBindingList.handleMouseInput();
   }

   public void superSuperHandleMouseInput() {
      int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      int k = Mouse.getEventButton();
      if (Mouse.getEventButtonState()) {
         if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0) {
            return;
         }

         this.eventButton = k;
         this.lastMouseEvent = Minecraft.getSystemTime();
         this.mouseClicked(i, j, this.eventButton);
      } else if (k != -1) {
         if (this.mc.gameSettings.touchscreen && --this.touchValue > 0) {
            return;
         }

         this.eventButton = -1;
         this.mouseReleased(i, j, k);
      } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
         long l = Minecraft.getSystemTime() - this.lastMouseEvent;
         this.mouseClickMove(i, j, this.eventButton, l);
      }

   }

   protected void actionPerformed(GuiButton button) {
      if (button.id == 200) {
         this.mc.displayGuiScreen(this.parentScreen);
      } else if (button.id == 201) {
         for(KeyBinding keybinding : this.mc.gameSettings.keyBindings) {
            keybinding.setToDefault();
         }

         KeyBinding.resetKeyBindingArrayAndHash();
         this.toggleFreeKeys = false;
      } else if (button.id < 100 && button instanceof GuiOptionButton) {
         this.options.setOptionValue(((GuiOptionButton)button).getOption(), 1);
         button.displayString = this.options.getKeyBinding(Options.byOrdinal(button.id));
      } else if (button.id == 2906) {
         this.toggleFreeKeys = false;
         this.none = false;
         this.buttonNone.displayString = this.translate("options.showNone");
         if (!this.conflicts) {
            this.conflicts = true;
            this.buttonConflict.displayString = this.translate("options.showAll");
            this.refreshKeys();
         } else {
            this.conflicts = false;
            this.buttonConflict.displayString = this.translate("options.showConflicts");
            this.refreshKeys();
         }
      } else if (button.id == 2907) {
         this.toggleFreeKeys = false;
         this.conflicts = false;
         this.buttonConflict.displayString = this.translate("options.showConflicts");
         if (!this.none) {
            this.none = true;
            this.buttonNone.displayString = this.translate("options.showAll");
            this.refreshKeys();
         } else {
            this.none = false;
            this.buttonNone.displayString = this.translate("options.showNone");
            this.refreshKeys();
         }
      } else if (button.id == 2908) {
         this.toggleFreeKeys = false;
         this.sortingType = this.sortingType.cycle();
         this.buttonSorting.displayString = this.translate("options.sort") + ": " + this.sortingType.getName();
         this.refreshKeys();
      } else if (button.id == 2909) {
         this.toggleFreeKeys = false;
         this.boxSearchKey.setIsChecked(false);
         this.refreshKeys();
      } else if (button.id == 2910) {
         this.toggleFreeKeys = false;
         this.boxSearchCategory.setIsChecked(false);
         this.refreshKeys();
      } else if (button.id == 2911) {
         this.buttonToggleKeys.displayString = this.translate("options.toggleFree");
         this.toggleFreeKeys = !this.toggleFreeKeys;
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (this.buttonId != null) {
         this.buttonId.setKeyModifierAndCode(KeyModifier.getActiveModifier(), -100 + mouseButton);
         this.options.setOptionKeyBinding(this.buttonId, -100 + mouseButton);
         this.buttonId = null;
         KeyBinding.resetKeyBindingArrayAndHash();
      } else if (mouseButton != 0 || !this.keyBindingList.mouseClicked(mouseX, mouseY, mouseButton)) {
         this.superSuperMouseClicked(mouseX, mouseY, mouseButton);
      }

      this.search.mouseClicked(mouseX, mouseY, mouseButton);
      if (mouseButton == 1 && mouseX >= this.search.x && mouseX < this.search.x + this.search.width && mouseY >= this.search.y && mouseY < this.search.y + this.search.height) {
         this.search.setText("");
      }

   }

   protected void superSuperMouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0) {
         for(int i = 0; i < this.buttonList.size(); ++i) {
            GuiButton guibutton = (GuiButton)this.buttonList.get(i);
            if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
               Pre event = new Pre(this, guibutton, this.buttonList);
               if (MinecraftForge.EVENT_BUS.post(event)) {
                  break;
               }

               guibutton = event.getButton();
               this.selectedButton = guibutton;
               guibutton.playPressSound(this.mc.getSoundHandler());
               this.actionPerformed(guibutton);
               if (this.equals(this.mc.currentScreen)) {
                  MinecraftForge.EVENT_BUS.post(new Post(this, event.getButton(), this.buttonList));
               }
            }
         }
      }

   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      if (state != 0 || !this.keyBindingList.mouseReleased(mouseX, mouseY, state)) {
         this.superSuperMouseReleased(mouseX, mouseY, state);
      }

   }

   protected void superSuperMouseReleased(int mouseX, int mouseY, int state) {
      if (this.selectedButton != null && state == 0) {
         this.selectedButton.mouseReleased(mouseX, mouseY);
         this.selectedButton = null;
      }

   }

   protected void keyTyped(char typedChar, int keyCode) {
      if (this.buttonId != null) {
         if (keyCode == 1) {
            this.buttonId.setKeyModifierAndCode(KeyModifier.NONE, 0);
            this.options.setOptionKeyBinding(this.buttonId, 0);
         } else if (keyCode != 0) {
            this.buttonId.setKeyModifierAndCode(KeyModifier.getActiveModifier(), keyCode);
            this.options.setOptionKeyBinding(this.buttonId, keyCode);
         } else if (typedChar > 0) {
            this.buttonId.setKeyModifierAndCode(KeyModifier.getActiveModifier(), typedChar + 256);
            this.options.setOptionKeyBinding(this.buttonId, typedChar + 256);
         }

         if (!KeyModifier.isKeyCodeModifier(keyCode)) {
            this.buttonId = null;
         }

         this.time = Minecraft.getSystemTime();
         KeyBinding.resetKeyBindingArrayAndHash();
      } else if (this.search.isFocused()) {
         this.search.textboxKeyTyped(typedChar, keyCode);
      } else {
         this.superSuperKeyTyped(typedChar, keyCode);
      }

   }

   protected void superSuperKeyTyped(char typedChar, int keyCode) {
      if (keyCode == 1) {
         this.mc.displayGuiScreen((GuiScreen)null);
         if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
         }
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 8, 16777215);
      this.drawCenteredString(this.fontRenderer, this.translate("options.search"), this.width / 2 - 77, this.height - 29 - 39, 16777215);
      boolean flag = false;

      for(KeyBinding keybinding : this.options.keyBindings) {
         if (!keybinding.isSetToDefaultValue()) {
            flag = true;
            break;
         }
      }

      this.buttonReset.enabled = flag;
      this.superSuperDrawScreen(mouseX, mouseY, partialTicks);
      this.search.drawTextBox();
      if (this.toggleFreeKeys) {
         drawRect(this.keyBindingList.left, this.keyBindingList.top, this.keyBindingList.right, this.keyBindingList.bottom, -16777216);
         LinkedList<Integer> keyCodes = new LinkedList();

         for(int i = 2; i < 256; ++i) {
            keyCodes.add(Integer.valueOf(i));
         }

         keyCodes.add(Integer.valueOf(-98));
         keyCodes.add(Integer.valueOf(-99));
         keyCodes.add(Integer.valueOf(-100));
         List<Integer> removed = new ArrayList();
         ((GuiNewKeyBindingList)this.keyBindingList).getListEntriesAll().forEach((ix) -> {
            if (i instanceof GuiNewKeyBindingList.KeyEntry) {
               removed.add(Integer.valueOf(((GuiNewKeyBindingList.KeyEntry)i).getKeybinding().getKeyCode()));
            }

         });
         int[] rem = new int[]{219, 100, 101, 102, 103, 104, 105, 112, 113, 41, 121, 87, 123, 125, 141, 144, 146, 147, 148, 149, 150, 151, 156, 167, 179, 197, 145, 196, 218};

         for(int i : rem) {
            removed.add(Integer.valueOf(i));
         }

         keyCodes.forEach((ix) -> {
            if (i.intValue() >= 0 && (Keyboard.getKeyName(i.intValue()) == null || Keyboard.getKeyName(i.intValue()).isEmpty())) {
               removed.add(i);
            }

         });
         keyCodes.removeAll(removed);
         Collections.sort(keyCodes);
         int[] x = new int[]{0};
         int[] y = new int[]{0};
         int[] count = new int[]{0};
         this.fontRenderer.drawString(this.translate("options.availableKeys") + ":", this.width / 2, this.keyBindingList.top + 2, 16777215);
         keyCodes.forEach((key) -> {
            if (key.intValue() >= 0) {
               FontRenderer var10000 = this.fontRenderer;
               String var10001 = Keyboard.getKeyName(key.intValue());
               int var10002 = this.keyBindingList.left + x[0] * 65;
               int var10003 = this.keyBindingList.top + 12;
               int var10007 = y[0];
               int var10004 = y[0];
               y[0] = var10007 + 1;
               var10000.drawString(var10001, var10002, var10003 + var10004 * this.fontRenderer.FONT_HEIGHT, 16733695);
            } else {
               switch(key.intValue() + 100) {
               case 0:
                  int var7 = this.keyBindingList.left + x[0] * 65;
                  int var10 = this.keyBindingList.top + 12;
                  int var16 = y[0];
                  int var13 = y[0];
                  y[0] = var16 + 1;
                  this.fontRenderer.drawString("Button 1", var7, var10 + var13 * this.fontRenderer.FONT_HEIGHT, 5635925);
                  break;
               case 1:
                  int var6 = this.keyBindingList.left + x[0] * 65;
                  int var9 = this.keyBindingList.top + 12;
                  int var15 = y[0];
                  int var12 = y[0];
                  y[0] = var15 + 1;
                  this.fontRenderer.drawString("Button 2", var6, var9 + var12 * this.fontRenderer.FONT_HEIGHT, 5635925);
                  break;
               case 2:
                  int var5 = this.keyBindingList.left + x[0] * 65;
                  int var8 = this.keyBindingList.top + 12;
                  int var14 = y[0];
                  int var11 = y[0];
                  y[0] = var14 + 1;
                  this.fontRenderer.drawString("Button 3", var5, var8 + var11 * this.fontRenderer.FONT_HEIGHT, 5635925);
               }
            }

            ++count[0];
            if (count[0] > this.keyBindingList.height / 30) {
               count[0] = 0;
               ++x[0];
               y[0] = 0;
            }

         });
      }

   }

   public void superSuperDrawScreen(int mouseX, int mouseY, float partialTicks) {
      for(GuiButton aButtonList : this.buttonList) {
         aButtonList.drawButton(this.mc, mouseX, mouseY, partialTicks);
      }

      for(GuiLabel aLabelList : this.labelList) {
         aLabelList.drawLabel(this.mc, mouseX, mouseY);
      }

   }
}
