package com.oblivioussp.spartanweaponry.client.gui;

import com.oblivioussp.spartanweaponry.client.KeyBinds;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HudQuiverAmmo extends HudElement {
   protected static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
   public static HudQuiverAmmo hudActive = null;
   protected ItemStack quiver = ItemStack.EMPTY;

   public HudQuiverAmmo(int elementWidth, int elementHeight, ItemStack stackQuiver) {
      super(elementWidth, elementHeight);
      this.quiver = stackQuiver;
   }

   public void setQuiver(ItemStack stackQuiver) {
      this.quiver = stackQuiver;
   }

   public void render() {
      Minecraft mc = Minecraft.getMinecraft();
      FontRenderer font = mc.fontRenderer;
      if (!this.quiver.isEmpty() && !mc.player.isSpectator()) {
         ScaledResolution res = new ScaledResolution(mc);
         AlignmentHelper.Alignment align = ConfigHandler.quiverHudAlignment;
         int totalAmmo = 0;
         String ammoStr = "";
         if (this.quiver.hasTagCompound()) {
            NBTTagList list = this.quiver.getTagCompound().getTagList("ClientInventory", 10);
            ItemStack stackToDraw = ItemStack.EMPTY;

            for(int i = 0; i < list.tagCount(); ++i) {
               ItemStack item = new ItemStack(list.getCompoundTagAt(i));
               if (stackToDraw.isEmpty()) {
                  stackToDraw = item.copy();
               }

               if (!item.isEmpty() && item.getCount() != 0) {
                  int ammo = item.getCount();
                  totalAmmo += ammo;
               }
            }

            ammoStr = String.format("%d", totalAmmo);
            int offsetX = this.getAlignedX(align, ConfigHandler.quiverHudOffsetX, res);
            int offsetY = this.getAlignedY(align, ConfigHandler.quiverHudOffsetY, res);
            GlStateManager.enableBlend();
            mc.getTextureManager().bindTexture(WIDGETS);
            mc.ingameGUI.drawTexturedModalRect(offsetX, offsetY, 24, 23, 22, 22);
            if (!this.quiver.isEmpty()) {
               mc.getRenderItem().renderItemAndEffectIntoGUI(this.quiver, offsetX + 3, offsetY + 3);
            }

            GlStateManager.disableDepth();
            font.drawStringWithShadow(ammoStr, (float)(offsetX + 20 - font.getStringWidth(ammoStr)), (float)(offsetY + 13), totalAmmo == 0 ? 16736352 : 16760832);
            if (!KeyBinds.KEY_ACCESS_QUIVER.getDisplayName().equals("NONE")) {
               String inventoryKey = "[" + KeyBinds.KEY_ACCESS_QUIVER.getDisplayName().toUpperCase() + "]";
               int keyWidth = font.getStringWidth(inventoryKey);
               AlignmentHelper.VerticalAlignment vert = align.getVertical();
               AlignmentHelper.HorizontalAlignment horiz = align.getHorizontal();
               int keyTextXOffset = horiz == AlignmentHelper.HorizontalAlignment.RIGHT && keyWidth > 19 ? -keyWidth + 22 : (horiz == AlignmentHelper.HorizontalAlignment.LEFT && keyWidth > 19 && offsetX < 19 ? 0 : 11 - (int)((float)keyWidth / 2.0F));
               int keyTextYOffset = align.getVertical() == AlignmentHelper.VerticalAlignment.TOP ? 22 : -8;
               font.drawStringWithShadow(inventoryKey, (float)(offsetX + keyTextXOffset), (float)(offsetY + keyTextYOffset), 16777215);
            }

            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
         }
      }
   }
}
