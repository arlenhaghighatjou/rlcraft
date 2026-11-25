package reborncore.common.blocks;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.block.properties.PropertyHelper;

public class PropertyString extends PropertyHelper<String> {
   private final LinkedList<String> valuesSet;

   public PropertyString(String name, Collection<String> values) {
      super(name, String.class);
      List<String> strList = new ArrayList();
      values.forEach((s) -> {
         strList.add(s.intern());
      });
      this.valuesSet = new LinkedList(strList);
   }

   public PropertyString(String name, String... values) {
      super(name, String.class);
      this.valuesSet = new LinkedList();
      Arrays.stream(values).forEach((s) -> {
         this.valuesSet.add(s.intern());
      });
   }

   public Collection<String> getAllowedValues() {
      return ImmutableSet.copyOf(this.valuesSet);
   }

   public Optional<String> parseValue(String value) {
      return this.valuesSet.contains(value.intern()) ? Optional.of(value.intern()) : Optional.absent();
   }

   public String getName(String value) {
      return value.intern();
   }
}
