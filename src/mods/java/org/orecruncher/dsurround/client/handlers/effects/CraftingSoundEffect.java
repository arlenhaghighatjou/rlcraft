package org.orecruncher.dsurround.client.handlers.effects;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.effects.EventEffect;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.sound.ISoundInstance;
import org.orecruncher.dsurround.client.sound.Sounds;

@SideOnly(Side.CLIENT)
public class CraftingSoundEffect extends EventEffect {
   private int craftSoundThrottle = 0;

   public CraftingSoundEffect() {
   }

   @SubscribeEvent
   public void onEvent(@Nonnull ItemCraftedEvent event) {
      if (ModOptions.sound.enableCraftingSound && this.isClientValid(event)) {
         if (this.craftSoundThrottle < EnvironStateHandler.EnvironState.getTickCounter() - 30) {
            this.craftSoundThrottle = EnvironStateHandler.EnvironState.getTickCounter();
            ISoundInstance fx = this.getState().createSound(Sounds.CRAFTING, event.player);
            this.getState().playSound(fx);
         }
      }
   }
}
