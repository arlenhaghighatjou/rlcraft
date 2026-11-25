package io.github.classgraph;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.Iterator;
import nonapi.io.github.classgraph.utils.JarUtils;

class ClassGraphClassLoader extends ClassLoader {
   private final ScanResult scanResult;

   ClassGraphClassLoader(ScanResult scanResult) {
      super((ClassLoader)null);
      this.scanResult = scanResult;
      registerAsParallelCapable();
   }

   protected Class<?> findClass(String className) throws ClassNotFoundException, LinkageError, SecurityException {
      Class<?> loadedClass = this.findLoadedClass(className);
      if (loadedClass != null) {
         return loadedClass;
      } else {
         ClassInfo classInfo = this.scanResult.getClassInfo(className);
         boolean triedClassInfoLoader = false;
         ClassLoader[] classLoaderOrder = this.scanResult.getClassLoaderOrderRespectingParentDelegation();
         if (classLoaderOrder != null) {
            for(ClassLoader envClassLoader : classLoaderOrder) {
               try {
                  return Class.forName(className, this.scanResult.scanSpec.initializeLoadedClasses, envClassLoader);
               } catch (NoClassDefFoundError | ClassNotFoundException var21) {
                  if (classInfo != null && envClassLoader == classInfo.classLoader) {
                     triedClassInfoLoader = true;
                  }
               }
            }
         }

         try {
            return Class.forName(className, this.scanResult.scanSpec.initializeLoadedClasses, (ClassLoader)null);
         } catch (NoClassDefFoundError | ClassNotFoundException var20) {
            if (!triedClassInfoLoader && classInfo != null && classInfo.classLoader != null) {
               try {
                  return Class.forName(className, this.scanResult.scanSpec.initializeLoadedClasses, classInfo.classLoader);
               } catch (NoClassDefFoundError | ClassNotFoundException var19) {
                  ;
               }
            }

            if (classInfo != null && classInfo.classpathElement instanceof ClasspathElementModule && !classInfo.isPublic()) {
               throw new ClassNotFoundException("Classfile for class " + className + " was found in a module, but the context and system classloaders could not load the class, probably because the class is not public.");
            } else {
               ResourceList classfileResources = this.scanResult.getResourcesWithPath(JarUtils.classNameToClassfilePath(className));
               if (classfileResources != null) {
                  Iterator var23 = classfileResources.iterator();
                  if (var23.hasNext()) {
                     Resource resource = (Resource)var23.next();

                     Class var10;
                     try {
                        ByteBuffer resourceByteBuffer = resource.read();
                        var10 = this.defineClass(className, resourceByteBuffer, (ProtectionDomain)null);
                     } catch (IOException var17) {
                        throw new ClassNotFoundException("Could not load classfile for class " + className + " : " + var17);
                     } finally {
                        resource.close();
                     }

                     return var10;
                  }
               }

               throw new ClassNotFoundException("Could not load classfile for class " + className);
            }
         }
      }
   }

   public URL getResource(String path) {
      ResourceList resourceList = this.scanResult.getResourcesWithPath(path);
      return resourceList != null && !resourceList.isEmpty() ? ((Resource)resourceList.get(0)).getURL() : super.getResource(path);
   }

   public Enumeration<URL> getResources(String path) throws IOException {
      final ResourceList resourceList = this.scanResult.getResourcesWithPath(path);
      return resourceList != null && !resourceList.isEmpty() ? new Enumeration<URL>() {
         int idx;

         public boolean hasMoreElements() {
            return this.idx < resourceList.size();
         }

         public URL nextElement() {
            return ((Resource)resourceList.get(this.idx++)).getURL();
         }
      } : super.getResources(path);
   }

   public InputStream getResourceAsStream(String path) {
      ResourceList resourceList = this.scanResult.getResourcesWithPath(path);
      if (resourceList != null && !resourceList.isEmpty()) {
         try {
            return ((Resource)resourceList.get(0)).open();
         } catch (IOException var4) {
            return null;
         }
      } else {
         return super.getResourceAsStream(path);
      }
   }
}
