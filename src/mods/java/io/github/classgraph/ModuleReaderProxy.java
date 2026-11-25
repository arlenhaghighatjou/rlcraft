package io.github.classgraph;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import nonapi.io.github.classgraph.utils.ReflectionUtils;

public class ModuleReaderProxy implements Closeable {
   private final AutoCloseable moduleReader;
   private static Class<?> collectorClass = ReflectionUtils.classForNameOrNull("java.util.stream.Collector");
   private static Object collectorsToList;

   ModuleReaderProxy(ModuleRef moduleRef) throws IOException {
      try {
         this.moduleReader = (AutoCloseable)ReflectionUtils.invokeMethod(moduleRef.getReference(), "open", true);
         if (this.moduleReader == null) {
            throw new IllegalArgumentException("moduleReference.open() should not return null");
         }
      } catch (SecurityException var3) {
         throw new IOException("Could not open module " + moduleRef.getName(), var3);
      }
   }

   public void close() {
      try {
         this.moduleReader.close();
      } catch (Exception var2) {
         ;
      }

   }

   public List<String> list() throws SecurityException {
      if (collectorsToList == null) {
         throw new IllegalArgumentException("Could not call Collectors.toList()");
      } else {
         Object resourcesStream = ReflectionUtils.invokeMethod(this.moduleReader, "list", true);
         if (resourcesStream == null) {
            throw new IllegalArgumentException("Could not call moduleReader.list()");
         } else {
            Object resourcesList = ReflectionUtils.invokeMethod(resourcesStream, "collect", collectorClass, collectorsToList, true);
            if (resourcesList == null) {
               throw new IllegalArgumentException("Could not call moduleReader.list().collect(Collectors.toList())");
            } else {
               List<String> resourcesListTyped = (List)resourcesList;
               return resourcesListTyped;
            }
         }
      }
   }

   private Object openOrRead(String path, boolean open) throws SecurityException {
      String methodName = open ? "open" : "read";
      Object optionalInputStream = ReflectionUtils.invokeMethod(this.moduleReader, methodName, String.class, path, true);
      if (optionalInputStream == null) {
         throw new IllegalArgumentException("Got null result from moduleReader." + methodName + "(name)");
      } else {
         Object inputStream = ReflectionUtils.invokeMethod(optionalInputStream, "get", true);
         if (inputStream == null) {
            throw new IllegalArgumentException("Got null result from moduleReader." + methodName + "(name).get()");
         } else {
            return inputStream;
         }
      }
   }

   public InputStream open(String path) throws SecurityException {
      return (InputStream)this.openOrRead(path, true);
   }

   public ByteBuffer read(String path) throws SecurityException, OutOfMemoryError {
      return (ByteBuffer)this.openOrRead(path, false);
   }

   public void release(ByteBuffer byteBuffer) {
      ReflectionUtils.invokeMethod(this.moduleReader, "release", ByteBuffer.class, byteBuffer, true);
   }

   static {
      Class<?> collectorsClass = ReflectionUtils.classForNameOrNull("java.util.stream.Collectors");
      if (collectorsClass != null) {
         collectorsToList = ReflectionUtils.invokeStaticMethod(collectorsClass, "toList", true);
      }

   }
}
