package meldexun.renderlib.integration;

import meldexun.reflectionutil.ReflectionField;
import meldexun.reflectionutil.ReflectionMethod;
import meldexun.renderlib.asm.RenderLibClassTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class Optifine {
   private static final ReflectionMethod<Boolean> IS_SHADERS = new ReflectionMethod<Boolean>("Config", "isShaders", "isShaders", new Class[0]);
   private static final ReflectionField<Boolean> IS_SHADOW_PASS = new ReflectionField<Boolean>("net.optifine.shaders.Shaders", "isShadowPass", "isShadowPass");
   private static final ReflectionMethod<Void> NEXT_ENTITY = new ReflectionMethod<Void>("net.optifine.shaders.Shaders", "nextEntity", "nextEntity", new Class[]{Entity.class});
   private static final ReflectionField<Entity> RENDERED_ENTITY = new ReflectionField<Entity>(RenderGlobal.class, "renderedEntity", "renderedEntity");
   private static final ReflectionMethod<Boolean> IS_FAST_RENDER = new ReflectionMethod<Boolean>("Config", "isFastRender", "isFastRender", new Class[0]);
   private static final ReflectionMethod<Boolean> IS_ANTIALIASING = new ReflectionMethod<Boolean>("Config", "isAntialiasing", "isAntialiasing", new Class[0]);
   private static final ReflectionMethod<Void> BEGIN_ENTITIES_GLOWING = new ReflectionMethod<Void>("net.optifine.shaders.Shaders", "beginEntitiesGlowing", "beginEntitiesGlowing", new Class[0]);
   private static final ReflectionMethod<Void> END_ENTITIES_GLOWING = new ReflectionMethod<Void>("net.optifine.shaders.Shaders", "endEntitiesGlowing", "endEntitiesGlowing", new Class[0]);
   private static final ReflectionMethod<Void> NEXT_BLOCK_ENTITY = new ReflectionMethod<Void>("net.optifine.shaders.Shaders", "nextBlockEntity", "nextBlockEntity", new Class[]{TileEntity.class});

   public Optifine() {
   }

   public static boolean isOptifineDetected() {
      return RenderLibClassTransformer.OPTIFINE_DETECTED;
   }

   public static boolean isShaders() {
      return ((Boolean)IS_SHADERS.invoke((Object)null)).booleanValue();
   }

   public static boolean isShadowPass() {
      return IS_SHADOW_PASS.getBoolean((Object)null);
   }

   public static void nextEntity(Entity entity) {
      NEXT_ENTITY.invoke((Object)null, entity);
   }

   public static void setRenderedEntity(Entity entity) {
      RENDERED_ENTITY.set(Minecraft.getMinecraft().renderGlobal, entity);
   }

   public static boolean isFastRender() {
      return ((Boolean)IS_FAST_RENDER.invoke((Object)null)).booleanValue();
   }

   public static boolean isAntialiasing() {
      return ((Boolean)IS_ANTIALIASING.invoke((Object)null)).booleanValue();
   }

   public static void beginEntitiesGlowing() {
      BEGIN_ENTITIES_GLOWING.invoke((Object)null);
   }

   public static void endEntitiesGlowing() {
      END_ENTITIES_GLOWING.invoke((Object)null);
   }

   public static void nextBlockEntity(TileEntity tileEntity) {
      NEXT_BLOCK_ENTITY.invoke((Object)null, tileEntity);
   }
}
