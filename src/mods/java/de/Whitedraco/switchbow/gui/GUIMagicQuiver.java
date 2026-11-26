package de.Whitedraco.switchbow.gui;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIMagicQuiver extends GuiContainer {
   private static final ResourceLocation dispenserGuiTextures = new ResourceLocation("switchbow".toLowerCase(), "textures/gui/container/magicquiver.png");
   private static final String __OBFID = "CL_00000765";

   public GUIMagicQuiver(EntityPlayer p_i1098_1_, ItemStack koecheritem) {
      super(new ContainerMagicQuiver(p_i1098_1_, koecheritem));
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      String s = I18n.translateToLocal("item.MagicQuiver.name");
      this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
      this.fontRenderer.drawString(net.minecraft.client.resources.I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(dispenserGuiTextures);
      int k = (this.width - this.xSize) / 2;
      int l = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }
}
