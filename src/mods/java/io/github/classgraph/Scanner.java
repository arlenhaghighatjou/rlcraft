package io.github.classgraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import nonapi.io.github.classgraph.classpath.ClasspathFinder;
import nonapi.io.github.classgraph.classpath.ModuleFinder;
import nonapi.io.github.classgraph.concurrency.AutoCloseableExecutorService;
import nonapi.io.github.classgraph.concurrency.InterruptionChecker;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

class Scanner implements Callable<ScanResult> {
   private final ScanSpec scanSpec;
   private final NestedJarHandler nestedJarHandler;
   private final ExecutorService executorService;
   private final InterruptionChecker interruptionChecker;
   private final int numParallelTasks;
   private final ClassGraph.ScanResultProcessor scanResultProcessor;
   private final ClassGraph.FailureHandler failureHandler;
   private final LogNode topLevelLog;
   private final ClasspathFinder classpathFinder;
   private final List<ClasspathElementModule> moduleOrder;
   private final ClassLoader[] classLoaderOrderRespectingParentDelegation;
   private static final Comparator<Entry<Integer, ClasspathElement>> INDEXED_CLASSPATH_ELEMENT_COMPARATOR = new Comparator<Entry<Integer, ClasspathElement>>() {
      public int compare(Entry<Integer, ClasspathElement> o1, Entry<Integer, ClasspathElement> o2) {
         return ((Integer)o1.getKey()).intValue() - ((Integer)o2.getKey()).intValue();
      }
   };
   private final SingletonMap<Entry<String, ClassLoader>, ClasspathElement, IOException> classpathEntryToClasspathElementSingletonMap = new SingletonMap<Entry<String, ClassLoader>, ClasspathElement, IOException>() {
      public ClasspathElement newInstance(Entry<String, ClassLoader> classpathEntry, LogNode log) throws IOException, InterruptedException {
         String classpathEntryPath = (String)classpathEntry.getKey();
         ClassLoader classLoader = (ClassLoader)classpathEntry.getValue();
         if (!classpathEntryPath.regionMatches(true, 0, "http://", 0, 7) && !classpathEntryPath.regionMatches(true, 0, "https://", 0, 8)) {
            String pathNormalized = FastPathResolver.resolve(FileUtils.CURR_DIR_PATH, classpathEntryPath);
            int plingIdx = pathNormalized.indexOf(33);
            String pathToCanonicalize = plingIdx < 0 ? pathNormalized : pathNormalized.substring(0, plingIdx);
            File fileCanonicalized = (new File(pathToCanonicalize)).getCanonicalFile();
            if (!fileCanonicalized.exists()) {
               throw new FileNotFoundException();
            } else if (!FileUtils.canRead(fileCanonicalized)) {
               throw new IOException("Cannot read file or directory");
            } else {
               boolean isJar = classpathEntryPath.regionMatches(true, 0, "jar:", 0, 4) || plingIdx > 0;
               if (fileCanonicalized.isFile()) {
                  isJar = true;
               } else {
                  if (!fileCanonicalized.isDirectory()) {
                     throw new IOException("Not a normal file or directory");
                  }

                  if (isJar) {
                     throw new IOException("Expected jar, found directory");
                  }
               }

               String baseFileCanonicalPathNormalized = FastPathResolver.resolve(FileUtils.CURR_DIR_PATH, fileCanonicalized.getPath());
               String canonicalPathNormalized = plingIdx < 0 ? baseFileCanonicalPathNormalized : baseFileCanonicalPathNormalized + pathNormalized.substring(plingIdx);
               if (!canonicalPathNormalized.equals(pathNormalized)) {
                  try {
                     return (ClasspathElement)this.get(new SimpleEntry(canonicalPathNormalized, classLoader), log);
                  } catch (SingletonMap.NullSingletonException var13) {
                     throw new IOException("Cannot get classpath element for canonical path " + canonicalPathNormalized + " : " + var13);
                  }
               } else {
                  return (ClasspathElement)(isJar ? new ClasspathElementZip(canonicalPathNormalized, classLoader, Scanner.this.nestedJarHandler, Scanner.this.scanSpec) : new ClasspathElementDir(fileCanonicalized, classLoader, Scanner.this.scanSpec));
               }
            }
         } else {
            return new ClasspathElementZip(classpathEntryPath, classLoader, Scanner.this.nestedJarHandler, Scanner.this.scanSpec);
         }
      }
   };

   Scanner(ScanSpec scanSpec, ExecutorService executorService, int numParallelTasks, ClassGraph.ScanResultProcessor scanResultProcessor, ClassGraph.FailureHandler failureHandler, LogNode topLevelLog) throws InterruptedException {
      this.scanSpec = scanSpec;
      scanSpec.sortPrefixes();
      scanSpec.log(topLevelLog);
      if (topLevelLog != null) {
         if (scanSpec.pathWhiteBlackList != null && scanSpec.packagePrefixWhiteBlackList.isSpecificallyWhitelisted("")) {
            topLevelLog.log("Note: There is no need to whitelist the root package (\"\") -- not whitelisting anything will have the same effect of causing all packages to be scanned");
         }

         topLevelLog.log("Number of worker threads: " + numParallelTasks);
      }

      this.executorService = executorService;
      this.interruptionChecker = executorService instanceof AutoCloseableExecutorService ? ((AutoCloseableExecutorService)executorService).interruptionChecker : new InterruptionChecker();
      this.nestedJarHandler = new NestedJarHandler(scanSpec, this.interruptionChecker);
      this.numParallelTasks = numParallelTasks;
      this.scanResultProcessor = scanResultProcessor;
      this.failureHandler = failureHandler;
      this.topLevelLog = topLevelLog;
      LogNode classpathFinderLog = topLevelLog == null ? null : topLevelLog.log("Finding classpath");
      this.classpathFinder = new ClasspathFinder(scanSpec, classpathFinderLog);
      this.classLoaderOrderRespectingParentDelegation = this.classpathFinder.getClassLoaderOrderRespectingParentDelegation();
      ModuleFinder moduleFinder = new ModuleFinder(this.classpathFinder.getCallStack(), scanSpec, classpathFinderLog);
      this.moduleOrder = new ArrayList();
      if (scanSpec.overrideClasspath == null && scanSpec.overrideClassLoaders == null && scanSpec.scanModules) {
         List<ModuleRef> systemModuleRefs = moduleFinder.getSystemModuleRefs();
         ClassLoader defaultClassLoader = this.classLoaderOrderRespectingParentDelegation != null && this.classLoaderOrderRespectingParentDelegation.length != 0 ? this.classLoaderOrderRespectingParentDelegation[0] : null;
         if (systemModuleRefs != null) {
            for(ModuleRef systemModuleRef : systemModuleRefs) {
               String moduleName = systemModuleRef.getName();
               if (scanSpec.enableSystemJarsAndModules && scanSpec.moduleWhiteBlackList.whitelistAndBlacklistAreEmpty() || scanSpec.moduleWhiteBlackList.isSpecificallyWhitelistedAndNotBlacklisted(moduleName)) {
                  ClasspathElementModule classpathElementModule = new ClasspathElementModule(systemModuleRef, defaultClassLoader, this.nestedJarHandler, scanSpec);
                  this.moduleOrder.add(classpathElementModule);
                  classpathElementModule.open((WorkQueue)null, this.moduleOrder.size() - 1, classpathFinderLog);
               } else if (classpathFinderLog != null) {
                  classpathFinderLog.log("Skipping non-whitelisted or blacklisted system module: " + moduleName);
               }
            }
         }

         List<ModuleRef> nonSystemModuleRefs = moduleFinder.getNonSystemModuleRefs();
         if (nonSystemModuleRefs != null) {
            for(ModuleRef nonSystemModuleRef : nonSystemModuleRefs) {
               String moduleName = nonSystemModuleRef.getName();
               if (moduleName == null) {
                  moduleName = "";
               }

               if (scanSpec.moduleWhiteBlackList.isWhitelistedAndNotBlacklisted(moduleName)) {
                  ClasspathElementModule classpathElementModule = new ClasspathElementModule(nonSystemModuleRef, defaultClassLoader, this.nestedJarHandler, scanSpec);
                  this.moduleOrder.add(classpathElementModule);
                  classpathElementModule.open((WorkQueue)null, this.moduleOrder.size() - 1, classpathFinderLog);
               } else if (classpathFinderLog != null) {
                  classpathFinderLog.log("Skipping non-whitelisted or blacklisted module: " + moduleName);
               }
            }
         }
      }

   }

   private static void findClasspathOrderRec(ClasspathElement currClasspathElement, Set<ClasspathElement> visitedClasspathElts, List<ClasspathElement> order) {
      if (visitedClasspathElts.add(currClasspathElement)) {
         if (!currClasspathElement.skipClasspathElement) {
            order.add(currClasspathElement);
         }

         for(ClasspathElement childClasspathElt : currClasspathElement.childClasspathElementsOrdered) {
            findClasspathOrderRec(childClasspathElt, visitedClasspathElts, order);
         }
      }

   }

   private static List<ClasspathElement> orderClasspathElements(Collection<Entry<Integer, ClasspathElement>> classpathEltsIndexed) {
      List<Entry<Integer, ClasspathElement>> classpathEltsIndexedOrdered = new ArrayList(classpathEltsIndexed);
      CollectionUtils.sortIfNotEmpty(classpathEltsIndexedOrdered, INDEXED_CLASSPATH_ELEMENT_COMPARATOR);
      List<ClasspathElement> classpathEltsOrdered = new ArrayList(classpathEltsIndexedOrdered.size());

      for(Entry<Integer, ClasspathElement> ent : classpathEltsIndexedOrdered) {
         classpathEltsOrdered.add(ent.getValue());
      }

      return classpathEltsOrdered;
   }

   private List<ClasspathElement> findClasspathOrder(Set<ClasspathElement> uniqueClasspathElements, Queue<Entry<Integer, ClasspathElement>> toplevelClasspathEltsIndexed) {
      List<ClasspathElement> toplevelClasspathEltsOrdered = orderClasspathElements(toplevelClasspathEltsIndexed);

      for(ClasspathElement classpathElt : uniqueClasspathElements) {
         classpathElt.childClasspathElementsOrdered = orderClasspathElements(classpathElt.childClasspathElementsIndexed);
      }

      Set<ClasspathElement> visitedClasspathElts = new HashSet();
      List<ClasspathElement> order = new ArrayList();

      for(ClasspathElement toplevelClasspathElt : toplevelClasspathEltsOrdered) {
         findClasspathOrderRec(toplevelClasspathElt, visitedClasspathElts, order);
      }

      return order;
   }

   private <W> void processWorkUnits(Collection<W> workUnits, LogNode log, WorkQueue.WorkUnitProcessor<W> workUnitProcessor) throws InterruptedException, ExecutionException {
      WorkQueue.runWorkQueue(workUnits, this.executorService, this.interruptionChecker, this.numParallelTasks, log, workUnitProcessor);
      if (log != null) {
         log.addElapsedTime();
      }

      this.interruptionChecker.check();
   }

   private WorkQueue.WorkUnitProcessor<Scanner.ClasspathEntryWorkUnit> newClasspathEntryWorkUnitProcessor(final Set<ClasspathElement> openedClasspathElementsSet, final Queue<Entry<Integer, ClasspathElement>> toplevelClasspathEltOrder) {
      return new WorkQueue.WorkUnitProcessor<Scanner.ClasspathEntryWorkUnit>() {
         public void processWorkUnit(Scanner.ClasspathEntryWorkUnit workUnit, int workUnitIdx, WorkQueue<Scanner.ClasspathEntryWorkUnit> workQueue, LogNode log) throws InterruptedException {
            try {
               ClasspathElement classpathElt;
               try {
                  classpathElt = Scanner.this.classpathEntryToClasspathElementSingletonMap.get(workUnit.rawClasspathEntry, log);
               } catch (SingletonMap.NullSingletonException var7) {
                  throw new IOException("Cannot get classpath element for classpath entry " + workUnit.rawClasspathEntry + " : " + var7);
               }

               if (openedClasspathElementsSet.add(classpathElt)) {
                  classpathElt.open(workQueue, workUnitIdx, log);
                  SimpleEntry<Integer, ClasspathElement> classpathEltEntry = new SimpleEntry(workUnit.orderWithinParentClasspathElement, classpathElt);
                  if (workUnit.parentClasspathElement != null) {
                     workUnit.parentClasspathElement.childClasspathElementsIndexed.add(classpathEltEntry);
                  } else {
                     toplevelClasspathEltOrder.add(classpathEltEntry);
                  }
               }
            } catch (SecurityException | IOException var8) {
               if (log != null) {
                  log.log("Skipping invalid classpath element " + (String)workUnit.rawClasspathEntry.getKey() + " : " + var8);
               }
            }

         }
      };
   }

   private void findNestedClasspathElements(List<SimpleEntry<String, ClasspathElement>> classpathElts, LogNode log) {
      CollectionUtils.sortIfNotEmpty(classpathElts, new Comparator<SimpleEntry<String, ClasspathElement>>() {
         public int compare(SimpleEntry<String, ClasspathElement> o1, SimpleEntry<String, ClasspathElement> o2) {
            return ((String)o1.getKey()).compareTo((String)o2.getKey());
         }
      });

      for(int i = 0; i < classpathElts.size(); ++i) {
         SimpleEntry<String, ClasspathElement> ei = (SimpleEntry)classpathElts.get(i);
         String basePath = (String)ei.getKey();
         int basePathLen = basePath.length();

         for(int j = i + 1; j < classpathElts.size(); ++j) {
            SimpleEntry<String, ClasspathElement> ej = (SimpleEntry)classpathElts.get(j);
            String comparePath = (String)ej.getKey();
            int comparePathLen = comparePath.length();
            boolean foundNestedClasspathRoot = false;
            if (comparePath.startsWith(basePath) && comparePathLen > basePathLen) {
               char nextChar = comparePath.charAt(basePathLen);
               if (nextChar == '/' || nextChar == '!') {
                  String nestedClasspathRelativePath = comparePath.substring(basePathLen + 1);
                  if (nestedClasspathRelativePath.indexOf(33) < 0) {
                     foundNestedClasspathRoot = true;
                     ClasspathElement baseElement = (ClasspathElement)ei.getValue();
                     if (baseElement.nestedClasspathRootPrefixes == null) {
                        baseElement.nestedClasspathRootPrefixes = new ArrayList();
                     }

                     baseElement.nestedClasspathRootPrefixes.add(nestedClasspathRelativePath + "/");
                     if (log != null) {
                        log.log(basePath + " is a prefix of the nested element " + comparePath);
                     }
                  }
               }
            }

            if (!foundNestedClasspathRoot) {
               break;
            }
         }
      }

   }

   private void preprocessClasspathElementsByType(List<ClasspathElement> finalTraditionalClasspathEltOrder, LogNode classpathFinderLog) {
      List<SimpleEntry<String, ClasspathElement>> classpathEltDirs = new ArrayList();
      List<SimpleEntry<String, ClasspathElement>> classpathEltZips = new ArrayList();

      for(ClasspathElement classpathElt : finalTraditionalClasspathEltOrder) {
         if (classpathElt instanceof ClasspathElementDir) {
            classpathEltDirs.add(new SimpleEntry(((ClasspathElementDir)classpathElt).getFile().getPath(), classpathElt));
         } else if (classpathElt instanceof ClasspathElementZip) {
            ClasspathElementZip classpathEltZip = (ClasspathElementZip)classpathElt;
            classpathEltZips.add(new SimpleEntry(classpathEltZip.getZipFilePath(), classpathElt));
            if (classpathEltZip.logicalZipFile != null) {
               if (classpathEltZip.logicalZipFile.addExportsManifestEntryValue != null) {
                  for(String addExports : JarUtils.smartPathSplit(classpathEltZip.logicalZipFile.addExportsManifestEntryValue, ' ')) {
                     this.scanSpec.modulePathInfo.addExports.add(addExports + "=ALL-UNNAMED");
                  }
               }

               if (classpathEltZip.logicalZipFile.addOpensManifestEntryValue != null) {
                  for(String addOpens : JarUtils.smartPathSplit(classpathEltZip.logicalZipFile.addOpensManifestEntryValue, ' ')) {
                     this.scanSpec.modulePathInfo.addOpens.add(addOpens + "=ALL-UNNAMED");
                  }
               }

               if (classpathEltZip.logicalZipFile.automaticModuleNameManifestEntryValue != null) {
                  classpathEltZip.moduleNameFromManifestFile = classpathEltZip.logicalZipFile.automaticModuleNameManifestEntryValue;
               }
            }
         }
      }

      this.findNestedClasspathElements(classpathEltDirs, classpathFinderLog);
      this.findNestedClasspathElements(classpathEltZips, classpathFinderLog);
   }

   private void maskClassfiles(List<ClasspathElement> classpathElementOrder, LogNode maskLog) {
      Set<String> whitelistedClasspathRelativePathsFound = new HashSet();

      for(int classpathIdx = 0; classpathIdx < classpathElementOrder.size(); ++classpathIdx) {
         ClasspathElement classpathElement = (ClasspathElement)classpathElementOrder.get(classpathIdx);
         classpathElement.maskClassfiles(classpathIdx, whitelistedClasspathRelativePathsFound, maskLog);
      }

      if (maskLog != null) {
         maskLog.addElapsedTime();
      }

   }

   private ScanResult performScan(List<ClasspathElement> finalClasspathEltOrder, List<String> finalClasspathEltOrderStrs, ClassLoader[] classLoaderOrderRespectingParentDelegation) throws InterruptedException, ExecutionException {
      if (this.scanSpec.enableClassInfo) {
         this.maskClassfiles(finalClasspathEltOrder, this.topLevelLog == null ? null : this.topLevelLog.log("Masking classfiles"));
      }

      Map<File, Long> fileToLastModified = new HashMap();

      for(ClasspathElement classpathElement : finalClasspathEltOrder) {
         fileToLastModified.putAll(classpathElement.fileToLastModified);
      }

      Map<String, ClassInfo> classNameToClassInfo = new ConcurrentHashMap();
      Map<String, PackageInfo> packageNameToPackageInfo = new HashMap();
      Map<String, ModuleInfo> moduleNameToModuleInfo = new HashMap();
      if (this.scanSpec.enableClassInfo) {
         List<Scanner.ClassfileScanWorkUnit> classfileScanWorkItems = new ArrayList();
         Set<String> whitelistedClassNamesFound = new HashSet();

         for(ClasspathElement classpathElement : finalClasspathEltOrder) {
            for(Resource resource : classpathElement.whitelistedClassfileResources) {
               String className = JarUtils.classfilePathToClassName(resource.getPath());
               if (!whitelistedClassNamesFound.add(className) && !className.equals("module-info") && !className.equals("package-info") && !className.endsWith(".package-info")) {
                  throw new IllegalArgumentException("Class " + className + " should not have been scheduled more than once for scanning due to classpath masking -- please report this bug at: https://github.com/classgraph/classgraph/issues");
               }

               classfileScanWorkItems.add(new Scanner.ClassfileScanWorkUnit(classpathElement, resource, false));
            }
         }

         Queue<Classfile> scannedClassfiles = new ConcurrentLinkedQueue();
         Scanner.ClassfileScannerWorkUnitProcessor classfileWorkUnitProcessor = new Scanner.ClassfileScannerWorkUnitProcessor(this.scanSpec, finalClasspathEltOrder, Collections.unmodifiableSet(whitelistedClassNamesFound), scannedClassfiles);
         this.processWorkUnits(classfileScanWorkItems, this.topLevelLog == null ? null : this.topLevelLog.log("Scanning classfiles"), classfileWorkUnitProcessor);
         LogNode linkLog = this.topLevelLog == null ? null : this.topLevelLog.log("Linking related classfiles");

         while(!scannedClassfiles.isEmpty()) {
            Classfile c = (Classfile)scannedClassfiles.remove();
            c.link(classNameToClassInfo, packageNameToPackageInfo, moduleNameToModuleInfo);
         }

         if (linkLog != null) {
            linkLog.addElapsedTime();
         }
      } else if (this.topLevelLog != null) {
         this.topLevelLog.log("Classfile scanning is disabled");
      }

      return new ScanResult(this.scanSpec, finalClasspathEltOrder, finalClasspathEltOrderStrs, classLoaderOrderRespectingParentDelegation, classNameToClassInfo, packageNameToPackageInfo, moduleNameToModuleInfo, fileToLastModified, this.nestedJarHandler, this.topLevelLog);
   }

   private ScanResult openClasspathElementsThenScan() throws InterruptedException, ExecutionException {
      List<Scanner.ClasspathEntryWorkUnit> rawClasspathEntryWorkUnits = new ArrayList();

      for(Entry<String, ClassLoader> rawClasspathEntry : this.classpathFinder.getClasspathOrder().getOrder()) {
         rawClasspathEntryWorkUnits.add(new Scanner.ClasspathEntryWorkUnit(rawClasspathEntry, (ClasspathElement)null, rawClasspathEntryWorkUnits.size()));
      }

      Set<ClasspathElement> openedClasspathEltsSet = Collections.newSetFromMap(new ConcurrentHashMap());
      Queue<Entry<Integer, ClasspathElement>> toplevelClasspathEltOrder = new ConcurrentLinkedQueue();
      this.processWorkUnits(rawClasspathEntryWorkUnits, this.topLevelLog == null ? null : this.topLevelLog.log("Opening classpath elements"), this.newClasspathEntryWorkUnitProcessor(openedClasspathEltsSet, toplevelClasspathEltOrder));
      List<ClasspathElement> classpathEltOrder = this.findClasspathOrder(openedClasspathEltsSet, toplevelClasspathEltOrder);
      this.preprocessClasspathElementsByType(classpathEltOrder, this.topLevelLog == null ? null : this.topLevelLog.log("Finding nested classpath elements"));
      LogNode classpathOrderLog = this.topLevelLog == null ? null : this.topLevelLog.log("Final classpath element order:");
      int numElts = this.moduleOrder.size() + classpathEltOrder.size();
      List<ClasspathElement> finalClasspathEltOrder = new ArrayList(numElts);
      List<String> finalClasspathEltOrderStrs = new ArrayList(numElts);

      for(ClasspathElementModule classpathElt : this.moduleOrder) {
         finalClasspathEltOrder.add(classpathElt);
         finalClasspathEltOrderStrs.add(classpathElt.toString());
         if (classpathOrderLog != null) {
            ModuleRef moduleRef = classpathElt.getModuleRef();
            classpathOrderLog.log(moduleRef.toString());
         }
      }

      for(ClasspathElement classpathElt : classpathEltOrder) {
         finalClasspathEltOrder.add(classpathElt);
         finalClasspathEltOrderStrs.add(classpathElt.toString());
         if (classpathOrderLog != null) {
            classpathOrderLog.log(classpathElt.toString());
         }
      }

      this.processWorkUnits(finalClasspathEltOrder, this.topLevelLog == null ? null : this.topLevelLog.log("Scanning classpath elements"), new WorkQueue.WorkUnitProcessor<ClasspathElement>() {
         public void processWorkUnit(ClasspathElement classpathElement, int classpathElementIdx, WorkQueue<ClasspathElement> workQueueIgnored, LogNode pathScanLog) throws InterruptedException {
            classpathElement.scanPaths(classpathElementIdx, pathScanLog);
         }
      });
      List<ClasspathElement> finalClasspathEltOrderFiltered = finalClasspathEltOrder;
      if (!this.scanSpec.classpathElementResourcePathWhiteBlackList.whitelistIsEmpty()) {
         finalClasspathEltOrderFiltered = new ArrayList(finalClasspathEltOrder.size());

         for(ClasspathElement classpathElement : finalClasspathEltOrder) {
            if (classpathElement.containsSpecificallyWhitelistedClasspathElementResourcePath) {
               finalClasspathEltOrderFiltered.add(classpathElement);
            }
         }
      }

      if (this.scanSpec.performScan) {
         return (void)this.performScan(finalClasspathEltOrderFiltered, finalClasspathEltOrderStrs, this.classLoaderOrderRespectingParentDelegation);
      } else {
         if (this.topLevelLog != null) {
            this.topLevelLog.log("Only returning classpath elements (not performing a scan)");
         }

         return (void)(new ScanResult(this.scanSpec, finalClasspathEltOrderFiltered, finalClasspathEltOrderStrs, this.classLoaderOrderRespectingParentDelegation, (Map)null, (Map)null, (Map)null, (Map)null, this.nestedJarHandler, this.topLevelLog));
      }
   }

   public ScanResult call() throws InterruptedException, CancellationException, ExecutionException {
      ScanResult scanResult = null;
      long scanStart = System.currentTimeMillis();
      boolean removeTemporaryFilesAfterScan = this.scanSpec.removeTemporaryFilesAfterScan;

      try {
         scanResult = this.openClasspathElementsThenScan();
         if (this.topLevelLog != null) {
            this.topLevelLog.log("~", String.format("Total time: %.3f sec", (double)(System.currentTimeMillis() - scanStart) * 0.001D));
            this.topLevelLog.flush();
         }

         if (this.scanResultProcessor != null) {
            try {
               this.scanResultProcessor.processScanResult(scanResult);
            } finally {
               scanResult.close();
            }
         }
      } catch (Throwable var19) {
         Throwable e = var19;
         if (this.topLevelLog != null) {
            this.topLevelLog.log("~", !(var19 instanceof InterruptedException) && !(var19 instanceof CancellationException) ? (!(var19 instanceof ExecutionException) && !(var19 instanceof RuntimeException) ? var19.getMessage() : "Uncaught exception during scan") : "Scan interrupted or canceled", InterruptionChecker.getCause(var19));
            this.topLevelLog.flush();
         }

         removeTemporaryFilesAfterScan = true;
         this.interruptionChecker.interrupt();
         if (this.failureHandler == null) {
            throw var19;
         }

         try {
            this.failureHandler.onFailure(e);
         } catch (Exception var18) {
            if (this.topLevelLog != null) {
               this.topLevelLog.log("~", "The failure handler threw an exception:", var18);
               this.topLevelLog.flush();
            }

            ExecutionException failureHandlerException = new ExecutionException("Exception while calling failure handler", var18);
            failureHandlerException.addSuppressed(var19);
            throw failureHandlerException;
         }
      } finally {
         if (removeTemporaryFilesAfterScan) {
            this.nestedJarHandler.close(this.topLevelLog);
         }

      }

      return scanResult;
   }

   static class ClassfileScanWorkUnit {
      private final ClasspathElement classpathElement;
      private final Resource classfileResource;
      private final boolean isExternalClass;

      ClassfileScanWorkUnit(ClasspathElement classpathElement, Resource classfileResource, boolean isExternalClass) {
         this.classpathElement = classpathElement;
         this.classfileResource = classfileResource;
         this.isExternalClass = isExternalClass;
      }
   }

   private static class ClassfileScannerWorkUnitProcessor implements WorkQueue.WorkUnitProcessor<Scanner.ClassfileScanWorkUnit> {
      private final ScanSpec scanSpec;
      private final List<ClasspathElement> classpathOrder;
      private final Set<String> whitelistedClassNamesFound;
      private final Set<String> classNamesScheduledForExtendedScanning = Collections.newSetFromMap(new ConcurrentHashMap());
      private final Queue<Classfile> scannedClassfiles;
      private final ConcurrentHashMap<String, String> stringInternMap = new ConcurrentHashMap();

      public ClassfileScannerWorkUnitProcessor(ScanSpec scanSpec, List<ClasspathElement> classpathOrder, Set<String> whitelistedClassNamesFound, Queue<Classfile> scannedClassfiles) {
         this.scanSpec = scanSpec;
         this.classpathOrder = classpathOrder;
         this.whitelistedClassNamesFound = whitelistedClassNamesFound;
         this.scannedClassfiles = scannedClassfiles;
      }

      public void processWorkUnit(Scanner.ClassfileScanWorkUnit workUnit, int workUnitIdxIgnored, WorkQueue<Scanner.ClassfileScanWorkUnit> workQueue, LogNode log) throws InterruptedException {
         LogNode subLog = workUnit.classfileResource.scanLog == null ? null : workUnit.classfileResource.scanLog.log(workUnit.classfileResource.getPath(), "Parsing classfile");

         try {
            Classfile classfile = new Classfile(workUnit.classpathElement, this.classpathOrder, this.whitelistedClassNamesFound, this.classNamesScheduledForExtendedScanning, workUnit.classfileResource.getPath(), workUnit.classfileResource, workUnit.isExternalClass, this.stringInternMap, workQueue, this.scanSpec, subLog);
            this.scannedClassfiles.add(classfile);
         } catch (Classfile.SkipClassException var12) {
            if (subLog != null) {
               subLog.log(workUnit.classfileResource.getPath(), "Skipping classfile: " + var12.getMessage());
            }
         } catch (Classfile.ClassfileFormatException var13) {
            if (subLog != null) {
               subLog.log(workUnit.classfileResource.getPath(), "Invalid classfile: " + var13.getMessage());
            }
         } catch (IOException var14) {
            if (subLog != null) {
               subLog.log(workUnit.classfileResource.getPath(), "Could not read classfile: " + var14);
            }
         } finally {
            if (subLog != null) {
               subLog.addElapsedTime();
            }

         }

      }
   }

   static class ClasspathEntryWorkUnit {
      private final Entry<String, ClassLoader> rawClasspathEntry;
      private final ClasspathElement parentClasspathElement;
      private final int orderWithinParentClasspathElement;

      public ClasspathEntryWorkUnit(Entry<String, ClassLoader> rawClasspathEntry, ClasspathElement parentClasspathElement, int orderWithinParentClasspathElement) {
         this.rawClasspathEntry = rawClasspathEntry;
         this.parentClasspathElement = parentClasspathElement;
         this.orderWithinParentClasspathElement = orderWithinParentClasspathElement;
      }
   }
}
