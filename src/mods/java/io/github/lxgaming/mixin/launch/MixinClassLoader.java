package io.github.lxgaming.mixin.launch;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MixinClassLoader extends ClassLoader {
   private final ClassLoader child;
   private final Set<String> invalidClasses;

   public MixinClassLoader(ClassLoader parent, ClassLoader child) {
      super(parent);
      this.child = child;
      this.invalidClasses = Collections.newSetFromMap(new ConcurrentHashMap());
   }

   protected Class<?> findClass(String name) throws ClassNotFoundException {
      if (this.invalidClasses.contains(name)) {
         throw new ClassNotFoundException(name);
      } else {
         this.invalidClasses.add(name);
         Class<?> validClass = this.child.loadClass(name);
         this.invalidClasses.remove(name);
         return validClass;
      }
   }
}
