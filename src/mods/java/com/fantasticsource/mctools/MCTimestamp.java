package com.fantasticsource.mctools;

import com.fantasticsource.tools.Timestamp;
import java.time.Instant;
import net.minecraft.world.World;

public class MCTimestamp extends Timestamp {
   public final long serverTick;
   public final long clientTick;
   public final long worldTick;

   public MCTimestamp(World world) {
      super(Instant.now());
      this.serverTick = ServerTickTimer.currentTick();
      this.clientTick = ClientTickTimer.currentTick();
      this.worldTick = world.getWorldTime();
   }

   public MCTimestamp(Instant instant, long serverTick, long clientTick, long worldTick) {
      super(instant);
      this.serverTick = serverTick;
      this.clientTick = clientTick;
      this.worldTick = worldTick;
   }

   public int getGameDayAbsolute() {
      return (int)(this.worldTick / 24000L);
   }

   public int getGameTimeOfDayAbsolute() {
      return (int)(this.worldTick % 24000L);
   }

   public int getGameYear() {
      return this.getGameDayAbsolute() / 360 + 1;
   }

   public int getGameSeason() {
      return this.getGameDayAbsolute() % 360 / 90 + 1;
   }

   public int getGameMonth() {
      return this.getGameDayAbsolute() % 360 / 30 + 1;
   }

   public int getGameDay() {
      return this.getGameDayAbsolute() % 30 + 1;
   }

   public int getGameHour() {
      return (int)(this.worldTick % 24000L / 1000L);
   }

   public int getGameMinute() {
      return (int)((double)(this.worldTick % 1000L) / 16.6666666667D);
   }

   public int getGameSecond() {
      return (int)((double)this.worldTick % 16.6666666667D / 0.27777777777D);
   }
}
