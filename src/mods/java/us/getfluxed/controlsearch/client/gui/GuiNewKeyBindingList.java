package us.getfluxed.controlsearch.client.gui;

import java.util.Arrays;
import java.util.LinkedList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

@SideOnly(Side.CLIENT)
public class GuiNewKeyBindingList extends GuiKeyBindingList {
   private final GuiNewControls controlsScreen;
   private final Minecraft mc;
   private LinkedList<IGuiListEntry> listEntries;
   private LinkedList<IGuiListEntry> listEntriesAll;
   private int maxListLabelWidth;
   private static int wrapped = 0;
   private static int fill = 0;

   public GuiNewKeyBindingList(GuiNewControls controls, Minecraft mcIn) {
      super(controls, mcIn);
      this.controlsScreen = controls;
      this.mc = mcIn;
      this.width = controls.width + 45;
      this.height = controls.height + 80;
      this.top = 63;
      this.bottom = controls.height - 80;
      KeyBinding[] akeybinding = (KeyBinding[])ArrayUtils.clone(mcIn.gameSettings.keyBindings);
      this.listEntries = new LinkedList();
      this.listEntriesAll = new LinkedList();
      Arrays.sort(akeybinding);
      int i = 0;
      String s = null;

      for(KeyBinding keybinding : akeybinding) {
         String s1 = keybinding.getKeyCategory();
         if (!s1.equals(s)) {
            s = s1;
            if (!s1.endsWith(".hidden")) {
               this.listEntries.add(new GuiNewKeyBindingList.CategoryEntry(s1));
               this.listEntriesAll.add(new GuiNewKeyBindingList.CategoryEntry(s1));
            }
         }

         int j = mcIn.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription(), new Object[0]));
         if (j > this.maxListLabelWidth) {
            this.maxListLabelWidth = j;
         }

         if (!s1.endsWith(".hidden")) {
            this.listEntries.add(new GuiNewKeyBindingList.KeyEntry(keybinding));
            this.listEntriesAll.add(new GuiNewKeyBindingList.KeyEntry(keybinding));
         }
      }

   }

   protected void drawSelectionBox(int insideLeft, int insideTop, int mouseXIn, int mouseYIn, float partialTicks) {
      int i = this.getSize();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      fill = 0;

      for(int j = 0; j < i; ++j) {
         int k = insideTop + (j + fill) * this.slotHeight + this.headerPadding;
         int l = this.slotHeight - 4;
         if (k > this.bottom || k + l < this.top) {
            this.updateItemPos(j, insideLeft, k, partialTicks);
         }

         if (this.showSelectionBox && this.isSelected(j)) {
            int i1 = this.left + (this.width / 2 - this.getListWidth() / 2);
            int j1 = this.left + this.width / 2 + this.getListWidth() / 2;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableTexture2D();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)i1, (double)(k + l + 2), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)j1, (double)(k + l + 2), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)j1, (double)(k - 2), 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)i1, (double)(k - 2), 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos((double)(i1 + 1), (double)(k + l + 1), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)(j1 - 1), (double)(k + l + 1), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)(j1 - 1), (double)(k - 1), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)(i1 + 1), (double)(k - 1), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
         }

         this.drawSlot(j, insideLeft, k, l, mouseXIn, mouseYIn, partialTicks);
         fill += wrapped;
      }

   }

   protected int getSize() {
      return this.listEntries.size();
   }

   protected int getContentHeight() {
      return (this.getSize() + fill) * this.slotHeight + this.headerPadding;
   }

   public IGuiListEntry getListEntry(int index) {
      return (IGuiListEntry)this.listEntries.get(index);
   }

   protected int getScrollBarX() {
      return this.mc.currentScreen.width - 20;
   }

   public int getListWidth() {
      return super.getListWidth() + 32;
   }

   public LinkedList<IGuiListEntry> getListEntries() {
      return this.listEntries;
   }

   public LinkedList<IGuiListEntry> getListEntriesAll() {
      return this.listEntriesAll;
   }

   public void setListEntries(LinkedList<IGuiListEntry> listEntries) {
      this.listEntries = listEntries;
   }

   @SideOnly(Side.CLIENT)
   public class CategoryEntry implements IGuiListEntry {
      public final String labelText;
      private final int labelWidth;

      public CategoryEntry(String name) {
         this.labelText = I18n.format(name, new Object[0]);
         this.labelWidth = GuiNewKeyBindingList.this.mc.fontRenderer.getStringWidth(this.labelText);
      }

      public void updatePosition(int i, int i1, int i2, float v) {
      }

      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float p_192634_9_) {
         GuiNewKeyBindingList.this.mc.fontRenderer.drawString(this.labelText, GuiNewKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2, y + slotHeight - GuiNewKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT - 1, 16777215);
      }

      public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
         return false;
      }

      public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
      }
   }

   @SideOnly(Side.CLIENT)
   public class KeyEntry implements IGuiListEntry {
      private final KeyBinding keybinding;
      private final String keyDesc;
      private final GuiButton btnChangeKeyBinding;
      private final GuiButton btnReset;

      private KeyEntry(KeyBinding name) {
         this.keybinding = name;
         this.keyDesc = I18n.format(name.getKeyDescription(), new Object[0]);
         this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 95, 20, I18n.format(name.getKeyDescription(), new Object[0]));
         this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset", new Object[0]));
      }

      public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {
      }

      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float p_192634_9_) {
         boolean flag = GuiNewKeyBindingList.this.controlsScreen.buttonId == this.keybinding;
         int halfScreen = GuiNewKeyBindingList.this.mc.currentScreen.width / 2;
         GuiNewKeyBindingList.this.mc.fontRenderer.drawSplitString(this.keyDesc, Math.max(x + 90 - GuiNewKeyBindingList.this.maxListLabelWidth, 20), y + slotHeight / 2 - GuiNewKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT / 2, halfScreen - 20, 16777215);
         GuiNewKeyBindingList.wrapped = Math.max(GuiNewKeyBindingList.this.mc.fontRenderer.listFormattedStringToWidth(this.keyDesc, halfScreen - 20).size() / 2 - 1, 0);
         this.btnReset.visible = !this.keybinding.isSetToDefaultValue();
         this.btnReset.x = halfScreen + 110;
         this.btnReset.y = y;
         this.btnReset.enabled = !this.keybinding.isSetToDefaultValue();
         this.btnChangeKeyBinding.x = halfScreen + 5;
         this.btnChangeKeyBinding.y = y;
         this.btnChangeKeyBinding.displayString = this.keybinding.getDisplayName();
         boolean flag1 = false;
         boolean keyCodeModifierConflict = true;
         if (this.keybinding.getKeyCode() != 0) {
            for(KeyBinding keybinding : GuiNewKeyBindingList.this.mc.gameSettings.keyBindings) {
               if (keybinding != this.keybinding && keybinding.conflicts(this.keybinding)) {
                  flag1 = true;
                  keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(this.keybinding);
               }
            }
         }

         if (flag) {
            this.btnChangeKeyBinding.displayString = TextFormatting.WHITE + "> " + TextFormatting.YELLOW + this.btnChangeKeyBinding.displayString + TextFormatting.WHITE + " <";
         } else if (flag1) {
            this.btnChangeKeyBinding.displayString = (keyCodeModifierConflict ? TextFormatting.GOLD : TextFormatting.RED) + this.btnChangeKeyBinding.displayString;
         }

         this.btnChangeKeyBinding.drawButton(GuiNewKeyBindingList.this.mc, mouseX, mouseY, p_192634_9_);
         this.btnReset.drawButton(GuiNewKeyBindingList.this.mc, mouseX, mouseY, p_192634_9_);
         if (mouseY >= y && mouseY <= y + slotHeight * GuiNewKeyBindingList.wrapped) {
            GuiNewKeyBindingList.this.mc.fontRenderer.drawString(I18n.format(this.keybinding.getKeyCategory(), new Object[0]), mouseX + 10, mouseY, 16777215);
         }

      }

      public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
         if (this.btnChangeKeyBinding.mousePressed(GuiNewKeyBindingList.this.mc, mouseX, mouseY)) {
            GuiNewKeyBindingList.this.controlsScreen.buttonId = this.keybinding;
            return true;
         } else if (this.btnReset.mousePressed(GuiNewKeyBindingList.this.mc, mouseX, mouseY)) {
            this.keybinding.setToDefault();
            GuiNewKeyBindingList.this.mc.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
            KeyBinding.resetKeyBindingArrayAndHash();
            return true;
         } else {
            return false;
         }
      }

      public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
         this.btnChangeKeyBinding.mouseReleased(x, y);
         this.btnReset.mouseReleased(x, y);
      }

      public KeyBinding getKeybinding() {
         return this.keybinding;
      }
   }
}
