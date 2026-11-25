package xzeroair.trinkets.client.gui.entityPropertiesGui;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.ForgeRegistry;
import org.lwjgl.input.Mouse;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.races.EntityRace;
import xzeroair.trinkets.util.TrinketsConfig;

@SideOnly(Side.CLIENT)
public class GuiRaceSelectionScreen extends GuiScreen {
   public EntityPlayer player;
   public EntityProperties properties;
   public int buttonPressed;
   protected boolean flip = false;
   public static ResourceLocation background = null;
   private float oldMouseX;
   private float oldMouseY;
   private GuiScrollingList raceSelection;
   private int selected = -1;
   private int listWidth;
   private int buttonMargin = 1;
   Map<Integer, EntityRace> buttonMap = new HashMap();

   public GuiRaceSelectionScreen(EntityPlayer player) {
      this.player = player;
      this.properties = Capabilities.getEntityProperties(player);
   }

   public void selectAbilityIndex(int index) {
      if (index != this.selected) {
         this.selected = index;
         this.updateCache();
      }
   }

   public boolean abilityIndexSelected(int index) {
      return index == this.selected;
   }

   public void initGui() {
      this.buttonList.clear();
      this.buttonMap.clear();
      super.initGui();
      this.addButton(new GuiPropertiesButton(2, this.width - 16, 2, 14, 20, TextFormatting.RED + "X"));
      int slotHeight = 20;
      ForgeRegistry<EntityRace> registryList = EntityRace.Registry;
      int ID = 3;
      int xPos = 40;
      int yPos = 40;

      for(EntityRace r : registryList) {
         if (r.getUUID().compareTo(UUID.fromString("00000000-0000-0000-0000-000000000000")) != 0) {
            boolean forbiddon = false;

            for(String blkList : TrinketsConfig.SERVER.races.selectionBlacklist) {
               if (r.getName().equalsIgnoreCase(blkList)) {
                  forbiddon = true;
                  break;
               }
            }

            if (!forbiddon) {
               int txtLength = this.getFontRenderer().getStringWidth(r.getName()) + 10;
               this.addButton(new GuiButton(ID, xPos, yPos, txtLength, 20, r.getName()));
               this.buttonMap.putIfAbsent(Integer.valueOf(ID), r);
               ++ID;
               if (xPos + txtLength >= this.width) {
                  xPos = 10;
                  yPos += 24;
               } else {
                  xPos += txtLength;
               }
            }
         }
      }

   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      this.buttonPressed = 0;
   }

   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
   }

   protected void mouseClicked(int x, int y, int button) throws IOException {
      super.mouseClicked(x, y, button);
   }

   protected void keyTyped(char c, int keyCode) throws IOException {
      super.keyTyped(c, keyCode);
      if (keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode()) {
         this.player.closeScreen();
         this.displayNormalInventory();
      }

   }

   public void displayNormalInventory() {
      GuiInventory gui = new GuiInventory(this.mc.player);
      this.mc.displayGuiScreen(gui);
   }

   public void updateScreen() {
      super.updateScreen();
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      this.buttonPressed = button.id;
      super.actionPerformed(button);
      if (button.id == 1) {
         this.mc.player.openGui(Trinkets.instance, 2, this.mc.player.world, 0, 0, 0);
      }

      if (button.id == 2) {
         this.mc.player.closeScreen();
      }

      if (button.id > 2) {
         EntityRace race = (EntityRace)this.buttonMap.get(Integer.valueOf(button.id));
         if (race != null) {
            this.properties.setImbuedRace(race);
            this.properties.sendInformationToServer();
            this.mc.player.closeScreen();
         }
      }

   }

   public void onGuiClosed() {
      super.onGuiClosed();
   }

   public boolean doesGuiPauseGame() {
      return super.doesGuiPauseGame();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.oldMouseX = (float)mouseX;
      this.oldMouseY = (float)mouseY;
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.properties != null) {
         if (this.raceSelection != null) {
            this.raceSelection.drawScreen(mouseX, mouseY, partialTicks);
         }

      }
   }

   public void handleMouseInput() throws IOException {
      int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      super.handleMouseInput();
      if (this.raceSelection != null) {
         this.raceSelection.handleMouseInput(mouseX, mouseY);
      }

   }

   public int drawLine(String line, int offset, int shifty) {
      this.fontRenderer.drawString(line, offset, shifty, 14151146);
      return shifty + 10;
   }

   public Minecraft getMinecraftInstance() {
      return this.mc;
   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   private void updateCache() {
      ResourceLocation logoPath2 = null;
      new Dimension(0, 0);
      ResourceLocation logoPath = null;
      new Dimension(0, 0);
      List<String> lines = new ArrayList();
      lines.add((Object)null);

      for(int i = 0; i < 40; ++i) {
         lines.add("This is a Test Line");
      }

   }

   private class Info extends GuiScrollingList {
      @Nullable
      private ResourceLocation logoPath;
      private Dimension logoDims;
      private List<ITextComponent> lines = null;

      public Info(int width, List<String> lines, @Nullable ResourceLocation logoPath, Dimension logoDims, int top, int bottom, int left) {
         super(GuiRaceSelectionScreen.this.getMinecraftInstance(), width, GuiRaceSelectionScreen.this.height, top, bottom, left, 60, GuiRaceSelectionScreen.this.width, GuiRaceSelectionScreen.this.height);
         this.lines = this.resizeContent(lines);
         this.logoPath = logoPath;
         this.logoDims = logoDims;
         this.setHeaderInfo(true, this.getHeaderHeight());
      }

      public Info(int width, List<String> lines, @Nullable ResourceLocation logoPath, Dimension logoDims) {
         super(GuiRaceSelectionScreen.this.getMinecraftInstance(), width, GuiRaceSelectionScreen.this.height, 32, GuiRaceSelectionScreen.this.height - 18 + 4, GuiRaceSelectionScreen.this.listWidth + 20, 60, GuiRaceSelectionScreen.this.width, GuiRaceSelectionScreen.this.height);
         this.lines = this.resizeContent(lines);
         this.logoPath = logoPath;
         this.logoDims = logoDims;
         this.setHeaderInfo(true, this.getHeaderHeight());
      }

      protected int getSize() {
         return 0;
      }

      protected void elementClicked(int index, boolean doubleClick) {
      }

      protected boolean isSelected(int index) {
         return false;
      }

      protected void drawBackground() {
      }

      protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
      }

      public void drawScreen(int mouseX, int mouseY, float partialTicks) {
         super.drawScreen(mouseX, mouseY, partialTicks);
      }

      private List<ITextComponent> resizeContent(List<String> lines) {
         List<ITextComponent> ret = new ArrayList();

         for(String line : lines) {
            if (line == null) {
               ret.add((Object)null);
            } else {
               ITextComponent chat = ForgeHooks.newChatWithLinks(line, false);
               int maxTextLength = this.listWidth - 8;
               if (maxTextLength >= 0) {
                  ret.addAll(GuiUtilRenderComponents.splitText(chat, maxTextLength, GuiRaceSelectionScreen.this.getFontRenderer(), false, true));
               }
            }
         }

         return ret;
      }

      private int getHeaderHeight() {
         int height = 0;
         if (this.logoPath != null) {
            double scaleX = (double)this.logoDims.width / 200.0D;
            double scaleY = (double)this.logoDims.height / 65.0D;
            double scale = 1.0D;
            if (scaleX > 1.0D || scaleY > 1.0D) {
               scale = 1.0D / Math.max(scaleX, scaleY);
            }

            this.logoDims.width = (int)((double)this.logoDims.width * scale);
            this.logoDims.height = (int)((double)this.logoDims.height * scale);
            height = height + this.logoDims.height;
            height = height + 10;
         }

         height = height + this.lines.size() * 10;
         if (height < this.bottom - this.top - 8) {
            height = this.bottom - this.top - 8;
         }

         return height;
      }

      protected void drawHeader(int entryRight, int relativeY, Tessellator tess) {
         int top = relativeY;
         if (this.logoPath != null) {
            GlStateManager.enableBlend();
            GuiRaceSelectionScreen.this.getMinecraftInstance().renderEngine.bindTexture(this.logoPath);
            BufferBuilder wr = tess.getBuffer();
            int offset = this.left + this.listWidth / 2 - this.logoDims.width / 2;
            wr.begin(7, DefaultVertexFormats.POSITION_TEX);
            wr.pos((double)offset, (double)(relativeY + this.logoDims.height), (double)GuiRaceSelectionScreen.this.zLevel).tex(0.0D, 1.0D).endVertex();
            wr.pos((double)(offset + this.logoDims.width), (double)(relativeY + this.logoDims.height), (double)GuiRaceSelectionScreen.this.zLevel).tex(1.0D, 1.0D).endVertex();
            wr.pos((double)(offset + this.logoDims.width), (double)relativeY, (double)GuiRaceSelectionScreen.this.zLevel).tex(1.0D, 0.0D).endVertex();
            wr.pos((double)offset, (double)relativeY, (double)GuiRaceSelectionScreen.this.zLevel).tex(0.0D, 0.0D).endVertex();
            tess.draw();
            GlStateManager.disableBlend();
            top = relativeY + this.logoDims.height + 10;
         }

         for(ITextComponent line : this.lines) {
            if (line != null) {
               GlStateManager.enableBlend();
               GuiRaceSelectionScreen.this.getFontRenderer().drawStringWithShadow(line.getFormattedText(), (float)(this.left + 4), (float)top, 16777215);
               GlStateManager.disableAlpha();
               GlStateManager.disableBlend();
            }

            top += 10;
         }

      }

      protected void clickHeader(int x, int y) {
         int offset = y;
         if (this.logoPath != null) {
            offset = y - (this.logoDims.height + 10);
         }

         if (offset > 0) {
            int lineIdx = offset / 10;
            if (lineIdx < this.lines.size()) {
               ITextComponent line = (ITextComponent)this.lines.get(lineIdx);
               if (line != null) {
                  int k = -4;

                  for(ITextComponent part : line) {
                     if (part instanceof TextComponentString) {
                        k += GuiRaceSelectionScreen.this.fontRenderer.getStringWidth(((TextComponentString)part).getText());
                        if (k >= x) {
                           GuiRaceSelectionScreen.this.handleComponentClick(part);
                           break;
                        }
                     }
                  }
               }

            }
         }
      }
   }
}
