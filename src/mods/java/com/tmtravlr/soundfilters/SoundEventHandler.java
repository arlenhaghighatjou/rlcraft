package com.tmtravlr.soundfilters;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import paulscode.sound.Source;

public class SoundEventHandler {
   public SoundEventHandler() {
   }

   @SubscribeEvent
   public void playSoundEventHandler(PlaySoundEvent event) {
      if (event.getSound().getAttenuationType() != AttenuationType.NONE) {
         this.addSourceToMap(event.getSound().getXPosF(), event.getSound().getYPosF(), event.getSound().getZPosF());
      }

   }

   private void addSourceToMap(float x, float y, float z) {
      Minecraft mc = Minecraft.getMinecraft();
      double amount = 0.0D;
      if (mc.world != null && mc.player != null) {
         amount = SoundTickHandler.getSoundOcclusion(mc.world, new Vec3d((double)x, (double)y, (double)z), new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ));
      }

      SoundTickHandler.sourceOcclusionMap.put(new SoundTickHandler.ComparablePosition(x, y, z), new SoundTickHandler.DoubleWithTimeout((Source)null, amount, 10));
   }
}
