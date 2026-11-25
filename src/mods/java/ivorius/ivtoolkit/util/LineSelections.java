package ivorius.ivtoolkit.util;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class LineSelections {
   public LineSelections() {
   }

   public static LineSelection combine(Stream<LineSelection> selections, boolean additive) {
      LineSelection selection = new LineSelection(!additive);
      selections.forEach((s) -> {
         selection.set(s, additive, additive);
      });
      return selection;
   }
}
