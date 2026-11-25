package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchChunkTicks extends PatchManager {
   public PatchChunkTicks() {
      super("Chunk Ticks");
      this.add(new Patch(this, "net.minecraft.world.WorldServer", 1) {
         public void patch(ClassNode clazzNode) {
            String rlChunkTickContainer_name = "rlChunkTickContainer";
            String rlChunkTickContainer_desc = "Lcom/charles445/rltweaker/hook/HookWorld$ChunkTickContainer;";
            FieldNode f_rlChunkTickContainer = new FieldNode(327680, 1, rlChunkTickContainer_name, rlChunkTickContainer_desc, (String)null, (Object)null);
            clazzNode.fields.add(f_rlChunkTickContainer);
            MethodNode m_updateBlocks = this.findMethod(clazzNode, new String[]{"updateBlocks", "updateBlocks"});
            if (m_updateBlocks == null) {
               throw new RuntimeException("Couldn't find updateBlocks or updateBlocks");
            } else {
               InsnList inject = new InsnList();
               inject.add(new VarInsnNode(25, 0));
               inject.add(new VarInsnNode(25, 0));
               inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookWorld", "onPreUpdateBlocks", "(Lnet/minecraft/world/WorldServer;)Lcom/charles445/rltweaker/hook/HookWorld$ChunkTickContainer;", false));
               inject.add(new FieldInsnNode(181, "net/minecraft/world/WorldServer", rlChunkTickContainer_name, rlChunkTickContainer_desc));
               this.insert(m_updateBlocks, this.first(m_updateBlocks), inject);
               inject = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_updateBlocks), 182, "enqueueRelightChecks", "enqueueRelightChecks");
               if (inject == null) {
                  throw new RuntimeException("Couldn't find call to enqueueRelightChecks or enqueueRelightChecks in updateBlocks");
               } else {
                  AbstractInsnNode var10 = ASMHelper.findPreviousInstruction(inject);
                  if (!(var10 instanceof VarInsnNode)) {
                     throw new RuntimeException("Previous instruction to enqueueRelightChecks was unexpectedly not a VarInsnNode");
                  } else {
                     int lvn_chunk_var = ((VarInsnNode)var10).var;
                     AbstractInsnNode var11 = TransformUtil.findNextCallWithOpcodeAndName(var10, 182, "onTick", "onTick");
                     if (var11 == null) {
                        throw new RuntimeException("Couldn't find call to onTick or onTick after enqueueRelightChecks in updateBlocks");
                     } else {
                        InsnList inject = new InsnList();
                        inject.add(new VarInsnNode(25, lvn_chunk_var));
                        inject.add(new VarInsnNode(25, 0));
                        inject.add(new FieldInsnNode(180, "net/minecraft/world/WorldServer", rlChunkTickContainer_name, rlChunkTickContainer_desc));
                        inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookWorld", "postBlockTickChunk", "(Lnet/minecraft/world/chunk/Chunk;Lcom/charles445/rltweaker/hook/HookWorld$ChunkTickContainer;)V", false));
                        this.insert(m_updateBlocks, var11, inject);
                     }
                  }
               }
            }
         }
      });
   }
}
