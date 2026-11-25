package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.entity.ChickenEntityProperties;
import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.entity.EntityAmphithere;
import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import com.github.alexthe666.iceandfire.entity.EntityCyclops;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.EntityMutlipartPart;
import com.github.alexthe666.iceandfire.entity.EntitySiren;
import com.github.alexthe666.iceandfire.entity.EntityStoneStatue;
import com.github.alexthe666.iceandfire.entity.FrozenEntityProperties;
import com.github.alexthe666.iceandfire.entity.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.IHearsSiren;
import com.github.alexthe666.iceandfire.entity.IPhasesThroughBlock;
import com.github.alexthe666.iceandfire.entity.IVillagerFear;
import com.github.alexthe666.iceandfire.entity.MiscPlayerProperties;
import com.github.alexthe666.iceandfire.entity.SirenEntityProperties;
import com.github.alexthe666.iceandfire.entity.StoneEntityProperties;
import com.github.alexthe666.iceandfire.entity.ai.EntitySheepAIFollowCyclops;
import com.github.alexthe666.iceandfire.entity.ai.VillagerAIFearUntamed;
import com.github.alexthe666.iceandfire.item.ItemSeaSerpentArmor;
import com.github.alexthe666.iceandfire.item.ItemTrollArmor;
import com.github.alexthe666.iceandfire.message.MessagePlayerHitMultipart;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget.Sorter;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class EventLiving {
   private static final Predicate VILLAGER_FEAR = new Predicate<EntityLivingBase>() {
      public boolean apply(@Nullable EntityLivingBase entity) {
         return entity != null && entity instanceof IVillagerFear;
      }
   };
   EaglercraftRandom rand = new EaglercraftRandom();

   public EventLiving() {
   }

   @SubscribeEvent
   public void onArrowCollide(ProjectileImpactEvent event) {
      if (event.getEntity() instanceof EntityArrow && ((EntityArrow)event.getEntity()).shootingEntity != null && event.getRayTraceResult() != null && event.getRayTraceResult().entityHit != null) {
         Entity shootingEntity = ((EntityArrow)event.getEntity()).shootingEntity;
         Entity shotEntity = event.getRayTraceResult().entityHit;
         if (shootingEntity instanceof EntityLivingBase && shootingEntity.isRidingOrBeingRiddenBy(shotEntity) && shotEntity instanceof EntityTameable && ((EntityTameable)shotEntity).isTamed() && shotEntity.isOnSameTeam(shootingEntity)) {
            event.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void onPlayerAttackMob(AttackEntityEvent event) {
      if (event.getTarget() instanceof EntityMutlipartPart && event.getEntity() instanceof EntityPlayer) {
         event.setCanceled(true);
         EntityLivingBase parent = ((EntityMutlipartPart)event.getTarget()).getParent();
         ((EntityPlayer)event.getEntity()).attackTargetEntityWithCurrentItem(parent);
         IceAndFire.NETWORK_WRAPPER.sendToServer(new MessagePlayerHitMultipart(parent.getEntityId()));
      }

   }

   @SubscribeEvent
   public void onGatherCollisionBoxes(GetCollisionBoxesEvent event) {
      if (event.getEntity() != null && event.getEntity() instanceof IPhasesThroughBlock) {
         Iterator<AxisAlignedBB> itr = event.getCollisionBoxesList().iterator();

         while(itr.hasNext()) {
            AxisAlignedBB aabb = (AxisAlignedBB)itr.next();
            BlockPos pos = new BlockPos(aabb.minX, aabb.minY, aabb.minZ);
            if (((IPhasesThroughBlock)event.getEntity()).canPhaseThroughBlock(event.getWorld(), pos)) {
               itr.remove();
            }
         }
      }

   }

   @SubscribeEvent
   public void onEntityFall(LivingFallEvent event) {
      if (event.getEntityLiving() instanceof EntityPlayer) {
         MiscPlayerProperties properties = (MiscPlayerProperties)EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), MiscPlayerProperties.class);
         if (properties.hasDismountedDragon) {
            event.setDamageMultiplier(0.0F);
            properties.hasDismountedDragon = false;
         }
      }

   }

   @SubscribeEvent
   public void onEntityMount(EntityMountEvent event) {
      if (event.getEntityBeingMounted() instanceof EntityDragonBase) {
         EntityDragonBase dragon = (EntityDragonBase)event.getEntityBeingMounted();
         if (event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer && !event.getEntityMounting().world.isRemote) {
            EntityPlayer player = (EntityPlayer)event.getEntityMounting();
            if (dragon.isOwner((EntityPlayer)event.getEntityMounting())) {
               dragon.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
               player.fallDistance = -dragon.height;
            } else {
               dragon.renderYawOffset = dragon.rotationYaw;
               float modTick_0 = (float)(dragon.getAnimationTick() - 25);
               float modTick_1 = dragon.getAnimationTick() > 25 && dragon.getAnimationTick() < 55 ? 8.0F * MathHelper.clamp(MathHelper.sin((float)(3.141592653589793D + (double)modTick_0 * 0.25D)), -0.8F, 0.8F) : 0.0F;
               float modTick_2 = dragon.getAnimationTick() > 30 ? 10.0F : (float)Math.max(0, dragon.getAnimationTick() - 20);
               float radius = 0.75F * (0.6F * dragon.getRenderSize() / 3.0F) * -3.0F;
               float angle = 0.017453292F * dragon.renderYawOffset + 3.15F + modTick_1 * 2.0F * 0.015F;
               double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
               double extraZ = (double)(radius * MathHelper.cos(angle));
               double extraY = modTick_2 == 0.0F ? 0.0D : 0.03500000014901161D * ((double)(dragon.getRenderSize() / 3.0F) + (double)modTick_2 * 0.5D * (double)(dragon.getRenderSize() / 3.0F));
               player.setPosition(dragon.posX + extraX, dragon.posY + extraY, dragon.posZ + extraZ);
            }
         }
      }

      if (event.getEntityBeingMounted() instanceof EntityHippogryph) {
         EntityHippogryph hippogryph = (EntityHippogryph)event.getEntityBeingMounted();
         if (event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer && !event.getEntityMounting().world.isRemote && hippogryph.isOwner((EntityPlayer)event.getEntityMounting())) {
            EntityPlayer player = (EntityPlayer)event.getEntityMounting();
            hippogryph.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
         }
      }

   }

   @SubscribeEvent
   public void onEntityDamage(LivingHurtEvent event) {
      if (event.getSource().isProjectile()) {
         float multi = 1.0F;
         if (event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemTrollArmor) {
            multi = (float)((double)multi - 0.1D);
         }

         if (event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemTrollArmor) {
            multi = (float)((double)multi - 0.3D);
         }

         if (event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemTrollArmor) {
            multi = (float)((double)multi - 0.2D);
         }

         if (event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemTrollArmor) {
            multi = (float)((double)multi - 0.1D);
         }

         event.setAmount(event.getAmount() * multi);
      }

   }

   @SubscribeEvent
   public void onEntityDrop(LivingDropsEvent event) {
      if (event.getEntityLiving() instanceof EntityWitherSkeleton) {
         event.getEntityLiving().dropItem(ModItems.witherbone, event.getEntityLiving().getRNG().nextInt(2));
      }

      if (event.getEntityLiving() instanceof EntityLiving) {
         StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), StoneEntityProperties.class);
         if (properties != null && properties.isStone) {
            event.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void onEntityDespawn(AllowDespawn event) {
      if (event.getEntityLiving() instanceof EntityLiving) {
         StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), StoneEntityProperties.class);
         if (properties != null && properties.isStone) {
            event.setResult(Result.DENY);
         }
      }

   }

   @SubscribeEvent
   public void onLivingAttacked(LivingAttackEvent event) {
      if (event.getSource().getTrueSource() != null) {
         Entity attacker = event.getSource().getTrueSource();
         if (isAnimaniaChicken(event.getEntityLiving()) && attacker instanceof EntityLivingBase) {
            signalChickenAlarm(event.getEntityLiving(), (EntityLivingBase)attacker);
         }

         if (DragonUtils.isVillager(event.getEntityLiving()) && attacker instanceof EntityLivingBase) {
            signalAmphithereAlarm(event.getEntityLiving(), (EntityLivingBase)attacker);
         }
      }

   }

   @SubscribeEvent
   public void onLivingSetTarget(LivingSetAttackTargetEvent event) {
      if (event.getTarget() != null) {
         EntityLivingBase attacker = event.getEntityLiving();
         if (isAnimaniaChicken(event.getTarget())) {
            signalChickenAlarm(event.getTarget(), attacker);
         }

         if (DragonUtils.isVillager(event.getTarget())) {
            signalAmphithereAlarm(event.getTarget(), attacker);
         }
      }

   }

   private static void signalChickenAlarm(EntityLivingBase chicken, EntityLivingBase attacker) {
      float d0 = (float)IceAndFire.CONFIG.cockatriceChickenSearchLength;
      List<Entity> list = chicken.world.getEntitiesWithinAABB(EntityCockatrice.class, (new AxisAlignedBB(chicken.posX, chicken.posY, chicken.posZ, chicken.posX + 1.0D, chicken.posY + 1.0D, chicken.posZ + 1.0D)).grow((double)d0, 10.0D, (double)d0));
      Collections.sort(list, new Sorter(attacker));
      if (!list.isEmpty()) {
         for(Entity entity : list) {
            if (entity instanceof EntityCockatrice && !(attacker instanceof EntityCockatrice)) {
               EntityCockatrice cockatrice = (EntityCockatrice)entity;
               if (!DragonUtils.hasSameOwner(cockatrice, attacker)) {
                  if (attacker instanceof EntityPlayer) {
                     EntityPlayer player = (EntityPlayer)attacker;
                     if (!player.isCreative() && !cockatrice.isOwner(player)) {
                        cockatrice.setAttackTarget(player);
                     }
                  } else {
                     cockatrice.setAttackTarget(attacker);
                  }
               }
            }
         }
      }

   }

   private static void signalAmphithereAlarm(EntityLivingBase villager, EntityLivingBase attacker) {
      float d0 = IceAndFire.CONFIG.amphithereVillagerSearchLength;
      List<Entity> list = villager.world.getEntitiesWithinAABB(EntityAmphithere.class, (new AxisAlignedBB(villager.posX - 1.0D, villager.posY - 1.0D, villager.posZ - 1.0D, villager.posX + 1.0D, villager.posY + 1.0D, villager.posZ + 1.0D)).grow((double)d0, (double)d0, (double)d0));
      Collections.sort(list, new Sorter(attacker));
      if (!list.isEmpty()) {
         for(Entity entity : list) {
            if (entity instanceof EntityAmphithere && !(attacker instanceof EntityAmphithere)) {
               EntityAmphithere amphithere = (EntityAmphithere)entity;
               if (!DragonUtils.hasSameOwner(amphithere, attacker)) {
                  if (attacker instanceof EntityPlayer) {
                     EntityPlayer player = (EntityPlayer)attacker;
                     if (!player.isCreative() && !amphithere.isOwner(player)) {
                        amphithere.setAttackTarget(player);
                     }
                  } else {
                     amphithere.setAttackTarget(attacker);
                  }
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onPlayerAttack(AttackEntityEvent event) {
      if (event.getTarget() != null && isAnimaniaSheep(event.getTarget())) {
         float dist = (float)IceAndFire.CONFIG.cyclopesSheepSearchLength;
         List<Entity> list = event.getTarget().world.getEntitiesWithinAABBExcludingEntity(event.getEntityPlayer(), event.getEntityPlayer().getEntityBoundingBox().expand((double)dist, (double)dist, (double)dist));
         Collections.sort(list, new Sorter(event.getEntityPlayer()));
         if (!list.isEmpty()) {
            for(Entity entity : list) {
               if (entity instanceof EntityCyclops) {
                  EntityCyclops cyclops = (EntityCyclops)entity;
                  if (!cyclops.isBlinded() && !event.getEntityPlayer().capabilities.isCreativeMode) {
                     cyclops.setAttackTarget(event.getEntityPlayer());
                  }
               }
            }
         }
      }

      if (event.getTarget() instanceof EntityLiving) {
         boolean stonePlayer = event.getTarget() instanceof EntityStoneStatue;
         StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties((EntityLiving)event.getTarget(), StoneEntityProperties.class);
         if (properties != null && properties.isStone || stonePlayer) {
            ((EntityLiving)event.getTarget()).setHealth(((EntityLiving)event.getTarget()).getMaxHealth());
            if (event.getEntityPlayer() != null) {
               ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
               if (stack.getItem() != null && (stack.getItem().canHarvestBlock(Blocks.STONE.getDefaultState()) || stack.getItem().getUnlocalizedName().contains("pickaxe"))) {
                  boolean silkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;
                  boolean ready = false;
                  if (properties != null && !stonePlayer) {
                     ++properties.breakLvl;
                     ready = properties.breakLvl > 9;
                  }

                  if (stonePlayer) {
                     EntityStoneStatue statue = (EntityStoneStatue)event.getTarget();
                     statue.setCrackAmount(statue.getCrackAmount() + 1);
                     ready = statue.getCrackAmount() > 9;
                  }

                  if (ready) {
                     event.getTarget().setDead();
                     if (silkTouch) {
                        ItemStack statuette = new ItemStack(ModItems.stone_statue);
                        statuette.setTagCompound(new NBTTagCompound());
                        statuette.getTagCompound().setBoolean("IAFStoneStatueEntityPlayer", stonePlayer);
                        statuette.getTagCompound().setInteger("IAFStoneStatueEntityID", stonePlayer ? 90 : EntityList.getID(event.getTarget().getClass()));
                        ((EntityLiving)event.getTarget()).writeEntityToNBT(statuette.getTagCompound());
                        if (!event.getTarget().world.isRemote) {
                           event.getTarget().entityDropItem(statuette, 1.0F);
                        }
                     } else if (!((EntityLiving)event.getTarget()).world.isRemote) {
                        event.getTarget().dropItem(Item.getItemFromBlock(Blocks.COBBLESTONE), 2 + event.getEntityLiving().getRNG().nextInt(4));
                     }
                  }
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onEntityUseItem(RightClickItem event) {
      if (event.getEntityLiving() instanceof EntityPlayer && event.getEntityLiving().rotationPitch > 87.0F && event.getEntityLiving().getRidingEntity() != null && event.getEntityLiving().getRidingEntity() instanceof EntityDragonBase) {
         ((EntityDragonBase)event.getEntityLiving().getRidingEntity()).processInteract((EntityPlayer)event.getEntityLiving(), event.getHand());
      }

   }

   @SubscribeEvent
   public void onEntityUpdate(LivingUpdateEvent event) {
      if (event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemSeaSerpentArmor || event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemSeaSerpentArmor || event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemSeaSerpentArmor || event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemSeaSerpentArmor) {
         event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 50, 0, false, false));
         if (event.getEntityLiving().isWet()) {
            int headMod = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemSeaSerpentArmor ? 1 : 0;
            int chestMod = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemSeaSerpentArmor ? 1 : 0;
            int legMod = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemSeaSerpentArmor ? 1 : 0;
            int footMod = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemSeaSerpentArmor ? 1 : 0;
            event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 50, headMod + chestMod + legMod + footMod - 1, false, false));
         }
      }

      if (IceAndFire.CONFIG.chickensLayRottenEggs && !event.getEntityLiving().world.isRemote && isAnimaniaChicken(event.getEntityLiving()) && !event.getEntityLiving().isChild() && event.getEntityLiving() instanceof EntityAnimal) {
         ChickenEntityProperties chickenProps = (ChickenEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), ChickenEntityProperties.class);
         if (chickenProps != null) {
            if (chickenProps.timeUntilNextEgg < 0) {
               chickenProps.timeUntilNextEgg = 0;
            }

            if (chickenProps.timeUntilNextEgg == 0) {
               if (event.getEntityLiving().getRNG().nextInt(IceAndFire.CONFIG.cockatriceEggChance + 1) == 0 && event.getEntityLiving().ticksExisted > 30) {
                  event.getEntityLiving().playSound(SoundEvents.ENTITY_CHICKEN_HURT, 2.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                  event.getEntityLiving().playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                  event.getEntityLiving().dropItem(ModItems.rotten_egg, 1);
               }

               chickenProps.timeUntilNextEgg = chickenProps.generateTime();
            } else if (chickenProps.timeUntilNextEgg > 0) {
               --chickenProps.timeUntilNextEgg;
            }
         }
      }

      FrozenEntityProperties frozenProps = (FrozenEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), FrozenEntityProperties.class);
      if (frozenProps != null) {
         boolean prevFrozen = frozenProps.isFrozen;
         if (event.getEntityLiving() instanceof EntityIceDragon) {
            frozenProps.isFrozen = false;
         }

         if (!event.getEntityLiving().world.isRemote) {
            if (frozenProps.isFrozen && event.getEntityLiving().isBurning()) {
               frozenProps.isFrozen = false;
               event.getEntityLiving().extinguish();
            }

            if (event.getEntityLiving().deathTime > 0) {
               frozenProps.isFrozen = false;
            }

            if (frozenProps.ticksUntilUnfrozen > 0) {
               --frozenProps.ticksUntilUnfrozen;
            } else {
               frozenProps.ticksUntilUnfrozen = 0;
               frozenProps.isFrozen = false;
            }
         }

         if (frozenProps.isFrozen && (!(event.getEntityLiving() instanceof EntityPlayer) || !((EntityPlayer)event.getEntityLiving()).isCreative())) {
            EntityLivingBase var10000 = event.getEntityLiving();
            var10000.motionX *= 0.25D;
            var10000 = event.getEntityLiving();
            var10000.motionZ *= 0.25D;
            if (!(event.getEntityLiving() instanceof EntityDragon)) {
               var10000 = event.getEntityLiving();
               var10000.motionY -= 0.1D;
            }
         }

         if (prevFrozen != frozenProps.isFrozen) {
            if (frozenProps.isFrozen) {
               event.getEntityLiving().playSound(SoundEvents.BLOCK_GLASS_PLACE, 1.0F, 1.0F);
            } else {
               for(int i = 0; i < 15; ++i) {
                  event.getEntityLiving().world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, event.getEntityLiving().posX + (this.rand.nextDouble() - 0.5D) * (double)event.getEntityLiving().width, event.getEntityLiving().posY + this.rand.nextDouble() * (double)event.getEntityLiving().height, event.getEntityLiving().posZ + (this.rand.nextDouble() - 0.5D) * (double)event.getEntityLiving().width, 0.0D, 0.0D, 0.0D, new int[]{Block.getIdFromBlock(ModBlocks.dragon_ice)});
               }

               event.getEntityLiving().playSound(SoundEvents.BLOCK_GLASS_BREAK, 3.0F, 1.0F);
            }
         }
      }

      if (event.getEntityLiving() instanceof EntityPlayer || event.getEntityLiving() instanceof EntityVillager || event.getEntityLiving() instanceof IHearsSiren) {
         SirenEntityProperties sirenProps = (SirenEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), SirenEntityProperties.class);
         if (sirenProps != null && sirenProps.sirenID != 0) {
            EntitySiren closestSiren = sirenProps.getSiren(event.getEntityLiving().world);
            if (closestSiren != null && closestSiren.isActuallySinging()) {
               if (!EntitySiren.isWearingEarplugs(event.getEntityLiving()) && sirenProps.singTime <= IceAndFire.CONFIG.sirenMaxSingTime) {
                  sirenProps.isCharmed = true;
                  ++sirenProps.singTime;
                  if (this.rand.nextInt(7) == 0) {
                     for(int i = 0; i < 5; ++i) {
                        event.getEntityLiving().world.spawnParticle(EnumParticleTypes.HEART, event.getEntityLiving().posX + (this.rand.nextDouble() - 0.5D) * 3.0D, event.getEntityLiving().posY + (this.rand.nextDouble() - 0.5D) * 3.0D, event.getEntityLiving().posZ + (this.rand.nextDouble() - 0.5D) * 3.0D, 0.0D, 0.0D, 0.0D, new int[0]);
                     }
                  }

                  EntityLivingBase entity = event.getEntityLiving();
                  if (entity.collidedHorizontally) {
                     if (entity instanceof EntityLiving) {
                        ((EntityLiving)entity).getJumpHelper().setJumping();
                     } else if (entity.onGround) {
                        entity.motionY = 0.41999998688697815D;
                     }
                  }

                  entity.motionX += (Math.signum(closestSiren.posX - entity.posX) * 0.5D - entity.motionX) * 0.100000000372529D;
                  entity.motionY += (Math.signum(closestSiren.posY - entity.posY + 1.0D) * 0.5D - entity.motionY) * 0.100000000372529D;
                  entity.motionZ += (Math.signum(closestSiren.posZ - entity.posZ) * 0.5D - entity.motionZ) * 0.100000000372529D;
                  float angle = (float)(Math.atan2(entity.motionZ, entity.motionX) * 180.0D / 3.141592653589793D) - 90.0F;
                  double d0 = closestSiren.posX - entity.posX;
                  double d2 = closestSiren.posZ - entity.posZ;
                  double d1 = closestSiren.posY - 1.0D - entity.posY;
                  if (entity.isRiding()) {
                     entity.dismountRidingEntity();
                  }

                  double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
                  float f = (float)(MathHelper.atan2(d2, d0) * 57.29577951308232D) - 90.0F;
                  float f1 = (float)(-(MathHelper.atan2(d1, d3) * 57.29577951308232D));
                  entity.rotationPitch = updateRotation(entity.rotationPitch, f1, 30.0F);
                  entity.rotationYaw = updateRotation(entity.rotationYaw, f, 30.0F);
                  if ((double)entity.getDistance(closestSiren) < 5.0D) {
                     sirenProps.isCharmed = false;
                     sirenProps.sirenID = 0;
                     sirenProps.singTime = 0;
                     closestSiren.singCooldown = IceAndFire.CONFIG.sirenTimeBetweenSongs;
                     closestSiren.setSinging(false);
                     closestSiren.setAttackTarget(entity);
                     closestSiren.setAggressive(true);
                     closestSiren.triggerOtherSirens(entity);
                  }

                  if (closestSiren.isDead || entity.getDistance(closestSiren) > 64.0F || sirenProps.getSiren(event.getEntityLiving().world) == null || entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative()) {
                     sirenProps.isCharmed = false;
                     sirenProps.sirenID = 0;
                     sirenProps.singTime = 0;
                  }
               } else {
                  sirenProps.isCharmed = false;
                  sirenProps.sirenID = 0;
                  sirenProps.singTime = 0;
                  closestSiren.singCooldown = IceAndFire.CONFIG.sirenTimeBetweenSongs;
               }
            }
         }
      }

      if (event.getEntityLiving() instanceof EntityLiving) {
         boolean stonePlayer = event.getEntityLiving() instanceof EntityStoneStatue;
         StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), StoneEntityProperties.class);
         if (properties != null && properties.isStone || stonePlayer) {
            EntityLiving living = (EntityLiving)event.getEntityLiving();
            if (!living.getPassengers().isEmpty()) {
               for(Entity e : living.getPassengers()) {
                  e.dismountRidingEntity();
               }
            }

            living.motionX *= 0.0D;
            living.motionZ *= 0.0D;
            living.motionY -= 0.1D;
            living.swingProgress = 0.0F;
            living.limbSwing = 0.0F;
            living.setInvisible(!stonePlayer);
            living.livingSoundTime = 0;
            living.hurtTime = 0;
            living.hurtResistantTime = living.maxHurtResistantTime - 1;
            living.extinguish();
            if (living instanceof EntityAnimal) {
               ((EntityAnimal)living).resetInLove();
            }

            if (!living.isAIDisabled()) {
               living.setNoAI(true);
            }

            if (living.getAttackTarget() != null) {
               living.setAttackTarget((EntityLivingBase)null);
            }

            if (living instanceof EntityHorse) {
               EntityHorse horse = (EntityHorse)living;
               horse.tailCounter = 0;
               horse.setEatingHaystack(false);
            }
         }
      }

   }

   public static float updateRotation(float angle, float targetAngle, float maxIncrease) {
      float f = MathHelper.wrapDegrees(targetAngle - angle);
      if (f > maxIncrease) {
         f = maxIncrease;
      }

      if (f < -maxIncrease) {
         f = -maxIncrease;
      }

      return angle + f;
   }

   @SubscribeEvent
   public void onEntityInteract(EntityInteractSpecific event) {
      if (event.getEntityLiving() instanceof EntityLiving) {
         StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), StoneEntityProperties.class);
         if (properties != null && properties.isStone) {
            event.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void onEntityInteract(EntityInteract event) {
      if (event.getEntityLiving() instanceof EntityLiving) {
         StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), StoneEntityProperties.class);
         if (properties != null && properties.isStone) {
            event.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void onPlayerRightClick(RightClickBlock event) {
      if (event.getEntityPlayer() != null && event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockChest) {
         float dist = (float)IceAndFire.CONFIG.dragonGoldSearchLength;
         List<Entity> list = event.getWorld().getEntitiesWithinAABBExcludingEntity(event.getEntityPlayer(), event.getEntityPlayer().getEntityBoundingBox().expand((double)dist, (double)dist, (double)dist));
         Collections.sort(list, new Sorter(event.getEntityPlayer()));
         if (!list.isEmpty()) {
            for(Entity entity : list) {
               if (entity instanceof EntityDragonBase) {
                  EntityDragonBase dragon = (EntityDragonBase)entity;
                  if (!dragon.isTamed() && !dragon.isModelDead() && !dragon.isOwner(event.getEntityPlayer()) && !event.getEntityPlayer().capabilities.isCreativeMode) {
                     dragon.setSleeping(false);
                     dragon.setSitting(false);
                     dragon.setAttackTarget(event.getEntityPlayer());
                  }
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onBreakBlock(BreakEvent event) {
      if (event.getPlayer() != null && (event.getState().getBlock() == ModBlocks.goldPile || event.getState().getBlock() == ModBlocks.silverPile)) {
         float dist = (float)IceAndFire.CONFIG.dragonGoldSearchLength;
         List<Entity> list = event.getWorld().getEntitiesWithinAABBExcludingEntity(event.getPlayer(), event.getPlayer().getEntityBoundingBox().expand((double)dist, (double)dist, (double)dist));
         Collections.sort(list, new Sorter(event.getPlayer()));
         if (!list.isEmpty()) {
            for(Entity entity : list) {
               if (entity instanceof EntityDragonBase) {
                  EntityDragonBase dragon = (EntityDragonBase)entity;
                  if (!dragon.isTamed() && !dragon.isModelDead() && !dragon.isOwner(event.getPlayer()) && !event.getPlayer().capabilities.isCreativeMode) {
                     dragon.setSleeping(false);
                     dragon.setSitting(false);
                     dragon.setAttackTarget(event.getPlayer());
                  }
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onChestGenerated(LootTableLoadEvent event) {
      if (event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {
         LootPool pool1 = event.getTable().getPool("pool1");
         if (pool1 != null) {
            pool1.addEntry(new LootEntryItem(ModItems.manuscript, 10, 5, new LootFunction[0], new LootCondition[0], "iceandfire:manuscript"));
         }
      }

   }

   @SubscribeEvent
   public void onPlayerLeaveEvent(PlayerLoggedOutEvent event) {
      if (event.player != null && !event.player.getPassengers().isEmpty()) {
         Iterator<Entity> itr = event.player.getPassengers().iterator();

         while(itr.hasNext()) {
            ((Entity)itr.next()).dismountRidingEntity();
         }
      }

   }

   @SubscribeEvent
   public void onEntityJoinWorld(EntityJoinWorldEvent event) {
      if (event.getEntity() != null && isAnimaniaSheep(event.getEntity()) && event.getEntity() instanceof EntityAnimal) {
         EntityAnimal animal = (EntityAnimal)event.getEntity();
         animal.tasks.addTask(8, new EntitySheepAIFollowCyclops(animal, 1.2D));
      }

      if (event.getEntity() != null && DragonUtils.isVillager(event.getEntity()) && event.getEntity() instanceof EntityCreature && IceAndFire.CONFIG.villagersFearDragons) {
         EntityCreature villager = (EntityCreature)event.getEntity();
         villager.tasks.addTask(1, new VillagerAIFearUntamed(villager, EntityLivingBase.class, VILLAGER_FEAR, 12.0F, 0.8D, 0.8D));
      }

      if (event.getEntity() != null && DragonUtils.isLivestock(event.getEntity()) && event.getEntity() instanceof EntityCreature && IceAndFire.CONFIG.animalsFearDragons) {
         final EntityCreature animal = (EntityCreature)event.getEntity();
         animal.tasks.addTask(1, new VillagerAIFearUntamed(animal, EntityLivingBase.class, new Predicate<EntityLivingBase>() {
            public boolean apply(@Nullable EntityLivingBase entity) {
               return entity != null && entity instanceof IAnimalFear && ((IAnimalFear)entity).shouldAnimalsFear(animal);
            }
         }, 12.0F, 1.2D, 1.5D));
      }

   }

   public static boolean isAnimaniaSheep(Entity entity) {
      String className = entity.getClass().getName();
      return className.contains("sheep") || entity instanceof EntitySheep;
   }

   public static boolean isAnimaniaChicken(Entity entity) {
      String className = entity.getClass().getName();
      return (className.contains("chicken") || entity instanceof EntityChicken) && entity instanceof EntityLiving && !entity.isCreatureType(EnumCreatureType.MONSTER, false);
   }

   public static boolean isAnimaniaFerret(Entity entity) {
      String className = entity.getClass().getName();
      return className.contains("ferret") || className.contains("polecat");
   }
}
