package vazkii.quark.management.client.gui;

import java.awt.Color;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.math.BlockPos;

public class GuiButtonShulker extends GuiButtonChest {
   public GuiButtonShulker(GuiShulkerBox parent, GuiButtonChest.Action action, int id, int par2, int par3, int left, int top) {
      super(parent, action, id, par2, par3, left, top);
   }

   protected void drawChest() {
      Minecraft mc = Minecraft.getMinecraft();
      BlockPos pos = mc.objectMouseOver.getBlockPos();
      if (pos != null) {
         TileEntity tile = mc.world.getTileEntity(pos);
         if (tile instanceof TileEntityShulkerBox) {
            TileEntityShulkerBox shulker = (TileEntityShulkerBox)tile;
            EnumDyeColor dye = ((BlockShulkerBox)shulker.getBlockType()).getColor();
            int color = ItemDye.DYE_COLORS[dye.getDyeDamage()];
            Color colorObj = (new Color(color)).brighter();
            GlStateManager.color((float)colorObj.getRed() / 255.0F, (float)colorObj.getGreen() / 255.0F, (float)colorObj.getBlue() / 255.0F);
            super.drawIcon(16, 128);
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            return;
         }
      }

      super.drawChest();
   }
}
