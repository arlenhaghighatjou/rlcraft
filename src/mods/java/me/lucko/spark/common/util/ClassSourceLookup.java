package me.lucko.spark.common.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import me.lucko.spark.common.sampler.node.StackTraceNode;
import me.lucko.spark.common.sampler.node.ThreadNode;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ClassSourceLookup {
   ClassSourceLookup NO_OP = new ClassSourceLookup() {
      @Nullable
      public String identify(Class<?> clazz) {
         return null;
      }
   };

   @Nullable
   String identify(Class<?> var1) throws Exception;

   static ClassSourceLookup.Visitor createVisitor(ClassSourceLookup lookup) {
      return (ClassSourceLookup.Visitor)(lookup == NO_OP ? ClassSourceLookup.NoOpVistitor.INSTANCE : new ClassSourceLookup.VisitorImpl(lookup));
   }

   public abstract static class ByClassLoader implements ClassSourceLookup {
      public ByClassLoader() {
      }

      @Nullable
      public abstract String identify(ClassLoader var1) throws Exception;

      @Nullable
      public final String identify(Class<?> clazz) throws Exception {
         for(ClassLoader loader = clazz.getClassLoader(); loader != null; loader = loader.getParent()) {
            String source = this.identify(loader);
            if (source != null) {
               return source;
            }
         }

         return null;
      }
   }

   public static class ByCodeSource implements ClassSourceLookup, ClassSourceLookup.ByUrl {
      public ByCodeSource() {
      }

      @Nullable
      public String identify(Class<?> clazz) throws URISyntaxException {
         ProtectionDomain protectionDomain = clazz.getProtectionDomain();
         if (protectionDomain == null) {
            return null;
         } else {
            CodeSource codeSource = protectionDomain.getCodeSource();
            if (codeSource == null) {
               return null;
            } else {
               URL url = codeSource.getLocation();
               return url == null ? null : this.identifyUrl(url);
            }
         }
      }
   }

   public static class ByFirstUrlSource extends ClassSourceLookup.ByClassLoader implements ClassSourceLookup.ByUrl {
      public ByFirstUrlSource() {
      }

      @Nullable
      public String identify(ClassLoader loader) throws IOException, URISyntaxException {
         if (loader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader)loader;
            URL[] urls = urlClassLoader.getURLs();
            return urls.length == 0 ? null : this.identifyUrl(urls[0]);
         } else {
            return null;
         }
      }
   }

   public interface ByUrl extends ClassSourceLookup {
      default String identifyUrl(URL url) throws URISyntaxException {
         return url.getProtocol().equals("file") ? this.identifyFile(Paths.get(url.toURI())) : null;
      }

      default String identifyFile(Path path) {
         return this.identifyFileName(path.getFileName().toString());
      }

      default String identifyFileName(String fileName) {
         return fileName.endsWith(".jar") ? fileName.substring(0, fileName.length() - 4) : null;
      }
   }

   public static enum NoOpVistitor implements ClassSourceLookup.Visitor {
      INSTANCE;

      private NoOpVistitor() {
      }

      public void visit(ThreadNode node) {
      }

      public boolean hasMappings() {
         return false;
      }

      public Map<String, String> getMapping() {
         return Collections.emptyMap();
      }
   }

   public interface Visitor {
      void visit(ThreadNode var1);

      boolean hasMappings();

      Map<String, String> getMapping();
   }

   public static class VisitorImpl implements ClassSourceLookup.Visitor {
      private final ClassSourceLookup lookup;
      private final ClassFinder classFinder = new ClassFinder();
      private final Map<String, String> map = new HashMap();

      VisitorImpl(ClassSourceLookup lookup) {
         this.lookup = lookup;
      }

      public void visit(ThreadNode node) {
         for(StackTraceNode child : node.getChildren()) {
            this.visitStackNode(child);
         }

      }

      public boolean hasMappings() {
         return !this.map.isEmpty();
      }

      public Map<String, String> getMapping() {
         this.map.values().removeIf(Objects::isNull);
         return this.map;
      }

      private void visitStackNode(StackTraceNode node) {
         String className = node.getClassName();
         if (!this.map.containsKey(className)) {
            try {
               Class<?> clazz = this.classFinder.findClass(className);
               Objects.requireNonNull(clazz);
               this.map.put(className, this.lookup.identify(clazz));
            } catch (Throwable var5) {
               this.map.put(className, (Object)null);
            }
         }

         for(StackTraceNode child : node.getChildren()) {
            this.visitStackNode(child);
         }

      }
   }
}
