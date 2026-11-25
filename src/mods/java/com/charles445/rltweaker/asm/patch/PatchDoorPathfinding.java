package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchDoorPathfinding extends PatchManager {
   public PatchDoorPathfinding() {
      super("Door Pathfinding");
      this.add(new Patch(this, "net.minecraft.pathfinding.WalkNodeProcessor", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_getPathNodeTypeRaw = this.findMethod(clazzNode, new String[]{"getPathNodeTypeRaw", "getPathNodeTypeRaw"});
            if (m_getPathNodeTypeRaw == null) {
               throw new RuntimeException("Couldn't find getPathNodeTypeRaw or getPathNodeTypeRaw");
            } else {
               AbstractInsnNode doorWoodAnchor = TransformUtil.findNextFieldWithOpcodeAndName(this.first(m_getPathNodeTypeRaw), 178, "DOOR_WOOD_CLOSED");
               InsnList inj = new InsnList();
               inj.add(new VarInsnNode(25, 1));
               inj.add(new VarInsnNode(21, 2));
               inj.add(new VarInsnNode(21, 3));
               inj.add(new VarInsnNode(21, 4));
               inj.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookMinecraft", "verifyPathNodeType", "(Lnet/minecraft/pathfinding/PathNodeType;Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/pathfinding/PathNodeType;", false));
               this.insert(m_getPathNodeTypeRaw, doorWoodAnchor, inj);
               doorWoodAnchor = TransformUtil.findNextFieldWithOpcodeAndName(this.first(m_getPathNodeTypeRaw), 178, "DOOR_IRON_CLOSED");
               inj = new InsnList();
               inj.add(new VarInsnNode(25, 1));
               inj.add(new VarInsnNode(21, 2));
               inj.add(new VarInsnNode(21, 3));
               inj.add(new VarInsnNode(21, 4));
               inj.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookMinecraft", "verifyPathNodeType", "(Lnet/minecraft/pathfinding/PathNodeType;Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/pathfinding/PathNodeType;", false));
               this.insert(m_getPathNodeTypeRaw, doorWoodAnchor, inj);
            }
         }
      });
   }
}
