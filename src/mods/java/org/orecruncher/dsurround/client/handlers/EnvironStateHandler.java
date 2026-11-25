package org.orecruncher.dsurround.client.handlers;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilityDimensionInfo;
import org.orecruncher.dsurround.capabilities.CapabilitySeasonInfo;
import org.orecruncher.dsurround.capabilities.dimension.DimensionInfo;
import org.orecruncher.dsurround.capabilities.dimension.IDimensionInfo;
import org.orecruncher.dsurround.capabilities.season.ISeasonInfo;
import org.orecruncher.dsurround.capabilities.season.TemperatureRating;
import org.orecruncher.dsurround.client.handlers.scanners.BattleScanner;
import org.orecruncher.dsurround.client.handlers.scanners.CeilingCoverage;
import org.orecruncher.dsurround.client.weather.Weather;
import org.orecruncher.dsurround.event.DiagnosticEvent;
import org.orecruncher.dsurround.expression.ExpressionEngine;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.biome.BiomeInfo;
import org.orecruncher.dsurround.registry.biome.BiomeRegistry;
import org.orecruncher.dsurround.registry.item.ItemClass;
import org.orecruncher.lib.DiurnalUtils;
import org.orecruncher.lib.MinecraftClock;
import org.orecruncher.lib.DiurnalUtils.DayCycle;

@SideOnly(Side.CLIENT)
public class EnvironStateHandler extends EffectHandlerBase {
   protected final CeilingCoverage ceiling = new CeilingCoverage();
   private static final String[] scripts = new String[]{"'Dim: ' + dim.id + '/' + dim.name", "'Biome: ' + biome.name + ' (' + biome.id + '); Temp ' + biome.temperature + '/' + biome.temperatureValue + ' rainfall: ' + biome.rainfall + ' traits: ' + biome.traits", "'Weather: ' + IF(weather.isRaining,'rainfall: ' + weather.rainfall,'not raining') + IF(weather.isThundering,' thundering','') + ' Temp: ' + weather.temperature + '/' + weather.temperatureValue + ' ice: ' + IF(weather.canWaterFreeze, 'true', 'false') + ' ' + IF(weather.temperatureValue < 0.2, '(breath)', '')", "'Season: ' + season.season + IF(diurnal.isNight,' night',' day') + IF(player.isInside,' inside',' outside') + ' celestialAngle: ' + diurnal.celestialAngle", "'Player: Temp ' + player.temperature + '; health ' + player.health + '/' + player.maxHealth + '; food ' + player.food.level + '; saturation ' + player.food.saturation + IF(player.isHurt,' isHurt','') + IF(player.isHungry,' isHungry','') + ' pos: (' + player.X + ',' + player.Y + ',' + player.Z + ') light: ' + player.lightLevel", "'Village: ' + player.inVillage"};

   public EnvironStateHandler() {
      super("State Handler");
   }

   public void process(@Nonnull EntityPlayer player) {
      EnvironStateHandler.EnvironStateData data = EnvironStateHandler.EnvironState.data;
      World world = player.getEntityWorld();
      BiomeRegistry biomes = RegistryManager.BIOME;
      ISeasonInfo season = CapabilitySeasonInfo.getCapability(world);
      if (!Minecraft.getMinecraft().isGamePaused()) {
         ++data.tickCounter;
      }

      Weather.update();
      this.ceiling.update();
      data.clock.update(world);
      data.battle.update(player);
      data.dimInfo = CapabilityDimensionInfo.getCapability(world);
      data.playerBiome = biomes.getPlayerBiome(player, false);
      data.dimensionId = world.provider.getDimension();
      data.dimensionName = world.provider.getDimensionType().getName();
      data.playerPosition = new BlockPos(player.posX, player.getEntityBoundingBox().minY, player.posZ);
      data.inside = this.ceiling.isReallyInside();
      data.truePlayerBiome = biomes.getPlayerBiome(player, true);
      if (season != null) {
         data.playerTemperature = season.getPlayerTemperature();
         data.biomeTemperature = season.getBiomeTemperature(data.playerPosition);
      } else {
         data.playerTemperature = TemperatureRating.MILD;
         data.biomeTemperature = TemperatureRating.MILD;
      }

      data.armorStack = ItemClass.effectiveArmorStack(player);
      data.footArmorStack = ItemClass.footArmorStack(player);
      data.isUnderground = data.playerBiome == biomes.UNDERGROUND_INFO;
      data.isInSpace = data.playerBiome == biomes.OUTERSPACE_INFO;
      data.isInClouds = data.playerBiome == biomes.CLOUDS_INFO;
      int blockLight = world.getLightFor(EnumSkyBlock.BLOCK, data.playerPosition);
      int skyLight = world.getLightFor(EnumSkyBlock.SKY, data.playerPosition) - world.calculateSkylightSubtracted(1.0F);
      data.lightLevel = Math.max(blockLight, skyLight);
      data.dayCycle = DiurnalUtils.getCycle(world);
      ExpressionEngine.instance().reset();
   }

   private void reset() {
      EnvironStateHandler.EnvironState.data = new EnvironStateHandler.EnvironStateData();
   }

   public void onConnect() {
      this.reset();
      ExpressionEngine.instance();
   }

   public void onDisconnect() {
      this.reset();
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void diagnostics(DiagnosticEvent.Gather event) {
      event.output.add(TextFormatting.GREEN + "Minecraft Date: " + EnvironStateHandler.EnvironState.getClock().toString());

      for(String s : scripts) {
         String result = ExpressionEngine.instance().eval(s).toString();
         event.output.add(TextFormatting.YELLOW + result);
      }

      for(String s : ExpressionEngine.instance().getNaughtyList()) {
         event.output.add("BAD SCRIPT: " + s);
      }

      event.output.add(TextFormatting.RED + EnvironStateHandler.EnvironState.getBattleScanner().toString());
   }

   public static final class EnvironState {
      private static EnvironStateHandler.EnvironStateData data = new EnvironStateHandler.EnvironStateData();

      public EnvironState() {
      }

      public static MinecraftClock getClock() {
         return data.clock;
      }

      public static BattleScanner getBattleScanner() {
         return data.battle;
      }

      public static IDimensionInfo getDimensionInfo() {
         return data.dimInfo;
      }

      public static BiomeInfo getPlayerBiome() {
         return data.playerBiome;
      }

      public static BiomeInfo getTruePlayerBiome() {
         return data.truePlayerBiome;
      }

      public static String getBiomeName() {
         return getPlayerBiome().getBiomeName();
      }

      public static TemperatureRating getPlayerTemperature() {
         return data.playerTemperature;
      }

      public static TemperatureRating getBiomeTemperature() {
         return data.biomeTemperature;
      }

      public static int getDimensionId() {
         return data.dimensionId;
      }

      public static String getDimensionName() {
         return data.dimensionName;
      }

      public static EntityPlayer getPlayer() {
         return Minecraft.getMinecraft().player;
      }

      public static World getWorld() {
         return Minecraft.getMinecraft().world;
      }

      public static BlockPos getPlayerPosition() {
         return data.playerPosition;
      }

      public static boolean isPlayer(Entity entity) {
         if (entity == null) {
            return false;
         } else {
            EntityPlayer player = getPlayer();
            return player == null ? false : entity.getPersistentID().equals(player.getPersistentID());
         }
      }

      public static boolean isPlayerHurt() {
         if (ModOptions.player.playerHurtThreshold == 0.0F) {
            return false;
         } else {
            EntityPlayer player = getPlayer();
            return !player.isCreative() && player.getHealth() <= ModOptions.player.playerHurtThreshold * player.getMaxHealth();
         }
      }

      public static boolean isPlayerHungry() {
         if (ModOptions.player.playerHungerThreshold == 0) {
            return false;
         } else {
            EntityPlayer player = getPlayer();
            return !player.isCreative() && player.getFoodStats().getFoodLevel() <= ModOptions.player.playerHungerThreshold;
         }
      }

      public static boolean isPlayerBurning() {
         return getPlayer().isBurning();
      }

      public static boolean isPlayerSuffocating() {
         EntityPlayer player = getPlayer();
         return !player.isCreative() && player.getAir() <= 0;
      }

      public static boolean isPlayerFlying() {
         return getPlayer().capabilities.isFlying;
      }

      public static boolean isPlayerSprinting() {
         return getPlayer().isSprinting();
      }

      public static boolean isPlayerInLava() {
         return getPlayer().isInLava();
      }

      public static boolean isPlayerInvisible() {
         return getPlayer().isInvisible();
      }

      public static boolean isPlayerBlind() {
         return getPlayer().isPotionActive(MobEffects.BLINDNESS);
      }

      public static boolean isPlayerInWater() {
         return getPlayer().isInWater();
      }

      public static boolean isPlayerMoving() {
         EntityPlayer player = getPlayer();
         return player.distanceWalkedModified != player.prevDistanceWalkedModified;
      }

      public static boolean isPlayerInside() {
         return data.inside;
      }

      public static boolean isPlayerUnderground() {
         return data.isUnderground;
      }

      public static boolean isPlayerInSpace() {
         return data.isInSpace;
      }

      public static boolean isPlayerInClouds() {
         return data.isInClouds;
      }

      public static ItemStack getPlayerItemStack() {
         return data.armorStack;
      }

      public static ItemStack getPlayerFootArmorStack() {
         return data.footArmorStack;
      }

      public static boolean inVillage() {
         return data.inVillage;
      }

      public static void setInVillage(boolean f) {
         data.inVillage = f;
      }

      public static int getLightLevel() {
         return data.lightLevel;
      }

      public static int getTickCounter() {
         return data.tickCounter;
      }

      public static DayCycle getDayCycle() {
         return data.dayCycle;
      }

      public static float getPartialTick() {
         return Minecraft.getMinecraft().getRenderPartialTicks();
      }

      protected static void setData(EnvironStateHandler.EnvironStateData d) {
         data = d;
      }

      protected static EnvironStateHandler.EnvironStateData getData() {
         return data;
      }
   }

   protected static final class EnvironStateData {
      public BiomeInfo playerBiome;
      public BiomeInfo truePlayerBiome;
      public int dimensionId;
      public String dimensionName;
      public IDimensionInfo dimInfo;
      public BlockPos playerPosition;
      public TemperatureRating playerTemperature;
      public TemperatureRating biomeTemperature;
      public ItemStack armorStack;
      public ItemStack footArmorStack;
      public boolean inside;
      public boolean inVillage;
      public boolean isUnderground;
      public boolean isInSpace;
      public boolean isInClouds;
      public int lightLevel;
      public int tickCounter;
      public DayCycle dayCycle;
      public MinecraftClock clock;
      public BattleScanner battle;

      protected EnvironStateData() {
         this.playerBiome = RegistryManager.BIOME.WTF_INFO;
         this.truePlayerBiome = RegistryManager.BIOME.WTF_INFO;
         this.dimensionName = "";
         this.dimInfo = DimensionInfo.NONE;
         this.playerPosition = BlockPos.ORIGIN;
         this.playerTemperature = TemperatureRating.MILD;
         this.biomeTemperature = TemperatureRating.MILD;
         this.armorStack = ItemStack.EMPTY;
         this.footArmorStack = ItemStack.EMPTY;
         this.tickCounter = 1;
         this.dayCycle = DayCycle.NO_SKY;
         this.clock = new MinecraftClock();
         this.battle = new BattleScanner();
      }
   }
}
