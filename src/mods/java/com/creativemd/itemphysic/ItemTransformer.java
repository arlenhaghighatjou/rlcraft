package com.creativemd.itemphysic;

import com.creativemd.creativecore.transformer.CreativeTransformer;
import com.creativemd.creativecore.transformer.Transformer;
import com.creativemd.creativecore.transformer.TransformerNames;
import java.util.ArrayList;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ItemTransformer extends CreativeTransformer {
   public static boolean isLite = false;

   public ItemTransformer() {
      super("itemphysic");
   }

   protected void initTransformers() {
      this.addTransformer(new Transformer("net.minecraft.client.renderer.entity.RenderEntityItem") {
         public void transform(ClassNode node) {
            String desc = this.patchDESC("(Lnet/minecraft/entity/item/EntityItem;DDDFF)V");
            String method = TransformerNames.patchMethodName("doRender", this.patchDESC("(Lnet/minecraft/entity/Entity;DDDFF)V"), this.patchClassName("net/minecraft/client/renderer/entity/Render"));
            MethodNode m = this.findMethod(node, method, desc);
            m.localVariables.clear();
            m.instructions.add(new LabelNode());
            AbstractInsnNode before = m.instructions.getFirst();
            m.instructions.insertBefore(before, new VarInsnNode(25, 0));
            m.instructions.insertBefore(before, new VarInsnNode(25, 1));
            m.instructions.insertBefore(before, new VarInsnNode(24, 2));
            m.instructions.insertBefore(before, new VarInsnNode(24, 4));
            m.instructions.insertBefore(before, new VarInsnNode(24, 6));
            m.instructions.insertBefore(before, new VarInsnNode(23, 8));
            m.instructions.insertBefore(before, new VarInsnNode(23, 9));
            m.instructions.insertBefore(before, new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ClientPhysic", "doRender", this.patchDESC("(L" + this.patchClassName("net/minecraft/client/renderer/entity/RenderEntityItem") + ";Lnet/minecraft/entity/Entity;DDDFF)Z"), false));
            LabelNode elseNode = new LabelNode();
            m.instructions.insertBefore(before, new JumpInsnNode(153, elseNode));
            m.instructions.insertBefore(before, new LabelNode());
            m.instructions.insertBefore(before, new VarInsnNode(25, 0));
            m.instructions.insertBefore(before, new VarInsnNode(25, 1));
            m.instructions.insertBefore(before, new VarInsnNode(24, 2));
            m.instructions.insertBefore(before, new VarInsnNode(24, 4));
            m.instructions.insertBefore(before, new VarInsnNode(24, 6));
            m.instructions.insertBefore(before, new VarInsnNode(23, 8));
            m.instructions.insertBefore(before, new VarInsnNode(23, 9));
            m.instructions.insertBefore(before, new MethodInsnNode(183, this.patchClassName("net/minecraft/client/renderer/entity/Render"), method, this.patchDESC("(Lnet/minecraft/entity/Entity;DDDFF)V"), false));
            m.instructions.insertBefore(before, new InsnNode(177));
            m.instructions.insertBefore(before, elseNode);
            m.instructions.insertBefore(before, new FrameNode(3, 0, (Object[])null, 0, (Object[])null));
         }
      });
      this.addTransformer(new Transformer("net.minecraft.entity.item.EntityItem") {
         public void transform(ClassNode node) {
            node.fields.add(new FieldNode(1, "skipPhysicRenderer", "Z", (String)null, false));
            String targetMethodDesc = "(DDDFFIZ)V";
            String targetMethodName = TransformerNames.patchMethodName("setPositionAndRotationDirect", targetMethodDesc, this.patchClassName("net/minecraft/entity/Entity"));
            MethodNode m = new MethodNode(1, targetMethodName, targetMethodDesc, (String)null, (String[])null);
            LabelNode label = new LabelNode();
            m.instructions.add(label);
            m.instructions.add(new VarInsnNode(25, 0));
            m.instructions.add(new VarInsnNode(24, 1));
            m.instructions.add(new VarInsnNode(24, 3));
            m.instructions.add(new VarInsnNode(24, 5));
            m.instructions.add(new VarInsnNode(23, 7));
            m.instructions.add(new VarInsnNode(23, 8));
            m.instructions.add(new VarInsnNode(21, 9));
            m.instructions.add(new VarInsnNode(21, 10));
            m.instructions.add(new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ClientPhysic", "setPositionAndRotationDirect", this.patchDESC("(Lnet/minecraft/entity/item/EntityItem;DDDFFIZ)V"), false));
            m.instructions.add(new InsnNode(177));
            LabelNode label2 = new LabelNode();
            m.instructions.add(label2);
            m.localVariables.add(new LocalVariableNode("this", this.patchDESC("Lnet/minecraft/entity/item/EntityItem;"), (String)null, label, label2, 0));
            m.localVariables.add(new LocalVariableNode("x", "D", (String)null, label, label2, 1));
            m.localVariables.add(new LocalVariableNode("y", "D", (String)null, label, label2, 3));
            m.localVariables.add(new LocalVariableNode("z", "D", (String)null, label, label2, 5));
            m.localVariables.add(new LocalVariableNode("yaw", "F", (String)null, label, label2, 7));
            m.localVariables.add(new LocalVariableNode("pitch", "F", (String)null, label, label2, 8));
            m.localVariables.add(new LocalVariableNode("posRotationIncrements", "I", (String)null, label, label2, 9));
            m.localVariables.add(new LocalVariableNode("p_180426_10_", "Z", (String)null, label, label2, 10));
            node.methods.add(m);
         }
      });
      if (!isLite) {
         this.addTransformer(new Transformer("net.minecraft.entity.item.EntityItem") {
            public ArrayList<AbstractInsnNode> getCallingNodes(String methodName, String desc) {
               ArrayList<AbstractInsnNode> nodes = new ArrayList();
               nodes.add(new VarInsnNode(25, 0));
               MethodInsnNode callingNode = new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ServerPhysic", methodName, desc, false);
               nodes.add(callingNode);
               return nodes;
            }

            public void transform(ClassNode node) {
               MethodNode m = this.findMethod(node, TransformerNames.patchMethodName("onUpdate", "()V", this.patchClassName("net/minecraft/entity/Entity")), "()V");
               String updateDESC = this.patchDESC("(Lnet/minecraft/entity/item/EntityItem;)V");
               MethodInsnNode methodNode = (MethodInsnNode)this.findNode(m.instructions, new MethodInsnNode(182, this.patchDESC("net/minecraft/entity/item/EntityItem"), TransformerNames.patchMethodName("hasNoGravity", "()Z", this.patchClassName("net/minecraft/entity/Entity")), "()Z", false));
               methodNode.setOpcode(184);
               methodNode.owner = "com/creativemd/itemphysic/physics/ServerPhysic";
               methodNode.name = "updatePre";
               methodNode.desc = this.patchDESC("(Lnet/minecraft/entity/item/EntityItem;)Z");
               String materialClassName = this.patchClassName("net/minecraft/block/material/Material");
               ArrayList<AbstractInsnNode> nodes = this.getCallingNodes("updateBurn", updateDESC);
               this.replaceLabel(m.instructions, new FieldInsnNode(178, materialClassName, TransformerNames.patchFieldName("LAVA", materialClassName), "L" + materialClassName + ";"), nodes, 5, true);
               this.removeLabel(m.instructions, new LdcInsnNode(new Double("0.9800000190734863")), 1);
               nodes = this.getCallingNodes("updatePost", updateDESC);
               this.replaceLabelBefore(m.instructions, new LdcInsnNode(new Double("-0.5")), nodes, 1, 1, true, false);
               m = new MethodNode(1, TransformerNames.patchMethodName("isBurning", "()Z", this.patchClassName("net/minecraft/entity/Entity")), "()Z", (String)null, (String[])null);
               LabelNode label = new LabelNode();
               m.instructions.add(label);
               m.instructions.add(new VarInsnNode(25, 0));
               m.instructions.add(new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ServerPhysic", "isItemBurning", this.patchDESC("(Lnet/minecraft/entity/item/EntityItem;)Z"), false));
               m.instructions.add(new InsnNode(172));
               LabelNode label2 = new LabelNode();
               m.instructions.add(label2);
               m.localVariables.add(new LocalVariableNode("this", "L" + this.patchClassName(this.className.replace(".", "/")) + ";", (String)null, label, label2, 0));
               node.methods.add(m);
               String attackDESC = this.patchDESC("(Lnet/minecraft/util/DamageSource;F)Z");
               m = this.findMethod(node, TransformerNames.patchMethodName("attackEntityFrom", attackDESC, this.patchClassName("net/minecraft/entity/Entity")), attackDESC);
               m.instructions.clear();
               m.instructions.add(new VarInsnNode(25, 0));
               m.instructions.add(new VarInsnNode(25, 1));
               m.instructions.add(new VarInsnNode(23, 2));
               m.instructions.add(new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ServerPhysic", "attackEntityFrom", this.patchDESC("(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/DamageSource;F)Z"), false));
               m.instructions.add(new InsnNode(172));
               String collideDESC = this.patchDESC("(Lnet/minecraft/entity/player/EntityPlayer;)V");
               m = this.findMethod(node, TransformerNames.patchMethodName("onCollideWithPlayer", collideDESC, this.patchClassName("net/minecraft/entity/Entity")), collideDESC);
               AbstractInsnNode before = m.instructions.getFirst();
               LabelNode label0 = new LabelNode();
               LabelNode labelElse = new LabelNode();
               LabelNode labelIf = new LabelNode();
               m.instructions.insertBefore(before, label0);
               m.instructions.insertBefore(before, new VarInsnNode(25, 0));
               m.instructions.insertBefore(before, new VarInsnNode(25, 1));
               m.instructions.insertBefore(before, new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ServerPhysic", "onCollideWithPlayer", this.patchDESC("(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/entity/player/EntityPlayer;)Z"), false));
               m.instructions.insertBefore(before, new JumpInsnNode(153, labelElse));
               m.instructions.insertBefore(before, labelIf);
               m.instructions.insertBefore(before, new InsnNode(177));
               m.instructions.insertBefore(before, labelElse);
               m.instructions.remove(before);
               String interactDESC = this.patchDESC("(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumHand;)Z");
               m = new MethodNode(1, TransformerNames.patchMethodName("processInitialInteract", interactDESC, this.patchClassName("net/minecraft/entity/Entity")), interactDESC, (String)null, (String[])null);
               label = new LabelNode();
               m.instructions.add(label);
               m.instructions.add(new VarInsnNode(25, 0));
               m.instructions.add(new VarInsnNode(25, 1));
               m.instructions.add(new VarInsnNode(25, 2));
               m.instructions.add(new VarInsnNode(25, 3));
               m.instructions.add(new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ServerPhysic", "processInitialInteract", this.patchDESC("(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumHand;)Z"), false));
               label2 = new LabelNode();
               m.instructions.add(label2);
               m.instructions.add(new InsnNode(4));
               m.instructions.add(new InsnNode(172));
               m.maxLocals = 4;
               m.localVariables.add(new LocalVariableNode("this", this.patchDESC("Lnet/minecraft/entity/item/EntityItem;"), (String)null, label, label2, 0));
               m.localVariables.add(new LocalVariableNode("player", this.patchDESC("Lnet/minecraft/entity/player/EntityPlayer;"), (String)null, label, label2, 1));
               m.localVariables.add(new LocalVariableNode("stack", this.patchDESC("Lnet/minecraft/item/ItemStack;"), (String)null, label, label2, 2));
               m.localVariables.add(new LocalVariableNode("hand", this.patchDESC("Lnet/minecraft/util/EnumHand;"), (String)null, label, label2, 3));
               node.methods.add(m);
               String updateFallStateDESC = this.patchDESC("(DZLnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)V");
               String updateFallStateName = TransformerNames.patchMethodName("updateFallState", updateFallStateDESC, this.patchClassName("net/minecraft/entity/Entity"));
               m = new MethodNode(1, updateFallStateName, updateFallStateDESC, (String)null, (String[])null);
               label = new LabelNode();
               m.instructions.add(label);
               m.instructions.add(new VarInsnNode(25, 0));
               m.instructions.add(new VarInsnNode(24, 1));
               m.instructions.add(new VarInsnNode(21, 3));
               m.instructions.add(new VarInsnNode(25, 4));
               m.instructions.add(new VarInsnNode(25, 5));
               m.instructions.add(new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ServerPhysic", "updateFallState", this.patchDESC("(Lnet/minecraft/entity/item/EntityItem;DZLnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)V"), false));
               m.instructions.add(new LabelNode());
               m.instructions.add(new VarInsnNode(25, 0));
               m.instructions.add(new VarInsnNode(24, 1));
               m.instructions.add(new VarInsnNode(21, 3));
               m.instructions.add(new VarInsnNode(25, 4));
               m.instructions.add(new VarInsnNode(25, 5));
               m.instructions.add(new MethodInsnNode(183, this.patchClassName("net/minecraft/entity/Entity"), updateFallStateName, updateFallStateDESC, false));
               label2 = new LabelNode();
               m.instructions.add(label2);
               m.instructions.add(new InsnNode(177));
               m.maxLocals = 5;
               m.maxStack = 6;
               m.localVariables.add(new LocalVariableNode("this", this.patchDESC("Lnet/minecraft/entity/item/EntityItem;"), (String)null, label, label2, 0));
               m.localVariables.add(new LocalVariableNode("y", "D", (String)null, label, label2, 1));
               m.localVariables.add(new LocalVariableNode("onGroundIn", "Z", (String)null, label, label2, 3));
               m.localVariables.add(new LocalVariableNode("state", this.patchDESC("Lnet/minecraft/block/state/IBlockState;"), (String)null, label, label2, 4));
               m.localVariables.add(new LocalVariableNode("pos", this.patchDESC("Lnet/minecraft/util/math/BlockPos;"), (String)null, label, label2, 5));
               node.methods.add(m);
            }
         });
         this.addTransformer(new Transformer("net.minecraft.client.entity.EntityPlayerSP") {
            public void transform(ClassNode node) {
               String desc = this.patchDESC("(Z)Lnet/minecraft/entity/item/EntityItem;");
               MethodNode m = this.findMethod(node, TransformerNames.patchMethodName("dropItem", desc, this.patchClassName("net/minecraft/entity/player/EntityPlayer")), desc);
               m.instructions.clear();
               m.instructions.add(new LabelNode());
               m.instructions.add(new VarInsnNode(21, 1));
               m.instructions.add(new MethodInsnNode(184, "com/creativemd/itemphysic/EventHandler", "dropItem", "(Z)V", false));
               m.instructions.add(new LabelNode());
               m.instructions.add(new InsnNode(1));
               m.instructions.add(new InsnNode(176));
            }
         });
      }

   }
}
