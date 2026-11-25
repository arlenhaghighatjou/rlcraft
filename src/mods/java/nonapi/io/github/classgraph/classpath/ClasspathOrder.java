package nonapi.io.github.classgraph.classpath;

import io.github.classgraph.ClassGraph;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class ClasspathOrder {
   private final ScanSpec scanSpec;
   private final Set<String> classpathEntryUniqueResolvedPaths = new HashSet();
   private final List<Entry<String, ClassLoader>> order = new ArrayList();

   ClasspathOrder(ScanSpec scanSpec) {
      this.scanSpec = scanSpec;
   }

   public List<Entry<String, ClassLoader>> getOrder() {
      return this.order;
   }

   public Set<String> getClasspathEntryUniqueResolvedPaths() {
      return this.classpathEntryUniqueResolvedPaths;
   }

   private boolean filter(String classpathElementPath) {
      if (this.scanSpec.classpathElementFilters != null) {
         for(ClassGraph.ClasspathElementFilter filter : this.scanSpec.classpathElementFilters) {
            if (!filter.includeClasspathElement(classpathElementPath)) {
               return false;
            }
         }
      }

      return true;
   }

   boolean addSystemClasspathEntry(String pathEntry, ClassLoader classLoader) {
      if (this.classpathEntryUniqueResolvedPaths.add(pathEntry)) {
         this.order.add(new SimpleEntry(pathEntry, classLoader));
         return true;
      } else {
         return false;
      }
   }

   private boolean addClasspathEntry(String pathEntry, ClassLoader classLoader, ScanSpec scanSpec) {
      if (scanSpec.overrideClasspath != null || !SystemJarFinder.getJreLibOrExtJars().contains(pathEntry) && !pathEntry.equals(SystemJarFinder.getJreRtJarPath())) {
         if (this.classpathEntryUniqueResolvedPaths.add(pathEntry)) {
            this.order.add(new SimpleEntry(pathEntry, classLoader));
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean addClasspathEntry(String pathElement, ClassLoader classLoader, ScanSpec scanSpec, LogNode log) {
      if (pathElement != null && !pathElement.isEmpty()) {
         if (pathElement.endsWith("*")) {
            if (pathElement.length() == 1 || pathElement.length() > 2 && pathElement.charAt(pathElement.length() - 1) == '*' && (pathElement.charAt(pathElement.length() - 2) == File.separatorChar || File.separatorChar != '/' && pathElement.charAt(pathElement.length() - 2) == '/')) {
               String baseDirPath = pathElement.length() == 1 ? "" : pathElement.substring(0, pathElement.length() - 2);
               String baseDirPathResolved = FastPathResolver.resolve(FileUtils.CURR_DIR_PATH, baseDirPath);
               if (!this.filter(baseDirPath) || !baseDirPathResolved.equals(baseDirPath) && !this.filter(baseDirPathResolved)) {
                  if (log != null) {
                     log.log("Classpath element did not match filter criterion, skipping: " + pathElement);
                  }

                  return false;
               } else {
                  File baseDir = new File(baseDirPathResolved);
                  if (!baseDir.exists()) {
                     if (log != null) {
                        log.log("Directory does not exist for wildcard classpath element: " + pathElement);
                     }

                     return false;
                  } else if (!FileUtils.canRead(baseDir)) {
                     if (log != null) {
                        log.log("Cannot read directory for wildcard classpath element: " + pathElement);
                     }

                     return false;
                  } else if (!baseDir.isDirectory()) {
                     if (log != null) {
                        log.log("Wildcard is appended to something other than a directory: " + pathElement);
                     }

                     return false;
                  } else {
                     LogNode dirLog = log == null ? null : log.log("Adding classpath elements from wildcarded directory: " + pathElement);
                     File[] baseDirFiles = baseDir.listFiles();
                     if (baseDirFiles != null) {
                        for(File fileInDir : baseDirFiles) {
                           String name = fileInDir.getName();
                           if (!name.equals(".") && !name.equals("..")) {
                              String fileInDirPath = fileInDir.getPath();
                              String fileInDirPathResolved = FastPathResolver.resolve(FileUtils.CURR_DIR_PATH, fileInDirPath);
                              if (this.addClasspathEntry(fileInDirPathResolved, classLoader, scanSpec)) {
                                 if (dirLog != null) {
                                    dirLog.log("Found classpath element: " + fileInDirPath + (fileInDirPath.equals(fileInDirPathResolved) ? "" : " -> " + fileInDirPathResolved));
                                 }
                              } else if (dirLog != null) {
                                 dirLog.log("Ignoring duplicate classpath element: " + fileInDirPath + (fileInDirPath.equals(fileInDirPathResolved) ? "" : " -> " + fileInDirPathResolved));
                              }
                           }
                        }

                        return true;
                     } else {
                        return false;
                     }
                  }
               }
            } else {
               if (log != null) {
                  log.log("Wildcard classpath elements can only end with a leaf of \"*\", can't have a partial name and then a wildcard: " + pathElement);
               }

               return false;
            }
         } else {
            String pathElementResolved = FastPathResolver.resolve(FileUtils.CURR_DIR_PATH, pathElement);
            if (!this.filter(pathElement) || !pathElementResolved.equals(pathElement) && !this.filter(pathElementResolved)) {
               if (log != null) {
                  log.log("Classpath element did not match filter criterion, skipping: " + pathElement + (pathElement.equals(pathElementResolved) ? "" : " -> " + pathElementResolved));
               }

               return false;
            } else if (this.addClasspathEntry(pathElementResolved, classLoader, scanSpec)) {
               if (log != null) {
                  log.log("Found classpath element: " + pathElement + (pathElement.equals(pathElementResolved) ? "" : " -> " + pathElementResolved));
               }

               return true;
            } else {
               if (log != null) {
                  log.log("Ignoring duplicate classpath element: " + pathElement + (pathElement.equals(pathElementResolved) ? "" : " -> " + pathElementResolved));
               }

               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean addClasspathEntries(String pathStr, ClassLoader classLoader, ScanSpec scanSpec, LogNode log) {
      if (pathStr != null && !pathStr.isEmpty()) {
         String[] parts = JarUtils.smartPathSplit(pathStr);
         if (parts.length == 0) {
            return false;
         } else {
            for(String pathElement : parts) {
               this.addClasspathEntry(pathElement, classLoader, scanSpec, log);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean addClasspathEntryObject(Object pathObject, ClassLoader classLoader, ScanSpec scanSpec, LogNode log) {
      boolean valid = false;
      if (pathObject != null) {
         if (pathObject instanceof String) {
            valid |= this.addClasspathEntries((String)pathObject, classLoader, scanSpec, log);
         } else if (pathObject instanceof Iterable) {
            for(Object p : (Iterable)pathObject) {
               if (p != null) {
                  valid |= this.addClasspathEntries(p.toString(), classLoader, scanSpec, log);
               }
            }
         } else {
            Class<?> valClass = pathObject.getClass();
            if (valClass.isArray()) {
               int j = 0;

               for(int n = Array.getLength(pathObject); j < n; ++j) {
                  Object elt = Array.get(pathObject, j);
                  if (elt != null) {
                     valid |= this.addClasspathEntryObject(elt, classLoader, scanSpec, log);
                  }
               }
            } else {
               valid |= this.addClasspathEntries(pathObject.toString(), classLoader, scanSpec, log);
            }
         }
      }

      return valid;
   }
}
