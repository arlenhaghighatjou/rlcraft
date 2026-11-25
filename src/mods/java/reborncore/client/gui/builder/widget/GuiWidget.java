package reborncore.client.gui.builder.widget;

import java.util.ArrayList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public abstract class GuiWidget<T extends Container> extends GuiContainer {
   public static final LanguageMap translate = (LanguageMap)ObfuscationReflectionHelper.getPrivateValue(LanguageMap.class, (Object)null, 2);
   private final ArrayList<Widget> widgets = new ArrayList();
   private final ResourceLocation background;

   public GuiWidget(T inventorySlotsIn, ResourceLocation background, int xSize, int ySize) {
      super(inventorySlotsIn);
      this.xSize = xSize;
      this.ySize = ySize;
      this.background = background;
   }

   public T getContainer() {
      return (T)this.inventorySlots;
   }

   public void initGui() {
      super.initGui();
      this.widgets.clear();
      this.initWidgets();
   }

   public void addWidget(Widget widget) {
      this.widgets.add(widget);
   }

   public void removeWidget(Widget widget) {
      this.widgets.remove(widget);
   }

   public abstract void initWidgets();

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(this.background);
      int x = (this.width - this.xSize) / 2;
      int y = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      int x = (this.width - this.xSize) / 2;
      int y = (this.height - this.ySize) / 2;
      String name = translate.translateKey("tile.techreborn.industrialgrinder.name");
      this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);
      this.fontRenderer.drawString(translate.translateKey("container.inventory"), 8, this.ySize - 94, 4210752);

      for(Widget widget : this.widgets) {
         widget.drawWidget(this, x, y, mouseX, mouseY);
      }

   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }
}
