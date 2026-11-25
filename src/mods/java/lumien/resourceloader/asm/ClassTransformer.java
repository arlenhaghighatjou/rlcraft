package lumien.resourceloader.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ClassTransformer implements IClassTransformer {
   Logger logger = LogManager.getLogger("ResourceLoader");

   public ClassTransformer() {
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      return transformedName.equals("net.minecraft.client.Minecraft") ? this.patchMinecraft(basicClass) : basicClass;
   }

   private byte[] patchMinecraft(byte[] basicClass) {
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(basicClass);
      classReader.accept(classNode, 0);
      this.logger.log(Level.DEBUG, "Found Minecraft Class: " + classNode.name);
      MethodNode refreshResources = null;
      MethodNode startGame = null;

      for(MethodNode mn : classNode.methods) {
         if (mn.name.equals(MCPNames.method("refreshResources"))) {
            refreshResources = mn;
         } else if (mn.name.equals(MCPNames.method("init"))) {
            ;
         }
      }

      if (refreshResources != null) {
         this.logger.log(Level.DEBUG, " - Found refreshResources 1/3");

         for(int i = 0; i < refreshResources.instructions.size(); ++i) {
            AbstractInsnNode ain = refreshResources.instructions.get(i);
            if (ain instanceof MethodInsnNode) {
               MethodInsnNode min = (MethodInsnNode)ain;
               if (min.name.equals(MCPNames.method("reloadResources"))) {
                  InsnList toInsert = new InsnList();
                  toInsert.add(new MethodInsnNode(184, "lumien/resourceloader/ResourceLoader", "insertForcedPack", "(Ljava/util/List;)V", false));
                  toInsert.add(new VarInsnNode(25, 1));
                  refreshResources.instructions.insertBefore(min, toInsert);
                  this.logger.log(Level.DEBUG, " - Patched refreshResources 3/3");
                  i += 2;
               } else if (min.name.equals("newArrayList")) {
                  AbstractInsnNode target = refreshResources.instructions.get(i + 1);
                  InsnList toInsert = new InsnList();
                  toInsert.add(new VarInsnNode(25, 1));
                  toInsert.add(new MethodInsnNode(184, "lumien/resourceloader/ResourceLoader", "insertNormalPack", "(Ljava/util/List;)V", false));
                  refreshResources.instructions.insert(target, toInsert);
                  this.logger.log(Level.DEBUG, " - Patched Patched refreshResources 2/3");
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
      this.logger.log(Level.INFO, "Found Dummy Class: " + classNode.name);
      ClassWriter writer = new ClassWriter(1);
      classNode.accept(writer);
      return writer.toByteArray();
   }
}
