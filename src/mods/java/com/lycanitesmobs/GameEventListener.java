package com.lycanitesmobs;

import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.info.ItemConfig;
import com.lycanitesmobs.core.info.ItemManager;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.network.MessagePlayerLeftClick;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class GameEventListener {
   WorldEventListener worldEventListener = new WorldEventListener(this);

   public GameEventListener() {
   }

   @SubscribeEvent
   public void registerBlocks(Register<Block> event) {
      ObjectManager.registerBlocks(event);
      ItemManager.getInstance().registerBlockOres();
   }

   @SubscribeEvent
   public void registerItems(Register<Item> event) {
      ObjectManager.registerItems(event);
      ItemManager.getInstance().registerItemOres();
   }

   @SubscribeEvent
   public void registerPotions(Register<Potion> event) {
      ObjectManager.registerPotions(event);
   }

   @SubscribeEvent
   public void registerEntities(Register<EntityEntry> event) {
      ObjectManager.registerSpecialEntities(event);
   }

   @SubscribeEvent
   public void onWorldLoading(Load event) {
      if (event.getWorld() != null) {
         ExtendedWorld.getForWorld(event.getWorld());
         event.getWorld().addEventListener(this.worldEventListener);
      }
   }

   @SubscribeEvent
   public void onWorldUnloading(Unload event) {
      ExtendedWorld.remove(event.getWorld());
   }

   @SubscribeEvent
   public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof EntityLivingBase) {
         event.addCapability(new ResourceLocation("lycanitesmobs", "IExtendedEntity"), new ICapabilitySerializable<NBTTagCompound>() {
            private final ExtendedEntity instance = (ExtendedEntity)LycanitesMobs.EXTENDED_ENTITY.getDefaultInstance();

            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
               return capability == LycanitesMobs.EXTENDED_ENTITY;
            }

            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
               return (T)(capability == LycanitesMobs.EXTENDED_ENTITY ? LycanitesMobs.EXTENDED_ENTITY.cast(this.instance) : null);
            }

            public NBTTagCompound serializeNBT() {
               return (NBTTagCompound)LycanitesMobs.EXTENDED_ENTITY.getStorage().writeNBT(LycanitesMobs.EXTENDED_ENTITY, this.instance, (EnumFacing)null);
            }

            public void deserializeNBT(NBTTagCompound nbt) {
               LycanitesMobs.EXTENDED_ENTITY.getStorage().readNBT(LycanitesMobs.EXTENDED_ENTITY, this.instance, (EnumFacing)null, nbt);
            }
         });
      }

      if (event.getObject() instanceof EntityPlayer) {
         event.addCapability(new ResourceLocation("lycanitesmobs", "IExtendedPlayer"), new ICapabilitySerializable<NBTTagCompound>() {
            private final ExtendedPlayer instance = (ExtendedPlayer)LycanitesMobs.EXTENDED_PLAYER.getDefaultInstance();

            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
               return capability == LycanitesMobs.EXTENDED_PLAYER;
            }

            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
               return (T)(capability == LycanitesMobs.EXTENDED_PLAYER ? LycanitesMobs.EXTENDED_PLAYER.cast(this.instance) : null);
            }

            public NBTTagCompound serializeNBT() {
               return (NBTTagCompound)LycanitesMobs.EXTENDED_PLAYER.getStorage().writeNBT(LycanitesMobs.EXTENDED_PLAYER, this.instance, (EnumFacing)null);
            }

            public void deserializeNBT(NBTTagCompound nbt) {
               LycanitesMobs.EXTENDED_PLAYER.getStorage().readNBT(LycanitesMobs.EXTENDED_PLAYER, this.instance, (EnumFacing)null, nbt);
            }
         });
      }

   }

   @SubscribeEvent
   public void onPlayerClone(Clone event) {
      ExtendedPlayer extendedPlayerOld = ExtendedPlayer.getForPlayer(event.getOriginal());
      ExtendedPlayer extendedPlayerNew = ExtendedPlayer.getForPlayer(event.getEntityPlayer());
      NBTTagCompound nbt = new NBTTagCompound();
      extendedPlayerOld.writeNBT(nbt);
      extendedPlayerNew.readNBT(nbt);
   }

   @SubscribeEvent
   public void onPlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
      ExtendedPlayer.getForPlayer(event.player).needsFullSync = true;
   }

   @SubscribeEvent
   public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
      ExtendedPlayer.getForPlayer(event.player).needsFullSync = true;
   }

   @SubscribeEvent
   public void onEntityConstructing(EntityConstructing event) {
      if (event.getEntity() != null && event.getEntity().getEntityWorld() != null && !event.getEntity().getEntityWorld().isRemote) {
         if (!(event.getEntity() instanceof EntityLivingBase) && ExtendedEntity.FORCE_REMOVE_ENTITY_IDS != null && ExtendedEntity.FORCE_REMOVE_ENTITY_IDS.length > 0) {
            LycanitesMobs.logDebug("ForceRemoveEntity", "Forced entity removal, checking: " + event.getEntity().getName());

            for(String forceRemoveID : ExtendedEntity.FORCE_REMOVE_ENTITY_IDS) {
               if (forceRemoveID.equalsIgnoreCase(event.getEntity().getName())) {
                  event.getEntity().setDead();
                  break;
               }
            }
         }

      }
   }

   @SubscribeEvent
   public void onLivingDeathEvent(LivingDeathEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (entity != null) {
         ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
         if (extendedEntity != null) {
            extendedEntity.onDeath();
         }

         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
            if (extendedPlayer != null) {
               extendedPlayer.onDeath();
            }
         }

      }
   }

   @SubscribeEvent
   public void onEntityUpdate(LivingUpdateEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (entity != null) {
         ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
         if (extendedEntity != null) {
            extendedEntity.onUpdate();
         }

         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
            if (playerExt != null) {
               playerExt.onUpdate();
            }
         }

      }
   }

   @SubscribeEvent
   public void onPlayerLeftClickEmpty(LeftClickEmpty event) {
      EntityPlayer player = event.getEntityPlayer();
      if (player != null) {
         ItemStack itemStack = player.getHeldItem(event.getHand());
         Item item = itemStack.getItem();
         if (item instanceof ItemEquipment) {
            MessagePlayerLeftClick message = new MessagePlayerLeftClick();
            LycanitesMobs.packetHandler.sendToServer(message);
         }

      }
   }

   @SubscribeEvent
   public void onPlayerLeftClickBlock(LeftClickBlock event) {
      EntityPlayer player = event.getEntityPlayer();
      if (player != null && !event.getSide().isClient()) {
         ItemStack itemStack = player.getHeldItem(event.getHand());
         Item item = itemStack.getItem();
         if (item instanceof ItemEquipment) {
            ((ItemEquipment)item).onItemLeftClick(event.getWorld(), player, event.getHand());
         }

      }
   }

   @SubscribeEvent
   public void onEntityInteract(EntityInteract event) {
      EntityPlayer player = event.getEntityPlayer();
      Entity entity = event.getTarget();
      if (player != null && entity != null) {
         if (player.getHeldItem(event.getHand()) != null) {
            ItemStack itemStack = player.getHeldItem(event.getHand());
            Item item = itemStack.getItem();
            if (item instanceof ItemBase && ((ItemBase)item).onItemRightClickOnEntity(player, entity, itemStack) && event.isCancelable()) {
               event.setCanceled(true);
            }
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onAttackTarget(LivingSetAttackTargetEvent event) {
      Entity targetEntity = event.getTarget();
      if (event.getEntityLiving() != null && targetEntity != null) {
         if (!event.getEntityLiving().isPotionActive(MobEffects.NIGHT_VISION) && targetEntity.isInvisible()) {
            if (event.isCancelable()) {
               event.setCanceled(true);
            }

         } else {
            if (event.getEntityLiving() instanceof EntityLiving && targetEntity instanceof BaseCreatureEntity && !((BaseCreatureEntity)targetEntity).canBeTargetedBy(event.getEntityLiving()) && event.isCancelable()) {
               event.setCanceled(true);
            }

         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onLivingHurt(LivingHurtEvent event) {
      if (!event.isCanceled()) {
         if (event.getSource() != null && event.getEntityLiving() != null) {
            EntityLivingBase damagedEntity = event.getEntityLiving();
            ExtendedEntity damagedEntityExt = ExtendedEntity.getForEntity(damagedEntity);
            if (event.getSource() instanceof EntityDamageSource) {
               EntityDamageSource entityDamageSource = (EntityDamageSource)event.getSource();
               if (entityDamageSource.getTrueSource() != null && entityDamageSource.getTrueSource() instanceof EntityLivingBase) {
                  ExtendedEntity attackerExtendedEntity = ExtendedEntity.getForEntity((EntityLivingBase)entityDamageSource.getTrueSource());
                  if (attackerExtendedEntity != null) {
                     attackerExtendedEntity.setLastAttackedEntity(damagedEntity);
                  }
               }
            }

            if (damagedEntity.getRidingEntity() != null && damagedEntity.getRidingEntity() instanceof RideableCreatureEntity) {
               RideableCreatureEntity creatureRideable = (RideableCreatureEntity)event.getEntityLiving().getRidingEntity();
               if (creatureRideable.isBlocking()) {
                  event.setAmount(0.0F);
                  event.setCanceled(true);
                  return;
               }

               if ("inWall".equals(event.getSource().damageType)) {
                  event.setAmount(0.0F);
                  event.setCanceled(true);
                  return;
               }

               if (!creatureRideable.isDamageTypeApplicable(event.getSource().damageType, event.getSource(), event.getAmount())) {
                  event.setAmount(0.0F);
                  event.setCanceled(true);
                  return;
               }
            }

            if (damagedEntityExt != null && damagedEntityExt.isPickedUp() && "inWall".equals(event.getSource().damageType)) {
               event.setAmount(0.0F);
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public void onLivingDrops(LivingDropsEvent event) {
      World world = event.getEntityLiving().getEntityWorld();
      if (ItemConfig.seasonalItemDropChance > 0.0D && (Utilities.isHalloween() || Utilities.isYuletide() || Utilities.isNewYear())) {
         boolean noSeaonalDrop = false;
         if (event.getEntityLiving() instanceof BaseCreatureEntity && ((BaseCreatureEntity)event.getEntityLiving()).isMinion()) {
            noSeaonalDrop = true;
         }

         Item seasonalItem = null;
         if (Utilities.isHalloween()) {
            seasonalItem = ObjectManager.getItem("halloweentreat");
         }

         if (Utilities.isYuletide()) {
            seasonalItem = ObjectManager.getItem("wintergift");
            if (Utilities.isYuletidePeak() && world.rand.nextBoolean()) {
               seasonalItem = ObjectManager.getItem("wintergiftlarge");
            }
         }

         if (seasonalItem != null && !noSeaonalDrop && (double)event.getEntityLiving().getRNG().nextFloat() < ItemConfig.seasonalItemDropChance) {
            ItemStack dropStack = new ItemStack(seasonalItem, 1);
            EntityItemCustom entityItem = new EntityItemCustom(world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, dropStack);
            entityItem.setPickupDelay(10);
            world.spawnEntity(entityItem);
         }
      }

   }

   @SubscribeEvent
   public void onMountEntity(EntityMountEvent event) {
      if (event.isMounting() && event.getEntityBeingMounted() instanceof EntityBoat && event.getEntityBeingMounted() instanceof EntityMinecart && event.getEntityMounting() instanceof BaseCreatureEntity) {
         BaseCreatureEntity creatureEntity = (BaseCreatureEntity)event.getEntityMounting();
         if (creatureEntity.isBoss() || creatureEntity.width >= 2.0F || creatureEntity.height >= 2.0F) {
            event.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void onBucketFill(FillBucketEvent event) {
      World world = event.getWorld();
      RayTraceResult target = event.getTarget();
      if (target != null) {
         BlockPos pos = target.getBlockPos();
         Block block = world.getBlockState(pos).getBlock();
         Item bucket = (Item)ObjectManager.buckets.get(block);
         if (bucket != null && ((Integer)world.getBlockState(pos).getValue(BlockLiquid.LEVEL)).intValue() == 0) {
            world.setBlockToAir(pos);
         }

         if (bucket != null) {
            event.setFilledBucket(new ItemStack(bucket));
            event.setResult(Result.ALLOW);
         }
      }
   }

   @SubscribeEvent
   public void onBlockBreak(BreakEvent event) {
      if (event.getState() != null && event.getWorld() != null && !event.isCanceled() && !event.getWorld().isRemote) {
         if (event.getPlayer() != null && !event.getPlayer().isCreative()) {
            ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(event.getWorld());
            if (!(event.getState().getBlock() instanceof BlockFireBase) && extendedWorld.isBossNearby(new Vec3d(event.getPos()))) {
               event.setCanceled(true);
               event.setResult(Result.DENY);
               event.getPlayer().sendStatusMessage(new TextComponentString(LanguageManager.translate("boss.block.protection.break")), true);
               return;
            }
         }

         if (event.getPlayer() != null) {
            ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(event.getPlayer());
            if (extendedPlayer == null) {
               return;
            }

            extendedPlayer.setJustBrokenBlock(event.getState());
         }

      }
   }

   @SubscribeEvent
   public void onBlockPlace(PlaceEvent event) {
      if (event.getState() != null && event.getWorld() != null && !event.isCanceled()) {
         if (event.getPlayer() != null && !event.getPlayer().isCreative()) {
            ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(event.getWorld());
            if (extendedWorld.isBossNearby(new Vec3d(event.getPos()))) {
               event.setCanceled(true);
               event.setResult(Result.DENY);
               event.getPlayer().sendStatusMessage(new TextComponentString(LanguageManager.translate("boss.block.protection.place")), true);
            }
         }

      }
   }

   @SubscribeEvent
   public void onEntityMount(EntityMountEvent event) {
      if (LycanitesMobs.config.getBool("Extras", "Disable Sneak Dismount", true)) {
         if (event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer && event.getEntityBeingMounted() instanceof RideableCreatureEntity) {
            ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer((EntityPlayer)event.getEntityMounting());
            if (extendedPlayer != null) {
               event.setCanceled(event.getEntityMounting().isSneaking() && !extendedPlayer.isControlActive(ExtendedPlayer.CONTROL_ID.MOUNT_DISMOUNT));
            }
         }
      }
   }

   @SubscribeEvent
   public void onProjectileImpact(ProjectileImpactEvent event) {
      Entity shooter = null;
      Entity target = event.getRayTraceResult().entityHit;
      if (target instanceof BaseCreatureEntity) {
         BaseCreatureEntity targetCreature = (BaseCreatureEntity)target;
         if (event.getEntity() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow)event.getEntity();
            shooter = arrow.shootingEntity;
         }

         if (event.getEntity() instanceof EntityThrowable) {
            EntityThrowable throwable = (EntityThrowable)event.getEntity();
            shooter = throwable.getThrower();
         }

         if (shooter != null && !targetCreature.isDamageEntityApplicable(shooter)) {
            event.setCanceled(true);
         }

      }
   }
}
