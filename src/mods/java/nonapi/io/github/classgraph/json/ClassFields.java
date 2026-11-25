package nonapi.io.github.classgraph.json;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ClassFields {
   final List<FieldTypeInfo> fieldOrder = new ArrayList();
   final Map<String, FieldTypeInfo> fieldNameToFieldTypeInfo = new HashMap();
   Field idField;

   public ClassFields(Class<?> cls, boolean resolveTypes, boolean onlySerializePublicFields, ClassFieldCache classFieldCache) {
      Set<String> visibleFieldNames = new HashSet();
      List<List<FieldTypeInfo>> fieldSuperclassReversedOrder = new ArrayList();
      TypeResolutions currTypeResolutions = null;
      Type currType = cls;

      while(currType != Object.class && currType != null) {
         Class<?> currRawType;
         if (currType instanceof ParameterizedType) {
            ParameterizedType currParameterizedType = (ParameterizedType)currType;
            currRawType = (Class)currParameterizedType.getRawType();
         } else {
            if (!(currType instanceof Class)) {
               throw new IllegalArgumentException("Illegal class type: " + currType);
            }

            currRawType = (Class)currType;
         }

         Field[] fields = currRawType.getDeclaredFields();
         List<FieldTypeInfo> fieldOrderWithinClass = new ArrayList();

         for(Field field : fields) {
            if (visibleFieldNames.add(field.getName())) {
               boolean isIdField = field.isAnnotationPresent(Id.class);
               if (isIdField) {
                  if (this.idField != null) {
                     throw new IllegalArgumentException("More than one @Id annotation: " + this.idField.getDeclaringClass() + "." + this.idField + " ; " + currRawType.getName() + "." + field.getName());
                  }

                  this.idField = field;
               }

               if (!JSONUtils.fieldIsSerializable(field, onlySerializePublicFields)) {
                  if (isIdField) {
                     throw new IllegalArgumentException("@Id annotation field must be accessible, final, and non-transient: " + currRawType.getName() + "." + field.getName());
                  }
               } else {
                  Type fieldGenericType = field.getGenericType();
                  Type fieldTypePartiallyResolved = currTypeResolutions != null && resolveTypes ? currTypeResolutions.resolveTypeVariables(fieldGenericType) : fieldGenericType;
                  FieldTypeInfo fieldTypeInfo = new FieldTypeInfo(field, fieldTypePartiallyResolved, classFieldCache);
                  this.fieldNameToFieldTypeInfo.put(field.getName(), fieldTypeInfo);
                  fieldOrderWithinClass.add(fieldTypeInfo);
               }
            }
         }

         fieldSuperclassReversedOrder.add(fieldOrderWithinClass);
         Type genericSuperType = currRawType.getGenericSuperclass();
         if (resolveTypes) {
            if (genericSuperType instanceof ParameterizedType) {
               Type resolvedSupertype = currTypeResolutions == null ? genericSuperType : currTypeResolutions.resolveTypeVariables(genericSuperType);
               currTypeResolutions = resolvedSupertype instanceof ParameterizedType ? new TypeResolutions((ParameterizedType)resolvedSupertype) : null;
               currType = resolvedSupertype;
            } else {
               if (!(genericSuperType instanceof Class)) {
                  throw new IllegalArgumentException("Got unexpected supertype " + genericSuperType);
               }

               currType = genericSuperType;
               currTypeResolutions = null;
            }
         } else {
            currType = genericSuperType;
         }
      }

      for(int i = fieldSuperclassReversedOrder.size() - 1; i >= 0; --i) {
         List<FieldTypeInfo> fieldGroupingForClass = (List)fieldSuperclassReversedOrder.get(i);
         this.fieldOrder.addAll(fieldGroupingForClass);
      }

   }
}
