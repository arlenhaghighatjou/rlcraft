package io.github.classgraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandlerRegistry;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.InputStreamOrByteBufferAdapter;
import nonapi.io.github.classgraph.utils.LogNode;

class ClasspathElementDir extends ClasspathElement {
   private final File classpathEltDir;
   private final int ignorePrefixLen;
   private final Set<String> scannedCanonicalPaths = new HashSet();

   ClasspathElementDir(File classpathEltDir, ClassLoader classLoader, ScanSpec scanSpec) {
      super(classLoader, scanSpec);
      this.classpathEltDir = classpathEltDir;
      this.ignorePrefixLen = classpathEltDir.getPath().length() + 1;
   }

   void open(WorkQueue<Scanner.ClasspathEntryWorkUnit> workQueue, int classpathElementIdx, LogNode log) {
      if (!this.scanSpec.scanDirs) {
         if (log != null) {
            this.log(classpathElementIdx, "Skipping classpath element, since dir scanning is disabled: " + this.classpathEltDir, log);
         }

         this.skipClasspathElement = true;
      } else {
         try {
            int childClasspathEntryIdx = 0;

            for(String libDirPrefix : ClassLoaderHandlerRegistry.AUTOMATIC_LIB_DIR_PREFIXES) {
               File libDir = new File(this.classpathEltDir, libDirPrefix);
               if (FileUtils.canReadAndIsDir(libDir)) {
                  File[] listFiles = libDir.listFiles();
                  if (listFiles != null) {
                     Arrays.sort(listFiles);

                     for(File file : listFiles) {
                        if (file.isFile() && file.getName().endsWith(".jar")) {
                           if (log != null) {
                              this.log(classpathElementIdx, "Found lib jar: " + file, log);
                           }

                           workQueue.addWorkUnit(new Scanner.ClasspathEntryWorkUnit(new SimpleEntry(file.getPath(), this.classLoader), this, childClasspathEntryIdx++));
                        }
                     }
                  }
               }
            }

            for(String packageRootPrefix : ClassLoaderHandlerRegistry.AUTOMATIC_PACKAGE_ROOT_PREFIXES) {
               File packageRootDir = new File(this.classpathEltDir, packageRootPrefix);
               if (FileUtils.canReadAndIsDir(packageRootDir)) {
                  if (log != null) {
                     this.log(classpathElementIdx, "Found package root: " + packageRootDir, log);
                  }

                  workQueue.addWorkUnit(new Scanner.ClasspathEntryWorkUnit(new SimpleEntry(packageRootDir.getPath(), this.classLoader), this, childClasspathEntryIdx++));
               }
            }

         } catch (SecurityException var15) {
            if (log != null) {
               this.log(classpathElementIdx, "Skipping classpath element, since dir cannot be accessed: " + this.classpathEltDir, log);
            }

            this.skipClasspathElement = true;
         }
      }
   }

   private Resource newResource(final String relativePath, final File classpathResourceFile) {
      Set<PosixFilePermission> posixFilePermissions = null;

      try {
         posixFilePermissions = ((PosixFileAttributes)Files.readAttributes(classpathResourceFile.toPath(), PosixFileAttributes.class)).permissions();
      } catch (IOException | SecurityException | UnsupportedOperationException var5) {
         ;
      }

      return new Resource(this, classpathResourceFile.length(), classpathResourceFile.lastModified(), posixFilePermissions) {
         private RandomAccessFile randomAccessFile;
         private FileChannel fileChannel;

         public String getPath() {
            return relativePath;
         }

         public String getPathRelativeToClasspathElement() {
            return relativePath;
         }

         public synchronized ByteBuffer read() throws IOException {
            if (ClasspathElementDir.this.skipClasspathElement) {
               throw new IOException("Parent directory could not be opened");
            } else {
               this.markAsOpen();

               try {
                  this.randomAccessFile = new RandomAccessFile(classpathResourceFile, "r");
                  this.fileChannel = this.randomAccessFile.getChannel();
                  MappedByteBuffer buffer = null;

                  try {
                     buffer = this.fileChannel.map(MapMode.READ_ONLY, 0L, this.fileChannel.size());
                  } catch (FileNotFoundException var3) {
                     throw var3;
                  } catch (OutOfMemoryError | IOException var4) {
                     System.gc();
                     System.runFinalization();
                     buffer = this.fileChannel.map(MapMode.READ_ONLY, 0L, this.fileChannel.size());
                  }

                  this.byteBuffer = buffer;
                  this.length = (long)this.byteBuffer.remaining();
                  return this.byteBuffer;
               } catch (SecurityException | OutOfMemoryError | IOException var5) {
                  this.close();
                  throw new IOException("Could not open " + this, var5);
               }
            }
         }

         synchronized InputStreamOrByteBufferAdapter openOrRead() throws IOException {
            return this.length >= (long)FileUtils.FILECHANNEL_FILE_SIZE_THRESHOLD ? new InputStreamOrByteBufferAdapter(this.read()) : new InputStreamOrByteBufferAdapter(this.inputStream = new Resource.InputStreamResourceCloser(this, Files.newInputStream(classpathResourceFile.toPath())));
         }

         public synchronized InputStream open() throws IOException {
            if (this.length >= (long)FileUtils.FILECHANNEL_FILE_SIZE_THRESHOLD) {
               this.read();
               return this.inputStream = new Resource.InputStreamResourceCloser(this, this.byteBufferToInputStream());
            } else {
               this.markAsOpen();

               try {
                  return this.inputStream = new Resource.InputStreamResourceCloser(this, Files.newInputStream(classpathResourceFile.toPath()));
               } catch (SecurityException | IOException var2) {
                  this.close();
                  throw new IOException("Could not open " + this, var2);
               }
            }
         }

         public synchronized byte[] load() throws IOException {
            byte[] var2;
            try {
               byte[] byteArray;
               if (this.length >= (long)FileUtils.FILECHANNEL_FILE_SIZE_THRESHOLD) {
                  this.read();
                  byteArray = this.byteBufferToByteArray();
               } else {
                  this.open();
                  byteArray = FileUtils.readAllBytesAsArray(this.inputStream, this.length);
               }

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
               FileUtils.closeDirectByteBuffer(this.byteBuffer, (LogNode)null);
               this.byteBuffer = null;
            }

            if (this.fileChannel != null) {
               try {
                  this.fileChannel.close();
               } catch (IOException var3) {
                  ;
               }

               this.fileChannel = null;
            }

            if (this.randomAccessFile != null) {
               try {
                  this.randomAccessFile.close();
               } catch (IOException var2) {
                  ;
               }

               this.randomAccessFile = null;
            }

            this.markAsClosed();
         }
      };
   }

   Resource getResource(String relativePath) {
      File resourceFile = new File(this.classpathEltDir, relativePath);
      return FileUtils.canReadAndIsFile(resourceFile) ? this.newResource(relativePath, resourceFile) : null;
   }

   private void scanDirRecursively(File dir, LogNode log) {
      if (!this.skipClasspathElement) {
         String canonicalPath;
         try {
            canonicalPath = dir.getCanonicalPath();
            if (!this.scannedCanonicalPaths.add(canonicalPath)) {
               if (log != null) {
                  log.log("Reached symlink cycle, stopping recursion: " + dir);
               }

               return;
            }
         } catch (SecurityException | IOException var15) {
            if (log != null) {
               log.log("Could not canonicalize path: " + dir, var15);
            }

            return;
         }

         String dirPath = dir.getPath();
         String dirRelativePath = this.ignorePrefixLen > dirPath.length() ? "/" : dirPath.substring(this.ignorePrefixLen).replace(File.separatorChar, '/') + "/";
         if (this.nestedClasspathRootPrefixes != null && this.nestedClasspathRootPrefixes.contains(dirRelativePath)) {
            if (log != null) {
               log.log("Reached nested classpath root, stopping recursion to avoid duplicate scanning: " + dirRelativePath);
            }

         } else if (dirRelativePath.startsWith("META-INF/versions/")) {
            if (log != null) {
               log.log("Found unexpected nested versioned entry in directory classpath element -- skipping: " + dirRelativePath);
            }

         } else {
            this.checkResourcePathWhiteBlackList(dirRelativePath, log);
            if (!this.skipClasspathElement) {
               ScanSpec.ScanSpecPathMatch parentMatchStatus = this.scanSpec.dirWhitelistMatchStatus(dirRelativePath);
               if (parentMatchStatus == ScanSpec.ScanSpecPathMatch.HAS_BLACKLISTED_PATH_PREFIX) {
                  if (log != null) {
                     log.log("Reached blacklisted directory, stopping recursive scan: " + dirRelativePath);
                  }

               } else if (parentMatchStatus != ScanSpec.ScanSpecPathMatch.NOT_WITHIN_WHITELISTED_PATH) {
                  File[] filesInDir = dir.listFiles();
                  if (filesInDir == null) {
                     if (log != null) {
                        log.log("Invalid directory " + dir);
                     }

                  } else {
                     Arrays.sort(filesInDir);
                     LogNode subLog = log == null ? null : log.log("1:" + canonicalPath, "Scanning directory: " + dir + (dir.getPath().equals(canonicalPath) ? "" : " ; canonical path: " + canonicalPath));
                     if (parentMatchStatus != ScanSpec.ScanSpecPathMatch.ANCESTOR_OF_WHITELISTED_PATH) {
                        for(File fileInDir : filesInDir) {
                           if (fileInDir.isFile()) {
                              String fileInDirRelativePath = !dirRelativePath.isEmpty() && !"/".equals(dirRelativePath) ? dirRelativePath + fileInDir.getName() : fileInDir.getName();
                              this.checkResourcePathWhiteBlackList(fileInDirRelativePath, subLog);
                              if (this.skipClasspathElement) {
                                 return;
                              }

                              if (parentMatchStatus != ScanSpec.ScanSpecPathMatch.HAS_WHITELISTED_PATH_PREFIX && parentMatchStatus != ScanSpec.ScanSpecPathMatch.AT_WHITELISTED_PATH && (parentMatchStatus != ScanSpec.ScanSpecPathMatch.AT_WHITELISTED_CLASS_PACKAGE || !this.scanSpec.classfileIsSpecificallyWhitelisted(fileInDirRelativePath))) {
                                 if (subLog != null) {
                                    subLog.log("Skipping non-whitelisted file: " + fileInDirRelativePath);
                                 }
                              } else {
                                 Resource resource = this.newResource(fileInDirRelativePath, fileInDir);
                                 this.addWhitelistedResource(resource, parentMatchStatus, false, subLog);
                                 this.fileToLastModified.put(fileInDir, Long.valueOf(fileInDir.lastModified()));
                              }
                           }
                        }
                     } else if (this.scanSpec.enableClassInfo && dirRelativePath.equals("/")) {
                        for(File fileInDir : filesInDir) {
                           if (fileInDir.getName().equals("module-info.class") && fileInDir.isFile()) {
                              Resource resource = this.newResource("module-info.class", fileInDir);
                              this.addWhitelistedResource(resource, parentMatchStatus, true, subLog);
                              this.fileToLastModified.put(fileInDir, Long.valueOf(fileInDir.lastModified()));
                           }
                        }
                     }

                     for(File fileInDir : filesInDir) {
                        if (fileInDir.isDirectory()) {
                           this.scanDirRecursively(fileInDir, subLog);
                           if (this.skipClasspathElement) {
                              if (subLog != null) {
                                 subLog.addElapsedTime();
                              }

                              return;
                           }
                        }
                     }

                     if (subLog != null) {
                        subLog.addElapsedTime();
                     }

                     this.fileToLastModified.put(dir, Long.valueOf(dir.lastModified()));
                  }
               }
            }
         }
      }
   }

   void scanPaths(int classpathElementIdx, LogNode log) {
      if (!this.skipClasspathElement) {
         if (this.scanned.getAndSet(true)) {
            throw new IllegalArgumentException("Already scanned classpath element " + this.toString());
         } else {
            LogNode subLog = log == null ? null : this.log(classpathElementIdx, "Scanning directory classpath element " + this.classpathEltDir, log);
            this.scanDirRecursively(this.classpathEltDir, subLog);
            this.finishScanPaths(subLog);
         }
      }
   }

   public String getModuleName() {
      return this.moduleNameFromModuleDescriptor != null && !this.moduleNameFromModuleDescriptor.isEmpty() ? this.moduleNameFromModuleDescriptor : null;
   }

   public File getFile() {
      return this.classpathEltDir;
   }

   URI getURI() {
      return this.classpathEltDir.toURI();
   }

   public String toString() {
      return this.classpathEltDir.toString();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ClasspathElementDir)) {
         return false;
      } else {
         ClasspathElementDir other = (ClasspathElementDir)obj;
         return this.classpathEltDir.equals(other.classpathEltDir);
      }
   }

   public int hashCode() {
      return this.classpathEltDir.hashCode();
   }
}
