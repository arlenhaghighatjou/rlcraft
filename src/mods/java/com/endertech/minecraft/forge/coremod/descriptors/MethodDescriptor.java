package com.endertech.minecraft.forge.coremod.descriptors;

import com.endertech.minecraft.forge.coremod.names.Name;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MethodDescriptor extends Descriptor {
   public static final MethodDescriptor VOID = of(Types.VOID);
   private final List<Descriptor> parameters;
   private final Descriptor returnType;

   protected static String builder(List<Descriptor> parameters, Descriptor returnType, boolean obfuscated) {
      StringBuilder result = new StringBuilder("(");

      for(Name parameter : parameters) {
         result.append(parameter.toString(obfuscated));
      }

      result.append(")").append(returnType.toString(obfuscated));
      return result.toString();
   }

   protected MethodDescriptor(List<Descriptor> parameters, Descriptor returnType) {
      super(builder(parameters, returnType, false), builder(parameters, returnType, true));
      this.returnType = returnType;
      this.parameters = parameters;
   }

   public static MethodDescriptor of(Descriptor returnTypeDesc) {
      return new MethodDescriptor(Collections.emptyList(), returnTypeDesc);
   }

   public static MethodDescriptor of(Classes returnType) {
      return of(returnType.descriptor);
   }

   public static MethodDescriptor of(Types returnType) {
      return of(returnType.descriptor);
   }

   public boolean complyWith(String string) {
      return this.toString(false).equals(string) || this.toString(true).equals(string);
   }

   public MethodDescriptor withParameters(Descriptor... parameters) {
      return new MethodDescriptor(Arrays.asList(parameters), this.returnType);
   }

   public MethodDescriptor withParameters(Classes... classes) {
      List<Descriptor> parameters = new ArrayList();

      for(Classes clazz : classes) {
         parameters.add(clazz.descriptor);
      }

      return new MethodDescriptor(parameters, this.returnType);
   }

   public MethodDescriptor withParameters(Types... types) {
      List<Descriptor> parameters = new ArrayList();

      for(Types type : types) {
         parameters.add(type.descriptor);
      }

      return new MethodDescriptor(parameters, this.returnType);
   }

   public MethodDescriptor withReversedParameters() {
      return new MethodDescriptor(Lists.reverse(this.parameters), this.returnType);
   }
}
