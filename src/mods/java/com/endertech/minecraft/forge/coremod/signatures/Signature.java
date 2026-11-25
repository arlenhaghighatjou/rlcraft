package com.endertech.minecraft.forge.coremod.signatures;

import com.endertech.common.Args;
import com.endertech.minecraft.forge.coremod.descriptors.Descriptor;
import com.endertech.minecraft.forge.coremod.names.ClassName;
import com.endertech.minecraft.forge.coremod.names.Name;

public class Signature {
   public final ClassName className;
   public final Name name;
   public final Descriptor descriptor;

   public Signature(ClassName className, Name name, Descriptor descriptor) {
      this.className = className;
      this.name = name;
      this.descriptor = descriptor;
   }

   public Signature(String className, String name, Descriptor descriptor) {
      this(ClassName.from(className), new Name(name, name), descriptor);
   }

   public String getFullJavaName() {
      return Args.joinDelim(".", this.className.java, this.name.normal);
   }

   public String getSimpleJavaName() {
      return Args.joinDelim(".", this.className.getSimpleName(), this.name.normal);
   }

   public String toString() {
      return this.getClass().getSimpleName() + Args.group(this.className, this.name, this.descriptor);
   }
}
