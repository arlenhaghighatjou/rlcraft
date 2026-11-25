package vazkii.quark.client.feature;

import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;
import vazkii.quark.client.gui.GuiBetterEditSign;

public class ImprovedSignEdit extends Feature {
   public static boolean enableCancel;
   public static boolean enableClear;
   public static boolean enableShift;

   public ImprovedSignEdit() {
   }

   public void setupConfig() {
      enableCancel = this.loadPropBool("Enable Cancel Button", "", true);
      enableClear = this.loadPropBool("Enable Clear Button", "", true);
      enableShift = this.loadPropBool("Enable Shift Button", "", true);
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onOpenGUI(GuiOpenEvent event) {
      if (event.getGui() instanceof GuiEditSign) {
         TileEntitySign sign = (TileEntitySign)ObfuscationReflectionHelper.getPrivateValue(GuiEditSign.class, (GuiEditSign)event.getGui(), "tileSign");
         event.setGui(new GuiBetterEditSign(sign));
      }

   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }
}
