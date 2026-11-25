package slimeknights.mantle.property;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertyUnlistedDirection extends PropertyDirection implements IUnlistedProperty<EnumFacing> {
   public PropertyUnlistedDirection(String name, Collection<EnumFacing> values) {
      super(name, values);
   }

   public PropertyUnlistedDirection(String name, Predicate<EnumFacing> filter) {
      this(name, Collections2.filter(Lists.newArrayList(EnumFacing.values()), filter));
   }

   public boolean isValid(EnumFacing value) {
      return true;
   }

   public Class<EnumFacing> getType() {
      return this.getValueClass();
   }

   public String valueToString(EnumFacing value) {
      return this.getName(value);
   }
}
