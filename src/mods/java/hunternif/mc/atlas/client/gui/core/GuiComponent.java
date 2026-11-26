package hunternif.mc.atlas.client.gui.core;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;

@SideOnly(Side.CLIENT)
public class GuiComponent extends GuiScreen {
   private GuiComponent parent = null;
   private final List<GuiComponent> children = new CopyOnWriteArrayList();
   int properWidth;
   int properHeight;
   int contentWidth;
   int contentHeight;
   private boolean sizeIsInvalid = false;
   private boolean isClipped = false;
   protected boolean isMouseOver = false;
   private boolean interceptsMouse = true;
   private boolean interceptsKeyboard = true;
   private boolean hasHandledKeyboard = false;
   private boolean hasHandledMouse = false;
   private boolean blocksScreen = false;
   private int guiX = 0;
   private int guiY = 0;
   private final GuiComponent.HoveringTextInfo hoveringTextInfo = new GuiComponent.HoveringTextInfo();

   public GuiComponent() {
   }

   public void setGuiCoords(int x, int y) {
      int dx = x - this.guiX;
      int dy = y - this.guiY;
      this.guiX = x;
      this.guiY = y;

      for(GuiComponent child : this.children) {
         child.offsetGuiCoords(dx, dy);
      }

      if (this.parent != null && (dx != 0 || dy != 0)) {
         this.parent.invalidateSize();
      }

   }

   public final void setRelativeCoords(int x, int y) {
      if (this.parent != null) {
         this.setGuiCoords(this.parent.getGuiX() + x, this.parent.getGuiY() + y);
      } else {
         this.setGuiCoords(x, y);
      }

   }

   public final void setRelativeX(int x) {
      if (this.parent != null) {
         this.setGuiCoords(this.parent.getGuiX() + x, this.guiY);
      } else {
         this.setGuiCoords(x, this.guiY);
      }

   }

   public final void setRelativeY(int y) {
      if (this.parent != null) {
         this.setGuiCoords(this.guiX, this.parent.getGuiY() + y);
      } else {
         this.setGuiCoords(this.guiX, y);
      }

   }

   public final void offsetGuiCoords(int dx, int dy) {
      this.setGuiCoords(this.guiX + dx, this.guiY + dy);
   }

   protected final void setCentered() {
      this.validateSize();
      if (this.parent == null) {
         this.setGuiCoords((this.width - this.getWidth()) / 2, (this.height - this.getHeight()) / 2);
      } else {
         this.setRelativeCoords((this.parent.getWidth() - this.getWidth()) / 2, (this.parent.getHeight() - this.getHeight()) / 2);
      }

   }

   public int getGuiX() {
      return this.guiX;
   }

   public int getGuiY() {
      return this.guiY;
   }

   int getRelativeX() {
      return this.parent == null ? this.guiX : this.guiX - this.parent.guiX;
   }

   int getRelativeY() {
      return this.parent == null ? this.guiY : this.guiY - this.parent.guiY;
   }

   protected void setSize(int width, int height) {
      this.properWidth = width;
      this.properHeight = height;
      this.contentWidth = width;
      this.contentHeight = height;
      this.invalidateSize();
   }

   protected GuiComponent addChild(GuiComponent child) {
      this.doAddChild((GuiComponent)null, child, (GuiComponent)null);
      return child;
   }

   public GuiComponent addChildInfrontOf(GuiComponent inFrontOf, GuiComponent child) {
      this.doAddChild(inFrontOf, child, (GuiComponent)null);
      return child;
   }

   protected GuiComponent addChildBehind(GuiComponent behind, GuiComponent child) {
      this.doAddChild((GuiComponent)null, child, behind);
      return child;
   }

   private void doAddChild(GuiComponent inFrontOf, GuiComponent child, GuiComponent behind) {
      if (child != null && !this.children.contains(child) && this.parent != child) {
         int i = this.children.indexOf(inFrontOf);
         if (i == -1) {
            int j = this.children.indexOf(behind);
            if (j == -1) {
               this.children.add(child);
            } else {
               this.children.add(j, child);
            }
         } else {
            this.children.add(i + 1, child);
         }

         child.parent = this;
         child.setGuiCoords(this.guiX, this.guiY);
         if (this.mc != null) {
            child.setWorldAndResolution(this.mc, this.width, this.height);
         }

         this.invalidateSize();
      }
   }

   protected GuiComponent removeChild(GuiComponent child) {
      if (child != null && this.children.contains(child)) {
         child.parent = null;
         this.children.remove(child);
         this.invalidateSize();
         this.onChildClosed(child);
      }

      return child;
   }

   void removeAllChildren() {
      this.children.clear();
      this.invalidateSize();
   }

   public GuiComponent getParent() {
      return this.parent;
   }

   List<GuiComponent> getChildren() {
      return this.children;
   }

   public void setInterceptMouse(boolean value) {
      this.interceptsMouse = value;
      this.allowUserInput = !this.interceptsMouse | !this.interceptsKeyboard;
   }

   protected void setInterceptKeyboard(boolean value) {
      this.interceptsKeyboard = value;
      this.allowUserInput = !this.interceptsMouse | !this.interceptsKeyboard;
   }

   public void handleInput() throws IOException {
      ListIterator<GuiComponent> iter = this.children.listIterator(this.children.size());

      while(iter.hasPrevious()) {
         GuiComponent child = (GuiComponent)iter.previous();
         if (child.blocksScreen) {
            child.handleInput();
            this.isMouseOver = false;
            return;
         }
      }

      if (this.interceptsMouse) {
         while(Mouse.next()) {
            this.handleMouseInput();
         }
      }

      if (this.interceptsKeyboard) {
         while(Keyboard.next()) {
            this.handleKeyboardInput();
         }
      }

   }

   void mouseHasBeenHandled() {
      this.hasHandledMouse = true;
   }

   protected void keyboardHasBeenHandled() {
      this.hasHandledKeyboard = true;
   }

   protected void setBlocksScreen(boolean value) {
      this.blocksScreen = value;
   }

   public void handleMouseInput() throws IOException {
      boolean handled = false;
      this.isMouseOver = false;
      ListIterator<GuiComponent> iter = this.children.listIterator(this.children.size());

      while(iter.hasPrevious()) {
         GuiComponent child = (GuiComponent)iter.previous();
         child.handleMouseInput();
         if (child.hasHandledMouse) {
            child.hasHandledMouse = false;
            handled = true;
         }
      }

      if (!handled) {
         this.isMouseOver = this.isMouseInRegion(this.getGuiX(), this.getGuiY(), this.getWidth(), this.getHeight());
         super.handleMouseInput();
      }

   }

   public void handleKeyboardInput() throws IOException {
      boolean handled = false;
      ListIterator<GuiComponent> iter = this.children.listIterator(this.children.size());

      while(iter.hasPrevious()) {
         GuiComponent child = (GuiComponent)iter.previous();
         child.handleKeyboardInput();
         if (child.hasHandledKeyboard) {
            child.hasHandledKeyboard = false;
            handled = true;
         }
      }

      if (!handled && Keyboard.getEventKeyState()) {
         this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
      }

   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1 && this.mc.currentScreen != null) {
         this.mc.displayGuiScreen((GuiScreen)null);
         if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
         }
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTick) {
      super.drawScreen(mouseX, mouseY, partialTick);

      for(GuiComponent child : this.children) {
         if (!child.isClipped) {
            child.drawScreen(mouseX, mouseY, partialTick);
         }
      }

      if (this.hoveringTextInfo.shouldDraw) {
         this.drawHoveringText2(this.hoveringTextInfo.lines, this.hoveringTextInfo.x, this.hoveringTextInfo.y, this.hoveringTextInfo.font);
         this.hoveringTextInfo.shouldDraw = false;
      }

   }

   public void onGuiClosed() {
      for(GuiComponent child : this.children) {
         child.onGuiClosed();
      }

      super.onGuiClosed();
   }

   public void updateScreen() {
      for(GuiComponent child : this.children) {
         child.updateScreen();
      }

      super.updateScreen();
      if (this.sizeIsInvalid) {
         this.validateSize();
      }

   }

   public void setWorldAndResolution(Minecraft mc, int width, int height) {
      super.setWorldAndResolution(mc, width, height);

      for(GuiComponent child : this.children) {
         child.setWorldAndResolution(mc, width, height);
      }

   }

   protected int getWidth() {
      return this.contentWidth;
   }

   protected int getHeight() {
      return this.contentHeight;
   }

   void setClipped(boolean value) {
      this.isClipped = value;
   }

   private void invalidateSize() {
      this.sizeIsInvalid = true;
      if (this.parent != null) {
         this.parent.invalidateSize();
      }

   }

   void validateSize() {
      int leftmost = Integer.MAX_VALUE;
      int rightmost = Integer.MIN_VALUE;
      int topmost = Integer.MAX_VALUE;
      int bottommost = Integer.MIN_VALUE;

      for(GuiComponent child : this.children) {
         int x = child.getGuiX();
         if (x < leftmost) {
            leftmost = x;
         }

         int childWidth = child.getWidth();
         if (x + childWidth > rightmost) {
            rightmost = x + childWidth;
         }

         int y = child.getGuiY();
         if (y < topmost) {
            topmost = y;
         }

         int childHeight = child.getHeight();
         if (y + childHeight > bottommost) {
            bottommost = y + childHeight;
         }
      }

      this.contentWidth = Math.max(this.properWidth, rightmost - leftmost);
      this.contentHeight = Math.max(this.properHeight, bottommost - topmost);
      this.sizeIsInvalid = false;
   }

   boolean isMouseInRegion(int left, int top, int width, int height) {
      int mouseX = this.getMouseX();
      int mouseY = this.getMouseY();
      return mouseX >= left && mouseX < left + width && mouseY >= top && mouseY < top + height;
   }

   protected boolean isMouseInRadius(int x, int y, int radius) {
      int mouseX = this.getMouseX();
      int mouseY = this.getMouseY();
      return mouseX >= x - radius && mouseX < x + radius && mouseY >= y - radius && mouseY < y + radius;
   }

   private void drawHoveringText2(List<String> lines, int x, int y, FontRenderer font) {
      GuiUtils.drawHoveringText(lines, x, y, this.width, this.height, -1, font);
   }

   private GuiComponent getTopLevelParent() {
      GuiComponent component;
      for(component = this; component.parent != null; component = component.parent) {
         ;
      }

      return component;
   }

   protected void drawTooltip(List<String> lines, FontRenderer font) {
      GuiComponent topLevel = this.getTopLevelParent();
      topLevel.hoveringTextInfo.lines = lines;
      topLevel.hoveringTextInfo.x = this.getMouseX();
      topLevel.hoveringTextInfo.y = this.getMouseY();
      topLevel.hoveringTextInfo.font = font;
      topLevel.hoveringTextInfo.shouldDraw = true;
   }

   public void close() {
      if (this.parent != null) {
         this.parent.removeChild(this);
      } else {
         Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
      }

   }

   protected void onChildClosed(GuiComponent child) {
   }

   protected void drawCenteredString(String text, int y, int color, boolean dropShadow) {
      int length = this.fontRenderer.getStringWidth(text);
      this.fontRenderer.drawString(text, (float)((this.width - length) / 2), (float)y, color, dropShadow);
   }

   protected int getMouseX() {
      return Mouse.getX() * this.width / this.mc.displayWidth;
   }

   protected int getMouseY() {
      return this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
   }

   private static class HoveringTextInfo {
      List<String> lines;
      int x;
      int y;
      FontRenderer font;
      boolean shouldDraw;

      private HoveringTextInfo() {
         this.shouldDraw = false;
      }
   }
}
