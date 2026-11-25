package lumien.chunkanimator.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ClassTransformer implements IClassTransformer {
   Logger logger = LogManager.getLogger("ChunkAnimatorCore");
   final String asmHandler = "lumien/chunkanimator/handler/AsmHandler";

   public ClassTransformer() {
      this.logger.log(Level.DEBUG, "Starting Class Transformation");
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      if (transformedName.equals("net.minecraft.client.renderer.ChunkRenderContainer")) {
         return this.patchChunkRenderContainer(basicClass);
      } else {
         return transformedName.equals("net.minecraft.client.renderer.chunk.RenderChunk") ? this.patchRenderChunk(basicClass) : basicClass;
      }
   }

   private byte[] patchRenderChunk(byte[] basicClass) {
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(basicClass);
      classReader.accept(classNode, 0);
      this.logger.log(Level.DEBUG, "Found RenderChunk Class: " + classNode.name);
      MethodNode setOrigin = null;

      for(MethodNode mn : classNode.methods) {
         if (mn.name.equals(MCPNames.method("setPosition"))) {
            setOrigin = mn;
            break;
         }
      }

      if (setOrigin != null) {
         this.logger.log(Level.DEBUG, "- Found setOrigin");

         for(int i = 0; i < setOrigin.instructions.size(); ++i) {
            AbstractInsnNode ain;
            if ((ain = setOrigin.instructions.get(i)) instanceof MethodInsnNode) {
               MethodInsnNode min = (MethodInsnNode)ain;
               if (min.name.equals(MCPNames.method("stopCompileTask"))) {
                  InsnList toInsert = new InsnList();
                  toInsert.add(new VarInsnNode(25, 0));
                  toInsert.add(new VarInsnNode(21, 1));
                  toInsert.add(new VarInsnNode(21, 2));
                  toInsert.add(new VarInsnNode(21, 3));
                  toInsert.add(new MethodInsnNode(184, "lumien/chunkanimator/handler/AsmHandler", "setOrigin", "(Lnet/minecraft/client/renderer/chunk/RenderChunk;III)V", false));
                  setOrigin.instructions.insertBefore(min, toInsert);
                  i += 5;
               }
            }
         }

         this.logger.log(Level.DEBUG, "- Patched setOrigin");
      }

      ClassWriter writer = new ClassWriter(1);
      classNode.accept(writer);
      return writer.toByteArray();
   }

   private byte[] patchChunkRenderContainer(byte[] basicClass) {
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(basicClass);
      classReader.accept(classNode, 0);
      this.logger.log(Level.DEBUG, "Found ChunkRenderContainer Class: " + classNode.name);
      MethodNode preRenderChunk = null;

      for(MethodNode mn : classNode.methods) {
         if (mn.name.equals(MCPNames.method("preRenderChunk"))) {
            preRenderChunk = mn;
            break;
         }
      }

      if (preRenderChunk != null) {
         this.logger.log(Level.DEBUG, "- Found preRenderChunk");

         for(int i = 0; i < preRenderChunk.instructions.size(); ++i) {
            AbstractInsnNode ain = preRenderChunk.instructions.get(i);
            if (ain instanceof MethodInsnNode) {
               MethodInsnNode min = (MethodInsnNode)ain;
               if (min.name.equals(MCPNames.method("translate"))) {
                  this.logger.log(Level.DEBUG, "- Patched preRenderChunk");
                  InsnList toInsert = new InsnList();
                  toInsert.add(new VarInsnNode(25, 1));
                  toInsert.add(new MethodInsnNode(184, "lumien/chunkanimator/handler/AsmHandler", "preRenderChunk", "(Lnet/minecraft/client/renderer/chunk/RenderChunk;)V", false));
                  preRenderChunk.instructions.insert(min, toInsert);
                  break;
               }
            }
         }
      }

      ClassWriter writer = new ClassWriter(1);
      classNode.accept(writer);
      return writer.toByteArray();
   }

   private byte[] patchDummyClass(byte[] basicClass) {
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(basicClass);
      classReader.accept(classNode, 0);
      this.logger.log(Level.DEBUG, "Found Dummy Class: " + classNode.name);
      ClassWriter writer = new ClassWriter(1);
      classNode.accept(writer);
      return writer.toByteArray();
   }
}
