package org.spongepowered.asm.launch.platform.container;

import java.nio.file.Path;
import java.util.List;
import java.util.Map.Entry;
import org.spongepowered.asm.service.MixinService;

public class ContainerHandleModLauncher extends ContainerHandleVirtual {
   public ContainerHandleModLauncher(String name) {
      super(name);
   }

   public void addResource(String name, Path path) {
      this.add(new ContainerHandleModLauncher.Resource(name, path));
   }

   public void addResource(Entry<String, Path> entry) {
      this.add(new ContainerHandleModLauncher.Resource((String)entry.getKey(), (Path)entry.getValue()));
   }

   public void addResource(Object resource) {
      if (resource instanceof Entry) {
         this.addResource((Entry)resource);
      } else {
         MixinService.getService().getLogger("mixin").error("Unrecognised resource type {} passed to {}", resource.getClass(), this);
      }

   }

   public void addResources(List<?> resources) {
      for(Object resource : resources) {
         this.addResource(resource);
      }

   }

   public String toString() {
      return String.format("ModLauncher Root Container(%s:%x)", this.getName(), this.hashCode());
   }

   class Resource extends ContainerHandleURI {
      private String name;
      private Path path;

      public Resource(String name, Path path) {
         super(path.toUri());
         this.name = name;
         this.path = path;
      }

      public String getName() {
         return this.name;
      }

      public Path getPath() {
         return this.path;
      }

      public String toString() {
         return String.format("ContainerHandleModLauncher.Resource(%s:%s)", this.name, this.path);
      }
   }
}
