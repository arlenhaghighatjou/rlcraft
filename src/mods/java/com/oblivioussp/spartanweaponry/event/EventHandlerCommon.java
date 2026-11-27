package com.oblivioussp.spartanweaponry.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.IWeaponPropertyContainer;
import com.oblivioussp.spartanweaponry.api.weaponproperty.IPropertyCallback;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrownWeapon;
import com.oblivioussp.spartanweaponry.init.EnchantmentRegistrySW;
import com.oblivioussp.spartanweaponry.item.IBlockingWeapon;
import com.oblivioussp.spartanweaponry.item.ItemQuiverBase;
import com.oblivioussp.spartanweaponry.item.ItemThrowingWeapon;
import com.oblivioussp.spartanweaponry.util.AdvancementTrigger;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.Log;
import com.oblivioussp.spartanweaponry.util.QuiverHelper;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Map.Entry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class EventHandlerCommon {
   public static EaglercraftRandom rand = new EaglercraftRandom();
   public static int tickCounter = 0;

   public EventHandlerCommon() {
   }

   public static void register(EventBus bus) {
      bus.register(LivingHurtEvent.class, EventHandlerCommon::onLivingHurt);
      bus.register(LivingAttackEvent.class, EventHandlerCommon::attackEvent);
      bus.register(ItemCraftedEvent.class, EventHandlerCommon::onCraftEvent);
      bus.register(LootingLevelEvent.class, EventHandlerCommon::onLootLevel);
      bus.register(LivingEquipmentChangeEvent.class, EventHandlerCommon::onEquipmentChange);
      bus.register(EntityItemPickupEvent.class, EventHandlerCommon::onPlayerPickupEvent);
      bus.register(LootTableLoadEvent.class, EventHandlerCommon::onLootTableLoad);
      bus.register(AnvilUpdateEvent.class, EventHandlerCommon::handleAnvilUpdate);
   }

   public static void onLivingHurt(LivingHurtEvent ev) {
      DamageSource source = ev.getSource();
      float dmgDealt = ev.getAmount();
      EntityLivingBase victim = ev.getEntityLiving();
      if (ModSpartanWeaponry.debugMode) {
         Log.info("Damage: Entity: " + victim.getDisplayName().getUnformattedComponentText() + " Armour value: " + victim.getTotalArmorValue() + " Damage value: " + dmgDealt + " Source: " + source.damageType + " BypassesArmour: " + source.isUnblockable());
      }

      if (dmgDealt != 0.0F && !source.isProjectile() && !source.isFireDamage() && !source.isExplosion() && !source.isMagicDamage() && (source.getDamageType().equals("player") || source.getDamageType().equals("mob"))) {
         if (source.getImmediateSource() == source.getTrueSource() && source.getTrueSource() instanceof EntityLivingBase && victim != null) {
            EntityLivingBase attacker = (EntityLivingBase)source.getTrueSource();
            ItemStack stack = attacker.getHeldItemMainhand();
            ItemStack victimStack = victim.getHeldItemMainhand();
            if (!stack.isEmpty()) {
               if (stack.getItem() instanceof IWeaponPropertyContainer) {
                  IWeaponPropertyContainer container = (IWeaponPropertyContainer)stack.getItem();
                  float directDamage = container.getDirectAttackDamage();

                  for(WeaponProperty prop : (List<WeaponProperty>)container.getAllWeaponProperties()) {
                     IPropertyCallback callback = prop.getCallback();
                     if (callback != null) {
                        dmgDealt = callback.modifyDamageDealt(container.getMaterialEx(), dmgDealt, directDamage + 1.0F, source, attacker, victim);
                     }
                  }

                  for(WeaponProperty prop : container.getMaterialEx().getAllWeaponProperties()) {
                     IPropertyCallback callback = prop.getCallback();
                     if (callback != null) {
                        dmgDealt = callback.modifyDamageDealt(container.getMaterialEx(), dmgDealt, directDamage + 1.0F, source, attacker, victim);
                     }
                  }
               }

               if (stack.getItem() instanceof ItemThrowingWeapon && stack.hasTagCompound() && stack.getTagCompound().getInteger("AmmoUsed") >= ((ItemThrowingWeapon)stack.getItem()).getMaxAmmo(stack)) {
                  dmgDealt = 1.0F;
               }
            }

            if (!victimStack.isEmpty() && victimStack.getItem() instanceof IWeaponPropertyContainer) {
               IWeaponPropertyContainer container = (IWeaponPropertyContainer)victimStack.getItem();

               for(WeaponProperty prop : (List<WeaponProperty>)container.getAllWeaponProperties()) {
                  IPropertyCallback callback = prop.getCallback();
                  if (callback != null) {
                     dmgDealt = callback.modifyDamageTaken(container.getMaterialEx(), dmgDealt, source, attacker, victim);
                  }
               }

               for(WeaponProperty prop : container.getMaterialEx().getAllWeaponProperties()) {
                  IPropertyCallback callback = prop.getCallback();
                  if (callback != null) {
                     dmgDealt = callback.modifyDamageTaken(container.getMaterialEx(), dmgDealt, source, attacker, victim);
                  }
               }
            }

            if (dmgDealt != ev.getAmount()) {
               if (!attacker.world.isRemote) {
                  if (dmgDealt > ev.getAmount()) {
                     ((WorldServer)attacker.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, victim.posX, victim.posY + (double)(victim.height / 2.0F), victim.posZ, 16, 0.2D, 0.2D, 0.2D, 0.0D, new int[0]);
                  } else if (dmgDealt < ev.getAmount()) {
                     ((WorldServer)attacker.world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, victim.posX, victim.posY + (double)(victim.height / 2.0F), victim.posZ, 16, 0.2D, 0.2D, 0.2D, 0.0D, new int[0]);
                  }
               }

               ev.setAmount(dmgDealt);
            }
         }

      }
   }

   public static void attackEvent(LivingAttackEvent ev) {
      if (ev.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)ev.getEntityLiving();
         if (player.isHandActive() && !player.getActiveItemStack().isEmpty()) {
            ItemStack activeStack = player.getActiveItemStack();
            if (activeStack.getItem() instanceof IBlockingWeapon) {
               IBlockingWeapon weapon = (IBlockingWeapon)activeStack.getItem();
               DamageSource source = ev.getSource();
               boolean damageItem = false;
               if (weapon.canBlockMelee() && !source.isExplosion() && !source.isFireDamage() && !source.isMagicDamage() && !source.isProjectile() && !source.isUnblockable()) {
                  Entity trueSourceEntity = source.getTrueSource();
                  double mX = trueSourceEntity.motionX;
                  double mY = trueSourceEntity.motionY;
                  double mZ = trueSourceEntity.motionZ;
                  if (trueSourceEntity instanceof EntityLivingBase) {
                     ((EntityLivingBase)source.getTrueSource()).knockBack(player, 0.3F, player.posX - source.getTrueSource().posX, player.posZ - source.getTrueSource().posZ);
                  }

                  if (trueSourceEntity instanceof EntityPlayerMP && trueSourceEntity.velocityChanged) {
                     ((EntityPlayerMP)trueSourceEntity).connection.sendPacket(new SPacketEntityVelocity(trueSourceEntity));
                     trueSourceEntity.velocityChanged = false;
                     trueSourceEntity.motionX = mX;
                     trueSourceEntity.motionY = mY;
                     trueSourceEntity.motionZ = mZ;
                  }

                  damageItem = true;
               } else if (weapon.canBlockProjectiles() && ev.getSource().isProjectile()) {
                  damageItem = true;
               }

               if (damageItem) {
                  ItemStack copy = activeStack.copy();
                  int itemDamage = 1 + MathHelper.floor(ev.getAmount());
                  activeStack.damageItem(itemDamage, player);
                  if (activeStack.isEmpty()) {
                     EnumHand activeHand = player.getActiveHand();
                     ForgeEventFactory.onPlayerDestroyItem(player, copy, activeHand);
                     player.renderBrokenItemStack(copy);
                     if (activeHand == EnumHand.MAIN_HAND) {
                        player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                     } else if (activeHand == EnumHand.OFF_HAND) {
                        player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
                     }

                     activeStack = ItemStack.EMPTY;
                  }

                  player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundCategory.PLAYERS, 0.8F, 0.8F);
                  ev.setCanceled(true);
               }
            }
         }
      }

   }

   public static void onCraftEvent(ItemCraftedEvent ev) {
      if (ev.player instanceof EntityPlayerMP) {
         AdvancementTrigger.CRAFT_ITEM.trigger((EntityPlayerMP)ev.player, ev.crafting.getItem());
      }

   }

   public static void onLootLevel(LootingLevelEvent ev) {
      Entity e = ev.getDamageSource().getImmediateSource();
      if (e instanceof EntityThrownWeapon) {
         EntityThrownWeapon thrownWeapon = (EntityThrownWeapon)e;
         int luckLevel = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_LUCK, thrownWeapon.getWeaponStack());
         ev.setLootingLevel(luckLevel);
      }

   }

   public static void onEquipmentChange(LivingEquipmentChangeEvent ev) {
      if ((ev.getSlot() == EntityEquipmentSlot.MAINHAND || ev.getSlot() == EntityEquipmentSlot.OFFHAND) && ev.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)ev.getEntityLiving();
         ItemStack fromStack = ev.getFrom();
         ItemStack toStack = ev.getTo();
         EntityEquipmentSlot oppositeHand;
         if (ev.getSlot() == EntityEquipmentSlot.OFFHAND) {
            oppositeHand = EntityEquipmentSlot.MAINHAND;
         } else {
            oppositeHand = EntityEquipmentSlot.OFFHAND;
         }

         ItemStack oppositeStack = player.getItemStackFromSlot(oppositeHand);
         if (!toStack.isItemEqual(fromStack)) {
            if (!oppositeStack.isEmpty()) {
               boolean toStackBlacklisted = false;
               String toName = toStack.getItem().getRegistryName().toString();
               if (ConfigHandler.quiverBowBlacklist.contains(toName)) {
                  toStackBlacklisted = true;
               }

               for(QuiverHelper.IQuiverInfo quiverInfo : QuiverHelper.info) {
                  if (quiverInfo.isWeapon(fromStack) && (!quiverInfo.isWeapon(toStack) || toStackBlacklisted) && quiverInfo.isAmmo(oppositeStack)) {
                     ItemStack quiver = QuiverHelper.findFirstOfType(player, quiverInfo);
                     placeAmmoIntoQuiver(player, quiver, oppositeHand);
                     oppositeStack = player.getItemStackFromSlot(oppositeHand);
                     if (oppositeStack.isEmpty()) {
                        NBTTagCompound nbt = quiver.getOrCreateSubCompound("offhandMoved");
                        if (nbt != null) {
                           String itemId = nbt.getString("id");
                           int itemSlot = nbt.getInteger("slot");
                           ItemStack offhandStack = player.inventory.getStackInSlot(itemSlot);
                           if (offhandStack.getItem().getRegistryName().toString().equals(itemId)) {
                              player.setItemStackToSlot(oppositeHand, offhandStack);
                              player.inventory.setInventorySlotContents(itemSlot, ItemStack.EMPTY);
                           }

                           quiver.getTagCompound().removeTag("offhandMoved");
                        }
                        break;
                     }
                  }
               }
            }

            String regName = toStack.getItem().getRegistryName().toString();
            if (ConfigHandler.quiverBowBlacklist.contains(regName)) {
               return;
            }

            for(QuiverHelper.IQuiverInfo quiverInfo : QuiverHelper.info) {
               if (quiverInfo.isWeapon(toStack)) {
                  ItemStack quiver = QuiverHelper.findFirstOfType(player, quiverInfo);
                  if (!quiver.isEmpty()) {
                     IItemHandler quiverHandler = (IItemHandler)quiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
                     boolean isQuiverEmpty = true;

                     for(int i = 0; i < quiverHandler.getSlots(); ++i) {
                        ItemStack arrowStack = quiverHandler.getStackInSlot(i);
                        if (!arrowStack.isEmpty()) {
                           isQuiverEmpty = false;
                           break;
                        }
                     }

                     if (!isQuiverEmpty && !oppositeStack.isEmpty() && !quiverInfo.isAmmo(oppositeStack)) {
                        int emptySlot = -1;

                        for(int i = 0; i < player.inventory.mainInventory.size(); ++i) {
                           ItemStack playerStack = (ItemStack)player.inventory.mainInventory.get(i);
                           if (playerStack.isEmpty()) {
                              emptySlot = i;
                              break;
                           }
                        }

                        if (emptySlot != -1) {
                           String itemId = oppositeStack.getItem().getRegistryName().toString();
                           NBTTagCompound nbt = quiver.getOrCreateSubCompound("offhandMoved");
                           nbt.setString("id", itemId);
                           nbt.setInteger("slot", emptySlot);
                           player.inventory.setInventorySlotContents(emptySlot, oppositeStack);
                           player.setItemStackToSlot(oppositeHand, ItemStack.EMPTY);
                        }
                     }

                     if (player.getItemStackFromSlot(oppositeHand).isEmpty()) {
                        takeAmmoFromQuiver(player, quiver, oppositeHand);
                     }

                     oppositeStack = player.getItemStackFromSlot(oppositeHand);
                     if (!oppositeStack.isEmpty()) {
                        break;
                     }
                  }
               }
            }
         }
      }

   }

   protected static void placeAmmoIntoQuiver(EntityPlayer player, ItemStack quiver, EntityEquipmentSlot oppositeHandSlot) {
      if (!quiver.isEmpty() && quiver.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)) {
         IItemHandler quiverHandler = (IItemHandler)quiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
         ItemStack arrowStack = player.getItemStackFromSlot(oppositeHandSlot);

         for(int j = 0; j < quiverHandler.getSlots(); ++j) {
            arrowStack = quiverHandler.insertItem(j, arrowStack, false);
            if (arrowStack.isEmpty()) {
               break;
            }
         }

         player.setItemStackToSlot(oppositeHandSlot, arrowStack);
      }

   }

   protected static void takeAmmoFromQuiver(EntityPlayer player, ItemStack quiver, EntityEquipmentSlot oppositeHandSlot) {
      if (!quiver.isEmpty() && quiver.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)) {
         IItemHandler quiverHandler = (IItemHandler)quiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
         ItemStack arrowStack = ItemStack.EMPTY;

         for(int j = 0; j < quiverHandler.getSlots(); ++j) {
            arrowStack = quiverHandler.extractItem(j, 64, false);
            if (!arrowStack.isEmpty()) {
               player.setItemStackToSlot(oppositeHandSlot, arrowStack);
               break;
            }
         }
      }

   }

   public static void onPlayerPickupEvent(EntityItemPickupEvent ev) {
      ItemStack pickedUpStack = ev.getItem().getItem().copy();
      int beforeCount = pickedUpStack.getCount();
      EntityPlayer player = ev.getEntityPlayer();
      List<ItemStack> quivers = QuiverHelper.findValidQuivers(player);
      if (!quivers.isEmpty()) {
         for(ItemStack quiver : quivers) {
            if (!pickedUpStack.isEmpty() && !quiver.isEmpty() && quiver.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null) && ((ItemQuiverBase)quiver.getItem()).isAmmoValid(pickedUpStack, quiver) && quiver.hasTagCompound() && quiver.getTagCompound().getBoolean("ammoCollect")) {
               IItemHandler quiverHandler = (IItemHandler)quiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);

               for(int i = 0; i < quiverHandler.getSlots(); ++i) {
                  pickedUpStack = quiverHandler.insertItem(i, pickedUpStack, false);
                  if (pickedUpStack.isEmpty()) {
                     break;
                  }
               }
            }

            if (pickedUpStack.isEmpty()) {
               break;
            }
         }

         int afterCount = pickedUpStack.getCount();
         if (afterCount < beforeCount) {
            player.onItemPickup(ev.getItem(), beforeCount - afterCount);
            ev.getItem().getItem().setCount(afterCount);
            player.world.playSound((EntityPlayer)null, ev.getItem().posX, ev.getItem().posY, ev.getItem().posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (rand.nextFloat() - rand.nextFloat()) * 0.7F + 0.0F);
         }
      }

      if (pickedUpStack.getItem() instanceof ItemThrowingWeapon) {
         boolean pickUpAsNewItem = true;
         boolean removeItems = false;

         for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack slotStack = player.inventory.getStackInSlot(i);
            if (ItemStack.areItemsEqualIgnoreDurability(slotStack, pickedUpStack) && pickedUpStack.hasTagCompound() && slotStack.hasTagCompound() && pickedUpStack.getTagCompound().getUniqueId("UUID").equals(slotStack.getTagCompound().getUniqueId("UUID")) && pickedUpStack.getItem() instanceof ItemThrowingWeapon) {
               int maxAmmo = ((ItemThrowingWeapon)slotStack.getItem()).getMaxAmmo(slotStack);
               int currentAmmo = maxAmmo - slotStack.getTagCompound().getInteger("AmmoUsed");
               boolean currentNotOriginalStack = !slotStack.getTagCompound().getBoolean("Original");
               boolean pickedUpOriginalStack = pickedUpStack.getTagCompound().getBoolean("Original");
               if (currentAmmo < maxAmmo || currentNotOriginalStack && pickedUpOriginalStack) {
                  int itemDamage = slotStack.getItemDamage() + pickedUpStack.getItemDamage();
                  if (itemDamage > slotStack.getMaxDamage()) {
                     player.renderBrokenItemStack(pickedUpStack);
                     itemDamage -= slotStack.getMaxDamage() + 1;
                  } else {
                     int pickupMaxAmmo = ((ItemThrowingWeapon)pickedUpStack.getItem()).getMaxAmmo(pickedUpStack);
                     currentAmmo = currentAmmo + (pickupMaxAmmo - pickedUpStack.getTagCompound().getInteger("AmmoUsed"));
                     int newMaxAmmo = Math.max(maxAmmo, pickupMaxAmmo);
                     currentAmmo = MathHelper.clamp(currentAmmo, 0, newMaxAmmo);
                     slotStack.getTagCompound().setInteger("AmmoUsed", newMaxAmmo - currentAmmo);
                     if (currentNotOriginalStack && pickedUpOriginalStack) {
                        slotStack.getTagCompound().setBoolean("Original", pickedUpOriginalStack);
                        if (pickedUpStack.isItemEnchanted()) {
                           Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(pickedUpStack);
                           EnchantmentHelper.setEnchantments(enchantments, slotStack);
                        }
                     }
                  }

                  slotStack.setItemDamage(itemDamage);
                  pickUpAsNewItem = false;
                  removeItems = true;
                  break;
               }
            }
         }

         if (pickUpAsNewItem) {
            removeItems = player.inventory.addItemStackToInventory(pickedUpStack);
         }

         if (removeItems) {
            player.onItemPickup(ev.getItem(), 1);
            player.world.playSound((EntityPlayer)null, ev.getItem().posX, ev.getItem().posY, ev.getItem().posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (rand.nextFloat() - rand.nextFloat()) * 0.7F + 0.0F);
            ev.getItem().getItem().setCount(0);
         }
      }

   }

   public static void onLootTableLoad(LootTableLoadEvent ev) {
      if (ev.getName().toString().equals("minecraft:chests/village_blacksmith")) {
         LootEntry entry = new LootEntryTable(new ResourceLocation("spartanweaponry", "inject/village_blacksmith"), 1, 1, new LootCondition[0], "spartanweaponry_inject_entry");
         LootPool pool = new LootPool(new LootEntry[]{entry}, new LootCondition[0], new RandomValueRange(1.0F), new RandomValueRange(0.0F, 1.0F), "spartanweaponry_inject_pool");
         ev.getTable().addPool(pool);
      }

   }

   public static void handleAnvilUpdate(AnvilUpdateEvent ev) {
      ItemStack left = ev.getLeft();
      ItemStack right = ev.getRight();
      if (left.getItem() instanceof ItemThrowingWeapon && left.hasTagCompound() && left.getTagCompound().getBoolean("Original") && left.isItemEqualIgnoreDurability(right)) {
         ItemThrowingWeapon throwingWeapon = (ItemThrowingWeapon)left.getItem();
         int leftAmmo = left.getTagCompound().getInteger("AmmoUsed");
         int rightAmmo = right.getTagCompound().getInteger("AmmoUsed");
         if (leftAmmo == 0) {
            return;
         }

         int maxAmmo = ((ItemThrowingWeapon)left.getItem()).getMaxAmmo(left);
         int durability = left.getItemDamage() + right.getItemDamage();
         int combinedAmmo = MathHelper.clamp(maxAmmo - leftAmmo + (maxAmmo - rightAmmo), 0, throwingWeapon.getMaxAmmo(left));
         if (durability > left.getMaxDamage()) {
            combinedAmmo = Math.max(combinedAmmo - 1, 0);
            durability -= left.getMaxDamage();
         }

         ItemStack resultStack = ev.getLeft().copy();
         resultStack.getTagCompound().setInteger("AmmoUsed", maxAmmo - combinedAmmo);
         resultStack.setItemDamage(durability);
         Map<Enchantment, Integer> enchantmentMap = EnchantmentHelper.getEnchantments(left);
         int cost = 2;

         for(Entry<Enchantment, Integer> enchantment : enchantmentMap.entrySet()) {
            cost += ((Integer)enchantment.getValue()).intValue();
         }

         ev.setCost(cost);
         ev.setOutput(resultStack);
      }

   }
}
