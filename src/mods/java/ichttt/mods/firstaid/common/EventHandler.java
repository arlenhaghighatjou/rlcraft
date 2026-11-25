package ichttt.mods.firstaid.common;

import com.creativemd.playerrevive.api.event.PlayerKilledEvent;
import com.creativemd.playerrevive.api.event.PlayerRevivedEvent;
import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.IDamageDistribution;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.apiimpl.FirstAidRegistryImpl;
import ichttt.mods.firstaid.common.config.ConfigEntry;
import ichttt.mods.firstaid.common.config.ExtraConfig;
import ichttt.mods.firstaid.common.damagesystem.PlayerDamageModel;
import ichttt.mods.firstaid.common.damagesystem.distribution.DamageDistribution;
import ichttt.mods.firstaid.common.damagesystem.distribution.HealthDistribution;
import ichttt.mods.firstaid.common.damagesystem.distribution.RandomDamageDistribution;
import ichttt.mods.firstaid.common.damagesystem.distribution.StandardDamageDistribution;
import ichttt.mods.firstaid.common.items.FirstAidItems;
import ichttt.mods.firstaid.common.network.MessageConfiguration;
import ichttt.mods.firstaid.common.network.MessageSyncDamageModel;
import ichttt.mods.firstaid.common.potion.FirstAidPotion;
import ichttt.mods.firstaid.common.potion.PotionPoisonPatched;
import ichttt.mods.firstaid.common.util.CommonUtils;
import ichttt.mods.firstaid.common.util.PlayerSizeHelper;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;

public class EventHandler {
   public static final EaglercraftRandom rand = new EaglercraftRandom();
   public static final SoundEvent HEARTBEAT = (SoundEvent)(new SoundEvent(new ResourceLocation("firstaid", "debuff.heartbeat"))).setRegistryName(new ResourceLocation("firstaid", "debuff.heartbeat"));
   public static final Potion MORPHINE = (new FirstAidPotion(false, 3549, "morphine")).setBeneficial();
   public static final Potion POISEN_PATCHED = PotionPoisonPatched.INSTANCE;
   @ObjectHolder("minecraft:resistance")
   public static final Potion RESISTANCE = null;
   public static final Map<EntityPlayer, Pair<Entity, RayTraceResult>> HIT_LIST = new WeakHashMap();

   public EventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onLivingHurt(LivingHurtEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (!entity.world.isRemote && entity instanceof EntityPlayer && !(entity instanceof FakePlayer)) {
         float amountToDamage = event.getAmount();
         EntityPlayer player = (EntityPlayer)entity;
         AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
         DamageSource source = event.getSource();
         if (amountToDamage != Float.MAX_VALUE && !Float.isNaN(amountToDamage) && amountToDamage != Float.POSITIVE_INFINITY) {
            boolean addStat = amountToDamage < 3.4028235E37F;
            IDamageDistribution damageDistribution = FirstAidRegistryImpl.INSTANCE.getDamageDistributionForSource(source);
            if (source.isProjectile()) {
               Pair<Entity, RayTraceResult> rayTraceResult = (Pair)HIT_LIST.remove(player);
               if (rayTraceResult != null) {
                  Entity entityProjectile = (Entity)rayTraceResult.getLeft();
                  EntityEquipmentSlot slot = PlayerSizeHelper.getSlotTypeForProjectileHit(entityProjectile, player);
                  if (slot != null) {
                     EnumPlayerPart[] possibleParts = CommonUtils.getPartArrayForSlot(slot);
                     damageDistribution = new StandardDamageDistribution(Collections.singletonList(Pair.of(slot, possibleParts)), false, true);
                  }
               }
            }

            if (damageDistribution == null) {
               damageDistribution = PlayerSizeHelper.getMeleeDistribution(player, source);
               if (damageDistribution == null) {
                  damageDistribution = RandomDamageDistribution.getDefault();
               }
            }

            DamageDistribution.handleDamageTaken(damageDistribution, damageModel, amountToDamage, player, source, addStat, true);
            event.setCanceled(true);
            HIT_LIST.remove(player);
         } else {
            damageModel.forEach((damageablePart) -> {
               damageablePart.currentHealth = 0.0F;
            });
            if (player instanceof EntityPlayerMP) {
               FirstAid.NETWORKING.sendTo(new MessageSyncDamageModel(damageModel, false), (EntityPlayerMP)player);
            }

            event.setCanceled(true);
            CommonUtils.killPlayer(damageModel, player, source);
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onProjectileImpact(ProjectileImpactEvent event) {
      RayTraceResult result = event.getRayTraceResult();
      if (result.typeOfHit == Type.ENTITY) {
         Entity entity = result.entityHit;
         if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            HIT_LIST.put((EntityPlayer)entity, Pair.of(event.getEntity(), event.getRayTraceResult()));
         }

      }
   }

   @SubscribeEvent
   public static void registerCapability(AttachCapabilitiesEvent<Entity> event) {
      Entity obj = (Entity)event.getObject();
      if (obj instanceof EntityPlayer && !(obj instanceof FakePlayer)) {
         EntityPlayer player = (EntityPlayer)obj;
         AbstractPlayerDamageModel damageModel = PlayerDamageModel.create();
         event.addCapability(CapProvider.IDENTIFIER, new CapProvider(damageModel));
         player.dataManager = new DataManagerWrapper(player, player.dataManager);
      }

   }

   @SubscribeEvent
   public static void registerItems(Register<Item> event) {
      FirstAidItems.registerItems(event.getRegistry());
   }

   @SubscribeEvent
   public static void registerPotion(Register<Potion> event) {
      event.getRegistry().register(MORPHINE);
      event.getRegistry().register(POISEN_PATCHED);
   }

   @SubscribeEvent
   public static void registerSound(Register<SoundEvent> event) {
      event.getRegistry().register(HEARTBEAT);
   }

   @SubscribeEvent
   public static void tickPlayers(PlayerTickEvent event) {
      if (event.phase == Phase.END && CommonUtils.isSurvivalOrAdventure(event.player)) {
         ((AbstractPlayerDamageModel)Objects.requireNonNull(event.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null))).tick(event.player.world, event.player);
         HIT_LIST.remove(event.player);
      }

   }

   @SubscribeEvent
   public static void tickWorld(WorldTickEvent event) {
      if (event.phase != Phase.END) {
         if (FirstAidConfig.externalHealing.sleepHealPercentage > 0.0D) {
            World world = event.world;
            if (!world.isRemote && world instanceof WorldServer && ((WorldServer)world).areAllPlayersAsleep()) {
               for(EntityPlayer player : world.playerEntities) {
                  AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
                  ((AbstractPlayerDamageModel)Objects.requireNonNull(damageModel, "damage model")).sleepHeal(player);
               }
            }

         }
      }
   }

   @SubscribeEvent
   public static void onLootTableLoad(LootTableLoadEvent event) {
      ResourceLocation tableName = event.getName();
      LootPool pool = null;
      int bandage = 0;
      int plaster = 0;
      int morphine = 0;
      if (tableName.equals(LootTableList.CHESTS_SPAWN_BONUS_CHEST)) {
         pool = event.getTable().getPool("main");
         bandage = 8;
         plaster = 16;
         morphine = 4;
      } else if (tableName.equals(LootTableList.CHESTS_STRONGHOLD_CORRIDOR) || tableName.equals(LootTableList.CHESTS_STRONGHOLD_CROSSING) || tableName.equals(LootTableList.CHESTS_ABANDONED_MINESHAFT)) {
         pool = event.getTable().getPool("main");
         bandage = 20;
         plaster = 24;
         morphine = 8;
      }

      if (pool != null) {
         pool.addEntry(new LootEntryItem(FirstAidItems.BANDAGE, bandage, 0, new SetCount[]{new SetCount(new LootCondition[0], new RandomValueRange(1.0F, 3.0F))}, new LootCondition[0], "firstaidbandage"));
         pool.addEntry(new LootEntryItem(FirstAidItems.PLASTER, plaster, 0, new SetCount[]{new SetCount(new LootCondition[0], new RandomValueRange(1.0F, 5.0F))}, new LootCondition[0], "firstaidplaster"));
         pool.addEntry(new LootEntryItem(FirstAidItems.MORPHINE, morphine, 0, new SetCount[]{new SetCount(new LootCondition[0], new RandomValueRange(1.0F, 2.0F))}, new LootCondition[0], "firstaidmorphine"));
      }

   }

   @SubscribeEvent
   public static void onConfigChange(OnConfigChangedEvent event) {
      if (event.getModID().equals("firstaid")) {
         Map<ConfigEntry<ExtraConfig.Sync>, ByteBuf> map = new LinkedHashMap();

         for(ConfigEntry<ExtraConfig.Sync> option : FirstAid.syncedConfigOptions) {
            if (option.hasRemoteData()) {
               ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
               option.writeToBuf(buf);
               map.put(option, buf);
               option.revert();
            }
         }

         ConfigManager.sync("firstaid", net.minecraftforge.common.config.Config.Type.INSTANCE);

         for(ConfigEntry<ExtraConfig.Sync> option : FirstAid.syncedConfigOptions) {
            option.updateOrigState();
            if (map.containsKey(option)) {
               ByteBuf buf = (ByteBuf)map.get(option);
               option.readFromBuf(buf);
               buf.release();
            }
         }

         event.setResult(Result.ALLOW);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onHeal(LivingHealEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (entity.hasCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null)) {
         event.setCanceled(true);
         if (!entity.world.isRemote && FirstAidConfig.externalHealing.allowOtherHealingItems) {
            float amount = event.getAmount();
            if (Arrays.stream(Thread.currentThread().getStackTrace()).anyMatch((stackTraceElement) -> {
               return stackTraceElement.getClassName().equals(FoodStats.class.getName());
            })) {
               if (FirstAidConfig.externalHealing.allowNaturalRegeneration) {
                  amount *= (float)FirstAidConfig.externalHealing.naturalRegenMultiplier;
               }
            } else {
               amount *= (float)FirstAidConfig.externalHealing.otherRegenMultiplier;
            }

            if (FirstAidConfig.debug) {
               CommonUtils.debugLogStacktrace("External healing: : " + amount);
            }

            HealthDistribution.distributeHealth(amount, (EntityPlayer)entity, true);
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onLogin(PlayerLoggedInEvent event) {
      if (!event.player.world.isRemote) {
         FirstAid.LOGGER.debug("Sending damage model to " + event.player.getName());
         AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(event.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
         if (damageModel.hasTutorial) {
            CapProvider.tutorialDone.add(event.player.getName());
         }

         EntityPlayerMP playerMP = (EntityPlayerMP)event.player;
         FirstAid.NETWORKING.sendTo(new MessageConfiguration(damageModel, !playerMP.connection.netManager.isLocalChannel()), playerMP);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onLogout(PlayerLoggedOutEvent event) {
      HIT_LIST.remove(event.player);
   }

   @SubscribeEvent
   public static void onWorldLoad(Load event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         world.getGameRules().setOrCreateGameRule("naturalRegeneration", Boolean.toString(FirstAidConfig.externalHealing.allowNaturalRegeneration));
      }

   }

   @SubscribeEvent
   public static void onDimensionChange(PlayerChangedDimensionEvent event) {
      if (!event.player.world.isRemote && event.player instanceof EntityPlayerMP) {
         FirstAid.NETWORKING.sendTo(new MessageSyncDamageModel((AbstractPlayerDamageModel)Objects.requireNonNull(event.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null)), true), (EntityPlayerMP)event.player);
      }

   }

   @SubscribeEvent
   public static void onPlayerBleedToDeath(PlayerKilledEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null);
      if (damageModel != null) {
         damageModel.stopWaitingForHelp(player);
      }

   }

   @SubscribeEvent
   public static void onPlayerRevived(PlayerRevivedEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null);
      if (damageModel != null) {
         damageModel.revivePlayer(player);
         damageModel.stopWaitingForHelp(player);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onPlayerRespawn(PlayerRespawnEvent event) {
      if (!event.isEndConquered() && !event.player.world.isRemote && event.player instanceof EntityPlayerMP) {
         AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(event.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
         damageModel.runScaleLogic(event.player);
         damageModel.forEach((damageablePart) -> {
            damageablePart.heal((float)damageablePart.getMaxHealth(), event.player, false);
         });
         damageModel.scheduleResync();
      }

   }
}
