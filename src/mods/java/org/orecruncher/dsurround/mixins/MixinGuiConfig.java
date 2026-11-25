package org.orecruncher.dsurround.mixins;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.gui.GuiFilteredConfigEntries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({GuiConfig.class})
public class MixinGuiConfig {
   public MixinGuiConfig() {
   }

   @Redirect(
      method = {"*"},
      at = @At(
   value = "NEW",
   target = "(Lnet/minecraftforge/fml/client/config/GuiConfig;Lnet/minecraft/client/Minecraft;)Lnet/minecraftforge/fml/client/config/GuiConfigEntries;"
)
   )
   public GuiConfigEntries constructEntries(GuiConfig parent, Minecraft mc) {
      return (GuiConfigEntries)(ModOptions.asm.enableOptionSearchASM ? new GuiFilteredConfigEntries(parent, mc) : new GuiConfigEntries(parent, mc));
   }
}
