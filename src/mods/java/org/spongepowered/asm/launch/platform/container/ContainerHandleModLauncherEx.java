package org.spongepowered.asm.launch.platform.container;

import cpw.mods.jarhandling.SecureJar;
import java.nio.file.Path;

public class ContainerHandleModLauncherEx extends ContainerHandleModLauncher {
   public ContainerHandleModLauncherEx(String name) {
      super(name);
   }

   public void addResource(Object resource) {
      if (resource instanceof SecureJar) {
         this.add(new ContainerHandleModLauncherEx.SecureJarResource((SecureJar)resource));
      } else {
         super.addResource(resource);
      }

   }

   static class SecureJarResource extends ContainerHandleURI {
      private SecureJar jar;

      public SecureJarResource(SecureJar resource) {
         super(resource.getPrimaryPath().toUri());
         this.jar = resource;
      }

      public String getName() {
         return this.jar.name();
      }

      public Path getPath() {
         return this.jar.getPrimaryPath();
      }

      public String toString() {
         return String.format("SecureJarResource(%s)", this.getName());
      }
   }
}
