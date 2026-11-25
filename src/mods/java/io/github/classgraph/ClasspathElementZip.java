package io.github.classgraph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandlerRegistry;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.fastzipfilereader.FastZipEntry;
import nonapi.io.github.classgraph.fastzipfilereader.LogicalZipFile;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.fastzipfilereader.ZipFileSlice;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.InputStreamOrByteBufferAdapter;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.URLPathEncoder;
import nonapi.io.github.classgraph.utils.VersionFinder;

class ClasspathElementZip extends ClasspathElement {
   private final String rawPath;
   LogicalZipFile logicalZipFile;
   private String packageRootPrefix = "";
   private String zipFilePath;
   private final ConcurrentHashMap<String, Resource> relativePathToResource = new ConcurrentHashMap();
   private final NestedJarHandler nestedJarHandler;
   String moduleNameFromManifestFile;
   private String derivedAutomaticModuleName;

   ClasspathElementZip(String rawPath, ClassLoader classLoader, NestedJarHandler nestedJarHandler, ScanSpec scanSpec) {
      super(classLoader, scanSpec);
      this.rawPath = rawPath;
      this.zipFilePath = rawPath;
      this.nestedJarHandler = nestedJarHandler;
   }

   void open(WorkQueue<Scanner.ClasspathEntryWorkUnit> workQueue, int classpathElementIdx, LogNode log) throws InterruptedException {
      if (!this.scanSpec.scanJars) {
         if (log != null) {
            this.log(classpathElementIdx, "Skipping classpath element, since jar scanning is disabled: " + this.rawPath, log);
         }

         this.skipClasspathElement = true;
      } else {
         LogNode subLog = log == null ? null : this.log(classpathElementIdx, "Opening jar: " + this.rawPath, log);
         int plingIdx = this.rawPath.indexOf(33);
         String outermostZipFilePathResolved = FastPathResolver.resolve(FileUtils.CURR_DIR_PATH, plingIdx < 0 ? this.rawPath : this.rawPath.substring(0, plingIdx));
         if (!this.scanSpec.jarWhiteBlackList.isWhitelistedAndNotBlacklisted(outermostZipFilePathResolved)) {
            if (subLog != null) {
               subLog.log("Skipping jarfile that is blacklisted or not whitelisted: " + this.rawPath);
            }

            this.skipClasspathElement = true;
         } else {
            try {
               Entry<LogicalZipFile, String> logicalZipFileAndPackageRoot;
               try {
                  logicalZipFileAndPackageRoot = (Entry)this.nestedJarHandler.nestedPathToLogicalZipFileAndPackageRootMap.get(this.rawPath, subLog);
               } catch (SingletonMap.NullSingletonException var17) {
                  throw new IOException("Could not get logical zipfile " + this.rawPath + " : " + var17);
               }

               this.logicalZipFile = (LogicalZipFile)logicalZipFileAndPackageRoot.getKey();
               if (this.logicalZipFile == null) {
                  throw new IOException("Logical zipfile was null");
               }

               this.zipFilePath = FastPathResolver.resolve(FileUtils.CURR_DIR_PATH, this.logicalZipFile.getPath());
               String packageRoot = (String)logicalZipFileAndPackageRoot.getValue();
               if (!packageRoot.isEmpty()) {
                  this.packageRootPrefix = packageRoot + "/";
               }
            } catch (IllegalArgumentException | IOException var18) {
               if (subLog != null) {
                  subLog.log("Could not open jarfile " + this.rawPath + " : " + var18);
               }

               this.skipClasspathElement = true;
               return;
            }

            if (!this.scanSpec.enableSystemJarsAndModules && this.logicalZipFile.isJREJar) {
               if (subLog != null) {
                  subLog.log("Ignoring JRE jar: " + this.rawPath);
               }

               this.skipClasspathElement = true;
            } else if (!this.logicalZipFile.isWhitelistedAndNotBlacklisted(this.scanSpec.jarWhiteBlackList)) {
               if (subLog != null) {
                  subLog.log("Skipping jarfile that is blacklisted or not whitelisted: " + this.rawPath);
               }

               this.skipClasspathElement = true;
            } else {
               int childClasspathEntryIdx = 0;
               if (this.scanSpec.scanNestedJars) {
                  for(FastZipEntry zipEntry : this.logicalZipFile.entries) {
                     for(String libDirPrefix : ClassLoaderHandlerRegistry.AUTOMATIC_LIB_DIR_PREFIXES) {
                        if (zipEntry.entryNameUnversioned.startsWith(libDirPrefix) && zipEntry.entryNameUnversioned.endsWith(".jar")) {
                           String entryPath = zipEntry.getPath();
                           if (subLog != null) {
                              subLog.log("Found nested lib jar: " + entryPath);
                           }

                           workQueue.addWorkUnit(new Scanner.ClasspathEntryWorkUnit(new SimpleEntry(entryPath, this.classLoader), this, childClasspathEntryIdx++));
                           break;
                        }
                     }
                  }
               }

               Set<String> scheduledChildClasspathElements = new HashSet();
               scheduledChildClasspathElements.add(this.rawPath);
               if (this.logicalZipFile.classPathManifestEntryValue != null) {
                  String jarParentDir = FileUtils.getParentDirPath(this.logicalZipFile.getPathWithinParentZipFileSlice());

                  for(String childClassPathEltPathRelative : this.logicalZipFile.classPathManifestEntryValue.split(" ")) {
                     if (!childClassPathEltPathRelative.isEmpty()) {
                        String childClassPathEltPath = FastPathResolver.resolve(jarParentDir, childClassPathEltPathRelative);
                        ZipFileSlice parentZipFileSlice = this.logicalZipFile.getParentZipFileSlice();
                        String childClassPathEltPathWithPrefix = parentZipFileSlice == null ? childClassPathEltPath : parentZipFileSlice.getPath() + (childClassPathEltPath.startsWith("/") ? "!" : "!/") + childClassPathEltPath;
                        if (scheduledChildClasspathElements.add(childClassPathEltPathWithPrefix)) {
                           workQueue.addWorkUnit(new Scanner.ClasspathEntryWorkUnit(new SimpleEntry(childClassPathEltPathWithPrefix, this.classLoader), this, childClasspathEntryIdx++));
                        }
                     }
                  }
               }

               if (this.logicalZipFile.bundleClassPathManifestEntryValue != null) {
                  String zipFilePathPrefix = this.zipFilePath + "!/";
                  String[] var25 = this.logicalZipFile.bundleClassPathManifestEntryValue.split(",");
                  int var27 = var25.length;

                  for(int var29 = 0; var29 < var27; ++var29) {
                     String childBundlePath;
                     for(childBundlePath = var25[var29]; childBundlePath.startsWith("/"); childBundlePath = childBundlePath.substring(1)) {
                        ;
                     }

                     if (!childBundlePath.isEmpty() && !childBundlePath.equals(".")) {
                        String childClassPathEltPath = zipFilePathPrefix + FileUtils.sanitizeEntryPath(childBundlePath, true);
                        if (scheduledChildClasspathElements.add(childClassPathEltPath)) {
                           workQueue.addWorkUnit(new Scanner.ClasspathEntryWorkUnit(new SimpleEntry(childClassPathEltPath, this.classLoader), this, childClasspathEntryIdx++));
                        }
                     }
                  }
               }

            }
         }
      }
   }

   private Resource newResource(final FastZipEntry zipEntry, final String pathRelativeToPackageRoot) {
      return new Resource(this, zipEntry.uncompressedSize, zipEntry.lastModified, zipEntry.posixFilePermissions) {
         public String getPath() {
            return pathRelativeToPackageRoot;
         }

         public String getPathRelativeToClasspathElement() {
            return zipEntry.entryName;
         }

         public synchronized InputStream open() throws IOException {
            if (ClasspathElementZip.this.skipClasspathElement) {
               throw new IOException("Jarfile could not be opened");
            } else {
               this.markAsOpen();

               try {
                  this.inputStream = new Resource.InputStreamResourceCloser(this, zipEntry.open());
                  this.length = zipEntry.uncompressedSize;
                  return this.inputStream;
               } catch (IOException var2) {
                  this.close();
                  throw var2;
               } catch (InterruptedException var3) {
                  this.close();
                  ClasspathElementZip.this.nestedJarHandler.interruptionChecker.interrupt();
                  throw new IOException(var3);
               }
            }
         }

         synchronized InputStreamOrByteBufferAdapter openOrRead() throws IOException {
            return new InputStreamOrByteBufferAdapter(this.open());
         }

         public synchronized ByteBuffer read() throws IOException {
            try {
               if (zipEntry.canGetAsSlice()) {
                  this.markAsOpen();
                  this.length = zipEntry.compressedSize;
                  return zipEntry.getAsSlice();
               } else {
                  this.open();
                  return this.inputStreamToByteBuffer();
               }
            } catch (IOException var2) {
               this.close();
               throw var2;
            } catch (InterruptedException var3) {
               this.close();
               ClasspathElementZip.this.nestedJarHandler.interruptionChecker.interrupt();
               throw new IOException(var3);
            }
         }

         public synchronized byte[] load() throws IOException {
            byte[] var2;
            try {
               this.open();
               byte[] byteArray = this.inputStreamToByteArray();
               this.length = (long)byteArray.length;
               var2 = byteArray;
            } finally {
               this.close();
            }

            return var2;
         }

         public synchronized void close() {
            super.close();
            if (this.byteBuffer != null) {
               this.byteBuffer = null;
            }

            this.markAsClosed();
         }
      };
   }

   Resource getResource(String relativePath) {
      return (Resource)this.relativePathToResource.get(relativePath);
   }

   void scanPaths(int classpathElementIdx, LogNode log) {
      if (this.logicalZipFile == null) {
         this.skipClasspathElement = true;
      }

      if (!this.skipClasspathElement) {
         if (this.scanned.getAndSet(true)) {
            throw new IllegalArgumentException("Already scanned classpath element " + this.getZipFilePath());
         } else {
            LogNode subLog = log == null ? null : this.log(classpathElementIdx, "Scanning jarfile classpath element " + this.getZipFilePath(), log);
            Set<String> loggedNestedClasspathRootPrefixes = null;
            String prevParentRelativePath = null;
            ScanSpec.ScanSpecPathMatch prevParentMatchStatus = null;

            for(FastZipEntry zipEntry : this.logicalZipFile.entries) {
               String relativePath = zipEntry.entryNameUnversioned;
               if (relativePath.startsWith("META-INF/versions/")) {
                  if (subLog != null) {
                     if (VersionFinder.JAVA_MAJOR_VERSION < 9) {
                        subLog.log("Skipping versioned entry in jar, because JRE version " + VersionFinder.JAVA_MAJOR_VERSION + " does not support this: " + relativePath);
                     } else {
                        subLog.log("Found unexpected versioned entry in jar (the jar's manifest file may be missing the \"Multi-Release\" key) -- skipping: " + relativePath);
                     }
                  }
               } else {
                  if (this.nestedClasspathRootPrefixes != null) {
                     boolean reachedNestedRoot = false;

                     for(String nestedClasspathRoot : this.nestedClasspathRootPrefixes) {
                        if (relativePath.startsWith(nestedClasspathRoot)) {
                           if (subLog != null) {
                              if (loggedNestedClasspathRootPrefixes == null) {
                                 loggedNestedClasspathRootPrefixes = new HashSet();
                              }

                              if (loggedNestedClasspathRootPrefixes.add(nestedClasspathRoot)) {
                                 subLog.log("Reached nested classpath root, stopping recursion to avoid duplicate scanning: " + nestedClasspathRoot);
                              }
                           }

                           reachedNestedRoot = true;
                           break;
                        }
                     }

                     if (reachedNestedRoot) {
                        continue;
                     }
                  }

                  if (this.packageRootPrefix.isEmpty() || relativePath.startsWith(this.packageRootPrefix)) {
                     if (!this.packageRootPrefix.isEmpty()) {
                        relativePath = relativePath.substring(this.packageRootPrefix.length());
                     } else {
                        for(int i = 0; i < ClassLoaderHandlerRegistry.AUTOMATIC_PACKAGE_ROOT_PREFIXES.length; ++i) {
                           if (relativePath.startsWith(ClassLoaderHandlerRegistry.AUTOMATIC_PACKAGE_ROOT_PREFIXES[i])) {
                              relativePath = relativePath.substring(ClassLoaderHandlerRegistry.AUTOMATIC_PACKAGE_ROOT_PREFIXES[i].length());
                           }
                        }
                     }

                     this.checkResourcePathWhiteBlackList(relativePath, log);
                     if (this.skipClasspathElement) {
                        return;
                     }

                     int lastSlashIdx = relativePath.lastIndexOf(47);
                     String parentRelativePath = lastSlashIdx < 0 ? "/" : relativePath.substring(0, lastSlashIdx + 1);
                     boolean parentRelativePathChanged = !parentRelativePath.equals(prevParentRelativePath);
                     ScanSpec.ScanSpecPathMatch parentMatchStatus = parentRelativePathChanged ? this.scanSpec.dirWhitelistMatchStatus(parentRelativePath) : prevParentMatchStatus;
                     prevParentRelativePath = parentRelativePath;
                     prevParentMatchStatus = parentMatchStatus;
                     if (parentMatchStatus == ScanSpec.ScanSpecPathMatch.HAS_BLACKLISTED_PATH_PREFIX) {
                        if (subLog != null) {
                           subLog.log("Skipping blacklisted path: " + relativePath);
                        }
                     } else {
                        Resource resource = this.newResource(zipEntry, relativePath);
                        if (this.relativePathToResource.putIfAbsent(relativePath, resource) == null) {
                           if (parentMatchStatus != ScanSpec.ScanSpecPathMatch.HAS_WHITELISTED_PATH_PREFIX && parentMatchStatus != ScanSpec.ScanSpecPathMatch.AT_WHITELISTED_PATH && (parentMatchStatus != ScanSpec.ScanSpecPathMatch.AT_WHITELISTED_CLASS_PACKAGE || !this.scanSpec.classfileIsSpecificallyWhitelisted(relativePath))) {
                              if (this.scanSpec.enableClassInfo && relativePath.equals("module-info.class")) {
                                 this.addWhitelistedResource(resource, parentMatchStatus, true, subLog);
                              }
                           } else {
                              this.addWhitelistedResource(resource, parentMatchStatus, false, subLog);
                           }
                        }
                     }
                  }
               }
            }

            File zipfile = this.getFile();
            if (zipfile != null) {
               this.fileToLastModified.put(zipfile, Long.valueOf(zipfile.lastModified()));
            }

            this.finishScanPaths(subLog);
         }
      }
   }

   public String getModuleName() {
      String moduleName = this.moduleNameFromModuleDescriptor;
      if (moduleName == null || moduleName.isEmpty()) {
         moduleName = this.moduleNameFromManifestFile;
      }

      if (moduleName == null || moduleName.isEmpty()) {
         if (this.derivedAutomaticModuleName == null) {
            this.derivedAutomaticModuleName = JarUtils.derivedAutomaticModuleName(this.zipFilePath);
         }

         moduleName = this.derivedAutomaticModuleName;
      }

      return moduleName != null && !moduleName.isEmpty() ? moduleName : null;
   }

   String getZipFilePath() {
      return this.packageRootPrefix.isEmpty() ? this.zipFilePath : this.zipFilePath + "!/" + this.packageRootPrefix.substring(0, this.packageRootPrefix.length() - 1);
   }

   URI getURI() {
      try {
         return new URI(URLPathEncoder.normalizeURLPath(this.getZipFilePath()));
      } catch (URISyntaxException var2) {
         throw new IllegalArgumentException("Could not form URI: " + var2);
      }
   }

   File getFile() {
      if (this.logicalZipFile != null) {
         return this.logicalZipFile.getPhysicalFile();
      } else {
         int plingIdx = this.rawPath.indexOf(33);
         String outermostZipFilePathResolved = FastPathResolver.resolve(FileUtils.CURR_DIR_PATH, plingIdx < 0 ? this.rawPath : this.rawPath.substring(0, plingIdx));
         return new File(outermostZipFilePathResolved);
      }
   }

   public String toString() {
      return this.getZipFilePath();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ClasspathElementZip)) {
         return false;
      } else {
         ClasspathElementZip other = (ClasspathElementZip)obj;
         return this.getZipFilePath().equals(other.getZipFilePath());
      }
   }

   public int hashCode() {
      return this.getZipFilePath().hashCode();
   }
}
