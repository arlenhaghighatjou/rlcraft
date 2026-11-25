package de.Whitedraco.switchbow.gui;

import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.gui.elements.ArrowButton;
import de.Whitedraco.switchbow.proxy.packets.TEArrowDispenserCountPacket;
import de.Whitedraco.switchbow.proxy.packets.TEArrowDispenserRangePacket;
import de.Whitedraco.switchbow.proxy.packets.TEArrowDispenserShootAnglePacket;
import de.Whitedraco.switchbow.tileentity.TileEntityArrowDispenser;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIArrowDispenser extends GuiContainer {
   private static final ResourceLocation MULTICANNON_GUI_TEXTURES = new ResourceLocation("switchbow".toLowerCase(), "textures/gui/container/arrowdispenser.png");
   private final InventoryPlayer playerInventory;
   private TileEntityArrowDispenser TEArrowDispenser;

   public GUIArrowDispenser(InventoryPlayer playerInv, TileEntityArrowDispenser TileEntityArrowLauncher) {
      super(new ContainerArrowDispenser(playerInv, TileEntityArrowLauncher));
      this.playerInventory = playerInv;
      this.setGuiSize(176, 170);
      this.xSize = 176;
      this.ySize = 170;
      this.TEArrowDispenser = TileEntityArrowLauncher;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String s = I18n.translateToLocal("tile.ArrowDispenser.name");
      this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
      String shootangle = I18n.translateToLocal("TEMC.shootangle");
      String range = I18n.translateToLocal("TEMC.range");
      String count = I18n.translateToLocal("TEMC.count");
      this.fontRenderer.drawString(shootangle + ": " + this.FloatToStr(this.TEArrowDispenser.getShootangle()), 8, 25, 4210752);
      this.fontRenderer.drawString(range + ": " + this.FloatToStr(this.TEArrowDispenser.getRange()), 8, 43, 4210752);
      this.fontRenderer.drawString(count + ": " + this.toStr(this.TEArrowDispenser.getCount()), 8, 61, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(MULTICANNON_GUI_TEXTURES);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void initGui() {
      super.initGui();
      this.addButton(new ArrowButton(0, this.guiLeft + 100, this.guiTop + 22, 0, 0));
      this.addButton(new ArrowButton(1, this.guiLeft + 100, this.guiTop + 22 + 7, 9, 0));
      this.addButton(new ArrowButton(2, this.guiLeft + 100, this.guiTop + 40, 0, 0));
      this.addButton(new ArrowButton(3, this.guiLeft + 100, this.guiTop + 40 + 7, 9, 0));
      this.addButton(new ArrowButton(4, this.guiLeft + 100, this.guiTop + 58, 0, 0));
      this.addButton(new ArrowButton(5, this.guiLeft + 100, this.guiTop + 58 + 7, 9, 0));
   }

   public void actionPerformed(GuiButton button) {
      float Wert = 0.0F;
      int count = 0;
      switch(button.id) {
      case 0:
         if (this.TEArrowDispenser.getShootangle() < 45.5F) {
            Wert = this.TEArrowDispenser.getShootangle() + 1.0F;
         } else {
            Wert = this.TEArrowDispenser.getShootangle();
         }

         this.TEArrowDispenser.setShootangle(Wert);
         break;
      case 1:
         if (this.TEArrowDispenser.getShootangle() > 5.5F) {
            Wert = this.TEArrowDispenser.getShootangle() - 1.0F;
         } else {
            Wert = this.TEArrowDispenser.getShootangle();
         }

         this.TEArrowDispenser.setShootangle(Wert);
         break;
      case 2:
         if (this.TEArrowDispenser.getRange() < 0.8F) {
            Wert = this.TEArrowDispenser.getRange() + 0.1F;
         } else {
            Wert = this.TEArrowDispenser.getRange();
         }

         this.TEArrowDispenser.setRange(Wert);
         break;
      case 3:
         if ((double)this.TEArrowDispenser.getRange() > 0.1D) {
            Wert = this.TEArrowDispenser.getRange() - 0.1F;
         } else {
            Wert = this.TEArrowDispenser.getRange();
         }

         this.TEArrowDispenser.setRange(Wert);
         break;
      case 4:
         if (this.TEArrowDispenser.getCount() < 9) {
            count = this.TEArrowDispenser.getCount() + 1;
         } else {
            count = this.TEArrowDispenser.getCount();
         }

         this.TEArrowDispenser.setCount(count);
         break;
      case 5:
         if (this.TEArrowDispenser.getCount() > 1) {
            count = this.TEArrowDispenser.getCount() - 1;
         } else {
            count = this.TEArrowDispenser.getCount();
         }

         this.TEArrowDispenser.setCount(count);
      }

      if (button.id != 1 && button.id != 0) {
         if (button.id != 2 && button.id != 3) {
            if (button.id == 4 || button.id == 5) {
               Initial.network.sendToServer(new TEArrowDispenserCountPacket(this.TEArrowDispenser.getPos().getX(), this.TEArrowDispenser.getPos().getY(), this.TEArrowDispenser.getPos().getZ(), count));
            }
         } else {
            Initial.network.sendToServer(new TEArrowDispenserRangePacket(this.TEArrowDispenser.getPos().getX(), this.TEArrowDispenser.getPos().getY(), this.TEArrowDispenser.getPos().getZ(), Wert));
         }
      } else {
         Initial.network.sendToServer(new TEArrowDispenserShootAnglePacket(this.TEArrowDispenser.getPos().getX(), this.TEArrowDispenser.getPos().getY(), this.TEArrowDispenser.getPos().getZ(), Wert));
      }

   }

   public String toStr(int Int) {
      String str = "";
      str = String.valueOf(Int);
      return str;
   }

   public String FloatToStr(float flt) {
      String str = "";
      str = String.valueOf((float)((double)((int)(flt * 100.0F)) / 100.0D));
      return str;
   }
}
