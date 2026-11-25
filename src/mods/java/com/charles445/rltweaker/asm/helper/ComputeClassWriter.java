package com.charles445.rltweaker.asm.helper;

import com.charles445.rltweaker.asm.util.ASMLogger;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLRemappingAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.RemappingClassAdapter;

public class ComputeClassWriter extends ClassWriter {
   private ClassLoader l = this.getClass().getClassLoader();
   public Map<String, ClassReader> clazzCache = new ConcurrentHashMap();

   public ComputeClassWriter(int flags) {
      super(flags);
   }

   protected String getCommonSuperClass(String type1, String type2) {
      try {
         ClassReader info1 = this.typeInfo(type1);
         ClassReader info2 = this.typeInfo(type2);
         if ((info1.getAccess() & 512) != 0) {
            return this.typeImplements(type2, info2, type1) ? type1 : "java/lang/Object";
         } else if ((info2.getAccess() & 512) != 0) {
            return this.typeImplements(type1, info1, type2) ? type2 : "java/lang/Object";
         } else {
            StringBuilder b1 = this.typeAncestors(type1, info1);
            StringBuilder b2 = this.typeAncestors(type2, info2);
            String result = "java/lang/Object";
            int end1 = b1.length();
            int end2 = b2.length();

            while(true) {
               int start1 = b1.lastIndexOf(";", end1 - 1);
               int start2 = b2.lastIndexOf(";", end2 - 1);
               if (start1 == -1 || start2 == -1 || end1 - start1 != end2 - start2) {
                  return result;
               }

               String p1 = b1.substring(start1 + 1, end1);
               String p2 = b2.substring(start2 + 1, end2);
               if (!p1.equals(p2)) {
                  return result;
               }

               result = p1;
               end1 = start1;
               end2 = start2;
            }
         }
      } catch (IOException var14) {
         throw new RuntimeException(var14.toString());
      }
   }

   private StringBuilder typeAncestors(String type, ClassReader info) throws IOException {
      StringBuilder b;
      for(b = new StringBuilder(); !"java/lang/Object".equals(type); info = this.typeInfo(type)) {
         b.append(';').append(type);
         type = info.getSuperName();
      }

      return b;
   }

   private boolean typeImplements(String type, ClassReader info, String itf) throws IOException {
      while(!"java/lang/Object".equals(type)) {
         String[] itfs = info.getInterfaces();

         for(int i = 0; i < itfs.length; ++i) {
            if (itfs[i].equals(itf)) {
               return true;
            }
         }

         for(int i = 0; i < itfs.length; ++i) {
            if (this.typeImplements(itfs[i], this.typeInfo(itfs[i]), itf)) {
               return true;
            }
         }

         type = info.getSuperName();
         info = this.typeInfo(type);
      }

      return false;
   }

   public byte[] toByteArray() {
      this.clazzCache.clear();
      return super.toByteArray();
   }

   private ClassReader typeInfo(String type) throws IOException {
      ClassReader fetchedReader = (ClassReader)this.clazzCache.get(type);
      if (fetchedReader != null) {
         return fetchedReader;
      } else {
         InputStream is = this.l.getResourceAsStream(type + ".class");
         if (is == null) {
            is = this.l.getResourceAsStream(ObfHelper.forceToObfClassName(type) + ".class");
            if (is != null) {
               ClassReader var8;
               try {
                  ClassWriter writer = new ClassWriter(1);
                  RemappingClassAdapter adapter = new FMLRemappingAdapter(writer);
                  ClassReader reader = new ClassReader(is);
                  reader.accept(adapter, 4);
                  ClassReader newReader = new ClassReader(writer.toByteArray());
                  this.clazzCache.put(type, newReader);
                  var8 = newReader;
               } finally {
                  is.close();
               }

               return var8;
            }

            ASMLogger.error("RLTweaker was unable to find the class file resource for the type: " + type);
            ASMLogger.error("The game will now crash. Please contact the RLTweaker developer for support.");
         }

         ClassReader adapter;
         try {
            ClassReader newReader = new ClassReader(is);
            this.clazzCache.put(type, newReader);
            adapter = newReader;
         } finally {
            is.close();
         }

         return adapter;
      }
   }
}
