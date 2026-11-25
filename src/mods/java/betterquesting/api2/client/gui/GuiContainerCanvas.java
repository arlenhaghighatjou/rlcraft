package betterquesting.api2.client.gui;

import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.storage.BQ_Settings;
import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.misc.ComparatorGuiDepth;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.popups.PopChoice;
import betterquesting.api2.client.gui.themes.presets.PresetIcon;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.client.BQ_Keybindings;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiContainerCanvas extends GuiContainer implements IScene {
   private final List<IGuiPanel> guiPanels = new CopyOnWriteArrayList();
   private final GuiRectangle rootTransform = new GuiRectangle(0, 0, 0, 0, 0);
   private final GuiTransform transform = new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(16, 16, 16, 16), 0);
   private boolean enabled = true;
   private boolean useMargins = true;
   private boolean useDefaultBG = false;
   private boolean isVolatile = false;
   public final GuiScreen parent;
   private IGuiPanel popup = null;
   private boolean[] mBtnState = new boolean[3];

   public GuiContainerCanvas(GuiScreen parent, Container container) {
      super(container);
      this.parent = parent;
   }

   public void openPopup(@Nonnull IGuiPanel panel) {
      this.popup = panel;
   }

   public void closePopup() {
      this.popup = null;
   }

   public IGuiRect getTransform() {
      return this.transform;
   }

   @Nonnull
   public List<IGuiPanel> getChildren() {
      return this.guiPanels;
   }

   public GuiContainerCanvas useMargins(boolean enable) {
      this.useMargins = enable;
      return this;
   }

   public GuiContainerCanvas useDefaultBG(boolean enable) {
      this.useDefaultBG = enable;
      return this;
   }

   public GuiContainerCanvas setVolatile(boolean state) {
      this.isVolatile = state;
      return this;
   }

   public final void initGui() {
      super.initGui();
      this.guiLeft = 0;
      this.guiTop = 0;
      this.xSize = this.width;
      this.ySize = this.height;
      this.initPanel();
   }

   public void onGuiClosed() {
      super.onGuiClosed();
      Keyboard.enableRepeatEvents(false);
   }

   public void initPanel() {
      this.rootTransform.w = this.width;
      this.rootTransform.h = this.height;
      this.transform.setParent(this.rootTransform);
      if (this.useMargins) {
         int marginX = BQ_Settings.guiWidth <= 0 ? 16 : Math.max(16, (this.width - BQ_Settings.guiWidth) / 2);
         int marginY = BQ_Settings.guiHeight <= 0 ? 16 : Math.max(16, (this.height - BQ_Settings.guiHeight) / 2);
         this.transform.getPadding().setPadding(marginX, marginY, marginX, marginY);
      } else {
         this.transform.getPadding().setPadding(0, 0, 0, 0);
      }

      this.guiPanels.clear();
   }

   public void setEnabled(boolean state) {
      this.enabled = state;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   protected void drawGuiContainerBackgroundLayer(float partialTick, int mx, int my) {
      if (this.useDefaultBG) {
         this.drawDefaultBackground();
      }

      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableDepth();
      this.drawPanel(mx, my, partialTick);
      GlStateManager.enableDepth();
      GlStateManager.popMatrix();
   }

   public void drawScreen(int mx, int my, float partialTick) {
      super.drawScreen(mx, my, partialTick);
      List<String> tt = this.getTooltip(mx, my);
      if (tt != null && tt.size() > 0) {
         this.drawHoveringText(tt, mx, my);
      }

   }

   /** @deprecated */
   @Deprecated
   public void actionPerformed(GuiButton button) {
   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      int k = Mouse.getEventButton();
      int SDX = (int)(-Math.signum((float)Mouse.getEventDWheel()));
      boolean flag = Mouse.getEventButtonState();
      if (k >= 0 && k < 3 && this.mBtnState[k] != flag) {
         if (flag) {
            this.onMouseClick(i, j, k);
         } else {
            this.onMouseRelease(i, j, k);
         }

         this.mBtnState[k] = flag;
      }

      if (SDX != 0) {
         this.onMouseScroll(i, j, SDX);
      }

   }

   public void keyTyped(char c, int keyCode) {
      if (keyCode != 1) {
         this.onKeyTyped(c, keyCode);
      } else {
         if (!this.isVolatile && !(this instanceof IVolatileScreen)) {
            this.mc.displayGuiScreen((GuiScreen)null);
            if (this.mc.currentScreen == null) {
               this.mc.setIngameFocus();
            }
         } else {
            this.openPopup(new PopChoice(QuestTranslation.translate("betterquesting.gui.closing_warning") + "\n\n" + QuestTranslation.translate("betterquesting.gui.closing_confirm"), PresetIcon.ICON_NOTICE.getTexture(), this::confirmClose, new String[]{QuestTranslation.translate("gui.yes"), QuestTranslation.translate("gui.no")}));
         }

      }
   }

   public void drawPanel(int mx, int my, float partialTick) {
      for(IGuiPanel entry : this.guiPanels) {
         if (entry.isEnabled()) {
            entry.drawPanel(mx, my, partialTick);
         }
      }

      if (this.popup != null && this.popup.isEnabled()) {
         this.popup.drawPanel(mx, my, partialTick);
      }

   }

   public boolean onMouseClick(int mx, int my, int click) {
      boolean used = false;
      if (this.popup != null && this.popup.isEnabled()) {
         this.popup.onMouseClick(mx, my, click);
         return true;
      } else {
         ListIterator<IGuiPanel> pnIter = this.guiPanels.listIterator(this.guiPanels.size());

         while(pnIter.hasPrevious()) {
            IGuiPanel entry = (IGuiPanel)pnIter.previous();
            if (entry.isEnabled() && entry.onMouseClick(mx, my, click)) {
               used = true;
               break;
            }
         }

         return used;
      }
   }

   public boolean onMouseRelease(int mx, int my, int click) {
      boolean used = false;
      if (this.popup != null && this.popup.isEnabled()) {
         this.popup.onMouseRelease(mx, my, click);
         return true;
      } else {
         ListIterator<IGuiPanel> pnIter = this.guiPanels.listIterator(this.guiPanels.size());

         while(pnIter.hasPrevious()) {
            IGuiPanel entry = (IGuiPanel)pnIter.previous();
            if (entry.isEnabled() && entry.onMouseRelease(mx, my, click)) {
               used = true;
               break;
            }
         }

         return used;
      }
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      boolean used = false;
      if (this.popup != null && this.popup.isEnabled()) {
         this.popup.onMouseScroll(mx, my, scroll);
         return true;
      } else {
         ListIterator<IGuiPanel> pnIter = this.guiPanels.listIterator(this.guiPanels.size());

         while(pnIter.hasPrevious()) {
            IGuiPanel entry = (IGuiPanel)pnIter.previous();
            if (entry.isEnabled() && entry.onMouseScroll(mx, my, scroll)) {
               used = true;
               break;
            }
         }

         return used;
      }
   }

   public boolean onKeyTyped(char c, int keycode) {
      boolean used = false;
      if (this.popup != null && this.popup.isEnabled()) {
         this.popup.onKeyTyped(c, keycode);
         return true;
      } else {
         ListIterator<IGuiPanel> pnIter = this.guiPanels.listIterator(this.guiPanels.size());

         while(pnIter.hasPrevious()) {
            IGuiPanel entry = (IGuiPanel)pnIter.previous();
            if (entry.isEnabled() && entry.onKeyTyped(c, keycode)) {
               used = true;
               break;
            }
         }

         if (!used && (BQ_Keybindings.openQuests.getKeyCode() == keycode || this.mc.gameSettings.keyBindInventory.getKeyCode() == keycode)) {
            if (!this.isVolatile && !(this instanceof IVolatileScreen)) {
               this.mc.displayGuiScreen((GuiScreen)null);
               if (this.mc.currentScreen == null) {
                  this.mc.setIngameFocus();
               }
            } else {
               this.openPopup(new PopChoice(QuestTranslation.translate("betterquesting.gui.closing_warning") + "\n\n" + QuestTranslation.translate("betterquesting.gui.closing_confirm"), PresetIcon.ICON_NOTICE.getTexture(), this::confirmClose, new String[]{QuestTranslation.translate("gui.yes"), QuestTranslation.translate("gui.no")}));
            }
         }

         return used;
      }
   }

   public List<String> getTooltip(int mx, int my) {
      ListIterator<IGuiPanel> pnIter = this.guiPanels.listIterator(this.guiPanels.size());
      List<String> tt = null;
      if (this.popup != null && this.popup.isEnabled()) {
         tt = this.popup.getTooltip(mx, my);
         if (tt != null) {
            return tt;
         }
      }

      while(pnIter.hasPrevious()) {
         IGuiPanel entry = (IGuiPanel)pnIter.previous();
         if (entry.isEnabled()) {
            tt = entry.getTooltip(mx, my);
            if (tt != null && tt.size() > 0) {
               return tt;
            }
         }
      }

      if (tt == null) {
         for(Slot slot : this.inventorySlots.inventorySlots) {
            if (slot.isEnabled() && slot.getHasStack() && this.isPointInRegion(slot.xPos, slot.yPos, 16, 16, mx, my)) {
               tt = slot.getStack().getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL);
               return tt.size() <= 0 ? null : tt;
            }
         }
      }

      return null;
   }

   public void addPanel(IGuiPanel panel) {
      if (panel != null && !this.guiPanels.contains(panel)) {
         this.guiPanels.add(panel);
         this.guiPanels.sort(ComparatorGuiDepth.INSTANCE);
         panel.getTransform().setParent(this.getTransform());
         panel.initPanel();
      }
   }

   public boolean removePanel(IGuiPanel panel) {
      return this.guiPanels.remove(panel);
   }

   public void resetCanvas() {
      this.guiPanels.clear();
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public <T extends GuiButton> T addButton(T button) {
      return (T)super.addButton(button);
   }

   protected void renderToolTip(ItemStack stack, int x, int y) {
      FontRenderer font = stack.getItem().getFontRenderer(stack);
      RenderUtils.drawHoveringText(stack, this.getItemToolTip(stack), x, y, this.width, this.height, -1, font == null ? this.fontRenderer : font);
   }

   protected void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {
      RenderUtils.drawHoveringText(textLines, x, y, this.width, this.height, -1, font);
   }

   public void confirmClose(int id) {
      if (id == 0) {
         this.mc.displayGuiScreen((GuiScreen)null);
         if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
         }
      }

   }
}
