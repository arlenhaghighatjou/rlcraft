package reborncore.client.gui.builder.slot.elements;

import java.awt.Color;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.FMLClientHandler;
import reborncore.RebornCore;
import reborncore.client.gui.GuiUtil;
import reborncore.client.gui.builder.GuiBase;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.packet.PacketIOSave;
import reborncore.common.network.packet.PacketSlotSave;
import reborncore.common.tile.RebornMachineTile;
import reborncore.common.tile.SlotConfiguration;
import reborncore.common.util.MachineFacing;

public class SlotConfigPopupElement extends ElementBase {
   int id;
   public boolean filter = false;
   ConfigSlotElement slotElement;
   boolean allowInput = true;

   public SlotConfigPopupElement(int slotId, int x, int y, ConfigSlotElement slotElement, boolean allowInput) {
      super(x, y, Sprite.SLOT_CONFIG_POPUP);
      this.id = slotId;
      this.slotElement = slotElement;
      this.allowInput = allowInput;
   }

   public void draw(GuiBase gui) {
      this.drawDefaultBackground(gui, this.adjustX(gui, this.getX() - 8), this.adjustY(gui, this.getY() - 7), 84, 105 + (this.filter ? 15 : 0));
      super.draw(gui);
      RebornMachineTile machine = (RebornMachineTile)gui.tile;
      IBlockAccess blockAccess = machine.getWorld();
      BlockPos pos = machine.getPos();
      IBlockState state = blockAccess.getBlockState(pos);
      IBlockState actualState = state.getBlock().getDefaultState().getActualState(blockAccess, pos);
      BlockRendererDispatcher dispatcher = FMLClientHandler.instance().getClient().getBlockRendererDispatcher();
      IBakedModel model = dispatcher.getBlockModelShapes().getModelForState(state.getBlock().getDefaultState());
      FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      this.drawState(gui, blockAccess, model, actualState, pos, dispatcher, 4, 23);
      this.drawState(gui, blockAccess, model, actualState, pos, dispatcher, 23, -12, -90.0F, 1.0F, 0.0F, 0.0F);
      this.drawState(gui, blockAccess, model, actualState, pos, dispatcher, 23, 23, -90.0F, 0.0F, 1.0F, 0.0F);
      this.drawState(gui, blockAccess, model, actualState, pos, dispatcher, 23, 42, 90.0F, 1.0F, 0.0F, 0.0F);
      this.drawState(gui, blockAccess, model, actualState, pos, dispatcher, 26, 23, 180.0F, 0.0F, 1.0F, 0.0F);
      this.drawState(gui, blockAccess, model, actualState, pos, dispatcher, 26, 42, 90.0F, 0.0F, 1.0F, 0.0F);
      this.drawSlotSateColor(gui.getMachine(), MachineFacing.UP.getFacing(machine), this.id, 22, -1, gui);
      this.drawSlotSateColor(gui.getMachine(), MachineFacing.FRONT.getFacing(machine), this.id, 22, 18, gui);
      this.drawSlotSateColor(gui.getMachine(), MachineFacing.DOWN.getFacing(machine), this.id, 22, 37, gui);
      this.drawSlotSateColor(gui.getMachine(), MachineFacing.RIGHT.getFacing(machine), this.id, 41, 18, gui);
      this.drawSlotSateColor(gui.getMachine(), MachineFacing.BACK.getFacing(machine), this.id, 41, 37, gui);
      this.drawSlotSateColor(gui.getMachine(), MachineFacing.LEFT.getFacing(machine), this.id, 3, 18, gui);
   }

   public boolean onRelease(RebornMachineTile provider, GuiBase gui, int mouseX, int mouseY) {
      if (this.isInBox(23, 4, 16, 16, mouseX, mouseY, gui)) {
         this.cyleSlotConfig(MachineFacing.UP.getFacing(provider), gui);
      } else if (this.isInBox(23, 23, 16, 16, mouseX, mouseY, gui)) {
         this.cyleSlotConfig(MachineFacing.FRONT.getFacing(provider), gui);
      } else if (this.isInBox(42, 23, 16, 16, mouseX, mouseY, gui)) {
         this.cyleSlotConfig(MachineFacing.RIGHT.getFacing(provider), gui);
      } else if (this.isInBox(4, 23, 16, 16, mouseX, mouseY, gui)) {
         this.cyleSlotConfig(MachineFacing.LEFT.getFacing(provider), gui);
      } else if (this.isInBox(23, 42, 16, 16, mouseX, mouseY, gui)) {
         this.cyleSlotConfig(MachineFacing.DOWN.getFacing(provider), gui);
      } else {
         if (!this.isInBox(42, 42, 16, 16, mouseX, mouseY, gui)) {
            return false;
         }

         this.cyleSlotConfig(MachineFacing.BACK.getFacing(provider), gui);
      }

      return true;
   }

   public void cyleSlotConfig(EnumFacing side, GuiBase guiBase) {
      SlotConfiguration.SlotConfig currentSlot = guiBase.getMachine().slotConfiguration.getSlotDetails(this.id).getSideDetail(side);
      SlotConfiguration.ExtractConfig nextConfig = currentSlot.getSlotIO().getIoConfig().getNext();
      if (!this.allowInput && nextConfig == SlotConfiguration.ExtractConfig.INPUT) {
         nextConfig = SlotConfiguration.ExtractConfig.OUTPUT;
      }

      SlotConfiguration.SlotIO slotIO = new SlotConfiguration.SlotIO(nextConfig);
      SlotConfiguration.SlotConfig newConfig = new SlotConfiguration.SlotConfig(side, slotIO, this.id);
      PacketSlotSave packetSlotSave = new PacketSlotSave(guiBase.tile.getPos(), newConfig);
      NetworkManager.sendToServer(packetSlotSave);
   }

   public void updateCheckBox(CheckBoxElement checkBoxElement, String type, GuiBase guiBase) {
      SlotConfiguration.SlotConfigHolder configHolder = guiBase.getMachine().slotConfiguration.getSlotDetails(this.id);
      boolean input = configHolder.autoInput();
      boolean output = configHolder.autoOutput();
      boolean filter = configHolder.filter();
      if (type.equalsIgnoreCase("input")) {
         input = !configHolder.autoInput();
      }

      if (type.equalsIgnoreCase("output")) {
         output = !configHolder.autoOutput();
      }

      if (type.equalsIgnoreCase("filter")) {
         filter = !configHolder.filter();
      }

      PacketIOSave packetSlotSave = new PacketIOSave(guiBase.tile.getPos(), this.id, input, output, filter);
      NetworkManager.sendToServer(packetSlotSave);
   }

   private void drawSlotSateColor(RebornMachineTile machineBase, EnumFacing side, int slotID, int inx, int iny, GuiBase gui) {
      iny = iny + 4;
      int sx = inx + this.getX() + gui.guiLeft;
      int sy = iny + this.getY() + gui.guiTop;
      SlotConfiguration.SlotConfigHolder slotConfigHolder = machineBase.slotConfiguration.getSlotDetails(slotID);
      if (slotConfigHolder == null) {
         RebornCore.logHelper.debug("Humm, this isnt suppoed to happen");
      } else {
         SlotConfiguration.SlotConfig slotConfig = slotConfigHolder.getSideDetail(side);
         Color color;
         switch(slotConfig.getSlotIO().getIoConfig()) {
         case NONE:
            color = new Color(0, 0, 0, 0);
            break;
         case INPUT:
            color = new Color(0, 0, 255, 128);
            break;
         case OUTPUT:
            color = new Color(255, 69, 0, 128);
            break;
         default:
            color = new Color(0, 0, 0, 0);
         }

         GlStateManager.color(255.0F, 255.0F, 255.0F);
         GuiUtil.drawGradientRect(sx, sy, 18, 18, color.getRGB(), color.getRGB());
         GlStateManager.color(255.0F, 255.0F, 255.0F);
      }
   }

   private boolean isInBox(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY, GuiBase guiBase) {
      rectX = rectX + this.getX();
      rectY = rectY + this.getY();
      return this.isInRect(guiBase, rectX, rectY, rectWidth, rectHeight, pointX, pointY);
   }

   public void drawState(GuiBase gui, IBlockAccess blockAccess, IBakedModel model, IBlockState actualState, BlockPos pos, BlockRendererDispatcher dispatcher, int x, int y, float rotAngle, float rotX, float rotY, float rotZ) {
      GlStateManager.pushMatrix();
      GlStateManager.enableDepth();
      GlStateManager.translate((float)(8 + gui.guiLeft + this.x + x), (float)(8 + gui.guiTop + this.y + y), 512.0F);
      GlStateManager.scale(16.0F, 16.0F, 16.0F);
      GlStateManager.translate(0.5F, 0.5F, 0.5F);
      GlStateManager.scale(-1.0F, -1.0F, -1.0F);
      if (rotAngle != 0.0F) {
         GlStateManager.rotate(rotAngle, rotX, rotY, rotZ);
      }

      dispatcher.getBlockModelRenderer().renderModelBrightness(model, actualState, 1.0F, false);
      GlStateManager.disableDepth();
      GlStateManager.popMatrix();
   }

   public void drawState(GuiBase gui, IBlockAccess blockAccess, IBakedModel model, IBlockState actualState, BlockPos pos, BlockRendererDispatcher dispatcher, int x, int y) {
      this.drawState(gui, blockAccess, model, actualState, pos, dispatcher, x, y, 0.0F, 0.0F, 0.0F, 0.0F);
   }
}
