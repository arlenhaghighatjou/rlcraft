package sereneseasons.asm.transformer;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import sereneseasons.asm.ASMHelper;
import sereneseasons.asm.ObfHelper;

public class EntityRendererTransformer implements IClassTransformer {
   private static final String[] RENDER_RAIN_SNOW_NAMES = new String[]{"renderRainSnow", "renderRainSnow", "c"};
   private static final String[] ADD_RAIN_PARTICLES_NAMES = new String[]{"addRainParticles", "addRainParticles", "q"};
   private static final String[] GET_FLOAT_TEMPERATURE_NAMES = new String[]{"getTemperature", "getTemperature", "a"};
   private static final String[] CAN_RAIN_NAMES = new String[]{"canRain", "canRain", "d"};

   public EntityRendererTransformer() {
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      return transformedName.equals("net.minecraft.client.renderer.EntityRenderer") ? this.transformEntityRenderer(basicClass, !transformedName.equals(name)) : basicClass;
   }

   private byte[] transformEntityRenderer(byte[] bytes, boolean obfuscatedClass) {
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(bytes);
      classReader.accept(classNode, 0);
      List<String> successfulTransformations = Lists.newArrayList();

      for(MethodNode methodNode : classNode.methods) {
         if (ASMHelper.methodEquals(methodNode, RENDER_RAIN_SNOW_NAMES, "(F)V")) {
            int successCount = 0;
            int worldVarNum = -1;
            MethodInsnNode targetMethodInsnNode = ASMHelper.getUniqueMethodInsnNode(methodNode, 182, ObfHelper.unmapType(obfuscatedClass, "net/minecraft/world/biome/Biome"), CAN_RAIN_NAMES, ObfHelper.createMethodDescriptor(obfuscatedClass, "Z"));
            if (targetMethodInsnNode != null) {
               int targetInsnIndex = methodNode.instructions.indexOf(targetMethodInsnNode);
               int worldAloadIndex = targetInsnIndex - 7;
               if (!(methodNode.instructions.get(worldAloadIndex) instanceof VarInsnNode)) {
                  throw new RuntimeException("Failed to locate world var number whilst replacing biome.canRain() || biome.getEnableSnow()");
               }

               worldVarNum = ((VarInsnNode)methodNode.instructions.get(worldAloadIndex)).var;
               targetMethodInsnNode.setOpcode(184);
               targetMethodInsnNode.owner = "sereneseasons/season/SeasonASMHelper";
               targetMethodInsnNode.name = "shouldRenderRainSnow";
               targetMethodInsnNode.desc = ObfHelper.createMethodDescriptor(obfuscatedClass, "Z", "net/minecraft/world/World", "net/minecraft/world/biome/Biome");
               ASMHelper.clearNextInstructions(methodNode, methodNode.instructions.get(targetInsnIndex + 1), 3);
               methodNode.instructions.insertBefore(methodNode.instructions.get(targetInsnIndex - 1), new VarInsnNode(25, worldVarNum));
               --targetInsnIndex;
               int offset = 0;
               AbstractInsnNode currentInsn = methodNode.instructions.get(targetInsnIndex - offset);

               while(true) {
                  if (!(currentInsn instanceof VarInsnNode)) {
                     methodNode.instructions.remove(currentInsn);
                  } else {
                     VarInsnNode varInsnNode = (VarInsnNode)currentInsn;
                     if (varInsnNode.getOpcode() == 25 && varInsnNode.var != 5 && varInsnNode.var != 29) {
                        methodNode.instructions.remove(currentInsn);
                     } else {
                        ++offset;
                     }
                  }

                  currentInsn = methodNode.instructions.get(targetInsnIndex - offset);
                  if (currentInsn instanceof LabelNode) {
                     break;
                  }
               }

               ++successCount;
            }

            targetMethodInsnNode = ASMHelper.getUniqueMethodInsnNode(methodNode, 182, ObfHelper.unmapType(obfuscatedClass, "net/minecraft/world/biome/Biome"), GET_FLOAT_TEMPERATURE_NAMES, ObfHelper.createMethodDescriptor(obfuscatedClass, "F", "net/minecraft/util/math/BlockPos"));
            if (targetMethodInsnNode != null) {
               int targetInsnIndex = methodNode.instructions.indexOf(targetMethodInsnNode);
               targetMethodInsnNode.setOpcode(184);
               targetMethodInsnNode.owner = "sereneseasons/season/SeasonASMHelper";
               targetMethodInsnNode.name = "getFloatTemperature";
               targetMethodInsnNode.desc = ObfHelper.createMethodDescriptor(obfuscatedClass, "F", "net/minecraft/world/World", "net/minecraft/world/biome/Biome", "net/minecraft/util/math/BlockPos");
               methodNode.instructions.insertBefore(methodNode.instructions.get(targetInsnIndex - 2), new VarInsnNode(25, worldVarNum));
               ++successCount;
            }

            if (successCount == 2) {
               successfulTransformations.add(methodNode.name + " " + methodNode.desc);
            }
         } else if (ASMHelper.methodEquals(methodNode, ADD_RAIN_PARTICLES_NAMES, "()V")) {
            int successCount = 0;
            MethodInsnNode targetMethodInsnNode = ASMHelper.getUniqueMethodInsnNode(methodNode, 182, ObfHelper.unmapType(obfuscatedClass, "net/minecraft/world/biome/Biome"), CAN_RAIN_NAMES, ObfHelper.createMethodDescriptor(obfuscatedClass, "Z"));
            if (targetMethodInsnNode != null) {
               int targetInsnIndex = methodNode.instructions.indexOf(targetMethodInsnNode);
               targetMethodInsnNode.setOpcode(184);
               targetMethodInsnNode.owner = "sereneseasons/season/SeasonASMHelper";
               targetMethodInsnNode.name = "shouldAddRainParticles";
               targetMethodInsnNode.desc = ObfHelper.createMethodDescriptor(obfuscatedClass, "Z", "net/minecraft/world/World", "net/minecraft/world/biome/Biome");
               methodNode.instructions.insertBefore(methodNode.instructions.get(targetInsnIndex - 1), new VarInsnNode(25, 3));
               ++successCount;
            }

            targetMethodInsnNode = ASMHelper.getUniqueMethodInsnNode(methodNode, 182, ObfHelper.unmapType(obfuscatedClass, "net/minecraft/world/biome/Biome"), GET_FLOAT_TEMPERATURE_NAMES, ObfHelper.createMethodDescriptor(obfuscatedClass, "F", "net/minecraft/util/math/BlockPos"));
            if (targetMethodInsnNode != null) {
               int targetInsnIndex = methodNode.instructions.indexOf(targetMethodInsnNode);
               targetMethodInsnNode.setOpcode(184);
               targetMethodInsnNode.owner = "sereneseasons/season/SeasonASMHelper";
               targetMethodInsnNode.name = "getFloatTemperature";
               targetMethodInsnNode.desc = ObfHelper.createMethodDescriptor(obfuscatedClass, "F", "net/minecraft/world/World", "net/minecraft/world/biome/Biome", "net/minecraft/util/math/BlockPos");
               methodNode.instructions.insertBefore(methodNode.instructions.get(targetInsnIndex - 2), new VarInsnNode(25, 3));
               ++successCount;
            }

            if (successCount == 2) {
               successfulTransformations.add(methodNode.name + " " + methodNode.desc);
            }
         }
      }

      if (successfulTransformations.size() != 2) {
         throw new RuntimeException("An error occurred transforming EntityRenderer. Applied transformations: " + successfulTransformations.toString());
      } else {
         ClassWriter writer = new ClassWriter(1);
         classNode.accept(writer);
         bytes = writer.toByteArray();
         return bytes;
      }
   }
}
