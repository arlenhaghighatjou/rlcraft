package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.IceAndFireReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.LootUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootingEnchantBonus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.registries.GameData;

public class IceAndFireHandler {
   private IceAndFireReflect reflector;

   public IceAndFireHandler() {
      try {
         this.reflector = new IceAndFireReflect();
         if (ModConfig.server.iceandfire.rightHandDragonInteraction) {
            CompatUtil.wrapSpecificHandler("IAFUseItem", (x$0) -> {
               new IceAndFireHandler.IAFUseItem(x$0);
            }, "com.github.alexthe666.iceandfire.event.EventLiving", "onEntityUseItem");
            CompatUtil.wrapSpecificHandler("IAFUseItem", (x$0) -> {
               new IceAndFireHandler.IAFUseItem(x$0);
            }, "com.github.alexthe666.iceandfire.event.ServerEvents", "onEntityUseItem");
         }

         if (ModConfig.server.iceandfire.rlcraftTradeBalancing) {
            this.attemptRemoveRLCraftTrades();
         }

         MinecraftForge.EVENT_BUS.register(this);
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup IceAndFireHandler!", var2);
         ErrorUtil.logSilent("IceAndFireHandler Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   @SubscribeEvent
   public void onEntityInteract(EntityInteract event) {
      if (ModConfig.server.iceandfire.rightHandDragonInteraction && event.getHand() == EnumHand.OFF_HAND && this.reflector.c_ItemDragonHornStatic != null && this.reflector.c_EntityDragonBase.isInstance(event.getTarget())) {
         ItemStack offhand = event.getEntityPlayer().getHeldItemOffhand();
         if (offhand.isEmpty()) {
            return;
         }

         if (this.reflector.c_ItemDragonHornStatic.isInstance(offhand.getItem())) {
            event.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void onMobGriefing(EntityMobGriefingEvent event) {
      if (ModConfig.server.iceandfire.statueMobGriefingFix && event.getEntity() instanceof EntityLiving) {
         EntityLiving entity = (EntityLiving)event.getEntity();
         if (!entity.world.isRemote && entity.canPickUpLoot() && this.reflector.getIsStone(entity)) {
            event.setResult(Result.DENY);
         }
      }

   }

   @SubscribeEvent
   public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
      if (ModConfig.server.iceandfire.statueDataFixes && !event.getWorld().isRemote && event.getEntity() instanceof EntityItem) {
         EntityItem itemEntity = (EntityItem)event.getEntity();
         if (this.reflector.c_ItemStoneStatue.isInstance(itemEntity.getItem().getItem())) {
            ItemStack readStack = itemEntity.getItem();
            if (readStack.getTagCompound() != null) {
               EntityEntry entry = (EntityEntry)GameData.getEntityRegistry().getValue(readStack.getTagCompound().getInteger("IAFStoneStatueEntityID"));
               Class<? extends Entity> entityClazz = entry.getEntityClass();
               if (entityClazz != null) {
                  ResourceLocation entityResource = EntityList.getKey(entityClazz);
                  if (entityResource != null && entityResource.toString().equals("lycanitesmobs:kobold")) {
                     ItemStack writeStack = readStack.copy();
                     writeStack.getTagCompound().removeTag("Items");
                     itemEntity.setItem(writeStack);
                  }
               }
            }
         }
      }

      if (ModConfig.server.iceandfire.myrmexQueenTradeFix && !event.getWorld().isRemote && this.reflector.c_EntityMyrmexQueen.isInstance(event.getEntity())) {
         Entity myrmexBase = event.getEntity();

         try {
            MerchantRecipeList trades = this.reflector.getMyrmexTrades(myrmexBase);
            if (trades != null) {
               boolean myrmexIsJungle = this.reflector.isMyrmexJungle(myrmexBase);
               boolean hasJungle = false;
               boolean hasDesert = false;

               for(MerchantRecipe trade : trades) {
                  String reg = trade.getItemToBuy().getItem().getRegistryName().toString();
                  if (reg.equals("iceandfire:myrmex_desert_resin")) {
                     hasDesert = true;
                  } else if (reg.equals("iceandfire:myrmex_jungle_resin")) {
                     hasJungle = true;
                  }
               }

               if (myrmexIsJungle && hasDesert || !myrmexIsJungle && hasJungle) {
                  RLTweaker.logger.info("Clearing broken myrmex queen trades: " + myrmexBase.getPosition().toString());
                  this.reflector.resetMyrmexTrades(myrmexBase);
               }
            }
         } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException var10) {
            ErrorUtil.logSilent("IceAndFireHandler Myrmex Queen Trades Failure");
         }
      }

   }

   @SubscribeEvent
   public void onLootTableLoad(LootTableLoadEvent event) {
      if (ModConfig.server.iceandfire.deathwormEggsIgnoreLooting) {
         String var2 = event.getName().toString();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1757188841:
            if (var2.equals("iceandfire:deathworm_tan_giant")) {
               var3 = 2;
            }
            break;
         case -848745983:
            if (var2.equals("iceandfire:deathworm_red")) {
               var3 = 1;
            }
            break;
         case -848744175:
            if (var2.equals("iceandfire:deathworm_tan")) {
               var3 = 3;
            }
            break;
         case -46075385:
            if (var2.equals("iceandfire:deathworm_red_giant")) {
               var3 = 0;
            }
            break;
         case 403612057:
            if (var2.equals("iceandfire:deathworm_white")) {
               var3 = 5;
            }
            break;
         case 1717852063:
            if (var2.equals("iceandfire:deathworm_white_giant")) {
               var3 = 4;
            }
         }

         switch(var3) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
            this.removeDeathWormEggLooting(event, event.getName());
         default:
         }
      }
   }

   private void removeDeathWormEggLooting(LootTableLoadEvent event, ResourceLocation resource) {
      LootTable table = event.getTable();
      if (table != null) {
         try {
            List<LootPool> pools = this.reflector.getPools(table);
            if (pools == null) {
               return;
            }

            for(LootPool pool : pools) {
               if (pool.getName().equals(resource.getResourcePath())) {
                  List<LootEntry> poolEntries = this.reflector.getEntries(pool);
                  if (poolEntries != null) {
                     for(LootEntry entry : poolEntries) {
                        if (entry != null && entry instanceof LootEntryItem && entry.getEntryName().contains("deathworm_egg")) {
                           LootFunction[] functions = this.reflector.getFunctions((LootEntryItem)entry);
                           if (functions != null) {
                              for(int i = 0; i < functions.length; ++i) {
                                 LootFunction function = functions[i];
                                 if (function instanceof LootingEnchantBonus) {
                                    functions[i] = new LootUtil.DoNothingFunction();
                                    RLTweaker.logger.debug("Removed LootingEnchantBonus for deathworm egg: " + resource.toString());
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         } catch (IllegalAccessException | IllegalArgumentException var13) {
            ErrorUtil.logSilent("IAF Death Worm Egg Looting Invocation");
         }

      }
   }

   @SubscribeEvent
   public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
      if (ModConfig.server.iceandfire.stopGorgonTargetingPlayerStatues) {
         if (this.reflector.c_EntityGorgon.isInstance(event.getEntity()) && this.reflector.c_EntityStoneStatue.isInstance(event.getTarget()) && event.getEntityLiving() instanceof EntityLiving) {
            ((EntityLiving)event.getEntityLiving()).setAttackTarget((EntityLivingBase)null);
         }

      }
   }

   private void attemptRemoveRLCraftTrades() {
      try {
         Class.forName("com.github.alexthe666.iceandfire.core.ModVillagers");
      } catch (Exception var8) {
         return;
      }

      try {
         Class c_ModVillagers = Class.forName("com.github.alexthe666.iceandfire.core.ModVillagers");
         Object o_ModVillagers_INSTANCE = ReflectUtil.findField(c_ModVillagers, "INSTANCE").get((Object)null);
         Map<Integer, VillagerProfession> o_ModVillagers_professions = (Map)ReflectUtil.findField(c_ModVillagers, "professions").get(o_ModVillagers_INSTANCE);
         VillagerCareer craftsmanCareer = ((VillagerProfession)o_ModVillagers_professions.get(Integer.valueOf(1))).getCareer(0);
         Field f_VillagerCareer_trades = ReflectUtil.findField(VillagerCareer.class, "trades");
         Field f_buyingItem = ReflectUtil.findField(Class.forName("com.github.alexthe666.iceandfire.core.ModVillagers$SapphireForItems"), "buyingItem");
         this.removeAllSapphireForItemWithSold(new ResourceLocation("minecraft", "snow"), (List)f_VillagerCareer_trades.get(craftsmanCareer), f_buyingItem);
      } catch (Exception var7) {
         RLTweaker.logger.error("Failed to remove RLCraft balancing trades", var7);
         ErrorUtil.logSilent("Ice and Fire RLCraft Trade Removal");
      }

   }

   private void removeAllListEmeraldWithBought(ResourceLocation itemRegistry, List<List<ITradeList>> trades, Field f_itemToBuy) throws IllegalArgumentException, IllegalAccessException {
      for(List<ITradeList> tradeLevel : trades) {
         Iterator<ITradeList> iterator = tradeLevel.iterator();

         while(iterator.hasNext()) {
            ITradeList tradeList = (ITradeList)iterator.next();
            if (tradeList.getClass().getName().equals("com.github.alexthe666.iceandfire.core.ModVillagers$ListItemForSapphires")) {
               ItemStack stack = (ItemStack)f_itemToBuy.get(tradeList);
               if (stack.getItem().getRegistryName().equals(itemRegistry)) {
                  iterator.remove();
                  RLTweaker.logger.info("Removed a snow villager trade: " + itemRegistry.toString());
               }
            }
         }
      }

   }

   private void removeAllSapphireForItemWithSold(ResourceLocation itemRegistry, List<List<ITradeList>> trades, Field f_buyingItem) throws IllegalArgumentException, IllegalAccessException {
      for(List<ITradeList> tradeLevel : trades) {
         Iterator<ITradeList> iterator = tradeLevel.iterator();

         while(iterator.hasNext()) {
            ITradeList tradeList = (ITradeList)iterator.next();
            if (tradeList.getClass().getName().equals("com.github.alexthe666.iceandfire.core.ModVillagers$SapphireForItems")) {
               Item item = (Item)f_buyingItem.get(tradeList);
               if (item.getRegistryName().equals(itemRegistry)) {
                  iterator.remove();
                  RLTweaker.logger.info("Removed a snow villager trade: " + itemRegistry.toString());
               }
            }
         }
      }

   }

   public class IAFUseItem {
      private IEventListener handler;

      public IAFUseItem(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onUseItem(RightClickItem event) {
         if (!ModConfig.server.iceandfire.rightHandDragonInteraction || event.getHand() != EnumHand.OFF_HAND) {
            this.handler.invoke(event);
         }
      }
   }
}
