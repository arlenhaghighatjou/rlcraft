package com.n247s.api.eventapi.eventsystem;

import com.n247s.api.eventapi.EventApi;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CustomEventSubscribe {
   CustomEventSubscribe.Priority eventPriority() default CustomEventSubscribe.Priority.Normal;

   public static enum Priority {
      Highest,
      High,
      Normal,
      Low,
      Lowest;

      private Priority() {
      }

      public static CustomEventSubscribe.Priority getPriorityInOrder(int i) {
         switch(i) {
         case 0:
            return Highest;
         case 1:
            return High;
         case 2:
            return Normal;
         case 3:
            return Low;
         case 4:
            return Lowest;
         default:
            EventApi.logger.catching(new IllegalArgumentException("Priority Number Can't be lower than 0, or higher than 4!"));
            return null;
         }
      }
   }
}
