package noppes.vc.client.gui;

import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import noppes.vc.client.Client;
import noppes.vc.constants.PacketServer;
import noppes.vc.containers.ContainerTradingBlock;
import noppes.vc.shared.gui.GuiBasic;
import noppes.vc.shared.gui.GuiButtonNop;
import noppes.vc.shared.gui.GuiContainerBasic;
import noppes.vc.shared.gui.GuiLabel;

public class GuiTradingBlock extends GuiContainerBasic implements GuiBasic.IGuiData {
   private final ResourceLocation resource = new ResourceLocation("variedcommodities", "textures/gui/tradingblock.png");
   public Map<Integer, ItemStack> items = new HashMap();
   private ContainerTradingBlock container;

   public GuiTradingBlock(ContainerTradingBlock container) {
      super(container);
      this.container = container;
      this.ySize = 230;
      this.closeOnEsc = true;
      container.tile.trader1 = this.player;
      container.tile.trader2 = null;
   }

   public void initGui() {
      super.initGui();
      GuiLabel label = null;
      if (this.container.tile.trader2 == null) {
         this.addLabel(label = new GuiLabel(0, "trader.waiting", this.guiLeft + 53, this.guiTop + 107));
      } else if (this.container.state != 0 && this.container.state != 1) {
         if (this.container.state == 3) {
            this.addLabel(label = new GuiLabel(0, "trader.complete", this.guiLeft + 53, this.guiTop + 107));
         } else if (this.container.state == 4) {
            this.addLabel(label = new GuiLabel(0, "trader.cancelled", this.guiLeft + 53, this.guiTop + 107));
         }
      } else {
         this.addButton(new GuiButtonNop(0, this.guiLeft + 53, this.guiTop + 102, 70, 20, "trader.trade"));
      }

      if (label != null) {
         label.center(70);
      }

   }

   public void actionPerformed(GuiButton guibutton) {
      if (guibutton.id == 0) {
         Client.sendData(PacketServer.TRADE_ACCEPT);
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   public void save() {
   }

   protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
      this.drawWorldBackground(0);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc = Minecraft.getMinecraft();
      this.mc.renderEngine.bindTexture(this.resource);
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      GuiInventory.drawEntityOnScreen(this.guiLeft + 34, this.guiTop + 94, 30, (float)(this.guiLeft + 33 - i), (float)(this.guiTop + 52 - j), this.mc.player);
      if (this.container.state != 3 && this.container.state != 4) {
         if (this.container.tile.trader2 != null) {
            GuiInventory.drawEntityOnScreen(this.guiLeft + 142, this.guiTop + 94, 30, (float)(this.guiLeft + 141 - i), (float)(this.guiTop + 52 - j), this.container.tile.trader2);
         }

         ItemStack hover = ItemStack.EMPTY;

         for(int k = 0; k < 9; ++k) {
            ItemStack itemstack = (ItemStack)this.items.get(Integer.valueOf(k));
            if (itemstack != null && !itemstack.isEmpty()) {
               int x = this.guiLeft + 8 + k % 5 * 18;
               int y = this.guiTop + 8 + k / 5 * 18;
               GlStateManager.enableRescaleNormal();
               RenderHelper.enableGUIStandardItemLighting();
               this.itemRender.renderItemAndEffectIntoGUI(itemstack, x, y);
               this.itemRender.renderItemOverlays(this.fontRenderer, itemstack, x, y);
               RenderHelper.disableStandardItemLighting();
               GlStateManager.disableRescaleNormal();
               if (this.isPointInRegion(x - this.guiLeft, y - this.guiTop, 16, 16, i, j)) {
                  hover = itemstack;
               }
            }
         }

         if (!hover.isEmpty()) {
            this.renderToolTip(hover, i, j);
         }

         if (this.container.state == 1) {
            this.drawSquare(this.guiLeft + 6, this.guiTop + 6, 164, 20, 2);
         }

         if (this.container.state == 2) {
            this.drawSquare(this.guiLeft + 6, this.guiTop + 123, 164, 20, 2);
         }

         super.drawGuiContainerBackgroundLayer(f, i, j);
      } else {
         super.drawGuiContainerBackgroundLayer(f, i, j);
      }
   }

   private void drawSquare(int x, int y, int width, int height, int thick) {
      drawRect(x, y, x + width, y + thick, -16711936);
      drawRect(x, y, x + thick, y + height, -16711936);
      drawRect(x, y + height, x + width, y + height - thick, -16711936);
      drawRect(x + width, y, x + width - thick, y + height, -16711936);
   }

   public void setGuiData(NBTTagCompound compound) {
      if (compound.hasKey("Player")) {
         this.items = new HashMap();
         String id = compound.getString("Player");
         if (id.isEmpty()) {
            this.container.tile.trader2 = null;
         } else {
            this.container.tile.trader2 = this.player.world.getPlayerEntityByUUID(EaglercraftUUID.fromString(id));
         }
      } else if (compound.hasKey("State")) {
         this.container.state = compound.getInteger("State");
      } else {
         this.items = ContainerTradingBlock.CompToItem(compound);
      }

      this.initGui();
   }
}
