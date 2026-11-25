package crafttweaker.mc1120.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ActionApplyEvent extends Event {
   public ActionApplyEvent() {
   }

   public static class Post extends ActionApplyEvent {
      public Post() {
      }
   }

   public static class Pre extends ActionApplyEvent {
      public Pre() {
      }
   }
}
