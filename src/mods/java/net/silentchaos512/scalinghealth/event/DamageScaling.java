package net.silentchaos512.scalinghealth.event;

import gnu.trove.map.hash.THashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.silentchaos512.lib.config.ConfigMultiValueLineParser;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.api.ScalingHealthAPI;
import net.silentchaos512.scalinghealth.config.Config;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class DamageScaling {
   private static final Marker MARKER = MarkerManager.getMarker("DamageScaling");
   private static final String[] SOURCES_DEFAULT = new String[]{"inFire", "lightningBolt", "onFire", "lava", "hotFloor", "inWall", "cramming", "drown", "starve", "cactus", "fall", "flyIntoWall", "outOfWorld", "generic", "magic", "wither", "anvil", "fallingBlock", "dragonBreath", "fireworks"};
   private static final String SOURCES_COMMENT = "Set damage scaling by damage source. All vanilla sources should be included, but set to no scaling. Mod sources can be added too, you'll just need the damage type string. The number represents how steeply the damage scales. 0 means no scaling (vanilla), 1 means it will be proportional to difficulty/max health (whichever you set). The scaling number can be anything, although I recommend a non-negative number.";
   public static final DamageScaling INSTANCE = new DamageScaling();
   private float genericScale;
   private float difficultyWeight;
   private boolean affectHostileMobs;
   private boolean affectPassiveMobs;
   private DamageScaling.Mode scaleMode;
   private final Map<String, Float> scalingMap = new THashMap();
   private final Set<UUID> entityAttackedThisTick = new HashSet();

   private DamageScaling() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerHurt(LivingAttackEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (!entity.world.isRemote) {
         if (!entity.isEntityInvulnerable(event.getSource()) && entity.hurtResistantTime <= entity.maxHurtResistantTime / 2) {
            if (!this.entityAttackedThisTick.contains(entity.getPersistentID()) && (!(entity instanceof IMob) || this.affectHostileMobs) && (entity instanceof EntityPlayer || this.affectPassiveMobs)) {
               DamageSource source = event.getSource();
               if (source != null) {
                  float scale = ((Float)this.scalingMap.getOrDefault(source.getDamageType(), Float.valueOf(this.genericScale))).floatValue();
                  float affectedAmount = this.getEffectScale(entity);
                  float original = event.getAmount();
                  float change = scale * affectedAmount * original;
                  if (change != 0.0F) {
                     float newAmount = makeSane(event.getAmount() + change);
                     event.setCanceled(true);
                     this.entityAttackedThisTick.add(entity.getPersistentID());
                     entity.attackEntityFrom(event.getSource(), newAmount);
                     if (Config.Debug.debugMode && Config.Debug.logPlayerDamage) {
                        ScalingHealth.LOGGER.info(MARKER, "{} on {}: {} -> {} (scale={}, affected={}, change={})", source.damageType, entity.getName(), Float.valueOf(original), Float.valueOf(newAmount), Float.valueOf(scale), Float.valueOf(affectedAmount), Float.valueOf(change));
                     }
                  }

               }
            }
         }
      }
   }

   private float getEffectScale(EntityLivingBase entity) {
      switch(this.scaleMode) {
      case AREA_DIFFICULTY:
         return (float)ScalingHealthAPI.getAreaDifficulty(entity.world, entity.getPosition()) * this.difficultyWeight;
      case MAX_HEALTH:
         float baseHealth = entity instanceof EntityPlayer ? (float)Config.Player.Health.startingHealth : (float)entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
         return (entity.getMaxHealth() - baseHealth) / baseHealth;
      case PLAYER_DIFFICULTY:
         return (float)ScalingHealthAPI.getEntityDifficulty(entity) * this.difficultyWeight;
      default:
         throw new IllegalStateException("Unknown damage scaling mode: " + this.scaleMode);
      }
   }

   private static float makeSane(float scaledAmount) {
      if (scaledAmount < 0.0F) {
         return 0.0F;
      } else {
         return !Float.isFinite(scaledAmount) ? Float.MAX_VALUE : scaledAmount;
      }
   }

   @SubscribeEvent
   public void onServerTick(ServerTickEvent event) {
      this.entityAttackedThisTick.clear();
   }

   public void loadConfig(Configuration config) {
      String category = "main.player.damage";
      this.genericScale = config.getFloat("Generic Scale", "main.player.damage", 0.0F, -3.4028235E38F, Float.MAX_VALUE, "If the damage source is not in the \"Scale By Source\" list, this value is used instead.");
      this.difficultyWeight = config.getFloat("Difficulty Weight", "main.player.damage", 0.04F, 0.0F, 1000.0F, "How much each point of difficulty affects damage scaling. With the default value of 0.04 (1/25th) and max difficulty of 250, that's up to a 10x multiplier on added damage. So player's would take 11x damage at max difficulty, if the source scale is set to 1.0.");
      this.scaleMode = (DamageScaling.Mode)Config.INSTANCE.loadEnum("Scaling Mode", "main.player.damage", DamageScaling.Mode.class, DamageScaling.Mode.MAX_HEALTH, "Set what value we scale against. MAX_HEALTH scales to player's max health MINUS starting health. Defaults to MAX_HEALTH if an invalid string is entered.");
      this.affectHostileMobs = config.getBoolean("Affect Hostile Mobs", "main.player.damage", false, "Also apply damage scaling to hostile mobs when they take damage.");
      this.affectPassiveMobs = config.getBoolean("Affect Passive Mobs", "main.player.damage", false, "Also apply damage scaling to passive mobs when they take damage.");
      ConfigMultiValueLineParser parser = new ConfigMultiValueLineParser("Scale By Source", ScalingHealth.logHelper, "\\s", new Class[]{String.class, Float.class});
      this.scalingMap.clear();
      String[] defaults = new String[SOURCES_DEFAULT.length];

      for(int i = 0; i < defaults.length; ++i) {
         defaults[i] = SOURCES_DEFAULT[i] + " 0.0";
      }

      for(String str : config.getStringList("Scale By Source", "main.player.damage", defaults, "Set damage scaling by damage source. All vanilla sources should be included, but set to no scaling. Mod sources can be added too, you'll just need the damage type string. The number represents how steeply the damage scales. 0 means no scaling (vanilla), 1 means it will be proportional to difficulty/max health (whichever you set). The scaling number can be anything, although I recommend a non-negative number.")) {
         Object[] values = parser.parse(str);
         if (values != null) {
            this.scalingMap.put((String)values[0], (Float)values[1]);
         }
      }

   }

   public static enum Mode {
      MAX_HEALTH,
      PLAYER_DIFFICULTY,
      AREA_DIFFICULTY;

      private Mode() {
      }
   }
}
