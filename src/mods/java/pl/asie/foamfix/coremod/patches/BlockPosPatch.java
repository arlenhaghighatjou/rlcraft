package pl.asie.foamfix.coremod.patches;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.HashSet;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class BlockPosPatch {
   private static final BiMap<String, String> mutableFieldSwaps = HashBiMap.create();
   private static final HashSet<String> mutableDeletedMethods = new HashSet();
   private static final HashSet<String> mutableOwners = new HashSet();

   public BlockPosPatch() {
   }

   public static ClassNode patchVec3i(ClassNode node) {
      for(FieldNode fn : node.fields) {
         if ("I".equals(fn.desc) && fn.access == 18) {
            fn.access = 4;
         }
      }

      return node;
   }

   public static ClassVisitor patchOtherClass(ClassVisitor next) {
      return new BlockPosPatch.BlockPosClassVisitor(327680, next);
   }

   static {
      mutableFieldSwaps.put("x", "x");
      mutableFieldSwaps.put("y", "y");
      mutableFieldSwaps.put("z", "z");
      mutableFieldSwaps.put("x", "x");
      mutableFieldSwaps.put("y", "y");
      mutableFieldSwaps.put("z", "z");
      mutableDeletedMethods.add("getX");
      mutableDeletedMethods.add("getY");
      mutableDeletedMethods.add("getZ");
      mutableDeletedMethods.add("getX");
      mutableDeletedMethods.add("getY");
      mutableDeletedMethods.add("getZ");
      mutableOwners.add("net/minecraft/util/math/BlockPos$MutableBlockPos");
   }

   private static class BlockPosClassVisitor extends ClassVisitor {
      private boolean isMutable;
      private boolean isVec3i;
      private boolean hasChanged = false;

      public BlockPosClassVisitor(int api, ClassVisitor next) {
         super(api, next);
      }

      public void setCV(ClassVisitor visitor) {
         this.cv = visitor;
      }

      public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
         this.isMutable = "net/minecraft/util/math/BlockPos$MutableBlockPos".equals(name);
         this.isVec3i = "net/minecraft/util/math/Vec3i".equals(name);
         if (BlockPosPatch.mutableOwners.contains(superName)) {
            BlockPosPatch.mutableOwners.add(name);
         }

         if (this.cv != null) {
            this.cv.visit(version, access, name, signature, superName, interfaces);
         }

      }

      public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
         if (this.cv == null) {
            return null;
         } else if (this.isVec3i && BlockPosPatch.mutableFieldSwaps.containsValue(name)) {
            return this.cv.visitField(access & -24 | 4, name, desc, signature, value);
         } else {
            return this.isMutable && BlockPosPatch.mutableFieldSwaps.containsKey(name) ? null : this.cv.visitField(access, name, desc, signature, value);
         }
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         return this.isMutable && BlockPosPatch.mutableDeletedMethods.contains(name) ? null : new BlockPosPatch.BlockPosMethodVisitor(this.api, this, this.cv != null ? this.cv.visitMethod(access, name, desc, signature, exceptions) : null);
      }
   }

   private static class BlockPosMethodVisitor extends MethodVisitor {
      private final BlockPosPatch.BlockPosClassVisitor classVisitor;

      public BlockPosMethodVisitor(int api, BlockPosPatch.BlockPosClassVisitor cv, MethodVisitor mv) {
         super(api, mv);
         this.classVisitor = cv;
      }

      public void visitFieldInsn(int opcode, String owner, String name, String desc) {
         if (BlockPosPatch.mutableOwners.contains(owner)) {
            String dst = (String)BlockPosPatch.mutableFieldSwaps.get(name);
            if (dst != null) {
               if (this.mv != null) {
                  this.mv.visitFieldInsn(opcode, "net/minecraft/util/math/Vec3i", dst, desc);
               }

               this.classVisitor.hasChanged = true;
            } else if (this.mv != null) {
               this.mv.visitFieldInsn(opcode, owner, name, desc);
            }
         } else if (this.mv != null) {
            this.mv.visitFieldInsn(opcode, owner, name, desc);
         }

      }
   }
}
