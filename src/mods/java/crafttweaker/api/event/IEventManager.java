package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.util.IEventHandler;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.events.IEventManager")
@ZenRegister
public interface IEventManager {
   @ZenMethod
   IEventHandle onPlayerLoggedOut(IEventHandler<PlayerLoggedOutEvent> var1);

   @ZenMethod
   IEventHandle onPlayerLoggedIn(IEventHandler<PlayerLoggedInEvent> var1);

   @ZenMethod
   void clear();

   @ZenMethod
   IEventHandle onPlayerCrafted(IEventHandler<PlayerCraftedEvent> var1);

   @ZenMethod
   IEventHandle onPlayerSmelted(IEventHandler<PlayerSmeltedEvent> var1);

   @ZenMethod
   IEventHandle onPlayerChangedDimension(IEventHandler<PlayerChangedDimensionEvent> var1);

   @ZenMethod
   IEventHandle onPlayerRespawn(IEventHandler<PlayerRespawnEvent> var1);

   @ZenMethod
   IEventHandle onPlayerAttackEntity(IEventHandler<PlayerAttackEntityEvent> var1);

   @ZenMethod
   IEventHandle onPlayerAdvancement(IEventHandler<PlayerAdvancementEvent> var1);

   @ZenMethod
   IEventHandle onPlayerBonemeal(IEventHandler<PlayerBonemealEvent> var1);

   @ZenMethod
   IEventHandle onPlayerInteractEntity(IEventHandler<PlayerInteractEntityEvent> var1);

   @ZenMethod
   IEventHandle onPlayerPickupItem(IEventHandler<PlayerPickupItemEvent> var1);

   @ZenMethod
   IEventHandle onPlayerFillBucket(IEventHandler<PlayerFillBucketEvent> var1);

   @ZenMethod
   IEventHandle onPlayerDeathDrops(IEventHandler<PlayerDeathDropsEvent> var1);

   @ZenMethod
   IEventHandle onPlayerInteract(IEventHandler<PlayerInteractEvent> var1);

   @ZenMethod
   IEventHandle onPlayerOpenContainer(IEventHandler<PlayerOpenContainerEvent> var1);

   @ZenMethod
   IEventHandle onPlayerPickupXp(IEventHandler<PlayerPickupXpEvent> var1);

   @ZenMethod
   IEventHandle onPlayerSleepInBed(IEventHandler<PlayerSleepInBedEvent> var1);

   @ZenMethod
   IEventHandle onPlayerWakeUp(IEventHandler<PlayerWakeUpEvent> var1);

   @ZenMethod
   IEventHandle onPlayerUseHoe(IEventHandler<PlayerUseHoeEvent> var1);

   @ZenMethod
   IEventHandle onEntityLivingUseItem(IEventHandler<EntityLivingUseItemEvent> var1);

   @ZenMethod
   IEventHandle onEntityLivingUseItemStart(IEventHandler<EntityLivingUseItemEvent.Start> var1);

   @ZenMethod
   IEventHandle onEntityLivingUseItemStop(IEventHandler<EntityLivingUseItemEvent.Stop> var1);

   @ZenMethod
   IEventHandle onEntityLivingUseItemTick(IEventHandler<EntityLivingUseItemEvent.Tick> var1);

   @ZenMethod
   IEventHandle onEntityLivingUseItemFinish(IEventHandler<EntityLivingUseItemEvent.Finish> var1);

   @ZenMethod
   IEventHandle onEntityStruckByLightning(IEventHandler<EntityStruckByLightningEvent> var1);

   @ZenMethod
   IEventHandle onEnderTeleport(IEventHandler<EnderTeleportEvent> var1);

   @ZenMethod
   IEventHandle onEntityLivingAttacked(IEventHandler<EntityLivingAttackedEvent> var1);

   @ZenMethod
   IEventHandle onEntityLivingDeath(IEventHandler<EntityLivingDeathEvent> var1);

   @ZenMethod
   IEventHandle onEntityLivingFall(IEventHandler<EntityLivingFallEvent> var1);

   @ZenMethod
   IEventHandle onEntityLivingHurt(IEventHandler<EntityLivingHurtEvent> var1);

   @ZenMethod
   IEventHandle onEntityLivingJump(IEventHandler<EntityLivingJumpEvent> var1);

   @ZenMethod
   IEventHandle onEntityLivingDeathDrops(IEventHandler<EntityLivingDeathDropsEvent> var1);

   @ZenMethod
   IEventHandle onItemExpire(IEventHandler<ItemExpireEvent> var1);

   @ZenMethod
   IEventHandle onItemToss(IEventHandler<ItemTossEvent> var1);

   @ZenMethod
   IEventHandle onPlayerAnvilRepair(IEventHandler<PlayerAnvilRepairEvent> var1);

   @ZenMethod
   IEventHandle onPlayerAnvilUpdate(IEventHandler<PlayerAnvilUpdateEvent> var1);

   @ZenMethod
   IEventHandle onPlayerSetSpawn(IEventHandler<PlayerSetSpawnEvent> var1);

   @ZenMethod
   IEventHandle onPlayerDestroyItem(IEventHandler<PlayerDestroyItemEvent> var1);

   @ZenMethod
   IEventHandle onPlayerBrewedPotion(IEventHandler<PlayerBrewedPotionEvent> var1);

   @ZenMethod
   IEventHandle onPlayerTick(IEventHandler<PlayerTickEvent> var1);

   @ZenMethod
   IEventHandle onClientTick(IEventHandler<ClientTickEvent> var1);

   @ZenMethod
   IEventHandle onServerTick(IEventHandler<ServerTickEvent> var1);

   @ZenMethod
   IEventHandle onRenderTick(IEventHandler<RenderTickEvent> var1);

   @ZenMethod
   IEventHandle onWorldTick(IEventHandler<WorldTickEvent> var1);

   @ZenMethod
   IEventHandle onBlockBreak(IEventHandler<BlockBreakEvent> var1);

   @ZenMethod
   IEventHandle onBlockHarvestDrops(IEventHandler<BlockHarvestDropsEvent> var1);

   @ZenMethod
   IEventHandle onPlayerBreakSpeed(IEventHandler<PlayerBreakSpeedEvent> var1);

   @ZenMethod
   IEventHandle onPlayerRightClickBlock(IEventHandler<PlayerRightClickBlockEvent> var1);

   @ZenMethod
   IEventHandle onPlayerInteractBlock(IEventHandler<PlayerRightClickBlockEvent> var1);

   @ZenMethod
   IEventHandle onCommand(IEventHandler<CommandEvent> var1);

   @ZenMethod
   IEventHandle onCheckSpawn(IEventHandler<EntityLivingSpawnEvent.EntityLivingExtendedSpawnEvent> var1);

   @ZenMethod
   IEventHandle onSpecialSpawn(IEventHandler<EntityLivingSpawnEvent.EntityLivingExtendedSpawnEvent> var1);

   @ZenMethod
   IEventHandle onAllowDespawn(IEventHandler<EntityLivingSpawnEvent> var1);

   @ZenMethod
   IEventHandle onAnimalTame(IEventHandler<AnimalTameEvent> var1);

   @ZenMethod
   IEventHandle onFarmlandTrample(IEventHandler<BlockFarmlandTrampleEvent> var1);

   @ZenMethod
   IEventHandle onCriticalHit(IEventHandler<CriticalHitEvent> var1);

   @ZenMethod
   IEventHandle onEnchantmentLevelSet(IEventHandler<EnchantmentLevelSetEvent> var1);

   @ZenMethod
   IEventHandle onEntityMount(IEventHandler<EntityMountEvent> var1);

   @ZenMethod
   IEventHandle onExplosionStart(IEventHandler<ExplosionStartEvent> var1);

   @ZenMethod
   IEventHandle onExplosionDetonate(IEventHandler<ExplosionDetonateEvent> var1);

   @ZenMethod
   IEventHandle onItemFished(IEventHandler<ItemFishedEvent> var1);

   @ZenMethod
   IEventHandle onCropGrowPre(IEventHandler<CropGrowPreEvent> var1);

   @ZenMethod
   IEventHandle onCropGrowPost(IEventHandler<CropGrowPostEvent> var1);

   @ZenMethod
   IEventHandle onBlockPlace(IEventHandler<BlockPlaceEvent> var1);

   @ZenMethod
   IEventHandle onMobGriefing(IEventHandler<MobGriefingEvent> var1);

   @ZenMethod
   IEventHandle onEntityTravelToDimension(IEventHandler<EntityTravelToDimensionEvent> var1);

   @ZenMethod
   IEventHandle onLivingDestroyBlock(IEventHandler<LivingDestroyBlockEvent> var1);

   @ZenMethod
   IEventHandle onLivingExperienceDrop(IEventHandler<LivingExperienceDropEvent> var1);

   @ZenMethod
   IEventHandle onLivingKnockBack(IEventHandler<LivingKnockBackEvent> var1);

   @ZenMethod
   IEventHandle onLootingLevel(IEventHandler<LootingLevelEvent> var1);

   @ZenMethod
   IEventHandle onMinecartCollision(IEventHandler<MinecartCollisionEvent> var1);

   @ZenMethod
   IEventHandle onMinecartInteract(IEventHandler<MinecartInteractEvent> var1);

   @ZenMethod
   IEventHandle onMinecartUpdate(IEventHandler<MinecartUpdateEvent> var1);

   @ZenMethod
   IEventHandle onNoteBlock(IEventHandler<INoteBlockEvent> var1);

   @ZenMethod
   IEventHandle onNoteBlockChange(IEventHandler<NoteBlockChangeEvent> var1);

   @ZenMethod
   IEventHandle onNoteBlockPlay(IEventHandler<NoteBlockPlayEvent> var1);

   @ZenMethod
   IEventHandle onPlayerCloseContainer(IEventHandler<PlayerCloseContainerEvent> var1);

   @ZenMethod
   IEventHandle onPlayerItemPickup(IEventHandler<PlayerItemPickupEvent> var1);

   @ZenMethod
   IEventHandle onPlayerVisibility(IEventHandler<PlayerVisibilityEvent> var1);

   @ZenMethod
   IEventHandle onPlayerLeftClickBlock(IEventHandler<PlayerLeftClickBlockEvent> var1);

   @ZenMethod
   IEventHandle onPlayerRightClickItem(IEventHandler<PlayerRightClickItemEvent> var1);

   @ZenMethod
   IEventHandle onSleepingLocationCheck(IEventHandler<SleepingLocationCheckEvent> var1);

   @ZenMethod
   IEventHandle onSleepingTimeCheck(IEventHandler<SleepingTimeCheckEvent> var1);

   @ZenMethod
   IEventHandle onPotionBrewPre(IEventHandler<PotionBrewPreEvent> var1);

   @ZenMethod
   IEventHandle onPotionBrewPost(IEventHandler<PotionBrewPostEvent> var1);

   @ZenMethod
   IEventHandle onProjectileImpactArrow(IEventHandler<ProjectileImpactArrowEvent> var1);

   @ZenMethod
   IEventHandle onProjectileImpactFireball(IEventHandler<ProjectileImpactFireballEvent> var1);

   @ZenMethod
   IEventHandle onProjectileImpactThrowable(IEventHandler<ProjectileImpactThrowableEvent> var1);

   @ZenMethod
   IEventHandle onArrowLoose(IEventHandler<ArrowLooseEvent> var1);

   @ZenMethod
   IEventHandle onArrowNock(IEventHandler<ArrowNockEvent> var1);

   @ZenMethod
   IEventHandle onEntityJoinWorld(IEventHandler<EntityJoinWorldEvent> var1);

   @ZenMethod
   IEventHandle onEntityLivingEquipmentChange(IEventHandler<EntityLivingEquipmentChangeEvent> var1);

   @ZenMethod
   IEventHandle onEntityLivingDamage(IEventHandler<EntityLivingDamageEvent> var1);

   @ZenMethod
   IEventHandle onEntityLivingHeal(IEventHandler<EntityLivingHealEvent> var1);

   @ZenMethod
   IEventHandle onEntityLivingUpdate(IEventHandler<EntityLivingUpdateEvent> var1);

   @ZenMethod
   IEventHandle onPotionEffectAdded(IEventHandler<PotionEffectAddedEvent> var1);

   @ZenMethod
   IEventHandle onPotionEffectRemove(IEventHandler<PotionEffectRemoveEvent> var1);

   @ZenMethod
   IEventHandle onPotionEffectApplicable(IEventHandler<PotionEffectApplicableEvent> var1);

   @ZenMethod
   IEventHandle onPotionEffectExpiry(IEventHandler<PotionEffectExpiryEvent> var1);

   @ZenMethod
   IEventHandle onPlayerClone(IEventHandler<PlayerCloneEvent> var1);

   @ZenMethod
   IEventHandle onBlockNeighborNotify(IEventHandler<BlockNeighborNotifyEvent> var1);

   @ZenMethod
   IEventHandle onPortalSpawn(IEventHandler<PortalSpawnEvent> var1);
}
