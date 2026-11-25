package reborncore.client.gui.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.api.tile.IUpgradeable;
import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.client.gui.builder.slot.GuiFluidConfiguration;
import reborncore.client.gui.builder.slot.GuiSlotConfiguration;
import reborncore.client.gui.builder.widget.GuiButtonHologram;
import reborncore.client.gui.builder.widget.GuiButtonPowerBar;
import reborncore.client.guibuilder.GuiBuilder;
import reborncore.common.container.RebornContainer;
import reborncore.common.tile.RebornMachineTile;
import reborncore.common.util.StringUtils;

public class GuiBase extends GuiContainer {
   public int xSize = 176;
   public int ySize = 176;
   public GuiBuilder builder = new GuiBuilder();
   public TileEntity tile;
   @Nullable
   public BuiltContainer container;
   public static GuiBase.SlotConfigType slotConfigType = GuiBase.SlotConfigType.NONE;
   public static ItemStack wrenchStack = ItemStack.EMPTY;
   public static GuiBase.FluidCellProvider fluidCellProvider = (fluid) -> {
      return (void)ItemStack.EMPTY;
   };
   public boolean upgrades;

   public GuiBase(EntityPlayer player, TileEntity tile, BuiltContainer container) {
      super(container);
      this.tile = tile;
      this.container = container;
      slotConfigType = GuiBase.SlotConfigType.NONE;
   }

   public GuiBase(EntityPlayer player, TileEntity tile, RebornContainer container) {
      super(container);
      this.tile = tile;
      this.container = null;
      slotConfigType = GuiBase.SlotConfigType.NONE;
   }

   protected void drawSlot(int x, int y, GuiBase.Layer layer) {
      if (layer == GuiBase.Layer.BACKGROUND) {
         x += this.guiLeft;
         y += this.guiTop;
      }

      this.builder.drawSlot(this, x - 1, y - 1);
   }

   protected void drawOutputSlotBar(int x, int y, int count, GuiBase.Layer layer) {
      if (layer == GuiBase.Layer.BACKGROUND) {
         x += this.guiLeft;
         y += this.guiTop;
      }

      this.builder.drawOutputSlotBar(this, x - 4, y - 4, count);
   }

   protected void drawArmourSlots(int x, int y, GuiBase.Layer layer) {
      if (layer == GuiBase.Layer.BACKGROUND) {
         x += this.guiLeft;
         y += this.guiTop;
      }

      this.builder.drawSlot(this, x - 1, y - 1);
      this.builder.drawSlot(this, x - 1, y - 1 + 18);
      this.builder.drawSlot(this, x - 1, y - 1 + 18 + 18);
      this.builder.drawSlot(this, x - 1, y - 1 + 18 + 18 + 18);
   }

   protected void drawOutputSlot(int x, int y, GuiBase.Layer layer) {
      if (layer == GuiBase.Layer.BACKGROUND) {
         x += this.guiLeft;
         y += this.guiTop;
      }

      this.builder.drawOutputSlot(this, x - 5, y - 5);
   }

   public void initGui() {
      super.initGui();
      if (this.isConfigEnabled()) {
         GuiSlotConfiguration.init(this);
      }

      if (this.isConfigEnabled() && this.getMachine().getTank() != null && this.getMachine().showTankConfig()) {
         GuiFluidConfiguration.init(this);
      }

   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.builder.drawDefaultBackground(this, this.guiLeft, this.guiTop, this.xSize, this.ySize);
      if (this.drawPlayerSlots()) {
         this.builder.drawPlayerSlots(this, this.guiLeft + this.xSize / 2, this.guiTop + 93, true);
      }

      if (this.tryAddUpgrades() && this.tile instanceof IUpgradeable) {
         IUpgradeable upgradeable = (IUpgradeable)this.tile;
         if (upgradeable.canBeUpgraded()) {
            this.builder.drawUpgrades(this, this.guiLeft - 24, this.guiTop + 6);
            this.upgrades = true;
         }
      }

      int offset = this.upgrades ? 86 : 6;
      if (this.isConfigEnabled() && this.getMachine().hasSlotConfig()) {
         this.builder.drawSlotTab(this, this.guiLeft - 24, this.guiTop + offset, wrenchStack);
      }

      if (this.isConfigEnabled() && this.getMachine().showTankConfig()) {
         this.builder.drawSlotTab(this, this.guiLeft - 24, this.guiTop + 24 + offset, fluidCellProvider.provide(FluidRegistry.LAVA));
      }

   }

   public boolean drawPlayerSlots() {
      return true;
   }

   public boolean tryAddUpgrades() {
      return true;
   }

   @SideOnly(Side.CLIENT)
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.buttonList.clear();
      this.drawTitle();
      if (this.isConfigEnabled() && slotConfigType == GuiBase.SlotConfigType.ITEMS && this.getMachine().hasSlotConfig()) {
         GuiSlotConfiguration.draw(this, mouseX, mouseY);
      }

      if (this.isConfigEnabled() && slotConfigType == GuiBase.SlotConfigType.FLUIDS && this.getMachine().showTankConfig()) {
         GuiFluidConfiguration.draw(this, mouseX, mouseY);
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   public void basicDrawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void renderHoveredToolTip(int mouseX, int mouseY) {
      if (this.isPointInRegion(-25, 6, 24, 80, mouseX, mouseY) && this.upgrades) {
         List<String> list = new ArrayList();
         list.add(StringUtils.t("reborncore.gui.tooltip.upgrades"));
         this.drawHoveringText(list, mouseX, mouseY);
         GlStateManager.disableLighting();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }

      int offset = this.upgrades ? 81 : 0;
      if (this.isConfigEnabled() && this.isPointInRegion(-26, 6 + offset, 24, 24, mouseX, mouseY) && this.getMachine().hasSlotConfig()) {
         List<String> list = new ArrayList();
         list.add(StringUtils.t("reborncore.gui.tooltip.config_slots"));
         this.drawHoveringText(list, mouseX, mouseY);
         GlStateManager.disableLighting();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (this.isConfigEnabled() && this.isPointInRegion(-26, 6 + offset + 25, 24, 24, mouseX, mouseY) && this.getMachine().showTankConfig()) {
         List<String> list = new ArrayList();
         list.add(StringUtils.t("reborncore.gui.tooltip.config_fluids"));
         this.drawHoveringText(list, mouseX, mouseY);
         GlStateManager.disableLighting();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }

      super.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void drawTitle() {
      this.drawCentredString(I18n.format(this.tile.getBlockType().getUnlocalizedName() + ".name", new Object[0]), 6, 4210752, GuiBase.Layer.FOREGROUND);
   }

   public void drawCentredString(String string, int y, int colour, GuiBase.Layer layer) {
      this.drawString(string, this.xSize / 2 - this.mc.fontRenderer.getStringWidth(string) / 2, y, colour, layer);
   }

   protected void drawCentredString(String string, int y, int colour, int modifier, GuiBase.Layer layer) {
      this.drawString(string, this.xSize / 2 - this.mc.fontRenderer.getStringWidth(string) / 2 + modifier, y, colour, layer);
   }

   public void drawString(String string, int x, int y, int colour, GuiBase.Layer layer) {
      int factorX = 0;
      int factorY = 0;
      if (layer == GuiBase.Layer.BACKGROUND) {
         factorX = this.guiLeft;
         factorY = this.guiTop;
      }

      this.mc.fontRenderer.drawString(string, x + factorX, y + factorY, colour);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void addPowerButton(int x, int y, int id, GuiBase.Layer layer) {
      int factorX = 0;
      int factorY = 0;
      if (layer == GuiBase.Layer.BACKGROUND) {
         factorX = this.guiLeft;
         factorY = this.guiTop;
      }

      this.buttonList.add(new GuiButtonPowerBar(id, x + factorX, y + factorY, this, layer));
   }

   public void addHologramButton(int x, int y, int id, GuiBase.Layer layer) {
      int factorX = 0;
      int factorY = 0;
      if (layer == GuiBase.Layer.BACKGROUND) {
         factorX = this.guiLeft;
         factorY = this.guiTop;
      }

      this.buttonList.add(new GuiButtonHologram(id, x + factorX, y + factorY, this, layer));
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (!this.isConfigEnabled() || slotConfigType != GuiBase.SlotConfigType.ITEMS || !this.getMachine().hasSlotConfig() || !GuiSlotConfiguration.mouseClicked(mouseX, mouseY, mouseButton, this)) {
         if (!this.isConfigEnabled() || slotConfigType != GuiBase.SlotConfigType.FLUIDS || !this.getMachine().showTankConfig() || !GuiFluidConfiguration.mouseClicked(mouseX, mouseY, mouseButton, this)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
         }
      }
   }

   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      if (this.isConfigEnabled() && slotConfigType == GuiBase.SlotConfigType.ITEMS && this.getMachine().hasSlotConfig()) {
         GuiSlotConfiguration.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, this);
      }

      if (this.isConfigEnabled() && slotConfigType == GuiBase.SlotConfigType.FLUIDS && this.getMachine().showTankConfig()) {
         GuiFluidConfiguration.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, this);
      }

      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      int offset = 0;
      if (!this.upgrades) {
         offset = 80;
      }

      if (this.isConfigEnabled() && this.isPointInRegion(-26, 84 - offset, 30, 30, mouseX, mouseY) && this.getMachine().hasSlotConfig()) {
         if (slotConfigType != GuiBase.SlotConfigType.ITEMS) {
            slotConfigType = GuiBase.SlotConfigType.ITEMS;
         } else {
            slotConfigType = GuiBase.SlotConfigType.NONE;
         }

         if (slotConfigType == GuiBase.SlotConfigType.ITEMS) {
            GuiSlotConfiguration.reset();
         }
      }

      if (this.isConfigEnabled() && this.isPointInRegion(-26, 84 - offset + 27, 30, 30, mouseX, mouseY) && this.getMachine().hasSlotConfig()) {
         if (slotConfigType != GuiBase.SlotConfigType.FLUIDS) {
            slotConfigType = GuiBase.SlotConfigType.FLUIDS;
         } else {
            slotConfigType = GuiBase.SlotConfigType.NONE;
         }
      }

      if (!this.isConfigEnabled() || slotConfigType != GuiBase.SlotConfigType.ITEMS || !this.getMachine().hasSlotConfig() || !GuiSlotConfiguration.mouseReleased(mouseX, mouseY, state, this)) {
         if (!this.isConfigEnabled() || slotConfigType != GuiBase.SlotConfigType.FLUIDS || !this.getMachine().showTankConfig() || !GuiFluidConfiguration.mouseReleased(mouseX, mouseY, state, this)) {
            super.mouseReleased(mouseX, mouseY, state);
         }
      }
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (this.isConfigEnabled() && slotConfigType == GuiBase.SlotConfigType.ITEMS) {
         if (isCtrlKeyDown() && keyCode == 46) {
            GuiSlotConfiguration.copyToClipboard();
            return;
         }

         if (isCtrlKeyDown() && keyCode == 47) {
            GuiSlotConfiguration.pasteFromClipboard();
            return;
         }
      }

      super.keyTyped(typedChar, keyCode);
   }

   public void onGuiClosed() {
      slotConfigType = GuiBase.SlotConfigType.NONE;
      super.onGuiClosed();
   }

   @Nullable
   public RebornMachineTile getMachine() {
      return (RebornMachineTile)this.tile;
   }

   public boolean isPointInRect(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
      return super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
   }

   public boolean isConfigEnabled() {
      return this.tile instanceof RebornMachineTile && this.container != null;
   }

   public interface FluidCellProvider {
      ItemStack provide(Fluid var1);
   }

   public static enum Layer {
      BACKGROUND,
      FOREGROUND;

      private Layer() {
      }
   }

   public static enum SlotConfigType {
      NONE,
      ITEMS,
      FLUIDS;

      private SlotConfigType() {
      }
   }
}
