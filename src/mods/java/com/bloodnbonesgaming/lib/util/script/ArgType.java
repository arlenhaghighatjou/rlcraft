package com.bloodnbonesgaming.lib.util.script;

import com.bloodnbonesgaming.lib.BNBGamingLib;
import net.minecraft.world.gen.layer.GenLayer;

public enum ArgType {
   INT {
      public Object transformArg(Object arg) {
         return ArgTransformer.toInt(arg);
      }

      public boolean allowNull() {
         return false;
      }

      public String toString() {
         return "int";
      }
   },
   LONG {
      public Object transformArg(Object arg) {
         return ArgTransformer.toLong(arg);
      }

      public boolean allowNull() {
         return false;
      }

      public String toString() {
         return "long";
      }
   },
   FLOAT {
      public Object transformArg(Object arg) {
         return ArgTransformer.toFloat(arg);
      }

      public boolean allowNull() {
         return false;
      }

      public String toString() {
         return "float";
      }
   },
   DOUBLE {
      public Object transformArg(Object arg) {
         return ArgTransformer.toDouble(arg);
      }

      public boolean allowNull() {
         return false;
      }

      public String toString() {
         return "double";
      }
   },
   STRING {
      public Object transformArg(Object arg) {
         return ArgTransformer.toString(arg);
      }

      public boolean allowNull() {
         return true;
      }

      public String toString() {
         return "String";
      }
   },
   NON_NULL_BIOME_ID {
      public Object transformArg(Object arg) {
         return ArgTransformer.toBiomeInt(arg);
      }

      public boolean allowNull() {
         return false;
      }

      public String toString() {
         return "Non_Null_Biome_ID";
      }
   },
   BIOME_ID_ARRAY {
      public Object transformArg(Object arg) {
         return ArgTransformer.toBiomeIntArray(arg);
      }

      public boolean allowNull() {
         return true;
      }

      public String toString() {
         return "Biome_ID_Array";
      }
   },
   NON_NULL_BIOME_ID_ARRAY {
      public Object transformArg(Object arg) {
         return ArgTransformer.toBiomeIntArray(arg);
      }

      public boolean allowNull() {
         return false;
      }

      public String toString() {
         return "Non_Null_Biome_ID_Array";
      }
   },
   GENLAYER {
      public Object transformArg(Object arg) {
         return arg instanceof GenLayer ? arg : null;
      }

      public boolean allowNull() {
         return false;
      }

      public String toString() {
         return "GenLayer";
      }
   };

   private ArgType() {
   }

   public abstract Object transformArg(Object var1);

   public abstract boolean allowNull();

   public abstract String toString();

   public static Object[] transformArgs(Object[] masterArgs, ArgType[] argTypes) {
      Object[] args = new Object[masterArgs.length];

      for(int i = 0; i < args.length && i < argTypes.length; ++i) {
         if (masterArgs[i] == null) {
            if (!argTypes[i].allowNull()) {
               BNBGamingLib.libInstance.getLog().info("Arg " + i + " was null and the argtype does not accept null");
               return null;
            }
         } else {
            Object arg = argTypes[i].transformArg(masterArgs[i]);
            if (arg == null) {
               BNBGamingLib.libInstance.getLog().info("Arg " + i + " could not be transformed");
               return null;
            }

            args[i] = arg;
         }
      }

      return args;
   }
}
