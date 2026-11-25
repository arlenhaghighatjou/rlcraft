package com.creativemd.itemphysic;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ItemOldTransformer implements IClassTransformer {
   public static boolean isLite = false;
   public static boolean obfuscated = false;
   public static final String[] names = new String[]{".", "net/minecraft/client/renderer/entity/RenderEntityItem", "doRender", "net/minecraft/entity/item/EntityItem", "net/minecraft/entity/Entity", "net/minecraft/client/renderer/entity/Render", "setPositionAndRotationDirect", "onUpdate", "isBurning", "attackEntityFrom", "net/minecraft/util/DamageSource", "onCollideWithPlayer", "net/minecraft/entity/player/EntityPlayer", "processInitialInteract", "net/minecraft/item/ItemStack", "net/minecraft/util/EnumHand", "canBeCollidedWith", "net/minecraft/client/entity/EntityPlayerSP", "dropItem"};
   public static final String[] namesOb = new String[]{"/", "bru", "a", "yd", "rr", "brk", "a", "m", "aH", "a", "rc", "d", "zj", "a", "adq", "qm", "ap", "bmr", "a"};

   public ItemOldTransformer() {
   }

   public static String forcePatch(String input) {
      for(int zahl = 0; zahl < names.length; ++zahl) {
         input = input.replace(names[zahl], namesOb[zahl]);
      }

      return input;
   }

   public static String patch(String input) {
      return obfuscated ? forcePatch(input) : input;
   }

   public byte[] transform(String arg0, String arg1, byte[] arg2) {
      if (arg0.equals(forcePatch("net.minecraft.client.renderer.entity.RenderEntityItem")) | arg0.contains("net.minecraft.client.renderer.entity.RenderEntityItem")) {
         obfuscated = !arg0.contains("net.minecraft.client.renderer.entity.RenderEntityItem");
         ItemDummyContainer.logger.info("[ItemPhysic] Patching " + arg0);
         arg2 = this.replaceMethodDoRender(arg0, arg2);
      }

      if (arg0.equals(forcePatch("net.minecraft.entity.item.EntityItem")) | arg0.equals("net.minecraft.entity.item.EntityItem")) {
         obfuscated = !arg0.contains("net.minecraft.entity.item.EntityItem");
         if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            arg2 = this.addPositionMethod(arg0, arg2);
         }
      }

      if (!isLite) {
         if (arg0.equals(forcePatch("net.minecraft.entity.item.EntityItem")) | arg0.equals("net.minecraft.entity.item.EntityItem")) {
            ItemDummyContainer.logger.info("[ItemPhysic] Patching " + arg0);
            arg2 = this.replaceMethodOnUpdate(arg0, arg2);
            arg2 = this.addMethodIsBurning(arg0, arg2);
            arg2 = this.replaceMethods(arg0, arg2);
         }

         if (arg0.equals(forcePatch("net.minecraft.client.entity.EntityPlayerSP")) | arg0.equals("net.minecraft.client.entity.EntityPlayerSP")) {
            obfuscated = !arg0.contains("net.minecraft.client.entity.EntityPlayerSP");
            ItemDummyContainer.logger.info("[ItemPhysic] Patching " + arg0);
            arg2 = this.removeDrop(arg0, arg2);
         }
      }

      return arg2;
   }

   public byte[] replaceMethodDoRender(String name, byte[] bytes) {
      String targetMethodName = patch("doRender");
      String targetDESC = patch("(Lnet/minecraft/entity/item/EntityItem;DDDFF)V");
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(bytes);
      classReader.accept(classNode, 0);

      for(MethodNode m : classNode.methods) {
         if (m.name.equals(targetMethodName) && m.desc.equals(targetDESC)) {
            m.localVariables.clear();
            m.instructions.clear();
            m.instructions.add(new VarInsnNode(25, 0));
            m.instructions.add(new VarInsnNode(25, 1));
            m.instructions.add(new VarInsnNode(24, 2));
            m.instructions.add(new VarInsnNode(24, 4));
            m.instructions.add(new VarInsnNode(24, 6));
            m.instructions.add(new VarInsnNode(23, 8));
            m.instructions.add(new VarInsnNode(23, 9));
            m.instructions.add(new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ClientPhysic", "doRender", patch("(Lnet/minecraft/client/renderer/entity/RenderEntityItem;Lnet/minecraft/entity/Entity;DDDFF)V"), false));
            m.instructions.add(new VarInsnNode(25, 0));
            m.instructions.add(new VarInsnNode(25, 1));
            m.instructions.add(new VarInsnNode(24, 2));
            m.instructions.add(new VarInsnNode(24, 4));
            m.instructions.add(new VarInsnNode(24, 6));
            m.instructions.add(new VarInsnNode(23, 8));
            m.instructions.add(new VarInsnNode(23, 9));
            m.instructions.add(new MethodInsnNode(183, patch("net/minecraft/client/renderer/entity/Render"), targetMethodName, patch("(Lnet/minecraft/entity/Entity;DDDFF)V"), false));
            m.instructions.add(new InsnNode(177));
            break;
         }
      }

      ClassWriter writer = new ClassWriter(1);
      classNode.accept(writer);
      return writer.toByteArray();
   }

   public byte[] addPositionMethod(String name, byte[] bytes) {
      String targetMethodName = patch("setPositionAndRotationDirect");
      String targetMethodDesc = "(DDDFFIZ)V";
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(bytes);
      classReader.accept(classNode, 0);
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
      m.instructions.add(new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ClientPhysic", "setPositionAndRotationDirect", patch("(Lnet/minecraft/entity/item/EntityItem;DDDFFIZ)V"), false));
      m.instructions.add(new InsnNode(177));
      LabelNode label2 = new LabelNode();
      m.instructions.add(label2);
      m.localVariables.add(new LocalVariableNode("this", patch("Lnet/minecraft/entity/item/EntityItem;"), (String)null, label, label2, 0));
      m.localVariables.add(new LocalVariableNode("x", "D", (String)null, label, label2, 1));
      m.localVariables.add(new LocalVariableNode("y", "D", (String)null, label, label2, 3));
      m.localVariables.add(new LocalVariableNode("z", "D", (String)null, label, label2, 5));
      m.localVariables.add(new LocalVariableNode("yaw", "F", (String)null, label, label2, 7));
      m.localVariables.add(new LocalVariableNode("pitch", "F", (String)null, label, label2, 8));
      m.localVariables.add(new LocalVariableNode("posRotationIncrements", "I", (String)null, label, label2, 9));
      m.localVariables.add(new LocalVariableNode("p_180426_10_", "Z", (String)null, label, label2, 10));
      classNode.methods.add(m);
      ClassWriter writer = new ClassWriter(1);
      classNode.accept(writer);
      return writer.toByteArray();
   }

   public byte[] removeDrop(String name, byte[] bytes) {
      String invokeName = patch("dropItem");
      String invokeDESC = patch("(Z)Lnet/minecraft/entity/item/EntityItem;");
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(bytes);
      classReader.accept(classNode, 0);

      for(MethodNode m : classNode.methods) {
         if (m.name.equals(invokeName) && m.desc.equals(invokeDESC)) {
            m.instructions.clear();
            m.instructions.add(new InsnNode(1));
            m.instructions.add(new InsnNode(176));
         }
      }

      ClassWriter writer = new ClassWriter(1);
      classNode.accept(writer);
      return writer.toByteArray();
   }

   public byte[] addMethodIsBurning(String name, byte[] bytes) {
      String targetMethodName = patch("isBurning");
      String targetDESC = patch("(Lnet/minecraft/entity/item/EntityItem;)Z");
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(bytes);
      classReader.accept(classNode, 0);
      MethodNode m = new MethodNode(1, targetMethodName, "()Z", (String)null, (String[])null);
      LabelNode label = new LabelNode();
      m.instructions.add(label);
      m.instructions.add(new VarInsnNode(25, 0));
      m.instructions.add(new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ServerPhysic", "isItemBurning", targetDESC, false));
      m.instructions.add(new InsnNode(172));
      LabelNode label2 = new LabelNode();
      m.instructions.add(label2);
      m.localVariables.add(new LocalVariableNode("this", "L" + name.replace(".", "/") + ";", (String)null, label, label2, 0));
      classNode.methods.add(m);
      ClassWriter writer = new ClassWriter(1);
      classNode.accept(writer);
      return writer.toByteArray();
   }

   public byte[] replaceMethods(String name, byte[] bytes) {
      String targetMethodName = patch("attackEntityFrom");
      String targetDESC = patch("(Lnet/minecraft/util/DamageSource;F)Z");
      String targetNewDESC = patch("(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/DamageSource;F)Z");
      String targetMethodName2 = patch("onCollideWithPlayer");
      String targetDESC2 = patch("(Lnet/minecraft/entity/player/EntityPlayer;)V");
      String newDESC2 = patch("(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/entity/player/EntityPlayer;)V");
      String newDESC3 = patch("(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumHand;)Z");
      String newMethod = patch("processInitialInteract");
      String newMethodDESC = patch("(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumHand;)Z");
      String newMethodVar = patch("Lnet/minecraft/entity/player/EntityPlayer;");
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(bytes);
      classReader.accept(classNode, 0);

      for(MethodNode m : classNode.methods) {
         if (m.name.equals(targetMethodName) && m.desc.equals(targetDESC)) {
            m.instructions.clear();
            m.instructions.add(new VarInsnNode(25, 0));
            m.instructions.add(new VarInsnNode(25, 1));
            m.instructions.add(new VarInsnNode(23, 2));
            m.instructions.add(new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ServerPhysic", "attackEntityFrom", targetNewDESC, false));
            m.instructions.add(new InsnNode(172));
         }

         if (m.name.equals(targetMethodName2) && m.desc.equals(targetDESC2)) {
            m.instructions.clear();
            m.instructions.add(new VarInsnNode(25, 0));
            m.instructions.add(new VarInsnNode(25, 1));
            m.instructions.add(new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ServerPhysic", "onCollideWithPlayer", newDESC2, false));
            m.instructions.add(new InsnNode(177));
         }
      }

      MethodNode m = new MethodNode(1, newMethod, newMethodDESC, (String)null, (String[])null);
      LabelNode label = new LabelNode();
      m.instructions.add(label);
      m.instructions.add(new VarInsnNode(25, 0));
      m.instructions.add(new VarInsnNode(25, 1));
      m.instructions.add(new VarInsnNode(25, 2));
      m.instructions.add(new VarInsnNode(25, 3));
      m.instructions.add(new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ServerPhysic", "processInitialInteract", newDESC3, false));
      LabelNode label2 = new LabelNode();
      m.instructions.add(label2);
      m.instructions.add(new InsnNode(4));
      m.instructions.add(new InsnNode(172));
      m.maxLocals = 4;
      m.localVariables.add(new LocalVariableNode("this", "L" + name.replace(".", "/") + ";", (String)null, label, label2, 0));
      m.localVariables.add(new LocalVariableNode("par1EntityPlayer", newMethodVar, (String)null, label, label2, 1));
      m.localVariables.add(new LocalVariableNode("stack", patch("Lnet/minecraft/item/ItemStack;"), (String)null, label, label2, 2));
      m.localVariables.add(new LocalVariableNode("hand", patch("Lnet/minecraft/util/EnumHand;"), (String)null, label, label2, 3));
      classNode.methods.add(m);
      ClassWriter writer = new ClassWriter(1);
      classNode.accept(writer);
      return writer.toByteArray();
   }

   public byte[] replaceMethodOnUpdate(String name, byte[] bytes) {
      String targetMethodName = patch("onUpdate");
      String targetDESC = patch("(Lnet/minecraft/entity/item/EntityItem;)V");
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(bytes);
      classReader.accept(classNode, 0);

      for(MethodNode m : classNode.methods) {
         if (m.name.equals(targetMethodName) && m.desc.equals("()V")) {
            m.instructions.clear();
            m.instructions.add(new VarInsnNode(25, 0));
            m.instructions.add(new MethodInsnNode(184, "com/creativemd/itemphysic/physics/ServerPhysic", "update", targetDESC, false));
            m.instructions.add(new InsnNode(177));
         }
      }

      ClassWriter writer = new ClassWriter(1);
      classNode.accept(writer);
      return writer.toByteArray();
   }
}
