package meldexun.renderlib.asm;

import java.util.function.Consumer;
import meldexun.asmutil.ASMUtil;
import meldexun.asmutil.transformer.clazz.AbstractClassTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class RenderLibClassTransformer extends AbstractClassTransformer implements IClassTransformer {
   public static final boolean OPTIFINE_DETECTED;

   public RenderLibClassTransformer() {
   }

   protected void registerTransformers() {
      this.registerMethodTransformer("buy", "a", "(Lvg;Lbxy;F)V", "net/minecraft/client/renderer/RenderGlobal", "renderEntities", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V", (methodNode) -> {
         ASMUtil.LOGGER.info("Transforming method: RenderGlobal#renderEntities(Entity, ICamera, float)");
         AbstractInsnNode targetNode1 = ASMUtil.findFirstMethodCall(methodNode, 184, "com/google/common/collect/Lists", "newArrayList", "()Ljava/util/ArrayList;", "com/google/common/collect/Lists", "newArrayList", "()Ljava/util/ArrayList;");
         targetNode1 = ASMUtil.findLastInsnByType(methodNode, 8, targetNode1);
         AbstractInsnNode popNode1 = ASMUtil.findFirstMethodCall(methodNode, 182, "bwx", "preDrawBatch", "()V", "net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher", "preDrawBatch", "()V", targetNode1);
         popNode1 = ASMUtil.findLastMethodCall(methodNode, 182, "rl", "c", "(Ljava/lang/String;)V", "net/minecraft/profiler/Profiler", "endStartSection", "(Ljava/lang/String;)V", popNode1);
         if (OPTIFINE_DETECTED) {
            popNode1 = ASMUtil.findLastMethodCall(methodNode, 184, "net/optifine/shaders/Shaders", "endEntities", "()V", "net/optifine/shaders/Shaders", "endEntities", "()V", popNode1);
            popNode1 = ASMUtil.findLastInsnByType(methodNode, 7, popNode1);
         }

         AbstractInsnNode var8 = ASMUtil.findLastInsnByType(methodNode, 8, popNode1);
         AbstractInsnNode targetNode2 = ASMUtil.findFirstMethodCall(methodNode, 182, "bwx", "preDrawBatch", "()V", "net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher", "preDrawBatch", "()V", var8);
         if (OPTIFINE_DETECTED) {
            targetNode2 = ASMUtil.findFirstMethodCall(methodNode, 184, "bxf", "updateTextRenderDistance", "()V", "net/minecraft/client/renderer/tileentity/TileEntitySignRenderer", "updateTextRenderDistance", "()V", targetNode2);
         }

         targetNode2 = ASMUtil.findFirstInsnByType(methodNode, 8, targetNode2);
         AbstractInsnNode popNode2 = ASMUtil.findFirstMethodCall(methodNode, 182, "bwx", "drawBatch", "(I)V", "net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher", "drawBatch", "(I)V", targetNode2);
         if (OPTIFINE_DETECTED) {
            popNode2 = ASMUtil.findLastMethodCall(methodNode, 182, "net/optifine/reflect/ReflectorMethod", "exists", "()Z", "net/optifine/reflect/ReflectorMethod", "exists", "()Z", popNode2);
         }

         popNode2 = ASMUtil.findLastInsnByType(methodNode, 8, popNode2);
         methodNode.instructions.insert(targetNode1, ASMUtil.listOf(new VarInsnNode(23, 3), new MethodInsnNode(184, "meldexun/renderlib/renderer/EntityRenderManager", "renderEntities", "(F)V", false), new JumpInsnNode(167, (LabelNode)var8)));
         methodNode.instructions.insert(targetNode2, ASMUtil.listOf(new VarInsnNode(23, 3), new MethodInsnNode(184, "meldexun/renderlib/renderer/TileEntityRenderManager", "renderTileEntities", "(F)V", false), new JumpInsnNode(167, (LabelNode)popNode2)));
      });
   }

   static {
      boolean flag = false;

      try {
         Class.forName("optifine.OptiFineClassTransformer", false, RenderLibPlugin.class.getClassLoader());
         flag = true;
      } catch (ClassNotFoundException var2) {
         ;
      }

      OPTIFINE_DETECTED = flag;
   }
}
