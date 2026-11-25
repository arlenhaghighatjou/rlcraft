package com.jamieswhiteshirt.trumpetskeleton;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.jamieswhiteshirt.trumpetskeleton.common.CommonProxy;
import com.jamieswhiteshirt.trumpetskeleton.common.TrumpetSkeletonItems;
import com.jamieswhiteshirt.trumpetskeleton.common.TrumpetSkeletonSoundEvents;
import com.jamieswhiteshirt.trumpetskeleton.common.entity.EntityTrumpetSkeleton;
import com.jamieswhiteshirt.trumpetskeleton.common.item.ItemTrumpet;
import java.util.ArrayList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "trumpetskeleton",
   version = "1.12-1.0.2.1",
   acceptedMinecraftVersions = "[1.12,1.13)",
   dependencies = "required-after:forge@[14.21.1.2387,)",
   name = "Trumpet Skeleton"
)
public class TrumpetSkeleton {
   public static final String MODID = "trumpetskeleton";
   public static final String VERSION = "1.12-1.0.2.1";
   public static final ResourceLocation ENTITIES_TRUMPET_SKELETON_LOOT_TABLE = new ResourceLocation("trumpetskeleton", "entities/trumpet_skeleton");
   public static TrumpetSkeleton instance;
   public static CommonProxy proxy;
   public static final Logger logger = LogManager.getLogger("trumpetskeleton");

   public TrumpetSkeleton() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.jamieswhiteshirt.trumpetskeleton.client.ClientProxy() : new com.jamieswhiteshirt.trumpetskeleton.server.ServerProxy();
         com.jamieswhiteshirt.trumpetskeleton.common.TrumpetSkeletonItems.registerHolders();
         com.jamieswhiteshirt.trumpetskeleton.common.TrumpetSkeletonSoundEvents.registerHolders();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      this.register(MinecraftForge.EVENT_BUS);
      LootTableList.register(ENTITIES_TRUMPET_SKELETON_LOOT_TABLE);
      proxy.preInit(event);
   }

   public void init(FMLInitializationEvent event) {
      EntityParrot.registerMimicSound(EntityTrumpetSkeleton.class, TrumpetSkeletonSoundEvents.E_PARROT_IM_TRUMPET_SKELETON);
   }

   public void register(EventBus bus) {
      bus.registerGeneric(Register.class, Item.class, this::registerItems);
      bus.registerGeneric(Register.class, SoundEvent.class, this::registerSoundEvents);
      bus.registerGeneric(Register.class, EntityEntry.class, this::registerEntityEntries);
      bus.register(Tick.class, this::onActiveItemUseTick);
      bus.register(PlaySoundAtEntityEvent.class, this::onPlaySoundAtEntity);
   }

   public void registerItems(Register<Item> event) {
      event.getRegistry().registerAll(new Item[]{((Item)(new ItemTrumpet()).setUnlocalizedName("trumpetskeleton.trumpet").setRegistryName("trumpetskeleton", "trumpet")).setCreativeTab(CreativeTabs.MISC)});
   }

   public void registerSoundEvents(Register<SoundEvent> event) {
      event.getRegistry().registerAll(new SoundEvent[]{(SoundEvent)(new SoundEvent(new ResourceLocation("trumpetskeleton", "entity.trumpet_skeleton.ambient"))).setRegistryName(new ResourceLocation("trumpetskeleton", "entity.trumpet_skeleton.ambient")), (SoundEvent)(new SoundEvent(new ResourceLocation("trumpetskeleton", "item.trumpet.use"))).setRegistryName(new ResourceLocation("trumpetskeleton", "item.trumpet.use")), (SoundEvent)(new SoundEvent(new ResourceLocation("trumpetskeleton", "entity.parrot.imitate.trumpet_skeleton"))).setRegistryName(new ResourceLocation("trumpetskeleton", "entity.parrot.imitate.trumpet_skeleton"))});
   }

   public void registerEntityEntries(Register<EntityEntry> event) {
      EntityRegistry.registerModEntity(new ResourceLocation("trumpetskeleton", "trumpet_skeleton"), EntityTrumpetSkeleton.class, "trumpetskeleton.TrumpetSkeleton", 0, this, 80, 3, false, 12698049, 16579584, EntityTrumpetSkeleton::new);

      for(Biome biome : ForgeRegistries.BIOMES.getValues()) {
         for(SpawnListEntry entry : new ArrayList<SpawnListEntry>(biome.getSpawnableList(EnumCreatureType.MONSTER))) {
            if (entry.entityClass == EntitySkeleton.class) {
               EntityRegistry.addSpawn(EntityTrumpetSkeleton.class, entry.itemWeight / 4, entry.minGroupCount, entry.maxGroupCount, EnumCreatureType.MONSTER, new Biome[]{biome});
            }
         }
      }

   }

   public static void scare(World world, EntityLivingBase user) {
      if (!world.isRemote) {
         for(EntityLivingBase spookedEntity : world.getEntitiesWithinAABB(EntityLivingBase.class, user.getEntityBoundingBox().grow(10.0D))) {
            if (spookedEntity != user) {
               double deltaX = spookedEntity.posX - user.posX + world.rand.nextDouble() - world.rand.nextDouble();
               double deltaZ = spookedEntity.posZ - user.posZ + world.rand.nextDouble() - world.rand.nextDouble();
               double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
               spookedEntity.velocityChanged = true;
               spookedEntity.addVelocity(0.5D * deltaX / distance, 5.0D / (10.0D + distance), 0.5D * deltaZ / distance);
               spookedEntity.setRevengeTarget(user);
            }
         }
      }

   }

   public void onActiveItemUseTick(Tick event) {
      ItemStack stack = event.getItem();
      if (stack.getItem() == TrumpetSkeletonItems.TRUMPET) {
         if (event.getDuration() == stack.getMaxItemUseDuration() - 10) {
            EntityLivingBase user = event.getEntityLiving();
            World world = user.world;
            user.playSound(TrumpetSkeletonSoundEvents.ITEM_TRUMPET_USE, 1.0F, 0.9F + world.rand.nextFloat() * 0.2F);
            scare(world, user);
            stack.damageItem(1, user);
         } else if (event.getDuration() <= stack.getMaxItemUseDuration() - 15) {
            event.setCanceled(true);
         }
      }

   }

   public void onPlaySoundAtEntity(PlaySoundAtEntityEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         if (player.getActiveItemStack().getItem() == TrumpetSkeletonItems.TRUMPET && event.getSound() == SoundEvents.ENTITY_GENERIC_EAT) {
            event.setCanceled(true);
         }
      }

   }
}
