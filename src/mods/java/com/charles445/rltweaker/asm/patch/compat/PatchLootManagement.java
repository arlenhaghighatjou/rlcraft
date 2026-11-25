package com.charles445.rltweaker.asm.patch.compat;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchLootManagement extends PatchManager {
   public PatchLootManagement() {
      super("Loot Management");
      this.add(new Patch(this, "net.minecraft.world.storage.loot.LootTableManager$Loader", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_loadLootTable = this.findMethod(clazzNode, new String[]{"loadLootTable", "loadLootTable"});
            if (m_loadLootTable == null) {
               throw new RuntimeException("Couldn't find loadLootTable in LootTableManager$Loader");
            } else if (m_loadLootTable.localVariables == null) {
               throw new RuntimeException("Couldn't find any local variables in loadLootTable in LootTableManager$Loader");
            } else {
               LocalVariableNode lvn_file1 = TransformUtil.findLocalVariableWithDesc(m_loadLootTable, "Ljava/io/File;");
               if (lvn_file1 == null) {
                  throw new RuntimeException("Couldn't find local variable node for File in loadLootTable in LootTableManager$Loader");
               } else {
                  AbstractInsnNode anchor;
                  for(anchor = this.first(m_loadLootTable); anchor != null && (anchor.getOpcode() != 58 || ((VarInsnNode)anchor).var != lvn_file1.index); anchor = anchor.getNext()) {
                     ;
                  }

                  if (anchor == null) {
                     throw new RuntimeException("Couldn't find earliest ASTORE for File in loadLootTable in LootTableManager$Loader");
                  } else {
                     InsnList inject = new InsnList();
                     inject.add(new VarInsnNode(25, lvn_file1.index));
                     inject.add(new VarInsnNode(25, 1));
                     inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/compat/HookLootManagement", "fixLootFilePath", "(Ljava/io/File;Lnet/minecraft/util/ResourceLocation;)Ljava/io/File;", false));
                     inject.add(new VarInsnNode(58, lvn_file1.index));
                     this.insert(m_loadLootTable, anchor, inject);
                  }
               }
            }
         }
      });
   }
}
