package goblinbob.mobends.core.client.gui.packswindow;

import goblinbob.mobends.core.client.gui.IGuiDraggable;
import goblinbob.mobends.core.client.gui.elements.IGuiListElement;
import goblinbob.mobends.core.pack.IBendsPack;
import goblinbob.mobends.core.util.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class GuiPackEntry implements IGuiListElement, IGuiDraggable {
   private static final int HEIGHT = 31;
   protected final FontRenderer fontRenderer;
   protected String name;
   protected String author;
   protected String description;
   private String displayName;
   private ResourceLocation thumbnailLocation;
   private int x;
   private int y;
   private int smoothX;
   private int smoothY;
   private int dragX;
   private int dragY;
   private int dragPivotX;
   private int dragPivotY;
   private int order;
   private GuiPackList parentList;
   private boolean firstInit = false;
   private boolean hover;
   private boolean selected;
   private boolean dragged;

   public GuiPackEntry(IBendsPack pack) {
      this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
      this.name = pack.getKey();
      this.displayName = pack.getDisplayName();
      this.author = pack.getAuthor();
      this.description = pack.getDescription();
      this.thumbnailLocation = pack.getThumbnail();
      this.dragX = this.dragY = 0;
      this.dragPivotX = this.dragPivotY = 0;
      this.hover = false;
      this.selected = false;
   }

   public void initGui(int x, int y) {
      this.x = x;
      this.y = y;
      if (!this.firstInit) {
         this.firstInit = true;
         this.smoothX = x;
         this.smoothY = y;
      }

   }

   public void update(int mouseX, int mouseY) {
      this.hover = mouseX >= this.x && mouseX <= this.x + 102 && mouseY >= this.y && mouseY <= this.y + 31;
      this.smoothX = (int)((float)this.smoothX + (float)(this.x - this.smoothX) * 0.7F);
      this.smoothY = (int)((float)this.smoothY + (float)(this.y - this.smoothY) * 0.7F);
   }

   public boolean handleMouseClicked(int mouseX, int mouseY, int state) {
      this.update(mouseX, mouseY);
      this.dragPivotX = mouseX - this.x;
      this.dragPivotY = mouseY - this.y;
      return this.hover;
   }

   public void dragTo(int x, int y) {
      this.dragX = x;
      this.dragY = y;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public void setDragged(boolean dragged) {
      this.dragged = dragged;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public void setParentList(GuiPackList parentList) {
      this.parentList = parentList;
   }

   public boolean isDragged() {
      return this.dragged;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getDragX() {
      return this.dragX;
   }

   public int getDragY() {
      return this.dragY;
   }

   public int getDragPivotX() {
      return this.dragPivotX;
   }

   public int getDragPivotY() {
      return this.dragPivotY;
   }

   public int getOrder() {
      return this.order;
   }

   public GuiPackList getParentList() {
      return this.parentList;
   }

   public int getHeight() {
      return 31;
   }

   public void draw(float partialTicks) {
      int viewX = this.dragged ? this.dragX - this.dragPivotX : this.smoothX;
      int viewY = this.dragged ? this.dragY - this.dragPivotY : this.smoothY;
      Minecraft mc = Minecraft.getMinecraft();
      TextureManager textureManager = mc.getTextureManager();
      textureManager.bindTexture(GuiPacksWindow.BACKGROUND_TEXTURE);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int SELECTED_TEXTURE_Y = 62;
      int HOVER_TEXTURE_Y = 31;
      int NEUTRAL_TEXTURE_Y = 0;
      int textureY = this.selected ? 62 : (this.hover ? 31 : 0);
      Draw.texturedModalRect(viewX - 1, viewY - (this.selected ? 1 : 0), 0, textureY, 102, 31);
      textureManager.bindTexture(this.thumbnailLocation);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Draw.texturedRectangle(viewX + 2, viewY + 2, 25, 25, 0.0F, 0.0F, 0.78125F, 0.78125F);
      this.fontRenderer.drawStringWithShadow(this.fontRenderer.trimStringToWidth(this.displayName, 70), (float)(viewX + 32), (float)(viewY + 1), 16777215);
      Draw.rectangleHorizontalGradient((float)(viewX + 101 - 40), (float)(viewY + 1), 39.0F, 9.0F, 5131854, -11645362);
   }
}
