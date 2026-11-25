package com.fantasticsource.mctools;

import com.fantasticsource.tools.datastructures.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PathedParticleManager {
   protected static final ResourceLocation PARTICLE_TEXTURES = new ResourceLocation("textures/particle/particles.png");
   protected static final Profiler profiler;
   protected static final TextureManager renderer;
   protected static HashMap<Pair<SourceFactor, DestFactor>, ArrayList<PathedParticle>> particles = new HashMap();

   public PathedParticleManager() {
   }

   public static void add(PathedParticle particle, SourceFactor sourceBlend, DestFactor destBlend) {
      ((ArrayList)particles.computeIfAbsent(new Pair(sourceBlend, destBlend), (o) -> {
         return new ArrayList();
      })).add(particle);
   }

   public static void update() {
      for(ArrayList<PathedParticle> list : particles.values()) {
         list.removeIf((particle) -> {
            particle.onUpdate();
            return !particle.isAlive();
         });
      }

      particles.values().removeIf(ArrayList::isEmpty);
   }

   public static void render(Entity entityIn, float partialTick) {
      if (particles.size() != 0) {
         float f1 = MathHelper.cos(entityIn.rotationYaw * 0.017453292F);
         float f2 = MathHelper.sin(entityIn.rotationYaw * 0.017453292F);
         float f3 = -f2 * MathHelper.sin(entityIn.rotationPitch * 0.017453292F);
         float f4 = f1 * MathHelper.sin(entityIn.rotationPitch * 0.017453292F);
         float f5 = MathHelper.cos(entityIn.rotationPitch * 0.017453292F);
         GlStateManager.depthMask(false);
         GlStateManager.enableBlend();
         renderer.bindTexture(PARTICLE_TEXTURES);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();

         for(Entry<Pair<SourceFactor, DestFactor>, ArrayList<PathedParticle>> entry : particles.entrySet()) {
            GlStateManager.blendFunc((SourceFactor)((Pair)entry.getKey()).getKey(), (DestFactor)((Pair)entry.getKey()).getValue());
            bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

            for(PathedParticle particle : (ArrayList)entry.getValue()) {
               particle.renderParticle(bufferbuilder, entityIn, partialTick, f1, f5, f2, f3, f4);
            }

            tessellator.draw();
         }

         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
      }
   }

   @SubscribeEvent
   public static void clientTick(ClientTickEvent event) {
      if (event.phase == Phase.START) {
         if (!Minecraft.getMinecraft().isGamePaused()) {
            profiler.startSection("FLib: Pathed Particles Update");
            update();
            profiler.endSection();
         }

      }
   }

   @SubscribeEvent
   public static void renderLast(RenderWorldLastEvent event) {
      profiler.startSection("FLib: Pathed Particles Render");
      render(Minecraft.getMinecraft().getRenderViewEntity(), event.getPartialTicks());
      profiler.endSection();
   }

   static {
      profiler = Minecraft.getMinecraft().mcProfiler;
      renderer = Minecraft.getMinecraft().renderEngine;
      MinecraftForge.EVENT_BUS.register(PathedParticleManager.class);
   }
}
