package svenhjol.charm.world.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.world.client.render.RenderSpectre;
import svenhjol.charm.world.entity.EntitySpectre;
import svenhjol.charm.world.message.MessageSpectreDespawn;
import svenhjol.meson.Feature;
import svenhjol.meson.handler.NetworkHandler;
import svenhjol.meson.helper.EntityHelper;
import svenhjol.meson.helper.SoundHelper;

public class Spectre extends Feature {
   public static int weight;
   public static int min;
   public static int max;
   public static int despawnLight;
   public static int trackingRange;
   public static int spawnDepth;
   public static int weaknessDuration;
   public static int weaknessAmplifier;
   public static float maxHealth;
   public static float attackDamage;
   public static float movementSpeed;
   public static boolean applyCurse;
   public static int eggColor1;
   public static int eggColor2;

   public Spectre() {
   }

   public String getDescription() {
      return "Spectres spawn in the darkness at quite a low depth, and are almost invisible to see.\nWhen they touch the player they curse a weapon, tool or armor item and disappear.\nThey can be defeated by light and convert sand to soul sand on death.";
   }

   public void configure() {
      super.configure();
      despawnLight = MathHelper.clamp(this.propInt("Despawn light level", "Light level at which a Spectre disappears.", 8), 1, 16);
      spawnDepth = this.propInt("Spawn depth", "Maximum depth at which Spectres can spawn.", 24);
      weight = this.propInt("Spawn weight", "The higher this value, the more Spectres will spawn.", 100);
      applyCurse = this.propBoolean("Apply curse", "If true, spectres will curse armor or items belonging to the player.\nIf false, spectres will apply a weakness effect.", false);
      eggColor1 = 15527148;
      eggColor2 = 16772477;
      maxHealth = 0.5F;
      attackDamage = 0.01F;
      movementSpeed = 0.19F;
      min = 4;
      max = 8;
      trackingRange = 100;
      weaknessDuration = 2;
      weaknessAmplifier = 1;
      if (despawnLight == 0) {
         despawnLight = 1;
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      String name = "charm:spectre";
      EntityRegistry.registerModEntity(new ResourceLocation(name), EntitySpectre.class, name, 2, Charm.instance, trackingRange, 3, true, eggColor1, eggColor2);
      LootTableList.register(EntitySpectre.LOOT_TABLE);
      NetworkHandler.register(MessageSpectreDespawn.class, Side.CLIENT);
   }

   public void init(FMLInitializationEvent event) {
      EntityRegistry.addSpawn(EntitySpectre.class, weight, min, max, EnumCreatureType.MONSTER, EntityHelper.getBiomesWithMob(EntityZombie.class));
   }

   public void preInitClient(FMLPreInitializationEvent event) {
      RenderingRegistry.registerEntityRenderingHandler(EntitySpectre.class, RenderSpectre.FACTORY);
   }

   @SideOnly(Side.CLIENT)
   public static void effectDespawn(BlockPos pos) {
      World world = Minecraft.getMinecraft().world;

      for(int i = 0; i < 8; ++i) {
         double d0 = world.rand.nextGaussian() * 0.02D;
         double d1 = world.rand.nextGaussian() * 0.02D;
         double d2 = world.rand.nextGaussian() * 0.02D;
         double dx = (double)((float)pos.getX() + MathHelper.clamp(world.rand.nextFloat(), 0.25F, 0.75F));
         double dy = (double)((float)pos.getY() + 0.65F + MathHelper.clamp(world.rand.nextFloat(), 0.25F, 1.0F));
         double dz = (double)((float)pos.getZ() + MathHelper.clamp(world.rand.nextFloat(), 0.25F, 0.75F));
         world.spawnParticle(EnumParticleTypes.CLOUD, dx, dy, dz, d0, d1, d2, new int[0]);
      }

      SoundHelper.playSoundAtPos(world, pos, CharmSounds.SPECTRE_HIT_NO_SUBTITLE, SoundCategory.HOSTILE, 0.2F - world.rand.nextFloat() / 4.0F, 1.25F - world.rand.nextFloat() / 4.0F);
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
