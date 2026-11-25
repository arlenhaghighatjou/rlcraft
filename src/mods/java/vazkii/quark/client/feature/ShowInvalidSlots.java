package vazkii.quark.client.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class ShowInvalidSlots extends Feature {
   public static boolean requiresShift;
   public static double opacity;
   public static int alphaMask;

   public ShowInvalidSlots() {
   }

   public void setupConfig() {
      requiresShift = this.loadPropBool("Requires Shift", "Set this to true to only display the reds boxes when Shift is held", true);
      opacity = this.loadPropDouble("Opacity", "How opaque the overlay is", 0.3333333333333333D);
      int alphaAmount = (int)(opacity * 255.0D);
      alphaMask = alphaAmount << 24;
   }

   @SideOnly(Side.CLIENT)
   public static void renderElements(GuiContainer gui) {
      if (ModuleLoader.isFeatureEnabled(ShowInvalidSlots.class)) {
         if (!requiresShift || GuiScreen.isShiftKeyDown()) {
            Minecraft mc = Minecraft.getMinecraft();
            ItemStack stack = mc.player.inventory.getItemStack();
            Slot slotUnder = gui.getSlotUnderMouse();
            if (stack.isEmpty() && slotUnder != null) {
               stack = slotUnder.getStack();
            }

            if (stack.isEmpty()) {
               return;
            }

            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
            GlStateManager.disableDepth();

            for(Slot slot : gui.inventorySlots.inventorySlots) {
               if (slot != slotUnder && !slot.isItemValid(stack) && slot.isEnabled()) {
                  GlStateManager.pushMatrix();
                  GlStateManager.translate(0.0F, 0.0F, mc.getRenderItem().zLevel + 100.125F);
                  int x = slot.xPos;
                  int y = slot.yPos;
                  GlStateManager.colorMask(true, true, true, false);
                  Gui.drawRect(x, y, x + 16, y + 16, 16711680 | alphaMask);
                  GlStateManager.colorMask(true, true, true, true);
                  GlStateManager.popMatrix();
               }
            }

            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            GlStateManager.enableLighting();
         }

      }
   }
}
