package crafttweaker.mc1120.events;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.CrafttweakerImplementationAPI;
import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.recipes.CraftingInfo;
import crafttweaker.mc1120.brackets.BracketHandlerBiome;
import crafttweaker.mc1120.brackets.BracketHandlerBiomeType;
import crafttweaker.mc1120.brackets.BracketHandlerEnchantments;
import crafttweaker.mc1120.brackets.BracketHandlerEntity;
import crafttweaker.mc1120.brackets.BracketHandlerItem;
import crafttweaker.mc1120.brackets.BracketHandlerLiquid;
import crafttweaker.mc1120.brackets.BracketHandlerPotion;
import crafttweaker.mc1120.damage.MCDamageSource;
import crafttweaker.mc1120.events.handling.MCAnimalTameEvent;
import crafttweaker.mc1120.events.handling.MCArrowLooseEvent;
import crafttweaker.mc1120.events.handling.MCArrowNockEvent;
import crafttweaker.mc1120.events.handling.MCBlockBreakEvent;
import crafttweaker.mc1120.events.handling.MCBlockFarmlandTrampleEvent;
import crafttweaker.mc1120.events.handling.MCBlockHarvestDropsEvent;
import crafttweaker.mc1120.events.handling.MCBlockNeighborNotifyEvent;
import crafttweaker.mc1120.events.handling.MCBlockPlaceEvent;
import crafttweaker.mc1120.events.handling.MCClientTickEvent;
import crafttweaker.mc1120.events.handling.MCCommandEvent;
import crafttweaker.mc1120.events.handling.MCCriticalHitEvent;
import crafttweaker.mc1120.events.handling.MCCropGrowPostEvent;
import crafttweaker.mc1120.events.handling.MCCropGrowPreEvent;
import crafttweaker.mc1120.events.handling.MCEnchantmentLevelSetEvent;
import crafttweaker.mc1120.events.handling.MCEnderTeleportEvent;
import crafttweaker.mc1120.events.handling.MCEntityJoinWorldEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingAttackedEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingDamageEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingDeathDropsEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingDeathEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingEquipmentChangeEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingFallEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingHealEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingHurtEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingJumpEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingSpawnEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingUpdateEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingUseItemEvent;
import crafttweaker.mc1120.events.handling.MCEntityMountEvent;
import crafttweaker.mc1120.events.handling.MCEntityStruckByLightningEvent;
import crafttweaker.mc1120.events.handling.MCEntityTravelToDimensionEvent;
import crafttweaker.mc1120.events.handling.MCExplosionDetonateEvent;
import crafttweaker.mc1120.events.handling.MCExplosionStartEvent;
import crafttweaker.mc1120.events.handling.MCItemExpireEvent;
import crafttweaker.mc1120.events.handling.MCItemFishedEvent;
import crafttweaker.mc1120.events.handling.MCItemTossEvent;
import crafttweaker.mc1120.events.handling.MCLivingDestroyBlockEvent;
import crafttweaker.mc1120.events.handling.MCLivingExperienceDropEvent;
import crafttweaker.mc1120.events.handling.MCLivingKnockBackEvent;
import crafttweaker.mc1120.events.handling.MCLootingLevelEvent;
import crafttweaker.mc1120.events.handling.MCMinecartCollisionEvent;
import crafttweaker.mc1120.events.handling.MCMinecartInteractEvent;
import crafttweaker.mc1120.events.handling.MCMinecartUpdateEvent;
import crafttweaker.mc1120.events.handling.MCMobGriefingEvent;
import crafttweaker.mc1120.events.handling.MCNoteBlockChangeEvent;
import crafttweaker.mc1120.events.handling.MCNoteBlockEvent;
import crafttweaker.mc1120.events.handling.MCNoteBlockPlayEvent;
import crafttweaker.mc1120.events.handling.MCPlayerAdvancementEvent;
import crafttweaker.mc1120.events.handling.MCPlayerAnvilRepairEvent;
import crafttweaker.mc1120.events.handling.MCPlayerAnvilUpdateEvent;
import crafttweaker.mc1120.events.handling.MCPlayerAttackEntityEvent;
import crafttweaker.mc1120.events.handling.MCPlayerBonemealEvent;
import crafttweaker.mc1120.events.handling.MCPlayerBreakSpeedEvent;
import crafttweaker.mc1120.events.handling.MCPlayerBrewedPotionEvent;
import crafttweaker.mc1120.events.handling.MCPlayerChangedDimensionEvent;
import crafttweaker.mc1120.events.handling.MCPlayerCloneEvent;
import crafttweaker.mc1120.events.handling.MCPlayerCloseContainerEvent;
import crafttweaker.mc1120.events.handling.MCPlayerCraftedEvent;
import crafttweaker.mc1120.events.handling.MCPlayerDeathDropsEvent;
import crafttweaker.mc1120.events.handling.MCPlayerDestroyItemEvent;
import crafttweaker.mc1120.events.handling.MCPlayerFillBucketEvent;
import crafttweaker.mc1120.events.handling.MCPlayerInteractEntityEvent;
import crafttweaker.mc1120.events.handling.MCPlayerInteractEvent;
import crafttweaker.mc1120.events.handling.MCPlayerItemPickupEvent;
import crafttweaker.mc1120.events.handling.MCPlayerLeftClickBlockEvent;
import crafttweaker.mc1120.events.handling.MCPlayerLoggedInEvent;
import crafttweaker.mc1120.events.handling.MCPlayerLoggedOutEvent;
import crafttweaker.mc1120.events.handling.MCPlayerOpenContainerEvent;
import crafttweaker.mc1120.events.handling.MCPlayerPickupItemEvent;
import crafttweaker.mc1120.events.handling.MCPlayerPickupXpEvent;
import crafttweaker.mc1120.events.handling.MCPlayerRespawnEvent;
import crafttweaker.mc1120.events.handling.MCPlayerRightClickBlockEvent;
import crafttweaker.mc1120.events.handling.MCPlayerRightClickItemEvent;
import crafttweaker.mc1120.events.handling.MCPlayerSetSpawnEvent;
import crafttweaker.mc1120.events.handling.MCPlayerSleepInBedEvent;
import crafttweaker.mc1120.events.handling.MCPlayerSmeltedEvent;
import crafttweaker.mc1120.events.handling.MCPlayerTickEvent;
import crafttweaker.mc1120.events.handling.MCPlayerUseHoeEvent;
import crafttweaker.mc1120.events.handling.MCPlayerVisibilityEvent;
import crafttweaker.mc1120.events.handling.MCPlayerWakeUpEvent;
import crafttweaker.mc1120.events.handling.MCPortalSpawnEvent;
import crafttweaker.mc1120.events.handling.MCPotionBrewPostEvent;
import crafttweaker.mc1120.events.handling.MCPotionBrewPreEvent;
import crafttweaker.mc1120.events.handling.MCPotionEffectAddedEvent;
import crafttweaker.mc1120.events.handling.MCPotionEffectApplicableEvent;
import crafttweaker.mc1120.events.handling.MCPotionEffectExpiryEvent;
import crafttweaker.mc1120.events.handling.MCPotionEffectRemoveEvent;
import crafttweaker.mc1120.events.handling.MCProjectileImpactArrowEvent;
import crafttweaker.mc1120.events.handling.MCProjectileImpactFireballEvent;
import crafttweaker.mc1120.events.handling.MCProjectileImpactThrowableEvent;
import crafttweaker.mc1120.events.handling.MCRenderTickEvent;
import crafttweaker.mc1120.events.handling.MCServerTickEvent;
import crafttweaker.mc1120.events.handling.MCSleepingLocationCheckEvent;
import crafttweaker.mc1120.events.handling.MCSleepingTimeCheckEvent;
import crafttweaker.mc1120.events.handling.MCWorldTickEvent;
import crafttweaker.mc1120.furnace.MCFurnaceManager;
import crafttweaker.mc1120.item.MCItemStack;
import crafttweaker.mc1120.oredict.MCOreDictEntry;
import crafttweaker.mc1120.recipes.MCCraftingInventorySquared;
import crafttweaker.mc1120.recipes.MCRecipeManager;
import crafttweaker.runtime.ScriptLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent.Arrow;
import net.minecraftforge.event.entity.ProjectileImpactEvent.Fireball;
import net.minecraftforge.event.entity.ProjectileImpactEvent.Throwable;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Start;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionExpiryEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent;
import net.minecraftforge.event.entity.minecart.MinecartCollisionEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Close;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Open;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.Visibility;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.FarmlandTrampleEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PortalSpawnEvent;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent.Post;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent.Pre;
import net.minecraftforge.event.world.ExplosionEvent.Detonate;
import net.minecraftforge.event.world.NoteBlockEvent.Change;
import net.minecraftforge.event.world.NoteBlockEvent.Play;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

public class CommonEventHandler {
   public CommonEventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void registerRecipes(Register<IRecipe> ev) {
      CraftTweakerAPI.logInfo("CraftTweaker: Building registry");
      BracketHandlerItem.rebuildItemRegistry();
      BracketHandlerLiquid.rebuildLiquidRegistry();
      BracketHandlerEntity.rebuildEntityRegistry();
      BracketHandlerPotion.rebuildRegistry();
      BracketHandlerBiome.rebuildBiomeRegistry();
      BracketHandlerBiomeType.rebuildBiomeTypeRegistry();
      BracketHandlerEnchantments.updateEnchantmentRegistry();
      CraftTweakerAPI.logInfo("CraftTweaker: Successfully built item registry");
      MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Pre());
      ScriptLoader loader = CraftTweakerAPI.tweaker.getOrCreateLoader("crafttweaker", "recipeEvent");
      loader.setMainName("crafttweaker");
      CraftTweakerAPI.tweaker.loadScript(false, loader);
      MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Post());
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
      if (!MCFurnaceManager.fuelMap.isEmpty() && !event.getItemStack().isEmpty()) {
         for(Entry<IItemStack, Integer> entry : MCFurnaceManager.fuelMap.entrySet()) {
            if (((IItemStack)entry.getKey()).matches(MCItemStack.createNonCopy(event.getItemStack()))) {
               event.setBurnTime(((Integer)entry.getValue()).intValue());
            }
         }
      }

   }

   @SubscribeEvent
   public void onPlayerLoggedIn(PlayerLoggedInEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerLoggedIn()) {
         CrafttweakerImplementationAPI.events.publishPlayerLoggedIn(new MCPlayerLoggedInEvent(CraftTweakerMC.getIPlayer(ev.player)));
      }

   }

   @SubscribeEvent
   public void onPlayerRespawn(PlayerRespawnEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerRespawn()) {
         CrafttweakerImplementationAPI.events.publishPlayerRespawn(new MCPlayerRespawnEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEntityItemPickup(EntityItemPickupEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerPickupItem()) {
         CrafttweakerImplementationAPI.events.publishPlayerPickupItem(new MCPlayerPickupItemEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerItemCrafted(ItemCraftedEvent ev) {
      if (ev.craftMatrix instanceof InventoryCrafting && !MCRecipeManager.transformerRecipes.isEmpty()) {
         MCRecipeManager.transformerRecipes.stream().filter(Objects::nonNull).filter((mcRecipeBase) -> {
            return mcRecipeBase.matches((InventoryCrafting)ev.craftMatrix, ev.player.world);
         }).forEach((recipe) -> {
            recipe.applyTransformers((InventoryCrafting)ev.craftMatrix, CraftTweakerMC.getIPlayer(ev.player));
         });
      }

      IPlayer iPlayer = CraftTweakerMC.getIPlayer(ev.player);
      if (ev.craftMatrix instanceof InventoryCrafting && !MCRecipeManager.actionRecipes.isEmpty()) {
         MCRecipeManager.actionRecipes.stream().filter(Objects::nonNull).filter((recipe) -> {
            return recipe.getRecipeOutput().isItemEqual(ev.crafting);
         }).filter((recipe) -> {
            return recipe.matches((InventoryCrafting)ev.craftMatrix, ev.player.world);
         }).forEach((recipe) -> {
            recipe.getRecipeAction().process(CraftTweakerMC.getIItemStack(ev.crafting), new CraftingInfo(new MCCraftingInventorySquared(ev.craftMatrix, iPlayer), iPlayer.getWorld()), iPlayer);
         });
      }

      if (CrafttweakerImplementationAPI.events.hasPlayerCrafted()) {
         CrafttweakerImplementationAPI.events.publishPlayerCrafted(new MCPlayerCraftedEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerItemSmelted(ItemSmeltedEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerSmelted()) {
         CrafttweakerImplementationAPI.events.publishPlayerSmelted(new MCPlayerSmeltedEvent(CraftTweakerMC.getIPlayer(ev.player), CraftTweakerMC.getIItemStack(ev.smelting)));
      }

   }

   @SubscribeEvent
   public void onPlayerLoggedOut(PlayerLoggedOutEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerLoggedOut()) {
         CrafttweakerImplementationAPI.events.publishPlayerLoggedOut(new MCPlayerLoggedOutEvent(CraftTweakerMC.getIPlayer(ev.player)));
      }

   }

   @SubscribeEvent
   public void onPlayerInteract(PlayerInteractEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerInteract()) {
         CrafttweakerImplementationAPI.events.publishPlayerInteract(new MCPlayerInteractEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerInteractEntity(EntityInteract ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerInteractEntity()) {
         CrafttweakerImplementationAPI.events.publishPlayerInteractEntity(new MCPlayerInteractEntityEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerChangedDimension(PlayerChangedDimensionEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerChangedDimension()) {
         CrafttweakerImplementationAPI.events.publishPlayerChangedDimension(new MCPlayerChangedDimensionEvent(ev));
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void mobDrop(LivingDropsEvent ev) {
      Entity entity = ev.getEntity();
      IEntityDefinition entityDefinition = CraftTweakerAPI.game.getEntity(EntityList.getEntityString(ev.getEntity()));
      if (entityDefinition != null) {
         if (entityDefinition.shouldClearDrops()) {
            ev.getDrops().clear();
         } else if (!entityDefinition.getDropsToRemove().isEmpty()) {
            List<EntityItem> removedDrops = new ArrayList();
            entityDefinition.getDropsToRemove().forEach((drop) -> {
               ev.getDrops().forEach((drops) -> {
                  if (drop.matches(new MCItemStack(drops.getItem()))) {
                     removedDrops.add(drops);
                  }

               });
            });
            ev.getDrops().removeAll(removedDrops);
         }

         if (!entityDefinition.getDrops().isEmpty()) {
            Random rand = entity.world.rand;
            entityDefinition.getDrops().forEach((drop) -> {
               if (!drop.isPlayerOnly() || ev.getSource().getTrueSource() instanceof EntityPlayer) {
                  if (drop.getChance() <= 0.0F || drop.getChance() >= 1.0F || rand.nextFloat() <= drop.getChance()) {
                     int count = drop.getMin() == 0 && drop.getMax() == 0 ? drop.getItemStack().getAmount() : drop.getRange().getRandom(rand);
                     if (count != 0) {
                        ev.getDrops().add(new EntityItem(entity.world, entity.posX + 0.5D, entity.posY + 0.5D, entity.posZ + 0.5D, CraftTweakerMC.getItemStack(drop.getItemStack().withAmount(count))));
                     }

                  }
               }
            });
         }

         if (!entityDefinition.getDropFunctions().isEmpty()) {
            IEntity ent = CraftTweakerMC.getIEntity(entity);
            IDamageSource dmgSource = new MCDamageSource(ev.getSource());
            Stream var10000 = entityDefinition.getDropFunctions().stream().map((fun) -> {
               return fun.handle(ent, dmgSource);
            }).filter(Objects::nonNull).filter((item) -> {
               return item.getAmount() > 0;
            }).map(CraftTweakerMC::getItemStack).map((item) -> {
               return new EntityItem(entity.world, entity.posX + 0.5D, entity.posY + 0.5D, entity.posZ + 0.5D, item);
            });
            List var10001 = ev.getDrops();
            var10000.forEach(var10001::add);
         }
      }

      if (CrafttweakerImplementationAPI.events.hasEntityLivingDeathDrops()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingDeathDrops(new MCEntityLivingDeathDropsEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerBonemeal(BonemealEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerBonemeal()) {
         CrafttweakerImplementationAPI.events.publishPlayerBonemeal(new MCPlayerBonemealEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerFillBucket(FillBucketEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerFillBucket()) {
         CrafttweakerImplementationAPI.events.publishPlayerFillBucket(new MCPlayerFillBucketEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerDeathDrops(PlayerDropsEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerDeathDrops()) {
         CrafttweakerImplementationAPI.events.publishPlayerDeathDrops(new MCPlayerDeathDropsEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerSleepInBed(PlayerSleepInBedEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerSleepInBed()) {
         CrafttweakerImplementationAPI.events.publishPlayerSleepInBed(new MCPlayerSleepInBedEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerWakeUp(PlayerWakeUpEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerWakeUp()) {
         CrafttweakerImplementationAPI.events.publishPlayerWakeUp(new MCPlayerWakeUpEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerOpenContainer(Open ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerOpenContainer()) {
         CrafttweakerImplementationAPI.events.publishPlayerOpenContainer(new MCPlayerOpenContainerEvent(ev));
      }

   }

   @SubscribeEvent
   public void onUseHoe(UseHoeEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerUseHoe()) {
         CrafttweakerImplementationAPI.events.publishPlayerUseHoe(new MCPlayerUseHoeEvent(ev));
      }

   }

   @SubscribeEvent
   public void onOreDictEvent(OreRegisterEvent ev) {
      List<IItemStack> ingredients = (List)MCOreDictEntry.REMOVED_CONTENTS.get(ev.getName());
      if (ingredients != null) {
         for(IItemStack ingredient : ingredients) {
            if (ingredient.matches(CraftTweakerMC.getIItemStackForMatching(ev.getOre()))) {
               OreDictionary.getOres(ev.getName()).remove(ev.getOre());
            }
         }
      }

   }

   @SubscribeEvent
   public void onPlayerPickupXp(PlayerPickupXpEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerPickupXp()) {
         CrafttweakerImplementationAPI.events.publishPlayerPickupXp(new MCPlayerPickupXpEvent(ev));
      }

   }

   @SubscribeEvent
   public void onLivingEntityUseItemStartEvent(Start ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingUseItemStart()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingUseItemStart(new MCEntityLivingUseItemEvent.Start(ev));
      }

   }

   @SubscribeEvent
   public void onLivingEntityUseItemStopEvent(Stop ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingUseItemStop()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingUseItemStop(new MCEntityLivingUseItemEvent.Stop(ev));
      }

   }

   @SubscribeEvent
   public void onLivingEntityUseItemTickEvent(Tick ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingUseItemTick()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingUseItemTick(new MCEntityLivingUseItemEvent.Tick(ev));
      }

   }

   @SubscribeEvent
   public void onLivingEntityUseItemFinishEvent(Finish ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingUseItemFinish()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingUseItemFinish(new MCEntityLivingUseItemEvent.Finish(ev));
      }

   }

   @SubscribeEvent
   public void onLivingEntityUseItemEvent(LivingEntityUseItemEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingUseItem()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingUseItem(new MCEntityLivingUseItemEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerAttackEntityEvent(AttackEntityEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerAttackEntity()) {
         CrafttweakerImplementationAPI.events.publishPlayerAttackEntity(new MCPlayerAttackEntityEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEntityStruckByLightningEvent(EntityStruckByLightningEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityStruckByLightning()) {
         CrafttweakerImplementationAPI.events.publishEntityStruckByLightning(new MCEntityStruckByLightningEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEnderTeleportEvent(EnderTeleportEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEnderTeleport()) {
         CrafttweakerImplementationAPI.events.publishEnderTeleport(new MCEnderTeleportEvent(ev));
      }

   }

   @SubscribeEvent
   public void onLivingAttackEvent(LivingAttackEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingAttacked()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingAttacked(new MCEntityLivingAttackedEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEntityLivingDeathEvent(LivingDeathEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingDeath()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingDeath(new MCEntityLivingDeathEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEntityLivingFallEvent(LivingFallEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingFall()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingFall(new MCEntityLivingFallEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEntityLivingHurtEvent(LivingHurtEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingHurt()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingHurt(new MCEntityLivingHurtEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEntityLivingJumpEvent(LivingJumpEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingJump()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingJump(new MCEntityLivingJumpEvent(ev));
      }

   }

   @SubscribeEvent
   public void onItemExpireEvent(ItemExpireEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasItemExpire()) {
         CrafttweakerImplementationAPI.events.publishItemExpire(new MCItemExpireEvent(ev));
      }

   }

   @SubscribeEvent
   public void onItemTossEvent(ItemTossEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasItemToss()) {
         CrafttweakerImplementationAPI.events.publishItemToss(new MCItemTossEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerAnvilRepairEvent(AnvilRepairEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerAnvilRepair()) {
         CrafttweakerImplementationAPI.events.publishPlayerAnvilRepair(new MCPlayerAnvilRepairEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerAnvilUpdateEvent(AnvilUpdateEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerAnvilUpdate()) {
         CrafttweakerImplementationAPI.events.publishPlayerAnvilUpdate(new MCPlayerAnvilUpdateEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerSetSpawnEvent(PlayerSetSpawnEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerSetSpawn()) {
         CrafttweakerImplementationAPI.events.publishPlayerSetSpawn(new MCPlayerSetSpawnEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerDestroyItemEvent(PlayerDestroyItemEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerDestroyItem()) {
         CrafttweakerImplementationAPI.events.publishPlayerDestroyItem(new MCPlayerDestroyItemEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerBrewedPotionEvent(PlayerBrewedPotionEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerBrewedPotion()) {
         CrafttweakerImplementationAPI.events.publishPlayerBrewedPotion(new MCPlayerBrewedPotionEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerTickEvent(PlayerTickEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerTick()) {
         CrafttweakerImplementationAPI.events.publishPlayerTick(new MCPlayerTickEvent(ev));
      }

   }

   @SubscribeEvent
   public void onClientTickEvent(ClientTickEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasClientTick()) {
         CrafttweakerImplementationAPI.events.publishClientTick(new MCClientTickEvent(ev));
      }

   }

   @SubscribeEvent
   public void onServerTickEvent(ServerTickEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasServerTick()) {
         CrafttweakerImplementationAPI.events.publishServerTick(new MCServerTickEvent(ev));
      }

   }

   @SubscribeEvent
   public void onRenderTickEvent(RenderTickEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasRenderTick()) {
         CrafttweakerImplementationAPI.events.publishRenderTick(new MCRenderTickEvent(ev));
      }

   }

   @SubscribeEvent
   public void onWorldTickEvent(WorldTickEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasWorldTick()) {
         CrafttweakerImplementationAPI.events.publishWorldTick(new MCWorldTickEvent(ev));
      }

   }

   @SubscribeEvent
   public void onBlockBreakEvent(BreakEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasBlockBreak()) {
         CrafttweakerImplementationAPI.events.publishBlockBreak(new MCBlockBreakEvent(ev));
      }

   }

   @SubscribeEvent
   public void onBlockHarvestDropsEvent(HarvestDropsEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasBlockHarvestDrops()) {
         CrafttweakerImplementationAPI.events.publishBlockHarvestDrops(new MCBlockHarvestDropsEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerBreakSpeedEvent(BreakSpeed ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerBreakSpeed()) {
         CrafttweakerImplementationAPI.events.publishPlayerBreakSpeed(new MCPlayerBreakSpeedEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerRightClickBlockEvent(RightClickBlock ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerRightClickBlock()) {
         CrafttweakerImplementationAPI.events.publishPlayerRightClickBlock(new MCPlayerRightClickBlockEvent(ev));
      }

   }

   @SubscribeEvent
   public void onCommandEvent(CommandEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasCommand()) {
         CrafttweakerImplementationAPI.events.publishCommand(new MCCommandEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerAdvancementEvent(AdvancementEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerAdvancement()) {
         CrafttweakerImplementationAPI.events.publishPlayerAdvancement(new MCPlayerAdvancementEvent(ev));
      }

   }

   @SubscribeEvent
   public void onCheckSpawnEvent(CheckSpawn ev) {
      if (CrafttweakerImplementationAPI.events.hasCheckSpawn()) {
         CrafttweakerImplementationAPI.events.publishCheckSpawn(new MCEntityLivingSpawnEvent.MCEntityLivingExtendedSpawnEvent(ev));
      }

   }

   @SubscribeEvent
   public void onSpecialSpawnEvent(SpecialSpawn ev) {
      if (CrafttweakerImplementationAPI.events.hasSpecialSpawn()) {
         CrafttweakerImplementationAPI.events.publishSpecialSpawn(new MCEntityLivingSpawnEvent.MCEntityLivingSpecialSpawnEvent(ev));
      }

   }

   @SubscribeEvent
   public void onAllowDespawnEvent(AllowDespawn ev) {
      if (CrafttweakerImplementationAPI.events.hasAllowDespawn()) {
         CrafttweakerImplementationAPI.events.publishAllowDespawn(new MCEntityLivingSpawnEvent(ev));
      }

   }

   @SubscribeEvent
   public void onAnimalTameEvent(AnimalTameEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasAnimalTame()) {
         CrafttweakerImplementationAPI.events.publishAnimalTame(new MCAnimalTameEvent(ev));
      }

   }

   @SubscribeEvent
   public void onFarmlandTrampleEvent(FarmlandTrampleEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasFarmlandTrample()) {
         CrafttweakerImplementationAPI.events.publishFarmlandTrample(new MCBlockFarmlandTrampleEvent(ev));
      }

   }

   @SubscribeEvent
   public void onCriticalHitEvent(CriticalHitEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasCriticalHit()) {
         CrafttweakerImplementationAPI.events.publishCriticalHit(new MCCriticalHitEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEnchantmentLevelSet(EnchantmentLevelSetEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEnchantmentLevelSet()) {
         CrafttweakerImplementationAPI.events.publishEnchantmentLevelSet(new MCEnchantmentLevelSetEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEntityMount(EntityMountEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityMount()) {
         CrafttweakerImplementationAPI.events.publishEntityMount(new MCEntityMountEvent(ev));
      }

   }

   @SubscribeEvent
   public void onExplosionDetonate(Detonate ev) {
      if (CrafttweakerImplementationAPI.events.hasExplosionDetonate()) {
         CrafttweakerImplementationAPI.events.publishExplosionDetonate(new MCExplosionDetonateEvent(ev));
      }

   }

   @SubscribeEvent
   public void onExplosionStart(net.minecraftforge.event.world.ExplosionEvent.Start ev) {
      if (CrafttweakerImplementationAPI.events.hasExplosionStart()) {
         CrafttweakerImplementationAPI.events.publishExplosionStart(new MCExplosionStartEvent(ev));
      }

   }

   @SubscribeEvent
   public void onItemFished(ItemFishedEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasItemFished()) {
         CrafttweakerImplementationAPI.events.publishItemFished(new MCItemFishedEvent(ev));
      }

   }

   @SubscribeEvent
   public void onCropGrowPre(Pre ev) {
      if (CrafttweakerImplementationAPI.events.hasCropGrowPre()) {
         CrafttweakerImplementationAPI.events.publishCropGrowPre(new MCCropGrowPreEvent(ev));
      }

   }

   @SubscribeEvent
   public void onCropGrowPost(Post ev) {
      if (CrafttweakerImplementationAPI.events.hasCropGrowPost()) {
         CrafttweakerImplementationAPI.events.publishCropGrowPost(new MCCropGrowPostEvent(ev));
      }

   }

   @SubscribeEvent
   public void onBlockPlace(PlaceEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasBlockPlace()) {
         CrafttweakerImplementationAPI.events.publishBlockPlace(new MCBlockPlaceEvent(ev));
      }

   }

   @SubscribeEvent
   public void onMobGriefing(EntityMobGriefingEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasMobGriefing()) {
         CrafttweakerImplementationAPI.events.publishMobGriefing(new MCMobGriefingEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEntityTravelToDimension(EntityTravelToDimensionEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityTravelToDimension()) {
         CrafttweakerImplementationAPI.events.publishEntityTravelToDimension(new MCEntityTravelToDimensionEvent(ev));
      }

   }

   @SubscribeEvent
   public void onLivingDestroyBlock(LivingDestroyBlockEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasLivingDestroyBlock()) {
         CrafttweakerImplementationAPI.events.publishLivingDestroyBlock(new MCLivingDestroyBlockEvent(ev));
      }

   }

   @SubscribeEvent
   public void onLivingExperienceDrop(LivingExperienceDropEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasLivingExperienceDrop()) {
         CrafttweakerImplementationAPI.events.publishLivingExperienceDrop(new MCLivingExperienceDropEvent(ev));
      }

   }

   @SubscribeEvent
   public void onLivingKnockBackEvent(LivingKnockBackEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasLivingKnockBack()) {
         CrafttweakerImplementationAPI.events.publishLivingKnockBack(new MCLivingKnockBackEvent(ev));
      }

   }

   @SubscribeEvent
   public void onLootingLevel(LootingLevelEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasLootingLevel()) {
         CrafttweakerImplementationAPI.events.publishLootingLevel(new MCLootingLevelEvent(ev));
      }

   }

   @SubscribeEvent
   public void onMinecartCollision(MinecartCollisionEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasMinecartCollision()) {
         CrafttweakerImplementationAPI.events.publishMinecartCollision(new MCMinecartCollisionEvent(ev));
      }

   }

   @SubscribeEvent
   public void onMinecartInteract(MinecartInteractEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasMinecartInteract()) {
         CrafttweakerImplementationAPI.events.publishMinecartInteract(new MCMinecartInteractEvent(ev));
      }

   }

   @SubscribeEvent
   public void onMinecartUpdate(MinecartUpdateEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasMinecartUpdate()) {
         CrafttweakerImplementationAPI.events.publishMinecartUpdate(new MCMinecartUpdateEvent(ev));
      }

   }

   @SubscribeEvent
   public void onNoteBlock(NoteBlockEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasNoteBlock()) {
         CrafttweakerImplementationAPI.events.publishNoteBlock(new MCNoteBlockEvent(ev));
      }

   }

   @SubscribeEvent
   public void onNoteBlockChange(Change ev) {
      if (CrafttweakerImplementationAPI.events.hasNoteBlockChange()) {
         CrafttweakerImplementationAPI.events.publishNoteBlockChange(new MCNoteBlockChangeEvent(ev));
      }

   }

   @SubscribeEvent
   public void onNoteBlockPlay(Play ev) {
      if (CrafttweakerImplementationAPI.events.hasNoteBlockPlay()) {
         CrafttweakerImplementationAPI.events.publishNoteBlockPlay(new MCNoteBlockPlayEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerCloseContainer(Close ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerCloseContainer()) {
         CrafttweakerImplementationAPI.events.publishPlayerCloseContainer(new MCPlayerCloseContainerEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerItemPickup(ItemPickupEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerItemPickup()) {
         CrafttweakerImplementationAPI.events.publishPlayerItemPickup(new MCPlayerItemPickupEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerVisibility(Visibility ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerVisibility()) {
         CrafttweakerImplementationAPI.events.publishPlayerVisibility(new MCPlayerVisibilityEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerLeftClickBlock(LeftClickBlock ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerLeftClickBlock()) {
         CrafttweakerImplementationAPI.events.publishPlayerLeftClickBlock(new MCPlayerLeftClickBlockEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerRightClickItem(RightClickItem ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerRightClickItem()) {
         CrafttweakerImplementationAPI.events.publishPlayerRightClickItem(new MCPlayerRightClickItemEvent(ev));
      }

   }

   @SubscribeEvent
   public void onSleepingLocationCheck(SleepingLocationCheckEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasSleepingLocationCheck()) {
         CrafttweakerImplementationAPI.events.publishSleepingLocationCheck(new MCSleepingLocationCheckEvent(ev));
      }

   }

   @SubscribeEvent
   public void onSleepingTimeCheck(SleepingTimeCheckEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasSleepingTimeCheck()) {
         CrafttweakerImplementationAPI.events.publishSleepingTimeCheck(new MCSleepingTimeCheckEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPotionBrewPre(net.minecraftforge.event.brewing.PotionBrewEvent.Pre ev) {
      if (CrafttweakerImplementationAPI.events.hasPotionBrewPre()) {
         CrafttweakerImplementationAPI.events.publishPotionBrewPre(new MCPotionBrewPreEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPotionBrewPost(net.minecraftforge.event.brewing.PotionBrewEvent.Post ev) {
      if (CrafttweakerImplementationAPI.events.hasPotionBrewPost()) {
         CrafttweakerImplementationAPI.events.publishPotionBrewPost(new MCPotionBrewPostEvent(ev));
      }

   }

   @SubscribeEvent
   public void onProjectileImpactArrow(Arrow ev) {
      if (CrafttweakerImplementationAPI.events.hasProjectileImpactArrow()) {
         CrafttweakerImplementationAPI.events.publishProjectileImpactArrow(new MCProjectileImpactArrowEvent(ev));
      }

   }

   @SubscribeEvent
   public void onProjectileImpactFireball(Fireball ev) {
      if (CrafttweakerImplementationAPI.events.hasProjectileImpactFireball()) {
         CrafttweakerImplementationAPI.events.publishProjectileImpactFireball(new MCProjectileImpactFireballEvent(ev));
      }

   }

   @SubscribeEvent
   public void onProjectileImpactThrowable(Throwable ev) {
      if (CrafttweakerImplementationAPI.events.hasProjectileImpactThrowable()) {
         CrafttweakerImplementationAPI.events.publishProjectileImpactThrowable(new MCProjectileImpactThrowableEvent(ev));
      }

   }

   @SubscribeEvent
   public void onArrowLoose(ArrowLooseEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasArrowLoose()) {
         CrafttweakerImplementationAPI.events.publishArrowLoose(new MCArrowLooseEvent(ev));
      }

   }

   @SubscribeEvent
   public void onArrowNock(ArrowNockEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasArrowNock()) {
         CrafttweakerImplementationAPI.events.publishArrowNock(new MCArrowNockEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEntityJoinWorld(EntityJoinWorldEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityJoinWorld()) {
         CrafttweakerImplementationAPI.events.publishEntityJoinWorld(new MCEntityJoinWorldEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEntityLivingDamage(LivingDamageEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingDamage()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingDamage(new MCEntityLivingDamageEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEntityLivingEquipmentChange(LivingEquipmentChangeEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingEquipmentChange()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingEquipmentChange(new MCEntityLivingEquipmentChangeEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEntityLivingHeal(LivingHealEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingHeal()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingHeal(new MCEntityLivingHealEvent(ev));
      }

   }

   @SubscribeEvent
   public void onEntityLivingUpdate(LivingUpdateEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasEntityLivingUpdateEvent()) {
         CrafttweakerImplementationAPI.events.publishEntityLivingUpdateEvent(new MCEntityLivingUpdateEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPotionEffectAdded(PotionAddedEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPotionEffectAdded()) {
         CrafttweakerImplementationAPI.events.publishPotionEffectAdded(new MCPotionEffectAddedEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPotionEffectRemove(PotionRemoveEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPotionEffectRemoveEvent()) {
         CrafttweakerImplementationAPI.events.publishPotionEffectRemoveEvent(new MCPotionEffectRemoveEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPotionEffectApplicable(PotionApplicableEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPotionEffectApplicableEvent()) {
         CrafttweakerImplementationAPI.events.publishPotionEffectApplicableEvent(new MCPotionEffectApplicableEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPotionEffectExpiry(PotionExpiryEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPotionEffectExpiryEvent()) {
         CrafttweakerImplementationAPI.events.publishPotionEffectExpiryEvent(new MCPotionEffectExpiryEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPlayerClone(Clone ev) {
      if (CrafttweakerImplementationAPI.events.hasPlayerCloneEvent()) {
         CrafttweakerImplementationAPI.events.publishPlayerCloneEvent(new MCPlayerCloneEvent(ev));
      }

   }

   @SubscribeEvent
   public void onBlockNeighborNotify(NeighborNotifyEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasBlockNeighborNotifyEvent()) {
         CrafttweakerImplementationAPI.events.publishBlockNeighborNotifyEvent(new MCBlockNeighborNotifyEvent(ev));
      }

   }

   @SubscribeEvent
   public void onPortalSpawn(PortalSpawnEvent ev) {
      if (CrafttweakerImplementationAPI.events.hasPortalSpawnEvent()) {
         CrafttweakerImplementationAPI.events.publishPortalSpawnEvent(new MCPortalSpawnEvent(ev));
      }

   }
}
