package slimeknights.mantle.configurate.objectmapping.serialize;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class TypeSerializerCollection {
   private final TypeSerializerCollection parent;
   private final Map<TypeToken<?>, TypeSerializer<?>> typeMatches = new ConcurrentHashMap();
   private final Map<Predicate<TypeToken<?>>, TypeSerializer<?>> functionMatches = new ConcurrentHashMap();

   TypeSerializerCollection(TypeSerializerCollection parent) {
      this.parent = parent;
   }

   public <T> TypeSerializer<T> get(TypeToken<T> type) {
      Preconditions.checkNotNull(type, "type");
      type = type.wrap();
      TypeSerializer<?> serial = (TypeSerializer)this.typeMatches.get(type);
      if (serial == null) {
         for(Entry<TypeToken<?>, TypeSerializer<?>> ent : this.typeMatches.entrySet()) {
            if (((TypeToken)ent.getKey()).isSupertypeOf(type)) {
               serial = (TypeSerializer)ent.getValue();
               this.typeMatches.put(type, serial);
               break;
            }
         }
      }

      if (serial == null) {
         for(Entry<Predicate<TypeToken<?>>, TypeSerializer<?>> ent : this.functionMatches.entrySet()) {
            if (((Predicate)ent.getKey()).test(type)) {
               serial = (TypeSerializer)ent.getValue();
               this.typeMatches.put(type, serial);
               break;
            }
         }
      }

      if (serial == null && this.parent != null) {
         serial = this.parent.get(type);
      }

      return serial;
   }

   public <T> TypeSerializerCollection registerType(TypeToken<T> type, TypeSerializer<? super T> serializer) {
      Preconditions.checkNotNull(type, "type");
      Preconditions.checkNotNull(serializer, "serializer");
      this.typeMatches.put(type, serializer);
      return this;
   }

   public <T> TypeSerializerCollection registerPredicate(Predicate<TypeToken<T>> type, TypeSerializer<? super T> serializer) {
      Preconditions.checkNotNull(type, "type");
      Preconditions.checkNotNull(serializer, "serializer");
      this.functionMatches.put(type, serializer);
      return this;
   }

   public TypeSerializerCollection newChild() {
      return new TypeSerializerCollection(this);
   }
}
