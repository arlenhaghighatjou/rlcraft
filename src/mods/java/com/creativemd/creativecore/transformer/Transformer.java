package com.creativemd.creativecore.transformer;

import java.util.ArrayList;
import java.util.ListIterator;
import javax.annotation.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public abstract class Transformer {
   public static ArrayList<Transformer> transformers = new ArrayList();
   public final String className;

   public Transformer(String className) {
      transformers.add(this);
      this.className = TransformerNames.patchClassName(className);
   }

   public static boolean areNodesEqual(AbstractInsnNode node, AbstractInsnNode node2) {
      if (node.getClass() == node2.getClass() && node.getOpcode() == node2.getOpcode()) {
         if (node instanceof MethodInsnNode) {
            return ((MethodInsnNode)node).name.equals(((MethodInsnNode)node2).name) && ((MethodInsnNode)node).desc.equals(((MethodInsnNode)node2).desc) && ((MethodInsnNode)node).owner.equals(((MethodInsnNode)node2).owner);
         }

         if (node instanceof VarInsnNode) {
            return ((VarInsnNode)node).var == ((VarInsnNode)node2).var;
         }

         if (node instanceof InsnNode) {
            return true;
         }

         if (node instanceof TypeInsnNode) {
            return ((TypeInsnNode)node).desc.equals(((TypeInsnNode)node2).desc);
         }

         if (node instanceof JumpInsnNode) {
            return ((JumpInsnNode)node).label == ((JumpInsnNode)node2).label;
         }

         if (node instanceof LabelNode) {
            return node == node2;
         }

         if (node instanceof LineNumberNode) {
            return ((LineNumberNode)node).line == ((LineNumberNode)node2).line;
         }

         if (node instanceof FieldInsnNode) {
            return ((FieldInsnNode)node).name.equals(((FieldInsnNode)node2).name) && ((FieldInsnNode)node).desc.equals(((FieldInsnNode)node2).desc) && ((FieldInsnNode)node).owner.equals(((FieldInsnNode)node2).owner);
         }

         if (node instanceof LdcInsnNode) {
            return ((LdcInsnNode)node).cst.equals(((LdcInsnNode)node2).cst);
         }
      }

      return false;
   }

   public AbstractInsnNode findNode(InsnList instructions, AbstractInsnNode node) {
      ListIterator<AbstractInsnNode> iterator = instructions.iterator();

      while(iterator.hasNext()) {
         AbstractInsnNode insn = (AbstractInsnNode)iterator.next();
         if (areNodesEqual(insn, node)) {
            return insn;
         }
      }

      return null;
   }

   public int getIndexOfVar(MethodNode m, String desc, String signature) {
      desc = this.patchDESC(desc);
      signature = signature == null ? null : this.patchDESC(signature);

      for(LocalVariableNode local : m.localVariables) {
         if (local.desc.equals(desc) && (signature == null || signature.equals(local.signature))) {
            return local.index;
         }
      }

      return -1;
   }

   public int getIndexOfVar(MethodNode m, String desc) {
      return this.getIndexOfVar(m, desc, (String)null);
   }

   public LabelNode findPreviousLabel(AbstractInsnNode node) {
      while(node.getPrevious() != null) {
         if (node.getPrevious() instanceof LabelNode) {
            return (LabelNode)node.getPrevious();
         }

         node = node.getPrevious();
      }

      return null;
   }

   public LabelNode findNextLabel(AbstractInsnNode node) {
      while(node.getNext() != null) {
         if (node.getNext() instanceof LabelNode) {
            return (LabelNode)node.getNext();
         }

         node = node.getNext();
      }

      return null;
   }

   public void removeLabel(InsnList instructions, AbstractInsnNode node, int labels) {
      this.replaceLabel(instructions, node, (ArrayList)null, labels, false);
   }

   public void replaceLabel(InsnList instructions, AbstractInsnNode node, @Nullable ArrayList<AbstractInsnNode> replaceInstructions, int labels, boolean keepFirstLabel) {
      this.replaceLabelBefore(instructions, node, replaceInstructions, labels, 0, keepFirstLabel, true);
   }

   public void replaceLabelBefore(InsnList instructions, AbstractInsnNode node, @Nullable ArrayList<AbstractInsnNode> replaceInstructions, int labels, int labelsBefore, boolean keepFirstLabel, boolean deleteFrame) {
      ListIterator<AbstractInsnNode> iterator = instructions.iterator();
      LabelNode searchedLabel = null;
      ArrayList<LabelNode> foundLabels = new ArrayList();

      while(iterator.hasNext()) {
         AbstractInsnNode insn = (AbstractInsnNode)iterator.next();
         if (insn instanceof LabelNode) {
            foundLabels.add((LabelNode)insn);
         }

         if (areNodesEqual(insn, node)) {
            int index = foundLabels.size() - 1;
            index = index - labelsBefore;
            if (index >= 0) {
               searchedLabel = (LabelNode)foundLabels.get(index);
            }

            labels += labelsBefore;
            break;
         }
      }

      if (searchedLabel != null) {
         boolean found = false;
         int labelCounter = 0;
         iterator = instructions.iterator();

         while(true) {
            AbstractInsnNode insn;
            while(true) {
               if (!iterator.hasNext()) {
                  return;
               }

               insn = (AbstractInsnNode)iterator.next();
               if (found && insn instanceof LabelNode) {
                  ++labelCounter;
                  if (labelCounter >= labels) {
                     if (replaceInstructions != null) {
                        for(int i = 0; i < replaceInstructions.size(); ++i) {
                           instructions.insertBefore(insn, (AbstractInsnNode)replaceInstructions.get(i));
                        }
                     }

                     return;
                  }
               }

               if (insn != searchedLabel) {
                  break;
               }

               found = true;
               if (!keepFirstLabel) {
                  break;
               }
            }

            if (found && (deleteFrame || !(insn instanceof FrameNode))) {
               instructions.remove(insn);
            }
         }
      } else if (node instanceof LineNumberNode) {
         System.out.println("COULD NOT FIND NODE line=" + ((LineNumberNode)node).line);
      }

   }

   public String map(String dev, String obf) {
      return TransformerNames.obfuscated ? obf : dev;
   }

   public boolean is(String className) {
      return className.equals(this.className);
   }

   public String patchDESC(String desc) {
      return TransformerNames.patchDESC(desc);
   }

   public String patchClassName(String className) {
      return TransformerNames.patchClassName(className);
   }

   public String patchFieldName(String fieldName) {
      return TransformerNames.patchFieldName(fieldName, this.patchClassName(this.className.replace(".", "/")));
   }

   public String patchMethodName(String methodName, String desc) {
      return TransformerNames.patchMethodName(methodName, desc, this.patchClassName(this.className.replace(".", "/")));
   }

   public abstract void transform(ClassNode var1);

   public MethodNode findMethod(ClassNode node, String name, String desc) {
      if (TransformerNames.obfuscated) {
         name = this.patchMethodName(name, desc);
         desc = this.patchDESC(desc);
      }

      for(MethodNode m : node.methods) {
         if (m.name.equals(name) && m.desc.equals(desc)) {
            return m;
         }
      }

      return null;
   }

   public FieldNode findField(ClassNode node, String name) {
      for(FieldNode f : node.fields) {
         if (f.name.equals(name)) {
            return f;
         }
      }

      return null;
   }

   public void done() {
      if (transformers.size() == 0) {
         TransformerNames.emptyLists();
      }

   }
}
