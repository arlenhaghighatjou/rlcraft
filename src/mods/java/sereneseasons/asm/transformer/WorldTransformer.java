package sereneseasons.asm.transformer;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import sereneseasons.asm.ASMHelper;
import sereneseasons.asm.ObfHelper;

public class WorldTransformer implements IClassTransformer {
   private static final String[] CAN_SNOW_AT_NAMES = new String[]{"canSnowAt", "canSnowAt", "f"};
   private static final String[] CAN_BLOCK_FREEZE_NAMES = new String[]{"canBlockFreeze", "canBlockFreeze", "e"};
   private static final String[] IS_RAINING_AT_NAMES = new String[]{"isRainingAt", "isRainingAt", "B"};
   private static final String[] GET_BIOME_GEN_FOR_COORDS_NAMES = new String[]{"getBiome", "getBiome", "b"};

   public WorldTransformer() {
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      return transformedName.equals("net.minecraft.world.World") ? this.transformWorld(basicClass, !transformedName.equals(name)) : basicClass;
   }

   private byte[] transformWorld(byte[] bytes, boolean obfuscatedClass) {
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(bytes);
      classReader.accept(classNode, 0);
      List<String> successfulTransformations = Lists.newArrayList();

      for(MethodNode methodNode : classNode.methods) {
         if (ASMHelper.methodEquals(methodNode, CAN_SNOW_AT_NAMES, ObfHelper.createMethodDescriptor(obfuscatedClass, "Z", "net/minecraft/util/math/BlockPos", "Z"))) {
            InsnList insnList = new InsnList();
            insnList.add(new VarInsnNode(25, 0));
            insnList.add(new MethodInsnNode(184, "sereneseasons/api/season/SeasonHelper", "getSeasonState", ObfHelper.createMethodDescriptor(obfuscatedClass, "sereneseasons/api/season/ISeasonState", "net/minecraft/world/World"), false));
            insnList.add(new VarInsnNode(58, 3));
            insnList.add(new VarInsnNode(25, 0));
            insnList.add(new VarInsnNode(25, 1));
            insnList.add(new VarInsnNode(21, 2));
            insnList.add(new VarInsnNode(25, 3));
            insnList.add(new MethodInsnNode(184, "sereneseasons/season/SeasonASMHelper", "canSnowAtInSeason", ObfHelper.createMethodDescriptor(obfuscatedClass, "Z", "net/minecraft/world/World", "net/minecraft/util/math/BlockPos", "Z", "sereneseasons/api/season/ISeasonState"), false));
            insnList.add(new InsnNode(172));
            methodNode.instructions.clear();
            methodNode.instructions.insert(insnList);
            successfulTransformations.add(methodNode.name + " " + methodNode.desc);
         } else if (ASMHelper.methodEquals(methodNode, CAN_BLOCK_FREEZE_NAMES, ObfHelper.createMethodDescriptor(obfuscatedClass, "Z", "net/minecraft/util/math/BlockPos", "Z"))) {
            InsnList insnList = new InsnList();
            insnList.add(new VarInsnNode(25, 0));
            insnList.add(new MethodInsnNode(184, "sereneseasons/api/season/SeasonHelper", "getSeasonState", ObfHelper.createMethodDescriptor(obfuscatedClass, "sereneseasons/api/season/ISeasonState", "net/minecraft/world/World"), false));
            insnList.add(new VarInsnNode(58, 3));
            insnList.add(new VarInsnNode(25, 0));
            insnList.add(new VarInsnNode(25, 1));
            insnList.add(new VarInsnNode(21, 2));
            insnList.add(new VarInsnNode(25, 3));
            insnList.add(new MethodInsnNode(184, "sereneseasons/season/SeasonASMHelper", "canBlockFreezeInSeason", ObfHelper.createMethodDescriptor(obfuscatedClass, "Z", "net/minecraft/world/World", "net/minecraft/util/math/BlockPos", "Z", "sereneseasons/api/season/ISeasonState"), false));
            insnList.add(new InsnNode(172));
            methodNode.instructions.clear();
            methodNode.instructions.insert(insnList);
            successfulTransformations.add(methodNode.name + " " + methodNode.desc);
         } else if (ASMHelper.methodEquals(methodNode, IS_RAINING_AT_NAMES, ObfHelper.createMethodDescriptor(obfuscatedClass, "Z", "net/minecraft/util/math/BlockPos"))) {
            InsnList insnList = new InsnList();
            insnList.add(new VarInsnNode(25, 0));
            insnList.add(new MethodInsnNode(184, "sereneseasons/api/season/SeasonHelper", "getSeasonState", ObfHelper.createMethodDescriptor(obfuscatedClass, "sereneseasons/api/season/ISeasonState", "net/minecraft/world/World"), false));
            insnList.add(new VarInsnNode(58, 2));
            insnList.add(new VarInsnNode(25, 0));
            insnList.add(new VarInsnNode(25, 1));
            insnList.add(new VarInsnNode(25, 2));
            insnList.add(new MethodInsnNode(184, "sereneseasons/season/SeasonASMHelper", "isRainingAtInSeason", ObfHelper.createMethodDescriptor(obfuscatedClass, "Z", "net/minecraft/world/World", "net/minecraft/util/math/BlockPos", "sereneseasons/api/season/ISeasonState"), false));
            insnList.add(new InsnNode(172));
            MethodInsnNode invokeMethodNode = ASMHelper.getUniqueMethodInsnNode(methodNode, 182, ObfHelper.unmapType(obfuscatedClass, "net/minecraft/world/World"), GET_BIOME_GEN_FOR_COORDS_NAMES, ObfHelper.createMethodDescriptor(obfuscatedClass, "net/minecraft/world/biome/Biome", "net/minecraft/util/math/BlockPos"));
            AbstractInsnNode insertionPoint = methodNode.instructions.get(methodNode.instructions.indexOf(invokeMethodNode) - 2);
            methodNode.instructions.insertBefore(insertionPoint, insnList);
            ASMHelper.clearNextInstructions(methodNode, insertionPoint);
            successfulTransformations.add(methodNode.name + " " + methodNode.desc);
         }
      }

      if (successfulTransformations.size() != 3) {
         throw new RuntimeException("An error occurred transforming World. Applied transformations: " + successfulTransformations.toString());
      } else {
         ClassWriter writer = new ClassWriter(1);
         classNode.accept(writer);
         bytes = writer.toByteArray();
         return bytes;
      }
   }
}
