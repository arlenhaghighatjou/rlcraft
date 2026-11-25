package reborncore.client.gui.builder.widget;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.math.BlockPos;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.multiblock.Multiblock;

public class GuiButtonHologram extends GuiButton {
   GuiBase.Layer layer;
   GuiBase gui;

   public GuiButtonHologram(int buttonId, int x, int y, GuiBase gui, GuiBase.Layer layer) {
      super(buttonId, x, y, 20, 12, "");
      this.layer = layer;
      this.gui = gui;
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (this.layer == GuiBase.Layer.FOREGROUND) {
         mouseX -= this.gui.getGuiLeft();
         mouseY -= this.gui.getGuiTop();
      }

      return this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
   }

   public void addComponent(int x, int y, int z, IBlockState blockState, Multiblock multiblock) {
      multiblock.addComponent(new BlockPos(x, y, z), blockState);
   }

   public void drawButtonForegroundLayer(int mouseX, int mouseY) {
   }

   public void drawButton(Minecraft p_191745_1_, int p_191745_2_, int p_191745_3_, float p_191745_4_) {
   }
}
