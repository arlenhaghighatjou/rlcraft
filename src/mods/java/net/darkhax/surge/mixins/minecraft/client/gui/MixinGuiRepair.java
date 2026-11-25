package net.darkhax.surge.mixins.minecraft.client.gui;

import net.darkhax.surge.core.SurgeConfiguration;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiRepair.class})
public class MixinGuiRepair {
   @Shadow
   private GuiTextField nameField;

   public MixinGuiRepair() {
   }

   @Inject(
      method = {"initGui"},
      at = {@At("RETURN")}
   )
   private void initGui(CallbackInfo ci) {
      this.nameField.setMaxStringLength(SurgeConfiguration.maxRenameLength);
   }
}
