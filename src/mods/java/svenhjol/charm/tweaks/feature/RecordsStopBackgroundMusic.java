package svenhjol.charm.tweaks.feature;

import java.util.function.BiConsumer;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.meson.Feature;
import svenhjol.meson.Meson;
import svenhjol.meson.helper.SoundHelper;

public class RecordsStopBackgroundMusic extends Feature {
   public static int stopAfterTicks;
   private int ticks;
   private ISound currentMusic = null;

   public RecordsStopBackgroundMusic() {
   }

   public String getDescription() {
      return "Stops the background music when playing a record in a jukebox to avoid a horrible cacophony.";
   }

   public void configure() {
      super.configure();
      stopAfterTicks = 10;
   }

   @SubscribeEvent
   public void onBlockInteract(RightClickBlock event) {
      if (event.getWorld().isRemote) {
         IBlockState state = event.getWorld().getBlockState(event.getPos());
         if (event.getEntity() instanceof EntityPlayer && event.getItemStack().getItem() instanceof ItemRecord && state.getBlock() == Blocks.JUKEBOX && !((Boolean)state.getValue(BlockJukebox.HAS_RECORD)).booleanValue()) {
            SoundHelper.getPlayingSounds().forEach((id, sound) -> {
               if (sound.getCategory() == SoundCategory.MUSIC) {
                  Meson.debug("Music playing before record started");
                  this.currentMusic = sound;
               }

            });
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onSoundPlay(SoundSourceEvent event) {
      ISound triggeredSound = event.getSound();
      if (triggeredSound.getCategory() == SoundCategory.MUSIC) {
         SoundHelper.getPlayingSounds().forEach((id, sound) -> {
            if (sound.getCategory() == SoundCategory.RECORDS) {
               Meson.debug("Music tried to start while record playing");
               this.currentMusic = triggeredSound;
            }

         });
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onTick(ClientTickEvent event) {
      if (event.phase == Phase.END && this.currentMusic != null) {
         if (++this.ticks % stopAfterTicks > 0) {
            return;
         }

         Meson.debug("Stopping the music");
         SoundHelper.getSoundManager().stopSound(this.currentMusic);
         this.ticks = 0;
         this.currentMusic = null;
      }

   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }
}
