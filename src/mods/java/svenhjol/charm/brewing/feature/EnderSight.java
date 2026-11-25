package svenhjol.charm.brewing.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.brewing.client.EnderSightSound;
import svenhjol.charm.brewing.message.MessageSetStructure;
import svenhjol.charm.brewing.potion.EnderSightPotion;
import svenhjol.meson.Feature;
import svenhjol.meson.handler.ClientHandler;
import svenhjol.meson.handler.NetworkHandler;
import svenhjol.meson.helper.EntityHelper;
import svenhjol.meson.helper.PlayerHelper;
import svenhjol.meson.helper.WorldHelper;

public class EnderSight extends Feature {
   public static EnderSightPotion potion;
   public static int duration;
   public static double endermanSpawnChance;
   public static boolean doShaderEffects;
   private BlockPos nearestStructure = null;
   private boolean hasEnderSight = false;
   private boolean isCloseAndPlaying = false;
   private int ticks = 0;
   private int clientTicks = 0;
   private int endermanSpawnDistance;
   private double minDist;

   public EnderSight() {
   }

   public String getDescription() {
      return "Brew an Eye of Ender with a Night Vision potion to make a Potion of Ender Sight.\nEnderman are attracted to your location and you might hear a ringing sound coming from the direction of a stronghold.\n";
   }

   public void configure() {
      super.configure();
      duration = this.propInt("Ender Sight duration", "Duration (in seconds) of Ender Sight effect when consumed.", 10);
      endermanSpawnChance = this.propDouble("Enderman spawn chance", "Chance (out of 1.0) of an Enderman spawning close by when experiencing the Ender Sight effect.", 0.05D);
      doShaderEffects = this.propBoolean("Client shader effect", "Invert the colours when under the effect of Ender Sight.", true);
      this.minDist = 2000.0D;
      this.endermanSpawnDistance = 5;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      potion = new EnderSightPotion();
      NetworkHandler.register(MessageSetStructure.class, Side.CLIENT);
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onClientTick(ClientTickEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      if (event.phase == Phase.START && mc.player != null && !mc.isGamePaused()) {
         EntityPlayer player = mc.player;
         World world = player.world;
         if (!this.hasEnderSight && mc.player.getActivePotionEffect(potion) != null) {
            this.hasEnderSight = true;
            mc.getSoundHandler().playSound(new EnderSightSound(CharmSounds.ENDER_WHISPERS, player, 0.25F, 0.8F));
            if (doShaderEffects) {
               mc.entityRenderer.loadShader(new ResourceLocation("charm", "shaders/ender_sight.json"));
            }
         } else if (this.hasEnderSight && player.getActivePotionEffect(potion) != null && world.provider.getDimension() == 0) {
            this.nearestStructure = ClientHandler.getNearestStronghold();
            if (this.nearestStructure != null) {
               double dist = this.nearestStructure.distanceSq(player.posX, player.posY, player.posZ);
               double d1 = MathHelper.positiveModulo((double)player.rotationYaw / 360.0D, 1.0D);
               double d2 = PlayerHelper.getLocationAngleToPlayer(player, this.nearestStructure) / 6.283185307179586D;
               double d0 = 0.5D - (d1 - 0.25D - d2);
               if (dist >= this.minDist) {
                  double d = MathHelper.positiveModulo(d0, 1.0D);
                  if ((d > 0.95D || d < 0.05D) && this.clientTicks % 4 + mc.world.rand.nextInt(4) == 0) {
                     float pitch = 0.9F + (0.1F - (float)(d > 0.95D ? 1.0D - d : d));
                     mc.getSoundHandler().playSound(new EnderSightSound(CharmSounds.ENDER_RESONANCE, player, 0.8F, pitch));
                  }
               } else if (!this.isCloseAndPlaying) {
                  this.isCloseAndPlaying = true;
                  mc.getSoundHandler().playSound(new EnderSightSound(CharmSounds.ENDER_ABOVE_STRONGHOLD, player, 0.8F, 1.0F));
               }

               ++this.clientTicks;
            }
         } else if (this.hasEnderSight && player.getActivePotionEffect(potion) == null) {
            this.hasEnderSight = false;
            this.isCloseAndPlaying = false;
            this.nearestStructure = null;
            if (doShaderEffects) {
               mc.entityRenderer.stopUseShader();
            }
         }
      }

   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && event.side.isServer() && event.player.getActivePotionEffect(potion) != null) {
         if (WorldHelper.canGenerateStructures(event.player.world) && WorldHelper.canGenerateStructure(event.player.world, WorldHelper.StructureType.STRONGHOLD) && event.player.world.provider.getDimension() == 0 && this.nearestStructure == null) {
            this.nearestStructure = event.player.getEntityWorld().findNearestStructure("Stronghold", event.player.getPosition(), false);
            if (this.nearestStructure != null) {
               NetworkHandler.INSTANCE.sendTo(new MessageSetStructure("Stronghold", this.nearestStructure), (EntityPlayerMP)event.player);
            }
         }

         if (this.ticks++ % 20 == 0 && (double)event.player.world.rand.nextFloat() < endermanSpawnChance) {
            EntityHelper.spawnEntityNearPlayer(event.player, this.endermanSpawnDistance, new ResourceLocation("enderman"));
            this.ticks = 0;
         }
      }

   }

   @SubscribeEvent
   public void onLoad(Load event) {
      WorldHelper.populateWorldSettings(event.getWorld());
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
