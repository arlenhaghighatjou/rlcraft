package stanhebben.zenscript.compiler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAny;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.type.ZenTypeArrayList;
import stanhebben.zenscript.type.ZenTypeAssociative;
import stanhebben.zenscript.type.ZenTypeBoolObject;
import stanhebben.zenscript.type.ZenTypeByte;
import stanhebben.zenscript.type.ZenTypeByteObject;
import stanhebben.zenscript.type.ZenTypeDouble;
import stanhebben.zenscript.type.ZenTypeDoubleObject;
import stanhebben.zenscript.type.ZenTypeEntry;
import stanhebben.zenscript.type.ZenTypeFloat;
import stanhebben.zenscript.type.ZenTypeFloatObject;
import stanhebben.zenscript.type.ZenTypeInt;
import stanhebben.zenscript.type.ZenTypeIntObject;
import stanhebben.zenscript.type.ZenTypeIntRange;
import stanhebben.zenscript.type.ZenTypeLong;
import stanhebben.zenscript.type.ZenTypeLongObject;
import stanhebben.zenscript.type.ZenTypeNative;
import stanhebben.zenscript.type.ZenTypeShort;
import stanhebben.zenscript.type.ZenTypeShortObject;
import stanhebben.zenscript.type.ZenTypeString;
import stanhebben.zenscript.type.ZenTypeVoid;
import stanhebben.zenscript.value.IAny;
import stanhebben.zenscript.value.IntRange;

public class TypeRegistry implements ITypeRegistry {
   private final Map<Class, ZenType> types = new HashMap();

   public TypeRegistry() {
      this.types.put(Boolean.TYPE, ZenType.BOOL);
      this.types.put(Byte.TYPE, ZenTypeByte.INSTANCE);
      this.types.put(Short.TYPE, ZenTypeShort.INSTANCE);
      this.types.put(Integer.TYPE, ZenTypeInt.INSTANCE);
      this.types.put(Long.TYPE, ZenTypeLong.INSTANCE);
      this.types.put(Float.TYPE, ZenTypeFloat.INSTANCE);
      this.types.put(Double.TYPE, ZenTypeDouble.INSTANCE);
      this.types.put(Void.TYPE, ZenTypeVoid.INSTANCE);
      this.types.put(Boolean.class, ZenTypeBoolObject.INSTANCE);
      this.types.put(Byte.class, ZenTypeByteObject.INSTANCE);
      this.types.put(Short.class, ZenTypeShortObject.INSTANCE);
      this.types.put(Integer.class, ZenTypeIntObject.INSTANCE);
      this.types.put(Long.class, ZenTypeLongObject.INSTANCE);
      this.types.put(Float.class, ZenTypeFloatObject.INSTANCE);
      this.types.put(Double.class, ZenTypeDoubleObject.INSTANCE);
      this.types.put(IAny.class, ZenTypeAny.INSTANCE);
      this.types.put(String.class, ZenTypeString.INSTANCE);
      this.types.put(List.class, new ZenTypeArrayBasic(ZenTypeAny.INSTANCE));
      this.types.put(IntRange.class, ZenTypeIntRange.INSTANCE);
   }

   public ZenType getClassType(Class cls) {
      if (this.types.containsKey(cls)) {
         return (ZenType)this.types.get(cls);
      } else if (cls.isArray()) {
         ZenType result = new ZenTypeArrayBasic(this.getType(cls.getComponentType()));
         this.types.put(cls, result);
         return result;
      } else {
         ZenTypeNative result = new ZenTypeNative(cls);
         this.types.put(cls, result);
         result.complete(this);
         return result;
      }
   }

   public ZenType getType(Type type) {
      if (type instanceof ParameterizedType) {
         ParameterizedType pType = (ParameterizedType)type;
         Type raw = pType.getRawType();
         if (raw instanceof Class) {
            Class rawClass = (Class)raw;
            if (List.class.isAssignableFrom(rawClass)) {
               return this.getListType(pType);
            } else if (Map.class.isAssignableFrom(rawClass)) {
               return this.getMapType(pType);
            } else {
               return (ZenType)(Entry.class.isAssignableFrom(rawClass) ? this.getMapEntryType(pType) : this.getClassType(rawClass));
            }
         } else {
            return this.getType(raw);
         }
      } else {
         return type instanceof Class ? this.getClassType((Class)type) : null;
      }
   }

   private ZenType getListType(ParameterizedType type) {
      return type.getRawType() == List.class ? new ZenTypeArrayList(this.getType(type.getActualTypeArguments()[0])) : null;
   }

   private ZenType getMapType(ParameterizedType type) {
      return type.getRawType() == Map.class ? new ZenTypeAssociative(this.getType(type.getActualTypeArguments()[1]), this.getType(type.getActualTypeArguments()[0])) : null;
   }

   private ZenTypeEntry getMapEntryType(ParameterizedType type) {
      return type.getRawType() == Entry.class ? new ZenTypeEntry(this.getType(type.getActualTypeArguments()[0]), this.getType(type.getActualTypeArguments()[1])) : null;
   }

   public Map<Class, ZenType> getTypeMap() {
      return this.types;
   }
}
