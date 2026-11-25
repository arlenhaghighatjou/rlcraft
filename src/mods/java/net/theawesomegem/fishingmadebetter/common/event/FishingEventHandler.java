package net.theawesomegem.fishingmadebetter.common.event;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.world.ChunkEvent.Load;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.theawesomegem.fishingmadebetter.BetterFishUtil;
import net.theawesomegem.fishingmadebetter.common.capability.fishing.FishPopulation;
import net.theawesomegem.fishingmadebetter.common.capability.fishing.FishingCapabilityProvider;
import net.theawesomegem.fishingmadebetter.common.capability.fishing.IFishingData;
import net.theawesomegem.fishingmadebetter.common.capability.world.ChunkCapabilityProvider;
import net.theawesomegem.fishingmadebetter.common.capability.world.IChunkFishingData;
import net.theawesomegem.fishingmadebetter.common.capability.world.PopulationData;
import net.theawesomegem.fishingmadebetter.common.configuration.ConfigurationManager;
import net.theawesomegem.fishingmadebetter.common.configuration.CustomConfigurationHandler;
import net.theawesomegem.fishingmadebetter.common.data.FishCaughtData;
import net.theawesomegem.fishingmadebetter.common.data.FishData;
import net.theawesomegem.fishingmadebetter.common.data.WeightedRandomFishPopulation;
import net.theawesomegem.fishingmadebetter.common.entity.EntityFMBCustomFishHook;
import net.theawesomegem.fishingmadebetter.common.entity.EntityFMBLavaFishHook;
import net.theawesomegem.fishingmadebetter.common.entity.EntityFMBVoidFishHook;
import net.theawesomegem.fishingmadebetter.common.item.ItemBaitBucket;
import net.theawesomegem.fishingmadebetter.common.item.attachment.hook.ItemHook;
import net.theawesomegem.fishingmadebetter.common.item.fishingrod.ItemBetterFishingRod;
import net.theawesomegem.fishingmadebetter.common.item.tracker.ItemFishTracker;
import net.theawesomegem.fishingmadebetter.common.loottable.LootHandler;
import net.theawesomegem.fishingmadebetter.common.networking.PrimaryPacketHandler;
import net.theawesomegem.fishingmadebetter.common.networking.packet.PacketFishingHandshakeS;
import net.theawesomegem.fishingmadebetter.common.networking.packet.PacketKeybindS;
import net.theawesomegem.fishingmadebetter.common.networking.packet.PacketReelingC;
import net.theawesomegem.fishingmadebetter.common.registry.RegistryManager;
import net.theawesomegem.fishingmadebetter.util.ItemStackUtil;
import net.theawesomegem.fishingmadebetter.util.LevelUpLoot;
import net.theawesomegem.fishingmadebetter.util.MathUtil;
import net.theawesomegem.fishingmadebetter.util.RandomUtil;
import net.theawesomegem.fishingmadebetter.util.TimeUtil;

public class FishingEventHandler {
   static final Field currentStateField = ObfuscationReflectionHelper.findField(EntityFishHook.class, "currentState");
   static final Field biteIntervalField = ObfuscationReflectionHelper.findField(EntityFishHook.class, "ticksCatchable");
   static final Field delayBeforeBiteField = ObfuscationReflectionHelper.findField(EntityFishHook.class, "ticksCatchableDelay");
   static final Field delayBeforeSwimmingToHookField = ObfuscationReflectionHelper.findField(EntityFishHook.class, "ticksCaughtDelay");

   public FishingEventHandler() {
   }

   @SubscribeEvent
   public void onPlayerFish(ItemFishedEvent e) {
      EntityPlayer player = e.getEntityPlayer();
      if (player != null && player.isEntityAlive()) {
         World world = player.world;
         if (!world.isRemote) {
            if (!this.usingVanillaFishingRod(player)) {
               IFishingData fishingData = (IFishingData)player.getCapability(FishingCapabilityProvider.FISHING_DATA_CAP, (EnumFacing)null);
               if (fishingData == null) {
                  e.setCanceled(true);
               } else {
                  IChunkFishingData chunkFishingData = (IChunkFishingData)world.getChunkFromBlockCoords(e.getHookEntity().getPosition()).getCapability(ChunkCapabilityProvider.CHUNK_FISHING_DATA_CAP, (EnumFacing)null);
                  if (chunkFishingData == null) {
                     e.setCanceled(true);
                  } else if (!fishingData.isFishing()) {
                     e.setCanceled(true);
                  } else {
                     FishCaughtData fishCaughtData = fishingData.getFishCaughtData();
                     if (fishCaughtData == null) {
                        e.setCanceled(true);
                     } else {
                        e.setCanceled(true);
                        if ((ConfigurationManager.server.skipMinigame || fishingData.getFishDistance() >= fishingData.getFishDeepLevel() - 10) && ((PopulationData)chunkFishingData.getFishes(world.getTotalWorldTime()).get(fishCaughtData.fishId)).getQuantity() > 0) {
                           ItemStack fishingRod = this.getBetterFishingRod(player);
                           ItemHook hookAttachment = ItemBetterFishingRod.getHookItem(fishingRod);
                           int weightModifier = hookAttachment.getWeightModifier();
                           int treasureChance = ConfigurationManager.server.baseTreasureChance + hookAttachment.getTreasureModifier();
                           EntityFishHook hook = e.getHookEntity();
                           ItemStack treasure = ItemStack.EMPTY;
                           if (treasureChance > RandomUtil.getRandomInRange(player.getRNG(), 0, 100)) {
                              Builder lootBuilder = new Builder((WorldServer)world);
                              lootBuilder.withLuck((float)EnchantmentHelper.getFishingLuckBonus(fishingRod) + player.getLuck()).withPlayer(player).withLootedEntity(hook);
                              List<ItemStack> result = world.getLootTableManager().getLootTableFromLocation(LootHandler.FMB_COMBINED).generateLootForPools(player.getRNG(), lootBuilder.build());
                              if (result.size() > 0) {
                                 treasure = (ItemStack)result.get(0);
                              }
                           }

                           ItemStack fishStack = this.getFishItemStack(fishCaughtData, world.getTotalWorldTime(), weightModifier);
                           EntityItem entityFishItem = new EntityItem(player.world, hook.posX, hook.posY, hook.posZ, fishStack);
                           if (ConfigurationManager.server.magneticFishing) {
                              entityFishItem.setPosition(player.posX, player.posY + 1.0D, player.posZ);
                           } else {
                              double d0 = player.posX - hook.posX;
                              double d1 = player.posY - hook.posY;
                              double d2 = player.posZ - hook.posZ;
                              double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                              entityFishItem.motionX = d0 * 0.1D;
                              entityFishItem.motionY = d1 * 0.1D + (double)MathHelper.sqrt(d3) * 0.08D;
                              entityFishItem.motionZ = d2 * 0.1D;
                           }

                           player.world.spawnEntity(entityFishItem);
                           if (!treasure.isEmpty()) {
                              EntityItem entityTreasureItem = new EntityItem(player.world, hook.posX, hook.posY, hook.posZ, treasure);
                              if (ConfigurationManager.server.magneticFishing) {
                                 entityTreasureItem.setPosition(player.posX, player.posY + 1.0D, player.posZ);
                              } else {
                                 double d0 = player.posX - hook.posX;
                                 double d1 = player.posY - hook.posY;
                                 double d2 = player.posZ - hook.posZ;
                                 double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                                 entityTreasureItem.motionX = d0 * 0.1D;
                                 entityTreasureItem.motionY = d1 * 0.1D + (double)MathHelper.sqrt(d3) * 0.08D;
                                 entityTreasureItem.motionZ = d2 * 0.1D;
                              }

                              player.world.spawnEntity(entityTreasureItem);
                              player.sendStatusMessage(new TextComponentTranslation("notif.fishingmadebetter.treasure", new Object[0]), true);
                           }

                           if (Loader.isModLoaded("levelup2") && ConfigurationManager.server.levelUpPatch) {
                              LevelUpLoot.doLevelUpLoot(hook, world, player);
                           }

                           chunkFishingData.reducePopulation(fishCaughtData.fishId, 1, world.getTotalWorldTime(), true);
                           player.world.spawnEntity(new EntityXPOrb(player.world, player.posX, player.posY + 0.5D, player.posZ + 0.5D, player.getRNG().nextInt(6) + 1));
                        }

                        fishingData.reset();
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onPlayerTickClient(PlayerTickEvent e) {
      EntityPlayer player = e.player;
      if (player.world.isRemote && e.phase != Phase.END) {
         IFishingData fishingData = (IFishingData)player.getCapability(FishingCapabilityProvider.FISHING_DATA_CAP, (EnumFacing)null);
         if (fishingData != null) {
            if (fishingData.isFishing()) {
               PrimaryPacketHandler.INSTANCE.sendToServer(new PacketFishingHandshakeS(true));
            }

         }
      }
   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent e) {
      EntityPlayer player = e.player;
      if (!player.world.isRemote && e.phase != Phase.END) {
         if (player.ticksExisted % 5 == 0) {
            this.checkForFishInventory(player);
         }

         IFishingData fishingData = (IFishingData)player.getCapability(FishingCapabilityProvider.FISHING_DATA_CAP, (EnumFacing)null);
         if (fishingData != null) {
            this.checkTracking(player, fishingData);
            this.updateFishingData(player, fishingData);
            EntityFishHook hook = player.fishEntity;
            if (hook != null && !hook.isDead) {
               if (player.getHeldItem(EnumHand.MAIN_HAND) != null && player.getHeldItem(EnumHand.OFF_HAND) != null && player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemFishingRod && player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemFishingRod) {
                  fishingData.reset();
                  hook.setDead();
               } else if (this.usingVanillaFishingRod(player)) {
                  fishingData.setUsingVanillaRod(true);
                  fishingData.reset();
               } else if (fishingData.getUsingVanillaRod()) {
                  fishingData.setUsingVanillaRod(false);
                  fishingData.reset();
                  hook.setDead();
               } else {
                  ItemStack betterFishingRodStack = this.getBetterFishingRod(player);
                  if (betterFishingRodStack != null && !betterFishingRodStack.isEmpty()) {
                     if (fishingData.getTimeBeforeFishSwimToHook() == 0) {
                        if (this.getDelayBeforeSwimmingToHook(hook) > 20) {
                           this.setLureSpeed(hook, 20);
                        }
                     } else {
                        this.setLureSpeed(hook, 200);
                     }

                     Enum hookState = this.getHookState(hook);
                     if (hookState != null && hookState.equals(FishingEventHandler.HookState.BOBBING)) {
                        if (fishingData.isFishing()) {
                           int distanceTime = fishingData.getFishDistanceTime();
                           if (distanceTime <= 0) {
                              distanceTime = Math.min(10, 1 + fishingData.getFishWeight() / 100);
                              int reelDiff = fishingData.getReelTarget() - fishingData.getReelAmount();
                              reelDiff = Math.abs(reelDiff);
                              int distanceMod = reelDiff <= fishingData.getErrorVariance() ? ItemBetterFishingRod.getReelItem(betterFishingRodStack).getReelSpeed() : -1;
                              fishingData.setFishDistance(Math.min(fishingData.getFishDeepLevel(), Math.max(0, fishingData.getFishDistance() + distanceMod)));
                           }

                           fishingData.setFishDistanceTime(distanceTime - 1);
                        }

                        World world = hook.world;
                        Chunk chunk = world.getChunkFromBlockCoords(hook.getPosition());
                        IChunkFishingData chunkFishingData = this.getChunkFishingData(chunk);
                        if (fishingData.getFishPopulation() == null && !fishingData.isFishing()) {
                           boolean justChecked = false;
                           if (world.getWorldTime() % 60L == 0L) {
                              fishingData.setFishPopulation(this.getSelectedFishPop(player, chunkFishingData));
                              justChecked = true;
                           }

                           if (fishingData.getFishPopulation() == null) {
                              fishingData.reset();
                              if (justChecked && world.getWorldTime() % 180L == 0L) {
                                 player.sendStatusMessage(new TextComponentTranslation("notif.fishingmadebetter.failure.empty", new Object[0]), true);
                              }

                              return;
                           }
                        }

                        if (fishingData.getTimeBeforeFishSwimToHook() == -1 && !fishingData.isFishing()) {
                           fishingData.setTimeBeforeFishSwimToHook(this.getLureSpeed(player, fishingData.getFishPopulation()));
                        }

                        int delayBeforeBite = this.getDelayBeforeBite(hook);
                        if (delayBeforeBite > 0 && delayBeforeBite <= 10) {
                           Boolean caughtTick = false;
                           if (fishingData.getLastFailedFishing() <= 0 && !fishingData.isFishing()) {
                              if (hook instanceof EntityFMBLavaFishHook) {
                                 player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundCategory.PLAYERS, 1.0F, 1.0F);
                              } else if (hook instanceof EntityFMBVoidFishHook) {
                                 player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDEREYE_DEATH, SoundCategory.PLAYERS, 1.0F, 1.0F);
                              } else {
                                 player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, RegistryManager.RegistryHandler.FISH_SPLASHING_EVENT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                              }

                              if (ConfigurationManager.server.looseBait) {
                                 ItemBetterFishingRod.removeBait(betterFishingRodStack);
                                 if (player.getHeldItemMainhand().getItem() instanceof ItemBaitBucket || player.getHeldItemOffhand().getItem() instanceof ItemBaitBucket) {
                                    ItemStack baitBucket = player.getHeldItemMainhand().getItem() instanceof ItemBaitBucket ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
                                    if (!ItemBaitBucket.getBaitId(baitBucket).isEmpty()) {
                                       ItemBetterFishingRod.setBaitItem(betterFishingRodStack, ItemBaitBucket.getBaitId(baitBucket));
                                       ItemBetterFishingRod.setBaitMetadata(betterFishingRodStack, ItemBaitBucket.getBaitMetadata(baitBucket));
                                       ItemBetterFishingRod.setBaitDisplayName(betterFishingRodStack, ItemBaitBucket.getBaitDisplayName(baitBucket));
                                       ItemBaitBucket.setBaitCount(baitBucket, ItemBaitBucket.getBaitCount(baitBucket) - 1);
                                       if (ItemBaitBucket.getBaitCount(baitBucket) <= 0) {
                                          ItemBaitBucket.removeBait(baitBucket);
                                       }
                                    }
                                 }
                              }

                              Integer[] minigameBackground = new Integer[5];
                              minigameBackground[0] = world.provider.getDimension();
                              minigameBackground[1] = hook.posY < 56.0D ? 1 : 0;
                              minigameBackground[2] = world.isDaytime() ? 0 : 1;
                              if (hook instanceof EntityFMBLavaFishHook) {
                                 minigameBackground[3] = Integer.valueOf(1);
                              } else if (hook instanceof EntityFMBVoidFishHook) {
                                 minigameBackground[3] = Integer.valueOf(2);
                              } else {
                                 minigameBackground[3] = Integer.valueOf(0);
                              }

                              minigameBackground[4] = this.textureIndexFromBiome(chunk.getBiome(hook.getPosition(), world.getBiomeProvider()));
                              fishingData.startFishing(player.getRNG(), betterFishingRodStack, minigameBackground);
                              player.sendStatusMessage(new TextComponentTranslation("notif.fishingmadebetter.bite", new Object[0]), true);
                              caughtTick = true;
                           }

                           if (player.ticksExisted % 3 == 0 && !caughtTick.booleanValue() && fishingData.isFishing() && (ConfigurationManager.server.skipMinigame || ConfigurationManager.server.autoReel && fishingData.getFishDistance() == fishingData.getFishDeepLevel() && fishingData.getFishDistance() != 0)) {
                              EnumHand hand = null;
                              if (player.getHeldItemMainhand().getItem() instanceof ItemBetterFishingRod) {
                                 hand = EnumHand.MAIN_HAND;
                              } else if (player.getHeldItemOffhand().getItem() instanceof ItemBetterFishingRod) {
                                 hand = EnumHand.OFF_HAND;
                              }

                              if (hand != null) {
                                 ((ItemBetterFishingRod)player.getHeldItem(hand).getItem()).onItemRightClick(world, player, hand);
                              }
                           }

                           if (fishingData.getLineBreak() < 60) {
                              this.spawnParticleBasedOnFishSpeed(player.world, hook, fishingData);
                              this.setBiteInterval(hook, 20);
                           } else {
                              if (fishingData.isFishing() && fishingData.getLineBreak() == 60) {
                                 player.sendStatusMessage(new TextComponentTranslation("notif.fishingmadebetter.failure.snap", new Object[0]), true);
                              }

                              this.setBiteInterval(hook, 0);
                              fishingData.setLastFailedFishing(20);
                              fishingData.reset();
                           }
                        } else {
                           fishingData.reset(false);
                        }

                     } else {
                        fishingData.reset();
                     }
                  } else {
                     fishingData.reset();
                  }
               }
            } else {
               fishingData.setUsingVanillaRod(false);
               fishingData.reset();
            }
         }
      }
   }

   @SubscribeEvent
   public void onChunkLoad(Load e) {
      if (!e.getWorld().isRemote) {
         Chunk chunk = e.getChunk();
         IChunkFishingData chunkFishingData = this.getChunkFishingData(chunk);
         chunkFishingData.chunkLoad(chunk);
      }
   }

   @SubscribeEvent
   public void onItemToss(ItemTossEvent event) {
      ItemStack stack = event.getEntityItem().getItem();
      if (stack.getItem() instanceof ItemBetterFishingRod) {
         if (ConfigurationManager.server.charmSalvagePatch && Loader.isModLoaded("charm") && stack.getItemDamage() >= stack.getMaxDamage()) {
            ItemBetterFishingRod.removeReelItem(stack);
            ItemBetterFishingRod.removeBobberItem(stack);
            ItemBetterFishingRod.removeHookItem(stack);
         }

      }
   }

   private int textureIndexFromBiome(Biome biome) {
      if (BiomeDictionary.hasType(biome, Type.MUSHROOM)) {
         return 8;
      } else if (!BiomeDictionary.hasType(biome, Type.DEAD) && !BiomeDictionary.hasType(biome, Type.SPOOKY)) {
         if (BiomeDictionary.hasType(biome, Type.JUNGLE)) {
            return 1;
         } else if (BiomeDictionary.hasType(biome, Type.SWAMP)) {
            return 7;
         } else if (!BiomeDictionary.hasType(biome, Type.HOT) && !BiomeDictionary.hasType(biome, Type.SANDY)) {
            if (BiomeDictionary.hasType(biome, Type.COLD)) {
               return 3;
            } else if (BiomeDictionary.hasType(biome, Type.MOUNTAIN)) {
               return 4;
            } else if (BiomeDictionary.hasType(biome, Type.FOREST)) {
               return 5;
            } else {
               return !BiomeDictionary.hasType(biome, Type.PLAINS) && !BiomeDictionary.hasType(biome, Type.RIVER) ? 0 : 6;
            }
         } else {
            return 2;
         }
      } else {
         return 9;
      }
   }

   private void checkForFishInventory(EntityPlayer player) {
      InventoryPlayer playerInventory = player.inventory;
      long currentTime = player.world.getTotalWorldTime();

      for(int i = 0; i < playerInventory.getSizeInventory(); ++i) {
         ItemStack itemStack = playerInventory.getStackInSlot(i);
         if (!itemStack.isEmpty() && BetterFishUtil.isBetterFish(itemStack)) {
            List<String> tooltipList = new ArrayList();
            tooltipList.add(String.format("Weight: %d", BetterFishUtil.getFishWeight(itemStack)));
            if (((FishData)CustomConfigurationHandler.fishDataMap.get(BetterFishUtil.getFishId(itemStack))).allowScaling) {
               tooltipList.add(String.format("Scale: %s", BetterFishUtil.doesFishHasScale(itemStack) ? "Attached" : "Detached"));
            }

            tooltipList.add(TextFormatting.BLUE + "" + TextFormatting.BOLD + (BetterFishUtil.isDead(itemStack, currentTime) ? "Dead" : "Alive") + TextFormatting.RESET);
            itemStack = ItemStackUtil.appendToolTip(itemStack, tooltipList);
            if (BetterFishUtil.isDead(itemStack, currentTime)) {
               BetterFishUtil.setFishCaughtTime(itemStack, 0);
            }
         }
      }

   }

   private void checkTracking(EntityPlayer player, IFishingData fishingData) {
      if (fishingData.getTimeSinceTracking() > 0L) {
         ItemStack mainHandItem = player.getHeldItem(EnumHand.MAIN_HAND);
         ItemStack offHandItem = player.getHeldItem(EnumHand.OFF_HAND);
         if (!(mainHandItem.getItem() instanceof ItemFishTracker) && !(offHandItem.getItem() instanceof ItemFishTracker)) {
            fishingData.setTimeSinceTracking(0L);
            player.sendStatusMessage(new TextComponentTranslation("notif.fishingmadebetter.tracker.nohand", new Object[0]), true);
         } else if (mainHandItem.getItem() instanceof ItemFishTracker && offHandItem.getItem() instanceof ItemFishTracker) {
            fishingData.setTimeSinceTracking(0L);
            player.sendStatusMessage(new TextComponentTranslation("notif.fishingmadebetter.tracker.twohand", new Object[0]), true);
         } else {
            ItemStack fishTrackerItem = mainHandItem.getItem() instanceof ItemFishTracker ? mainHandItem : offHandItem;
            fishingData.setTimeSinceTracking(fishingData.getTimeSinceTracking() + 2L);
            long maxTrackingTime = player.world.getTotalWorldTime() + TimeUtil.secondsToMinecraftTicks(ConfigurationManager.server.baitBoxUpdateInterval);
            if (fishingData.getTimeSinceTracking() >= maxTrackingTime) {
               fishingData.setTimeSinceTracking(0L);
               this.getTrackingFish(player, (ItemFishTracker)fishTrackerItem.getItem());
               player.sendStatusMessage(new TextComponentTranslation("notif.fishingmadebetter.tracker.finish", new Object[0]), true);
            }
         }

      }
   }

   private void getTrackingFish(EntityPlayer player, ItemFishTracker item) {
      World world = player.world;
      Chunk chunk = world.getChunkFromBlockCoords(player.getPosition());
      IChunkFishingData chunkFishingData = this.getChunkFishingData(chunk);
      if (chunkFishingData != null) {
         boolean limitInfo = !player.isSneaking();
         boolean creative = player.isCreative();
         boolean fishFound = false;
         int underYCount = 0;
         int trackerQualityCount = 0;
         int hibernatingCount = 0;
         player.sendMessage(new TextComponentString("-----"));

         for(PopulationData populationData : chunkFishingData.getFishes(world.getTotalWorldTime()).values()) {
            FishData fishData = (FishData)CustomConfigurationHandler.fishDataMap.get(populationData.getFishType());
            if (fishData != null) {
               if (creative) {
                  player.sendMessage(new TextComponentString(String.format("%s %s in %s at Y%s-%s", populationData.getQuantity(), fishData.fishId, fishData.liquid.toString(), fishData.minYLevel, fishData.maxYLevel)));
                  player.sendMessage(new TextComponentString(String.format("MinLine %sm, Time %s, MaxLight %s, Rain %s, Thunder %s", fishData.minDeepLevel, fishData.time.toString(), fishData.maxLightLevel, fishData.rainRequired, fishData.thunderRequired)));
               } else if (fishData.trackable && (fishData.liquid.equals(FishData.FishingLiquid.ANY) || item.getLiquidEnum().equals(fishData.liquid)) && (double)fishData.minYLevel <= player.posY) {
                  fishFound = true;
                  if ((double)fishData.maxYLevel < player.posY) {
                     ++underYCount;
                  } else if (fishData.minDeepLevel > item.getMaxDepth()) {
                     ++trackerQualityCount;
                  } else if (fishData.rarity < ItemFishTracker.TrackingVision.getMinRarity(item.getTrackingVision())) {
                     ++trackerQualityCount;
                  } else if ((!fishData.time.equals(FishData.TimeToFish.DAY) || world.isDaytime()) && (!fishData.time.equals(FishData.TimeToFish.NIGHT) || !world.isDaytime())) {
                     if (fishData.maxLightLevel < world.getLight(player.getPosition())) {
                        ++hibernatingCount;
                     } else if ((!fishData.rainRequired || world.isRaining()) && (!fishData.thunderRequired || world.isThundering())) {
                        int pop = populationData.getQuantity();
                        String quantity;
                        if (pop > 50) {
                           quantity = "notif.fishingmadebetter.fish_tracker.quantity_immense";
                        } else if (pop > 40) {
                           quantity = "notif.fishingmadebetter.fish_tracker.quantity_abundant";
                        } else if (pop > 30) {
                           quantity = "notif.fishingmadebetter.fish_tracker.quantity_ample";
                        } else if (pop > 20) {
                           quantity = "notif.fishingmadebetter.fish_tracker.quantity_substantial";
                        } else if (pop > 10) {
                           quantity = "notif.fishingmadebetter.fish_tracker.quantity_numerous";
                        } else if (pop > 3) {
                           quantity = "notif.fishingmadebetter.fish_tracker.quantity_light";
                        } else if (pop > 1) {
                           quantity = "notif.fishingmadebetter.fish_tracker.quantity_sparse";
                        } else {
                           quantity = "notif.fishingmadebetter.fish_tracker.quantity_meager";
                        }

                        if (limitInfo) {
                           player.sendMessage((new TextComponentTranslation("notif.fishingmadebetter.fish_tracker.detected", new Object[0])).appendText(" " + fishData.fishId + "."));
                        } else {
                           player.sendMessage((new TextComponentTranslation("notif.fishingmadebetter.fish_tracker.detected", new Object[0])).appendText(" " + fishData.fishId + ", " + fishData.description + " in ").appendSibling(new TextComponentTranslation(quantity, new Object[0])));
                        }
                     } else {
                        ++hibernatingCount;
                     }
                  } else {
                     ++hibernatingCount;
                  }
               }
            }
         }

         if (creative) {
            player.sendMessage(new TextComponentString("-----"));
         } else {
            if (!fishFound) {
               player.sendMessage(new TextComponentTranslation("notif.fishingmadebetter.fish_tracker.found_none", new Object[0]));
            } else {
               if (underYCount > 0) {
                  player.sendMessage((new TextComponentTranslation("notif.fishingmadebetter.fish_tracker.found_under_y", new Object[0])).appendText(": " + underYCount + "."));
               }

               if (trackerQualityCount > 0) {
                  player.sendMessage((new TextComponentTranslation("notif.fishingmadebetter.fish_tracker.found_outside_quality", new Object[0])).appendText(": " + trackerQualityCount + "."));
               }

               if (hibernatingCount > 0) {
                  player.sendMessage((new TextComponentTranslation("notif.fishingmadebetter.fish_tracker.found_hibernating", new Object[0])).appendText(": " + hibernatingCount + "."));
               }
            }

            player.sendMessage(new TextComponentString("-----"));
         }
      }
   }

   private int getDistanceTime(int weight) {
      return (int)(1.0D + Math.cbrt((double)(weight * weight)) / 6.0D);
   }

   private ItemStack getFishItemStack(FishCaughtData fishCaughtData, long currentTime, int weightModifier) {
      Item item = Item.getByNameOrId(fishCaughtData.itemId);
      if (item == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemStack = new ItemStack(item, 1, fishCaughtData.itemMetaData);
         if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
         }

         NBTTagCompound tagCompound = itemStack.getTagCompound();
         tagCompound.setString("FishId", fishCaughtData.fishId);
         tagCompound.setInteger("FishWeight", (int)((float)fishCaughtData.weight * (1.0F + (float)weightModifier / 100.0F)));
         tagCompound.setLong("FishCaughtTime", currentTime);
         if (((FishData)CustomConfigurationHandler.fishDataMap.get(fishCaughtData.fishId)).allowScaling) {
            tagCompound.setBoolean("FishScale", true);
         }

         itemStack.setTagCompound(tagCompound);
         itemStack.setStackDisplayName(TextFormatting.RESET + fishCaughtData.fishId);
         List<String> tooltipList = new ArrayList();
         tooltipList.add(String.format("Weight: %d", fishCaughtData.weight));
         if (itemStack.getTagCompound().hasKey("FishScale")) {
            tooltipList.add("Scale: Attached");
         }

         tooltipList.add(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Alive" + TextFormatting.RESET);
         itemStack = ItemStackUtil.appendToolTip(itemStack, tooltipList);
         return itemStack;
      }
   }

   private void spawnParticleBasedOnFishSpeed(World world, EntityFishHook hook, IFishingData fishingData) {
      if (!world.isRemote) {
         WorldServer worldServer = (WorldServer)world;
         int fishTime = fishingData.getLastFishTime();
         int minFishTime = 80;
         float numberOfParticles;
         double particleSpeed;
         if (MathUtil.inRange(fishTime, minFishTime, minFishTime + 80)) {
            numberOfParticles = 40.0F;
            particleSpeed = 0.40000000298023225D;
         } else if (MathUtil.inRange(fishTime, minFishTime + 81, minFishTime + 139)) {
            numberOfParticles = 30.0F;
            particleSpeed = 0.3000000029802322D;
         } else if (MathUtil.inRange(fishTime, minFishTime + 140, minFishTime + 175)) {
            numberOfParticles = 30.0F;
            particleSpeed = 0.3000000029802322D;
         } else if (MathUtil.inRange(fishTime, minFishTime + 176, minFishTime + 200)) {
            numberOfParticles = 20.0F;
            particleSpeed = 0.20000000298023224D;
         } else if (MathUtil.inRange(fishTime, minFishTime + 201, minFishTime + 230)) {
            numberOfParticles = 5.0F;
            particleSpeed = 0.10000000298023223D;
         } else {
            numberOfParticles = 2.0F;
            particleSpeed = 0.05000000298023224D;
         }

         double yPos = hook.getEntityBoundingBox().minY + 0.5D;
         if (hook instanceof EntityFMBCustomFishHook) {
            worldServer.spawnParticle(((EntityFMBCustomFishHook)hook).getBubbleParticle(), hook.posX, yPos, hook.posZ, (int)(1.0F + hook.width * numberOfParticles), (double)hook.width, 0.0D, (double)hook.width, particleSpeed, new int[0]);
            worldServer.spawnParticle(((EntityFMBCustomFishHook)hook).getWakeParticle(), hook.posX, yPos, hook.posZ, (int)(1.0F + hook.width * numberOfParticles), (double)hook.width, 0.0D, (double)hook.width, particleSpeed, new int[0]);
         } else {
            worldServer.spawnParticle(EnumParticleTypes.WATER_BUBBLE, hook.posX, yPos, hook.posZ, (int)(1.0F + hook.width * numberOfParticles), (double)hook.width, 0.0D, (double)hook.width, particleSpeed, new int[0]);
            worldServer.spawnParticle(EnumParticleTypes.WATER_WAKE, hook.posX, yPos, hook.posZ, (int)(1.0F + hook.width * numberOfParticles), (double)hook.width, 0.0D, (double)hook.width, particleSpeed, new int[0]);
         }

      }
   }

   private void updateFishingData(EntityPlayer player, IFishingData fishingData) {
      if (fishingData.getLastFailedFishing() > 0) {
         fishingData.setLastFailedFishing(fishingData.getLastFailedFishing() - 1);
      }

      if (fishingData.getTimeBeforeFishSwimToHook() > 0) {
         fishingData.setTimeBeforeFishSwimToHook(fishingData.getTimeBeforeFishSwimToHook() - 1 - (player.world.isRainingAt(player.getPosition()) ? player.getRNG().nextInt(2) : 0));
      }

      if (fishingData.isFishing()) {
         ItemStack betterFishingRodStack = this.getBetterFishingRod(player);
         int fishSpeed = 4 + (int)((float)this.getDistanceTime(fishingData.getFishWeight()) + 0.5F);
         int tensioningSpeed = 8;
         fishSpeed = fishSpeed - ItemBetterFishingRod.getHookItem(betterFishingRodStack).getTuggingReduction();
         tensioningSpeed = tensioningSpeed + ItemBetterFishingRod.getBobberItem(betterFishingRodStack).getTensioningModifier();
         this.updateReelTarget(player.getRNG(), fishingData, fishSpeed);
         this.updateReelPosition(fishingData, tensioningSpeed, player);
         this.updateLineBreak(fishingData);
         PrimaryPacketHandler.INSTANCE.sendTo(new PacketReelingC(fishingData.getReelAmount(), fishingData.getReelTarget(), fishingData.getFishDistance(), fishingData.getFishDeepLevel(), fishingData.getErrorVariance(), fishingData.isFishing(), fishingData.getMinigameBackground()[0].intValue(), fishingData.getMinigameBackground()[1].intValue(), fishingData.getMinigameBackground()[2].intValue(), fishingData.getMinigameBackground()[3].intValue(), fishingData.getMinigameBackground()[4].intValue(), fishingData.getLineBreak()), (EntityPlayerMP)player);
      }

   }

   private void updateReelTarget(EaglercraftRandom random, IFishingData fishingData, int fishSpeed) {
      int prevTugging = fishingData.getFishTugging();
      if (prevTugging == 0) {
         fishSpeed = fishSpeed * (random.nextInt(3) - 1) * Math.max(0, RandomUtil.getRandomInRange(random, -this.getDistanceTime(fishingData.getFishWeight()), 1));
      } else {
         fishSpeed = fishSpeed * (int)Math.signum((float)prevTugging) * (int)Math.signum((float)RandomUtil.getRandomInRange(random, -1, 20));
      }

      fishingData.setFishTugging(fishSpeed);
      int fishMomentum = fishingData.getFishMomentum();
      fishMomentum = fishMomentum + (int)Math.signum((float)(fishSpeed - fishMomentum));
      fishingData.setFishMomentum(fishMomentum);
      fishingData.setReelTarget(fishingData.getReelTarget() + fishMomentum);
   }

   private void updateReelPosition(IFishingData fishingData, int tensioningSpeed, EntityPlayer player) {
      if (fishingData.getKeybind().equals(PacketKeybindS.Keybind.REEL_IN)) {
         tensioningSpeed = tensioningSpeed * -1;
      } else if (fishingData.getKeybind().equals(PacketKeybindS.Keybind.REEL_OUT)) {
         tensioningSpeed = tensioningSpeed * 1;
      } else {
         tensioningSpeed = tensioningSpeed * 0;
      }

      int tensionMomentum = fishingData.getTensionMomentum();
      tensionMomentum = tensionMomentum + (int)Math.signum((float)(tensioningSpeed - tensionMomentum));
      fishingData.setTensionMomentum(tensionMomentum);
      fishingData.setReelAmount(fishingData.getReelAmount() + tensionMomentum);
      this.playReelingSound(tensionMomentum, player);
   }

   private void updateLineBreak(IFishingData fishingData) {
      fishingData.setLineBreak(fishingData.getLineBreak() + (fishingData.getFishDistance() == 0 ? 1 : -1));
   }

   private void playReelingSound(int momentum, EntityPlayer player) {
      if (momentum != 0 && player.ticksExisted % 2 == 0) {
         float pitch = Math.min(2.0F, Math.max(0.0F, (float)Math.abs(momentum) / 10.0F));
         player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, RegistryManager.RegistryHandler.REELING_EVENT, SoundCategory.PLAYERS, pitch, 2.0F);
      }
   }

   private IChunkFishingData getChunkFishingData(Chunk chunk) {
      return (IChunkFishingData)chunk.getCapability(ChunkCapabilityProvider.CHUNK_FISHING_DATA_CAP, (EnumFacing)null);
   }

   private int getLureSpeed(EntityPlayer player, FishPopulation population) {
      ItemStack itemFishingRod = this.getBetterFishingRod(player);
      int pop = population.population;
      int rate = (int)(ConfigurationManager.server.baseTimeToBobber * ((100.0F - (float)ItemBetterFishingRod.getHookItem(itemFishingRod).getBiteRateModifier()) / 100.0F));
      rate = rate - 100 * EnchantmentHelper.getFishingSpeedBonus(itemFishingRod);
      rate = rate - 4 * pop;
      FishData fishData = (FishData)CustomConfigurationHandler.fishDataMap.get(population.fishId);
      String baitItem = ItemBetterFishingRod.getBaitItem(itemFishingRod);
      if (ItemBetterFishingRod.hasBait(itemFishingRod) && (ConfigurationManager.server.simpleBait || fishData.baitItemMap.containsKey(baitItem) && Arrays.asList(fishData.baitItemMap.get(baitItem)).contains(Integer.valueOf(ItemBetterFishingRod.getBaitMetadata(itemFishingRod))))) {
         rate -= RandomUtil.getRandomInRange(player.getRNG(), 100, 300);
      }

      rate = Math.max(60, rate);
      return rate;
   }

   private FishPopulation getSelectedFishPop(EntityPlayer player, IChunkFishingData chunkFishingData) {
      if (!chunkFishingData.hasFishes()) {
         return null;
      } else {
         World world = player.world;
         EntityFishHook hook = player.fishEntity;
         FishData.FishingLiquid liquid = FishData.FishingLiquid.WATER;
         if (hook instanceof EntityFMBLavaFishHook) {
            liquid = FishData.FishingLiquid.LAVA;
         } else if (hook instanceof EntityFMBVoidFishHook) {
            liquid = FishData.FishingLiquid.VOID;
         }

         if (liquid == FishData.FishingLiquid.WATER) {
            int waterCount = 0;

            for(BlockPos pos : BlockPos.getAllInBox((int)hook.posX - 2, (int)hook.posY - 3, (int)hook.posZ - 2, (int)hook.posX + 2, (int)hook.posY, (int)hook.posZ + 2)) {
               Material mat = world.getBlockState(pos).getMaterial();
               if (mat == MaterialLiquid.WATER) {
                  ++waterCount;
               }

               if (waterCount >= 25) {
                  break;
               }
            }

            if (waterCount < 25) {
               return null;
            }
         }

         if (liquid == FishData.FishingLiquid.LAVA) {
            int lavaCount = 0;

            for(BlockPos pos : BlockPos.getAllInBox((int)hook.posX - 2, (int)hook.posY - 3, (int)hook.posZ - 2, (int)hook.posX + 2, (int)hook.posY, (int)hook.posZ + 2)) {
               Material mat = world.getBlockState(pos).getMaterial();
               if (mat == MaterialLiquid.LAVA) {
                  ++lavaCount;
               }

               if (lavaCount >= 25) {
                  break;
               }
            }

            if (lavaCount < 25) {
               return null;
            }
         }

         ItemStack itemFishingRod = this.getBetterFishingRod(player);
         boolean hasFishBait = ItemBetterFishingRod.hasBait(itemFishingRod);
         Map<String, PopulationData> fishPopulationMap = chunkFishingData.getFishes(world.getTotalWorldTime());
         List<WeightedRandomFishPopulation> weightedRandomFishPopulationList = new ArrayList();

         for(PopulationData populationData : fishPopulationMap.values()) {
            FishData fishData = (FishData)CustomConfigurationHandler.fishDataMap.get(populationData.getFishType());
            if (fishData != null && (!fishData.time.equals(FishData.TimeToFish.DAY) || world.isDaytime()) && (!fishData.time.equals(FishData.TimeToFish.NIGHT) || !world.isDaytime()) && (fishData.liquid.equals(liquid) || fishData.liquid.equals(FishData.FishingLiquid.ANY)) && fishData.minDeepLevel <= ItemBetterFishingRod.getReelItem(itemFishingRod).getReelRange() && (double)fishData.minYLevel <= hook.posY && (double)fishData.maxYLevel >= hook.posY && fishData.maxLightLevel >= world.getLight(hook.getPosition()) && (!fishData.rainRequired || world.isRaining()) && (!fishData.thunderRequired || world.isThundering())) {
               int rarity = fishData.rarity;
               if (!ConfigurationManager.server.simpleBait && hasFishBait && ItemBetterFishingRod.isBaitForFish(itemFishingRod, populationData.getFishType())) {
                  rarity += RandomUtil.getRandomInRange(player.getRNG(), 25, 75);
               }

               weightedRandomFishPopulationList.add(new WeightedRandomFishPopulation(rarity, populationData.getFishType(), populationData.getQuantity()));
            }
         }

         if (weightedRandomFishPopulationList.size() <= 0) {
            return null;
         } else {
            WeightedRandomFishPopulation randomFishPopulation = (WeightedRandomFishPopulation)WeightedRandom.getRandomItem(world.rand, weightedRandomFishPopulationList);
            return new FishPopulation(randomFishPopulation.fishId, randomFishPopulation.population);
         }
      }
   }

   private ItemStack getBetterFishingRod(EntityPlayer player) {
      if (this.usingVanillaFishingRod(player)) {
         return ItemStack.EMPTY;
      } else {
         ItemStack mainHandItem = player.getHeldItemMainhand();
         ItemStack offHandItem = player.getHeldItemOffhand();
         if (!mainHandItem.isEmpty() && mainHandItem.getItem() instanceof ItemBetterFishingRod) {
            return mainHandItem;
         } else {
            return !offHandItem.isEmpty() && offHandItem.getItem() instanceof ItemBetterFishingRod ? offHandItem : ItemStack.EMPTY;
         }
      }
   }

   private boolean usingVanillaFishingRod(EntityPlayer player) {
      ItemStack mainHandItem = player.getHeldItemMainhand();
      ItemStack offHandItem = player.getHeldItemOffhand();
      if (!mainHandItem.isEmpty()) {
         if (mainHandItem.getItem() instanceof ItemBetterFishingRod) {
            return false;
         }

         if (mainHandItem.getItem() instanceof ItemFishingRod) {
            return true;
         }
      }

      if (!offHandItem.isEmpty()) {
         if (offHandItem.getItem() instanceof ItemBetterFishingRod) {
            return false;
         }

         if (offHandItem.getItem() instanceof ItemFishingRod) {
            return true;
         }
      }

      return false;
   }

   private void setLureSpeed(EntityFishHook hook, int ticks) {
      if (delayBeforeSwimmingToHookField != null) {
         try {
            delayBeforeSwimmingToHookField.setAccessible(true);
            delayBeforeSwimmingToHookField.setInt(hook, ticks);
            delayBeforeSwimmingToHookField.setAccessible(false);
         } catch (IllegalAccessException | IllegalArgumentException var4) {
            delayBeforeSwimmingToHookField.setAccessible(false);
            var4.printStackTrace();
         }

      }
   }

   private int getDelayBeforeSwimmingToHook(EntityFishHook hook) {
      if (delayBeforeSwimmingToHookField == null) {
         return -1;
      } else {
         try {
            delayBeforeSwimmingToHookField.setAccessible(true);
            int delay = delayBeforeSwimmingToHookField.getInt(hook);
            delayBeforeSwimmingToHookField.setAccessible(false);
            return delay;
         } catch (IllegalAccessException | IllegalArgumentException var3) {
            delayBeforeSwimmingToHookField.setAccessible(false);
            var3.printStackTrace();
            return -1;
         }
      }
   }

   private int getDelayBeforeBite(EntityFishHook hook) {
      if (delayBeforeBiteField == null) {
         return -1;
      } else {
         try {
            delayBeforeBiteField.setAccessible(true);
            int delay = delayBeforeBiteField.getInt(hook);
            delayBeforeBiteField.setAccessible(false);
            return delay;
         } catch (IllegalAccessException | IllegalArgumentException var3) {
            delayBeforeBiteField.setAccessible(false);
            var3.printStackTrace();
            return -1;
         }
      }
   }

   private void setBiteInterval(EntityFishHook hook, int time) {
      if (biteIntervalField != null) {
         try {
            biteIntervalField.setAccessible(true);
            biteIntervalField.setInt(hook, time);
            biteIntervalField.setAccessible(false);
         } catch (IllegalAccessException | IllegalArgumentException var4) {
            biteIntervalField.setAccessible(false);
            var4.printStackTrace();
         }

      }
   }

   private FishingEventHandler.HookState getHookState(EntityFishHook hook) {
      if (currentStateField == null) {
         return null;
      } else {
         try {
            currentStateField.setAccessible(true);
            Enum hookEnum = (Enum)currentStateField.get(hook);
            int hookStateOrdinal = hookEnum.ordinal();
            currentStateField.setAccessible(false);
            return FishingEventHandler.HookState.values()[hookStateOrdinal];
         } catch (IllegalAccessException var4) {
            currentStateField.setAccessible(false);
            var4.printStackTrace();
            return null;
         }
      }
   }

   static enum HookState {
      FLYING,
      HOOKED_IN_ENTITY,
      BOBBING;

      private HookState() {
      }
   }
}
