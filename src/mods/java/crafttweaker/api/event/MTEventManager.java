package crafttweaker.api.event;

import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;

public class MTEventManager implements IEventManager {
   private final EventList<PlayerCraftedEvent> elPlayerCrafted = new EventList<PlayerCraftedEvent>();
   private final EventList<PlayerInteractEvent> elPlayerInteract = new EventList<PlayerInteractEvent>();
   private final EventList<PlayerOpenContainerEvent> elPlayerOpenContainer = new EventList<PlayerOpenContainerEvent>();
   private final EventList<PlayerPickupXpEvent> elPlayerPickupXp = new EventList<PlayerPickupXpEvent>();
   private final EventList<PlayerSleepInBedEvent> elPlayerSleepInBed = new EventList<PlayerSleepInBedEvent>();
   private final EventList<PlayerWakeUpEvent> elPlayerWakeUp = new EventList<PlayerWakeUpEvent>();
   private final EventList<PlayerUseHoeEvent> elPlayerUseHoe = new EventList<PlayerUseHoeEvent>();
   private final EventList<EntityLivingUseItemEvent.Start> elEntityLivingUseItemStart = new EventList<EntityLivingUseItemEvent.Start>();
   private final EventList<EntityLivingUseItemEvent.Stop> elEntityLivingUseItemStop = new EventList<EntityLivingUseItemEvent.Stop>();
   private final EventList<EntityLivingUseItemEvent.Tick> elEntityLivingUseItemTick = new EventList<EntityLivingUseItemEvent.Tick>();
   private final EventList<EntityLivingUseItemEvent.Finish> elEntityLivingUseItemFinish = new EventList<EntityLivingUseItemEvent.Finish>();
   private final EventList<EntityLivingUseItemEvent> elEntityLivingUseItem = new EventList<EntityLivingUseItemEvent>();
   private final EventList<PlayerPickupItemEvent> elPlayerPickupItem = new EventList<PlayerPickupItemEvent>();
   private final EventList<PlayerFillBucketEvent> elPlayerFillBucket = new EventList<PlayerFillBucketEvent>();
   private final EventList<PlayerDeathDropsEvent> elPlayerDeathDrops = new EventList<PlayerDeathDropsEvent>();
   private final EventList<PlayerRespawnEvent> elPlayerRespawn = new EventList<PlayerRespawnEvent>();
   private final EventList<PlayerAttackEntityEvent> elPlayerAttackEntity = new EventList<PlayerAttackEntityEvent>();
   private final EventList<PlayerBonemealEvent> elPlayerBonemeal = new EventList<PlayerBonemealEvent>();
   private final EventList<PlayerInteractEntityEvent> elPlayerInteractEntity = new EventList<PlayerInteractEntityEvent>();
   private final EventList<PlayerSmeltedEvent> elPlayerSmelted = new EventList<PlayerSmeltedEvent>();
   private final EventList<PlayerChangedDimensionEvent> elPlayerChangedDimension = new EventList<PlayerChangedDimensionEvent>();
   private final EventList<PlayerLoggedInEvent> elPlayerLoggedIn = new EventList<PlayerLoggedInEvent>();
   private final EventList<PlayerLoggedOutEvent> elPlayerLoggedOut = new EventList<PlayerLoggedOutEvent>();
   private final EventList<EntityStruckByLightningEvent> elEntityStruckByLightning = new EventList<EntityStruckByLightningEvent>();
   private final EventList<EnderTeleportEvent> elEnderTeleport = new EventList<EnderTeleportEvent>();
   private final EventList<EntityLivingAttackedEvent> elEntityLivingAttacked = new EventList<EntityLivingAttackedEvent>();
   private final EventList<EntityLivingDeathEvent> elEntityLivingDeath = new EventList<EntityLivingDeathEvent>();
   private final EventList<EntityLivingFallEvent> elEntityLivingFall = new EventList<EntityLivingFallEvent>();
   private final EventList<EntityLivingHurtEvent> elEntityLivingHurt = new EventList<EntityLivingHurtEvent>();
   private final EventList<EntityLivingJumpEvent> elEntityLivingJump = new EventList<EntityLivingJumpEvent>();
   private final EventList<EntityLivingDeathDropsEvent> elEntityLivingDeathDrops = new EventList<EntityLivingDeathDropsEvent>();
   private final EventList<ItemExpireEvent> elItemExpire = new EventList<ItemExpireEvent>();
   private final EventList<ItemTossEvent> elItemToss = new EventList<ItemTossEvent>();
   private final EventList<PlayerAnvilRepairEvent> elPlayerAnvilRepair = new EventList<PlayerAnvilRepairEvent>();
   private final EventList<PlayerAnvilUpdateEvent> elPlayerAnvilUpdate = new EventList<PlayerAnvilUpdateEvent>();
   private final EventList<PlayerSetSpawnEvent> elPlayerSetSpawn = new EventList<PlayerSetSpawnEvent>();
   private final EventList<PlayerDestroyItemEvent> elPlayerDestroyItem = new EventList<PlayerDestroyItemEvent>();
   private final EventList<PlayerBrewedPotionEvent> elPlayerBrewedPotion = new EventList<PlayerBrewedPotionEvent>();
   private final EventList<PlayerTickEvent> elPlayerTick = new EventList<PlayerTickEvent>();
   private final EventList<ClientTickEvent> elClientTick = new EventList<ClientTickEvent>();
   private final EventList<ServerTickEvent> elServerTick = new EventList<ServerTickEvent>();
   private final EventList<RenderTickEvent> elRenderTick = new EventList<RenderTickEvent>();
   private final EventList<WorldTickEvent> elWorldTick = new EventList<WorldTickEvent>();
   private final EventList<BlockBreakEvent> elBlockBreak = new EventList<BlockBreakEvent>();
   private final EventList<BlockHarvestDropsEvent> elBlockHarvestDrops = new EventList<BlockHarvestDropsEvent>();
   private final EventList<PlayerBreakSpeedEvent> elPlayerBreakSpeed = new EventList<PlayerBreakSpeedEvent>();
   private final EventList<PlayerRightClickBlockEvent> elPlayerRightClickBlock = new EventList<PlayerRightClickBlockEvent>();
   private final EventList<BlockNeighborNotifyEvent> elBlockNeighborNotify = new EventList<BlockNeighborNotifyEvent>();
   private final EventList<PortalSpawnEvent> elPortalSpawn = new EventList<PortalSpawnEvent>();
   private final EventList<CommandEvent> elCommand = new EventList<CommandEvent>();
   private final EventList<PlayerAdvancementEvent> elPlayerAdvancement = new EventList<PlayerAdvancementEvent>();
   private final EventList<EntityLivingSpawnEvent.EntityLivingExtendedSpawnEvent> elCheckSpawn = new EventList<EntityLivingSpawnEvent.EntityLivingExtendedSpawnEvent>();
   private final EventList<EntityLivingSpawnEvent.EntityLivingExtendedSpawnEvent> elSpecialSpawn = new EventList<EntityLivingSpawnEvent.EntityLivingExtendedSpawnEvent>();
   private final EventList<EntityLivingSpawnEvent> elAllowDespawn = new EventList<EntityLivingSpawnEvent>();
   private final EventList<AnimalTameEvent> elAnimalTame = new EventList<AnimalTameEvent>();
   private final EventList<BlockFarmlandTrampleEvent> elFarmlandTrample = new EventList<BlockFarmlandTrampleEvent>();
   private final EventList<CriticalHitEvent> elCriticalHit = new EventList<CriticalHitEvent>();
   private final EventList<EnchantmentLevelSetEvent> elEnchantmentLevelSet = new EventList<EnchantmentLevelSetEvent>();
   private final EventList<EntityMountEvent> elEntityMountEvent = new EventList<EntityMountEvent>();
   private final EventList<ExplosionStartEvent> elExplosionStartEvent = new EventList<ExplosionStartEvent>();
   private final EventList<ExplosionDetonateEvent> elExplosionDetonateEvent = new EventList<ExplosionDetonateEvent>();
   private final EventList<ItemFishedEvent> elItemFishedEvent = new EventList<ItemFishedEvent>();
   private final EventList<CropGrowPreEvent> elCropGrowPre = new EventList<CropGrowPreEvent>();
   private final EventList<CropGrowPostEvent> elCropGrowPost = new EventList<CropGrowPostEvent>();
   private final EventList<BlockPlaceEvent> elBlockPlace = new EventList<BlockPlaceEvent>();
   private final EventList<MobGriefingEvent> elMobGriefing = new EventList<MobGriefingEvent>();
   private final EventList<EntityTravelToDimensionEvent> elEntityTravelToDimension = new EventList<EntityTravelToDimensionEvent>();
   private final EventList<LivingDestroyBlockEvent> elLivingDestroyBlock = new EventList<LivingDestroyBlockEvent>();
   private final EventList<LivingExperienceDropEvent> elLivingExperienceDrop = new EventList<LivingExperienceDropEvent>();
   private final EventList<LivingKnockBackEvent> elLivingKnockBack = new EventList<LivingKnockBackEvent>();
   private final EventList<LootingLevelEvent> elLootingLevel = new EventList<LootingLevelEvent>();
   private final EventList<MinecartCollisionEvent> elMinecartCollision = new EventList<MinecartCollisionEvent>();
   private final EventList<MinecartInteractEvent> elMinecartInteract = new EventList<MinecartInteractEvent>();
   private final EventList<MinecartUpdateEvent> elMinecartUpdate = new EventList<MinecartUpdateEvent>();
   private final EventList<INoteBlockEvent> elNoteBlock = new EventList<INoteBlockEvent>();
   private final EventList<NoteBlockChangeEvent> elNoteBlockChange = new EventList<NoteBlockChangeEvent>();
   private final EventList<NoteBlockPlayEvent> elNoteBlockPlay = new EventList<NoteBlockPlayEvent>();
   private final EventList<PlayerCloseContainerEvent> elPlayerCloseContainer = new EventList<PlayerCloseContainerEvent>();
   private final EventList<PlayerItemPickupEvent> elPlayerItemPickup = new EventList<PlayerItemPickupEvent>();
   private final EventList<PlayerVisibilityEvent> elPlayerVisibility = new EventList<PlayerVisibilityEvent>();
   private final EventList<PlayerLeftClickBlockEvent> elPlayerLeftClickBlock = new EventList<PlayerLeftClickBlockEvent>();
   private final EventList<PlayerRightClickItemEvent> elPlayerRightClickItem = new EventList<PlayerRightClickItemEvent>();
   private final EventList<SleepingLocationCheckEvent> elSleepingLocationCheck = new EventList<SleepingLocationCheckEvent>();
   private final EventList<SleepingTimeCheckEvent> elSleepingTimeCheck = new EventList<SleepingTimeCheckEvent>();
   private final EventList<PotionBrewPreEvent> elPotionBrewPre = new EventList<PotionBrewPreEvent>();
   private final EventList<PotionBrewPostEvent> elPotionBrewPost = new EventList<PotionBrewPostEvent>();
   private final EventList<ProjectileImpactArrowEvent> elProjectileImpactArrow = new EventList<ProjectileImpactArrowEvent>();
   private final EventList<ProjectileImpactFireballEvent> elProjectileImpactFireball = new EventList<ProjectileImpactFireballEvent>();
   private final EventList<ProjectileImpactThrowableEvent> elProjectileImpactThrowable = new EventList<ProjectileImpactThrowableEvent>();
   private final EventList<ArrowLooseEvent> elArrowLoose = new EventList<ArrowLooseEvent>();
   private final EventList<ArrowNockEvent> elArrowNock = new EventList<ArrowNockEvent>();
   private final EventList<EntityJoinWorldEvent> elEntityJoinWorld = new EventList<EntityJoinWorldEvent>();
   private final EventList<EntityLivingEquipmentChangeEvent> elEntityLivingEquipmentChange = new EventList<EntityLivingEquipmentChangeEvent>();
   private final EventList<EntityLivingDamageEvent> elEntityLivingDamage = new EventList<EntityLivingDamageEvent>();
   private final EventList<EntityLivingHealEvent> elEntityLivingHeal = new EventList<EntityLivingHealEvent>();
   private final EventList<EntityLivingUpdateEvent> elEntityLivingUpdateEvent = new EventList<EntityLivingUpdateEvent>();
   private final EventList<PotionEffectAddedEvent> elPotionEffectAdded = new EventList<PotionEffectAddedEvent>();
   private final EventList<PotionEffectRemoveEvent> elPotionEffectRemove = new EventList<PotionEffectRemoveEvent>();
   private final EventList<PotionEffectApplicableEvent> elPotionEffectApplicable = new EventList<PotionEffectApplicableEvent>();
   private final EventList<PotionEffectExpiryEvent> elPotionEffectExpiry = new EventList<PotionEffectExpiryEvent>();
   private final EventList<PlayerCloneEvent> elPlayerCloneEvent = new EventList<PlayerCloneEvent>();

   public MTEventManager() {
   }

   public void clear() {
      this.elPlayerCrafted.clear();
      this.elPlayerInteract.clear();
      this.elPlayerOpenContainer.clear();
      this.elPlayerPickupXp.clear();
      this.elPlayerSleepInBed.clear();
      this.elPlayerWakeUp.clear();
      this.elPlayerUseHoe.clear();
      this.elEntityLivingUseItemStart.clear();
      this.elEntityLivingUseItemStop.clear();
      this.elEntityLivingUseItemTick.clear();
      this.elEntityLivingUseItemFinish.clear();
      this.elEntityLivingUseItem.clear();
      this.elPlayerPickupItem.clear();
      this.elPlayerFillBucket.clear();
      this.elPlayerDeathDrops.clear();
      this.elPlayerRespawn.clear();
      this.elPlayerAttackEntity.clear();
      this.elPlayerBonemeal.clear();
      this.elPlayerInteractEntity.clear();
      this.elPlayerSmelted.clear();
      this.elPlayerChangedDimension.clear();
      this.elPlayerLoggedIn.clear();
      this.elPlayerLoggedOut.clear();
      this.elEntityStruckByLightning.clear();
      this.elEnderTeleport.clear();
      this.elEntityLivingAttacked.clear();
      this.elEntityLivingDeath.clear();
      this.elEntityLivingFall.clear();
      this.elEntityLivingHurt.clear();
      this.elEntityLivingJump.clear();
      this.elEntityLivingDeathDrops.clear();
      this.elItemExpire.clear();
      this.elItemToss.clear();
      this.elPlayerAnvilRepair.clear();
      this.elPlayerSetSpawn.clear();
      this.elPlayerDestroyItem.clear();
      this.elPlayerBrewedPotion.clear();
      this.elPlayerTick.clear();
      this.elClientTick.clear();
      this.elServerTick.clear();
      this.elRenderTick.clear();
      this.elWorldTick.clear();
      this.elBlockBreak.clear();
      this.elBlockHarvestDrops.clear();
      this.elPlayerBreakSpeed.clear();
      this.elPlayerRightClickBlock.clear();
      this.elAnimalTame.clear();
      this.elFarmlandTrample.clear();
      this.elEnchantmentLevelSet.clear();
      this.elEntityMountEvent.clear();
      this.elExplosionStartEvent.clear();
      this.elExplosionDetonateEvent.clear();
      this.elItemFishedEvent.clear();
      this.elCropGrowPost.clear();
      this.elCropGrowPre.clear();
      this.elBlockPlace.clear();
      this.elMobGriefing.clear();
      this.elEntityTravelToDimension.clear();
      this.elLivingDestroyBlock.clear();
      this.elLivingExperienceDrop.clear();
      this.elLivingKnockBack.clear();
      this.elMinecartCollision.clear();
      this.elMinecartInteract.clear();
      this.elMinecartUpdate.clear();
      this.elPlayerCloseContainer.clear();
      this.elPlayerItemPickup.clear();
      this.elPlayerVisibility.clear();
      this.elPlayerLeftClickBlock.clear();
      this.elPlayerRightClickItem.clear();
      this.elSleepingLocationCheck.clear();
      this.elSleepingTimeCheck.clear();
      this.elPotionBrewPre.clear();
      this.elPotionBrewPost.clear();
      this.elProjectileImpactArrow.clear();
      this.elProjectileImpactFireball.clear();
      this.elProjectileImpactThrowable.clear();
      this.elAllowDespawn.clear();
      this.elCheckSpawn.clear();
      this.elCommand.clear();
      this.elCriticalHit.clear();
      this.elLootingLevel.clear();
      this.elPlayerAdvancement.clear();
      this.elSpecialSpawn.clear();
      this.elArrowLoose.clear();
      this.elArrowNock.clear();
      this.elEntityJoinWorld.clear();
      this.elEntityLivingEquipmentChange.clear();
      this.elEntityLivingDamage.clear();
      this.elEntityLivingHeal.clear();
      this.elEntityLivingUpdateEvent.clear();
      this.elPotionEffectAdded.clear();
      this.elPotionEffectRemove.clear();
      this.elPotionEffectApplicable.clear();
      this.elPotionEffectExpiry.clear();
      this.elPlayerCloneEvent.clear();
      this.elBlockNeighborNotify.clear();
      this.elPortalSpawn.clear();
      this.elNoteBlock.clear();
      this.elNoteBlockChange.clear();
      this.elNoteBlockPlay.clear();
   }

   public IEventHandle onPlayerCrafted(IEventHandler<PlayerCraftedEvent> ev) {
      return this.elPlayerCrafted.add(ev);
   }

   public boolean hasPlayerCrafted() {
      return this.elPlayerCrafted.hasHandlers();
   }

   public void publishPlayerCrafted(PlayerCraftedEvent event) {
      this.elPlayerCrafted.publish(event);
   }

   public IEventHandle onPlayerSmelted(IEventHandler<PlayerSmeltedEvent> ev) {
      return this.elPlayerSmelted.add(ev);
   }

   public boolean hasPlayerSmelted() {
      return this.elPlayerSmelted.hasHandlers();
   }

   public void publishPlayerSmelted(PlayerSmeltedEvent event) {
      this.elPlayerSmelted.publish(event);
   }

   public IEventHandle onPlayerChangedDimension(IEventHandler<PlayerChangedDimensionEvent> ev) {
      return this.elPlayerChangedDimension.add(ev);
   }

   public boolean hasPlayerChangedDimension() {
      return this.elPlayerChangedDimension.hasHandlers();
   }

   public void publishPlayerChangedDimension(PlayerChangedDimensionEvent event) {
      this.elPlayerChangedDimension.publish(event);
   }

   public IEventHandle onPlayerLoggedIn(IEventHandler<PlayerLoggedInEvent> ev) {
      return this.elPlayerLoggedIn.add(ev);
   }

   public boolean hasPlayerLoggedIn() {
      return this.elPlayerLoggedIn.hasHandlers();
   }

   public void publishPlayerLoggedIn(PlayerLoggedInEvent event) {
      this.elPlayerLoggedIn.publish(event);
   }

   public IEventHandle onPlayerLoggedOut(IEventHandler<PlayerLoggedOutEvent> ev) {
      return this.elPlayerLoggedOut.add(ev);
   }

   public boolean hasPlayerLoggedOut() {
      return this.elPlayerLoggedOut.hasHandlers();
   }

   public void publishPlayerLoggedOut(PlayerLoggedOutEvent event) {
      this.elPlayerLoggedOut.publish(event);
   }

   public IEventHandle onPlayerRespawn(IEventHandler<PlayerRespawnEvent> ev) {
      return this.elPlayerRespawn.add(ev);
   }

   public boolean hasPlayerRespawn() {
      return this.elPlayerRespawn.hasHandlers();
   }

   public void publishPlayerRespawn(PlayerRespawnEvent event) {
      this.elPlayerRespawn.publish(event);
   }

   public IEventHandle onPlayerAttackEntity(IEventHandler<PlayerAttackEntityEvent> ev) {
      return this.elPlayerAttackEntity.add(ev);
   }

   public boolean hasPlayerAttackEntity() {
      return this.elPlayerAttackEntity.hasHandlers();
   }

   public void publishPlayerAttackEntity(PlayerAttackEntityEvent event) {
      this.elPlayerAttackEntity.publish(event);
   }

   public IEventHandle onPlayerBonemeal(IEventHandler<PlayerBonemealEvent> ev) {
      return this.elPlayerBonemeal.add(ev);
   }

   public boolean hasPlayerBonemeal() {
      return this.elPlayerBonemeal.hasHandlers();
   }

   public void publishPlayerBonemeal(PlayerBonemealEvent event) {
      this.elPlayerBonemeal.publish(event);
   }

   public IEventHandle onPlayerInteractEntity(IEventHandler<PlayerInteractEntityEvent> ev) {
      return this.elPlayerInteractEntity.add(ev);
   }

   public boolean hasPlayerInteractEntity() {
      return this.elPlayerInteractEntity.hasHandlers();
   }

   public void publishPlayerInteractEntity(PlayerInteractEntityEvent event) {
      this.elPlayerInteractEntity.publish(event);
   }

   public IEventHandle onPlayerPickupItem(IEventHandler<PlayerPickupItemEvent> ev) {
      return this.elPlayerPickupItem.add(ev);
   }

   public boolean hasPlayerPickupItem() {
      return this.elPlayerPickupItem.hasHandlers();
   }

   public void publishPlayerPickupItem(PlayerPickupItemEvent event) {
      this.elPlayerPickupItem.publish(event);
   }

   public IEventHandle onPlayerFillBucket(IEventHandler<PlayerFillBucketEvent> ev) {
      return this.elPlayerFillBucket.add(ev);
   }

   public boolean hasPlayerFillBucket() {
      return this.elPlayerFillBucket.hasHandlers();
   }

   public void publishPlayerFillBucket(PlayerFillBucketEvent event) {
      this.elPlayerFillBucket.publish(event);
   }

   public IEventHandle onPlayerDeathDrops(IEventHandler<PlayerDeathDropsEvent> ev) {
      return this.elPlayerDeathDrops.add(ev);
   }

   public boolean hasPlayerDeathDrops() {
      return this.elPlayerDeathDrops.hasHandlers();
   }

   public void publishPlayerDeathDrops(PlayerDeathDropsEvent event) {
      this.elPlayerDeathDrops.publish(event);
   }

   public IEventHandle onPlayerInteract(IEventHandler<PlayerInteractEvent> ev) {
      return this.elPlayerInteract.add(ev);
   }

   public boolean hasPlayerInteract() {
      return this.elPlayerInteract.hasHandlers();
   }

   public void publishPlayerInteract(PlayerInteractEvent event) {
      this.elPlayerInteract.publish(event);
   }

   public IEventHandle onPlayerOpenContainer(IEventHandler<PlayerOpenContainerEvent> ev) {
      return this.elPlayerOpenContainer.add(ev);
   }

   public boolean hasPlayerOpenContainer() {
      return this.elPlayerOpenContainer.hasHandlers();
   }

   public void publishPlayerOpenContainer(PlayerOpenContainerEvent event) {
      this.elPlayerOpenContainer.publish(event);
   }

   public IEventHandle onPlayerPickupXp(IEventHandler<PlayerPickupXpEvent> ev) {
      return this.elPlayerPickupXp.add(ev);
   }

   public boolean hasPlayerPickupXp() {
      return this.elPlayerPickupXp.hasHandlers();
   }

   public void publishPlayerPickupXp(PlayerPickupXpEvent event) {
      this.elPlayerPickupXp.publish(event);
   }

   public IEventHandle onPlayerSleepInBed(IEventHandler<PlayerSleepInBedEvent> ev) {
      return this.elPlayerSleepInBed.add(ev);
   }

   public boolean hasPlayerSleepInBed() {
      return this.elPlayerSleepInBed.hasHandlers();
   }

   public void publishPlayerSleepInBed(PlayerSleepInBedEvent event) {
      this.elPlayerSleepInBed.publish(event);
   }

   public IEventHandle onPlayerWakeUp(IEventHandler<PlayerWakeUpEvent> ev) {
      return this.elPlayerWakeUp.add(ev);
   }

   public boolean hasPlayerWakeUp() {
      return this.elPlayerWakeUp.hasHandlers();
   }

   public void publishPlayerWakeUp(PlayerWakeUpEvent event) {
      this.elPlayerWakeUp.publish(event);
   }

   public IEventHandle onPlayerUseHoe(IEventHandler<PlayerUseHoeEvent> ev) {
      return this.elPlayerUseHoe.add(ev);
   }

   public boolean hasPlayerUseHoe() {
      return this.elPlayerUseHoe.hasHandlers();
   }

   public void publishPlayerUseHoe(PlayerUseHoeEvent event) {
      this.elPlayerUseHoe.publish(event);
   }

   public IEventHandle onEntityLivingUseItem(IEventHandler<EntityLivingUseItemEvent> ev) {
      return this.elEntityLivingUseItem.add(ev);
   }

   public boolean hasEntityLivingUseItem() {
      return this.elEntityLivingUseItem.hasHandlers();
   }

   public void publishEntityLivingUseItem(EntityLivingUseItemEvent event) {
      this.elEntityLivingUseItem.publish(event);
   }

   public IEventHandle onEntityLivingUseItemStart(IEventHandler<EntityLivingUseItemEvent.Start> ev) {
      return this.elEntityLivingUseItemStart.add(ev);
   }

   public boolean hasEntityLivingUseItemStart() {
      return this.elEntityLivingUseItemStart.hasHandlers();
   }

   public void publishEntityLivingUseItemStart(EntityLivingUseItemEvent.Start event) {
      this.elEntityLivingUseItemStart.publish(event);
   }

   public IEventHandle onEntityLivingUseItemStop(IEventHandler<EntityLivingUseItemEvent.Stop> ev) {
      return this.elEntityLivingUseItemStop.add(ev);
   }

   public boolean hasEntityLivingUseItemStop() {
      return this.elEntityLivingUseItemStop.hasHandlers();
   }

   public void publishEntityLivingUseItemStop(EntityLivingUseItemEvent.Stop event) {
      this.elEntityLivingUseItemStop.publish(event);
   }

   public IEventHandle onEntityLivingUseItemTick(IEventHandler<EntityLivingUseItemEvent.Tick> ev) {
      return this.elEntityLivingUseItemTick.add(ev);
   }

   public boolean hasEntityLivingUseItemTick() {
      return this.elEntityLivingUseItemTick.hasHandlers();
   }

   public void publishEntityLivingUseItemTick(EntityLivingUseItemEvent.Tick event) {
      this.elEntityLivingUseItemTick.publish(event);
   }

   public IEventHandle onEntityLivingUseItemFinish(IEventHandler<EntityLivingUseItemEvent.Finish> ev) {
      return this.elEntityLivingUseItemFinish.add(ev);
   }

   public boolean hasEntityLivingUseItemFinish() {
      return this.elEntityLivingUseItemFinish.hasHandlers();
   }

   public void publishEntityLivingUseItemFinish(EntityLivingUseItemEvent.Finish event) {
      this.elEntityLivingUseItemFinish.publish(event);
   }

   public IEventHandle onEntityStruckByLightning(IEventHandler<EntityStruckByLightningEvent> ev) {
      return this.elEntityStruckByLightning.add(ev);
   }

   public boolean hasEntityStruckByLightning() {
      return this.elEntityStruckByLightning.hasHandlers();
   }

   public void publishEntityStruckByLightning(EntityStruckByLightningEvent event) {
      this.elEntityStruckByLightning.publish(event);
   }

   public IEventHandle onEnderTeleport(IEventHandler<EnderTeleportEvent> ev) {
      return this.elEnderTeleport.add(ev);
   }

   public boolean hasEnderTeleport() {
      return this.elEnderTeleport.hasHandlers();
   }

   public void publishEnderTeleport(EnderTeleportEvent event) {
      this.elEnderTeleport.publish(event);
   }

   public IEventHandle onEntityLivingAttacked(IEventHandler<EntityLivingAttackedEvent> ev) {
      return this.elEntityLivingAttacked.add(ev);
   }

   public boolean hasEntityLivingAttacked() {
      return this.elEntityLivingAttacked.hasHandlers();
   }

   public void publishEntityLivingAttacked(EntityLivingAttackedEvent event) {
      this.elEntityLivingAttacked.publish(event);
   }

   public IEventHandle onEntityLivingDeath(IEventHandler<EntityLivingDeathEvent> ev) {
      return this.elEntityLivingDeath.add(ev);
   }

   public boolean hasEntityLivingDeath() {
      return this.elEntityLivingDeath.hasHandlers();
   }

   public void publishEntityLivingDeath(EntityLivingDeathEvent event) {
      this.elEntityLivingDeath.publish(event);
   }

   public IEventHandle onEntityLivingFall(IEventHandler<EntityLivingFallEvent> ev) {
      return this.elEntityLivingFall.add(ev);
   }

   public boolean hasEntityLivingFall() {
      return this.elEntityLivingFall.hasHandlers();
   }

   public void publishEntityLivingFall(EntityLivingFallEvent event) {
      this.elEntityLivingFall.publish(event);
   }

   public IEventHandle onEntityLivingHurt(IEventHandler<EntityLivingHurtEvent> ev) {
      return this.elEntityLivingHurt.add(ev);
   }

   public boolean hasEntityLivingHurt() {
      return this.elEntityLivingHurt.hasHandlers();
   }

   public void publishEntityLivingHurt(EntityLivingHurtEvent event) {
      this.elEntityLivingHurt.publish(event);
   }

   public IEventHandle onEntityLivingJump(IEventHandler<EntityLivingJumpEvent> ev) {
      return this.elEntityLivingJump.add(ev);
   }

   public boolean hasEntityLivingJump() {
      return this.elEntityLivingJump.hasHandlers();
   }

   public void publishEntityLivingJump(EntityLivingJumpEvent event) {
      this.elEntityLivingJump.publish(event);
   }

   public IEventHandle onEntityLivingDeathDrops(IEventHandler<EntityLivingDeathDropsEvent> ev) {
      return this.elEntityLivingDeathDrops.add(ev);
   }

   public boolean hasEntityLivingDeathDrops() {
      return this.elEntityLivingDeathDrops.hasHandlers();
   }

   public void publishEntityLivingDeathDrops(EntityLivingDeathDropsEvent event) {
      this.elEntityLivingDeathDrops.publish(event);
   }

   public IEventHandle onItemExpire(IEventHandler<ItemExpireEvent> ev) {
      return this.elItemExpire.add(ev);
   }

   public boolean hasItemExpire() {
      return this.elItemExpire.hasHandlers();
   }

   public void publishItemExpire(ItemExpireEvent event) {
      this.elItemExpire.publish(event);
   }

   public IEventHandle onItemToss(IEventHandler<ItemTossEvent> ev) {
      return this.elItemToss.add(ev);
   }

   public boolean hasItemToss() {
      return this.elItemToss.hasHandlers();
   }

   public void publishItemToss(ItemTossEvent event) {
      this.elItemToss.publish(event);
   }

   public IEventHandle onPlayerAnvilRepair(IEventHandler<PlayerAnvilRepairEvent> ev) {
      return this.elPlayerAnvilRepair.add(ev);
   }

   public boolean hasPlayerAnvilRepair() {
      return this.elPlayerAnvilRepair.hasHandlers();
   }

   public void publishPlayerAnvilRepair(PlayerAnvilRepairEvent event) {
      this.elPlayerAnvilRepair.publish(event);
   }

   public IEventHandle onPlayerAnvilUpdate(IEventHandler<PlayerAnvilUpdateEvent> ev) {
      return this.elPlayerAnvilUpdate.add(ev);
   }

   public boolean hasPlayerAnvilUpdate() {
      return this.elPlayerAnvilUpdate.hasHandlers();
   }

   public void publishPlayerAnvilUpdate(PlayerAnvilUpdateEvent event) {
      this.elPlayerAnvilUpdate.publish(event);
   }

   public IEventHandle onPlayerSetSpawn(IEventHandler<PlayerSetSpawnEvent> ev) {
      return this.elPlayerSetSpawn.add(ev);
   }

   public boolean hasPlayerSetSpawn() {
      return this.elPlayerSetSpawn.hasHandlers();
   }

   public void publishPlayerSetSpawn(PlayerSetSpawnEvent event) {
      this.elPlayerSetSpawn.publish(event);
   }

   public IEventHandle onPlayerDestroyItem(IEventHandler<PlayerDestroyItemEvent> ev) {
      return this.elPlayerDestroyItem.add(ev);
   }

   public boolean hasPlayerDestroyItem() {
      return this.elPlayerDestroyItem.hasHandlers();
   }

   public void publishPlayerDestroyItem(PlayerDestroyItemEvent event) {
      this.elPlayerDestroyItem.publish(event);
   }

   public IEventHandle onPlayerBrewedPotion(IEventHandler<PlayerBrewedPotionEvent> ev) {
      return this.elPlayerBrewedPotion.add(ev);
   }

   public boolean hasPlayerBrewedPotion() {
      return this.elPlayerBrewedPotion.hasHandlers();
   }

   public void publishPlayerBrewedPotion(PlayerBrewedPotionEvent event) {
      this.elPlayerBrewedPotion.publish(event);
   }

   public IEventHandle onPlayerTick(IEventHandler<PlayerTickEvent> ev) {
      return this.elPlayerTick.add(ev);
   }

   public boolean hasPlayerTick() {
      return this.elPlayerTick.hasHandlers();
   }

   public void publishPlayerTick(PlayerTickEvent event) {
      this.elPlayerTick.publish(event);
   }

   public IEventHandle onClientTick(IEventHandler<ClientTickEvent> ev) {
      return this.elClientTick.add(ev);
   }

   public boolean hasClientTick() {
      return this.elClientTick.hasHandlers();
   }

   public void publishClientTick(ClientTickEvent event) {
      this.elClientTick.publish(event);
   }

   public IEventHandle onServerTick(IEventHandler<ServerTickEvent> ev) {
      return this.elServerTick.add(ev);
   }

   public boolean hasServerTick() {
      return this.elServerTick.hasHandlers();
   }

   public void publishServerTick(ServerTickEvent event) {
      this.elServerTick.publish(event);
   }

   public IEventHandle onRenderTick(IEventHandler<RenderTickEvent> ev) {
      return this.elRenderTick.add(ev);
   }

   public boolean hasRenderTick() {
      return this.elRenderTick.hasHandlers();
   }

   public void publishRenderTick(RenderTickEvent event) {
      this.elRenderTick.publish(event);
   }

   public IEventHandle onWorldTick(IEventHandler<WorldTickEvent> ev) {
      return this.elWorldTick.add(ev);
   }

   public boolean hasWorldTick() {
      return this.elWorldTick.hasHandlers();
   }

   public void publishWorldTick(WorldTickEvent event) {
      this.elWorldTick.publish(event);
   }

   public IEventHandle onBlockBreak(IEventHandler<BlockBreakEvent> ev) {
      return this.elBlockBreak.add(ev);
   }

   public boolean hasBlockBreak() {
      return this.elBlockBreak.hasHandlers();
   }

   public void publishBlockBreak(BlockBreakEvent event) {
      this.elBlockBreak.publish(event);
   }

   public IEventHandle onBlockHarvestDrops(IEventHandler<BlockHarvestDropsEvent> ev) {
      return this.elBlockHarvestDrops.add(ev);
   }

   public boolean hasBlockHarvestDrops() {
      return this.elBlockHarvestDrops.hasHandlers();
   }

   public void publishBlockHarvestDrops(BlockHarvestDropsEvent event) {
      this.elBlockHarvestDrops.publish(event);
   }

   public IEventHandle onPlayerBreakSpeed(IEventHandler<PlayerBreakSpeedEvent> ev) {
      return this.elPlayerBreakSpeed.add(ev);
   }

   public boolean hasPlayerBreakSpeed() {
      return this.elPlayerBreakSpeed.hasHandlers();
   }

   public void publishPlayerBreakSpeed(PlayerBreakSpeedEvent event) {
      this.elPlayerBreakSpeed.publish(event);
   }

   public IEventHandle onPlayerRightClickBlock(IEventHandler<PlayerRightClickBlockEvent> ev) {
      return this.elPlayerRightClickBlock.add(ev);
   }

   public boolean hasPlayerRightClickBlock() {
      return this.elPlayerRightClickBlock.hasHandlers();
   }

   public void publishPlayerRightClickBlock(PlayerRightClickBlockEvent event) {
      this.elPlayerRightClickBlock.publish(event);
   }

   public IEventHandle onPlayerInteractBlock(IEventHandler<PlayerRightClickBlockEvent> ev) {
      return this.onPlayerRightClickBlock(ev);
   }

   public IEventHandle onCommand(IEventHandler<CommandEvent> ev) {
      return this.elCommand.add(ev);
   }

   public boolean hasCommand() {
      return this.elCommand.hasHandlers();
   }

   public void publishCommand(CommandEvent event) {
      this.elCommand.publish(event);
   }

   public IEventHandle onPlayerAdvancement(IEventHandler<PlayerAdvancementEvent> ev) {
      return this.elPlayerAdvancement.add(ev);
   }

   public boolean hasPlayerAdvancement() {
      return this.elPlayerAdvancement.hasHandlers();
   }

   public void publishPlayerAdvancement(PlayerAdvancementEvent event) {
      this.elPlayerAdvancement.publish(event);
   }

   public IEventHandle onCheckSpawn(IEventHandler<EntityLivingSpawnEvent.EntityLivingExtendedSpawnEvent> ev) {
      return this.elCheckSpawn.add(ev);
   }

   public boolean hasCheckSpawn() {
      return this.elCheckSpawn.hasHandlers();
   }

   public void publishCheckSpawn(EntityLivingSpawnEvent.EntityLivingExtendedSpawnEvent event) {
      this.elCheckSpawn.publish(event);
   }

   public IEventHandle onSpecialSpawn(IEventHandler<EntityLivingSpawnEvent.EntityLivingExtendedSpawnEvent> ev) {
      return this.elSpecialSpawn.add(ev);
   }

   public boolean hasSpecialSpawn() {
      return this.elSpecialSpawn.hasHandlers();
   }

   public void publishSpecialSpawn(EntityLivingSpawnEvent.EntityLivingExtendedSpawnEvent event) {
      this.elSpecialSpawn.publish(event);
   }

   public IEventHandle onAllowDespawn(IEventHandler<EntityLivingSpawnEvent> ev) {
      return this.elAllowDespawn.add(ev);
   }

   public boolean hasAllowDespawn() {
      return this.elAllowDespawn.hasHandlers();
   }

   public void publishAllowDespawn(EntityLivingSpawnEvent event) {
      this.elAllowDespawn.publish(event);
   }

   public IEventHandle onAnimalTame(IEventHandler<AnimalTameEvent> ev) {
      return this.elAnimalTame.add(ev);
   }

   public boolean hasAnimalTame() {
      return this.elAnimalTame.hasHandlers();
   }

   public void publishAnimalTame(AnimalTameEvent event) {
      this.elAnimalTame.publish(event);
   }

   public IEventHandle onFarmlandTrample(IEventHandler<BlockFarmlandTrampleEvent> ev) {
      return this.elFarmlandTrample.add(ev);
   }

   public boolean hasFarmlandTrample() {
      return this.elFarmlandTrample.hasHandlers();
   }

   public void publishFarmlandTrample(BlockFarmlandTrampleEvent event) {
      this.elFarmlandTrample.publish(event);
   }

   public IEventHandle onCriticalHit(IEventHandler<CriticalHitEvent> ev) {
      return this.elCriticalHit.add(ev);
   }

   public boolean hasCriticalHit() {
      return this.elCriticalHit.hasHandlers();
   }

   public void publishCriticalHit(CriticalHitEvent event) {
      this.elCriticalHit.publish(event);
   }

   public IEventHandle onEnchantmentLevelSet(IEventHandler<EnchantmentLevelSetEvent> ev) {
      return this.elEnchantmentLevelSet.add(ev);
   }

   public boolean hasEnchantmentLevelSet() {
      return this.elEnchantmentLevelSet.hasHandlers();
   }

   public void publishEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
      this.elEnchantmentLevelSet.publish(event);
   }

   public IEventHandle onEntityMount(IEventHandler<EntityMountEvent> ev) {
      return this.elEntityMountEvent.add(ev);
   }

   public boolean hasEntityMount() {
      return this.elEntityMountEvent.hasHandlers();
   }

   public void publishEntityMount(EntityMountEvent event) {
      this.elEntityMountEvent.publish(event);
   }

   public IEventHandle onExplosionStart(IEventHandler<ExplosionStartEvent> ev) {
      return this.elExplosionStartEvent.add(ev);
   }

   public boolean hasExplosionStart() {
      return this.elExplosionStartEvent.hasHandlers();
   }

   public void publishExplosionStart(ExplosionStartEvent event) {
      this.elExplosionStartEvent.publish(event);
   }

   public IEventHandle onExplosionDetonate(IEventHandler<ExplosionDetonateEvent> ev) {
      return this.elExplosionDetonateEvent.add(ev);
   }

   public boolean hasExplosionDetonate() {
      return this.elExplosionDetonateEvent.hasHandlers();
   }

   public void publishExplosionDetonate(ExplosionDetonateEvent event) {
      this.elExplosionDetonateEvent.publish(event);
   }

   public IEventHandle onItemFished(IEventHandler<ItemFishedEvent> ev) {
      return this.elItemFishedEvent.add(ev);
   }

   public boolean hasItemFished() {
      return this.elItemFishedEvent.hasHandlers();
   }

   public void publishItemFished(ItemFishedEvent event) {
      this.elItemFishedEvent.publish(event);
   }

   public IEventHandle onCropGrowPre(IEventHandler<CropGrowPreEvent> ev) {
      return this.elCropGrowPre.add(ev);
   }

   public boolean hasCropGrowPre() {
      return this.elCropGrowPre.hasHandlers();
   }

   public void publishCropGrowPre(CropGrowPreEvent event) {
      this.elCropGrowPre.publish(event);
   }

   public IEventHandle onCropGrowPost(IEventHandler<CropGrowPostEvent> ev) {
      return this.elCropGrowPost.add(ev);
   }

   public boolean hasCropGrowPost() {
      return this.elCropGrowPost.hasHandlers();
   }

   public void publishCropGrowPost(CropGrowPostEvent event) {
      this.elCropGrowPost.publish(event);
   }

   public IEventHandle onBlockPlace(IEventHandler<BlockPlaceEvent> ev) {
      return this.elBlockPlace.add(ev);
   }

   public boolean hasBlockPlace() {
      return this.elBlockPlace.hasHandlers();
   }

   public void publishBlockPlace(BlockPlaceEvent event) {
      this.elBlockPlace.publish(event);
   }

   public IEventHandle onMobGriefing(IEventHandler<MobGriefingEvent> ev) {
      return this.elMobGriefing.add(ev);
   }

   public boolean hasMobGriefing() {
      return this.elMobGriefing.hasHandlers();
   }

   public void publishMobGriefing(MobGriefingEvent event) {
      this.elMobGriefing.publish(event);
   }

   public IEventHandle onEntityTravelToDimension(IEventHandler<EntityTravelToDimensionEvent> ev) {
      return this.elEntityTravelToDimension.add(ev);
   }

   public boolean hasEntityTravelToDimension() {
      return this.elEntityTravelToDimension.hasHandlers();
   }

   public void publishEntityTravelToDimension(EntityTravelToDimensionEvent event) {
      this.elEntityTravelToDimension.publish(event);
   }

   public IEventHandle onLivingDestroyBlock(IEventHandler<LivingDestroyBlockEvent> ev) {
      return this.elLivingDestroyBlock.add(ev);
   }

   public boolean hasLivingDestroyBlock() {
      return this.elLivingDestroyBlock.hasHandlers();
   }

   public void publishLivingDestroyBlock(LivingDestroyBlockEvent event) {
      this.elLivingDestroyBlock.publish(event);
   }

   public IEventHandle onLivingExperienceDrop(IEventHandler<LivingExperienceDropEvent> ev) {
      return this.elLivingExperienceDrop.add(ev);
   }

   public boolean hasLivingExperienceDrop() {
      return this.elLivingExperienceDrop.hasHandlers();
   }

   public void publishLivingExperienceDrop(LivingExperienceDropEvent event) {
      this.elLivingExperienceDrop.publish(event);
   }

   public IEventHandle onLivingKnockBack(IEventHandler<LivingKnockBackEvent> ev) {
      return this.elLivingKnockBack.add(ev);
   }

   public boolean hasLivingKnockBack() {
      return this.elLivingKnockBack.hasHandlers();
   }

   public void publishLivingKnockBack(LivingKnockBackEvent event) {
      this.elLivingKnockBack.publish(event);
   }

   public IEventHandle onLootingLevel(IEventHandler<LootingLevelEvent> ev) {
      return this.elLootingLevel.add(ev);
   }

   public boolean hasLootingLevel() {
      return this.elLootingLevel.hasHandlers();
   }

   public void publishLootingLevel(LootingLevelEvent event) {
      this.elLootingLevel.publish(event);
   }

   public IEventHandle onMinecartCollision(IEventHandler<MinecartCollisionEvent> ev) {
      return this.elMinecartCollision.add(ev);
   }

   public boolean hasMinecartCollision() {
      return this.elMinecartCollision.hasHandlers();
   }

   public void publishMinecartCollision(MinecartCollisionEvent event) {
      this.elMinecartCollision.publish(event);
   }

   public IEventHandle onMinecartInteract(IEventHandler<MinecartInteractEvent> ev) {
      return this.elMinecartInteract.add(ev);
   }

   public boolean hasMinecartInteract() {
      return this.elMinecartInteract.hasHandlers();
   }

   public void publishMinecartInteract(MinecartInteractEvent event) {
      this.elMinecartInteract.publish(event);
   }

   public IEventHandle onMinecartUpdate(IEventHandler<MinecartUpdateEvent> ev) {
      return this.elMinecartUpdate.add(ev);
   }

   public boolean hasMinecartUpdate() {
      return this.elMinecartUpdate.hasHandlers();
   }

   public void publishMinecartUpdate(MinecartUpdateEvent event) {
      this.elMinecartUpdate.publish(event);
   }

   public IEventHandle onNoteBlock(IEventHandler<INoteBlockEvent> ev) {
      return this.elNoteBlock.add(ev);
   }

   public boolean hasNoteBlock() {
      return this.elNoteBlock.hasHandlers();
   }

   public void publishNoteBlock(INoteBlockEvent event) {
      this.elNoteBlock.publish(event);
   }

   public IEventHandle onNoteBlockChange(IEventHandler<NoteBlockChangeEvent> ev) {
      return this.elNoteBlockChange.add(ev);
   }

   public boolean hasNoteBlockChange() {
      return this.elNoteBlockChange.hasHandlers();
   }

   public void publishNoteBlockChange(NoteBlockChangeEvent event) {
      this.elNoteBlockChange.publish(event);
   }

   public IEventHandle onNoteBlockPlay(IEventHandler<NoteBlockPlayEvent> ev) {
      return this.elNoteBlockPlay.add(ev);
   }

   public boolean hasNoteBlockPlay() {
      return this.elNoteBlockPlay.hasHandlers();
   }

   public void publishNoteBlockPlay(NoteBlockPlayEvent event) {
      this.elNoteBlockPlay.publish(event);
   }

   public IEventHandle onPlayerCloseContainer(IEventHandler<PlayerCloseContainerEvent> ev) {
      return this.elPlayerCloseContainer.add(ev);
   }

   public boolean hasPlayerCloseContainer() {
      return this.elPlayerCloseContainer.hasHandlers();
   }

   public void publishPlayerCloseContainer(PlayerCloseContainerEvent event) {
      this.elPlayerCloseContainer.publish(event);
   }

   public IEventHandle onPlayerItemPickup(IEventHandler<PlayerItemPickupEvent> ev) {
      return this.elPlayerItemPickup.add(ev);
   }

   public boolean hasPlayerItemPickup() {
      return this.elPlayerItemPickup.hasHandlers();
   }

   public void publishPlayerItemPickup(PlayerItemPickupEvent event) {
      this.elPlayerItemPickup.publish(event);
   }

   public IEventHandle onPlayerVisibility(IEventHandler<PlayerVisibilityEvent> ev) {
      return this.elPlayerVisibility.add(ev);
   }

   public boolean hasPlayerVisibility() {
      return this.elPlayerVisibility.hasHandlers();
   }

   public void publishPlayerVisibility(PlayerVisibilityEvent event) {
      this.elPlayerVisibility.publish(event);
   }

   public IEventHandle onPlayerLeftClickBlock(IEventHandler<PlayerLeftClickBlockEvent> ev) {
      return this.elPlayerLeftClickBlock.add(ev);
   }

   public boolean hasPlayerLeftClickBlock() {
      return this.elPlayerLeftClickBlock.hasHandlers();
   }

   public void publishPlayerLeftClickBlock(PlayerLeftClickBlockEvent event) {
      this.elPlayerLeftClickBlock.publish(event);
   }

   public IEventHandle onPlayerRightClickItem(IEventHandler<PlayerRightClickItemEvent> ev) {
      return this.elPlayerRightClickItem.add(ev);
   }

   public boolean hasPlayerRightClickItem() {
      return this.elPlayerRightClickItem.hasHandlers();
   }

   public void publishPlayerRightClickItem(PlayerRightClickItemEvent event) {
      this.elPlayerRightClickItem.publish(event);
   }

   public IEventHandle onSleepingLocationCheck(IEventHandler<SleepingLocationCheckEvent> ev) {
      return this.elSleepingLocationCheck.add(ev);
   }

   public boolean hasSleepingLocationCheck() {
      return this.elSleepingLocationCheck.hasHandlers();
   }

   public void publishSleepingLocationCheck(SleepingLocationCheckEvent event) {
      this.elSleepingLocationCheck.publish(event);
   }

   public IEventHandle onSleepingTimeCheck(IEventHandler<SleepingTimeCheckEvent> ev) {
      return this.elSleepingTimeCheck.add(ev);
   }

   public boolean hasSleepingTimeCheck() {
      return this.elSleepingTimeCheck.hasHandlers();
   }

   public void publishSleepingTimeCheck(SleepingTimeCheckEvent event) {
      this.elSleepingTimeCheck.publish(event);
   }

   public IEventHandle onPotionBrewPre(IEventHandler<PotionBrewPreEvent> ev) {
      return this.elPotionBrewPre.add(ev);
   }

   public boolean hasPotionBrewPre() {
      return this.elPotionBrewPre.hasHandlers();
   }

   public void publishPotionBrewPre(PotionBrewPreEvent event) {
      this.elPotionBrewPre.publish(event);
   }

   public IEventHandle onPotionBrewPost(IEventHandler<PotionBrewPostEvent> ev) {
      return this.elPotionBrewPost.add(ev);
   }

   public boolean hasPotionBrewPost() {
      return this.elPotionBrewPost.hasHandlers();
   }

   public void publishPotionBrewPost(PotionBrewPostEvent event) {
      this.elPotionBrewPost.publish(event);
   }

   public IEventHandle onProjectileImpactArrow(IEventHandler<ProjectileImpactArrowEvent> ev) {
      return this.elProjectileImpactArrow.add(ev);
   }

   public boolean hasProjectileImpactArrow() {
      return this.elProjectileImpactArrow.hasHandlers();
   }

   public void publishProjectileImpactArrow(ProjectileImpactArrowEvent event) {
      this.elProjectileImpactArrow.publish(event);
   }

   public IEventHandle onProjectileImpactFireball(IEventHandler<ProjectileImpactFireballEvent> ev) {
      return this.elProjectileImpactFireball.add(ev);
   }

   public boolean hasProjectileImpactFireball() {
      return this.elProjectileImpactFireball.hasHandlers();
   }

   public void publishProjectileImpactFireball(ProjectileImpactFireballEvent event) {
      this.elProjectileImpactFireball.publish(event);
   }

   public IEventHandle onProjectileImpactThrowable(IEventHandler<ProjectileImpactThrowableEvent> ev) {
      return this.elProjectileImpactThrowable.add(ev);
   }

   public boolean hasProjectileImpactThrowable() {
      return this.elProjectileImpactThrowable.hasHandlers();
   }

   public void publishProjectileImpactThrowable(ProjectileImpactThrowableEvent event) {
      this.elProjectileImpactThrowable.publish(event);
   }

   public IEventHandle onArrowLoose(IEventHandler<ArrowLooseEvent> ev) {
      return this.elArrowLoose.add(ev);
   }

   public boolean hasArrowLoose() {
      return this.elArrowLoose.hasHandlers();
   }

   public void publishArrowLoose(ArrowLooseEvent event) {
      this.elArrowLoose.publish(event);
   }

   public IEventHandle onArrowNock(IEventHandler<ArrowNockEvent> ev) {
      return this.elArrowNock.add(ev);
   }

   public boolean hasArrowNock() {
      return this.elArrowNock.hasHandlers();
   }

   public void publishArrowNock(ArrowNockEvent event) {
      this.elArrowNock.publish(event);
   }

   public IEventHandle onEntityJoinWorld(IEventHandler<EntityJoinWorldEvent> ev) {
      return this.elEntityJoinWorld.add(ev);
   }

   public boolean hasEntityJoinWorld() {
      return this.elEntityJoinWorld.hasHandlers();
   }

   public void publishEntityJoinWorld(EntityJoinWorldEvent event) {
      this.elEntityJoinWorld.publish(event);
   }

   public IEventHandle onEntityLivingEquipmentChange(IEventHandler<EntityLivingEquipmentChangeEvent> ev) {
      return this.elEntityLivingEquipmentChange.add(ev);
   }

   public boolean hasEntityLivingEquipmentChange() {
      return this.elEntityLivingEquipmentChange.hasHandlers();
   }

   public void publishEntityLivingEquipmentChange(EntityLivingEquipmentChangeEvent event) {
      this.elEntityLivingEquipmentChange.publish(event);
   }

   public IEventHandle onEntityLivingDamage(IEventHandler<EntityLivingDamageEvent> ev) {
      return this.elEntityLivingDamage.add(ev);
   }

   public boolean hasEntityLivingDamage() {
      return this.elEntityLivingDamage.hasHandlers();
   }

   public void publishEntityLivingDamage(EntityLivingDamageEvent event) {
      this.elEntityLivingDamage.publish(event);
   }

   public IEventHandle onEntityLivingHeal(IEventHandler<EntityLivingHealEvent> ev) {
      return this.elEntityLivingHeal.add(ev);
   }

   public boolean hasEntityLivingHeal() {
      return this.elEntityLivingHeal.hasHandlers();
   }

   public void publishEntityLivingHeal(EntityLivingHealEvent event) {
      this.elEntityLivingHeal.publish(event);
   }

   public IEventHandle onEntityLivingUpdate(IEventHandler<EntityLivingUpdateEvent> ev) {
      return this.elEntityLivingUpdateEvent.add(ev);
   }

   public boolean hasEntityLivingUpdateEvent() {
      return this.elEntityLivingUpdateEvent.hasHandlers();
   }

   public void publishEntityLivingUpdateEvent(EntityLivingUpdateEvent event) {
      this.elEntityLivingUpdateEvent.publish(event);
   }

   public IEventHandle onPotionEffectAdded(IEventHandler<PotionEffectAddedEvent> ev) {
      return this.elPotionEffectAdded.add(ev);
   }

   public boolean hasPotionEffectAdded() {
      return this.elPotionEffectAdded.hasHandlers();
   }

   public void publishPotionEffectAdded(PotionEffectAddedEvent event) {
      this.elPotionEffectAdded.publish(event);
   }

   public IEventHandle onPotionEffectRemove(IEventHandler<PotionEffectRemoveEvent> ev) {
      return this.elPotionEffectRemove.add(ev);
   }

   public boolean hasPotionEffectRemoveEvent() {
      return this.elPotionEffectRemove.hasHandlers();
   }

   public void publishPotionEffectRemoveEvent(PotionEffectRemoveEvent event) {
      this.elPotionEffectRemove.publish(event);
   }

   public IEventHandle onPotionEffectApplicable(IEventHandler<PotionEffectApplicableEvent> ev) {
      return this.elPotionEffectApplicable.add(ev);
   }

   public boolean hasPotionEffectApplicableEvent() {
      return this.elPotionEffectApplicable.hasHandlers();
   }

   public void publishPotionEffectApplicableEvent(PotionEffectApplicableEvent event) {
      this.elPotionEffectApplicable.publish(event);
   }

   public IEventHandle onPotionEffectExpiry(IEventHandler<PotionEffectExpiryEvent> ev) {
      return this.elPotionEffectExpiry.add(ev);
   }

   public boolean hasPotionEffectExpiryEvent() {
      return this.elPotionEffectExpiry.hasHandlers();
   }

   public void publishPotionEffectExpiryEvent(PotionEffectExpiryEvent event) {
      this.elPotionEffectExpiry.publish(event);
   }

   public IEventHandle onPlayerClone(IEventHandler<PlayerCloneEvent> ev) {
      return this.elPlayerCloneEvent.add(ev);
   }

   public boolean hasPlayerCloneEvent() {
      return this.elPlayerCloneEvent.hasHandlers();
   }

   public void publishPlayerCloneEvent(PlayerCloneEvent event) {
      this.elPlayerCloneEvent.publish(event);
   }

   public IEventHandle onBlockNeighborNotify(IEventHandler<BlockNeighborNotifyEvent> ev) {
      return this.elBlockNeighborNotify.add(ev);
   }

   public boolean hasBlockNeighborNotifyEvent() {
      return this.elBlockNeighborNotify.hasHandlers();
   }

   public void publishBlockNeighborNotifyEvent(BlockNeighborNotifyEvent ev) {
      this.elBlockNeighborNotify.publish(ev);
   }

   public IEventHandle onPortalSpawn(IEventHandler<PortalSpawnEvent> ev) {
      return this.elPortalSpawn.add(ev);
   }

   public boolean hasPortalSpawnEvent() {
      return this.elPortalSpawn.hasHandlers();
   }

   public void publishPortalSpawnEvent(PortalSpawnEvent ev) {
      this.elPortalSpawn.publish(ev);
   }
}
