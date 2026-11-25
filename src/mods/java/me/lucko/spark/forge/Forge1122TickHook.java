package me.lucko.spark.forge;

import me.lucko.spark.common.tick.AbstractTickHook;
import me.lucko.spark.common.tick.TickHook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;

public class Forge1122TickHook extends AbstractTickHook implements TickHook {
   private final Type type;

   public Forge1122TickHook(Type type) {
      this.type = type;
   }

   @SubscribeEvent
   public void onTick(TickEvent e) {
      if (e.phase == Phase.START) {
         if (e.type == this.type) {
            this.onTick();
         }
      }
   }

   public void start() {
      MinecraftForge.EVENT_BUS.register(this);
   }

   public void close() {
      MinecraftForge.EVENT_BUS.unregister(this);
   }
}
