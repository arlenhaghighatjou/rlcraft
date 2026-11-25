package ivorius.reccomplex.world.gen.feature;

import ivorius.reccomplex.RecurrentComplex;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiConsumer;

public class RCWorldgenMonitor {
   protected static final Deque<String> actions = new ArrayDeque();

   public RCWorldgenMonitor() {
   }

   public static void start(String action) {
      actions.push(action);
   }

   public static void stop() {
      actions.pop();
   }

   public static void create() {
      WorldgenMonitor.create("Recurrent Complex", (p, d) -> {
         if (actions.size() > 0) {
            RecurrentComplex.logger.warn("Cascading chunk generation happening while " + (String)actions.peek());
         }

      });
   }
}
