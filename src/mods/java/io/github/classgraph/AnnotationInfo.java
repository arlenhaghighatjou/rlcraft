package io.github.classgraph;

import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import nonapi.io.github.classgraph.utils.ReflectionUtils;

public class AnnotationInfo extends ScanResultObject implements Comparable<AnnotationInfo>, HasName {
   private String name;
   private AnnotationParameterValueList annotationParamValues;
   private transient boolean annotationParamValuesHasBeenConvertedToPrimitive;
   private transient AnnotationParameterValueList annotationParamValuesWithDefaults;

   AnnotationInfo() {
   }

   AnnotationInfo(String name, AnnotationParameterValueList annotationParamValues) {
      this.name = name;
      this.annotationParamValues = annotationParamValues;
   }

   public String getName() {
      return this.name;
   }

   public boolean isInherited() {
      return this.getClassInfo().isInherited;
   }

   public AnnotationParameterValueList getDefaultParameterValues() {
      return this.getClassInfo().getAnnotationDefaultParameterValues();
   }

   public AnnotationParameterValueList getParameterValues() {
      if (this.annotationParamValuesWithDefaults == null) {
         ClassInfo classInfo = this.getClassInfo();
         if (classInfo == null) {
            return this.annotationParamValues == null ? AnnotationParameterValueList.EMPTY_LIST : this.annotationParamValues;
         }

         if (this.annotationParamValues != null && !this.annotationParamValuesHasBeenConvertedToPrimitive) {
            this.annotationParamValues.convertWrapperArraysToPrimitiveArrays(classInfo);
            this.annotationParamValuesHasBeenConvertedToPrimitive = true;
         }

         if (classInfo.annotationDefaultParamValues != null && !classInfo.annotationDefaultParamValuesHasBeenConvertedToPrimitive) {
            classInfo.annotationDefaultParamValues.convertWrapperArraysToPrimitiveArrays(classInfo);
            classInfo.annotationDefaultParamValuesHasBeenConvertedToPrimitive = true;
         }

         AnnotationParameterValueList defaultParamValues = classInfo.annotationDefaultParamValues;
         if (defaultParamValues == null && this.annotationParamValues == null) {
            return AnnotationParameterValueList.EMPTY_LIST;
         }

         if (defaultParamValues == null) {
            return this.annotationParamValues;
         }

         if (this.annotationParamValues == null) {
            return defaultParamValues;
         }

         Map<String, Object> allParamValues = new HashMap();

         for(AnnotationParameterValue defaultParamValue : defaultParamValues) {
            allParamValues.put(defaultParamValue.getName(), defaultParamValue.getValue());
         }

         for(AnnotationParameterValue annotationParamValue : this.annotationParamValues) {
            allParamValues.put(annotationParamValue.getName(), annotationParamValue.getValue());
         }

         if (classInfo.methodInfo == null) {
            throw new IllegalArgumentException("Could not find methods for annotation " + classInfo.getName());
         }

         this.annotationParamValuesWithDefaults = new AnnotationParameterValueList();

         for(MethodInfo mi : classInfo.methodInfo) {
            String paramName = mi.getName();
            byte var8 = -1;
            switch(paramName.hashCode()) {
            case -1944711511:
               if (paramName.equals("<clinit>")) {
                  var8 = 1;
               }
               break;
            case -1776922004:
               if (paramName.equals("toString")) {
                  var8 = 4;
               }
               break;
            case -1295482945:
               if (paramName.equals("equals")) {
                  var8 = 3;
               }
               break;
            case 147696667:
               if (paramName.equals("hashCode")) {
                  var8 = 2;
               }
               break;
            case 1444986633:
               if (paramName.equals("annotationType")) {
                  var8 = 5;
               }
               break;
            case 1818100338:
               if (paramName.equals("<init>")) {
                  var8 = 0;
               }
            }

            switch(var8) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
               break;
            default:
               Object paramValue = allParamValues.get(paramName);
               if (paramValue != null) {
                  this.annotationParamValuesWithDefaults.add(new AnnotationParameterValue(paramName, paramValue));
               }
            }
         }
      }

      return this.annotationParamValuesWithDefaults;
   }

   protected String getClassName() {
      return this.name;
   }

   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.annotationParamValues != null) {
         for(AnnotationParameterValue a : this.annotationParamValues) {
            a.setScanResult(scanResult);
         }
      }

   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo) {
      super.findReferencedClassInfo(classNameToClassInfo, refdClassInfo);
      if (this.annotationParamValues != null) {
         for(AnnotationParameterValue annotationParamValue : this.annotationParamValues) {
            annotationParamValue.findReferencedClassInfo(classNameToClassInfo, refdClassInfo);
         }
      }

   }

   public ClassInfo getClassInfo() {
      return super.getClassInfo();
   }

   public Annotation loadClassAndInstantiate() {
      Class<? extends Annotation> annotationClass = this.getClassInfo().<Annotation>loadClass(Annotation.class);
      return (Annotation)Proxy.newProxyInstance(annotationClass.getClassLoader(), new Class[]{annotationClass}, new AnnotationInfo.AnnotationInvocationHandler(annotationClass, this));
   }

   void convertWrapperArraysToPrimitiveArrays() {
      if (this.annotationParamValues != null) {
         this.annotationParamValues.convertWrapperArraysToPrimitiveArrays(this.getClassInfo());
      }

   }

   public int compareTo(AnnotationInfo o) {
      int diff = this.getName().compareTo(o.getName());
      if (diff != 0) {
         return diff;
      } else if (this.annotationParamValues == null && o.annotationParamValues == null) {
         return 0;
      } else if (this.annotationParamValues == null) {
         return -1;
      } else if (o.annotationParamValues == null) {
         return 1;
      } else {
         int i = 0;

         for(int max = Math.max(this.annotationParamValues.size(), o.annotationParamValues.size()); i < max; ++i) {
            if (i >= this.annotationParamValues.size()) {
               return -1;
            }

            if (i >= o.annotationParamValues.size()) {
               return 1;
            }

            int diff2 = ((AnnotationParameterValue)this.annotationParamValues.get(i)).compareTo((AnnotationParameterValue)o.annotationParamValues.get(i));
            if (diff2 != 0) {
               return diff2;
            }
         }

         return 0;
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof AnnotationInfo)) {
         return false;
      } else {
         AnnotationInfo other = (AnnotationInfo)obj;
         return this.compareTo(other) == 0;
      }
   }

   public int hashCode() {
      int h = this.getName().hashCode();
      if (this.annotationParamValues != null) {
         for(AnnotationParameterValue e : this.annotationParamValues) {
            h = h * 7 + e.getName().hashCode() * 3 + e.getValue().hashCode();
         }
      }

      return h;
   }

   void toString(StringBuilder buf) {
      buf.append('@').append(this.getName());
      AnnotationParameterValueList paramVals = this.getParameterValues();
      if (!paramVals.isEmpty()) {
         buf.append('(');

         for(int i = 0; i < paramVals.size(); ++i) {
            if (i > 0) {
               buf.append(", ");
            }

            AnnotationParameterValue paramVal = (AnnotationParameterValue)paramVals.get(i);
            if (paramVals.size() <= 1 && "value".equals(paramVal.getName())) {
               paramVal.toStringParamValueOnly(buf);
            } else {
               paramVal.toString(buf);
            }
         }

         buf.append(')');
      }

   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      this.toString(buf);
      return buf.toString();
   }

   private static class AnnotationInvocationHandler implements InvocationHandler {
      private final Class<? extends Annotation> annotationClass;
      private final AnnotationInfo annotationInfo;
      private final Map<String, Object> annotationParameterValuesInstantiated = new HashMap();

      AnnotationInvocationHandler(Class<? extends Annotation> annotationClass, AnnotationInfo annotationInfo) {
         this.annotationClass = annotationClass;
         this.annotationInfo = annotationInfo;

         for(AnnotationParameterValue apv : annotationInfo.getParameterValues()) {
            Object instantiatedValue = apv.instantiate(annotationInfo.getClassInfo());
            if (instantiatedValue == null) {
               throw new IllegalArgumentException("Got null value for annotation parameter " + apv.getName() + " of annotation " + annotationInfo.getName());
            }

            this.annotationParameterValuesInstantiated.put(apv.getName(), instantiatedValue);
         }

      }

      public Object invoke(Object proxy, Method method, Object[] args) {
         String methodName = method.getName();
         Class<?>[] paramTypes = method.getParameterTypes();
         if ((args == null ? 0 : args.length) != paramTypes.length) {
            throw new IllegalArgumentException("Wrong number of arguments for " + this.annotationClass.getName() + "." + methodName + ": got " + (args == null ? 0 : args.length) + ", expected " + paramTypes.length);
         } else if (args != null && paramTypes.length == 1) {
            if ("equals".equals(methodName) && paramTypes[0] == Object.class) {
               if (this == args[0]) {
                  return true;
               } else if (!this.annotationClass.isInstance(args[0])) {
                  return false;
               } else {
                  for(Entry<String, Object> ent : this.annotationParameterValuesInstantiated.entrySet()) {
                     String paramName = (String)ent.getKey();
                     Object paramVal = ent.getValue();
                     Object otherParamVal = ReflectionUtils.invokeMethod(args[0], paramName, false);
                     if (paramVal == null != (otherParamVal == null)) {
                        return false;
                     }

                     if (paramVal == null && otherParamVal == null) {
                        return true;
                     }

                     if (paramVal == null || !paramVal.equals(otherParamVal)) {
                        return false;
                     }
                  }

                  return true;
               }
            } else {
               throw new IllegalArgumentException();
            }
         } else if (paramTypes.length == 0) {
            byte annotationParameterValueClass = -1;
            switch(methodName.hashCode()) {
            case -1776922004:
               if (methodName.equals("toString")) {
                  annotationParameterValueClass = 0;
               }
               break;
            case 147696667:
               if (methodName.equals("hashCode")) {
                  annotationParameterValueClass = 1;
               }
               break;
            case 1444986633:
               if (methodName.equals("annotationType")) {
                  annotationParameterValueClass = 2;
               }
            }

            switch(annotationParameterValueClass) {
            case 0:
               return this.annotationInfo.toString();
            case 1:
               int result = 0;

               for(Entry<String, Object> ent : this.annotationParameterValuesInstantiated.entrySet()) {
                  String paramName = (String)ent.getKey();
                  Object paramVal = ent.getValue();
                  int paramValHashCode;
                  if (paramVal == null) {
                     paramValHashCode = 0;
                  } else {
                     Class<?> type = paramVal.getClass();
                     if (!type.isArray()) {
                        paramValHashCode = paramVal.hashCode();
                     } else if (type == byte[].class) {
                        paramValHashCode = Arrays.hashCode((byte[])paramVal);
                     } else if (type == char[].class) {
                        paramValHashCode = Arrays.hashCode((char[])paramVal);
                     } else if (type == double[].class) {
                        paramValHashCode = Arrays.hashCode((double[])paramVal);
                     } else if (type == float[].class) {
                        paramValHashCode = Arrays.hashCode((float[])paramVal);
                     } else if (type == int[].class) {
                        paramValHashCode = Arrays.hashCode((int[])paramVal);
                     } else if (type == long[].class) {
                        paramValHashCode = Arrays.hashCode((long[])paramVal);
                     } else if (type == short[].class) {
                        paramValHashCode = Arrays.hashCode((short[])paramVal);
                     } else if (type == boolean[].class) {
                        paramValHashCode = Arrays.hashCode((boolean[])paramVal);
                     } else {
                        paramValHashCode = Arrays.hashCode(paramVal);
                     }
                  }

                  result += 127 * paramName.hashCode() ^ paramValHashCode;
               }

               return result;
            case 2:
               return this.annotationClass;
            default:
               Object annotationParameterValue = this.annotationParameterValuesInstantiated.get(methodName);
               if (annotationParameterValue == null) {
                  throw new IncompleteAnnotationException(this.annotationClass, methodName);
               } else {
                  Class<?> annotationParameterValueClass = annotationParameterValue.getClass();
                  if (annotationParameterValueClass.isArray()) {
                     if (annotationParameterValueClass == String[].class) {
                        return ((String[])annotationParameterValue).clone();
                     } else if (annotationParameterValueClass == byte[].class) {
                        return ((byte[])annotationParameterValue).clone();
                     } else if (annotationParameterValueClass == char[].class) {
                        return ((char[])annotationParameterValue).clone();
                     } else if (annotationParameterValueClass == double[].class) {
                        return ((double[])annotationParameterValue).clone();
                     } else if (annotationParameterValueClass == float[].class) {
                        return ((float[])annotationParameterValue).clone();
                     } else if (annotationParameterValueClass == int[].class) {
                        return ((int[])annotationParameterValue).clone();
                     } else if (annotationParameterValueClass == long[].class) {
                        return ((long[])annotationParameterValue).clone();
                     } else if (annotationParameterValueClass == short[].class) {
                        return ((short[])annotationParameterValue).clone();
                     } else if (annotationParameterValueClass == boolean[].class) {
                        return ((boolean[])annotationParameterValue).clone();
                     } else {
                        Object[] arr = annotationParameterValue;
                        return arr.clone();
                     }
                  } else {
                     return annotationParameterValue;
                  }
               }
            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }
}
