package me.lucko.spark.forge;

import me.lucko.spark.common.tick.SimpleTickReporter;
import me.lucko.spark.common.tick.TickReporter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;

public class Forge1122TickReporter extends SimpleTickReporter implements TickReporter {
   private final Type type;

   public Forge1122TickReporter(Type type) {
      this.type = type;
   }

   @SubscribeEvent
   public void onTick(TickEvent e) {
      if (e.type == this.type) {
         switch(e.phase) {
         case START:
            this.onStart();
            break;
         case END:
            this.onEnd();
            break;
         default:
            throw new AssertionError(e.phase);
         }

      }
   }

   public void start() {
      MinecraftForge.EVENT_BUS.register(this);
   }

   public void close() {
      MinecraftForge.EVENT_BUS.unregister(this);
      super.close();
   }
}
