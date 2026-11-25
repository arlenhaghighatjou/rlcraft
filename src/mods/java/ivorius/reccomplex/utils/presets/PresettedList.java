package ivorius.reccomplex.utils.presets;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Nonnull;

public class PresettedList<T> extends PresettedObject<ArrayList<T>> {
   public PresettedList(@Nonnull PresetRegistry<ArrayList<T>> presetRegistry, String preset) {
      super(presetRegistry, preset);
   }

   @SafeVarargs
   public final void setContents(T... ts) {
      super.setContents(Lists.newArrayList(ts));
   }

   public void setContents(Collection<T> ts) {
      super.setContents(Lists.newArrayList(ts));
   }
}
