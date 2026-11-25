package com.tmtravlr.qualitytools.reforging;

import com.tmtravlr.qualitytools.QualityToolsMod;
import com.tmtravlr.qualitytools.network.CToSMessage;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiReforgingStation extends GuiContainer {
   private static final ResourceLocation REFORGING_STATION_GUI_TEXTURES = new ResourceLocation("qualitytools", "textures/gui/container/reforging_station.png");
   private final InventoryPlayer playerInventory;
   private final TileEntityReforgingStation tileReforgingStation;
   private GuiButtonReforgingStation reforgeButton;

   public GuiReforgingStation(InventoryPlayer playerInvIn, TileEntityReforgingStation stationInvIn) {
      super(new ContainerReforgingStation(playerInvIn, stationInvIn));
      this.playerInventory = playerInvIn;
      this.tileReforgingStation = stationInvIn;
   }

   public void initGui() {
      super.initGui();
      this.reforgeButton = (GuiButtonReforgingStation)this.addButton(new GuiButtonReforgingStation(0, 80, 39));
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String s = this.tileReforgingStation.getDisplayName().getUnformattedText();
      this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
      this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(REFORGING_STATION_GUI_TEXTURES);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      int canReforge = this.tileReforgingStation.getField(0);
      this.reforgeButton.enabled = canReforge > 0;
      this.reforgeButton.updatePos(i, j);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         PacketBuffer out = new PacketBuffer(Unpooled.buffer());
         out.writeInt(1);
         out.writeBlockPos(this.tileReforgingStation.getPos());
         out.writeInt(this.tileReforgingStation.getWorld() == null ? 0 : this.tileReforgingStation.getWorld().provider.getDimension());
         CToSMessage packet = new CToSMessage(out);
         QualityToolsMod.networkWrapper.sendToServer(packet);
      }

   }
}
