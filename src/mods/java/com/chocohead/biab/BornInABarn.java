package com.chocohead.biab;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

@Name("Born in a Barn")
@SortingIndex(1001)
public final class BornInABarn implements IFMLLoadingPlugin, IClassTransformer {
   public BornInABarn() {
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
   }

   public String getModContainerClass() {
      return null;
   }

   public String[] getASMTransformerClass() {
      return new String[]{BornInABarn.class.getName()};
   }

   public String getAccessTransformerClass() {
      return null;
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      switch(transformedName.hashCode()) {
      case 1301807608:
         if (transformedName.equals("net.minecraft.village.Village")) {
            return poke(basicClass, name.equals(transformedName) ? "isWoodDoor" : "isWoodDoor", "world");
         }
         break;
      case 1625257654:
         if (transformedName.equals("net.minecraft.village.VillageCollection")) {
            return poke(basicClass, name.equals(transformedName) ? "isWoodDoor" : "isWoodDoor", "world");
         }
      }

      return basicClass;
   }

   private static byte[] poke(byte[] basicClass, String targetName, String changed) {
      ClassNode node = new ClassNode();
      (new ClassReader(basicClass)).accept(node, 0);
      boolean extra = ForgeVersion.getBuildVersion() >= 2623;

      for(MethodNode method : node.methods) {
         if (targetName.equals(method.name)) {
            AbstractInsnNode start = method.instructions.getFirst();
            boolean change = targetName.startsWith("func_");
            InsnList l = new InsnList();
            l.add(new VarInsnNode(25, 0));
            l.add(new FieldInsnNode(180, node.name, change ? changed : "world", "Lnet/minecraft/world/World;"));
            l.add(new VarInsnNode(25, 1));
            l.add(new MethodInsnNode(182, "net/minecraft/world/World", change ? "isBlockLoaded" : "isBlockLoaded", "(Lnet/minecraft/util/math/BlockPos;)Z", false));
            LabelNode normal = new LabelNode();
            l.add(new JumpInsnNode(154, normal));
            l.add(new InsnNode(3));
            l.add(new InsnNode(172));
            l.add(normal);
            l.add(new FrameNode(3, 0, (Object[])null, 0, (Object[])null));
            method.instructions.insert(start, l);
            break;
         }

         if (extra) {
            boolean change = targetName.startsWith("func_");
            if ((change ? "removeDeadAndOutOfRangeDoors" : "removeDeadAndOutOfRangeDoors").equals(method.name)) {
               splice("removeDeadAndOutOfRangeDoors", method.instructions, 50, 8);
            } else if ((change ? "addDoorsAround" : "addDoorsAround").equals(method.name)) {
               splice("addDoorsAround", method.instructions, 2, 10);
            }
         }
      }

      ClassWriter writer = new ClassWriter(0);
      node.accept(writer);
      return writer.toByteArray();
   }

   private static void splice(String name, InsnList list, int from, int length) {
      for(Iterator<AbstractInsnNode> it = list.iterator(from); length > 0; --length) {
         it.next();
         it.remove();
      }

   }
}
