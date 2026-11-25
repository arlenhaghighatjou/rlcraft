package noppes.vc.shared.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import noppes.vc.containers.ContainerEmpty;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public abstract class GuiContainerBasic extends GuiContainer {
   public boolean drawDefaultBackground = false;
   public int guiLeft;
   public int guiTop;
   public EntityPlayerSP player;
   private HashMap<Integer, GuiButtonNop> buttons = new HashMap();
   private HashMap<Integer, GuiTextFieldNop> textfields = new HashMap();
   private HashMap<Integer, GuiLabel> labels = new HashMap();
   private HashMap<Integer, GuiScroll> scrolls = new HashMap();
   private HashMap<Integer, GuiSliderNop> sliders = new HashMap();
   public boolean closeOnEsc = false;
   private GuiBasic subgui;
   private int subguiId;
   public int mouseX;
   public int mouseY;

   public GuiContainerBasic(Container cont) {
      super(cont);
      this.player = Minecraft.getMinecraft().player;
      this.mc = Minecraft.getMinecraft();
      this.itemRender = this.mc.getRenderItem();
      this.fontRenderer = this.mc.fontRenderer;
   }

   public void setWorldAndResolution(Minecraft mc, int width, int height) {
      super.setWorldAndResolution(mc, width, height);
      this.initPacket();
   }

   public void initPacket() {
   }

   public void initGui() {
      super.initGui();
      GuiTextFieldNop.unfocus();
      this.buttonList.clear();
      this.buttons.clear();
      this.scrolls.clear();
      this.sliders.clear();
      this.labels.clear();
      this.textfields.clear();
      Keyboard.enableRepeatEvents(true);
      if (this.subgui != null) {
         this.subgui.setWorldAndResolution(this.mc, this.width, this.height);
         this.subgui.initGui();
      }

      this.buttonList.clear();
      this.guiLeft = (this.width - this.xSize) / 2;
      this.guiTop = (this.height - this.ySize) / 2;
   }

   public ResourceLocation getResource(String texture) {
      return new ResourceLocation("customnpcs", "textures/gui/" + texture);
   }

   public void updateScreen() {
      for(GuiTextFieldNop tf : new ArrayList(this.textfields.values())) {
         if (tf.enabled) {
            tf.updateCursorCounter();
         }
      }

      super.updateScreen();
   }

   protected void mouseClicked(int i, int j, int k) throws IOException {
      if (this.subgui != null) {
         this.subgui.mouseClicked(i, j, k);
      } else {
         for(GuiTextFieldNop tf : new ArrayList(this.textfields.values())) {
            if (tf.enabled) {
               tf.mouseClicked(i, j, k);
            }
         }

         if (k == 0) {
            for(GuiScroll scroll : new ArrayList(this.scrolls.values())) {
               scroll.mouseClicked(i, j, k);
            }
         }

         this.mouseEvent(i, j, k);
         super.mouseClicked(i, j, k);
      }

   }

   public void mouseEvent(int i, int j, int k) {
   }

   protected void keyTyped(char c, int i) {
      if (this.subgui != null) {
         this.subgui.keyTyped(c, i);
      } else {
         for(GuiTextFieldNop tf : new ArrayList(this.textfields.values())) {
            tf.textboxKeyTyped(c, i);
         }

         if (this.closeOnEsc && (i == 1 || i == this.mc.gameSettings.keyBindInventory.getKeyCode() && !GuiTextFieldNop.isActive())) {
            this.close();
         }
      }

   }

   protected void actionPerformed(GuiButton guibutton) {
      if (this.subgui != null) {
         this.subgui.buttonEvent(guibutton);
      } else {
         this.buttonEvent(guibutton);
      }

   }

   public void buttonEvent(GuiButton guibutton) {
   }

   public void close() {
      GuiTextFieldNop.unfocus();
      this.save();
      this.player.closeScreen();
      this.displayGuiScreen((GuiScreen)null);
      this.mc.setIngameFocus();
   }

   public void addButton(GuiButtonNop button) {
      this.buttons.put(Integer.valueOf(button.id), button);
      this.buttonList.add(button);
   }

   public GuiButtonNop getButton(int i) {
      return (GuiButtonNop)this.buttons.get(Integer.valueOf(i));
   }

   public void addTextField(GuiTextFieldNop tf) {
      this.textfields.put(Integer.valueOf(tf.id), tf);
   }

   public GuiTextFieldNop getTextField(int i) {
      return (GuiTextFieldNop)this.textfields.get(Integer.valueOf(i));
   }

   public void addLabel(GuiLabel label) {
      this.labels.put(Integer.valueOf(label.id), label);
   }

   public GuiLabel getLabel(int i) {
      return (GuiLabel)this.labels.get(Integer.valueOf(i));
   }

   public void addSlider(GuiSliderNop slider) {
      this.sliders.put(Integer.valueOf(slider.id), slider);
      this.buttonList.add(slider);
   }

   public GuiSliderNop getSlider(int i) {
      return (GuiSliderNop)this.sliders.get(Integer.valueOf(i));
   }

   public void addScroll(GuiScroll scroll) {
      scroll.setWorldAndResolution(this.mc, 350, 250);
      this.scrolls.put(Integer.valueOf(scroll.id), scroll);
   }

   public GuiScroll getScroll(int id) {
      return (GuiScroll)this.scrolls.get(Integer.valueOf(id));
   }

   protected void drawGuiContainerForegroundLayer(int par1, int par2) {
   }

   protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
      for(GuiLabel label : new ArrayList(this.labels.values())) {
         label.drawLabel(this, this.fontRenderer);
      }

      for(GuiTextFieldNop tf : new ArrayList(this.textfields.values())) {
         tf.drawTextBox(i, j);
      }

      for(GuiScroll scroll : new ArrayList(this.scrolls.values())) {
         scroll.drawScreen(i, j, f, this.hasSubGui() ? 0 : Mouse.getDWheel());
      }

   }

   public abstract void save();

   public void drawScreen(int i, int j, float f) {
      this.mouseX = i;
      this.mouseY = j;
      Container container = this.inventorySlots;
      if (this.subgui != null) {
         this.inventorySlots = new ContainerEmpty();
      }

      super.drawScreen(i, j, f);
      this.zLevel = 0.0F;
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.subgui != null) {
         this.inventorySlots = container;
         RenderHelper.disableStandardItemLighting();
         this.subgui.drawScreen(i, j, f);
      }

   }

   public void drawDefaultBackground() {
      if (this.drawDefaultBackground && this.subgui == null) {
         super.drawDefaultBackground();
      }

   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   public void setSubGui(int id, GuiBasic gui) {
      if (this.subgui != null) {
         this.closeSubGui();
      }

      this.subguiId = id;
      this.subgui = gui;
      this.subgui.setWorldAndResolution(this.mc, this.width, this.height);
      this.subgui.parent = this;
      this.initGui();
   }

   public void closeSubGui() {
      if (this instanceof GuiBasic.ISubGuiListener) {
         ((GuiBasic.ISubGuiListener)this).subGuiClosed(this.subguiId, this.subgui);
      }

      this.subgui = null;
   }

   public boolean hasSubGui() {
      return this.subgui != null;
   }

   public GuiBasic getSubGui() {
      return this.hasSubGui() && this.subgui.hasSubGui() ? this.subgui.getSubGui() : this.subgui;
   }

   public void displayGuiScreen(GuiScreen gui) {
      this.mc.displayGuiScreen(gui);
   }

   public void setSubGui(GuiBasic gui) {
      this.subgui = gui;
      this.subgui.setWorldAndResolution(this.mc, this.width, this.height);
      this.subgui.parent = this;
      this.initGui();
   }
}
