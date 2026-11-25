package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchFixOldGorgon extends PatchManager {
   public PatchFixOldGorgon() {
      super("Old Gorgon");
      this.add(new Patch(this, "com.github.alexthe666.iceandfire.entity.EntityGorgon", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_onLivingUpdate = this.findMethod(clazzNode, new String[]{"onLivingUpdate", "onLivingUpdate"});
            if (m_onLivingUpdate == null) {
               this.cancelled = true;
               this.announce("Couldn't find onLivingUpdate or onLivingUpdate in EntityGorgon, skipping");
            } else {
               FieldInsnNode soundField = TransformUtil.findNextFieldWithOpcodeAndName(this.first(m_onLivingUpdate), 178, "GORGON_ATTACK");
               if (soundField == null) {
                  this.cancelled = true;
                  this.announce("Couldn't find any GORGON_ATTACK in EntityGorgon, skipping");
               } else if (!soundField.owner.equals("com/github/alexthe666/iceandfire/core/ModSounds")) {
                  this.cancelled = true;
                  this.announce("GORGON_ATTACK was not owned by ModSounds in onLivingUpdate in EntityGorgon, skipping");
               } else {
                  AbstractInsnNode anchor = ASMHelper.findNextInstructionWithOpcode(soundField, 187);
                  if (!(anchor instanceof TypeInsnNode)) {
                     this.cancelled = true;
                     this.announce("Couldn't find an instantiation at all in onLivingUpdate in EntityGorgon, skipping");
                  } else if (!((TypeInsnNode)anchor).desc.equals("com/github/alexthe666/iceandfire/entity/EntityStoneStatue")) {
                     this.cancelled = true;
                     this.announce("First instantiation found was not EntityStoneStatue in onLivingUpdate in EntityGorgon, patcher is confused, skipping");
                  } else {
                     String fName_rltweakerGorgonDelay = "rltweakerGorgonDelay";
                     FieldNode fNode_rltweakerGorgonDelay = new FieldNode(1, fName_rltweakerGorgonDelay, "I", (String)null, (Object)null);
                     clazzNode.fields.add(fNode_rltweakerGorgonDelay);
                     AbstractInsnNode huntSuper = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_onLivingUpdate), 183, "onLivingUpdate", "onLivingUpdate");
                     if (huntSuper == null) {
                        throw new RuntimeException("Couldn't find super call in onLivingUpdate in EntityGorgon");
                     } else {
                        InsnList inject = new InsnList();
                        inject.add(new VarInsnNode(25, 0));
                        inject.add(new VarInsnNode(25, 0));
                        inject.add(new FieldInsnNode(180, "com/github/alexthe666/iceandfire/entity/EntityGorgon", fName_rltweakerGorgonDelay, "I"));
                        inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookIAF", "decrementToZero", "(I)I", false));
                        inject.add(new FieldInsnNode(181, "com/github/alexthe666/iceandfire/entity/EntityGorgon", fName_rltweakerGorgonDelay, "I"));
                        this.insert(m_onLivingUpdate, huntSuper, inject);
                        AbstractInsnNode var10 = TransformUtil.findNextCallWithOpcodeAndName(anchor, 182, "spawnEntity", "spawnEntity");
                        if (var10 == null) {
                           throw new RuntimeException("Couldn't find spawnEntity or spawnEntity in onLivingUpdate in EntityGorgon");
                        } else {
                           AbstractInsnNode var11 = this.next(var10, 2);
                           this.removePreviousInsn(m_onLivingUpdate, var11);
                           this.removePreviousInsn(m_onLivingUpdate, var11);
                           this.removePreviousInsn(m_onLivingUpdate, var11);
                           this.removePreviousInsn(m_onLivingUpdate, var11);
                           this.removePreviousInsn(m_onLivingUpdate, var11);
                           AbstractInsnNode var12 = TransformUtil.findNextCallWithOpcodeAndName(var11, 182, "setDead", "setDead");
                           if (var12 == null) {
                              throw new RuntimeException("Couldn't find setDead or setDead in onLivingUpdate in EntityGorgon");
                           } else {
                              AbstractInsnNode var13 = var12.getNext();
                              this.removePreviousInsn(m_onLivingUpdate, var13);
                              this.removePreviousInsn(m_onLivingUpdate, var13);
                              this.removePreviousInsn(m_onLivingUpdate, var13);
                              AbstractInsnNode var14 = TransformUtil.findNextFieldWithOpcodeAndName(var13, 178, "gorgon");
                              if (var14 == null) {
                                 throw new RuntimeException("Couldn't find getstatic for gorgon in onLivingUpdate in EntityGorgon");
                              } else {
                                 AbstractInsnNode var15 = this.next(var14, 4);
                                 this.removePreviousInsn(m_onLivingUpdate, var15);
                                 this.removePreviousInsn(m_onLivingUpdate, var15);
                                 this.removePreviousInsn(m_onLivingUpdate, var15);
                                 this.removePreviousInsn(m_onLivingUpdate, var15);
                                 this.removePreviousInsn(m_onLivingUpdate, var15);
                                 this.removePreviousInsn(m_onLivingUpdate, var15);

                                 for(int i = 0; i < 25; ++i) {
                                    this.removePreviousInsn(m_onLivingUpdate, var15);
                                 }

                                 LocalVariableNode lvn_statue = TransformUtil.findLocalVariableWithName(m_onLivingUpdate, "statue");
                                 if (lvn_statue == null) {
                                    throw new RuntimeException("Couldn't find local variable statue in onLivingUpdate in EntityGorgon");
                                 } else {
                                    InsnList inject = new InsnList();
                                    inject.add(new VarInsnNode(25, 0));
                                    inject.add(new VarInsnNode(25, 0));
                                    inject.add(new VarInsnNode(25, lvn_statue.index));
                                    inject.add(new VarInsnNode(25, 0));
                                    inject.add(new FieldInsnNode(180, "com/github/alexthe666/iceandfire/entity/EntityGorgon", fName_rltweakerGorgonDelay, "I"));
                                    inject.add(new FieldInsnNode(178, "com/github/alexthe666/iceandfire/IceAndFire", "gorgon", "Lnet/minecraft/util/DamageSource;"));
                                    inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookIAF", "handleOldGorgon", "(Lnet/minecraft/entity/monster/EntityMob;Lnet/minecraft/entity/EntityLiving;ILnet/minecraft/util/DamageSource;)I", false));
                                    inject.add(new FieldInsnNode(181, "com/github/alexthe666/iceandfire/entity/EntityGorgon", fName_rltweakerGorgonDelay, "I"));
                                    this.insert(m_onLivingUpdate, var11, inject);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      });
   }
}
