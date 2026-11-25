package nonapi.io.github.classgraph.json;

import io.github.classgraph.ClassGraphException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public final class JSONSerializer {
   private static final Comparator<Object> SET_COMPARATOR = new Comparator<Object>() {
      public int compare(Object o1, Object o2) {
         if (o1 != null && o2 != null) {
            if (Comparable.class.isAssignableFrom(o1.getClass()) && Comparable.class.isAssignableFrom(o2.getClass())) {
               Comparable<Object> comparableO1 = (Comparable)o1;
               return comparableO1.compareTo(o2);
            } else {
               return o1.toString().compareTo(o2.toString());
            }
         } else {
            return (o1 == null ? 0 : 1) - (o2 == null ? 0 : 1);
         }
      }
   };

   private JSONSerializer() {
   }

   private static void assignObjectIds(Object jsonVal, Map<ReferenceEqualityKey<Object>, JSONObject> objToJSONVal, ClassFieldCache classFieldCache, Map<ReferenceEqualityKey<JSONReference>, CharSequence> jsonReferenceToId, AtomicInteger objId, boolean onlySerializePublicFields) {
      if (jsonVal instanceof JSONObject) {
         for(Entry<String, Object> item : ((JSONObject)jsonVal).items) {
            assignObjectIds(item.getValue(), objToJSONVal, classFieldCache, jsonReferenceToId, objId, onlySerializePublicFields);
         }
      } else if (jsonVal instanceof JSONArray) {
         for(Object item : ((JSONArray)jsonVal).items) {
            assignObjectIds(item, objToJSONVal, classFieldCache, jsonReferenceToId, objId, onlySerializePublicFields);
         }
      } else if (jsonVal instanceof JSONReference) {
         Object refdObj = ((JSONReference)jsonVal).idObject;
         if (refdObj == null) {
            throw ClassGraphException.newClassGraphException("Internal inconsistency");
         }

         ReferenceEqualityKey<Object> refdObjKey = new ReferenceEqualityKey<Object>(refdObj);
         JSONObject refdJsonVal = (JSONObject)objToJSONVal.get(refdObjKey);
         if (refdJsonVal == null) {
            throw ClassGraphException.newClassGraphException("Internal inconsistency");
         }

         Field annotatedField = classFieldCache.get(refdObj.getClass()).idField;
         CharSequence idStr = null;
         if (annotatedField != null) {
            try {
               Object idObject = annotatedField.get(refdObj);
               if (idObject != null) {
                  idStr = idObject.toString();
                  refdJsonVal.objectId = idStr;
               }
            } catch (IllegalAccessException | IllegalArgumentException var12) {
               throw new IllegalArgumentException("Could not access @Id-annotated field " + annotatedField, var12);
            }
         }

         if (idStr == null) {
            if (refdJsonVal.objectId == null) {
               idStr = "[#" + objId.getAndIncrement() + "]";
               refdJsonVal.objectId = idStr;
            } else {
               idStr = refdJsonVal.objectId;
            }
         }

         jsonReferenceToId.put(new ReferenceEqualityKey((JSONReference)jsonVal), idStr);
      }

   }

   private static void convertVals(Object[] convertedVals, Set<ReferenceEqualityKey<Object>> visitedOnPath, Set<ReferenceEqualityKey<Object>> standardObjectVisited, ClassFieldCache classFieldCache, Map<ReferenceEqualityKey<Object>, JSONObject> objToJSONVal, boolean onlySerializePublicFields) {
      ReferenceEqualityKey<?>[] valKeys = new ReferenceEqualityKey[convertedVals.length];
      boolean[] needToConvert = new boolean[convertedVals.length];

      for(int i = 0; i < convertedVals.length; ++i) {
         Object val = convertedVals[i];
         needToConvert[i] = !JSONUtils.isBasicValueType(val);
         if (needToConvert[i] && !JSONUtils.isCollectionOrArray(val)) {
            ReferenceEqualityKey<Object> valKey = new ReferenceEqualityKey<Object>(val);
            valKeys[i] = valKey;
            boolean alreadyVisited = !standardObjectVisited.add(valKey);
            if (alreadyVisited) {
               convertedVals[i] = new JSONReference(val);
               needToConvert[i] = false;
            }
         }

         if (val instanceof Class) {
            convertedVals[i] = ((Class)val).getName();
         }
      }

      for(int i = 0; i < convertedVals.length; ++i) {
         if (needToConvert[i]) {
            Object val = convertedVals[i];
            convertedVals[i] = toJSONGraph(val, visitedOnPath, standardObjectVisited, classFieldCache, objToJSONVal, onlySerializePublicFields);
            if (!JSONUtils.isCollectionOrArray(val)) {
               ReferenceEqualityKey<Object> valKey = valKeys[i];
               objToJSONVal.put(valKey, (JSONObject)convertedVals[i]);
            }
         }
      }

   }

   private static Object toJSONGraph(Object obj, Set<ReferenceEqualityKey<Object>> visitedOnPath, Set<ReferenceEqualityKey<Object>> standardObjectVisited, ClassFieldCache classFieldCache, Map<ReferenceEqualityKey<Object>, JSONObject> objToJSONVal, boolean onlySerializePublicFields) {
      if (obj instanceof Class) {
         return ((Class)obj).getName();
      } else if (JSONUtils.isBasicValueType(obj)) {
         return obj;
      } else {
         ReferenceEqualityKey<Object> objKey = new ReferenceEqualityKey<Object>(obj);
         if (!visitedOnPath.add(objKey)) {
            if (JSONUtils.isCollectionOrArray(obj)) {
               throw new IllegalArgumentException("Cycles involving collections cannot be serialized, since collections are not assigned object ids. Reached cycle at: " + obj);
            } else {
               return new JSONReference(obj);
            }
         } else {
            Class<?> cls = obj.getClass();
            boolean isArray = cls.isArray();
            Object jsonVal;
            if (Map.class.isAssignableFrom(cls)) {
               Map<Object, Object> map = (Map)obj;
               ArrayList<?> keys = new ArrayList(map.keySet());
               int n = keys.size();
               boolean keysComparable = false;
               Object firstNonNullKey = null;

               for(int i = 0; i < n && firstNonNullKey == null; ++i) {
                  firstNonNullKey = keys.get(i);
               }

               if (firstNonNullKey != null && Comparable.class.isAssignableFrom(firstNonNullKey.getClass())) {
                  CollectionUtils.sortIfNotEmpty(keys);
                  keysComparable = true;
               }

               String[] convertedKeys = new String[n];

               for(int i = 0; i < n; ++i) {
                  Object key = keys.get(i);
                  if (key != null && !JSONUtils.isBasicValueType(key)) {
                     throw new IllegalArgumentException("Map key of type " + key.getClass().getName() + " is not a basic type (String, Integer, etc.), so can't be easily serialized as a JSON associative array key");
                  }

                  convertedKeys[i] = JSONUtils.escapeJSONString(key == null ? "null" : key.toString());
               }

               if (!keysComparable) {
                  Arrays.sort(convertedKeys);
               }

               Object[] convertedVals = new Object[n];

               for(int i = 0; i < n; ++i) {
                  convertedVals[i] = map.get(keys.get(i));
               }

               convertVals(convertedVals, visitedOnPath, standardObjectVisited, classFieldCache, objToJSONVal, onlySerializePublicFields);
               List<Entry<String, Object>> convertedKeyValPairs = new ArrayList(n);

               for(int i = 0; i < n; ++i) {
                  convertedKeyValPairs.add(new SimpleEntry(convertedKeys[i], convertedVals[i]));
               }

               jsonVal = new JSONObject(convertedKeyValPairs);
            } else if (!isArray && !List.class.isAssignableFrom(cls)) {
               if (Collection.class.isAssignableFrom(cls)) {
                  Collection<?> collection = (Collection)obj;
                  List<Object> convertedValsList = new ArrayList(collection);
                  if (Set.class.isAssignableFrom(cls)) {
                     CollectionUtils.sortIfNotEmpty(convertedValsList, SET_COMPARATOR);
                  }

                  Object[] convertedVals = convertedValsList.toArray();
                  convertVals(convertedVals, visitedOnPath, standardObjectVisited, classFieldCache, objToJSONVal, onlySerializePublicFields);
                  jsonVal = new JSONArray(Arrays.asList(convertedVals));
               } else {
                  try {
                     ClassFields resolvedFields = classFieldCache.get(cls);
                     List<FieldTypeInfo> fieldOrder = resolvedFields.fieldOrder;
                     int n = fieldOrder.size();
                     String[] fieldNames = new String[n];
                     Object[] convertedVals = new Object[n];

                     for(int i = 0; i < n; ++i) {
                        FieldTypeInfo fieldInfo = (FieldTypeInfo)fieldOrder.get(i);
                        Field field = fieldInfo.field;
                        fieldNames[i] = field.getName();
                        convertedVals[i] = JSONUtils.getFieldValue(obj, field);
                     }

                     convertVals(convertedVals, visitedOnPath, standardObjectVisited, classFieldCache, objToJSONVal, onlySerializePublicFields);
                     List<Entry<String, Object>> convertedKeyValPairs = new ArrayList(n);

                     for(int i = 0; i < n; ++i) {
                        convertedKeyValPairs.add(new SimpleEntry(fieldNames[i], convertedVals[i]));
                     }

                     jsonVal = new JSONObject(convertedKeyValPairs);
                  } catch (IllegalAccessException | IllegalArgumentException var19) {
                     throw ClassGraphException.newClassGraphException("Could not get value of field in object: " + obj, var19);
                  }
               }
            } else {
               boolean isList = List.class.isAssignableFrom(cls);
               List<?> list = isList ? (List)obj : null;
               int n = list != null ? list.size() : (isArray ? Array.getLength(obj) : 0);
               Object[] convertedVals = new Object[n];

               for(int i = 0; i < n; ++i) {
                  convertedVals[i] = list != null ? list.get(i) : (isArray ? Array.get(obj, i) : Integer.valueOf(0));
               }

               convertVals(convertedVals, visitedOnPath, standardObjectVisited, classFieldCache, objToJSONVal, onlySerializePublicFields);
               jsonVal = new JSONArray(Arrays.asList(convertedVals));
            }

            visitedOnPath.remove(objKey);
            return jsonVal;
         }
      }
   }

   static void jsonValToJSONString(Object jsonVal, Map<ReferenceEqualityKey<JSONReference>, CharSequence> jsonReferenceToId, boolean includeNullValuedFields, int depth, int indentWidth, StringBuilder buf) {
      if (jsonVal == null) {
         buf.append("null");
      } else if (jsonVal instanceof JSONObject) {
         ((JSONObject)jsonVal).toJSONString(jsonReferenceToId, includeNullValuedFields, depth, indentWidth, buf);
      } else if (jsonVal instanceof JSONArray) {
         ((JSONArray)jsonVal).toJSONString(jsonReferenceToId, includeNullValuedFields, depth, indentWidth, buf);
      } else if (jsonVal instanceof JSONReference) {
         Object referencedObjectId = jsonReferenceToId.get(new ReferenceEqualityKey((JSONReference)jsonVal));
         jsonValToJSONString(referencedObjectId, jsonReferenceToId, includeNullValuedFields, depth, indentWidth, buf);
      } else if (!(jsonVal instanceof CharSequence) && !(jsonVal instanceof Character) && !jsonVal.getClass().isEnum()) {
         buf.append(jsonVal.toString());
      } else {
         buf.append('"');
         JSONUtils.escapeJSONString(jsonVal.toString(), buf);
         buf.append('"');
      }

   }

   public static String serializeObject(Object obj, int indentWidth, boolean onlySerializePublicFields, ClassFieldCache classFieldCache) {
      HashMap<ReferenceEqualityKey<Object>, JSONObject> objToJSONVal = new HashMap();
      Object rootJsonVal = toJSONGraph(obj, new HashSet(), new HashSet(), classFieldCache, objToJSONVal, onlySerializePublicFields);
      Map<ReferenceEqualityKey<JSONReference>, CharSequence> jsonReferenceToId = new HashMap();
      AtomicInteger objId = new AtomicInteger(0);
      assignObjectIds(rootJsonVal, objToJSONVal, classFieldCache, jsonReferenceToId, objId, onlySerializePublicFields);
      StringBuilder buf = new StringBuilder(32768);
      jsonValToJSONString(rootJsonVal, jsonReferenceToId, false, 0, indentWidth, buf);
      return buf.toString();
   }

   public static String serializeObject(Object obj, int indentWidth, boolean onlySerializePublicFields) {
      return serializeObject(obj, indentWidth, onlySerializePublicFields, new ClassFieldCache(false, false));
   }

   public static String serializeObject(Object obj) {
      return serializeObject(obj, 0, false);
   }

   public static String serializeFromField(Object containingObject, String fieldName, int indentWidth, boolean onlySerializePublicFields, ClassFieldCache classFieldCache) {
      FieldTypeInfo fieldResolvedTypeInfo = (FieldTypeInfo)classFieldCache.get(containingObject.getClass()).fieldNameToFieldTypeInfo.get(fieldName);
      if (fieldResolvedTypeInfo == null) {
         throw new IllegalArgumentException("Class " + containingObject.getClass().getName() + " does not have a field named \"" + fieldName + "\"");
      } else {
         Field field = fieldResolvedTypeInfo.field;
         if (!JSONUtils.fieldIsSerializable(field, false)) {
            throw new IllegalArgumentException("Field " + containingObject.getClass().getName() + "." + fieldName + " needs to be accessible, non-transient, and non-final");
         } else {
            Object fieldValue;
            try {
               fieldValue = JSONUtils.getFieldValue(containingObject, field);
            } catch (IllegalAccessException var9) {
               throw new IllegalArgumentException("Could get value of field " + fieldName, var9);
            }

            return serializeObject(fieldValue, indentWidth, onlySerializePublicFields, classFieldCache);
         }
      }
   }

   public static String serializeFromField(Object containingObject, String fieldName, int indentWidth, boolean onlySerializePublicFields) {
      ClassFieldCache classFieldCache = new ClassFieldCache(false, onlySerializePublicFields);
      return serializeFromField(containingObject, fieldName, indentWidth, onlySerializePublicFields, classFieldCache);
   }
}
