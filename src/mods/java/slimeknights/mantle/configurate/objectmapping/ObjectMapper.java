package slimeknights.mantle.configurate.objectmapping;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import slimeknights.mantle.configurate.ConfigurationNode;
import slimeknights.mantle.configurate.commented.CommentedConfigurationNode;
import slimeknights.mantle.configurate.objectmapping.serialize.TypeSerializer;

public class ObjectMapper<T> {
   private final Class<T> clazz;
   private final Constructor<T> constructor;
   private final Map<String, ObjectMapper.FieldData> cachedFields = new HashMap();

   public static <T> ObjectMapper<T> forClass(Class<T> clazz) throws ObjectMappingException {
      return DefaultObjectMapperFactory.getInstance().<T>getMapper(clazz);
   }

   public static <T> ObjectMapper<T>.BoundInstance forObject(T obj) throws ObjectMappingException {
      Preconditions.checkNotNull(obj);
      return forClass(obj.getClass()).bind(obj);
   }

   protected ObjectMapper(Class<T> clazz) throws ObjectMappingException {
      this.clazz = clazz;
      Constructor<T> constructor = null;

      try {
         constructor = clazz.getDeclaredConstructor();
         constructor.setAccessible(true);
      } catch (NoSuchMethodException var4) {
         ;
      }

      this.constructor = constructor;
      Class<? super T> collectClass = clazz;

      while(true) {
         this.collectFields(this.cachedFields, collectClass);
         if ((collectClass = collectClass.getSuperclass()).equals(Object.class)) {
            break;
         }
      }

   }

   protected void collectFields(Map<String, ObjectMapper.FieldData> cachedFields, Class<? super T> clazz) throws ObjectMappingException {
      for(Field field : clazz.getDeclaredFields()) {
         if (field.isAnnotationPresent(Setting.class)) {
            Setting setting = (Setting)field.getAnnotation(Setting.class);
            String path = setting.value();
            if (path.isEmpty()) {
               path = field.getName();
            }

            ObjectMapper.FieldData data = new ObjectMapper.FieldData(field, setting.comment());
            field.setAccessible(true);
            if (!cachedFields.containsKey(path)) {
               cachedFields.put(path, data);
            }
         }
      }

   }

   protected T constructObject() throws ObjectMappingException {
      if (this.constructor == null) {
         throw new ObjectMappingException("No zero-arg constructor is available for class " + this.clazz + " but is required to construct new instances!");
      } else {
         try {
            return (T)this.constructor.newInstance();
         } catch (InstantiationException var2) {
            throw new ObjectMappingException("Unable to create instance of target class " + this.clazz, var2);
         } catch (IllegalAccessException var3) {
            throw new ObjectMappingException("Unable to create instance of target class " + this.clazz, var3);
         } catch (InvocationTargetException var4) {
            throw new ObjectMappingException("Unable to create instance of target class " + this.clazz, var4);
         }
      }
   }

   public boolean canCreateInstances() {
      return this.constructor != null;
   }

   public ObjectMapper<T>.BoundInstance bind(T instance) {
      return new ObjectMapper.BoundInstance(instance);
   }

   public ObjectMapper<T>.BoundInstance bindToNew() throws ObjectMappingException {
      return new ObjectMapper.BoundInstance(this.constructObject());
   }

   public Class<T> getMappedType() {
      return this.clazz;
   }

   public class BoundInstance {
      private final T boundInstance;

      protected BoundInstance(T boundInstance) {
         this.boundInstance = boundInstance;
      }

      public T populate(ConfigurationNode source) throws ObjectMappingException {
         for(Entry<String, ObjectMapper.FieldData> ent : ObjectMapper.this.cachedFields.entrySet()) {
            ConfigurationNode node = source.getNode(ent.getKey());
            ((ObjectMapper.FieldData)ent.getValue()).deserializeFrom(this.boundInstance, node);
         }

         return this.boundInstance;
      }

      public void serialize(ConfigurationNode target) throws ObjectMappingException {
         for(Entry<String, ObjectMapper.FieldData> ent : ObjectMapper.this.cachedFields.entrySet()) {
            ConfigurationNode node = target.getNode(ent.getKey());
            ((ObjectMapper.FieldData)ent.getValue()).serializeTo(this.boundInstance, node);
         }

      }

      public T getInstance() {
         return this.boundInstance;
      }
   }

   protected static class FieldData {
      private final Field field;
      private final TypeToken<?> fieldType;
      private final String comment;

      public FieldData(Field field, String comment) throws ObjectMappingException {
         this.field = field;
         this.comment = comment;
         this.fieldType = TypeToken.of(field.getGenericType());
      }

      public void deserializeFrom(Object instance, ConfigurationNode node) throws ObjectMappingException {
         TypeSerializer<?> serial = node.getOptions().getSerializers().get(this.fieldType);
         if (serial == null) {
            throw new ObjectMappingException("No TypeSerializer found for field " + this.field.getName() + " of type " + this.fieldType);
         } else {
            Object newVal = node.isVirtual() ? null : serial.deserialize(this.fieldType, node);

            try {
               if (newVal == null) {
                  Object existingVal = this.field.get(instance);
                  if (existingVal != null) {
                     this.serializeTo(instance, node);
                  }
               } else {
                  this.field.set(instance, newVal);
               }

            } catch (IllegalAccessException var6) {
               throw new ObjectMappingException("Unable to deserialize field " + this.field.getName(), var6);
            }
         }
      }

      public void serializeTo(Object instance, ConfigurationNode node) throws ObjectMappingException {
         try {
            Object fieldVal = this.field.get(instance);
            if (fieldVal == null) {
               node.setValue((Object)null);
            } else {
               TypeSerializer serial = node.getOptions().getSerializers().get(this.fieldType);
               if (serial == null) {
                  throw new ObjectMappingException("No TypeSerializer found for field " + this.field.getName() + " of type " + this.fieldType);
               }

               serial.serialize(this.fieldType, fieldVal, node);
            }

            if (node instanceof CommentedConfigurationNode && this.comment != null && !this.comment.isEmpty()) {
               CommentedConfigurationNode commentNode = (CommentedConfigurationNode)node;
               if (!commentNode.getComment().isPresent()) {
                  commentNode.setComment(this.comment);
               }
            }

         } catch (IllegalAccessException var5) {
            throw new ObjectMappingException("Unable to serialize field " + this.field.getName(), var5);
         }
      }
   }
}
