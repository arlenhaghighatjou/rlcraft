package org.orecruncher.dsurround.mixins;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({GuiScreenOptionsSounds.class})
public class MixinGuiScreenOptionsSounds extends GuiScreen {
   public MixinGuiScreenOptionsSounds() {
   }

   @ModifyConstant(
      method = {"initGui()V"},
      constant = {@Constant(
   intValue = 24
)}
   )
   public int getHeight(int v) {
      return v == 24 ? 22 : v;
   }
}
