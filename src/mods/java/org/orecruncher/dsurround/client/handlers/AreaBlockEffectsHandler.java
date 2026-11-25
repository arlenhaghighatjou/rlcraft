package org.orecruncher.dsurround.client.handlers;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.handlers.scanners.AlwaysOnBlockEffectScanner;
import org.orecruncher.dsurround.client.handlers.scanners.ClientPlayerLocus;
import org.orecruncher.dsurround.client.handlers.scanners.RandomBlockEffectScanner;

@SideOnly(Side.CLIENT)
public final class AreaBlockEffectsHandler extends EffectHandlerBase {
   protected final ClientPlayerLocus locus = new ClientPlayerLocus();
   protected final RandomBlockEffectScanner nearEffects;
   protected final RandomBlockEffectScanner farEffects;
   protected final AlwaysOnBlockEffectScanner alwaysOn;

   public AreaBlockEffectsHandler() {
      super("Area Block Effects");
      this.nearEffects = new RandomBlockEffectScanner(this.locus, 16);
      this.farEffects = new RandomBlockEffectScanner(this.locus, 32);
      this.alwaysOn = new AlwaysOnBlockEffectScanner(this.locus, ModOptions.effects.specialEffectRange);
   }

   public void process(@Nonnull EntityPlayer player) {
      this.nearEffects.update();
      this.farEffects.update();
      this.alwaysOn.update();
   }

   public void onConnect() {
      MinecraftForge.EVENT_BUS.register(this.alwaysOn);
   }

   public void onDisconnect() {
      MinecraftForge.EVENT_BUS.unregister(this.alwaysOn);
   }
}
