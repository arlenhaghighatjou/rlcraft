package com.mujmajnkraft.bettersurvival.eventhandlers;

import com.mujmajnkraft.bettersurvival.BetterSurvival;
import com.mujmajnkraft.bettersurvival.capabilities.extendedarrowproperties.ArrowPropertiesProvider;
import com.mujmajnkraft.bettersurvival.capabilities.extendedarrowproperties.IArrowProperties;
import com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.INunchakuCombo;
import com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.NunchakuComboProvider;
import com.mujmajnkraft.bettersurvival.capabilities.spearsinentity.ISpearsIn;
import com.mujmajnkraft.bettersurvival.capabilities.spearsinentity.SpearsInProvider;
import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentAgility;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentArrowRecovery;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentBlast;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentDiamonds;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentEducation;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentHighJump;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentMultishot;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentPenetration;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentRange;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentRapidFire;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentSmelting;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentTunneling;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentVersatility;
import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentVitality;
import com.mujmajnkraft.bettersurvival.init.ModBlocks;
import com.mujmajnkraft.bettersurvival.init.ModEnchantments;
import com.mujmajnkraft.bettersurvival.init.ModPotions;
import com.mujmajnkraft.bettersurvival.integration.InFCompat;
import com.mujmajnkraft.bettersurvival.items.ItemBattleAxe;
import com.mujmajnkraft.bettersurvival.items.ItemCustomShield;
import com.mujmajnkraft.bettersurvival.items.ItemCustomWeapon;
import com.mujmajnkraft.bettersurvival.items.ItemDagger;
import com.mujmajnkraft.bettersurvival.items.ItemHammer;
import com.mujmajnkraft.bettersurvival.items.ItemNunchaku;
import com.mujmajnkraft.bettersurvival.items.ItemSpear;
import java.util.ArrayList;
import java.util.Arrays;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class CommonEventHandler {
   public static final ResourceLocation ARROWPROPERTIES_CAP = new ResourceLocation("mujmajnkraftsbettersurvival", "ArrowProperties");
   public static final ResourceLocation SPEARSIN_CAP = new ResourceLocation("mujmajnkraftsbettersurvival", "spearsin");
   public static final ResourceLocation NUNCHAKU_CAP = new ResourceLocation("mujmajnkraftsbettersurvival", "nunchakucombo");

   public CommonEventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void onEvent(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof EntityArrow && !((Entity)event.getObject()).hasCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null)) {
         event.addCapability(ARROWPROPERTIES_CAP, new ArrowPropertiesProvider());
      }

      if (event.getObject() instanceof EntityLivingBase) {
         if (!((Entity)event.getObject()).hasCapability(SpearsInProvider.SPEARSIN_CAP, (EnumFacing)null)) {
            event.addCapability(SPEARSIN_CAP, new SpearsInProvider());
         }

         if (!((Entity)event.getObject()).hasCapability(NunchakuComboProvider.NUNCHAKUCOMBO_CAP, (EnumFacing)null)) {
            event.addCapability(NUNCHAKU_CAP, new NunchakuComboProvider());
         }
      }

   }

   @SubscribeEvent
   public void onArrowLoose(ArrowLooseEvent event) {
      if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.multishot, event.getBow()) != 0) {
         EnchantmentMultishot.shootMoreArrows(event.getEntity().getEntityWorld(), event.getEntityPlayer(), event.getBow(), event.getCharge());
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onBlockBreak(BreakEvent event) {
      if (!event.getPlayer().getHeldItemMainhand().hasTagCompound() || !event.getPlayer().getHeldItemMainhand().getTagCompound().getBoolean("tunnelCooldown")) {
         if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.tunneling, event.getPlayer().getHeldItemMainhand()) != 0) {
            EnchantmentTunneling.mineManyBlocks(event.getPlayer(), event.getState(), event.getPos());
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onBreakingStart(BreakSpeed event) {
      if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.versatility, event.getEntityPlayer().getHeldItemMainhand()) != 0) {
         event.setNewSpeed(event.getOriginalSpeed() * EnchantmentVersatility.getSpeedModifier(event.getEntityPlayer(), event.getState()));
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onEvent(EnderTeleportEvent event) {
      if (event.getEntityLiving().getActivePotionEffect(ModPotions.antiwarp) != null) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onEntityJoin(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityArrow && !event.getWorld().isRemote) {
         EntityArrow arrow = (EntityArrow)event.getEntity();
         if (arrow.shootingEntity instanceof EntityLivingBase) {
            EntityLivingBase shooter = (EntityLivingBase)arrow.shootingEntity;
            if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.arrowrecovery, shooter.getHeldItemMainhand()) > 0) {
               EnchantmentArrowRecovery.modifyArrow(shooter, arrow);
            }

            if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.blast, shooter.getHeldItemMainhand()) > 0) {
               EnchantmentBlast.modifyArrow(arrow, shooter);
            }

            if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.range, shooter.getHeldItemMainhand()) > 0) {
               EnchantmentRange.modifyArrow(arrow);
            }
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onAttack(LivingAttackEvent event) {
      Entity source = event.getSource().getImmediateSource();
      EntityLivingBase target = event.getEntityLiving();
      if (source instanceof EntityArrow) {
         if (target.getActiveItemStack().getItem() instanceof ItemCustomShield && ItemCustomShield.blockArrow(event.getSource(), target)) {
            event.setCanceled(true);
            return;
         }

         IArrowProperties cap = (IArrowProperties)source.getCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null);
         if (cap != null) {
            cap.hitEntity((EntityArrow)source);
         }
      }

      if (source instanceof EntityLivingBase && !source.world.isRemote && EnchantmentHelper.getMaxEnchantmentLevel(ModEnchantments.penetration, (EntityLivingBase)source) > 0 && !event.getSource().isMagicDamage()) {
         EnchantmentPenetration.dealPiercingDamage((EntityLivingBase)source, target, event.getAmount());
      }

      if ((!BetterSurvival.isRLCombatLoaded || !(event.getSource().getImmediateSource() instanceof EntityPlayer)) && event.getSource().getImmediateSource() instanceof EntityLivingBase) {
         EntityLivingBase attacker = (EntityLivingBase)event.getSource().getImmediateSource();
         if (attacker.getHeldItemMainhand().getItem() instanceof ItemSpear && attacker instanceof EntityPlayer && !((EntityPlayer)attacker).capabilities.isCreativeMode && ((ItemSpear)attacker.getHeldItemMainhand().getItem()).breakChance() >= attacker.world.rand.nextFloat()) {
            attacker.getHeldItemMainhand().shrink(1);
         }

         if (!attacker.world.isRemote && attacker.getHeldItemMainhand().hasTagCompound()) {
            NBTTagCompound compound = attacker.getHeldItemMainhand().getTagCompound();
            int h = compound.getInteger("remainingPotionHits");
            if (h > 0 && event.getEntityLiving().hurtResistantTime < 10) {
               for(PotionEffect effect : PotionUtils.getEffectsFromStack(attacker.getHeldItemMainhand())) {
                  if (effect.getPotion().isInstant()) {
                     event.getEntityLiving().hurtResistantTime = 0;
                     effect.getPotion().affectEntity((Entity)null, event.getSource().getImmediateSource(), event.getEntityLiving(), effect.getAmplifier(), 0.16666666666666666D);
                  } else {
                     event.getEntityLiving().addPotionEffect(new PotionEffect(effect.getPotion(), Math.max(effect.getDuration() / ForgeConfigHandler.potions.potionDivisor, 1), effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
                  }
               }

               if (attacker instanceof EntityPlayer && !((EntityPlayer)attacker).capabilities.isCreativeMode) {
                  compound.setInteger("remainingPotionHits", h - 1);
               }

               if (h - 1 <= 0) {
                  compound.removeTag("Potion");
                  compound.removeTag("CustomPotionEffects");
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onDamage(LivingHurtEvent event) {
      EntityLivingBase target = event.getEntityLiving();
      if (target.getActiveItemStack().getItem() instanceof ItemCustomShield) {
         event.setAmount(((ItemCustomShield)target.getActiveItemStack().getItem()).getDamageBlocked(target, event.getSource(), event.getAmount()));
      }

      Entity entitysource = event.getSource().getImmediateSource();
      if (!BetterSurvival.isRLCombatLoaded && entitysource instanceof EntityPlayer && !entitysource.world.isRemote) {
         EntityPlayer player = (EntityPlayer)entitysource;
         if (player.getHeldItemMainhand().getItem() instanceof ItemDagger) {
            event.setAmount(event.getAmount() * ((ItemDagger)player.getHeldItemMainhand().getItem()).getBackstabMultiplier(player, target, false));
         } else if (player.getHeldItemMainhand().getItem() instanceof ItemNunchaku) {
            INunchakuCombo combo = (INunchakuCombo)player.getCapability(NunchakuComboProvider.NUNCHAKUCOMBO_CAP, (EnumFacing)null);
            if (combo != null) {
               event.setAmount(event.getAmount() * (combo.getComboPower() + 1.0F));
            }
         } else if (player.getHeldItemMainhand().getItem() instanceof ItemHammer) {
            if ((double)player.getCooledAttackStrength(0.5F) > 0.9D) {
               int l = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.bash, player.getHeldItemMainhand());
               if (player.world.rand.nextFloat() < ForgeConfigHandler.weapons.stunBaseChance + (float)l * ForgeConfigHandler.weapons.bashModifier && !target.getIsInvulnerable()) {
                  PotionEffect potioneffectIn = new PotionEffect(ModPotions.stun, ((ItemHammer)player.getHeldItemMainhand().getItem()).stunduration);
                  target.addPotionEffect(potioneffectIn);
               }
            }
         } else if (player.getHeldItemMainhand().getItem() instanceof ItemBattleAxe && (double)player.getCooledAttackStrength(0.5F) > 0.9D) {
            int l = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.disarm, player.getHeldItemMainhand());
            if (player.world.rand.nextFloat() < ForgeConfigHandler.weapons.disarmBaseChance + (float)l * ForgeConfigHandler.weapons.disarmModifier && !target.getIsInvulnerable()) {
               if (target instanceof EntityPlayer) {
                  EntityItem drop = ((EntityPlayer)target).dropItem(((EntityPlayer)target).inventory.decrStackSize(((EntityPlayer)target).inventory.currentItem, 1), false);
                  if (drop != null) {
                     drop.setPickupDelay(40);
                  }
               } else if (!target.getHeldItemMainhand().isEmpty()) {
                  ItemStack item = target.getHeldItemMainhand();
                  NBTTagCompound nbttagcompound = target.writeToNBT(new NBTTagCompound());
                  if (nbttagcompound.hasKey("HandDropChances", 9)) {
                     NBTTagList nbttaglist = nbttagcompound.getTagList("HandDropChances", 5);
                     float chance = nbttaglist.getFloatAt(0);
                     target.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                     int rnd = target.world.rand.nextInt(100);
                     if (chance * 100.0F + (float)EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, player.getHeldItemMainhand()) > (float)(rnd + 1)) {
                        target.entityDropItem(item, 1.0F);
                     }
                  }
               }
            }
         }

         if (player.getHeldItemMainhand().getItem() instanceof ItemCustomWeapon && BetterSurvival.isIafLoaded) {
            ToolMaterial mat = ((ItemCustomWeapon)player.getHeldItemMainhand().getItem()).getMaterial();
            event.setAmount(event.getAmount() + InFCompat.getMaterialModifier(mat, target, player));
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onDeath(LivingDeathEvent event) {
      ISpearsIn spearsIn = (ISpearsIn)event.getEntity().getCapability(SpearsInProvider.SPEARSIN_CAP, (EnumFacing)null);
      if (spearsIn != null) {
         ArrayList<ItemStack> spears = spearsIn.getSpearsIn();
         if (!spears.isEmpty()) {
            for(ItemStack spear : spears) {
               event.getEntity().entityDropItem(spear, 0.0F);
            }
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onExpReward(LivingExperienceDropEvent event) {
      EntityPlayer player = event.getAttackingPlayer();
      if (player != null && EnchantmentHelper.getMaxEnchantmentLevel(ModEnchantments.education, player) > 0) {
         event.setDroppedExperience((int)((float)event.getDroppedExperience() * EnchantmentEducation.getExpMultiplyer(player, event.getEntityLiving())));
      }

   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL
   )
   public void onJumpHigh(LivingJumpEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (entity != null) {
         int j = EnchantmentHelper.getMaxEnchantmentLevel(ModEnchantments.highjump, entity);
         if (j > 0) {
            EnchantmentHighJump.boostJump(entity, j);
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onJumpLowest(LivingJumpEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (entity != null) {
         if (entity.getActivePotionEffect(ModPotions.stun) != null && entity.motionY > 0.0D) {
            entity.motionY = 0.0D;
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onItemUsing(Tick event) {
      EntityLivingBase entity = event.getEntityLiving();
      Item item = event.getItem().getItem();
      if (item instanceof ItemBow && EnchantmentHelper.getEnchantmentLevel(ModEnchantments.rapidfire, entity.getHeldItemMainhand()) > 0) {
         event.setDuration(event.getDuration() - EnchantmentRapidFire.getChargeTimeReduction(entity, event.getDuration()));
      }

   }

   @SubscribeEvent
   public void onEvent(HarvestDropsEvent event) {
      if (event.getHarvester() != null && !event.getDrops().isEmpty()) {
         EntityPlayer player = event.getHarvester();
         if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.smelting, player.getHeldItemMainhand()) > 0) {
            EnchantmentSmelting.smeltDrops(event.getDrops(), event.getFortuneLevel(), player);
         }

         if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.diamonds, player.getHeldItemMainhand()) > 0) {
            EnchantmentDiamonds.conjureDiamonds(event.getDrops(), event.getState().getBlock(), player);
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onLivingUpdate(LivingUpdateEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (entity instanceof EntityPlayer) {
         if (entity.getHeldItemMainhand().getItem() instanceof ItemNunchaku) {
            INunchakuCombo combo = (INunchakuCombo)entity.getCapability(NunchakuComboProvider.NUNCHAKUCOMBO_CAP, (EnumFacing)null);
            if (combo != null && (combo.getComboTime() > 0 || combo.getComboPower() > 0.0F)) {
               combo.countDown();
            }
         }

         int vitLvl = EnchantmentHelper.getMaxEnchantmentLevel(ModEnchantments.vitality, entity);
         if (vitLvl > 0) {
            EnchantmentVitality.healPlayer((EntityPlayer)entity, vitLvl);
         }

         if (entity.getActiveItemStack().getItem() instanceof ItemCustomShield) {
            ((ItemCustomShield)entity.getActiveItemStack().getItem()).applyModifiers(entity);
         } else if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(new AttributeModifier(ItemCustomShield.knockbackModifierUUID, "shield_knockback_adjustment", 0.0D, 0)) || entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(new AttributeModifier(ItemCustomShield.weightModifierUUID, "shield_speed_adjustment", 0.0D, 0))) {
            entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ItemCustomShield.knockbackModifierUUID);
            entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ItemCustomShield.weightModifierUUID);
         }
      }

      int speedLvl = EnchantmentHelper.getMaxEnchantmentLevel(ModEnchantments.agility, entity);
      if (speedLvl > 0) {
         EnchantmentAgility.applySpeedModifier(entity, speedLvl);
      } else if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(new AttributeModifier(EnchantmentAgility.speedModifier, "agility", 0.0D, 0))) {
         entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(EnchantmentAgility.speedModifier);
      }

      if (entity.getActivePotionEffect(ModPotions.stun) != null) {
         entity.motionX = 0.0D;
         if (entity.motionY > 0.0D) {
            entity.motionY = 0.0D;
         }

         entity.motionZ = 0.0D;
         if (entity instanceof EntityCreeper) {
            ((EntityCreeper)entity).setCreeperState(-1);
         }
      }

      if (entity instanceof EntityLiving) {
         if (entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).hasModifier(new AttributeModifier(EaglercraftUUID.fromString("a6107045-134f-4c14-a645-75c3ae5c7a27"), "blind", 0.0D, 1))) {
            entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).removeModifier(EaglercraftUUID.fromString("a6107045-134f-4c14-a645-75c3ae5c7a27"));
         }

         if (entity.getActivePotionEffect(MobEffects.BLINDNESS) != null) {
            EntityEntry entry = EntityRegistry.getEntry(entity.getClass());
            if (entry != null && !Arrays.asList(ForgeConfigHandler.potions.blindnessBlacklist).contains(entry.getRegistryName().toString())) {
               double strength = -0.01D * ForgeConfigHandler.potions.blindnessStrength;
               if (strength < 0.0D) {
                  AttributeModifier modifier = new AttributeModifier(EaglercraftUUID.fromString("a6107045-134f-4c14-a645-75c3ae5c7a27"), "blind", strength, 1);
                  entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(modifier);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onEvent(RightClickBlock event) {
      if (!BetterSurvival.isInspirationsLoaded) {
         if (event.getItemStack() != ItemStack.EMPTY) {
            Item item = event.getItemStack().getItem();
            IBlockState state = event.getWorld().getBlockState(event.getPos());
            if (state.getBlock() == Blocks.CAULDRON && !event.getWorld().isRemote && ((Integer)state.getValue(BlockCauldron.LEVEL)).intValue() == 0 && (item == Items.MILK_BUCKET || item == Items.POTIONITEM && (!PotionUtils.getEffectsFromStack(event.getItemStack()).isEmpty() || PotionUtils.getPotionFromItem(event.getItemStack()) != PotionTypes.WATER))) {
               event.getWorld().setBlockState(event.getPos(), ModBlocks.customcauldron.getDefaultState());
            }
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL
   )
   public void onLootLoad(LootTableLoadEvent event) {
      if (event.getName().toString().equals("minecraft:chests/end_city_treasure")) {
         LootCondition chance = new RandomChance(1.0F);
         LootEntry entry = new LootEntryTable(new ResourceLocation("mujmajnkraftsbettersurvival:chests/end_city_treasure_inject"), 1, 0, (LootCondition[])null, "Custom Weapons");
         LootPool pool = new LootPool(new LootEntry[]{entry}, new LootCondition[]{chance}, new RandomValueRange(1.0F), new RandomValueRange(0.0F), "Custom Weapons");
         event.getTable().addPool(pool);
      } else if (event.getName().toString().equals("minecraft:chests/stronghold_corridor")) {
         LootCondition chance = new RandomChance(1.0F);
         LootEntry entry = new LootEntryTable(new ResourceLocation("mujmajnkraftsbettersurvival:chests/stronghold_corridor_inject"), 1, 0, (LootCondition[])null, "Custom Weapons");
         LootPool pool = new LootPool(new LootEntry[]{entry}, new LootCondition[]{chance}, new RandomValueRange(1.0F), new RandomValueRange(0.0F), "Custom Weapons");
         event.getTable().addPool(pool);
      } else if (event.getName().toString().equals("minecraft:chests/village_blacksmith")) {
         LootCondition chance = new RandomChance(1.0F);
         LootEntry entry = new LootEntryTable(new ResourceLocation("mujmajnkraftsbettersurvival:chests/village_blacksmith_inject"), 1, 0, (LootCondition[])null, "Custom Weapons");
         LootPool pool = new LootPool(new LootEntry[]{entry}, new LootCondition[]{chance}, new RandomValueRange(1.0F), new RandomValueRange(0.0F), "Custom Weapons");
         event.getTable().addPool(pool);
      } else if (event.getName().toString().equals("minecraft:chests/nether_bridge")) {
         LootCondition chance = new RandomChance(1.0F);
         LootEntry entry = new LootEntryTable(new ResourceLocation("mujmajnkraftsbettersurvival:chests/nether_bridge_inject"), 1, 0, (LootCondition[])null, "Custom Weapons");
         LootPool pool = new LootPool(new LootEntry[]{entry}, new LootCondition[]{chance}, new RandomValueRange(1.0F), new RandomValueRange(0.0F), "Custom Weapons");
         event.getTable().addPool(pool);
      } else if (event.getName().toString().equals("minecraft:chests/spawn_bonus_chest")) {
         LootCondition chance = new RandomChance(1.0F);
         LootEntry entry = new LootEntryTable(new ResourceLocation("mujmajnkraftsbettersurvival:chests/spawn_bonus_inject"), 1, 0, (LootCondition[])null, "Custom Weapons");
         LootPool pool = new LootPool(new LootEntry[]{entry}, new LootCondition[]{chance}, new RandomValueRange(1.0F), new RandomValueRange(0.0F), "Custom Weapons");
         event.getTable().addPool(pool);
      }

   }
}
