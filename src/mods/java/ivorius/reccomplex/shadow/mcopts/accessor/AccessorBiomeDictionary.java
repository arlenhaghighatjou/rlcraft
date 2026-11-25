package ivorius.reccomplex..shadow.mcopts.accessor;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ivorius.reccomplex..shadow.mcopts.reflection.SafeReflector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraftforge.common.BiomeDictionary.Type;

public class AccessorBiomeDictionary {
   public AccessorBiomeDictionary() {
   }

   public static Type getTypeWeak(String var) {
      return (Type)getMap().get(var.toUpperCase());
   }

   public static Map<String, Type> getMap() {
      return (Map)SafeReflector.get(Type.class, "byName", (Object)null);
   }

   public static void addSubtypes(Type type, Type... subtypes) {
      addSubtypes(type, Arrays.asList(subtypes));
   }

   public static void addSubtypes(Type type, List<Type> subtypes) {
      setSubtypes(type, Lists.newArrayList(Iterables.concat(getSubtypes(type), subtypes)));
   }

   public static List<Type> getSubtypes(Type type) {
      return (List)SafeReflector.get(Type.class, "subTypes", type, new ArrayList());
   }

   public static void setSubtypes(Type type, List<Type> types) {
      SafeReflector.of(Type.class, "subTypes", (field) -> {
         field.set(type, types);
      });
   }
}
