package reborncore.client.gui.builder.slot.elements;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.guibuilder.GuiBuilder;
import reborncore.common.tile.RebornMachineTile;

public class ElementBase {
   public int x;
   public int y;
   public boolean isHovering = false;
   public boolean isDragging = false;
   public boolean isPressing = false;
   public boolean isReleasing = false;
   public boolean startPressLast = false;
   public boolean isHoveringLast = false;
   public boolean isDraggingLast = false;
   public boolean isPressingLast = false;
   public boolean isReleasingLast = false;
   public List<ElementBase.Action> hoverActions = new ArrayList();
   public List<ElementBase.Action> dragActions = new ArrayList();
   public List<ElementBase.Action> startPressActions = new ArrayList();
   public List<ElementBase.Action> pressActions = new ArrayList();
   public List<ElementBase.Action> releaseActions = new ArrayList();
   public SpriteContainer container;
   public List<ElementBase.UpdateAction> updateActions = new ArrayList();
   public List<ElementBase.UpdateAction> buttonUpdate = new ArrayList();
   private int width;
   private int height;
   public static final ResourceLocation MECH_ELEMENTS = new ResourceLocation("reborncore", "textures/gui/elements.png");

   public ElementBase(int x, int y, SpriteContainer container) {
      this.container = container;
      this.x = x;
      this.y = y;
   }

   public ElementBase(int x, int y, ISprite... sprites) {
      this.container = new SpriteContainer();

      for(ISprite sprite : sprites) {
         this.container.addSprite(sprite);
      }

      this.x = x;
      this.y = y;
   }

   public ElementBase(int x, int y, int width, int height) {
      this.container = new SpriteContainer();
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public ElementBase(int x, int y, int width, int height, SpriteContainer container) {
      this.container = container;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public ElementBase(int x, int y, int width, int height, ISprite... sprites) {
      this.container = new SpriteContainer();

      for(ISprite sprite : sprites) {
         this.container.addSprite(sprite);
      }

      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public SpriteContainer getSpriteContainer() {
      return this.container;
   }

   public void adjustDimensions(RebornMachineTile provider) {
      if (this.container.offsetSprites != null) {
         for(OffsetSprite offsetSprite : this.container.offsetSprites) {
            if (offsetSprite.getSprite().getSprite(provider).width + offsetSprite.getOffsetX(provider) > this.width) {
               this.width = offsetSprite.getSprite().getSprite(provider).width + offsetSprite.getOffsetX(provider);
            }

            if (offsetSprite.getSprite().getSprite(provider).height + offsetSprite.getOffsetY(provider) > this.height) {
               this.height = offsetSprite.getSprite().getSprite(provider).height + offsetSprite.getOffsetY(provider);
            }
         }
      }

   }

   public void draw(GuiBase gui) {
      for(OffsetSprite sprite : this.getSpriteContainer().offsetSprites) {
         this.drawSprite(gui, sprite.getSprite(), this.x + sprite.getOffsetX(gui.getMachine()), this.y + sprite.getOffsetY(gui.getMachine()));
      }

   }

   public void renderUpdate(GuiBase gui) {
      this.isHoveringLast = this.isHovering;
      this.isPressingLast = this.isPressing;
      this.isDraggingLast = this.isDragging;
      this.isReleasingLast = this.isReleasing;
   }

   public void update(GuiBase gui) {
      for(ElementBase.UpdateAction action : this.updateActions) {
         action.update(gui, this);
      }

   }

   public ElementBase addUpdateAction(ElementBase.UpdateAction action) {
      this.updateActions.add(action);
      return this;
   }

   public ElementBase setWidth(int width) {
      this.width = width;
      return this;
   }

   public ElementBase setHeight(int height) {
      this.height = height;
      return this;
   }

   public int getX() {
      return this.x;
   }

   public ElementBase setX(int x) {
      this.x = x;
      return this;
   }

   public int getY() {
      return this.y;
   }

   public ElementBase setY(int y) {
      this.y = y;
      return this;
   }

   public int getWidth(RebornMachineTile provider) {
      this.adjustDimensions(provider);
      return this.width;
   }

   public int getHeight(RebornMachineTile provider) {
      this.adjustDimensions(provider);
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public ElementBase addHoverAction(ElementBase.Action action) {
      this.hoverActions.add(action);
      return this;
   }

   public ElementBase addDragAction(ElementBase.Action action) {
      this.dragActions.add(action);
      return this;
   }

   public ElementBase addStartPressAction(ElementBase.Action action) {
      this.startPressActions.add(action);
      return this;
   }

   public ElementBase addPressAction(ElementBase.Action action) {
      this.pressActions.add(action);
      return this;
   }

   public ElementBase addReleaseAction(ElementBase.Action action) {
      this.releaseActions.add(action);
      return this;
   }

   public boolean onHover(RebornMachineTile provider, GuiBase gui, int mouseX, int mouseY) {
      for(ElementBase.Action action : this.hoverActions) {
         action.execute(this, gui, provider, mouseX, mouseY);
      }

      return !this.hoverActions.isEmpty();
   }

   public boolean onDrag(RebornMachineTile provider, GuiBase gui, int mouseX, int mouseY) {
      for(ElementBase.Action action : this.dragActions) {
         action.execute(this, gui, provider, mouseX, mouseY);
      }

      return !this.dragActions.isEmpty();
   }

   public boolean onStartPress(RebornMachineTile provider, GuiBase gui, int mouseX, int mouseY) {
      for(ElementBase.Action action : this.startPressActions) {
         action.execute(this, gui, provider, mouseX, mouseY);
      }

      return !this.startPressActions.isEmpty();
   }

   public boolean onRelease(RebornMachineTile provider, GuiBase gui, int mouseX, int mouseY) {
      for(ElementBase.Action action : this.releaseActions) {
         if (action.execute(this, gui, provider, mouseX, mouseY)) {
            return true;
         }
      }

      if (this.isPressing) {
         for(ElementBase.Action action : this.pressActions) {
            action.execute(this, gui, provider, mouseX, mouseY);
         }
      }

      return !this.releaseActions.isEmpty() || !this.pressActions.isEmpty();
   }

   public void drawRect(GuiBase gui, int x, int y, int width, int height, int colour) {
      this.drawGradientRect(gui, x, y, width, height, colour, colour);
   }

   public void drawGradientRect(GuiBase gui, int x, int y, int width, int height, int startColor, int endColor) {
      x = this.adjustX(gui, x);
      y = this.adjustY(gui, y);
      int right = x + width;
      int bottom = y + height;
      float f = (float)(startColor >> 24 & 255) / 255.0F;
      float f1 = (float)(startColor >> 16 & 255) / 255.0F;
      float f2 = (float)(startColor >> 8 & 255) / 255.0F;
      float f3 = (float)(startColor & 255) / 255.0F;
      float f4 = (float)(endColor >> 24 & 255) / 255.0F;
      float f5 = (float)(endColor >> 16 & 255) / 255.0F;
      float f6 = (float)(endColor >> 8 & 255) / 255.0F;
      float f7 = (float)(endColor & 255) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder vertexbuffer = tessellator.getBuffer();
      vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      vertexbuffer.pos((double)right, (double)y, 0.0D).color(f1, f2, f3, f).endVertex();
      vertexbuffer.pos((double)x, (double)y, 0.0D).color(f1, f2, f3, f).endVertex();
      vertexbuffer.pos((double)x, (double)bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
      vertexbuffer.pos((double)right, (double)bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public int adjustX(GuiBase gui, int x) {
      return gui.guiLeft + x;
   }

   public int adjustY(GuiBase gui, int y) {
      return gui.guiTop + y;
   }

   public boolean isInRect(GuiBase gui, int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
      return gui.isPointInRect(x + gui.guiLeft, y + gui.guiTop, xSize, ySize, mouseX, mouseY);
   }

   public void drawString(GuiBase gui, String string, int x, int y, int color) {
      x = this.adjustX(gui, x);
      y = this.adjustY(gui, y);
      gui.mc.fontRenderer.drawString(string, x, y, color);
   }

   public void drawString(GuiBase gui, String string, int x, int y) {
      this.drawString(gui, string, x, y, 16777215);
   }

   public void setTextureSheet(ResourceLocation textureLocation) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(textureLocation);
   }

   public void drawCenteredString(GuiBase gui, String string, int y, int colour) {
      this.drawString(gui, string, gui.getXSize() / 2 - gui.mc.fontRenderer.getStringWidth(string) / 2, y, colour);
   }

   public void drawCenteredString(GuiBase gui, String string, int x, int y, int colour) {
      this.drawString(gui, string, x - gui.mc.fontRenderer.getStringWidth(string) / 2, y, colour);
   }

   public int getStringWidth(String string) {
      return Minecraft.getMinecraft().fontRenderer.getStringWidth(string);
   }

   public void drawSprite(GuiBase gui, ISprite iSprite, int x, int y) {
      Sprite sprite = iSprite.getSprite(gui.getMachine());
      if (sprite != null) {
         if (sprite.hasTextureInfo()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            this.setTextureSheet(sprite.textureLocation);
            gui.drawTexturedModalRect(x + gui.guiLeft, y + gui.guiTop, sprite.x, sprite.y, sprite.width, sprite.height);
         }

         if (sprite.hasStack()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderHelper.enableGUIStandardItemLighting();
            RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
            itemRenderer.renderItemAndEffectIntoGUI(sprite.itemStack, x + gui.guiLeft, y + gui.guiTop);
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
         }
      }

   }

   public int getScaledBurnTime(int scale, int burnTime, int totalBurnTime) {
      return (int)((float)burnTime / (float)totalBurnTime * (float)scale);
   }

   public int getPercentage(int MaxValue, int CurrentValue) {
      return CurrentValue == 0 ? 0 : (int)((float)CurrentValue * 100.0F / (float)MaxValue);
   }

   public void drawDefaultBackground(GuiScreen gui, int x, int y, int width, int height) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiBuilder.defaultTextureSheet);
      gui.drawTexturedModalRect(x, y, 0, 0, width / 2, height / 2);
      gui.drawTexturedModalRect(x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2);
      gui.drawTexturedModalRect(x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2);
      gui.drawTexturedModalRect(x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2, height / 2);
   }

   public interface Action {
      boolean execute(ElementBase var1, GuiBase var2, RebornMachineTile var3, int var4, int var5);
   }

   public interface UpdateAction {
      void update(GuiBase var1, ElementBase var2);
   }
}
