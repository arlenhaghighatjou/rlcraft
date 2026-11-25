package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.ASMUtil;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchCustomAttributeInstances extends PatchManager {
   public PatchCustomAttributeInstances() {
      super("Patch Custom Attribute Instances");
      this.add(new Patch(this, "net.minecraft.entity.ai.attributes.AttributeMap", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_createInstance = this.findMethodWithDesc(clazzNode, "(Lnet/minecraft/entity/ai/attributes/IAttribute;)Lnet/minecraft/entity/ai/attributes/IAttributeInstance;", new String[]{"createInstance", "createInstance"});
            LabelNode label = new LabelNode();
            TransformUtil.insertBeforeFirst(m_createInstance, ASMUtil.listOf(new VarInsnNode(25, 1), new TypeInsnNode(193, "com/charles445/rltweaker/entity/attribute/AttributeInstanceFactory"), new JumpInsnNode(153, label), new VarInsnNode(25, 1), new VarInsnNode(25, 0), new VarInsnNode(25, 1), new MethodInsnNode(185, "com/charles445/rltweaker/entity/attribute/AttributeInstanceFactory", "createInstance", "(Lnet/minecraft/entity/ai/attributes/AbstractAttributeMap;Lnet/minecraft/entity/ai/attributes/IAttribute;)Lnet/minecraft/entity/ai/attributes/IAttributeInstance;", true), new InsnNode(176), label));
         }
      });
   }
}
