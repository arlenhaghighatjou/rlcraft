package io.github.classgraph;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.InputStreamOrByteBufferAdapter;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.Join;
import nonapi.io.github.classgraph.utils.LogNode;

class Classfile {
   private InputStreamOrByteBufferAdapter inputStreamOrByteBuffer;
   private final ClasspathElement classpathElement;
   private final List<ClasspathElement> classpathOrder;
   private final String relativePath;
   private final Resource classfileResource;
   private final ConcurrentHashMap<String, String> stringInternMap;
   private String className;
   private final boolean isExternalClass;
   private int classModifiers;
   private boolean isInterface;
   private boolean isAnnotation;
   private String superclassName;
   private List<String> implementedInterfaces;
   private AnnotationInfoList classAnnotations;
   private String fullyQualifiedDefiningMethodName;
   private List<Classfile.ClassContainment> classContainmentEntries;
   private AnnotationParameterValueList annotationParamDefaultValues;
   private Set<String> refdClassNames;
   private FieldInfoList fieldInfoList;
   private MethodInfoList methodInfoList;
   private String typeSignature;
   private final Set<String> whitelistedClassNamesFound;
   private final Set<String> classNamesScheduledForExtendedScanning;
   private List<Scanner.ClassfileScanWorkUnit> additionalWorkUnits;
   private final ScanSpec scanSpec;
   private int cpCount;
   private int[] entryOffset;
   private int[] entryTag;
   private int[] indirectStringRefs;
   private static final AnnotationInfo[] NO_ANNOTATIONS = new AnnotationInfo[0];

   private void scheduleScanningIfExternalClass(String className, String relationship, LogNode log) {
      if (className != null && !className.equals("java.lang.Object") && !this.whitelistedClassNamesFound.contains(className) && this.classNamesScheduledForExtendedScanning.add(className)) {
         if (this.scanSpec.classWhiteBlackList.isBlacklisted(className)) {
            if (log != null) {
               log.log("Cannot extend scanning upwards to external " + relationship + " " + className + ", since it is blacklisted");
            }
         } else {
            String classfilePath = JarUtils.classNameToClassfilePath(className);
            Resource classResource = this.classpathElement.getResource(classfilePath);
            ClasspathElement foundInClasspathElt = null;
            if (classResource != null) {
               foundInClasspathElt = this.classpathElement;
            } else {
               for(ClasspathElement classpathOrderElt : this.classpathOrder) {
                  if (classpathOrderElt != this.classpathElement) {
                     classResource = classpathOrderElt.getResource(classfilePath);
                     if (classResource != null) {
                        foundInClasspathElt = classpathOrderElt;
                        break;
                     }
                  }
               }
            }

            if (classResource != null) {
               if (log != null) {
                  classResource.scanLog = log.log("Extending scanning to external " + relationship + (foundInClasspathElt == this.classpathElement ? " in same classpath element" : " in classpath element " + foundInClasspathElt) + ": " + className);
               }

               if (this.additionalWorkUnits == null) {
                  this.additionalWorkUnits = new ArrayList();
               }

               this.additionalWorkUnits.add(new Scanner.ClassfileScanWorkUnit(foundInClasspathElt, classResource, true));
            } else if (log != null) {
               log.log("External " + relationship + " " + className + " was not found in non-blacklisted packages -- cannot extend scanning to this class");
            }
         }
      }

   }

   private void extendScanningUpwardsFromAnnotationParameterValues(Object annotationParamVal, LogNode log) {
      if (annotationParamVal != null) {
         if (annotationParamVal instanceof AnnotationInfo) {
            AnnotationInfo annotationInfo = (AnnotationInfo)annotationParamVal;
            this.scheduleScanningIfExternalClass(annotationInfo.getClassName(), "annotation class", log);

            for(AnnotationParameterValue apv : annotationInfo.getParameterValues()) {
               this.extendScanningUpwardsFromAnnotationParameterValues(apv.getValue(), log);
            }
         } else if (annotationParamVal instanceof AnnotationEnumValue) {
            this.scheduleScanningIfExternalClass(((AnnotationEnumValue)annotationParamVal).getClassName(), "enum class", log);
         } else if (annotationParamVal instanceof AnnotationClassRef) {
            this.scheduleScanningIfExternalClass(((AnnotationClassRef)annotationParamVal).getClassName(), "class ref", log);
         } else if (annotationParamVal.getClass().isArray()) {
            int i = 0;

            for(int n = Array.getLength(annotationParamVal); i < n; ++i) {
               this.extendScanningUpwardsFromAnnotationParameterValues(Array.get(annotationParamVal, i), log);
            }
         }
      }

   }

   private void extendScanningUpwards(LogNode log) {
      if (this.superclassName != null) {
         this.scheduleScanningIfExternalClass(this.superclassName, "superclass", log);
      }

      if (this.implementedInterfaces != null) {
         for(String interfaceName : this.implementedInterfaces) {
            this.scheduleScanningIfExternalClass(interfaceName, "interface", log);
         }
      }

      if (this.classAnnotations != null) {
         for(AnnotationInfo annotationInfo : this.classAnnotations) {
            this.scheduleScanningIfExternalClass(annotationInfo.getName(), "class annotation", log);
            this.extendScanningUpwardsFromAnnotationParameterValues(annotationInfo, log);
         }
      }

      if (this.annotationParamDefaultValues != null) {
         for(AnnotationParameterValue apv : this.annotationParamDefaultValues) {
            this.extendScanningUpwardsFromAnnotationParameterValues(apv.getValue(), log);
         }
      }

      if (this.methodInfoList != null) {
         for(MethodInfo methodInfo : this.methodInfoList) {
            if (methodInfo.annotationInfo != null) {
               for(AnnotationInfo methodAnnotationInfo : methodInfo.annotationInfo) {
                  this.scheduleScanningIfExternalClass(methodAnnotationInfo.getName(), "method annotation", log);
                  this.extendScanningUpwardsFromAnnotationParameterValues(methodAnnotationInfo, log);
               }

               if (methodInfo.parameterAnnotationInfo != null && methodInfo.parameterAnnotationInfo.length > 0) {
                  for(AnnotationInfo[] paramAnnInfoArr : methodInfo.parameterAnnotationInfo) {
                     if (paramAnnInfoArr != null && paramAnnInfoArr.length > 0) {
                        for(AnnotationInfo paramAnnInfo : paramAnnInfoArr) {
                           this.scheduleScanningIfExternalClass(paramAnnInfo.getName(), "method parameter annotation", log);
                           this.extendScanningUpwardsFromAnnotationParameterValues(paramAnnInfo, log);
                        }
                     }
                  }
               }
            }
         }
      }

      if (this.fieldInfoList != null) {
         for(FieldInfo fieldInfo : this.fieldInfoList) {
            if (fieldInfo.annotationInfo != null) {
               for(AnnotationInfo fieldAnnotationInfo : fieldInfo.annotationInfo) {
                  this.scheduleScanningIfExternalClass(fieldAnnotationInfo.getName(), "field annotation", log);
                  this.extendScanningUpwardsFromAnnotationParameterValues(fieldAnnotationInfo, log);
               }
            }
         }
      }

      if (this.classContainmentEntries != null) {
         for(Classfile.ClassContainment classContainmentEntry : this.classContainmentEntries) {
            if (classContainmentEntry.innerClassName.equals(this.className)) {
               this.scheduleScanningIfExternalClass(classContainmentEntry.outerClassName, "outer class", log);
            }
         }
      }

   }

   void link(Map<String, ClassInfo> classNameToClassInfo, Map<String, PackageInfo> packageNameToPackageInfo, Map<String, ModuleInfo> moduleNameToModuleInfo) {
      boolean isModuleDescriptor = false;
      boolean isPackageDescriptor = false;
      ClassInfo classInfo = null;
      if (this.className.equals("module-info")) {
         isModuleDescriptor = true;
      } else if (!this.className.equals("package-info") && !this.className.endsWith(".package-info")) {
         classInfo = ClassInfo.addScannedClass(this.className, this.classModifiers, this.isExternalClass, classNameToClassInfo, this.classpathElement, this.classfileResource);
         classInfo.setModifiers(this.classModifiers);
         classInfo.setIsInterface(this.isInterface);
         classInfo.setIsAnnotation(this.isAnnotation);
         if (this.superclassName != null) {
            classInfo.addSuperclass(this.superclassName, classNameToClassInfo);
         }

         if (this.implementedInterfaces != null) {
            for(String interfaceName : this.implementedInterfaces) {
               classInfo.addImplementedInterface(interfaceName, classNameToClassInfo);
            }
         }

         if (this.classAnnotations != null) {
            for(AnnotationInfo classAnnotation : this.classAnnotations) {
               classInfo.addClassAnnotation(classAnnotation, classNameToClassInfo);
            }
         }

         if (this.classContainmentEntries != null) {
            ClassInfo.addClassContainment(this.classContainmentEntries, classNameToClassInfo);
         }

         if (this.annotationParamDefaultValues != null) {
            classInfo.addAnnotationParamDefaultValues(this.annotationParamDefaultValues);
         }

         if (this.fullyQualifiedDefiningMethodName != null) {
            classInfo.addFullyQualifiedDefiningMethodName(this.fullyQualifiedDefiningMethodName);
         }

         if (this.fieldInfoList != null) {
            classInfo.addFieldInfo(this.fieldInfoList, classNameToClassInfo);
         }

         if (this.methodInfoList != null) {
            classInfo.addMethodInfo(this.methodInfoList, classNameToClassInfo);
         }

         if (this.typeSignature != null) {
            classInfo.setTypeSignature(this.typeSignature);
         }

         if (this.refdClassNames != null) {
            classInfo.addReferencedClassNames(this.refdClassNames);
         }
      } else {
         isPackageDescriptor = true;
      }

      PackageInfo packageInfo = null;
      if (!isModuleDescriptor) {
         String packageName = PackageInfo.getParentPackageName(this.className);
         packageInfo = PackageInfo.getOrCreatePackage(packageName, packageNameToPackageInfo);
         if (isPackageDescriptor) {
            packageInfo.addAnnotations(this.classAnnotations);
         } else if (classInfo != null) {
            packageInfo.addClassInfo(classInfo);
            classInfo.packageInfo = packageInfo;
         }
      }

      String moduleName = this.classpathElement.getModuleName();
      if (moduleName != null) {
         ModuleInfo moduleInfo = (ModuleInfo)moduleNameToModuleInfo.get(moduleName);
         if (moduleInfo == null) {
            moduleNameToModuleInfo.put(moduleName, moduleInfo = new ModuleInfo(this.classfileResource.getModuleRef(), this.classpathElement));
         }

         if (isModuleDescriptor) {
            moduleInfo.addAnnotations(this.classAnnotations);
         }

         if (classInfo != null) {
            moduleInfo.addClassInfo(classInfo);
            classInfo.moduleInfo = moduleInfo;
         }

         if (packageInfo != null) {
            moduleInfo.addPackageInfo(packageInfo);
         }
      }

   }

   private String intern(String str) {
      if (str == null) {
         return null;
      } else {
         String interned = (String)this.stringInternMap.putIfAbsent(str, str);
         return interned != null ? interned : str;
      }
   }

   private int getConstantPoolStringOffset(int cpIdx, int subFieldIdx) throws Classfile.ClassfileFormatException {
      if (cpIdx >= 1 && cpIdx < this.cpCount) {
         int t = this.entryTag[cpIdx];
         if ((t == 12 || subFieldIdx == 0) && (t != 12 || subFieldIdx == 0 || subFieldIdx == 1)) {
            if (t == 0) {
               return 0;
            } else {
               int cpIdxToUse;
               if (t == 1) {
                  cpIdxToUse = cpIdx;
               } else if (t != 7 && t != 8 && t != 19) {
                  if (t != 12) {
                     throw new Classfile.ClassfileFormatException("Wrong tag number " + t + " at constant pool index " + cpIdx + ", cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
                  }

                  int compoundIndirIdx = this.indirectStringRefs[cpIdx];
                  if (compoundIndirIdx == -1) {
                     throw new Classfile.ClassfileFormatException("Bad string indirection index, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
                  }

                  int indirIdx = (subFieldIdx == 0 ? compoundIndirIdx >> 16 : compoundIndirIdx) & '\uffff';
                  if (indirIdx == 0) {
                     throw new Classfile.ClassfileFormatException("Bad string indirection index, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
                  }

                  cpIdxToUse = indirIdx;
               } else {
                  int indirIdx = this.indirectStringRefs[cpIdx];
                  if (indirIdx == -1) {
                     throw new Classfile.ClassfileFormatException("Bad string indirection index, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
                  }

                  if (indirIdx == 0) {
                     return 0;
                  }

                  cpIdxToUse = indirIdx;
               }

               if (cpIdxToUse >= 1 && cpIdxToUse < this.cpCount) {
                  return this.entryOffset[cpIdxToUse];
               } else {
                  throw new Classfile.ClassfileFormatException("Constant pool index " + cpIdx + ", should be in range [1, " + (this.cpCount - 1) + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
               }
            }
         } else {
            throw new Classfile.ClassfileFormatException("Bad subfield index " + subFieldIdx + " for tag " + t + ", cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
         }
      } else {
         throw new Classfile.ClassfileFormatException("Constant pool index " + cpIdx + ", should be in range [1, " + (this.cpCount - 1) + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
      }
   }

   private String getConstantPoolString(int cpIdx, boolean replaceSlashWithDot, boolean stripLSemicolon) throws Classfile.ClassfileFormatException, IOException {
      int constantPoolStringOffset = this.getConstantPoolStringOffset(cpIdx, 0);
      return constantPoolStringOffset == 0 ? null : this.intern(this.inputStreamOrByteBuffer.readString(constantPoolStringOffset, replaceSlashWithDot, stripLSemicolon));
   }

   private String getConstantPoolString(int cpIdx, int subFieldIdx) throws Classfile.ClassfileFormatException, IOException {
      int constantPoolStringOffset = this.getConstantPoolStringOffset(cpIdx, subFieldIdx);
      return constantPoolStringOffset == 0 ? null : this.intern(this.inputStreamOrByteBuffer.readString(constantPoolStringOffset, false, false));
   }

   private String getConstantPoolString(int cpIdx) throws Classfile.ClassfileFormatException, IOException {
      return this.getConstantPoolString(cpIdx, 0);
   }

   private byte getConstantPoolStringFirstByte(int cpIdx) throws Classfile.ClassfileFormatException, IOException {
      int constantPoolStringOffset = this.getConstantPoolStringOffset(cpIdx, 0);
      if (constantPoolStringOffset == 0) {
         return 0;
      } else {
         int utfLen = this.inputStreamOrByteBuffer.readUnsignedShort(constantPoolStringOffset);
         return utfLen == 0 ? 0 : this.inputStreamOrByteBuffer.buf[constantPoolStringOffset + 2];
      }
   }

   private String getConstantPoolClassName(int cpIdx) throws Classfile.ClassfileFormatException, IOException {
      return this.getConstantPoolString(cpIdx, true, false);
   }

   private String getConstantPoolClassDescriptor(int cpIdx) throws Classfile.ClassfileFormatException, IOException {
      return this.getConstantPoolString(cpIdx, true, true);
   }

   private boolean constantPoolStringEquals(int cpIdx, String asciiString) throws Classfile.ClassfileFormatException, IOException {
      int strOffset = this.getConstantPoolStringOffset(cpIdx, 0);
      if (strOffset == 0) {
         return asciiString == null;
      } else if (asciiString == null) {
         return false;
      } else {
         int strLen = this.inputStreamOrByteBuffer.readUnsignedShort(strOffset);
         int otherLen = asciiString.length();
         if (strLen != otherLen) {
            return false;
         } else {
            int strStart = strOffset + 2;

            for(int i = 0; i < strLen; ++i) {
               if ((char)(this.inputStreamOrByteBuffer.buf[strStart + i] & 255) != asciiString.charAt(i)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private int cpReadUnsignedShort(int cpIdx) throws IOException {
      if (cpIdx >= 1 && cpIdx < this.cpCount) {
         return this.inputStreamOrByteBuffer.readUnsignedShort(this.entryOffset[cpIdx]);
      } else {
         throw new Classfile.ClassfileFormatException("Constant pool index " + cpIdx + ", should be in range [1, " + (this.cpCount - 1) + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
      }
   }

   private int cpReadInt(int cpIdx) throws IOException {
      if (cpIdx >= 1 && cpIdx < this.cpCount) {
         return this.inputStreamOrByteBuffer.readInt(this.entryOffset[cpIdx]);
      } else {
         throw new Classfile.ClassfileFormatException("Constant pool index " + cpIdx + ", should be in range [1, " + (this.cpCount - 1) + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
      }
   }

   private long cpReadLong(int cpIdx) throws IOException {
      if (cpIdx >= 1 && cpIdx < this.cpCount) {
         return this.inputStreamOrByteBuffer.readLong(this.entryOffset[cpIdx]);
      } else {
         throw new Classfile.ClassfileFormatException("Constant pool index " + cpIdx + ", should be in range [1, " + (this.cpCount - 1) + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
      }
   }

   private Object getFieldConstantPoolValue(int tag, char fieldTypeDescriptorFirstChar, int cpIdx) throws Classfile.ClassfileFormatException, IOException {
      switch(tag) {
      case 1:
      case 7:
      case 8:
         return this.getConstantPoolString(cpIdx);
      case 2:
      default:
         throw new Classfile.ClassfileFormatException("Unknown constant pool tag " + tag + ", cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
      case 3:
         int intVal = this.cpReadInt(cpIdx);
         switch(fieldTypeDescriptorFirstChar) {
         case 'B':
            return (byte)intVal;
         case 'C':
            return (char)intVal;
         case 'I':
            return intVal;
         case 'S':
            return (short)intVal;
         case 'Z':
            return intVal != 0;
         default:
            throw new Classfile.ClassfileFormatException("Unknown Constant_INTEGER type " + fieldTypeDescriptorFirstChar + ", cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
         }
      case 4:
         return Float.intBitsToFloat(this.cpReadInt(cpIdx));
      case 5:
         return this.cpReadLong(cpIdx);
      case 6:
         return Double.longBitsToDouble(this.cpReadLong(cpIdx));
      }
   }

   private AnnotationInfo readAnnotation() throws IOException {
      String annotationClassName = this.getConstantPoolClassDescriptor(this.inputStreamOrByteBuffer.readUnsignedShort());
      int numElementValuePairs = this.inputStreamOrByteBuffer.readUnsignedShort();
      AnnotationParameterValueList paramVals = null;
      if (numElementValuePairs > 0) {
         paramVals = new AnnotationParameterValueList(numElementValuePairs);

         for(int i = 0; i < numElementValuePairs; ++i) {
            String paramName = this.getConstantPoolString(this.inputStreamOrByteBuffer.readUnsignedShort());
            Object paramValue = this.readAnnotationElementValue();
            paramVals.add(new AnnotationParameterValue(paramName, paramValue));
         }
      }

      return new AnnotationInfo(annotationClassName, paramVals);
   }

   private Object readAnnotationElementValue() throws IOException {
      int tag = (char)this.inputStreamOrByteBuffer.readUnsignedByte();
      switch(tag) {
      case 64:
         return this.readAnnotation();
      case 65:
      case 69:
      case 71:
      case 72:
      case 75:
      case 76:
      case 77:
      case 78:
      case 79:
      case 80:
      case 81:
      case 82:
      case 84:
      case 85:
      case 86:
      case 87:
      case 88:
      case 89:
      case 92:
      case 93:
      case 94:
      case 95:
      case 96:
      case 97:
      case 98:
      case 100:
      case 102:
      case 103:
      case 104:
      case 105:
      case 106:
      case 107:
      case 108:
      case 109:
      case 110:
      case 111:
      case 112:
      case 113:
      case 114:
      default:
         throw new Classfile.ClassfileFormatException("Class " + this.className + " has unknown annotation element type tag '" + (char)tag + "': element size unknown, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
      case 66:
         return (byte)this.cpReadInt(this.inputStreamOrByteBuffer.readUnsignedShort());
      case 67:
         return (char)this.cpReadInt(this.inputStreamOrByteBuffer.readUnsignedShort());
      case 68:
         return Double.longBitsToDouble(this.cpReadLong(this.inputStreamOrByteBuffer.readUnsignedShort()));
      case 70:
         return Float.intBitsToFloat(this.cpReadInt(this.inputStreamOrByteBuffer.readUnsignedShort()));
      case 73:
         return this.cpReadInt(this.inputStreamOrByteBuffer.readUnsignedShort());
      case 74:
         return this.cpReadLong(this.inputStreamOrByteBuffer.readUnsignedShort());
      case 83:
         return (short)this.cpReadUnsignedShort(this.inputStreamOrByteBuffer.readUnsignedShort());
      case 90:
         return this.cpReadInt(this.inputStreamOrByteBuffer.readUnsignedShort()) != 0;
      case 91:
         int count = this.inputStreamOrByteBuffer.readUnsignedShort();
         Object[] arr = new Object[count];

         for(int i = 0; i < count; ++i) {
            arr[i] = this.readAnnotationElementValue();
         }

         return arr;
      case 99:
         String classRefTypeDescriptor = this.getConstantPoolString(this.inputStreamOrByteBuffer.readUnsignedShort());
         return new AnnotationClassRef(classRefTypeDescriptor);
      case 101:
         String annotationClassName = this.getConstantPoolClassDescriptor(this.inputStreamOrByteBuffer.readUnsignedShort());
         String annotationConstName = this.getConstantPoolString(this.inputStreamOrByteBuffer.readUnsignedShort());
         return new AnnotationEnumValue(annotationClassName, annotationConstName);
      case 115:
         return this.getConstantPoolString(this.inputStreamOrByteBuffer.readUnsignedShort());
      }
   }

   private void readConstantPoolEntries() throws IOException {
      List<Integer> classNameCpIdxs = null;
      List<Integer> typeSignatureIdxs = null;
      if (this.scanSpec.enableInterClassDependencies) {
         classNameCpIdxs = new ArrayList();
         typeSignatureIdxs = new ArrayList();
      }

      this.cpCount = this.inputStreamOrByteBuffer.readUnsignedShort();
      this.entryOffset = new int[this.cpCount];
      this.entryTag = new int[this.cpCount];
      this.indirectStringRefs = new int[this.cpCount];
      Arrays.fill(this.indirectStringRefs, 0, this.cpCount, -1);
      int i = 1;

      for(int skipSlot = 0; i < this.cpCount; ++i) {
         if (skipSlot == 1) {
            skipSlot = 0;
         } else {
            this.entryTag[i] = this.inputStreamOrByteBuffer.readUnsignedByte();
            this.entryOffset[i] = this.inputStreamOrByteBuffer.curr;
            switch(this.entryTag[i]) {
            case 0:
               throw new Classfile.ClassfileFormatException("Unknown constant pool tag 0 in classfile " + this.relativePath + " (possible buffer underflow issue). Please report this at https://github.com/classgraph/classgraph/issues");
            case 1:
               int strLen = this.inputStreamOrByteBuffer.readUnsignedShort();
               this.inputStreamOrByteBuffer.skip(strLen);
               break;
            case 2:
            case 13:
            case 14:
            case 17:
            default:
               throw new Classfile.ClassfileFormatException("Unknown constant pool tag " + this.entryTag[i] + " (element size unknown, cannot continue reading class). Please report this at https://github.com/classgraph/classgraph/issues");
            case 3:
            case 4:
               this.inputStreamOrByteBuffer.skip(4);
               break;
            case 5:
            case 6:
               this.inputStreamOrByteBuffer.skip(8);
               skipSlot = 1;
               break;
            case 7:
               this.indirectStringRefs[i] = this.inputStreamOrByteBuffer.readUnsignedShort();
               if (classNameCpIdxs != null) {
                  classNameCpIdxs.add(Integer.valueOf(this.indirectStringRefs[i]));
               }
               break;
            case 8:
               this.indirectStringRefs[i] = this.inputStreamOrByteBuffer.readUnsignedShort();
               break;
            case 9:
               this.inputStreamOrByteBuffer.skip(4);
               break;
            case 10:
               this.inputStreamOrByteBuffer.skip(4);
               break;
            case 11:
               this.inputStreamOrByteBuffer.skip(4);
               break;
            case 12:
               int nameRef = this.inputStreamOrByteBuffer.readUnsignedShort();
               int typeRef = this.inputStreamOrByteBuffer.readUnsignedShort();
               if (typeSignatureIdxs != null) {
                  typeSignatureIdxs.add(Integer.valueOf(typeRef));
               }

               this.indirectStringRefs[i] = nameRef << 16 | typeRef;
               break;
            case 15:
               this.inputStreamOrByteBuffer.skip(3);
               break;
            case 16:
               this.inputStreamOrByteBuffer.skip(2);
               break;
            case 18:
               this.inputStreamOrByteBuffer.skip(4);
               break;
            case 19:
               this.indirectStringRefs[i] = this.inputStreamOrByteBuffer.readUnsignedShort();
               break;
            case 20:
               this.inputStreamOrByteBuffer.skip(2);
            }
         }
      }

      if (classNameCpIdxs != null) {
         this.refdClassNames = new HashSet();
         Iterator var10 = classNameCpIdxs.iterator();

         while(var10.hasNext()) {
            int cpIdx = ((Integer)var10.next()).intValue();
            String refdClassName = this.getConstantPoolString(cpIdx, true, false);
            if (refdClassName != null) {
               if (refdClassName.startsWith("[")) {
                  try {
                     TypeSignature typeSig = TypeSignature.parse(refdClassName.replace('.', '/'), (String)null);
                     typeSig.findReferencedClassNames(this.refdClassNames);
                  } catch (ParseException var8) {
                     throw new Classfile.ClassfileFormatException("Could not parse class name: " + refdClassName, var8);
                  }
               } else {
                  this.refdClassNames.add(refdClassName);
               }
            }
         }
      }

      if (typeSignatureIdxs != null) {
         Iterator var11 = typeSignatureIdxs.iterator();

         while(var11.hasNext()) {
            int cpIdx = ((Integer)var11.next()).intValue();
            String typeSigStr = this.getConstantPoolString(cpIdx);
            if (typeSigStr != null) {
               try {
                  if (typeSigStr.indexOf(40) < 0 && !"<init>".equals(typeSigStr)) {
                     TypeSignature typeSig = TypeSignature.parse(typeSigStr, (String)null);
                     typeSig.findReferencedClassNames(this.refdClassNames);
                  } else {
                     MethodTypeSignature typeSig = MethodTypeSignature.parse(typeSigStr, (String)null);
                     typeSig.findReferencedClassNames(this.refdClassNames);
                  }
               } catch (ParseException var9) {
                  throw new Classfile.ClassfileFormatException("Could not parse type signature: " + typeSigStr, var9);
               }
            }
         }
      }

   }

   private void readBasicClassInfo() throws IOException, Classfile.ClassfileFormatException, Classfile.SkipClassException {
      this.classModifiers = this.inputStreamOrByteBuffer.readUnsignedShort();
      this.isInterface = (this.classModifiers & 512) != 0;
      this.isAnnotation = (this.classModifiers & 8192) != 0;
      String classNamePath = this.getConstantPoolString(this.inputStreamOrByteBuffer.readUnsignedShort());
      if (classNamePath == null) {
         throw new Classfile.ClassfileFormatException("Class name is null");
      } else {
         this.className = classNamePath.replace('/', '.');
         if ("java.lang.Object".equals(this.className)) {
            throw new Classfile.SkipClassException("No need to scan java.lang.Object");
         } else {
            boolean isModule = (this.classModifiers & '耀') != 0;
            boolean isPackage = this.relativePath.regionMatches(this.relativePath.lastIndexOf(47) + 1, "package-info.class", 0, 18);
            if (!this.scanSpec.ignoreClassVisibility && !Modifier.isPublic(this.classModifiers) && !isModule && !isPackage) {
               throw new Classfile.SkipClassException("Class is not public, and ignoreClassVisibility() was not called");
            } else if (!this.relativePath.endsWith(".class")) {
               throw new Classfile.SkipClassException("Classfile filename " + this.relativePath + " does not end in \".class\"");
            } else {
               int len = classNamePath.length();
               if (this.relativePath.length() == len + 6 && classNamePath.regionMatches(0, this.relativePath, 0, len)) {
                  int superclassNameCpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
                  if (superclassNameCpIdx > 0) {
                     this.superclassName = this.getConstantPoolClassName(superclassNameCpIdx);
                  }

               } else {
                  throw new Classfile.SkipClassException("Relative path " + this.relativePath + " does not match class name " + this.className);
               }
            }
         }
      }
   }

   private void readInterfaces() throws IOException {
      int interfaceCount = this.inputStreamOrByteBuffer.readUnsignedShort();

      for(int i = 0; i < interfaceCount; ++i) {
         String interfaceName = this.getConstantPoolClassName(this.inputStreamOrByteBuffer.readUnsignedShort());
         if (this.implementedInterfaces == null) {
            this.implementedInterfaces = new ArrayList();
         }

         this.implementedInterfaces.add(interfaceName);
      }

   }

   private void readFields() throws IOException, Classfile.ClassfileFormatException {
      int fieldCount = this.inputStreamOrByteBuffer.readUnsignedShort();

      for(int i = 0; i < fieldCount; ++i) {
         int fieldModifierFlags = this.inputStreamOrByteBuffer.readUnsignedShort();
         boolean isPublicField = (fieldModifierFlags & 1) == 1;
         boolean fieldIsVisible = isPublicField || this.scanSpec.ignoreFieldVisibility;
         boolean getStaticFinalFieldConstValue = this.scanSpec.enableStaticFinalFieldConstantInitializerValues && fieldIsVisible;
         if (!fieldIsVisible || !this.scanSpec.enableFieldInfo && !getStaticFinalFieldConstValue) {
            this.inputStreamOrByteBuffer.readUnsignedShort();
            this.inputStreamOrByteBuffer.readUnsignedShort();
            int attributesCount = this.inputStreamOrByteBuffer.readUnsignedShort();

            for(int j = 0; j < attributesCount; ++j) {
               this.inputStreamOrByteBuffer.readUnsignedShort();
               int attributeLength = this.inputStreamOrByteBuffer.readInt();
               this.inputStreamOrByteBuffer.skip(attributeLength);
            }
         } else {
            int fieldNameCpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
            String fieldName = this.getConstantPoolString(fieldNameCpIdx);
            int fieldTypeDescriptorCpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
            char fieldTypeDescriptorFirstChar = (char)this.getConstantPoolStringFirstByte(fieldTypeDescriptorCpIdx);
            String fieldTypeSignature = null;
            String fieldTypeDescriptor = this.getConstantPoolString(fieldTypeDescriptorCpIdx);
            Object fieldConstValue = null;
            AnnotationInfoList fieldAnnotationInfo = null;
            int attributesCount = this.inputStreamOrByteBuffer.readUnsignedShort();

            for(int j = 0; j < attributesCount; ++j) {
               int attributeNameCpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
               int attributeLength = this.inputStreamOrByteBuffer.readInt();
               if (getStaticFinalFieldConstValue && this.constantPoolStringEquals(attributeNameCpIdx, "ConstantValue")) {
                  int cpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
                  if (cpIdx < 1 || cpIdx >= this.cpCount) {
                     throw new Classfile.ClassfileFormatException("Constant pool index " + cpIdx + ", should be in range [1, " + (this.cpCount - 1) + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
                  }

                  fieldConstValue = this.getFieldConstantPoolValue(this.entryTag[cpIdx], fieldTypeDescriptorFirstChar, cpIdx);
               } else if (fieldIsVisible && this.constantPoolStringEquals(attributeNameCpIdx, "Signature")) {
                  fieldTypeSignature = this.getConstantPoolString(this.inputStreamOrByteBuffer.readUnsignedShort());
               } else if (this.scanSpec.enableAnnotationInfo && (this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleAnnotations") || !this.scanSpec.disableRuntimeInvisibleAnnotations && this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleAnnotations"))) {
                  int fieldAnnotationCount = this.inputStreamOrByteBuffer.readUnsignedShort();
                  if (fieldAnnotationCount > 0) {
                     if (fieldAnnotationInfo == null) {
                        fieldAnnotationInfo = new AnnotationInfoList(1);
                     }

                     for(int k = 0; k < fieldAnnotationCount; ++k) {
                        AnnotationInfo fieldAnnotation = this.readAnnotation();
                        fieldAnnotationInfo.add(fieldAnnotation);
                     }
                  }
               } else {
                  this.inputStreamOrByteBuffer.skip(attributeLength);
               }
            }

            if (this.scanSpec.enableFieldInfo && fieldIsVisible) {
               if (this.fieldInfoList == null) {
                  this.fieldInfoList = new FieldInfoList();
               }

               this.fieldInfoList.add(new FieldInfo(this.className, fieldName, fieldModifierFlags, fieldTypeDescriptor, fieldTypeSignature, fieldConstValue, fieldAnnotationInfo));
            }
         }
      }

   }

   private void readMethods() throws IOException, Classfile.ClassfileFormatException {
      int methodCount = this.inputStreamOrByteBuffer.readUnsignedShort();

      for(int i = 0; i < methodCount; ++i) {
         int methodModifierFlags = this.inputStreamOrByteBuffer.readUnsignedShort();
         boolean isPublicMethod = (methodModifierFlags & 1) == 1;
         boolean methodIsVisible = isPublicMethod || this.scanSpec.ignoreMethodVisibility;
         String methodName = null;
         String methodTypeDescriptor = null;
         String methodTypeSignature = null;
         boolean enableMethodInfo = this.scanSpec.enableMethodInfo || this.isAnnotation;
         if (!enableMethodInfo && !this.isAnnotation) {
            this.inputStreamOrByteBuffer.skip(4);
         } else {
            int methodNameCpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
            methodName = this.getConstantPoolString(methodNameCpIdx);
            int methodTypeDescriptorCpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
            methodTypeDescriptor = this.getConstantPoolString(methodTypeDescriptorCpIdx);
         }

         int attributesCount = this.inputStreamOrByteBuffer.readUnsignedShort();
         String[] methodParameterNames = null;
         int[] methodParameterModifiers = null;
         AnnotationInfo[][] methodParameterAnnotations = null;
         AnnotationInfoList methodAnnotationInfo = null;
         boolean methodHasBody = false;
         if (methodIsVisible && (enableMethodInfo || this.isAnnotation)) {
            for(int j = 0; j < attributesCount; ++j) {
               int attributeNameCpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
               int attributeLength = this.inputStreamOrByteBuffer.readInt();
               if (!this.scanSpec.enableAnnotationInfo || !this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleAnnotations") && (this.scanSpec.disableRuntimeInvisibleAnnotations || !this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleAnnotations"))) {
                  if (this.scanSpec.enableAnnotationInfo && (this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleParameterAnnotations") || !this.scanSpec.disableRuntimeInvisibleAnnotations && this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleParameterAnnotations"))) {
                     int numParams = this.inputStreamOrByteBuffer.readUnsignedByte();
                     if (methodParameterAnnotations == null) {
                        methodParameterAnnotations = new AnnotationInfo[numParams][];
                     } else if (methodParameterAnnotations.length != numParams) {
                        throw new Classfile.ClassfileFormatException("Mismatch in number of parameters between RuntimeVisibleParameterAnnotations and RuntimeInvisibleParameterAnnotations");
                     }

                     for(int paramIdx = 0; paramIdx < numParams; ++paramIdx) {
                        int numAnnotations = this.inputStreamOrByteBuffer.readUnsignedShort();
                        if (numAnnotations > 0) {
                           int annStartIdx = 0;
                           if (methodParameterAnnotations[paramIdx] != null) {
                              annStartIdx = methodParameterAnnotations[paramIdx].length;
                              methodParameterAnnotations[paramIdx] = (AnnotationInfo[])Arrays.copyOf(methodParameterAnnotations[paramIdx], annStartIdx + numAnnotations);
                           } else {
                              methodParameterAnnotations[paramIdx] = new AnnotationInfo[numAnnotations];
                           }

                           for(int annIdx = 0; annIdx < numAnnotations; ++annIdx) {
                              methodParameterAnnotations[paramIdx][annStartIdx + annIdx] = this.readAnnotation();
                           }
                        } else if (methodParameterAnnotations[paramIdx] == null) {
                           methodParameterAnnotations[paramIdx] = NO_ANNOTATIONS;
                        }
                     }
                  } else if (this.constantPoolStringEquals(attributeNameCpIdx, "MethodParameters")) {
                     int paramCount = this.inputStreamOrByteBuffer.readUnsignedByte();
                     methodParameterNames = new String[paramCount];
                     methodParameterModifiers = new int[paramCount];

                     for(int k = 0; k < paramCount; ++k) {
                        int cpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
                        methodParameterNames[k] = cpIdx == 0 ? null : this.getConstantPoolString(cpIdx);
                        methodParameterModifiers[k] = this.inputStreamOrByteBuffer.readUnsignedShort();
                     }
                  } else if (this.constantPoolStringEquals(attributeNameCpIdx, "Signature")) {
                     methodTypeSignature = this.getConstantPoolString(this.inputStreamOrByteBuffer.readUnsignedShort());
                  } else if (this.constantPoolStringEquals(attributeNameCpIdx, "AnnotationDefault")) {
                     if (this.annotationParamDefaultValues == null) {
                        this.annotationParamDefaultValues = new AnnotationParameterValueList();
                     }

                     this.annotationParamDefaultValues.add(new AnnotationParameterValue(methodName, this.readAnnotationElementValue()));
                  } else if (this.constantPoolStringEquals(attributeNameCpIdx, "Code")) {
                     methodHasBody = true;
                     this.inputStreamOrByteBuffer.skip(attributeLength);
                  } else {
                     this.inputStreamOrByteBuffer.skip(attributeLength);
                  }
               } else {
                  int methodAnnotationCount = this.inputStreamOrByteBuffer.readUnsignedShort();
                  if (methodAnnotationCount > 0) {
                     if (methodAnnotationInfo == null) {
                        methodAnnotationInfo = new AnnotationInfoList(1);
                     }

                     for(int k = 0; k < methodAnnotationCount; ++k) {
                        AnnotationInfo annotationInfo = this.readAnnotation();
                        methodAnnotationInfo.add(annotationInfo);
                     }
                  }
               }
            }

            if (enableMethodInfo) {
               if (this.methodInfoList == null) {
                  this.methodInfoList = new MethodInfoList();
               }

               this.methodInfoList.add(new MethodInfo(this.className, methodName, methodAnnotationInfo, methodModifierFlags, methodTypeDescriptor, methodTypeSignature, methodParameterNames, methodParameterModifiers, methodParameterAnnotations, methodHasBody));
            }
         } else {
            for(int j = 0; j < attributesCount; ++j) {
               this.inputStreamOrByteBuffer.skip(2);
               int attributeLength = this.inputStreamOrByteBuffer.readInt();
               this.inputStreamOrByteBuffer.skip(attributeLength);
            }
         }
      }

   }

   private void readClassAttributes() throws IOException, Classfile.ClassfileFormatException {
      int attributesCount = this.inputStreamOrByteBuffer.readUnsignedShort();

      for(int i = 0; i < attributesCount; ++i) {
         int attributeNameCpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
         int attributeLength = this.inputStreamOrByteBuffer.readInt();
         if (!this.scanSpec.enableAnnotationInfo || !this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleAnnotations") && (this.scanSpec.disableRuntimeInvisibleAnnotations || !this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleAnnotations"))) {
            if (this.constantPoolStringEquals(attributeNameCpIdx, "InnerClasses")) {
               int numInnerClasses = this.inputStreamOrByteBuffer.readUnsignedShort();

               for(int j = 0; j < numInnerClasses; ++j) {
                  int innerClassInfoCpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
                  int outerClassInfoCpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
                  this.inputStreamOrByteBuffer.skip(2);
                  int innerClassAccessFlags = this.inputStreamOrByteBuffer.readUnsignedShort();
                  if (innerClassInfoCpIdx != 0 && outerClassInfoCpIdx != 0) {
                     String innerClassName = this.getConstantPoolClassName(innerClassInfoCpIdx);
                     String outerClassName = this.getConstantPoolClassName(outerClassInfoCpIdx);
                     if (this.classContainmentEntries == null) {
                        this.classContainmentEntries = new ArrayList();
                     }

                     this.classContainmentEntries.add(new Classfile.ClassContainment(innerClassName, innerClassAccessFlags, outerClassName));
                  }
               }
            } else if (this.constantPoolStringEquals(attributeNameCpIdx, "Signature")) {
               this.typeSignature = this.getConstantPoolString(this.inputStreamOrByteBuffer.readUnsignedShort());
            } else if (this.constantPoolStringEquals(attributeNameCpIdx, "EnclosingMethod")) {
               String innermostEnclosingClassName = this.getConstantPoolClassName(this.inputStreamOrByteBuffer.readUnsignedShort());
               int enclosingMethodCpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
               String definingMethodName;
               if (enclosingMethodCpIdx == 0) {
                  definingMethodName = "<clinit>";
               } else {
                  definingMethodName = this.getConstantPoolString(enclosingMethodCpIdx, 0);
               }

               if (this.classContainmentEntries == null) {
                  this.classContainmentEntries = new ArrayList();
               }

               this.classContainmentEntries.add(new Classfile.ClassContainment(this.className, this.classModifiers, innermostEnclosingClassName));
               this.fullyQualifiedDefiningMethodName = innermostEnclosingClassName + "." + definingMethodName;
            } else if (this.constantPoolStringEquals(attributeNameCpIdx, "Module")) {
               int moduleNameCpIdx = this.inputStreamOrByteBuffer.readUnsignedShort();
               this.classpathElement.moduleNameFromModuleDescriptor = this.getConstantPoolString(moduleNameCpIdx);
               this.inputStreamOrByteBuffer.skip(attributeLength - 2);
            } else {
               this.inputStreamOrByteBuffer.skip(attributeLength);
            }
         } else {
            int annotationCount = this.inputStreamOrByteBuffer.readUnsignedShort();
            if (annotationCount > 0) {
               if (this.classAnnotations == null) {
                  this.classAnnotations = new AnnotationInfoList();
               }

               for(int m = 0; m < annotationCount; ++m) {
                  this.classAnnotations.add(this.readAnnotation());
               }
            }
         }
      }

   }

   Classfile(ClasspathElement classpathElement, List<ClasspathElement> classpathOrder, Set<String> whitelistedClassNamesFound, Set<String> classNamesScheduledForExtendedScanning, String relativePath, Resource classfileResource, boolean isExternalClass, ConcurrentHashMap<String, String> stringInternMap, WorkQueue<Scanner.ClassfileScanWorkUnit> workQueue, ScanSpec scanSpec, LogNode log) throws IOException, Classfile.ClassfileFormatException, Classfile.SkipClassException {
      this.classpathElement = classpathElement;
      this.classpathOrder = classpathOrder;
      this.relativePath = relativePath;
      this.whitelistedClassNamesFound = whitelistedClassNamesFound;
      this.classNamesScheduledForExtendedScanning = classNamesScheduledForExtendedScanning;
      this.classfileResource = classfileResource;
      this.isExternalClass = isExternalClass;
      this.stringInternMap = stringInternMap;
      this.scanSpec = scanSpec;

      try {
         this.inputStreamOrByteBuffer = classfileResource.openOrRead();
         if (this.inputStreamOrByteBuffer.readInt() != -889275714) {
            throw new Classfile.ClassfileFormatException("Classfile does not have correct magic number");
         }

         this.inputStreamOrByteBuffer.readUnsignedShort();
         this.inputStreamOrByteBuffer.readUnsignedShort();
         this.readConstantPoolEntries();
         this.readBasicClassInfo();
         this.readInterfaces();
         this.readFields();
         this.readMethods();
         this.readClassAttributes();
      } finally {
         classfileResource.close();
         this.inputStreamOrByteBuffer = null;
      }

      LogNode subLog = log == null ? null : log.log("Found " + (this.isAnnotation ? "annotation class" : (this.isInterface ? "interface class" : "class")) + " " + this.className);
      if (subLog != null) {
         if (this.superclassName != null) {
            subLog.log("Super" + (this.isInterface && !this.isAnnotation ? "interface" : "class") + ": " + this.superclassName);
         }

         if (this.implementedInterfaces != null) {
            subLog.log("Interfaces: " + Join.join(", ", this.implementedInterfaces));
         }

         if (this.classAnnotations != null) {
            subLog.log("Class annotations: " + Join.join(", ", this.classAnnotations));
         }

         if (this.annotationParamDefaultValues != null) {
            for(AnnotationParameterValue apv : this.annotationParamDefaultValues) {
               subLog.log("Annotation default param value: " + apv);
            }
         }

         if (this.fieldInfoList != null) {
            for(FieldInfo fieldInfo : this.fieldInfoList) {
               subLog.log("Field: " + fieldInfo);
            }
         }

         if (this.methodInfoList != null) {
            for(MethodInfo methodInfo : this.methodInfoList) {
               subLog.log("Method: " + methodInfo);
            }
         }

         if (this.typeSignature != null) {
            ClassTypeSignature typeSig = null;

            try {
               typeSig = ClassTypeSignature.parse(this.typeSignature, (ClassInfo)null);
               if (this.refdClassNames != null) {
                  typeSig.findReferencedClassNames(this.refdClassNames);
               }
            } catch (ParseException var17) {
               ;
            }

            subLog.log("Class type signature: " + (typeSig == null ? this.typeSignature : typeSig.toString(this.className, false, this.classModifiers, this.isAnnotation, this.isInterface)));
         }

         if (this.refdClassNames != null) {
            List<String> refdClassNamesSorted = new ArrayList(this.refdClassNames);
            CollectionUtils.sortIfNotEmpty(refdClassNamesSorted);
            subLog.log("Referenced class names: " + Join.join(", ", refdClassNamesSorted));
         }
      }

      if (scanSpec.extendScanningUpwardsToExternalClasses) {
         this.extendScanningUpwards(subLog);
         if (this.additionalWorkUnits != null) {
            workQueue.addWorkUnits(this.additionalWorkUnits);
         }
      }

   }

   static class ClassContainment {
      public final String innerClassName;
      public final int innerClassModifierBits;
      public final String outerClassName;

      public ClassContainment(String innerClassName, int innerClassModifierBits, String outerClassName) {
         this.innerClassName = innerClassName;
         this.innerClassModifierBits = innerClassModifierBits;
         this.outerClassName = outerClassName;
      }
   }

   static class ClassfileFormatException extends IOException {
      static final long serialVersionUID = 1L;

      public ClassfileFormatException(String message) {
         super(message);
      }

      public ClassfileFormatException(String message, Throwable cause) {
         super(message, cause);
      }

      public synchronized Throwable fillInStackTrace() {
         return this;
      }
   }

   static class SkipClassException extends IOException {
      static final long serialVersionUID = 1L;

      public SkipClassException(String message) {
         super(message);
      }

      public synchronized Throwable fillInStackTrace() {
         return this;
      }
   }
}
