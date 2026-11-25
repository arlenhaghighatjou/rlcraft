package net.ilexiconn.llibrary.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.ClientProxy;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.IElementGUI;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public abstract class ElementGUI extends GuiScreen implements IElementGUI {
   private final Object elementLock = new Object();
   private final List<Element> elements = new ArrayList();
   private Element currentlyClicking;

   public ElementGUI() {
   }

   protected abstract void initElements();

   public void addElement(Element element) {
      synchronized(this.elementLock) {
         this.elements.add(element);
      }

      element.init();
   }

   public void addElements(Collection<Element> elements) {
      elements.forEach(this::addElement);
   }

   public void removeElement(Element element) {
      synchronized(this.elementLock) {
         this.elements.remove(element);
         element.dispose();
      }
   }

   public void clearElements() {
      synchronized(this.elementLock) {
         this.elements.forEach(Element::dispose);
         this.elements.clear();
      }
   }

   public void sendElementToFront(Element element) {
      if (this.elements.contains(element)) {
         synchronized(this.elementLock) {
            this.elements.remove(element);
            this.elements.add(element);
         }
      }

   }

   public void sendElementToBack(Element element) {
      if (this.elements.contains(element)) {
         synchronized(this.elementLock) {
            this.elements.remove(element);
            this.elements.add(0, element);
         }
      }

   }

   public boolean isElementOnTop(Element element) {
      float mouseX = this.getPreciseMouseX();
      float mouseY = this.getPreciseMouseY();
      synchronized(this.elementLock) {
         for(Element e : this.getPostOrderElements()) {
            if (e.isVisible() && mouseX >= e.getPosX() && mouseY >= e.getPosY() && mouseX < e.getPosX() + (float)e.getWidth() && mouseY < e.getPosY() + (float)e.getHeight()) {
               return element == e || element.getParent() != null && element.getParent() == e;
            }
         }

         return false;
      }
   }

   public void playClickSound() {
      this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   public FontRenderer getFontRenderer() {
      return this.mc.fontRenderer;
   }

   public TextureManager getTextureManager() {
      return this.mc.getTextureManager();
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public void initGui() {
      this.initElements();
   }

   public void updateScreen() {
      this.getPostOrderElements().forEach(Element::update);
   }

   public abstract void drawScreen(float var1, float var2, float var3);

   public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
      Gui.drawRect(0, 0, this.width, this.height, LLibrary.CONFIG.getTertiaryColor());
      float preciseMouseX = this.getPreciseMouseX();
      float preciseMouseY = this.getPreciseMouseY();
      this.drawScreen(preciseMouseX, preciseMouseY, partialTicks);
      synchronized(this.elementLock) {
         this.elements.forEach((elementx) -> {
            this.renderElement(element, preciseMouseX, preciseMouseY, partialTicks);
         });
      }

      int scrollAmount = Mouse.getDWheel();
      if (scrollAmount != 0) {
         for(Element element : this.getPostOrderElements()) {
            if (element.isVisible() && element.isEnabled() && element.mouseScrolled(preciseMouseX, preciseMouseY, scrollAmount)) {
               return;
            }
         }
      }

   }

   protected void renderElement(Element element, float mouseX, float mouseY, float partialTicks) {
      if (element.isVisible()) {
         element.renderChildren(mouseX, mouseY, partialTicks);
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      float preciseMouseX = this.getPreciseMouseX();
      float preciseMouseY = this.getPreciseMouseY();
      synchronized(this.elementLock) {
         for(Element element : this.getPostOrderElements()) {
            if (element.isVisible() && element.isEnabled() && element.mouseClicked(preciseMouseX, preciseMouseY, mouseButton)) {
               this.currentlyClicking = element;
               break;
            }
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      float preciseMouseX = this.getPreciseMouseX();
      float preciseMouseY = this.getPreciseMouseY();
      synchronized(this.elementLock) {
         for(Element element : this.getPostOrderElements()) {
            if (element.isVisible() && element.isEnabled() && this.currentlyClicking == element && element.mouseDragged(preciseMouseX, preciseMouseY, clickedMouseButton, timeSinceLastClick)) {
               break;
            }
         }
      }

      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      float preciseMouseX = this.getPreciseMouseX();
      float preciseMouseY = this.getPreciseMouseY();
      synchronized(this.elementLock) {
         for(Element element : this.getPostOrderElements()) {
            if (element.isVisible() && element.isEnabled() && this.currentlyClicking == element && element.mouseReleased(preciseMouseX, preciseMouseY, state)) {
               break;
            }
         }
      }

      super.mouseReleased(mouseX, mouseY, state);
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      synchronized(this.elementLock) {
         for(Element element : this.getPostOrderElements()) {
            if (element.isVisible() && element.isEnabled() && element.keyPressed(typedChar, keyCode)) {
               break;
            }
         }
      }

      super.keyTyped(typedChar, keyCode);
   }

   public void onGuiClosed() {
      this.clearElements();
   }

   public float getPreciseMouseX() {
      ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
      return (float)Mouse.getX() / (float)scaledResolution.getScaleFactor();
   }

   public float getPreciseMouseY() {
      return (float)this.height - (float)Mouse.getY() * (float)this.height / (float)this.mc.displayHeight - 1.0F;
   }

   public Object getElementLock() {
      return this.elementLock;
   }

   public List<Element> getPostOrderElements() {
      List<Element> result = new ArrayList();
      this.traverseRecursively(this.elements, result);
      return Lists.reverse(result);
   }

   public List<Element> getPreOrderElements() {
      List<Element> result = new ArrayList();
      this.traverseRecursively(this.elements, result);
      return result;
   }

   private void traverseRecursively(List<Element> in, List<Element> out) {
      for(Element element : in) {
         out.add(element);
         this.traverseRecursively(element.getChildren(), out);
      }

   }
}
