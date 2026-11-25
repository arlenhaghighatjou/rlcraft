package com.TominoCZ.FBP;

import com.TominoCZ.FBP.block.FBPAnimationDummyBlock;
import com.TominoCZ.FBP.handler.FBPEventHandler;
import com.TominoCZ.FBP.handler.FBPGuiHandler;
import com.TominoCZ.FBP.handler.FBPKeyInputHandler;
import com.TominoCZ.FBP.keys.FBPKeyBindings;
import com.TominoCZ.FBP.particle.FBPParticleManager;
import com.google.common.base.Throwables;
import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SplittableRandom;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.lang3.StringUtils;

@Mod(
   clientSideOnly = true,
   modid = "fbp",
   acceptedMinecraftVersions = "[1.12,1.13)"
)
public class FBP {
   @Instance("fbp")
   public static FBP INSTANCE;
   public static final String MODID = "fbp";
   public static final ResourceLocation LOCATION_PARTICLE_TEXTURE = new ResourceLocation("textures/particle/particles.png");
   public static final ResourceLocation FBP_BUG = new ResourceLocation("fbp:textures/gui/bug.png");
   public static final ResourceLocation FBP_FBP = new ResourceLocation("fbp:textures/gui/fbp.png");
   public static final ResourceLocation FBP_WIDGETS = new ResourceLocation("fbp:textures/gui/widgets.png");
   public static File animBlacklistFile = null;
   public static File particleBlacklistFile = null;
   public static File floatingMaterialsFile = null;
   public static File config = null;
   public static int minAge;
   public static int maxAge;
   public static int particlesPerAxis;
   public static double scaleMult;
   public static double gravityMult;
   public static double rotationMult;
   public static double weatherParticleDensity;
   public static boolean enabled = true;
   public static boolean showInMillis = false;
   public static boolean infiniteDuration = false;
   public static boolean randomRotation;
   public static boolean cartoonMode;
   public static boolean spawnWhileFrozen;
   public static boolean spawnRedstoneBlockParticles;
   public static boolean randomizedScale;
   public static boolean randomFadingSpeed;
   public static boolean entityCollision;
   public static boolean bounceOffWalls;
   public static boolean lowTraction;
   public static boolean smartBreaking;
   public static boolean fancyPlaceAnim;
   public static boolean animSmoothLighting;
   public static boolean spawnPlaceParticles;
   public static boolean fancyRain;
   public static boolean fancySnow;
   public static boolean fancyFlame;
   public static boolean fancySmoke;
   public static boolean waterPhysics;
   public static boolean restOnFloor;
   public static boolean frozen;
   public List<String> blockParticleBlacklist;
   public List<String> blockAnimBlacklist;
   public List<Material> floatingMaterials;
   public static SplittableRandom random = new SplittableRandom();
   public static final Vec3d[] CUBE = new Vec3d[]{new Vec3d(1.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, -1.0D), new Vec3d(-1.0D, -1.0D, -1.0D), new Vec3d(-1.0D, -1.0D, 1.0D), new Vec3d(1.0D, -1.0D, 1.0D), new Vec3d(1.0D, -1.0D, -1.0D), new Vec3d(-1.0D, -1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 1.0D), new Vec3d(1.0D, 1.0D, 1.0D), new Vec3d(1.0D, -1.0D, 1.0D), new Vec3d(1.0D, -1.0D, -1.0D), new Vec3d(1.0D, 1.0D, -1.0D), new Vec3d(-1.0D, 1.0D, -1.0D), new Vec3d(-1.0D, -1.0D, -1.0D), new Vec3d(-1.0D, -1.0D, -1.0D), new Vec3d(-1.0D, 1.0D, -1.0D), new Vec3d(-1.0D, 1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 1.0D), new Vec3d(1.0D, -1.0D, 1.0D), new Vec3d(1.0D, 1.0D, 1.0D), new Vec3d(1.0D, 1.0D, -1.0D), new Vec3d(1.0D, -1.0D, -1.0D)};
   public static final Vec3d[] CUBE_NORMALS = new Vec3d[]{new Vec3d(0.0D, 1.0D, 0.0D), new Vec3d(0.0D, -1.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D)};
   public static VertexFormat POSITION_TEX_COLOR_LMAP_NORMAL;
   public static MethodHandle setSourcePos;
   public static FBPAnimationDummyBlock FBPBlock = new FBPAnimationDummyBlock();
   public static IRenderHandler fancyWeatherRenderer;
   public static IRenderHandler originalWeatherRenderer;
   public static FBPParticleManager fancyEffectRenderer;
   public static ParticleManager originalEffectRenderer;
   public FBPEventHandler eventHandler = new FBPEventHandler();
   public FBPGuiHandler guiHandler = new FBPGuiHandler();

   public FBP() {
      INSTANCE = this;
      POSITION_TEX_COLOR_LMAP_NORMAL = new VertexFormat();
      POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.POSITION_3F);
      POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.TEX_2F);
      POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.COLOR_4UB);
      POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.TEX_2S);
      POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.NORMAL_3B);
      this.blockParticleBlacklist = Collections.synchronizedList(new ArrayList());
      this.blockAnimBlacklist = Collections.synchronizedList(new ArrayList());
      this.floatingMaterials = Collections.synchronizedList(new ArrayList());
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent evt) {
      config = new File(evt.getModConfigurationDirectory() + "/FBP/Particle.properties");
      animBlacklistFile = new File(evt.getModConfigurationDirectory() + "/FBP/AnimBlockBlacklist.txt");
      particleBlacklistFile = new File(evt.getModConfigurationDirectory() + "/FBP/ParticleBlockBlacklist.txt");
      floatingMaterialsFile = new File(evt.getModConfigurationDirectory() + "/FBP/FloatingMaterials.txt");
      FBPKeyBindings.init();
      FMLCommonHandler.instance().bus().register(new FBPKeyInputHandler());
   }

   @EventHandler
   public void init(FMLInitializationEvent evt) {
      MinecraftForge.EVENT_BUS.register(this.eventHandler);
      FMLCommonHandler.instance().bus().register(this.eventHandler);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent evt) {
      MinecraftForge.EVENT_BUS.register(this.guiHandler);
      Lookup lookup = MethodHandles.publicLookup();

      try {
         setSourcePos = lookup.unreflectSetter(ReflectionHelper.findField(ParticleDigging.class, new String[]{"sourcePos", "sourcePos"}));
      } catch (Exception var4) {
         throw Throwables.propagate(var4);
      }
   }

   @SubscribeEvent
   public static void registerItems(Register<Block> event) {
      event.getRegistry().register(FBPBlock);
   }

   public static boolean isEnabled() {
      boolean result = enabled;
      if (!result) {
         frozen = false;
      }

      return result;
   }

   public static void setEnabled(boolean enabled) {
      if (enabled != enabled) {
         if (enabled) {
            fancyEffectRenderer.carryOver();
            Minecraft.getMinecraft().effectRenderer = fancyEffectRenderer;
            Minecraft.getMinecraft().world.provider.setWeatherRenderer(fancyWeatherRenderer);
         } else {
            Minecraft.getMinecraft().effectRenderer = originalEffectRenderer;
            Minecraft.getMinecraft().world.provider.setWeatherRenderer(originalWeatherRenderer);
         }
      }

      enabled = enabled;
   }

   public static boolean isDev() {
      return ((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")).booleanValue();
   }

   public boolean isBlacklisted(Block b, boolean particle) {
      return b == null ? true : (particle ? this.blockParticleBlacklist : this.blockAnimBlacklist).contains(b.getRegistryName().toString());
   }

   public boolean doesMaterialFloat(Material mat) {
      return this.floatingMaterials.contains(mat);
   }

   public void addToBlacklist(Block b, boolean particle) {
      if (b != null) {
         String name = b.getRegistryName().toString();
         if (!(particle ? this.blockParticleBlacklist : this.blockAnimBlacklist).contains(name)) {
            (particle ? this.blockParticleBlacklist : this.blockAnimBlacklist).add(name);
         }

      }
   }

   public void removeFromBlacklist(Block b, boolean particle) {
      if (b != null) {
         String name = b.getRegistryName().toString();
         if ((particle ? this.blockParticleBlacklist : this.blockAnimBlacklist).contains(name)) {
            (particle ? this.blockParticleBlacklist : this.blockAnimBlacklist).remove(name);
         }

      }
   }

   public void addToBlacklist(String name, boolean particle) {
      if (!StringUtils.isEmpty(name)) {
         for(ResourceLocation rl : Block.REGISTRY.getKeys()) {
            String s = rl.toString();
            if (s.equals(name)) {
               Block b = (Block)Block.REGISTRY.getObject(rl);
               if (b != Blocks.REDSTONE_BLOCK) {
                  this.addToBlacklist(b, particle);
               }
               break;
            }
         }

      }
   }

   public void resetBlacklist(boolean particle) {
      if (particle) {
         this.blockParticleBlacklist.clear();
      } else {
         this.blockAnimBlacklist.clear();
      }

   }
}
