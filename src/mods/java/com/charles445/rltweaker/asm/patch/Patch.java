package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.ASMLogger;
import com.charles445.rltweaker.asm.util.ModTransformer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class Patch implements IPatch {
   public static Set<String> loadedTransformers = ConcurrentHashMap.newKeySet();
   protected String target;
   protected int flags;
   protected IPatchManager manager;
   protected boolean cancelled;

   public Patch(IPatchManager manager, String target, int flags) {
      this.manager = manager;
      this.target = target;
      this.flags = flags;
      this.cancelled = false;
   }

   public boolean hasModTransformer(ModTransformer mod) {
      String search = mod.getTransformerClassName();

      for(String transformer : loadedTransformers) {
         if (transformer.contains(search)) {
            return true;
         }
      }

      return false;
   }

   public String getTargetClazz() {
      return this.target;
   }

   public int getFlags() {
      return this.flags;
   }

   public IPatchManager getPatchManager() {
      return this.manager;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public abstract void patch(ClassNode var1);

   protected void announce(String s) {
      ASMLogger.info(s);
   }

   @Nullable
   protected MethodNode findMethod(ClassNode classNode, String... methodNames) {
      for(MethodNode m : classNode.methods) {
         for(String methodName : methodNames) {
            if (m.name.equals(methodName)) {
               return m;
            }
         }
      }

      return null;
   }

   @Nullable
   protected MethodNode findMethodWithDesc(ClassNode classNode, String desc, String... methodNames) {
      for(MethodNode m : classNode.methods) {
         if (m.desc.equals(desc)) {
            for(String methodName : methodNames) {
               if (m.name.equals(methodName)) {
                  return m;
               }
            }
         }
      }

      return null;
   }

   @Nullable
   protected AbstractInsnNode first(MethodNode methodNode) {
      return ASMHelper.findFirstInstruction(methodNode);
   }

   @Nullable
   protected AbstractInsnNode last(MethodNode methodNode) {
      return ASMHelper.getOrFindInstruction(methodNode.instructions.getLast(), true);
   }

   @Nullable
   protected AbstractInsnNode next(AbstractInsnNode node) {
      return node.getNext();
   }

   @Nullable
   protected AbstractInsnNode next(AbstractInsnNode node, int count) {
      AbstractInsnNode anchor = node;

      for(int i = 0; i < count; ++i) {
         anchor = anchor.getNext();
         if (anchor == null) {
            return anchor;
         }
      }

      return anchor;
   }

   @Nullable
   protected AbstractInsnNode nextInsn(AbstractInsnNode node) {
      return ASMHelper.findNextInstruction(node);
   }

   @Nullable
   protected AbstractInsnNode nextInsn(AbstractInsnNode node, int count) {
      AbstractInsnNode anchor = node;

      for(int i = 0; i < count; ++i) {
         anchor = ASMHelper.findNextInstruction(node);
         if (anchor == null) {
            return anchor;
         }
      }

      return anchor;
   }

   @Nullable
   protected AbstractInsnNode previous(AbstractInsnNode node) {
      return node.getPrevious();
   }

   @Nullable
   protected AbstractInsnNode previous(AbstractInsnNode node, int count) {
      AbstractInsnNode anchor = node;

      for(int i = 0; i < count; ++i) {
         anchor = anchor.getPrevious();
         if (anchor == null) {
            return anchor;
         }
      }

      return anchor;
   }

   @Nullable
   protected AbstractInsnNode previousInsn(AbstractInsnNode node) {
      return ASMHelper.findPreviousInstruction(node);
   }

   @Nullable
   protected AbstractInsnNode previousInsn(AbstractInsnNode node, int count) {
      AbstractInsnNode anchor = node;

      for(int i = 0; i < count; ++i) {
         anchor = ASMHelper.findPreviousInstruction(node);
         if (anchor == null) {
            return anchor;
         }
      }

      return anchor;
   }

   protected InsnList wrap(AbstractInsnNode... nodes) {
      InsnList wrapper = new InsnList();

      for(AbstractInsnNode node : nodes) {
         wrapper.add(node);
      }

      return wrapper;
   }

   protected void removePreviousInsn(MethodNode m, AbstractInsnNode node) {
      AbstractInsnNode toRemove = this.previousInsn(node);
      if (toRemove != null) {
         m.instructions.remove(toRemove);
      }

   }

   protected void removeNextInsn(MethodNode m, AbstractInsnNode node) {
      AbstractInsnNode toRemove = this.nextInsn(node);
      if (toRemove != null) {
         m.instructions.remove(toRemove);
      }

   }

   protected void insert(MethodNode methodNode, AbstractInsnNode anchor, InsnList wrapOrList) {
      methodNode.instructions.insert(anchor, wrapOrList);
   }

   protected void insertBefore(MethodNode methodNode, AbstractInsnNode anchor, InsnList wrapOrList) {
      methodNode.instructions.insertBefore(anchor, wrapOrList);
   }

   protected void insert(MethodNode methodNode, AbstractInsnNode anchor, AbstractInsnNode added) {
      methodNode.instructions.insert(anchor, added);
   }

   protected void insertBefore(MethodNode methodNode, AbstractInsnNode anchor, AbstractInsnNode added) {
      methodNode.instructions.insertBefore(anchor, added);
   }

   protected void insertInsn(MethodNode methodNode, AbstractInsnNode anchor, int opcode) {
      methodNode.instructions.insert(anchor, new InsnNode(opcode));
   }

   protected void insertInsnBefore(MethodNode methodNode, AbstractInsnNode anchor, int opcode) {
      methodNode.instructions.insertBefore(anchor, new InsnNode(opcode));
   }

   protected boolean methodReferencesOwner(MethodNode m, String owner) {
      for(AbstractInsnNode node = m.instructions.getFirst(); node != null; node = node.getNext()) {
         if (node.getType() == 5) {
            if (((MethodInsnNode)node).owner.equals(owner)) {
               return true;
            }
         } else if (node.getType() == 4 && ((FieldInsnNode)node).owner.equals(owner)) {
            return true;
         }
      }

      return false;
   }
}
