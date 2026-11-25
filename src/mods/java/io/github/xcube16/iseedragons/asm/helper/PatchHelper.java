package io.github.xcube16.iseedragons.asm.helper;

import io.github.xcube16.iseedragons.ISD;
import io.github.xcube16.iseedragons.asm.PatchResult;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PatchHelper {
   public PatchHelper() {
   }

   public static PatchResult muteMethod(MethodNode method) {
      boolean flag = false;

      for(AbstractInsnNode ip = method.instructions.getFirst(); ip != null; ip = ip.getNext()) {
         if (ip.getOpcode() == 185) {
            MethodInsnNode call = (MethodInsnNode)ip;
            if (call.owner.equals("org/apache/logging/log4j/Logger") && call.name.equals("error") && call.desc.equals("(Ljava/lang/String;Ljava/lang/Throwable;)V")) {
               method.instructions.insert(call, new InsnNode(88));
               method.instructions.insert(call, new InsnNode(87));
               ip = call.getPrevious();
               method.instructions.remove(call);
               flag = true;
            }
         }
      }

      return flag ? PatchResult.NO_FLAGS : PatchResult.NO_MUTATION;
   }

   public static MethodNode findMethod(ClassNode node, String name) throws NoSuchMethodException {
      return findMethod(node, name, (String)null);
   }

   public static MethodNode findMethod(ClassNode node, String name, @Nullable String desc) throws NoSuchMethodException {
      Optional<MethodNode> method = node.methods.stream().filter((m) -> {
         return m.name.equals(name);
      }).filter((m) -> {
         return desc == null || m.desc.equals(desc);
      }).findFirst();
      if (!method.isPresent()) {
         ISD.logger.warn("Failed to find loadCustomAdvancements() method");
         throw new NoSuchMethodException(node.name + " " + name + " " + desc);
      } else {
         return (MethodNode)method.get();
      }
   }
}
