package io.github.xcube16.iseedragons.asm.patch;

import io.github.xcube16.iseedragons.ISD;
import io.github.xcube16.iseedragons.asm.ISeeDragonsTransformer;
import io.github.xcube16.iseedragons.asm.Patch;
import io.github.xcube16.iseedragons.asm.PatchResult;
import io.github.xcube16.iseedragons.asm.Patcher;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

@Patcher(
   name = "Ice and Fire Axe fix",
   config = "IceAndFireAxeFix"
)
public class PatchIceAndFireAxes {
   public PatchIceAndFireAxes() {
   }

   @Patch(
      target = "com.github.alexthe666.iceandfire.item.ItemModAxe",
      desc = "Make ItemModAxe extend ItemAxe and remove EFFECTIVE_ON hard-coded list"
   )
   public static PatchResult fixItemModAxe(ISeeDragonsTransformer tweaker, ClassNode node) {
      int fieldCount = node.fields.size();
      node.fields = (List)node.fields.stream().filter((field) -> {
         return !field.name.equals("EFFECTIVE_ON");
      }).collect(Collectors.toList());
      if (fieldCount != node.fields.size() + 1) {
         ISD.logger.error("Failed to remove EFFECTIVE_ON field");
         return PatchResult.NO_MUTATION;
      } else {
         node.superName = "net/minecraft/item/ItemAxe";
         Optional<MethodNode> clinitMethod = node.methods.stream().filter((method) -> {
            return method.name.equals("<clinit>");
         }).findFirst();
         if (!clinitMethod.isPresent()) {
            ISD.logger.warn("Failed to find <clinit> method");
            return PatchResult.NO_MUTATION;
         } else {
            node.methods.remove(clinitMethod.get());
            Optional<MethodNode> initMethod = node.methods.stream().filter((method) -> {
               return method.name.equals("<init>");
            }).findFirst();
            if (!initMethod.isPresent()) {
               ISD.logger.error("Failed to find <init> method");
               return PatchResult.NO_MUTATION;
            } else {
               AbstractInsnNode getstaticIns;
               for(getstaticIns = ((MethodNode)initMethod.get()).instructions.getFirst(); getstaticIns != null && getstaticIns.getOpcode() != 178; getstaticIns = getstaticIns.getNext()) {
                  ;
               }

               if (getstaticIns == null) {
                  ISD.logger.error("Failed to find GETSTATIC instruction");
                  return PatchResult.NO_MUTATION;
               } else {
                  AbstractInsnNode invokespecialIns = getstaticIns.getNext();
                  ((MethodNode)initMethod.get()).instructions.remove(getstaticIns.getPrevious());
                  ((FieldInsnNode)getstaticIns).owner = "net/minecraft/item/Item$ToolMaterial";
                  ((FieldInsnNode)getstaticIns).name = "DIAMOND";
                  ((FieldInsnNode)getstaticIns).desc = "Lnet/minecraft/item/Item$ToolMaterial;";
                  if (invokespecialIns != null && invokespecialIns.getOpcode() == 183) {
                     ((MethodInsnNode)invokespecialIns).owner = "net/minecraft/item/ItemAxe";
                     ((MethodInsnNode)invokespecialIns).desc = "(Lnet/minecraft/item/Item$ToolMaterial;)V";
                     InsnList setToolMaterial = new InsnList();
                     setToolMaterial.add(new VarInsnNode(25, 0));
                     setToolMaterial.add(new VarInsnNode(25, 1));
                     setToolMaterial.add(new FieldInsnNode(181, "com/github/alexthe666/iceandfire/item/ItemModAxe", "toolMaterial", "Lnet/minecraft/item/Item$ToolMaterial;"));
                     ((MethodNode)initMethod.get()).instructions.insert(invokespecialIns, setToolMaterial);
                     return PatchResult.NO_FLAGS;
                  } else {
                     ISD.logger.error("Failed to find INVOKESPECIAL instruction");
                     return PatchResult.NO_MUTATION;
                  }
               }
            }
         }
      }
   }
}
