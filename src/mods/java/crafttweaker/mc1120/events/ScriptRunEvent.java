package crafttweaker.mc1120.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ScriptRunEvent extends Event {
   public ScriptRunEvent() {
   }

   public static class Post extends ScriptRunEvent {
      public Post() {
      }
   }

   public static class Pre extends ScriptRunEvent {
      public Pre() {
      }
   }
}
