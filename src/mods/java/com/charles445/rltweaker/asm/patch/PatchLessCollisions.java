package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.ClassDisplayer;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PatchLessCollisions extends PatchManager {
   public PatchLessCollisions() {
      super("Less Collisions");
      this.add(new Patch(this, "net.minecraft.world.World", 1) {
         public void patch(ClassNode c_World) {
            MethodNode m_getEntWithAABBExclEntity = this.findMethodWithDesc(c_World, "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;", new String[]{"getEntitiesWithinAABBExcludingEntity", "getEntitiesWithinAABBExcludingEntity"});
            if (m_getEntWithAABBExclEntity == null) {
               throw new RuntimeException("Couldn't find getEntitiesWithinAABBExcludingEntity or getEntitiesWithinAABBExcludingEntity with matching desc");
            } else {
               MethodInsnNode getAABBCall = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_getEntWithAABBExclEntity), 182, "getEntitiesInAABBexcluding", "getEntitiesInAABBexcluding");
               if (getAABBCall == null) {
                  System.out.println("Unexpected error, please show the below wall of text to the RLTweaker developer, thanks! Couldn't find getEntitiesInAABBexcluding or getEntitiesInAABBexcluding");
                  ClassDisplayer.instance.printMethod(m_getEntWithAABBExclEntity);
                  throw new RuntimeException("Couldn't find getEntitiesInAABBexcluding or getEntitiesInAABBexcluding");
               } else {
                  getAABBCall.setOpcode(184);
                  getAABBCall.owner = "com/charles445/rltweaker/hook/HookWorld";
                  getAABBCall.name = "getEntitiesInAABBexcluding";
                  getAABBCall.desc = "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;";
               }
            }
         }
      });
      this.add(new Patch(this, "net.minecraft.entity.EntityLivingBase", 1) {
         public void patch(ClassNode c_EntityLivingBase) {
            MethodNode m_collideWithNearbyEntities = this.findMethodWithDesc(c_EntityLivingBase, "()V", new String[]{"collideWithNearbyEntities", "collideWithNearbyEntities"});
            if (m_collideWithNearbyEntities == null) {
               throw new RuntimeException("Couldn't find collideWithNearbyEntities or collideWithNearbyEntities with matching desc");
            } else {
               MethodInsnNode getAABBCall = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_collideWithNearbyEntities), 182, "getEntitiesInAABBexcluding", "getEntitiesInAABBexcluding");
               if (getAABBCall == null) {
                  System.out.println("Unexpected error, please show the below wall of text to the RLTweaker developer, thanks! Couldn't find getEntitiesInAABBexcluding or getEntitiesInAABBexcluding");
                  ClassDisplayer.instance.printMethod(m_collideWithNearbyEntities);
                  throw new RuntimeException("Couldn't find getEntitiesInAABBexcluding or getEntitiesInAABBexcluding");
               } else {
                  getAABBCall.setOpcode(184);
                  getAABBCall.owner = "com/charles445/rltweaker/hook/HookWorld";
                  getAABBCall.name = "getEntitiesInAABBexcluding";
                  getAABBCall.desc = "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;";
               }
            }
         }
      });
   }
}
