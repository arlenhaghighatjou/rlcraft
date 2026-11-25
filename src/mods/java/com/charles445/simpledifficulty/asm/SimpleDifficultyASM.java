package com.charles445.simpledifficulty.asm;

import com.charles445.simpledifficulty.asm.helper.ASMHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.launchwrapper.IClassTransformer;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class SimpleDifficultyASM implements IClassTransformer {
   Logger logger = LogManager.getLogger("SimpleDifficultyASM");

   public SimpleDifficultyASM() {
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      return transformedName.startsWith("com.charles445.simpledifficulty.compat.mod.") ? this.redirectShadowed(basicClass) : basicClass;
   }

   public byte[] redirectShadowed(byte[] basicClass) {
      ClassNode clazzNode = ASMHelper.readClassFromBytes(basicClass);
      List<AnnotationNode> annotations = clazzNode.visibleAnnotations;
      if (annotations == null) {
         return basicClass;
      } else {
         boolean hasAnnotation = false;

         for(AnnotationNode annotation : annotations) {
            if (annotation != null && "Lcom/charles445/simpledifficulty/compat/HasShadows;".equals(annotation.desc)) {
               this.logger.info("Found class with shadows: " + clazzNode.name);
               if (clazzNode.interfaces != null) {
                  List<String> readdedInterfaces = new ArrayList();
                  Iterator<String> it = clazzNode.interfaces.iterator();

                  while(it.hasNext()) {
                     readdedInterfaces.add(this.swapOwner((String)it.next()));
                     it.remove();
                  }

                  for(String iface : readdedInterfaces) {
                     clazzNode.interfaces.add(iface);
                  }
               }

               if (clazzNode.fields != null) {
                  for(FieldNode fNode : clazzNode.fields) {
                     fNode.desc = this.swapDesc(fNode.desc);
                  }
               }

               if (clazzNode.methods != null) {
                  for(MethodNode mNode : clazzNode.methods) {
                     mNode.desc = this.swapDesc(mNode.desc);
                     if (mNode.localVariables != null) {
                        for(LocalVariableNode lvn : mNode.localVariables) {
                           lvn.desc = this.swapDesc(lvn.desc);
                        }
                     }

                     if (mNode.instructions != null) {
                        for(AbstractInsnNode anchor = mNode.instructions.getFirst(); anchor != null; anchor = anchor.getNext()) {
                           this.shadowInsnNode(anchor);
                        }
                     }
                  }
               }

               this.logger.info("Rewriting class with shadows: " + clazzNode.name);
               return ASMHelper.writeClassToBytes(clazzNode, 1);
            }
         }

         return basicClass;
      }
   }

   public void shadowInsnNode(AbstractInsnNode anchor) {
      int type = anchor.getType();
      if (type == 4) {
         FieldInsnNode node = (FieldInsnNode)anchor;
         node.owner = this.swapOwner(node.owner);
         node.desc = this.swapDesc(node.desc);
      } else if (type == 5) {
         MethodInsnNode node = (MethodInsnNode)anchor;
         node.owner = this.swapOwner(node.owner);
         node.desc = this.swapDesc(node.desc);
      } else if (type == 3) {
         TypeInsnNode node = (TypeInsnNode)anchor;
         node.desc = this.swapOwner(node.desc);
      } else if (type == 14) {
         FrameNode frame = (FrameNode)anchor;
         if (frame.local != null) {
            List<Object> replaceList = new ArrayList();
            Iterator<Object> it = replaceList.iterator();

            while(it.hasNext()) {
               for(int i = 0; i < frame.local.size(); ++i) {
                  Object o = frame.local.get(i);
                  if (o instanceof String) {
                     replaceList.add(this.swapOwner((String)o));
                  } else {
                     replaceList.add(o);
                  }

                  it.remove();
               }
            }

            for(Object o : replaceList) {
               frame.local.add(o);
            }
         }

         if (frame.stack != null) {
            List<Object> replaceList = new ArrayList();
            Iterator<Object> it = replaceList.iterator();

            while(it.hasNext()) {
               for(int i = 0; i < frame.stack.size(); ++i) {
                  Object o = frame.stack.get(i);
                  if (o instanceof String) {
                     replaceList.add(this.swapOwner((String)o));
                  } else {
                     replaceList.add(o);
                  }

                  it.remove();
               }
            }

            for(Object o : replaceList) {
               frame.stack.add(o);
            }
         }
      }

   }

   @Nullable
   private String swapOwner(String owner) {
      if (owner == null) {
         return null;
      } else {
         String swap = (String)ShadowMap.ownerMap.get(owner);
         return swap != null ? swap : owner;
      }
   }

   @Nullable
   private String swapDesc(String desc) {
      if (desc == null) {
         return null;
      } else {
         String result = desc;

         for(Entry<String, String> entry : ShadowMap.descMap.entrySet()) {
            result = result.replace((CharSequence)entry.getKey(), (CharSequence)entry.getValue());
         }

         return result;
      }
   }
}
