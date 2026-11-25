package io.github.classgraph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.recycler.RecycleOnClose;
import nonapi.io.github.classgraph.recycler.Recycler;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.InputStreamOrByteBufferAdapter;
import nonapi.io.github.classgraph.utils.LogNode;

class ClasspathElementModule extends ClasspathElement {
   final ModuleRef moduleRef;
   private final NestedJarHandler nestedJarHandler;
   private Recycler<ModuleReaderProxy, IOException> moduleReaderProxyRecycler;
   private final Set<String> allResourcePaths = new HashSet();

   ClasspathElementModule(ModuleRef moduleRef, ClassLoader classLoader, NestedJarHandler nestedJarHandler, ScanSpec scanSpec) {
      super(classLoader, scanSpec);
      this.moduleRef = moduleRef;
      this.nestedJarHandler = nestedJarHandler;
   }

   void open(WorkQueue<Scanner.ClasspathEntryWorkUnit> workQueueIgnored, int classpathElementIdx, LogNode log) throws InterruptedException {
      if (!this.scanSpec.scanModules) {
         if (log != null) {
            this.log(classpathElementIdx, "Skipping module, since module scanning is disabled: " + this.getModuleName(), log);
         }

         this.skipClasspathElement = true;
      } else {
         try {
            this.moduleReaderProxyRecycler = (Recycler)this.nestedJarHandler.moduleRefToModuleReaderProxyRecyclerMap.get(this.moduleRef, (LogNode)null);
         } catch (SingletonMap.NullSingletonException | IOException var5) {
            if (log != null) {
               this.log(classpathElementIdx, "Skipping invalid module " + this.getModuleName() + " : " + var5, log);
            }

            this.skipClasspathElement = true;
         }
      }
   }

   private Resource newResource(final String resourcePath) {
      return new Resource(this, -1L) {
         private ModuleReaderProxy moduleReaderProxy;

         public String getPath() {
            return resourcePath;
         }

         public String getPathRelativeToClasspathElement() {
            return resourcePath;
         }

         public synchronized ByteBuffer read() throws IOException {
            if (ClasspathElementModule.this.skipClasspathElement) {
               throw new IOException("Module could not be opened");
            } else {
               this.markAsOpen();

               try {
                  this.moduleReaderProxy = ClasspathElementModule.this.moduleReaderProxyRecycler.acquire();
                  this.byteBuffer = this.moduleReaderProxy.read(resourcePath);
                  this.length = (long)this.byteBuffer.remaining();
                  return this.byteBuffer;
               } catch (OutOfMemoryError | SecurityException var2) {
                  this.close();
                  throw new IOException("Could not open " + this, var2);
               }
            }
         }

         synchronized InputStreamOrByteBufferAdapter openOrRead() throws IOException {
            return new InputStreamOrByteBufferAdapter(this.open());
         }

         public synchronized InputStream open() throws IOException {
            if (ClasspathElementModule.this.skipClasspathElement) {
               throw new IOException("Module could not be opened");
            } else {
               this.markAsOpen();

               try {
                  this.moduleReaderProxy = ClasspathElementModule.this.moduleReaderProxyRecycler.acquire();
                  this.inputStream = new Resource.InputStreamResourceCloser(this, this.moduleReaderProxy.open(resourcePath));
                  this.length = -1L;
                  return this.inputStream;
               } catch (SecurityException var2) {
                  this.close();
                  throw new IOException("Could not open " + this, var2);
               }
            }
         }

         public synchronized byte[] load() throws IOException {
            byte[] var2;
            try {
               this.read();
               byte[] byteArray = this.byteBufferToByteArray();
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
               if (this.moduleReaderProxy != null) {
                  this.moduleReaderProxy.release(this.byteBuffer);
               }

               this.byteBuffer = null;
            }

            if (this.moduleReaderProxy != null) {
               ClasspathElementModule.this.moduleReaderProxyRecycler.recycle(this.moduleReaderProxy);
               this.moduleReaderProxy = null;
            }

            this.markAsClosed();
         }
      };
   }

   Resource getResource(String relativePath) {
      return this.allResourcePaths.contains(relativePath) ? this.newResource(relativePath) : null;
   }

   void scanPaths(int classpathElementIdx, LogNode log) {
      if (!this.skipClasspathElement) {
         if (this.scanned.getAndSet(true)) {
            throw new IllegalArgumentException("Already scanned classpath element " + this.toString());
         } else {
            LogNode subLog = log == null ? null : this.log(classpathElementIdx, "Scanning module " + this.moduleRef.getName(), log);

            try {
               label158: {
                  RecycleOnClose<ModuleReaderProxy, IOException> moduleReaderProxyRecycleOnClose = this.moduleReaderProxyRecycler.acquireRecycleOnClose();

                  label148: {
                     label159: {
                        try {
                           label184: {
                              List<String> resourceRelativePaths;
                              try {
                                 resourceRelativePaths = ((ModuleReaderProxy)moduleReaderProxyRecycleOnClose.get()).list();
                              } catch (SecurityException var15) {
                                 if (subLog != null) {
                                    subLog.log("Could not get resource list for module " + this.moduleRef.getName(), var15);
                                 }
                                 break label184;
                              }

                              CollectionUtils.sortIfNotEmpty(resourceRelativePaths);
                              String prevParentRelativePath = null;
                              ScanSpec.ScanSpecPathMatch prevParentMatchStatus = null;

                              for(String relativePath : resourceRelativePaths) {
                                 if (!relativePath.endsWith("/")) {
                                    if (relativePath.startsWith("META-INF/versions/")) {
                                       if (subLog != null) {
                                          subLog.log("Found unexpected nested versioned entry in module -- skipping: " + relativePath);
                                       }
                                    } else {
                                       this.checkResourcePathWhiteBlackList(relativePath, log);
                                       if (this.skipClasspathElement) {
                                          break label148;
                                       }

                                       int lastSlashIdx = relativePath.lastIndexOf(47);
                                       String parentRelativePath = lastSlashIdx < 0 ? "/" : relativePath.substring(0, lastSlashIdx + 1);
                                       boolean parentRelativePathChanged = !parentRelativePath.equals(prevParentRelativePath);
                                       ScanSpec.ScanSpecPathMatch parentMatchStatus = prevParentRelativePath != null && !parentRelativePathChanged ? prevParentMatchStatus : this.scanSpec.dirWhitelistMatchStatus(parentRelativePath);
                                       prevParentRelativePath = parentRelativePath;
                                       prevParentMatchStatus = parentMatchStatus;
                                       if (parentMatchStatus == ScanSpec.ScanSpecPathMatch.HAS_BLACKLISTED_PATH_PREFIX) {
                                          if (subLog != null) {
                                             subLog.log("Skipping blacklisted path: " + relativePath);
                                          }
                                       } else if (this.allResourcePaths.add(relativePath)) {
                                          if (parentMatchStatus != ScanSpec.ScanSpecPathMatch.HAS_WHITELISTED_PATH_PREFIX && parentMatchStatus != ScanSpec.ScanSpecPathMatch.AT_WHITELISTED_PATH && (parentMatchStatus != ScanSpec.ScanSpecPathMatch.AT_WHITELISTED_CLASS_PACKAGE || !this.scanSpec.classfileIsSpecificallyWhitelisted(relativePath))) {
                                             if (this.scanSpec.enableClassInfo && relativePath.equals("module-info.class")) {
                                                this.addWhitelistedResource(this.newResource(relativePath), parentMatchStatus, true, subLog);
                                             }
                                          } else {
                                             this.addWhitelistedResource(this.newResource(relativePath), parentMatchStatus, false, subLog);
                                          }
                                       }
                                    }
                                 }
                              }

                              File moduleFile = this.moduleRef.getLocationFile();
                              if (moduleFile != null && moduleFile.exists()) {
                                 this.fileToLastModified.put(moduleFile, Long.valueOf(moduleFile.lastModified()));
                              }
                              break label159;
                           }
                        } catch (Throwable var16) {
                           if (moduleReaderProxyRecycleOnClose != null) {
                              try {
                                 moduleReaderProxyRecycleOnClose.close();
                              } catch (Throwable var14) {
                                 var16.addSuppressed(var14);
                              }
                           }

                           throw var16;
                        }

                        if (moduleReaderProxyRecycleOnClose != null) {
                           moduleReaderProxyRecycleOnClose.close();
                        }

                        return;
                     }

                     if (moduleReaderProxyRecycleOnClose != null) {
                        moduleReaderProxyRecycleOnClose.close();
                     }
                     break label158;
                  }

                  if (moduleReaderProxyRecycleOnClose != null) {
                     moduleReaderProxyRecycleOnClose.close();
                  }

                  return;
               }
            } catch (IOException var17) {
               if (subLog != null) {
                  subLog.log("Exception opening module " + this.moduleRef.getName(), var17);
               }

               this.skipClasspathElement = true;
            }

            this.finishScanPaths(subLog);
         }
      }
   }

   ModuleRef getModuleRef() {
      return this.moduleRef;
   }

   public String getModuleName() {
      String moduleName = this.moduleRef.getName();
      if (moduleName == null || moduleName.isEmpty()) {
         moduleName = this.moduleNameFromModuleDescriptor;
      }

      return moduleName != null && !moduleName.isEmpty() ? moduleName : null;
   }

   private String getModuleNameOrEmpty() {
      String moduleName = this.getModuleName();
      return moduleName == null ? "" : moduleName;
   }

   URI getURI() {
      URI uri = this.moduleRef.getLocation();
      if (uri == null) {
         throw new IllegalArgumentException("Module " + this.getModuleName() + " has a null location");
      } else {
         return uri;
      }
   }

   File getFile() {
      try {
         URI uri = this.moduleRef.getLocation();
         if (uri != null && !uri.getScheme().equals("jrt")) {
            File file = new File(uri);
            if (file.exists()) {
               return file;
            }
         }
      } catch (Exception var3) {
         ;
      }

      return null;
   }

   public String toString() {
      return this.moduleRef.toString();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ClasspathElementModule)) {
         return false;
      } else {
         ClasspathElementModule other = (ClasspathElementModule)obj;
         return this.getModuleNameOrEmpty().equals(other.getModuleNameOrEmpty());
      }
   }

   public int hashCode() {
      return this.getModuleNameOrEmpty().hashCode();
   }
}
