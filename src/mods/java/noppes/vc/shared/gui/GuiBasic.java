package noppes.vc.shared.gui;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public abstract class GuiBasic extends GuiScreen {
   public EntityPlayerSP player;
   public boolean drawDefaultBackground = true;
   private Map<Integer, GuiButtonNop> buttons = new ConcurrentHashMap();
   private Map<Integer, GuiTextFieldNop> textfields = new ConcurrentHashMap();
   private Map<Integer, GuiLabel> labels = new ConcurrentHashMap();
   private Map<Integer, GuiScroll> scrolls = new ConcurrentHashMap();
   private Map<Integer, GuiSliderNop> sliders = new ConcurrentHashMap();
   private Map<Integer, GuiScreen> extra = new ConcurrentHashMap();
   public String title;
   public ResourceLocation background = null;
   public boolean closeOnEsc = false;
   public int guiLeft;
   public int guiTop;
   public int xSize;
   public int ySize;
   private GuiBasic subgui;
   private int subguiId;
   public GuiScreen parent;
   public int mouseX;
   public int mouseY;
   public float bgScale = 1.0F;
   private GuiButton selectedButton;

   public GuiBasic() {
      this.player = Minecraft.getMinecraft().player;
      this.title = "";
      this.xSize = 200;
      this.ySize = 222;
      this.mc = Minecraft.getMinecraft();
      this.itemRender = this.mc.getRenderItem();
      this.fontRenderer = this.mc.fontRenderer;
   }

   public void setBackground(String texture) {
      this.background = new ResourceLocation("variedcommodities", "textures/gui/" + texture);
   }

   public ResourceLocation getResource(String texture) {
      return new ResourceLocation("variedcommodities", "textures/gui/" + texture);
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
      if (this.subgui != null) {
         this.subgui.setWorldAndResolution(this.mc, this.width, this.height);
         this.subgui.initGui();
      }

      this.guiLeft = (this.width - this.xSize) / 2;
      this.guiTop = (this.height - this.ySize) / 2;
      this.buttonList = Lists.newArrayList();
      this.buttons = new ConcurrentHashMap();
      this.textfields = new ConcurrentHashMap();
      this.labels = new ConcurrentHashMap();
      this.scrolls = new ConcurrentHashMap();
      this.sliders = new ConcurrentHashMap();
      this.extra = new ConcurrentHashMap();
   }

   public void updateScreen() {
      if (this.subgui != null) {
         this.subgui.updateScreen();
      } else {
         for(GuiTextFieldNop tf : new ArrayList<GuiTextFieldNop>(this.textfields.values())) {
            if (tf.enabled) {
               tf.updateCursorCounter();
            }
         }

         super.updateScreen();
      }

   }

   public void addExtra(GuiHoverText gui) {
      gui.setWorldAndResolution(this.mc, 350, 250);
      this.extra.put(Integer.valueOf(gui.id), gui);
   }

   public void mouseClicked(int i, int j, int k) {
      if (this.subgui != null) {
         this.subgui.mouseClicked(i, j, k);
      } else {
         for(GuiTextFieldNop tf : new ArrayList<GuiTextFieldNop>(this.textfields.values())) {
            if (tf.enabled) {
               tf.mouseClicked(i, j, k);
            }
         }

         this.mouseEvent(i, j, k);
         if (k == 0) {
            for(GuiScroll scroll : new ArrayList<GuiScroll>(this.scrolls.values())) {
               scroll.mouseClicked(i, j, k);
            }

            for(GuiButton guibutton : this.buttonList) {
               if (guibutton.mousePressed(this.mc, this.mouseX, this.mouseY)) {
                  Pre event = new Pre(this, guibutton, this.buttonList);
                  if (!MinecraftForge.EVENT_BUS.post(event)) {
                     guibutton = event.getButton();
                     this.selectedButton = guibutton;
                     guibutton.playPressSound(this.mc.getSoundHandler());
                     this.actionPerformed(guibutton);
                     if (this.equals(this.mc.currentScreen)) {
                        MinecraftForge.EVENT_BUS.post(new Post(this, event.getButton(), this.buttonList));
                     }
                  }
                  break;
               }
            }
         }
      }
   }

   public void mouseReleased(int mouseX, int mouseY, int state) {
      if (this.selectedButton != null && state == 0) {
         this.selectedButton.mouseReleased(mouseX, mouseY);
         this.selectedButton = null;
      }

   }

   public void mouseEvent(int i, int j, int k) {
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

   public void keyTyped(char c, int i) {
      if (this.subgui != null) {
         this.subgui.keyTyped(c, i);
      }

      for(GuiTextFieldNop tf : new ArrayList<GuiTextFieldNop>(this.textfields.values())) {
         tf.textboxKeyTyped(c, i);
      }

      if (this.closeOnEsc && (i == 1 || !GuiTextFieldNop.isActive() && this.isInventoryKey(i))) {
         this.close();
      }
   }

   public void onGuiClosed() {
      GuiTextFieldNop.unfocus();
   }

   public void close() {
      if (this.parent != null) {
         this.save();
         if (this.parent instanceof GuiBasic) {
            ((GuiBasic)this.parent).closeSubGui();
         }

         if (this.parent instanceof GuiContainerBasic) {
            ((GuiContainerBasic)this.parent).closeSubGui();
         }
      } else {
         this.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
         this.save();
      }

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

   public abstract void save();

   public void drawScreen(int i, int j, float f) {
      this.mouseX = i;
      this.mouseY = j;
      if (this.drawDefaultBackground && this.subgui == null) {
         this.drawDefaultBackground();
      }

      if (this.background != null && this.mc.renderEngine != null) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)this.guiLeft, (float)this.guiTop, 0.0F);
         GlStateManager.scale(this.bgScale, this.bgScale, this.bgScale);
         this.mc.renderEngine.bindTexture(this.background);
         if (this.xSize > 256) {
            this.drawTexturedModalRect(0, 0, 0, 0, 250, this.ySize);
            this.drawTexturedModalRect(250, 0, 256 - (this.xSize - 250), 0, this.xSize - 250, this.ySize);
         } else {
            this.drawTexturedModalRect(0, 0, 0, 0, this.xSize, this.ySize);
         }

         GlStateManager.popMatrix();
      }

      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);

      for(GuiLabel label : new ArrayList<GuiLabel>(this.labels.values())) {
         label.drawLabel(this, this.fontRenderer);
      }

      for(GuiTextFieldNop tf : new ArrayList<GuiTextFieldNop>(this.textfields.values())) {
         tf.drawTextBox(i, j);
      }

      for(GuiScroll scroll : new ArrayList<GuiScroll>(this.scrolls.values())) {
         scroll.drawScreen(i, j, f, !this.hasSubGui() && scroll.isMouseOver(i, j) ? Mouse.getDWheel() : 0);
      }

      for(GuiScreen gui : new ArrayList<GuiScreen>(this.extra.values())) {
         gui.drawScreen(i, j, f);
      }

      super.drawScreen(i, j, f);
      if (this.subgui != null) {
         this.subgui.drawScreen(i, j, f);
      }

   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   public void elementClicked() {
      if (this.subgui != null) {
         this.subgui.elementClicked();
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void doubleClicked() {
   }

   public boolean isInventoryKey(int i) {
      return i == this.mc.gameSettings.keyBindInventory.getKeyCode();
   }

   public void drawDefaultBackground() {
      super.drawDefaultBackground();
   }

   public void displayGuiScreen(GuiScreen gui) {
      this.mc.displayGuiScreen(gui);
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

   public void openLink(String link) {
      EagRuntime.openLink(link);
   }

   public interface IGuiData {
      void setGuiData(NBTTagCompound var1);
   }

   public interface ISubGuiListener {
      void subGuiClosed(int var1, GuiBasic var2);
   }
}
