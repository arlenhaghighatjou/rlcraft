package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.window.Window;

public abstract class Element {
   public final Window parent;
   public int posX;
   public int posY;
   public int width;
   public int height;
   public int id;
   public boolean ignoreMinimized;

   public Element(Window window, int x, int y, int w, int h, int ID, boolean igMin) {
      this.parent = window;
      this.posX = x;
      this.posY = y;
      this.width = w;
      this.height = h;
      this.id = ID;
      this.ignoreMinimized = igMin;
   }

   public void update() {
   }

   public abstract void draw(int var1, int var2, boolean var3);

   public boolean mouseInBoundary(int mouseX, int mouseY) {
      return mouseX >= this.posX && mouseX <= this.posX + this.width && mouseY >= this.posY && mouseY <= this.posY + this.height;
   }

   public boolean onClick(int mouseX, int mouseY, int id) {
      return false;
   }

   public void selected() {
   }

   public void deselected() {
   }

   public void resized() {
   }

   public void triggerInput() {
      this.parent.elementTriggered(this);
   }

   public void keyInput(char c, int key) {
   }

   public boolean mouseScroll(int mouseX, int mouseY, int k) {
      return false;
   }

   public String tooltip() {
      return null;
   }

   public final int getPosX() {
      return this.parent.posX + this.posX;
   }

   public final int getPosY() {
      return this.parent.posY + this.posY;
   }
}
