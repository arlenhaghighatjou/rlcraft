package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.ASMInfo;
import com.charles445.rltweaker.asm.util.ASMLogger;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchPathfindingChunkCache extends PatchManager {
   public PatchPathfindingChunkCache() {
      super("Pathfinding Chunk Cache");
      this.add(new Patch(this, "net.minecraft.world.ChunkCache", 1) {
         public void patch(ClassNode clazzNode) {
            if (ASMInfo.hasSponge) {
               ASMLogger.info("Sponge was detected, skipping patch");
               this.cancelled = true;
            } else {
               MethodNode m_init = this.findMethod(clazzNode, new String[]{"<init>"});
               if (m_init == null) {
                  throw new RuntimeException("Couldn't find init for ChunkCache... that's not good");
               } else {
                  MethodInsnNode toCall = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_init), 182, "getChunkFromChunkCoords", "getChunkFromChunkCoords");
                  if (toCall == null) {
                     throw new RuntimeException("Couldn't find getChunkFromChunkCoords or getChunkFromChunkCoords in ChunkCache init");
                  } else {
                     this.insertBefore(m_init, toCall, new VarInsnNode(25, 0));
                     toCall.setOpcode(184);
                     toCall.owner = "com/charles445/rltweaker/hook/HookMinecraft";
                     toCall.name = "cacheGetChunkFromChunkCoords";
                     toCall.desc = "(Lnet/minecraft/world/World;IILnet/minecraft/world/ChunkCache;)Lnet/minecraft/world/chunk/Chunk;";
                  }
               }
            }
         }
      });
      this.add(new Patch(this, "net.minecraft.pathfinding.PathNavigate", 1) {
         public void patch(ClassNode clazzNode) {
            if (ASMInfo.hasSponge) {
               ASMLogger.info("Sponge was detected, skipping patch");
               this.cancelled = true;
            } else {
               MethodNode m_getPathToPos = this.findMethod(clazzNode, new String[]{"getPathToPos", "getPathToPos"});
               if (m_getPathToPos == null) {
                  throw new RuntimeException("Couldn't find getPathToPos or getPathToPos");
               } else {
                  TypeInsnNode newNode = (TypeInsnNode)ASMHelper.findNextInstructionWithOpcode(this.first(m_getPathToPos), 187);
                  if (newNode == null) {
                     throw new RuntimeException("Couldn't find any instantiation in getPathToPos or getPathToPos");
                  } else {
                     while(!newNode.desc.equals("net/minecraft/world/ChunkCache")) {
                        newNode = (TypeInsnNode)ASMHelper.findNextInstructionWithOpcode(newNode, 187);
                        if (newNode == null) {
                           throw new RuntimeException("Failed to find ChunkCache instantiation new in getPathToPos or getPathToPo");
                        }
                     }

                     newNode.desc = "com/charles445/rltweaker/hook/NullableChunkCache";
                     MethodInsnNode callInit = TransformUtil.findNextCallWithOpcodeAndName(newNode, 183, "<init>");

                     while(!callInit.owner.equals("net/minecraft/world/ChunkCache")) {
                        callInit = TransformUtil.findNextCallWithOpcodeAndName(callInit, 183, "<init>");
                        if (callInit == null) {
                           throw new RuntimeException("Failed to find ChunkCache instantiation call in getPathToPos or getPathToPo");
                        }
                     }

                     callInit.owner = "com/charles445/rltweaker/hook/NullableChunkCache";
                     m_getPathToPos = this.findMethod(clazzNode, new String[]{"getPathToEntityLiving", "getPathToEntityLiving"});
                     newNode = (TypeInsnNode)ASMHelper.findNextInstructionWithOpcode(this.first(m_getPathToPos), 187);

                     while(!newNode.desc.equals("net/minecraft/world/ChunkCache")) {
                        newNode = (TypeInsnNode)ASMHelper.findNextInstructionWithOpcode(newNode, 187);
                        if (newNode == null) {
                           throw new RuntimeException("Failed to find ChunkCache instantiation new in getPathToEntityLiving or getPathToEntityLiving");
                        }
                     }

                     newNode.desc = "com/charles445/rltweaker/hook/NullableChunkCache";
                     callInit = TransformUtil.findNextCallWithOpcodeAndName(newNode, 183, "<init>");

                     while(!callInit.owner.equals("net/minecraft/world/ChunkCache")) {
                        callInit = TransformUtil.findNextCallWithOpcodeAndName(callInit, 183, "<init>");
                        if (callInit == null) {
                           throw new RuntimeException("Failed to find ChunkCache instantiation call in getPathToEntityLiving or getPathToEntityLiving");
                        }
                     }

                     callInit.owner = "com/charles445/rltweaker/hook/NullableChunkCache";
                  }
               }
            }
         }
      });
   }
}
