package ivorius.reccomplex.utils.accessor;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraftforge.common.BiomeDictionary.Type;

public class RCAccessorBiomeDictionary {
   public RCAccessorBiomeDictionary() {
   }

   public static Type getTypeWeak(String var) {
      return (Type)getMap().get(var.toUpperCase());
   }

   public static Map<String, Type> getMap() {
      return (Map)SafeReflector.get(Type.class, (Object)null, "byName");
   }

   public static void addSubtypes(Type type, Type... subtypes) {
      addSubtypes(type, Arrays.asList(subtypes));
   }

   public static void addSubtypes(Type type, List<Type> subtypes) {
      setSubtypes(type, Lists.newArrayList(Iterables.concat(getSubtypes(type), subtypes)));
   }

   public static List<Type> getSubtypes(Type type) {
      return (List)SafeReflector.get(Type.class, type, new ArrayList(), "subTypes");
   }

   public static void setSubtypes(Type type, List<Type> types) {
      SafeReflector.of(Type.class, (field) -> {
         field.set(type, types);
      }, "subTypes");
   }
}
