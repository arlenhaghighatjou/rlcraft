package net.silentchaos512.scalinghealth.event;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.silentchaos512.lib.util.AttributeHelper;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.api.event.BlightSpawnEvent;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.network.NetworkHandler;
import net.silentchaos512.scalinghealth.network.message.MessageDebugData;
import net.silentchaos512.scalinghealth.network.message.MessageMarkBlight;
import net.silentchaos512.scalinghealth.utils.EntityDifficultyChangeList;
import net.silentchaos512.scalinghealth.utils.EntityMatchList;
import net.silentchaos512.scalinghealth.utils.EquipmentTierMap;
import net.silentchaos512.scalinghealth.utils.MobPotionMap;
import net.silentchaos512.scalinghealth.utils.ModifierHandler;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;
import net.silentchaos512.scalinghealth.world.ScalingHealthSavedData;
import net.silentchaos512.utils.MathUtils;

public class DifficultyHandler {
   public static final String NBT_ENTITY_DIFFICULTY = "scalinghealth:difficulty";
   public static DifficultyHandler INSTANCE = new DifficultyHandler();
   private static int POTION_APPLY_TIME = 12000;
   private static final String[] POTION_DEFAULTS = new String[]{"minecraft:strength,30,1", "minecraft:speed,10,1", "minecraft:speed,50,2", "minecraft:fire_resistance,10,1", "minecraft:invisibility,25,1", "minecraft:resistance,30,1"};
   private int debugMobsProcessed;
   private int serverTicks;
   public MobPotionMap potionMap = new MobPotionMap();
   private EntityEquipmentSlot[] ORDERED_SLOTS = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
   public EquipmentTierMap mapHelmets = new EquipmentTierMap(5, EntityEquipmentSlot.HEAD);
   public EquipmentTierMap mapChestplates = new EquipmentTierMap(5, EntityEquipmentSlot.CHEST);
   public EquipmentTierMap mapLeggings = new EquipmentTierMap(5, EntityEquipmentSlot.LEGS);
   public EquipmentTierMap mapBoots = new EquipmentTierMap(5, EntityEquipmentSlot.FEET);
   public EquipmentTierMap mapMainhands = new EquipmentTierMap(5, EntityEquipmentSlot.MAINHAND);
   public EquipmentTierMap mapOffhands = new EquipmentTierMap(5, EntityEquipmentSlot.OFFHAND);

   public DifficultyHandler() {
   }

   public void initPotionMap() {
      this.potionMap.clear();
      String[] lines = Config.INSTANCE.getConfiguration().getStringList("Mob Potions", "main.mob.potion", POTION_DEFAULTS, "The potion effects that mobs can spawn with. You can add effects from other mods if you want to, or remove existing ones. Each line has 3 values separated by commas: the potion ID, the minimum difficulty (higher = less common), and the level (1 = level I, 2 = level II, etc).");

      for(String line : lines) {
         String[] params = line.split(",");
         if (params.length < 3) {
            ScalingHealth.logHelper.warn("Mob potion effects: malformed line (need 3 comma-separated values): " + line + "Ignoring entire line.", new Object[0]);
         } else {
            if (params.length > 3) {
               ScalingHealth.logHelper.warn("Mob potion effects: extra parameters in line: " + line + ". Ignoring extra parameters and processing the first 3.", new Object[0]);
            }

            int index = -1;
            String id = "null";

            Potion potion;
            int minDiff;
            int level;
            try {
               ++index;
               id = params[index];
               potion = (Potion)Potion.REGISTRY.getObject(new ResourceLocation(id));
               if (potion == null) {
                  throw new NullPointerException();
               }

               ++index;
               minDiff = Integer.parseInt(params[index]);
               ++index;
               level = Integer.parseInt(params[index]);
            } catch (NumberFormatException var13) {
               ScalingHealth.logHelper.warn("Mob potion effects: could not parse parameter " + index + " as integer. Ignoring entire line: " + line, new Object[0]);
               continue;
            } catch (NullPointerException var14) {
               ScalingHealth.logHelper.warn("Mob potion effects: potion \"" + id + "\" does not exist.", new Object[0]);
               continue;
            }

            this.potionMap.put(potion, minDiff, level - 1);
         }
      }

   }

   @SubscribeEvent
   public void onMobSpawn(LivingUpdateEvent event) {
      if (this.process(event.getEntityLiving())) {
         ++this.debugMobsProcessed;
      }

   }

   private boolean process(EntityLivingBase entity) {
      if (!entity.world.isRemote && !isProcessed(entity) && entity instanceof EntityLiving) {
         boolean difficultyEnabled = Config.Difficulty.maxValue > 0.0F;
         if (difficultyEnabled && canIncreaseEntityHealth(entity) && !entityBlacklistedFromHealthIncrease(entity)) {
            boolean makeBlight = this.increaseEntityHealth(entity);
            if (makeBlight && !BlightHandler.isBlight(entity)) {
               this.makeEntityBlight((EntityLiving)entity, ScalingHealth.random);
            }

            return true;
         }

         if (!difficultyEnabled && !entityBlacklistedFromBecomingBlight(entity)) {
            if (isAlwaysBlight(entity)) {
               this.makeEntityBlight((EntityLiving)entity, ScalingHealth.random);
            } else if (Config.Mob.Blight.fixedBlightChance && MathUtils.tryPercentage((double)Config.Mob.Blight.chanceMultiplier)) {
               this.makeEntityBlight((EntityLiving)entity, ScalingHealth.random);
            }

            entity.getEntityData().setShort("scalinghealth:difficulty", (short)-1);
            return true;
         }
      }

      return false;
   }

   public boolean recalculate(EntityLivingBase entity) {
      AttributeHelper.remove(entity, SharedMonsterAttributes.ATTACK_DAMAGE, ModifierHandler.MODIFIER_ID_DAMAGE);
      AttributeHelper.remove(entity, SharedMonsterAttributes.MAX_HEALTH, ModifierHandler.MODIFIER_ID_HEALTH);
      entity.getEntityData().setShort("scalinghealth:difficulty", (short)0);
      return this.process(entity);
   }

   private static boolean isProcessed(EntityLivingBase entity) {
      return entity.getEntityData().getShort("scalinghealth:difficulty") != 0;
   }

   private static boolean isAlwaysBlight(EntityLivingBase entity) {
      return Config.Mob.Blight.blightAlways && Config.Mob.Blight.blightAllList.matches(entity);
   }

   @SubscribeEvent
   public void onMobDeath(LivingDeathEvent event) {
      EntityLivingBase killed = event.getEntityLiving();
      DamageSource source = event.getSource();
      if (source.getTrueSource() instanceof EntityPlayer) {
         EntityDifficultyChangeList.DifficultyChanges changes = Config.Difficulty.DIFFICULTY_PER_KILL_BY_MOB.get(killed);
         EntityPlayer player = (EntityPlayer)source.getTrueSource();
         SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
         if (data != null) {
            boolean isBlight = BlightHandler.isBlight(killed);
            float amount = isBlight ? changes.onBlightKill : changes.onStandardKill;
            if (Config.Debug.debugMode) {
               ScalingHealth.logHelper.info("Killed " + (isBlight ? "Blight " : "") + killed.getName() + ": difficulty " + (amount > 0.0F ? "+" : "") + amount, new Object[0]);
            }

            data.incrementDifficulty((double)amount, true);
         }
      }

   }

   @SubscribeEvent
   public void onServerTick(ServerTickEvent event) {
      if (++this.serverTicks % 20 == 0 && Config.Debug.debugMode) {
         NetworkHandler.INSTANCE.sendToAll(new MessageDebugData());
      }

   }

   public static int debugGetMobsProcessed() {
      return INSTANCE.debugMobsProcessed;
   }

   public static float debugGetMobsProcessedRate() {
      return (float)INSTANCE.debugMobsProcessed / ((float)INSTANCE.serverTicks / 20.0F);
   }

   public static void debugHandleSyncMessage(MessageDebugData message) {
      INSTANCE.debugMobsProcessed = message.mobsProcessed;
   }

   private boolean increaseEntityHealth(EntityLivingBase entityLiving) {
      if (Config.Difficulty.maxValue <= 0.0F) {
         return false;
      } else {
         World world = entityLiving.world;
         float difficulty = (float)Config.Difficulty.AREA_DIFFICULTY_MODE.getAreaDifficulty(world, entityLiving.getPosition());
         float originalDifficulty = difficulty;
         float originalMaxHealth = entityLiving.getMaxHealth();
         EaglercraftRandom rand = ScalingHealth.random;
         boolean isHostile = entityLiving instanceof IMob;
         if (Config.Difficulty.DIFFICULTY_LUNAR_MULTIPLIERS_ENABLED && world.getWorldTime() % 24000L > 12000L) {
            int moonPhase = world.provider.getMoonPhase(world.getWorldTime()) % 8;
            float multi = Config.Difficulty.DIFFICULTY_LUNAR_MULTIPLIERS[moonPhase];
            difficulty *= multi;
         }

         boolean makeBlight = false;
         if (!entityBlacklistedFromBecomingBlight(entityLiving)) {
            float chance = Config.Mob.Blight.fixedBlightChance ? Config.Mob.Blight.chanceMultiplier : difficulty / Config.Difficulty.maxValue * Config.Mob.Blight.chanceMultiplier;
            if (isAlwaysBlight(entityLiving) || rand.nextFloat() < chance) {
               makeBlight = true;
               difficulty *= Config.Mob.Blight.difficultyMultiplier;
            }
         }

         entityLiving.getEntityData().setShort("scalinghealth:difficulty", (short)((int)difficulty));
         float totalDifficulty = difficulty;
         float baseMaxHealth = (float)entityLiving.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
         float healthMultiplier = isHostile ? Config.Mob.Health.hostileHealthMultiplier : Config.Mob.Health.peacefulHealthMultiplier;
         float genAddedHealth = difficulty * healthMultiplier;
         if (Config.Difficulty.statsConsumeDifficulty) {
            difficulty -= genAddedHealth;
         }

         if (difficulty > 0.0F) {
            float diffIncrease = 2.0F * healthMultiplier * difficulty * rand.nextFloat();
            if (Config.Difficulty.statsConsumeDifficulty) {
               difficulty -= diffIncrease;
            }

            genAddedHealth += diffIncrease;
         }

         float genAddedDamage = 0.0F;
         if (difficulty > 0.0F && !Config.Mob.damageBonusBlacklist.contains(entityLiving)) {
            float diffIncrease = difficulty * rand.nextFloat();
            genAddedDamage = diffIncrease * Config.Mob.damageMultiplier;
            if (Config.Mob.maxDamageBoost > 0.0F) {
               genAddedDamage = MathHelper.clamp(genAddedDamage, 0.0F, Config.Mob.maxDamageBoost);
            }

            if (Config.Difficulty.statsConsumeDifficulty) {
               difficulty -= genAddedDamage / Config.Mob.damageMultiplier;
            }
         }

         float potionChance = isHostile ? Config.Mob.hostilePotionChance : Config.Mob.passivePotionChance;
         if (difficulty > 0.0F && rand.nextFloat() < potionChance) {
            MobPotionMap.PotionEntry pot = this.potionMap.getRandom(rand, (int)difficulty);
            if (pot != null) {
               entityLiving.addPotionEffect(new PotionEffect(pot.potion, POTION_APPLY_TIME));
            }
         }

         float healthScaleDiff = Math.max(0.0F, baseMaxHealth - 20.0F);
         switch(Config.Mob.Health.healthScalingMode) {
         case ADD:
            ModifierHandler.setMaxHealth(entityLiving, (double)(genAddedHealth + baseMaxHealth), 0);
            break;
         case MULTI:
            float healthMulti = genAddedHealth / 20.0F;
            ModifierHandler.setMaxHealth(entityLiving, (double)(healthMulti + baseMaxHealth), 1);
            break;
         case MULTI_HALF:
            float healthMulti = genAddedHealth / (20.0F + healthScaleDiff * 0.5F);
            ModifierHandler.setMaxHealth(entityLiving, (double)(healthMulti + baseMaxHealth), 1);
            break;
         case MULTI_QUARTER:
            float healthMulti = genAddedHealth / (20.0F + healthScaleDiff * 0.75F);
            ModifierHandler.setMaxHealth(entityLiving, (double)(healthMulti + baseMaxHealth), 1);
            break;
         default:
            ScalingHealth.logHelper.fatal("Unknown mob health scaling mode: " + Config.Mob.Health.healthScalingMode.name(), new Object[0]);
         }

         ModifierHandler.addAttackDamage(entityLiving, (double)genAddedDamage, 0);
         if (entityLiving.getMaxHealth() != originalMaxHealth) {
            entityLiving.setHealth(entityLiving.getMaxHealth());
         }

         if (Config.Debug.debugMode && Config.Debug.logSpawns && originalDifficulty > 0.0F) {
            BlockPos pos = entityLiving.getPosition();
            String line = "Spawn debug: %s (%d, %d, %d): Difficulty=%.2f, Health +%.2f, Damage +%.2f";
            line = String.format(line, entityLiving.getName(), pos.getX(), pos.getY(), pos.getZ(), totalDifficulty, genAddedHealth, genAddedDamage);
            ScalingHealth.logHelper.info(line, new Object[0]);
         }

         return makeBlight;
      }
   }

   private void makeEntityBlight(EntityLiving entityLiving, EaglercraftRandom rand) {
      BlightSpawnEvent event = new BlightSpawnEvent.Pre(entityLiving, entityLiving.world, (float)entityLiving.posX, (float)entityLiving.posY, (float)entityLiving.posZ);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         BlightHandler.markBlight(entityLiving, true);
         BlightHandler.spawnBlightFire(entityLiving);
         BlightHandler.applyBlightPotionEffects(entityLiving);
         int highestTier = 4;
         int commonTier = Config.BLIGHT_EQUIPMENT_HIGHEST_COMMON_TIER;
         int tier = rand.nextInt(1 + commonTier);

         for(int j = 0; j < 4 - commonTier; ++j) {
            if (rand.nextFloat() < Config.BLIGHT_EQUIPMENT_TIER_UP_CHANCE) {
               ++tier;
            }
         }

         tier = MathHelper.clamp(tier, 0, 4);
         float pieceChance = Config.BLIGHT_EQUIPMENT_ARMOR_PIECE_CHANCE;

         for(EntityEquipmentSlot slot : this.ORDERED_SLOTS) {
            ItemStack oldEquipment = entityLiving.getItemStackFromSlot(slot);
            if (slot != EntityEquipmentSlot.HEAD && rand.nextFloat() > pieceChance) {
               break;
            }

            if (oldEquipment.isEmpty()) {
               ItemStack newEquipment = this.selectEquipmentForSlot(slot, tier);
               if (!newEquipment.isEmpty()) {
                  entityLiving.setItemStackToSlot(slot, newEquipment);
               }
            }
         }

         pieceChance = Config.BLIGHT_EQUIPMENT_HAND_PIECE_CHANCE;
         if (rand.nextFloat() > pieceChance) {
            ItemStack oldEquipment = entityLiving.getHeldItemMainhand();
            if (oldEquipment.isEmpty()) {
               ItemStack newEquipment = this.selectEquipmentForSlot(EntityEquipmentSlot.MAINHAND, tier);
               if (!newEquipment.isEmpty()) {
                  entityLiving.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, newEquipment);
               }
            }

            if (rand.nextFloat() > pieceChance) {
               oldEquipment = entityLiving.getHeldItemOffhand();
               if (oldEquipment.isEmpty()) {
                  ItemStack newEquipment = this.selectEquipmentForSlot(EntityEquipmentSlot.OFFHAND, tier);
                  if (!newEquipment.isEmpty()) {
                     entityLiving.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, newEquipment);
                  }
               }
            }
         }

         for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = entityLiving.getItemStackFromSlot(slot);
            if (!stack.isEmpty() && !stack.isItemEnchanted()) {
               EnchantmentHelper.addRandomEnchantment(rand, stack, 30, false);
            }
         }

         if (Config.Mob.Blight.superchargeCreepers && entityLiving instanceof EntityCreeper) {
            entityLiving.onStruckByLightning(new EntityLightningBolt(entityLiving.world, entityLiving.posX, entityLiving.posY, entityLiving.posZ, true));
            entityLiving.extinguish();
            entityLiving.heal(5.0F);
         }

         MessageMarkBlight message = new MessageMarkBlight(entityLiving, true);
         NetworkHandler.INSTANCE.sendToAllAround(message, new TargetPoint(entityLiving.dimension, entityLiving.posX, entityLiving.posY, entityLiving.posZ, 128.0D));
         MinecraftForge.EVENT_BUS.post(new BlightSpawnEvent.Post(entityLiving, entityLiving.world, (float)entityLiving.posX, (float)entityLiving.posY, (float)entityLiving.posZ));
      }
   }

   private static boolean entityBlacklistedFromHealthIncrease(EntityLivingBase entityLiving) {
      if (entityLiving == null) {
         return true;
      } else {
         boolean isBoss = !entityLiving.isNonBoss();
         boolean isHostile = entityLiving instanceof IMob;
         boolean isPassive = !isHostile;
         if ((!isHostile || Config.Mob.Health.hostileHealthMultiplier != 0.0F && Config.Mob.Health.allowHostile) && (!isPassive || Config.Mob.Health.peacefulHealthMultiplier != 0.0F && Config.Mob.Health.allowPeaceful) && (!isBoss || Config.Mob.Health.hostileHealthMultiplier != 0.0F && Config.Mob.Health.allowBoss)) {
            EntityMatchList blacklist = Config.Mob.Health.mobBlacklist;
            List<Integer> dimBlacklist = Config.Mob.Health.dimensionBlacklist;
            if (blacklist != null && dimBlacklist != null) {
               return blacklist.contains(entityLiving) || dimBlacklist.contains(Integer.valueOf(entityLiving.dimension));
            } else {
               return false;
            }
         } else {
            return true;
         }
      }
   }

   private static boolean canIncreaseEntityHealth(EntityLivingBase entity) {
      if (entity != null && !isProcessed(entity) && entity.world.getGameRules().getBoolean("ScalingHealthDifficulty")) {
         AttributeModifier modifier = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getModifier(ModifierHandler.MODIFIER_ID_HEALTH);
         return entity.ticksExisted > 1 && (modifier == null || modifier.getAmount() == 0.0D || Double.isNaN(modifier.getAmount()));
      } else {
         return false;
      }
   }

   private static boolean entityBlacklistedFromBecomingBlight(EntityLivingBase entityLiving) {
      if (entityLiving != null && !BlightHandler.isBlight(entityLiving)) {
         EntityMatchList blacklist = Config.Mob.Blight.blacklist;
         boolean isBoss = !entityLiving.isNonBoss();
         boolean isHostile = entityLiving instanceof IMob;
         boolean isPassive = !isHostile;
         return blacklist != null && (blacklist.contains(entityLiving) || isHostile && Config.Mob.Blight.blacklistHostiles || isPassive && Config.Mob.Blight.blacklistPassives || isBoss && Config.Mob.Blight.blacklistBosses);
      } else {
         return true;
      }
   }

   @SubscribeEvent
   public void onWorldTick(WorldTickEvent event) {
      if (event.world.getTotalWorldTime() % 20L == 0L) {
         ScalingHealthSavedData data = ScalingHealthSavedData.get(event.world);
         data.difficulty += (double)Config.Difficulty.perSecond;
         data.markDirty();
      }

   }

   public void initDefaultEquipment() {
      this.mapHelmets.put(new ItemStack(Items.LEATHER_HELMET), 0);
      this.mapHelmets.put(new ItemStack(Items.GOLDEN_HELMET), 1);
      this.mapHelmets.put(new ItemStack(Items.CHAINMAIL_HELMET), 2);
      this.mapHelmets.put(new ItemStack(Items.IRON_HELMET), 3);
      this.mapHelmets.put(new ItemStack(Items.DIAMOND_HELMET), 4);
      this.mapChestplates.put(new ItemStack(Items.LEATHER_CHESTPLATE), 0);
      this.mapChestplates.put(new ItemStack(Items.GOLDEN_CHESTPLATE), 1);
      this.mapChestplates.put(new ItemStack(Items.CHAINMAIL_CHESTPLATE), 2);
      this.mapChestplates.put(new ItemStack(Items.IRON_CHESTPLATE), 3);
      this.mapChestplates.put(new ItemStack(Items.DIAMOND_CHESTPLATE), 4);
      this.mapLeggings.put(new ItemStack(Items.LEATHER_LEGGINGS), 0);
      this.mapLeggings.put(new ItemStack(Items.GOLDEN_LEGGINGS), 1);
      this.mapLeggings.put(new ItemStack(Items.CHAINMAIL_LEGGINGS), 2);
      this.mapLeggings.put(new ItemStack(Items.IRON_LEGGINGS), 3);
      this.mapLeggings.put(new ItemStack(Items.DIAMOND_LEGGINGS), 4);
      this.mapBoots.put(new ItemStack(Items.LEATHER_BOOTS), 0);
      this.mapBoots.put(new ItemStack(Items.GOLDEN_BOOTS), 1);
      this.mapBoots.put(new ItemStack(Items.CHAINMAIL_BOOTS), 2);
      this.mapBoots.put(new ItemStack(Items.IRON_BOOTS), 3);
      this.mapBoots.put(new ItemStack(Items.DIAMOND_BOOTS), 4);
   }

   private ItemStack selectEquipmentForSlot(EntityEquipmentSlot slot, int tier) {
      tier = MathHelper.clamp(tier, 0, 4);
      switch(slot) {
      case CHEST:
         return this.mapChestplates.getRandom(tier);
      case FEET:
         return this.mapBoots.getRandom(tier);
      case HEAD:
         return this.mapHelmets.getRandom(tier);
      case LEGS:
         return this.mapLeggings.getRandom(tier);
      case MAINHAND:
         return this.mapMainhands.getRandom(tier);
      case OFFHAND:
         return this.mapOffhands.getRandom(tier);
      default:
         return ItemStack.EMPTY;
      }
   }
}
