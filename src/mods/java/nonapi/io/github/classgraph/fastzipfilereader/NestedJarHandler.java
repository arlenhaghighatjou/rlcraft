package nonapi.io.github.classgraph.fastzipfilereader;

import io.github.classgraph.ClassGraphException;
import io.github.classgraph.ModuleReaderProxy;
import io.github.classgraph.ModuleRef;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Queue;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.concurrency.InterruptionChecker;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.recycler.Recycler;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class NestedJarHandler {
   private final ScanSpec scanSpec;
   private SingletonMap<File, PhysicalZipFile, IOException> canonicalFileToPhysicalZipFileMap = new SingletonMap<File, PhysicalZipFile, IOException>() {
      public PhysicalZipFile newInstance(File canonicalFile, LogNode log) throws IOException {
         if (NestedJarHandler.this.closed.get()) {
            throw ClassGraphException.newClassGraphException(NestedJarHandler.class.getSimpleName() + " already closed");
         } else {
            return new PhysicalZipFile(canonicalFile, NestedJarHandler.this);
         }
      }
   };
   private Queue<PhysicalZipFile> additionalAllocatedPhysicalZipFiles = new ConcurrentLinkedQueue();
   private SingletonMap<FastZipEntry, ZipFileSlice, IOException> fastZipEntryToZipFileSliceMap = new SingletonMap<FastZipEntry, ZipFileSlice, IOException>() {
      public ZipFileSlice newInstance(FastZipEntry childZipEntry, LogNode log) throws IOException, InterruptedException {
         ZipFileSlice childZipEntrySlice;
         if (!childZipEntry.isDeflated) {
            childZipEntrySlice = new ZipFileSlice(childZipEntry);
         } else {
            if (childZipEntry.uncompressedSize >= 0L && childZipEntry.uncompressedSize < 33554432L && childZipEntry.compressedSize < 33554432L) {
               childZipEntrySlice = null;
            } else {
               File tempFile = null;

               try {
                  tempFile = NestedJarHandler.this.makeTempFile(childZipEntry.entryName, true);
                  if (log != null) {
                     log.log("Deflating zip entry to temporary file: " + childZipEntry + " ; uncompressed size: " + childZipEntry.uncompressedSize + " ; temp file: " + tempFile);
                  }

                  InputStream inputStream = childZipEntry.open();

                  try {
                     Files.copy(inputStream, tempFile.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                  } catch (Throwable var11) {
                     if (inputStream != null) {
                        try {
                           inputStream.close();
                        } catch (Throwable var9) {
                           var11.addSuppressed(var9);
                        }
                     }

                     throw var11;
                  }

                  if (inputStream != null) {
                     inputStream.close();
                  }

                  PhysicalZipFile physicalZipFile = NestedJarHandler.this.canonicalFileToPhysicalZipFile(tempFile, log);
                  NestedJarHandler.this.additionalAllocatedPhysicalZipFiles.add(physicalZipFile);
                  childZipEntrySlice = new ZipFileSlice(physicalZipFile);
               } catch (IOException | IllegalArgumentException var13) {
                  if (log != null) {
                     log.log("Deflating zip entry to temporary file failed: " + var13);
                  }

                  if (tempFile != null) {
                     try {
                        Files.delete(tempFile.toPath());
                     } catch (SecurityException | IOException var12) {
                        if (log != null) {
                           log.log("Removing temporary file failed: " + var12);
                        }
                     }
                  }

                  childZipEntrySlice = null;
               }
            }

            if (childZipEntrySlice == null) {
               if (childZipEntry.uncompressedSize > 2147483639L) {
                  throw new IOException("Uncompressed size of zip entry (" + childZipEntry.uncompressedSize + ") is too large to inflate to memory: " + childZipEntry.entryName);
               }

               if (log != null) {
                  log.log("Deflating zip entry to RAM: " + childZipEntry + " ; uncompressed size: " + childZipEntry.uncompressedSize);
               }

               InputStream inputStream = childZipEntry.open();

               ByteBuffer byteBuffer;
               try {
                  byteBuffer = ByteBuffer.wrap(FileUtils.readAllBytesAsArray(inputStream, childZipEntry.uncompressedSize));
               } catch (Throwable var10) {
                  if (inputStream != null) {
                     try {
                        inputStream.close();
                     } catch (Throwable var8) {
                        var10.addSuppressed(var8);
                     }
                  }

                  throw var10;
               }

               if (inputStream != null) {
                  inputStream.close();
               }

               PhysicalZipFile physicalZipFileInRam = new PhysicalZipFile(byteBuffer, childZipEntry.parentLogicalZipFile.physicalZipFile.getFile(), childZipEntry.getPath(), NestedJarHandler.this);
               NestedJarHandler.this.additionalAllocatedPhysicalZipFiles.add(physicalZipFileInRam);
               childZipEntrySlice = new ZipFileSlice(physicalZipFileInRam, childZipEntry);
            }
         }

         return childZipEntrySlice;
      }
   };
   private SingletonMap<ZipFileSlice, LogicalZipFile, IOException> zipFileSliceToLogicalZipFileMap = new SingletonMap<ZipFileSlice, LogicalZipFile, IOException>() {
      public LogicalZipFile newInstance(ZipFileSlice zipFileSlice, LogNode log) throws IOException, InterruptedException {
         if (NestedJarHandler.this.closed.get()) {
            throw ClassGraphException.newClassGraphException(NestedJarHandler.class.getSimpleName() + " already closed");
         } else {
            LogicalZipFile logicalZipFile = new LogicalZipFile(zipFileSlice, log);
            NestedJarHandler.this.allocatedLogicalZipFiles.add(logicalZipFile);
            return logicalZipFile;
         }
      }
   };
   private final Queue<LogicalZipFile> allocatedLogicalZipFiles = new ConcurrentLinkedQueue();
   public SingletonMap<String, Entry<LogicalZipFile, String>, IOException> nestedPathToLogicalZipFileAndPackageRootMap = new SingletonMap<String, Entry<LogicalZipFile, String>, IOException>() {
      public Entry<LogicalZipFile, String> newInstance(String nestedJarPathRaw, LogNode log) throws IOException, InterruptedException {
         if (NestedJarHandler.this.closed.get()) {
            throw ClassGraphException.newClassGraphException(NestedJarHandler.class.getSimpleName() + " already closed");
         } else {
            String nestedJarPath = FastPathResolver.resolve(nestedJarPathRaw);
            int lastPlingIdx = nestedJarPath.lastIndexOf(33);
            if (lastPlingIdx < 0) {
               boolean isRemote = nestedJarPath.startsWith("http://") || nestedJarPath.startsWith("https://");
               PhysicalZipFile physicalZipFile;
               if (isRemote) {
                  if (!NestedJarHandler.this.scanSpec.enableRemoteJarScanning) {
                     throw new IOException("Remote jar scanning has not been enabled, cannot scan classpath element: " + nestedJarPath);
                  }

                  physicalZipFile = NestedJarHandler.this.downloadJarFromURL(nestedJarPath, log);
               } else {
                  try {
                     File canonicalFile = (new File(nestedJarPath)).getCanonicalFile();
                     physicalZipFile = NestedJarHandler.this.canonicalFileToPhysicalZipFile(canonicalFile, log);
                  } catch (SecurityException var16) {
                     throw new IOException("Path component " + nestedJarPath + " could not be canonicalized: " + var16);
                  }
               }

               ZipFileSlice topLevelSlice = new ZipFileSlice(physicalZipFile);

               LogicalZipFile logicalZipFile;
               try {
                  logicalZipFile = NestedJarHandler.this.zipFileSliceToLogicalZipFileMap.get(topLevelSlice, log);
               } catch (SingletonMap.NullSingletonException var15) {
                  throw new IOException("Could not get toplevel slice " + topLevelSlice + " : " + var15);
               }

               return new SimpleEntry(logicalZipFile, "");
            } else {
               String parentPath = nestedJarPath.substring(0, lastPlingIdx);
               String childPath = nestedJarPath.substring(lastPlingIdx + 1);
               childPath = FileUtils.sanitizeEntryPath(childPath, true);

               Entry<LogicalZipFile, String> parentLogicalZipFileAndPackageRoot;
               try {
                  parentLogicalZipFileAndPackageRoot = (Entry)NestedJarHandler.this.nestedPathToLogicalZipFileAndPackageRootMap.get(parentPath, log);
               } catch (SingletonMap.NullSingletonException var19) {
                  throw new IOException("Could not get parent logical zipfile " + parentPath + " : " + var19);
               }

               LogicalZipFile parentLogicalZipFile = (LogicalZipFile)parentLogicalZipFileAndPackageRoot.getKey();

               boolean isDirectory;
               for(isDirectory = false; childPath.endsWith("/"); childPath = childPath.substring(0, childPath.length() - 1)) {
                  isDirectory = true;
               }

               FastZipEntry childZipEntry = null;
               if (!isDirectory) {
                  for(FastZipEntry entry : parentLogicalZipFile.entries) {
                     if (entry.entryName.equals(childPath)) {
                        childZipEntry = entry;
                        break;
                     }
                  }
               }

               if (childZipEntry == null) {
                  String childPathPrefix = childPath + "/";

                  for(FastZipEntry entry : parentLogicalZipFile.entries) {
                     if (entry.entryName.startsWith(childPathPrefix)) {
                        isDirectory = true;
                        break;
                     }
                  }
               }

               if (isDirectory) {
                  if (!childPath.isEmpty()) {
                     if (log != null) {
                        log.log("Path " + childPath + " in jarfile " + parentLogicalZipFile + " is a directory, not a file -- using as package root");
                     }

                     parentLogicalZipFile.classpathRoots.add(childPath);
                  }

                  return new SimpleEntry(parentLogicalZipFile, childPath);
               } else if (childZipEntry == null) {
                  throw new IOException("Path " + childPath + " does not exist in jarfile " + parentLogicalZipFile);
               } else if (!NestedJarHandler.this.scanSpec.scanNestedJars) {
                  throw new IOException("Nested jar scanning is disabled -- skipping nested jar " + nestedJarPath);
               } else {
                  ZipFileSlice childZipEntrySlice;
                  try {
                     childZipEntrySlice = NestedJarHandler.this.fastZipEntryToZipFileSliceMap.get(childZipEntry, log);
                  } catch (SingletonMap.NullSingletonException var18) {
                     throw new IOException("Could not get child zip entry slice " + childZipEntry + " : " + var18);
                  }

                  LogNode zipSliceLog = log == null ? null : log.log("Getting zipfile slice " + childZipEntrySlice + " for nested jar " + childZipEntry.entryName);

                  LogicalZipFile childLogicalZipFile;
                  try {
                     childLogicalZipFile = NestedJarHandler.this.zipFileSliceToLogicalZipFileMap.get(childZipEntrySlice, zipSliceLog);
                  } catch (SingletonMap.NullSingletonException var17) {
                     throw new IOException("Could not get child logical zipfile " + childZipEntrySlice + " : " + var17);
                  }

                  return new SimpleEntry(childLogicalZipFile, "");
               }
            }
         }
      }
   };
   public SingletonMap<ModuleRef, Recycler<ModuleReaderProxy, IOException>, IOException> moduleRefToModuleReaderProxyRecyclerMap = new SingletonMap<ModuleRef, Recycler<ModuleReaderProxy, IOException>, IOException>() {
      public Recycler<ModuleReaderProxy, IOException> newInstance(final ModuleRef moduleRef, LogNode ignored) {
         return new Recycler<ModuleReaderProxy, IOException>() {
            public ModuleReaderProxy newInstance() throws IOException {
               if (NestedJarHandler.this.closed.get()) {
                  throw ClassGraphException.newClassGraphException(NestedJarHandler.class.getSimpleName() + " already closed");
               } else {
                  return moduleRef.open();
               }
            }
         };
      }
   };
   Recycler<RecyclableInflater, RuntimeException> inflaterRecycler = new Recycler<RecyclableInflater, RuntimeException>() {
      public RecyclableInflater newInstance() throws RuntimeException {
         if (NestedJarHandler.this.closed.get()) {
            throw ClassGraphException.newClassGraphException(NestedJarHandler.class.getSimpleName() + " already closed");
         } else {
            return new RecyclableInflater();
         }
      }
   };
   private ConcurrentLinkedDeque<File> tempFiles = new ConcurrentLinkedDeque();
   public static final String TEMP_FILENAME_LEAF_SEPARATOR = "---";
   private static final int INFLATE_TO_DISK_THRESHOLD = 33554432;
   private final AtomicBoolean closed = new AtomicBoolean(false);
   public InterruptionChecker interruptionChecker;

   public NestedJarHandler(ScanSpec scanSpec, InterruptionChecker interruptionChecker) {
      this.scanSpec = scanSpec;
      this.interruptionChecker = interruptionChecker;
   }

   private PhysicalZipFile canonicalFileToPhysicalZipFile(File canonicalFile, LogNode log) throws IOException, InterruptedException {
      try {
         return this.canonicalFileToPhysicalZipFileMap.get(canonicalFile, log);
      } catch (SingletonMap.NullSingletonException var4) {
         throw new IOException("Could not get physical zipfile " + canonicalFile + " : " + var4);
      }
   }

   private static String leafname(String path) {
      return path.substring(path.lastIndexOf(47) + 1);
   }

   private String sanitizeFilename(String filename) {
      return filename.replace('/', '_').replace('\\', '_').replace(':', '_').replace('?', '_').replace('&', '_').replace('=', '_').replace(' ', '_');
   }

   private File makeTempFile(String filePath, boolean onlyUseLeafname) throws IOException {
      File tempFile = File.createTempFile("ClassGraph--", "---" + this.sanitizeFilename(onlyUseLeafname ? leafname(filePath) : filePath));
      tempFile.deleteOnExit();
      this.tempFiles.add(tempFile);
      return tempFile;
   }

   private PhysicalZipFile downloadJarFromURL(String jarURL, LogNode log) throws IOException, InterruptedException {
      LogNode subLog = log == null ? null : log.log(jarURL, "Downloading jar from URL " + jarURL);

      URL url;
      try {
         url = new URL(jarURL);
      } catch (MalformedURLException var16) {
         throw new IOException("Malformed URL: " + jarURL);
      }

      PhysicalZipFile var21;
      try {
         InputStream inputStream = url.openStream();

         try {
            PhysicalZipFile physicalZipFile = null;

            try {
               File tempFile = this.makeTempFile(jarURL, true);
               Files.copy(inputStream, tempFile.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
               if (subLog != null) {
                  subLog.log("Downloaded jar to temporary file " + tempFile);
               }

               physicalZipFile = this.canonicalFileToPhysicalZipFile(tempFile, log);
               this.additionalAllocatedPhysicalZipFiles.add(physicalZipFile);
            } catch (UnsupportedOperationException | IOException | SecurityException var17) {
               if (log != null) {
                  log.log("Could not download jar to temp file (" + var17 + "), downloading to ByteBuffer instead");
               }

               ByteBuffer byteBuffer = FileUtils.readAllBytesAsByteBuffer(inputStream, -1L);
               physicalZipFile = new PhysicalZipFile(byteBuffer, (File)null, jarURL, this);
            }

            if (physicalZipFile == null) {
               throw ClassGraphException.newClassGraphException("physicalZipFile should not be null");
            }

            var21 = physicalZipFile;
         } catch (Throwable var18) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var15) {
                  var18.addSuppressed(var15);
               }
            }

            throw var18;
         }

         if (inputStream != null) {
            inputStream.close();
         }
      } finally {
         if (subLog != null) {
            subLog.addElapsedTime();
            subLog.log("***** Note that it is time-consuming to scan jars at http(s) addresses, they must be downloaded for every scan, and the same jars must also be separately downloaded by the ClassLoader *****");
         }

      }

      return var21;
   }

   public void close(LogNode log) {
      if (!this.closed.getAndSet(true)) {
         if (this.inflaterRecycler != null) {
            this.inflaterRecycler.forceClose();
            this.inflaterRecycler = null;
         }

         if (this.moduleRefToModuleReaderProxyRecyclerMap != null) {
            try {
               for(Recycler<ModuleReaderProxy, IOException> recycler : this.moduleRefToModuleReaderProxyRecyclerMap.values()) {
                  recycler.forceClose();
               }
            } catch (InterruptedException var7) {
               this.interruptionChecker.interrupt();
            }

            this.moduleRefToModuleReaderProxyRecyclerMap.clear();
            this.moduleRefToModuleReaderProxyRecyclerMap = null;
         }

         if (this.zipFileSliceToLogicalZipFileMap != null) {
            this.zipFileSliceToLogicalZipFileMap.clear();
            this.zipFileSliceToLogicalZipFileMap = null;
         }

         if (this.nestedPathToLogicalZipFileAndPackageRootMap != null) {
            this.nestedPathToLogicalZipFileAndPackageRootMap.clear();
            this.nestedPathToLogicalZipFileAndPackageRootMap = null;
         }

         LogicalZipFile logicalZipFile;
         while((logicalZipFile = (LogicalZipFile)this.allocatedLogicalZipFiles.poll()) != null) {
            logicalZipFile.close();
         }

         if (this.canonicalFileToPhysicalZipFileMap != null) {
            try {
               for(PhysicalZipFile physicalZipFile : this.canonicalFileToPhysicalZipFileMap.values()) {
                  physicalZipFile.close();
               }
            } catch (InterruptedException var6) {
               this.interruptionChecker.interrupt();
            }

            this.canonicalFileToPhysicalZipFileMap.clear();
            this.canonicalFileToPhysicalZipFileMap = null;
         }

         if (this.additionalAllocatedPhysicalZipFiles != null) {
            while((physicalZipFile = (PhysicalZipFile)this.additionalAllocatedPhysicalZipFiles.poll()) != null) {
               physicalZipFile.close();
            }

            this.additionalAllocatedPhysicalZipFiles.clear();
            this.additionalAllocatedPhysicalZipFiles = null;
         }

         if (this.fastZipEntryToZipFileSliceMap != null) {
            this.fastZipEntryToZipFileSliceMap.clear();
            this.fastZipEntryToZipFileSliceMap = null;
         }

         if (this.tempFiles != null) {
            LogNode rmLog = !this.tempFiles.isEmpty() && log != null ? log.log("Removing temporary files") : null;

            while(!this.tempFiles.isEmpty()) {
               File tempFile = (File)this.tempFiles.removeLast();

               try {
                  Files.delete(tempFile.toPath());
               } catch (SecurityException | IOException var5) {
                  if (rmLog != null) {
                     rmLog.log("Removing temporary file failed: " + var5);
                  }
               }
            }

            this.tempFiles.clear();
            this.tempFiles = null;
         }
      }

   }
}
