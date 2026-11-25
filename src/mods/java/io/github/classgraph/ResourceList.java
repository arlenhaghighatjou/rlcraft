package io.github.classgraph;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public class ResourceList extends PotentiallyUnmodifiableList<Resource> implements AutoCloseable {
   static final long serialVersionUID = 1L;
   static final ResourceList EMPTY_LIST = new ResourceList();
   private static final ResourceList.ResourceFilter CLASSFILE_FILTER = new ResourceList.ResourceFilter() {
      public boolean accept(Resource resource) {
         String path = resource.getPath();
         if (path.endsWith(".class") && path.length() >= 7) {
            char c = path.charAt(path.length() - 7);
            return c != '/' && c != '.';
         } else {
            return false;
         }
      }
   };

   public static ResourceList emptyList() {
      return EMPTY_LIST;
   }

   public ResourceList() {
   }

   public ResourceList(int sizeHint) {
      super(sizeHint);
   }

   public ResourceList(Collection<Resource> resourceCollection) {
      super(resourceCollection);
   }

   public List<String> getPaths() {
      List<String> resourcePaths = new ArrayList(this.size());

      for(Resource resource : this) {
         resourcePaths.add(resource.getPath());
      }

      return resourcePaths;
   }

   public List<String> getPathsRelativeToClasspathElement() {
      List<String> resourcePaths = new ArrayList(this.size());

      for(Resource resource : this) {
         resourcePaths.add(resource.getPath());
      }

      return resourcePaths;
   }

   public List<URL> getURLs() {
      List<URL> resourceURLs = new ArrayList(this.size());

      for(Resource resource : this) {
         resourceURLs.add(resource.getURL());
      }

      return resourceURLs;
   }

   public List<URI> getURIs() {
      List<URI> resourceURLs = new ArrayList(this.size());

      for(Resource resource : this) {
         resourceURLs.add(resource.getURI());
      }

      return resourceURLs;
   }

   public ResourceList classFilesOnly() {
      return this.filter(CLASSFILE_FILTER);
   }

   public ResourceList nonClassFilesOnly() {
      return this.filter(new ResourceList.ResourceFilter() {
         public boolean accept(Resource resource) {
            return !ResourceList.CLASSFILE_FILTER.accept(resource);
         }
      });
   }

   public Map<String, ResourceList> asMap() {
      Map<String, ResourceList> pathToResourceList = new HashMap();

      for(Resource resource : this) {
         String path = resource.getPath();
         ResourceList resourceList = (ResourceList)pathToResourceList.get(path);
         if (resourceList == null) {
            resourceList = new ResourceList(1);
            pathToResourceList.put(path, resourceList);
         }

         resourceList.add(resource);
      }

      return pathToResourceList;
   }

   public List<Entry<String, ResourceList>> findDuplicatePaths() {
      List<Entry<String, ResourceList>> duplicatePaths = new ArrayList();

      for(Entry<String, ResourceList> pathAndResourceList : this.asMap().entrySet()) {
         if (((ResourceList)pathAndResourceList.getValue()).size() > 1) {
            duplicatePaths.add(new SimpleEntry(pathAndResourceList.getKey(), pathAndResourceList.getValue()));
         }
      }

      CollectionUtils.sortIfNotEmpty(duplicatePaths, new Comparator<Entry<String, ResourceList>>() {
         public int compare(Entry<String, ResourceList> o1, Entry<String, ResourceList> o2) {
            return ((String)o1.getKey()).compareTo((String)o2.getKey());
         }
      });
      return (int)duplicatePaths;
   }

   public ResourceList filter(ResourceList.ResourceFilter filter) {
      ResourceList resourcesFiltered = new ResourceList();

      for(Resource resource : this) {
         if (filter.accept(resource)) {
            resourcesFiltered.add(resource);
         }
      }

      return resourcesFiltered;
   }

   public void forEachByteArray(ResourceList.ByteArrayConsumer byteArrayConsumer, boolean ignoreIOExceptions) {
      for(Resource resource : this) {
         try {
            byte[] resourceContent = resource.load();
            byteArrayConsumer.accept(resource, resourceContent);
         } catch (IOException var9) {
            if (!ignoreIOExceptions) {
               throw new IllegalArgumentException("Could not load resource " + resource, var9);
            }
         } finally {
            resource.close();
         }
      }

   }

   public void forEachByteArray(ResourceList.ByteArrayConsumer byteArrayConsumer) {
      this.forEachByteArray(byteArrayConsumer, false);
   }

   public void forEachInputStream(ResourceList.InputStreamConsumer inputStreamConsumer, boolean ignoreIOExceptions) {
      for(Resource resource : this) {
         try {
            inputStreamConsumer.accept(resource, resource.open());
         } catch (IOException var9) {
            if (!ignoreIOExceptions) {
               throw new IllegalArgumentException("Could not load resource " + resource, var9);
            }
         } finally {
            resource.close();
         }
      }

   }

   public void forEachInputStream(ResourceList.InputStreamConsumer inputStreamConsumer) {
      this.forEachInputStream(inputStreamConsumer, false);
   }

   public void forEachByteBuffer(ResourceList.ByteBufferConsumer byteBufferConsumer, boolean ignoreIOExceptions) {
      for(Resource resource : this) {
         try {
            ByteBuffer byteBuffer = resource.read();
            byteBufferConsumer.accept(resource, byteBuffer);
         } catch (IOException var9) {
            if (!ignoreIOExceptions) {
               throw new IllegalArgumentException("Could not load resource " + resource, var9);
            }
         } finally {
            resource.close();
         }
      }

   }

   public void forEachByteBuffer(ResourceList.ByteBufferConsumer byteBufferConsumer) {
      this.forEachByteBuffer(byteBufferConsumer, false);
   }

   public void close() {
      for(Resource resource : this) {
         resource.close();
      }

   }

   static {
      EMPTY_LIST.makeUnmodifiable();
   }

   @FunctionalInterface
   public interface ByteArrayConsumer {
      void accept(Resource var1, byte[] var2);
   }

   @FunctionalInterface
   public interface ByteBufferConsumer {
      void accept(Resource var1, ByteBuffer var2);
   }

   @FunctionalInterface
   public interface InputStreamConsumer {
      void accept(Resource var1, InputStream var2);
   }

   @FunctionalInterface
   public interface ResourceFilter {
      boolean accept(Resource var1);
   }
}
