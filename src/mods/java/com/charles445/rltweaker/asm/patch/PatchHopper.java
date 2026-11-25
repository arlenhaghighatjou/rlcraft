package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchHopper extends PatchManager {
   public PatchHopper() {
      super("Hopper");
      this.add(new Patch(this, "net.minecraft.tileentity.TileEntityHopper", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_getInventoryAtPosition = this.findMethod(clazzNode, new String[]{"getInventoryAtPosition", "getInventoryAtPosition"});
            if (m_getInventoryAtPosition == null) {
               throw new RuntimeException("Couldn't find getInventoryAtPosition or getInventoryAtPosition");
            } else {
               AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_getInventoryAtPosition), 182, "hasTileEntity");
               if (anchor == null) {
                  throw new RuntimeException("Couldn't find call to hasTileEntity in getInventoryAtPosition");
               } else {
                  anchor = ASMHelper.findNextInstruction(anchor);
                  if (anchor.getType() != 7) {
                     throw new RuntimeException("Unexpected lack of jump instruction after hasTileEntity");
                  } else {
                     LabelNode lvn_tileBlockEnd = ((JumpInsnNode)anchor).label;
                     anchor = TransformUtil.gotoLabel(m_getInventoryAtPosition, lvn_tileBlockEnd);
                     if (anchor == null) {
                        throw new RuntimeException("Couldn't follow label in getInventoryAtPosition");
                     } else {
                        anchor = ASMHelper.findNextInstruction(anchor);
                        LocalVariableNode lvn_iinventory = TransformUtil.findLocalVariableWithName(m_getInventoryAtPosition, "iinventory");
                        LocalVariableNode lvn_block = TransformUtil.findLocalVariableWithName(m_getInventoryAtPosition, "block");
                        LocalVariableNode lvn_state = TransformUtil.findLocalVariableWithName(m_getInventoryAtPosition, "state");
                        LocalVariableNode lvn_blockpos = TransformUtil.findLocalVariableWithName(m_getInventoryAtPosition, "blockpos");
                        if (lvn_iinventory != null && lvn_block != null && lvn_state != null && lvn_blockpos != null) {
                           InsnList inject = new InsnList();
                           inject.add(new VarInsnNode(25, 0));
                           inject.add(new VarInsnNode(25, lvn_blockpos.index));
                           inject.add(new VarInsnNode(25, lvn_block.index));
                           inject.add(new VarInsnNode(25, lvn_state.index));
                           inject.add(new VarInsnNode(25, lvn_iinventory.index));
                           inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookMinecraft", "hopperInventoryAtPosition", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/inventory/IInventory;)Lnet/minecraft/inventory/IInventory;", false));
                           inject.add(new VarInsnNode(58, lvn_iinventory.index));
                           this.insertBefore(m_getInventoryAtPosition, anchor, inject);
                        } else {
                           throw new RuntimeException("Necessay LVN are missing in getInventoryAtPosition");
                        }
                     }
                  }
               }
            }
         }
      });
      this.add(new Patch(this, "net.minecraftforge.items.VanillaInventoryCodeHooks", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_getItemHandler = this.findMethodWithDesc(clazzNode, "(Lnet/minecraft/world/World;DDDLnet/minecraft/util/EnumFacing;)Lorg/apache/commons/lang3/tuple/Pair;", new String[]{"getItemHandler"});
            if (m_getItemHandler == null) {
               throw new RuntimeException("Couldn't find getItemHandler with matching desc");
            } else {
               AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_getItemHandler), 182, "hasTileEntity");
               if (anchor == null) {
                  throw new RuntimeException("Couldn't find call to hasTileEntity in getItemHandler");
               } else {
                  anchor = ASMHelper.findNextInstruction(anchor);
                  if (anchor.getType() != 7) {
                     throw new RuntimeException("Unexpected lack of jump instruction after hasTileEntity");
                  } else {
                     LabelNode lvn_tileBlockEnd = ((JumpInsnNode)anchor).label;
                     anchor = TransformUtil.gotoLabel(m_getItemHandler, lvn_tileBlockEnd);
                     if (anchor == null) {
                        throw new RuntimeException("Couldn't follow label in getItemHandler");
                     } else {
                        anchor = ASMHelper.findNextInstruction(anchor);
                        LocalVariableNode lvn_destination = TransformUtil.findLocalVariableWithName(m_getItemHandler, "destination");
                        LocalVariableNode lvn_block = TransformUtil.findLocalVariableWithName(m_getItemHandler, "block");
                        LocalVariableNode lvn_state = TransformUtil.findLocalVariableWithName(m_getItemHandler, "state");
                        LocalVariableNode lvn_blockpos = TransformUtil.findLocalVariableWithName(m_getItemHandler, "blockpos");
                        if (lvn_destination != null && lvn_block != null && lvn_state != null && lvn_blockpos != null) {
                           InsnList inject = new InsnList();
                           inject.add(new VarInsnNode(25, 0));
                           inject.add(new VarInsnNode(25, lvn_blockpos.index));
                           inject.add(new VarInsnNode(25, lvn_block.index));
                           inject.add(new VarInsnNode(25, lvn_state.index));
                           inject.add(new VarInsnNode(25, lvn_destination.index));
                           inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookMinecraft", "hopperItemHandler", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/block/state/IBlockState;Lorg/apache/commons/lang3/tuple/Pair;)Lorg/apache/commons/lang3/tuple/Pair;", false));
                           inject.add(new VarInsnNode(58, lvn_destination.index));
                           this.insertBefore(m_getItemHandler, anchor, inject);
                        } else {
                           throw new RuntimeException("Necessay LVN are missing in getItemHandler");
                        }
                     }
                  }
               }
            }
         }
      });
   }
}
