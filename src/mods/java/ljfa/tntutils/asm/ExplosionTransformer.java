package ljfa.tntutils.asm;

import java.util.Iterator;
import net.minecraft.launchwrapper.IClassTransformer;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ExplosionTransformer implements IClassTransformer {
   private final Logger coreLogger = LogManager.getLogger("TNTUtils Core", StringFormatterMessageFactory.INSTANCE);

   public ExplosionTransformer() {
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      if (transformedName.equals("net.minecraft.world.Explosion")) {
         if (name.equals(transformedName)) {
            this.coreLogger.info("About to patch class %s", transformedName);
            return this.patchClassExplosion(basicClass, false);
         } else {
            this.coreLogger.info("About to patch obfuscated class %s (%s)", name, transformedName);
            return this.patchClassExplosion(basicClass, true);
         }
      } else {
         return basicClass;
      }
   }

   private byte[] patchClassExplosion(byte[] basicClass, boolean obfuscated) {
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(basicClass);
      classReader.accept(classNode, 0);

      for(MethodNode mn : classNode.methods) {
         if (mn.name.equals(obfuscated ? "doExplosionB" : "doExplosionB") && mn.desc.equals("(Z)V")) {
            this.coreLogger.trace("Found target method %s%s", mn.name, mn.desc);
            this.patchDoExplosionB(mn);
            break;
         }
      }

      ClassWriter writer = new ClassWriter(3);
      classNode.accept(writer);
      return writer.toByteArray();
   }

   private void patchDoExplosionB(MethodNode mn) {
      Iterator<AbstractInsnNode> it = mn.instructions.iterator();
      boolean didInject = false;

      while(it.hasNext()) {
         AbstractInsnNode currentNode = (AbstractInsnNode)it.next();
         if (currentNode.getOpcode() == 110) {
            InsnNode fdivNode = (InsnNode)currentNode;
            currentNode = currentNode.getPrevious();
            if (currentNode.getOpcode() == 180) {
               currentNode = currentNode.getPrevious().getPrevious();
               if (currentNode.getOpcode() == 12) {
                  this.coreLogger.trace("Found target instructions \"fconst_1\" through \"fdiv\"");
                  LabelNode label = new LabelNode();
                  mn.instructions.insert(fdivNode, label);
                  InsnList toInject = new InsnList();
                  toInject.add(new VarInsnNode(25, 0));
                  toInject.add(new MethodInsnNode(184, Type.getInternalName(HooksExplosion.class), "getDropChance", "(Lnet/minecraft/world/Explosion;)F", false));
                  toInject.add(new JumpInsnNode(167, label));
                  mn.instructions.insertBefore(currentNode, toInject);
                  didInject = true;
                  break;
               }
            }
         }
      }

      if (didInject) {
         this.coreLogger.info("Successfully injected into %s%s", mn.name, mn.desc);
      } else {
         this.coreLogger.error("Failed injection into %s%s. There is probably an incompatibility with some other core mod.", mn.name, mn.desc);
      }

   }
}
