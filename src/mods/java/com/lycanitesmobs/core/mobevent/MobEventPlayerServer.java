package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MobEventPlayerServer {
   public static boolean testOnCreative = false;
   public MobEvent mobEvent;
   public int ticks = 0;
   public World world;
   public long startedWorldTime = 0L;
   public EntityPlayer player;
   public BlockPos origin = new BlockPos(0, 0, 0);
   public int level = 1;
   public int variant = -1;
   public boolean extended = false;

   public MobEventPlayerServer(MobEvent mobEvent, World world) {
      this.mobEvent = mobEvent;
      this.world = world;
      if (world.isRemote) {
         LycanitesMobs.logWarning("", "Created a MobEventServer with a client side world, this shouldn't happen, things are going to get weird!");
      }

   }

   public void onStart() {
      this.startedWorldTime = this.world.getTotalWorldTime();
      this.ticks = 0;
      LycanitesMobs.logInfo("", "Mob Event " + (this.extended ? "Extended" : "Started") + ": " + this.mobEvent.getTitle() + " In Dimension: " + this.world.provider.getDimension() + " Duration: " + this.mobEvent.duration / 20 + "secs");
   }

   public void changeStartedWorldTime(long newStartedTime) {
      this.startedWorldTime = newStartedTime;
      LycanitesMobs.logInfo("", "Mob Event Start Time Changed: " + this.mobEvent.getTitle() + " In Dimension: " + this.world.provider.getDimension() + " Duration: " + this.mobEvent.duration / 20 + "secs Time Remaining: " + ((long)this.mobEvent.duration - (this.world.getTotalWorldTime() - this.startedWorldTime)) / 20L + "secs");
   }

   public void onFinish() {
      LycanitesMobs.logInfo("", "Mob Event Finished: " + this.mobEvent.getTitle());
   }

   public void onUpdate() {
      if (this.world == null) {
         LycanitesMobs.logWarning("", "MobEventBase was trying to update without a world object, stopped!");
      } else if (this.world.isRemote) {
         LycanitesMobs.logWarning("", "MobEventBase was trying to update with a client side world, stopped!");
      } else {
         this.mobEvent.onUpdate(this.world, this.player, this.origin, this.level, this.ticks, this.variant);
         ++this.ticks;
         if (this.ticks >= this.mobEvent.duration) {
            ExtendedWorld worldExt = ExtendedWorld.getForWorld(this.world);
            if ("world".equalsIgnoreCase(this.mobEvent.channel)) {
               worldExt.stopWorldEvent();
            } else {
               worldExt.stopMobEvent(this.mobEvent.name);
            }
         }

      }
   }
}
