package xzeroair.trinkets.events;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Start;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.Vip.VipStatus;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.capabilities.statushandler.StatusHandler;
import xzeroair.trinkets.capabilities.statushandler.TrinketStatusEffect;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.races.EntityRacePropertiesHandler;
import xzeroair.trinkets.races.faelis.config.FaelisConfig;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.traits.abilities.interfaces.IItemUseAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ConfigHelper;
import xzeroair.trinkets.util.helpers.PotionHelper;
import xzeroair.trinkets.util.helpers.StringUtils;

public class EventHandler extends EventBaseHandler {
   public EventHandler() {
   }

   @SubscribeEvent
   public void playerUpdate(PlayerTickEvent event) {
      EntityPlayer entity = event.player;
      if (entity.isEntityAlive() && entity.getEntityWorld() != null) {
         if (event.phase == Phase.START) {
            try {
               Capabilities.getEntityProperties(entity, EntityProperties::onUpdatePre);
            } catch (Exception var5) {
               var5.printStackTrace();
            }
         } else {
            if (TrinketsConfig.SERVER.misc.retrieveVIP) {
               try {
                  Capabilities.getVipStatus(entity, VipStatus::onUpdate);
               } catch (Exception var4) {
                  var4.printStackTrace();
               }
            }

            this.raceHandlerTick(entity);
            this.effectHandlerTick(entity);
            this.magicHandlerTick(entity);
         }

      }
   }

   @SubscribeEvent
   public void EntityUpdate(LivingUpdateEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (!(entity instanceof EntityPlayer)) {
         this.raceHandlerTick(entity);
         this.effectHandlerTick(entity);
         this.magicHandlerTick(entity);
      }

   }

   private void raceHandlerTick(EntityLivingBase entity) {
      try {
         Capabilities.getEntityProperties(entity, EntityProperties::onUpdate);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   private void magicHandlerTick(EntityLivingBase entity) {
      try {
         Capabilities.getMagicStats(entity, MagicStats::onUpdate);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   private void effectHandlerTick(EntityLivingBase entity) {
      try {
         Capabilities.getStatusHandler(entity, StatusHandler::onUpdate);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   @SubscribeEvent
   public void makeNoise(PlaySoundAtEntityEvent event) {
      Entity entity = event.getEntity();
      if (event.getSound() != null && entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         boolean client = player.world.isRemote;
         Capabilities.getEntityProperties(player, (prop) -> {
            if (!prop.isNormalSize() && (event.getSound() == SoundEvents.BLOCK_STONE_STEP || event.getSound() == SoundEvents.BLOCK_GRASS_STEP || event.getSound() == SoundEvents.BLOCK_CLOTH_STEP || event.getSound() == SoundEvents.BLOCK_WOOD_STEP || event.getSound() == SoundEvents.BLOCK_GRAVEL_STEP || event.getSound() == SoundEvents.BLOCK_SNOW_STEP || event.getSound() == SoundEvents.BLOCK_GLASS_STEP || event.getSound() == SoundEvents.BLOCK_METAL_STEP || event.getSound() == SoundEvents.BLOCK_ANVIL_STEP || event.getSound() == SoundEvents.BLOCK_LADDER_STEP || event.getSound() == SoundEvents.BLOCK_SLIME_STEP) && !client) {
               if (!event.getEntity().isSprinting()) {
                  event.setVolume(0.0F);
               } else {
                  event.setVolume(0.1F);
               }
            }

         });
      }

   }

   @SubscribeEvent
   public void potionApply(PotionApplicableEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (entity.isEntityAlive() && entity.getEntityWorld() != null) {
         Capabilities.getEntityProperties(entity, (prop) -> {
            boolean cancel = false;
            Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();
            if (prop.getRaceHandler().potionBeingApplied(event.getPotionEffect())) {
               cancel = true;
            }

            if (!cancel) {
               for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
                  String key = (String)entry.getKey();
                  AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

                  try {
                     IAbilityInterface handler = holder.getAbility();
                     if (handler instanceof IPotionAbility) {
                        cancel = ((IPotionAbility)handler).potionApplied(entity, event.getPotionEffect(), cancel);
                     }
                  } catch (Exception var10) {
                     Trinkets.log.error("Trinkets had an Error with Potion Ability:" + key);
                     var10.printStackTrace();
                  }
               }
            }

            if (cancel) {
               event.setResult(Result.DENY);
            }

         });
      }
   }

   @SubscribeEvent
   public void playerInteract(PlayerInteractEvent event) {
      Capabilities.getEntityProperties(event.getEntityLiving(), (prop) -> {
         try {
            prop.getRaceHandler().interact(event);
         } catch (Exception var3) {
            var3.printStackTrace();
         }

      });
   }

   @SubscribeEvent
   public void playerInteractEntity(EntityInteract event) {
      Capabilities.getEntityProperties(event.getEntityLiving(), (prop) -> {
         prop.getRaceHandler().interactWithEntity(event);
      });
   }

   @SubscribeEvent
   public void playerInteractEntitySpecific(EntityInteractSpecific event) {
   }

   @SubscribeEvent
   public void playerRightClickBlock(RightClickBlock event) {
   }

   @SubscribeEvent
   public void playerLeftClickBlock(LeftClickBlock event) {
   }

   @SubscribeEvent
   public void playerRightClickItem(RightClickItem event) {
   }

   @SubscribeEvent
   public void onMount(EntityMountEvent event) {
      if (event.getEntityMounting() instanceof EntityLivingBase) {
         EntityLivingBase entity = (EntityLivingBase)event.getEntityMounting();
         Capabilities.getEntityProperties(entity, (prop) -> {
            EntityRacePropertiesHandler handler = prop.getRaceHandler();

            try {
               boolean bool = event.isMounting() ? !handler.mountEntity(event.getEntityBeingMounted()) : !handler.dismountedEntity(event.getEntityBeingMounted());
               if (bool) {
                  this.cancelEvent(event);
               }
            } catch (Exception var5) {
               var5.printStackTrace();
            }

         });
      }

   }

   @SubscribeEvent
   public void onItemStartUsingEvent(Start event) {
      EntityLivingBase entity = event.getEntityLiving();
      Capabilities.getEntityProperties(entity, (prop) -> {
         ItemStack stack = event.getItem();
         int duration = event.getDuration();
         int dur = duration;
         Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface handler = holder.getAbility();
               if (handler instanceof IItemUseAbility) {
                  if (dur < 0) {
                     break;
                  }

                  dur = ((IItemUseAbility)handler).onItemStartUse(entity, stack, dur);
               }
            } catch (Exception var12) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var12.printStackTrace();
            }
         }

         if (duration != dur) {
            event.setDuration(dur);
         }

      });
   }

   @SubscribeEvent
   public void onItemUsingTickEvent(Tick event) {
      EntityLivingBase entity = event.getEntityLiving();
      Capabilities.getEntityProperties(entity, (prop) -> {
         ItemStack stack = event.getItem();
         int duration = event.getDuration();
         int dur = duration;
         Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface handler = holder.getAbility();
               if (handler instanceof IItemUseAbility) {
                  if (dur < 0) {
                     break;
                  }

                  dur = ((IItemUseAbility)handler).onItemUseTick(entity, stack, dur);
               }
            } catch (Exception var12) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var12.printStackTrace();
            }
         }

         prop.getRaceHandler().bowDrawing(stack, dur);
         if (duration != dur) {
            event.setDuration(dur);
         }

      });
   }

   @SubscribeEvent
   public void onItemStopUseEvent(Stop event) {
      EntityLivingBase entity = event.getEntityLiving();
      Capabilities.getEntityProperties(entity, (prop) -> {
         ItemStack stack = event.getItem();
         int duration = event.getDuration();
         Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface handler = holder.getAbility();
               if (handler instanceof IItemUseAbility) {
                  ((IItemUseAbility)handler).onItemUseStop(entity, stack, duration);
               }
            } catch (Exception var11) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var11.printStackTrace();
            }
         }

      });
   }

   @SubscribeEvent
   public void onItemFinishUseEvent(Finish event) {
      EntityLivingBase entity = event.getEntityLiving();
      ItemStack stack = event.getItem();
      Item item = stack.getItem();
      String regName = item.getRegistryName().toString();
      String ModID = item.getRegistryName().getResourceDomain();
      String ItemID = item.getRegistryName().getResourcePath();
      int duration = event.getDuration();
      Capabilities.getEntityProperties(entity, (prop) -> {
         Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface handler = holder.getAbility();
               if (handler instanceof IItemUseAbility) {
                  ((IItemUseAbility)handler).onItemUseFinish(entity, stack, duration);
               }
            } catch (Exception var26) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var26.printStackTrace();
            }
         }

         try {
            FaelisConfig faelisConfig = TrinketsConfig.SERVER.races.faelis;
            if (prop.getCurrentRace().equals(EntityRaces.faelis) && faelisConfig.Invigorated) {
               StatusHandler status = Capabilities.getStatusHandler(entity);
               if (status != null) {
                  String[] milkList = faelisConfig.milk;

                  for(String milk : milkList) {
                     String[] itemConfig = milk.split(";");
                     String itemString = StringUtils.getStringFromArray(itemConfig, 0);
                     String metaString = StringUtils.getStringFromArray(itemConfig, 1);
                     String levelString = StringUtils.getStringFromArray(itemConfig, 2);
                     String durationString = StringUtils.getStringFromArray(itemConfig, 3);
                     if (event.getItem().getItem().getRegistryName().toString().equalsIgnoreCase(itemString)) {
                        int meta = metaString.isEmpty() ? 32767 : Integer.parseInt(metaString);
                        int level = levelString.isEmpty() ? 0 : Integer.parseInt(levelString);
                        int Iduration = durationString.isEmpty() ? faelisConfig.Invigorated_Duration : Integer.parseInt(durationString);
                        if (meta == 32767 || event.getItem().getMetadata() == meta) {
                           status.apply(new TrinketStatusEffect("Invigorated", Iduration, level, (Entity)null));
                           if (!entity.world.isRemote) {
                              for(String potID : faelisConfig.buffs) {
                                 PotionHelper.PotionHolder potion = PotionHelper.getPotionHolder(potID);
                                 if (potion.getPotion() != null) {
                                    entity.addPotionEffect(potion.getPotionEffect());
                                 }
                              }
                           }
                        }
                        break;
                     }
                  }
               }
            }
         } catch (Exception var27) {
            var27.printStackTrace();
         }

      });
      if (TrinketsConfig.SERVER.mana.mana_enabled) {
         try {
            TreeMap<String, ConfigHelper.MPRecoveryItem> MagicRecoveryItems = ConfigHelper.TrinketConfigStorage.MagicRecoveryItems;
            float amount = 0.0F;
            boolean multiplied = false;

            for(ConfigHelper.MPRecoveryItem entry : MagicRecoveryItems.values()) {
               if (entry.doesItemMatchEntry(stack)) {
                  amount = entry.getAmount();
                  multiplied = entry.isMultiplied();
                  break;
               }
            }

            if (amount > 0.0F || amount < 0.0F) {
               Capabilities.getMagicStats(entity, (magic) -> {
                  if (finalAmount > 0.0F) {
                     if (finalMultiplied) {
                        magic.addMana(magic.getMaxMana() * finalAmount * 0.01F);
                     } else {
                        magic.addMana(finalAmount);
                     }
                  } else if (finalMultiplied) {
                     magic.spendMana(magic.getMaxMana() * finalAmount * 0.01F);
                  } else {
                     magic.spendMana(finalAmount);
                  }

               });
            }
         } catch (Exception var14) {
            var14.printStackTrace();
         }
      }

   }
}
