package nonapi.io.github.classgraph.scanspec;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassGraphException;
import io.github.classgraph.ModulePathInfo;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nonapi.io.github.classgraph.utils.LogNode;

public class ScanSpec {
   public WhiteBlackList.WhiteBlackListWholeString packageWhiteBlackList = new WhiteBlackList.WhiteBlackListWholeString('.');
   public WhiteBlackList.WhiteBlackListPrefix packagePrefixWhiteBlackList = new WhiteBlackList.WhiteBlackListPrefix('.');
   public WhiteBlackList.WhiteBlackListWholeString pathWhiteBlackList = new WhiteBlackList.WhiteBlackListWholeString('/');
   public WhiteBlackList.WhiteBlackListPrefix pathPrefixWhiteBlackList = new WhiteBlackList.WhiteBlackListPrefix('/');
   public WhiteBlackList.WhiteBlackListWholeString classWhiteBlackList = new WhiteBlackList.WhiteBlackListWholeString('.');
   public WhiteBlackList.WhiteBlackListWholeString classfilePathWhiteBlackList = new WhiteBlackList.WhiteBlackListWholeString('/');
   public WhiteBlackList.WhiteBlackListWholeString classPackageWhiteBlackList = new WhiteBlackList.WhiteBlackListWholeString('.');
   public WhiteBlackList.WhiteBlackListWholeString classPackagePathWhiteBlackList = new WhiteBlackList.WhiteBlackListWholeString('/');
   public WhiteBlackList.WhiteBlackListWholeString moduleWhiteBlackList = new WhiteBlackList.WhiteBlackListWholeString('.');
   public WhiteBlackList.WhiteBlackListLeafname jarWhiteBlackList = new WhiteBlackList.WhiteBlackListLeafname('/');
   public WhiteBlackList.WhiteBlackListWholeString classpathElementResourcePathWhiteBlackList = new WhiteBlackList.WhiteBlackListWholeString('/');
   public WhiteBlackList.WhiteBlackListLeafname libOrExtJarWhiteBlackList = new WhiteBlackList.WhiteBlackListLeafname('/');
   public boolean performScan = true;
   public boolean scanJars = true;
   public boolean scanNestedJars = true;
   public boolean scanDirs = true;
   public boolean scanModules = true;
   public boolean enableClassInfo;
   public boolean enableFieldInfo;
   public boolean enableMethodInfo;
   public boolean enableAnnotationInfo;
   public boolean enableStaticFinalFieldConstantInitializerValues;
   public boolean enableInterClassDependencies;
   public boolean enableExternalClasses;
   public boolean enableSystemJarsAndModules;
   public boolean ignoreClassVisibility;
   public boolean ignoreFieldVisibility;
   public boolean ignoreMethodVisibility;
   public boolean disableRuntimeInvisibleAnnotations;
   public boolean extendScanningUpwardsToExternalClasses = true;
   public boolean enableRemoteJarScanning;
   public transient List<ClassLoader> addedClassLoaders;
   public transient List<ClassLoader> overrideClassLoaders;
   public transient List<Object> addedModuleLayers;
   public transient List<Object> overrideModuleLayers;
   public String overrideClasspath;
   public transient List<ClassGraph.ClasspathElementFilter> classpathElementFilters;
   public boolean initializeLoadedClasses;
   public boolean removeTemporaryFilesAfterScan;
   public boolean ignoreParentClassLoaders;
   public boolean ignoreParentModuleLayers;
   public ModulePathInfo modulePathInfo = new ModulePathInfo();

   public ScanSpec() {
   }

   public void sortPrefixes() {
      for(Field field : ScanSpec.class.getDeclaredFields()) {
         if (WhiteBlackList.class.isAssignableFrom(field.getType())) {
            try {
               ((WhiteBlackList)field.get(this)).sortPrefixes();
            } catch (ReflectiveOperationException var6) {
               throw ClassGraphException.newClassGraphException("Field is not accessible: " + field, var6);
            }
         }
      }

   }

   public void overrideClasspath(String overrideClasspath) {
      this.overrideClasspath = overrideClasspath;
   }

   public void filterClasspathElements(ClassGraph.ClasspathElementFilter classpathElementFilter) {
      if (this.classpathElementFilters == null) {
         this.classpathElementFilters = new ArrayList(2);
      }

      this.classpathElementFilters.add(classpathElementFilter);
   }

   public void addClassLoader(ClassLoader classLoader) {
      if (this.addedClassLoaders == null) {
         this.addedClassLoaders = new ArrayList();
      }

      if (classLoader != null) {
         this.addedClassLoaders.add(classLoader);
      }

   }

   public void overrideClassLoaders(ClassLoader... overrideClassLoaders) {
      if (overrideClassLoaders.length == 0) {
         throw new IllegalArgumentException("At least one override ClassLoader must be provided");
      } else {
         this.addedClassLoaders = null;
         this.overrideClassLoaders = new ArrayList();

         for(ClassLoader classLoader : overrideClassLoaders) {
            if (classLoader != null) {
               this.overrideClassLoaders.add(classLoader);
            }
         }

      }
   }

   private static boolean isModuleLayer(Object moduleLayer) {
      if (moduleLayer == null) {
         throw new IllegalArgumentException("ModuleLayer references must not be null");
      } else {
         for(Class<?> currClass = moduleLayer.getClass(); currClass != null; currClass = currClass.getSuperclass()) {
            if (currClass.getName().equals("java.lang.ModuleLayer")) {
               return true;
            }
         }

         return false;
      }
   }

   public void addModuleLayer(Object moduleLayer) {
      if (!isModuleLayer(moduleLayer)) {
         throw new IllegalArgumentException("moduleLayer must be of type java.lang.ModuleLayer");
      } else {
         if (this.addedModuleLayers == null) {
            this.addedModuleLayers = new ArrayList();
         }

         this.addedModuleLayers.add(moduleLayer);
      }
   }

   public void overrideModuleLayers(Object... overrideModuleLayers) {
      if (overrideModuleLayers == null) {
         throw new IllegalArgumentException("overrideModuleLayers cannot be null");
      } else if (overrideModuleLayers.length == 0) {
         throw new IllegalArgumentException("At least one override ModuleLayer must be provided");
      } else {
         for(Object moduleLayer : overrideModuleLayers) {
            if (!isModuleLayer(moduleLayer)) {
               throw new IllegalArgumentException("moduleLayer must be of type java.lang.ModuleLayer");
            }
         }

         this.addedModuleLayers = null;
         this.overrideModuleLayers = new ArrayList();
         Collections.addAll(this.overrideModuleLayers, overrideModuleLayers);
      }
   }

   public ScanSpec.ScanSpecPathMatch dirWhitelistMatchStatus(String relativePath) {
      if (this.pathWhiteBlackList.isBlacklisted(relativePath)) {
         return ScanSpec.ScanSpecPathMatch.HAS_BLACKLISTED_PATH_PREFIX;
      } else if (this.pathPrefixWhiteBlackList.isBlacklisted(relativePath)) {
         return ScanSpec.ScanSpecPathMatch.HAS_BLACKLISTED_PATH_PREFIX;
      } else if (this.pathWhiteBlackList.whitelistIsEmpty() && this.classPackagePathWhiteBlackList.whitelistIsEmpty()) {
         return !relativePath.isEmpty() && !relativePath.equals("/") ? ScanSpec.ScanSpecPathMatch.HAS_WHITELISTED_PATH_PREFIX : ScanSpec.ScanSpecPathMatch.AT_WHITELISTED_PATH;
      } else if (this.pathWhiteBlackList.isSpecificallyWhitelistedAndNotBlacklisted(relativePath)) {
         return ScanSpec.ScanSpecPathMatch.AT_WHITELISTED_PATH;
      } else if (this.classPackagePathWhiteBlackList.isSpecificallyWhitelistedAndNotBlacklisted(relativePath)) {
         return ScanSpec.ScanSpecPathMatch.AT_WHITELISTED_CLASS_PACKAGE;
      } else if (this.pathPrefixWhiteBlackList.isSpecificallyWhitelisted(relativePath)) {
         return ScanSpec.ScanSpecPathMatch.HAS_WHITELISTED_PATH_PREFIX;
      } else if (relativePath.equals("/")) {
         return ScanSpec.ScanSpecPathMatch.ANCESTOR_OF_WHITELISTED_PATH;
      } else if (this.pathWhiteBlackList.whitelistHasPrefix(relativePath)) {
         return ScanSpec.ScanSpecPathMatch.ANCESTOR_OF_WHITELISTED_PATH;
      } else {
         return this.classfilePathWhiteBlackList.whitelistHasPrefix(relativePath) ? ScanSpec.ScanSpecPathMatch.ANCESTOR_OF_WHITELISTED_PATH : ScanSpec.ScanSpecPathMatch.NOT_WITHIN_WHITELISTED_PATH;
      }
   }

   public boolean classfileIsSpecificallyWhitelisted(String relativePath) {
      return this.classfilePathWhiteBlackList.isSpecificallyWhitelistedAndNotBlacklisted(relativePath);
   }

   public boolean classOrPackageIsBlacklisted(String className) {
      return this.classWhiteBlackList.isBlacklisted(className) || this.packagePrefixWhiteBlackList.isBlacklisted(className);
   }

   public void log(LogNode log) {
      if (log != null) {
         LogNode scanSpecLog = log.log("ScanSpec:");

         for(Field field : ScanSpec.class.getDeclaredFields()) {
            try {
               scanSpecLog.log(field.getName() + ": " + field.get(this));
            } catch (ReflectiveOperationException var8) {
               ;
            }
         }
      }

   }

   public static enum ScanSpecPathMatch {
      HAS_BLACKLISTED_PATH_PREFIX,
      HAS_WHITELISTED_PATH_PREFIX,
      AT_WHITELISTED_PATH,
      ANCESTOR_OF_WHITELISTED_PATH,
      AT_WHITELISTED_CLASS_PACKAGE,
      NOT_WITHIN_WHITELISTED_PATH;

      private ScanSpecPathMatch() {
      }
   }
}
