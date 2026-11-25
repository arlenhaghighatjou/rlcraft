package io.github.classgraph;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

abstract class ClasspathElement {
   List<String> nestedClasspathRootPrefixes;
   boolean skipClasspathElement;
   boolean containsSpecificallyWhitelistedClasspathElementResourcePath;
   final Queue<Entry<Integer, ClasspathElement>> childClasspathElementsIndexed = new ConcurrentLinkedQueue();
   List<ClasspathElement> childClasspathElementsOrdered;
   protected final Collection<Resource> whitelistedResources = new ConcurrentLinkedQueue();
   protected Collection<Resource> whitelistedClassfileResources = new ConcurrentLinkedQueue();
   protected final Map<File, Long> fileToLastModified = new ConcurrentHashMap();
   protected final AtomicBoolean scanned = new AtomicBoolean(false);
   protected ClassLoader classLoader;
   String moduleNameFromModuleDescriptor;
   final ScanSpec scanSpec;

   ClasspathElement(ClassLoader classLoader, ScanSpec scanSpec) {
      this.classLoader = classLoader;
      this.scanSpec = scanSpec;
   }

   ClassLoader getClassLoader() {
      return this.classLoader;
   }

   int getNumClassfileMatches() {
      return this.whitelistedClassfileResources == null ? 0 : this.whitelistedClassfileResources.size();
   }

   protected void checkResourcePathWhiteBlackList(String relativePath, LogNode log) {
      if (!this.scanSpec.classpathElementResourcePathWhiteBlackList.whitelistAndBlacklistAreEmpty()) {
         if (this.scanSpec.classpathElementResourcePathWhiteBlackList.isBlacklisted(relativePath)) {
            if (log != null) {
               log.log("Reached blacklisted classpath element resource path, stopping scanning: " + relativePath);
            }

            this.skipClasspathElement = true;
            return;
         }

         if (this.scanSpec.classpathElementResourcePathWhiteBlackList.isSpecificallyWhitelisted(relativePath)) {
            if (log != null) {
               log.log("Reached specifically whitelisted classpath element resource path: " + relativePath);
            }

            this.containsSpecificallyWhitelistedClasspathElementResourcePath = true;
         }
      }

   }

   void maskClassfiles(int classpathIdx, Set<String> classpathRelativePathsFound, LogNode log) {
      if (!this.scanSpec.performScan) {
         throw new IllegalArgumentException("performScan is false");
      } else {
         List<Resource> whitelistedClassfileResourcesFiltered = new ArrayList(this.whitelistedClassfileResources.size());
         boolean foundMasked = false;

         for(Resource res : this.whitelistedClassfileResources) {
            String pathRelativeToPackageRoot = res.getPath();
            if (!pathRelativeToPackageRoot.equals("module-info.class") && !pathRelativeToPackageRoot.equals("package-info.class") && !pathRelativeToPackageRoot.endsWith("/package-info.class") && !classpathRelativePathsFound.add(pathRelativeToPackageRoot)) {
               foundMasked = true;
               if (log != null) {
                  log.log(String.format("%06d-1", classpathIdx), "Ignoring duplicate (masked) class " + JarUtils.classfilePathToClassName(pathRelativeToPackageRoot) + " found at " + res);
               }
            } else {
               whitelistedClassfileResourcesFiltered.add(res);
            }
         }

         if (foundMasked) {
            this.whitelistedClassfileResources = whitelistedClassfileResourcesFiltered;
         }

      }
   }

   protected void addWhitelistedResource(Resource resource, ScanSpec.ScanSpecPathMatch parentMatchStatus, boolean isClassfileOnly, LogNode log) {
      String path = resource.getPath();
      boolean isClassFile = FileUtils.isClassfile(path);
      boolean isWhitelisted = false;
      if (isClassFile) {
         if (this.scanSpec.enableClassInfo && !this.scanSpec.classfilePathWhiteBlackList.isBlacklisted(path)) {
            this.whitelistedClassfileResources.add(resource);
            isWhitelisted = true;
         }
      } else {
         isWhitelisted = true;
      }

      if (!isClassfileOnly) {
         this.whitelistedResources.add(resource);
      }

      if (log != null && isWhitelisted) {
         String type = isClassFile ? "classfile" : "resource";
         String logStr;
         switch(parentMatchStatus) {
         case HAS_WHITELISTED_PATH_PREFIX:
            logStr = "Found " + type + " within subpackage of whitelisted package: ";
            break;
         case AT_WHITELISTED_PATH:
            logStr = "Found " + type + " within whitelisted package: ";
            break;
         case AT_WHITELISTED_CLASS_PACKAGE:
            logStr = "Found specifically-whitelisted " + type + ": ";
            break;
         default:
            logStr = "Found whitelisted " + type + ": ";
         }

         resource.scanLog = log.log("0:" + path, logStr + path + (path.equals(resource.getPathRelativeToClasspathElement()) ? "" : " ; full path: " + resource.getPathRelativeToClasspathElement()));
      }

   }

   protected void finishScanPaths(LogNode log) {
      if (log != null) {
         if (this.whitelistedResources.isEmpty() && this.whitelistedClassfileResources.isEmpty()) {
            log.log(this.scanSpec.enableClassInfo ? "No whitelisted classfiles or resources found" : "Classfile scanning is disabled, and no whitelisted resources found");
         } else if (this.whitelistedResources.isEmpty()) {
            log.log("No whitelisted resources found");
         } else if (this.whitelistedClassfileResources.isEmpty()) {
            log.log(this.scanSpec.enableClassInfo ? "No whitelisted classfiles found" : "Classfile scanning is disabled");
         }
      }

      if (log != null) {
         log.addElapsedTime();
      }

   }

   protected LogNode log(int classpathElementIdx, String msg, LogNode log) {
      return log.log(String.format("%07d", classpathElementIdx), msg);
   }

   abstract void open(WorkQueue<Scanner.ClasspathEntryWorkUnit> var1, int var2, LogNode var3) throws InterruptedException;

   abstract void scanPaths(int var1, LogNode var2);

   abstract Resource getResource(String var1);

   abstract URI getURI();

   abstract File getFile();

   abstract String getModuleName();
}
