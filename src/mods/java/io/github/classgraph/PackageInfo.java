package io.github.classgraph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public class PackageInfo implements Comparable<PackageInfo>, HasName {
   private String name;
   private Set<AnnotationInfo> annotationInfoSet;
   private AnnotationInfoList annotationInfo;
   private PackageInfo parent;
   private Set<PackageInfo> children;
   private Map<String, ClassInfo> memberClassNameToClassInfo;

   PackageInfo() {
   }

   PackageInfo(String packageName) {
      this.name = packageName;
   }

   public String getName() {
      return this.name;
   }

   void addAnnotations(AnnotationInfoList packageAnnotations) {
      if (packageAnnotations != null && !packageAnnotations.isEmpty()) {
         if (this.annotationInfoSet == null) {
            this.annotationInfoSet = new LinkedHashSet();
         }

         this.annotationInfoSet.addAll(packageAnnotations);
      }

   }

   void addClassInfo(ClassInfo classInfo) {
      if (this.memberClassNameToClassInfo == null) {
         this.memberClassNameToClassInfo = new HashMap();
      }

      this.memberClassNameToClassInfo.put(classInfo.getName(), classInfo);
   }

   public AnnotationInfo getAnnotationInfo(String annotationName) {
      return (AnnotationInfo)this.getAnnotationInfo().get(annotationName);
   }

   public AnnotationInfoList getAnnotationInfo() {
      if (this.annotationInfo == null) {
         if (this.annotationInfoSet == null) {
            this.annotationInfo = AnnotationInfoList.EMPTY_LIST;
         } else {
            this.annotationInfo = new AnnotationInfoList();
            this.annotationInfo.addAll(this.annotationInfoSet);
         }
      }

      return this.annotationInfo;
   }

   public boolean hasAnnotation(String annotationName) {
      return this.getAnnotationInfo().containsName(annotationName);
   }

   public PackageInfo getParent() {
      return this.parent;
   }

   public PackageInfoList getChildren() {
      if (this.children == null) {
         return PackageInfoList.EMPTY_LIST;
      } else {
         PackageInfoList childrenSorted = new PackageInfoList(this.children);
         CollectionUtils.sortIfNotEmpty(childrenSorted, new Comparator<PackageInfo>() {
            public int compare(PackageInfo o1, PackageInfo o2) {
               return o1.name.compareTo(o2.name);
            }
         });
         return childrenSorted;
      }
   }

   public ClassInfo getClassInfo(String className) {
      return this.memberClassNameToClassInfo == null ? null : (ClassInfo)this.memberClassNameToClassInfo.get(className);
   }

   public ClassInfoList getClassInfo() {
      return this.memberClassNameToClassInfo == null ? ClassInfoList.EMPTY_LIST : new ClassInfoList(new HashSet(this.memberClassNameToClassInfo.values()), true);
   }

   private void obtainClassInfoRecursive(Set<ClassInfo> reachableClassInfo) {
      if (this.memberClassNameToClassInfo != null) {
         reachableClassInfo.addAll(this.memberClassNameToClassInfo.values());
      }

      for(PackageInfo subPackageInfo : this.getChildren()) {
         subPackageInfo.obtainClassInfoRecursive(reachableClassInfo);
      }

   }

   public ClassInfoList getClassInfoRecursive() {
      Set<ClassInfo> reachableClassInfo = new HashSet();
      this.obtainClassInfoRecursive(reachableClassInfo);
      return new ClassInfoList(reachableClassInfo, true);
   }

   static String getParentPackageName(String packageOrClassName) {
      if (packageOrClassName.isEmpty()) {
         return null;
      } else {
         int lastDotIdx = packageOrClassName.lastIndexOf(46);
         return lastDotIdx < 0 ? "" : packageOrClassName.substring(0, lastDotIdx);
      }
   }

   static PackageInfo getOrCreatePackage(String packageName, Map<String, PackageInfo> packageNameToPackageInfo) {
      PackageInfo packageInfo = (PackageInfo)packageNameToPackageInfo.get(packageName);
      if (packageInfo != null) {
         return packageInfo;
      } else {
         packageNameToPackageInfo.put(packageName, packageInfo = new PackageInfo(packageName));
         if (!packageName.isEmpty()) {
            PackageInfo parentPackageInfo = getOrCreatePackage(getParentPackageName(packageInfo.name), packageNameToPackageInfo);
            if (parentPackageInfo != null) {
               if (parentPackageInfo.children == null) {
                  parentPackageInfo.children = new HashSet();
               }

               parentPackageInfo.children.add(packageInfo);
               packageInfo.parent = parentPackageInfo;
            }
         }

         return packageInfo;
      }
   }

   public int compareTo(PackageInfo o) {
      return this.name.compareTo(o.name);
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof PackageInfo) ? false : this.name.equals(((PackageInfo)obj).name);
      }
   }

   public String toString() {
      return this.name;
   }
}
