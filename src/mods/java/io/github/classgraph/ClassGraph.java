package io.github.classgraph;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import nonapi.io.github.classgraph.classpath.SystemJarFinder;
import nonapi.io.github.classgraph.concurrency.AutoCloseableExecutorService;
import nonapi.io.github.classgraph.concurrency.InterruptionChecker;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.scanspec.WhiteBlackList;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.VersionFinder;

public class ClassGraph {
   ScanSpec scanSpec = new ScanSpec();
   private static final int DEFAULT_NUM_WORKER_THREADS = Math.max(2, (int)Math.ceil(Math.min(4.0D, (double)Runtime.getRuntime().availableProcessors() * 0.75D) + (double)Runtime.getRuntime().availableProcessors() * 1.25D));
   private LogNode topLevelLog;

   public ClassGraph() {
      ScanResult.init();
   }

   public static String getVersion() {
      return VersionFinder.getVersion();
   }

   public ClassGraph verbose() {
      if (this.topLevelLog == null) {
         this.topLevelLog = new LogNode();
      }

      return this;
   }

   public ClassGraph verbose(boolean verbose) {
      if (verbose) {
         this.verbose();
      }

      return this;
   }

   public ClassGraph enableAllInfo() {
      this.enableClassInfo();
      this.enableFieldInfo();
      this.enableMethodInfo();
      this.enableAnnotationInfo();
      this.enableStaticFinalFieldConstantInitializerValues();
      this.ignoreClassVisibility();
      this.ignoreFieldVisibility();
      this.ignoreMethodVisibility();
      return this;
   }

   public ClassGraph enableClassInfo() {
      this.scanSpec.enableClassInfo = true;
      return this;
   }

   public ClassGraph ignoreClassVisibility() {
      this.enableClassInfo();
      this.scanSpec.ignoreClassVisibility = true;
      return this;
   }

   public ClassGraph enableMethodInfo() {
      this.enableClassInfo();
      this.scanSpec.enableMethodInfo = true;
      return this;
   }

   public ClassGraph ignoreMethodVisibility() {
      this.enableClassInfo();
      this.enableMethodInfo();
      this.scanSpec.ignoreMethodVisibility = true;
      return this;
   }

   public ClassGraph enableFieldInfo() {
      this.enableClassInfo();
      this.scanSpec.enableFieldInfo = true;
      return this;
   }

   public ClassGraph ignoreFieldVisibility() {
      this.enableClassInfo();
      this.enableFieldInfo();
      this.scanSpec.ignoreFieldVisibility = true;
      return this;
   }

   public ClassGraph enableStaticFinalFieldConstantInitializerValues() {
      this.enableClassInfo();
      this.enableFieldInfo();
      this.scanSpec.enableStaticFinalFieldConstantInitializerValues = true;
      return this;
   }

   public ClassGraph enableAnnotationInfo() {
      this.enableClassInfo();
      this.scanSpec.enableAnnotationInfo = true;
      return this;
   }

   public ClassGraph enableInterClassDependencies() {
      this.enableClassInfo();
      this.enableFieldInfo();
      this.enableMethodInfo();
      this.enableAnnotationInfo();
      this.ignoreClassVisibility();
      this.ignoreFieldVisibility();
      this.ignoreMethodVisibility();
      this.scanSpec.enableInterClassDependencies = true;
      return this;
   }

   public ClassGraph disableRuntimeInvisibleAnnotations() {
      this.enableClassInfo();
      this.scanSpec.disableRuntimeInvisibleAnnotations = true;
      return this;
   }

   public ClassGraph disableJarScanning() {
      this.scanSpec.scanJars = false;
      return this;
   }

   public ClassGraph disableNestedJarScanning() {
      this.scanSpec.scanNestedJars = false;
      return this;
   }

   public ClassGraph disableDirScanning() {
      this.scanSpec.scanDirs = false;
      return this;
   }

   public ClassGraph disableModuleScanning() {
      this.scanSpec.scanModules = false;
      return this;
   }

   public ClassGraph enableExternalClasses() {
      this.enableClassInfo();
      this.scanSpec.enableExternalClasses = true;
      return this;
   }

   public ClassGraph initializeLoadedClasses() {
      this.scanSpec.initializeLoadedClasses = true;
      return this;
   }

   public ClassGraph removeTemporaryFilesAfterScan() {
      this.scanSpec.removeTemporaryFilesAfterScan = true;
      return this;
   }

   public ClassGraph overrideClasspath(String overrideClasspath) {
      this.scanSpec.overrideClasspath(overrideClasspath);
      return this;
   }

   public ClassGraph overrideClasspath(Iterable<?> overrideClasspathElements) {
      String overrideClasspath = JarUtils.pathElementsToPathStr(overrideClasspathElements);
      if (overrideClasspath.isEmpty()) {
         throw new IllegalArgumentException("Can't override classpath with an empty path");
      } else {
         this.overrideClasspath(overrideClasspath);
         return this;
      }
   }

   public ClassGraph overrideClasspath(Object... overrideClasspathElements) {
      String overrideClasspath = JarUtils.pathElementsToPathStr(overrideClasspathElements);
      if (overrideClasspath.isEmpty()) {
         throw new IllegalArgumentException("Can't override classpath with an empty path");
      } else {
         this.overrideClasspath(overrideClasspath);
         return this;
      }
   }

   public ClassGraph filterClasspathElements(ClassGraph.ClasspathElementFilter classpathElementFilter) {
      this.scanSpec.filterClasspathElements(classpathElementFilter);
      return this;
   }

   public ClassGraph addClassLoader(ClassLoader classLoader) {
      this.scanSpec.addClassLoader(classLoader);
      return this;
   }

   public ClassGraph overrideClassLoaders(ClassLoader... overrideClassLoaders) {
      this.scanSpec.overrideClassLoaders(overrideClassLoaders);
      return this;
   }

   public ClassGraph ignoreParentClassLoaders() {
      this.scanSpec.ignoreParentClassLoaders = true;
      return this;
   }

   public ClassGraph addModuleLayer(Object moduleLayer) {
      this.scanSpec.addModuleLayer(moduleLayer);
      return this;
   }

   public ClassGraph overrideModuleLayers(Object... overrideModuleLayers) {
      this.scanSpec.overrideModuleLayers(overrideModuleLayers);
      return this;
   }

   public ClassGraph ignoreParentModuleLayers() {
      this.scanSpec.ignoreParentModuleLayers = true;
      return this;
   }

   public ClassGraph whitelistPackages(String... packageNames) {
      this.enableClassInfo();

      for(String packageName : packageNames) {
         String packageNameNormalized = WhiteBlackList.normalizePackageOrClassName(packageName);
         if (packageNameNormalized.startsWith("!") || packageNameNormalized.startsWith("-")) {
            throw new IllegalArgumentException("This style of whitelisting/blacklisting is no longer supported: " + packageNameNormalized);
         }

         this.scanSpec.packageWhiteBlackList.addToWhitelist(packageNameNormalized);
         String path = WhiteBlackList.packageNameToPath(packageNameNormalized);
         this.scanSpec.pathWhiteBlackList.addToWhitelist(path + "/");
         if (packageNameNormalized.isEmpty()) {
            this.scanSpec.pathWhiteBlackList.addToWhitelist("");
         }

         if (!packageNameNormalized.contains("*")) {
            if (packageNameNormalized.isEmpty()) {
               this.scanSpec.packagePrefixWhiteBlackList.addToWhitelist("");
               this.scanSpec.pathPrefixWhiteBlackList.addToWhitelist("");
            } else {
               this.scanSpec.packagePrefixWhiteBlackList.addToWhitelist(packageNameNormalized + ".");
               this.scanSpec.pathPrefixWhiteBlackList.addToWhitelist(path + "/");
            }
         }
      }

      return this;
   }

   public ClassGraph whitelistPaths(String... paths) {
      for(String path : paths) {
         String pathNormalized = WhiteBlackList.normalizePath(path);
         String packageName = WhiteBlackList.pathToPackageName(pathNormalized);
         this.scanSpec.packageWhiteBlackList.addToWhitelist(packageName);
         this.scanSpec.pathWhiteBlackList.addToWhitelist(pathNormalized + "/");
         if (pathNormalized.isEmpty()) {
            this.scanSpec.pathWhiteBlackList.addToWhitelist("");
         }

         if (!pathNormalized.contains("*")) {
            if (pathNormalized.isEmpty()) {
               this.scanSpec.packagePrefixWhiteBlackList.addToWhitelist("");
               this.scanSpec.pathPrefixWhiteBlackList.addToWhitelist("");
            } else {
               this.scanSpec.packagePrefixWhiteBlackList.addToWhitelist(packageName + ".");
               this.scanSpec.pathPrefixWhiteBlackList.addToWhitelist(pathNormalized + "/");
            }
         }
      }

      return this;
   }

   public ClassGraph whitelistPackagesNonRecursive(String... packageNames) {
      this.enableClassInfo();

      for(String packageName : packageNames) {
         String packageNameNormalized = WhiteBlackList.normalizePackageOrClassName(packageName);
         if (packageNameNormalized.contains("*")) {
            throw new IllegalArgumentException("Cannot use a glob wildcard here: " + packageNameNormalized);
         }

         this.scanSpec.packageWhiteBlackList.addToWhitelist(packageNameNormalized);
         this.scanSpec.pathWhiteBlackList.addToWhitelist(WhiteBlackList.packageNameToPath(packageNameNormalized) + "/");
         if (packageNameNormalized.isEmpty()) {
            this.scanSpec.pathWhiteBlackList.addToWhitelist("");
         }
      }

      return this;
   }

   public ClassGraph whitelistPathsNonRecursive(String... paths) {
      for(String path : paths) {
         if (path.contains("*")) {
            throw new IllegalArgumentException("Cannot use a glob wildcard here: " + path);
         }

         String pathNormalized = WhiteBlackList.normalizePath(path);
         this.scanSpec.packageWhiteBlackList.addToWhitelist(WhiteBlackList.pathToPackageName(pathNormalized));
         this.scanSpec.pathWhiteBlackList.addToWhitelist(pathNormalized + "/");
         if (pathNormalized.isEmpty()) {
            this.scanSpec.pathWhiteBlackList.addToWhitelist("");
         }
      }

      return this;
   }

   public ClassGraph blacklistPackages(String... packageNames) {
      this.enableClassInfo();

      for(String packageName : packageNames) {
         String packageNameNormalized = WhiteBlackList.normalizePackageOrClassName(packageName);
         if (packageNameNormalized.isEmpty()) {
            throw new IllegalArgumentException("Blacklisting the root package (\"\") will cause nothing to be scanned");
         }

         this.scanSpec.packageWhiteBlackList.addToBlacklist(packageNameNormalized);
         String path = WhiteBlackList.packageNameToPath(packageNameNormalized);
         this.scanSpec.pathWhiteBlackList.addToBlacklist(path + "/");
         if (!packageNameNormalized.contains("*")) {
            this.scanSpec.packagePrefixWhiteBlackList.addToBlacklist(packageNameNormalized + ".");
            this.scanSpec.pathPrefixWhiteBlackList.addToBlacklist(path + "/");
         }
      }

      return this;
   }

   public ClassGraph blacklistPaths(String... paths) {
      for(String path : paths) {
         String pathNormalized = WhiteBlackList.normalizePath(path);
         if (pathNormalized.isEmpty()) {
            throw new IllegalArgumentException("Blacklisting the root package (\"\") will cause nothing to be scanned");
         }

         String packageName = WhiteBlackList.pathToPackageName(pathNormalized);
         this.scanSpec.packageWhiteBlackList.addToBlacklist(packageName);
         this.scanSpec.pathWhiteBlackList.addToBlacklist(pathNormalized + "/");
         if (!pathNormalized.contains("*")) {
            this.scanSpec.packagePrefixWhiteBlackList.addToBlacklist(packageName + ".");
            this.scanSpec.pathPrefixWhiteBlackList.addToBlacklist(pathNormalized + "/");
         }
      }

      return this;
   }

   public ClassGraph whitelistClasses(String... classNames) {
      this.enableClassInfo();

      for(String className : classNames) {
         if (className.contains("*")) {
            throw new IllegalArgumentException("Cannot use a glob wildcard here: " + className);
         }

         String classNameNormalized = WhiteBlackList.normalizePackageOrClassName(className);
         this.scanSpec.classWhiteBlackList.addToWhitelist(classNameNormalized);
         this.scanSpec.classfilePathWhiteBlackList.addToWhitelist(WhiteBlackList.classNameToClassfilePath(classNameNormalized));
         String packageName = PackageInfo.getParentPackageName(classNameNormalized);
         this.scanSpec.classPackageWhiteBlackList.addToWhitelist(packageName);
         this.scanSpec.classPackagePathWhiteBlackList.addToWhitelist(WhiteBlackList.packageNameToPath(packageName) + "/");
      }

      return this;
   }

   public ClassGraph blacklistClasses(String... classNames) {
      this.enableClassInfo();

      for(String className : classNames) {
         if (className.contains("*")) {
            throw new IllegalArgumentException("Cannot use a glob wildcard here: " + className);
         }

         String classNameNormalized = WhiteBlackList.normalizePackageOrClassName(className);
         this.scanSpec.classWhiteBlackList.addToBlacklist(classNameNormalized);
         this.scanSpec.classfilePathWhiteBlackList.addToBlacklist(WhiteBlackList.classNameToClassfilePath(classNameNormalized));
      }

      return this;
   }

   public ClassGraph whitelistJars(String... jarLeafNames) {
      for(String jarLeafName : jarLeafNames) {
         String leafName = JarUtils.leafName(jarLeafName);
         if (!leafName.equals(jarLeafName)) {
            throw new IllegalArgumentException("Can only whitelist jars by leafname: " + jarLeafName);
         }

         this.scanSpec.jarWhiteBlackList.addToWhitelist(leafName);
      }

      return this;
   }

   public ClassGraph blacklistJars(String... jarLeafNames) {
      for(String jarLeafName : jarLeafNames) {
         String leafName = JarUtils.leafName(jarLeafName);
         if (!leafName.equals(jarLeafName)) {
            throw new IllegalArgumentException("Can only blacklist jars by leafname: " + jarLeafName);
         }

         this.scanSpec.jarWhiteBlackList.addToBlacklist(leafName);
      }

      return this;
   }

   private void whitelistOrBlacklistLibOrExtJars(boolean whitelist, String... jarLeafNames) {
      if (jarLeafNames.length == 0) {
         for(String libOrExtJar : SystemJarFinder.getJreLibOrExtJars()) {
            this.whitelistOrBlacklistLibOrExtJars(whitelist, JarUtils.leafName(libOrExtJar));
         }
      } else {
         for(String jarLeafName : jarLeafNames) {
            String leafName = JarUtils.leafName(jarLeafName);
            if (!leafName.equals(jarLeafName)) {
               throw new IllegalArgumentException("Can only " + (whitelist ? "whitelist" : "blacklist") + " jars by leafname: " + jarLeafName);
            }

            if (jarLeafName.contains("*")) {
               Pattern pattern = WhiteBlackList.globToPattern(jarLeafName);
               boolean found = false;

               for(String libOrExtJarPath : SystemJarFinder.getJreLibOrExtJars()) {
                  String libOrExtJarLeafName = JarUtils.leafName(libOrExtJarPath);
                  if (pattern.matcher(libOrExtJarLeafName).matches()) {
                     if (!libOrExtJarLeafName.contains("*")) {
                        this.whitelistOrBlacklistLibOrExtJars(whitelist, libOrExtJarLeafName);
                     }

                     found = true;
                  }
               }

               if (!found && this.topLevelLog != null) {
                  this.topLevelLog.log("Could not find lib or ext jar matching wildcard: " + jarLeafName);
               }
            } else {
               boolean found = false;

               for(String libOrExtJarPath : SystemJarFinder.getJreLibOrExtJars()) {
                  String libOrExtJarLeafName = JarUtils.leafName(libOrExtJarPath);
                  if (jarLeafName.equals(libOrExtJarLeafName)) {
                     if (whitelist) {
                        this.scanSpec.libOrExtJarWhiteBlackList.addToWhitelist(jarLeafName);
                     } else {
                        this.scanSpec.libOrExtJarWhiteBlackList.addToBlacklist(jarLeafName);
                     }

                     if (this.topLevelLog != null) {
                        this.topLevelLog.log((whitelist ? "Whitelisting" : "Blacklisting") + " lib or ext jar: " + libOrExtJarPath);
                     }

                     found = true;
                     break;
                  }
               }

               if (!found && this.topLevelLog != null) {
                  this.topLevelLog.log("Could not find lib or ext jar: " + jarLeafName);
               }
            }
         }
      }

   }

   public ClassGraph whitelistLibOrExtJars(String... jarLeafNames) {
      this.whitelistOrBlacklistLibOrExtJars(true, jarLeafNames);
      return this;
   }

   public ClassGraph blacklistLibOrExtJars(String... jarLeafNames) {
      this.whitelistOrBlacklistLibOrExtJars(false, jarLeafNames);
      return this;
   }

   public ClassGraph whitelistModules(String... moduleNames) {
      for(String moduleName : moduleNames) {
         this.scanSpec.moduleWhiteBlackList.addToWhitelist(WhiteBlackList.normalizePackageOrClassName(moduleName));
      }

      return this;
   }

   public ClassGraph blacklistModules(String... moduleNames) {
      for(String moduleName : moduleNames) {
         this.scanSpec.moduleWhiteBlackList.addToBlacklist(WhiteBlackList.normalizePackageOrClassName(moduleName));
      }

      return this;
   }

   public ClassGraph whitelistClasspathElementsContainingResourcePath(String... resourcePaths) {
      for(String resourcePath : resourcePaths) {
         String resourcePathNormalized = WhiteBlackList.normalizePath(resourcePath);
         this.scanSpec.classpathElementResourcePathWhiteBlackList.addToWhitelist(resourcePathNormalized);
      }

      return this;
   }

   public ClassGraph blacklistClasspathElementsContainingResourcePath(String... resourcePaths) {
      for(String resourcePath : resourcePaths) {
         String resourcePathNormalized = WhiteBlackList.normalizePath(resourcePath);
         this.scanSpec.classpathElementResourcePathWhiteBlackList.addToBlacklist(resourcePathNormalized);
      }

      return this;
   }

   public ClassGraph enableRemoteJarScanning() {
      this.scanSpec.enableRemoteJarScanning = true;
      return this;
   }

   public ClassGraph enableSystemJarsAndModules() {
      this.enableClassInfo();
      this.scanSpec.enableSystemJarsAndModules = true;
      return this;
   }

   public ClassGraph enableRealtimeLogging() {
      this.verbose();
      LogNode.logInRealtime(true);
      return this;
   }

   public void scanAsync(final ExecutorService executorService, final int numParallelTasks, final ClassGraph.ScanResultProcessor scanResultProcessor, final ClassGraph.FailureHandler failureHandler) {
      if (scanResultProcessor == null) {
         throw new IllegalArgumentException("scanResultProcessor cannot be null");
      } else if (failureHandler == null) {
         throw new IllegalArgumentException("failureHandler cannot be null");
      } else {
         executorService.execute(new Runnable() {
            public void run() {
               try {
                  new Scanner(ClassGraph.this.scanSpec, executorService, numParallelTasks, scanResultProcessor, failureHandler, ClassGraph.this.topLevelLog);
               } catch (InterruptedException var2) {
                  failureHandler.onFailure(var2);
               }

            }
         });
      }
   }

   public Future<ScanResult> scanAsync(ExecutorService executorService, int numParallelTasks) {
      try {
         return executorService.submit(new Scanner(this.scanSpec, executorService, numParallelTasks, (ClassGraph.ScanResultProcessor)null, (ClassGraph.FailureHandler)null, this.topLevelLog));
      } catch (final InterruptedException var4) {
         return executorService.submit(new Callable<ScanResult>() {
            public ScanResult call() throws Exception {
               throw var4;
            }
         });
      }
   }

   public ScanResult scan(ExecutorService executorService, int numParallelTasks) {
      try {
         ScanResult scanResult = (ScanResult)this.scanAsync(executorService, numParallelTasks).get();
         if (scanResult == null) {
            throw new NullPointerException();
         } else {
            return scanResult;
         }
      } catch (CancellationException | InterruptedException var4) {
         throw ClassGraphException.newClassGraphException("Scan interrupted", var4);
      } catch (ExecutionException var5) {
         throw ClassGraphException.newClassGraphException("Uncaught exception during scan", InterruptionChecker.getCause(var5));
      }
   }

   public ScanResult scan(int numThreads) {
      AutoCloseableExecutorService executorService = new AutoCloseableExecutorService(numThreads);

      ScanResult var3;
      try {
         var3 = this.scan(executorService, numThreads);
      } catch (Throwable var6) {
         try {
            executorService.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      executorService.close();
      return var3;
   }

   public ScanResult scan() {
      return this.scan(DEFAULT_NUM_WORKER_THREADS);
   }

   public List<File> getClasspathFiles() {
      this.scanSpec.performScan = false;
      ScanResult scanResult = this.scan();

      List var2;
      try {
         var2 = scanResult.getClasspathFiles();
      } catch (Throwable var5) {
         if (scanResult != null) {
            try {
               scanResult.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (scanResult != null) {
         scanResult.close();
      }

      return var2;
   }

   public String getClasspath() {
      return JarUtils.pathElementsToPathStr(this.getClasspathFiles());
   }

   public List<URI> getClasspathURIs() {
      this.scanSpec.performScan = false;
      ScanResult scanResult = this.scan();

      List var2;
      try {
         var2 = scanResult.getClasspathURIs();
      } catch (Throwable var5) {
         if (scanResult != null) {
            try {
               scanResult.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (scanResult != null) {
         scanResult.close();
      }

      return var2;
   }

   public List<URL> getClasspathURLs() {
      this.scanSpec.performScan = false;
      ScanResult scanResult = this.scan();

      List var2;
      try {
         var2 = scanResult.getClasspathURLs();
      } catch (Throwable var5) {
         if (scanResult != null) {
            try {
               scanResult.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (scanResult != null) {
         scanResult.close();
      }

      return var2;
   }

   public List<ModuleRef> getModules() {
      this.scanSpec.performScan = false;
      ScanResult scanResult = this.scan();

      List var2;
      try {
         var2 = scanResult.getModules();
      } catch (Throwable var5) {
         if (scanResult != null) {
            try {
               scanResult.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (scanResult != null) {
         scanResult.close();
      }

      return var2;
   }

   public ModulePathInfo getModulePathInfo() {
      return this.scanSpec.modulePathInfo;
   }

   @FunctionalInterface
   public interface ClasspathElementFilter {
      boolean includeClasspathElement(String var1);
   }

   @FunctionalInterface
   public interface FailureHandler {
      void onFailure(Throwable var1);
   }

   @FunctionalInterface
   public interface ScanResultProcessor {
      void processScanResult(ScanResult var1);
   }
}
