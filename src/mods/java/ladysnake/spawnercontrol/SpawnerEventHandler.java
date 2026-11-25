package ladysnake.spawnercontrol;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.annotations.VisibleForTesting;
import java.util.HashSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import ladysnake.spawnercontrol.config.MSCConfig;
import ladysnake.spawnercontrol.config.SpawnerConfig;
import ladysnake.spawnercontrol.controlledspawner.BlockControlledSpawner;
import ladysnake.spawnercontrol.controlledspawner.CapabilityControllableSpawner;
import ladysnake.spawnercontrol.controlledspawner.IControllableSpawner;
import ladysnake.spawnercontrol.controlledspawner.TileEntityControlledSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

public class SpawnerEventHandler {
   private static final String NBT_TAG_SPAWNER_POS = "spawnercontrol:spawnerPos";
   @VisibleForTesting
   static Set<TileEntityMobSpawner> allSpawners = new HashSet<TileEntityMobSpawner>();

   public SpawnerEventHandler() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(AttachCapabilitiesEvent.class, TileEntity.class, SpawnerEventHandler::onAttachCapabilities);
      bus.register(WorldTickEvent.class, SpawnerEventHandler::onTickWorldTick);
      bus.register(CheckSpawn.class, EventPriority.LOWEST, false, SpawnerEventHandler::onCheckSpawnerSpawn);
      bus.register(SpecialSpawn.class, SpawnerEventHandler::onLivingSpawnSpecialSpawn);
      bus.register(LivingDeathEvent.class, SpawnerEventHandler::onLivingDeath);
      bus.register(LivingExperienceDropEvent.class, SpawnerEventHandler::onLivingExperienceDrop);
      bus.register(LivingDropsEvent.class, EventPriority.LOWEST, false, SpawnerEventHandler::onLivingItemDrop);
      bus.register(BreakEvent.class, SpawnerEventHandler::onBlockBreak);
      bus.register(HarvestDropsEvent.class, SpawnerEventHandler::onBlockHarvestDrops);
   }

   public static void onAttachCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
      if (event.getObject() instanceof TileEntityMobSpawner) {
         TileEntityMobSpawner spawner = (TileEntityMobSpawner)event.getObject();
         if ((MSCConfig.alterVanillaSpawner || spawner instanceof TileEntityControlledSpawner) && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            allSpawners.add(spawner);
         }

         if (!(spawner instanceof TileEntityControlledSpawner)) {
            event.addCapability(CapabilityControllableSpawner.CAPABILITY_KEY, new CapabilityControllableSpawner.Provider(spawner));
         }
      }

   }

   public static void onTickWorldTick(WorldTickEvent event) {
      if (event.phase != Phase.START && event.side != Side.CLIENT) {
         Iterator<TileEntityMobSpawner> iterator = allSpawners.iterator();

         while(iterator.hasNext()) {
            TileEntityMobSpawner spawner = (TileEntityMobSpawner)iterator.next();
            if (spawner.isInvalid()) {
               iterator.remove();
            } else if (spawner.getWorld() == event.world) {
               IControllableSpawner handler = CapabilityControllableSpawner.getHandler(spawner);
               if (!handler.canSpawn()) {
                  if (handler.getConfig().breakSpawner) {
                     spawner.getWorld().setBlockToAir(spawner.getPos());
                  }

                  if (!spawner.getWorld().isRemote) {
                     double x = (double)((float)spawner.getPos().getX() + spawner.getWorld().rand.nextFloat());
                     double y = (double)((float)spawner.getPos().getY() + spawner.getWorld().rand.nextFloat());
                     double z = (double)((float)spawner.getPos().getZ() + spawner.getWorld().rand.nextFloat());
                     ((WorldServer)spawner.getWorld()).spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 3, 0.0D, 0.0D, 0.0D, 0.0D, new int[0]);
                  }
               }
            }
         }

      }
   }

   public static void onCheckSpawnerSpawn(CheckSpawn event) {
      MobSpawnerBaseLogic spawner = event.getSpawner();
      if (spawner != null) {
         if (event.getWorld().getTileEntity(spawner.getSpawnerPosition()) == null && spawner.getSpawnerEntity() == null) {
            event.setResult(Result.DENY);
         }

         IControllableSpawner handler = SpawnerUtil.getHandlerIfAffected(event.getWorld(), spawner.getSpawnerPosition());
         if (handler == null) {
            return;
         }

         SpawnerConfig cfg = handler.getConfig();
         boolean canSpawn;
         if (event.getResult() == Result.DEFAULT && event.getEntityLiving() instanceof EntityLiving) {
            EntityLiving spawned = (EntityLiving)event.getEntityLiving();
            canSpawn = spawned.isNotColliding();
            if (cfg.spawnConditions.forceSpawnerMobSpawns && event.getEntity() instanceof IMob) {
               if (cfg.spawnConditions.checkSunlight) {
                  canSpawn &= !event.getWorld().canSeeSky(new BlockPos(spawned)) || !event.getWorld().isDaytime();
               }
            } else if (!cfg.spawnConditions.forceSpawnerAllSpawns) {
               canSpawn &= spawned.getCanSpawnHere();
            }
         } else {
            canSpawn = event.getResult() == Result.ALLOW;
         }

         if (canSpawn) {
            if (handler.canSpawn()) {
               if (!handler.getConfig().incrementOnMobDeath) {
                  handler.incrementSpawnedMobsCount();
               }

               NBTTagCompound compound = new NBTTagCompound();
               World spawnerWorld = spawner.getSpawnerWorld();
               if (spawnerWorld != null) {
                  compound.setInteger("dimension", spawnerWorld.provider.getDimension());
                  compound.setLong("pos", spawner.getSpawnerPosition().toLong());
                  event.getEntity().getEntityData().setTag("spawnercontrol:spawnerPos", compound);
               }

               event.setResult(Result.ALLOW);
            } else {
               event.setResult(Result.DENY);
            }
         } else {
            event.setResult(Result.DENY);
         }
      }

   }

   public static void onLivingSpawnSpecialSpawn(SpecialSpawn event) {
      if (event.getSpawner() != null) {
         IControllableSpawner handler = SpawnerUtil.getHandlerIfAffected(event.getWorld(), event.getSpawner().getSpawnerPosition());
         if (handler != null) {
            if (!handler.canSpawn()) {
               event.setResult(Result.DENY);
            }

         }
      }
   }

   public static void onLivingDeath(LivingDeathEvent event) {
      NBTTagCompound data = event.getEntityLiving().getEntityData();
      if (data.hasKey("spawnercontrol:spawnerPos")) {
         NBTBase nbt = event.getEntityLiving().getEntityData().getTag("spawnercontrol:spawnerPos");
         World world;
         long spawnerPos;
         if (nbt instanceof NBTTagCompound) {
            spawnerPos = ((NBTTagCompound)nbt).getLong("pos");
            world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(((NBTTagCompound)nbt).getInteger("dimension"));
         } else {
            if (!(nbt instanceof NBTTagLong)) {
               return;
            }

            spawnerPos = ((NBTTagLong)nbt).getLong();
            world = event.getEntity().getEntityWorld();
         }

         TileEntity tile = world.getTileEntity(BlockPos.fromLong(spawnerPos));
         if (tile instanceof TileEntityMobSpawner) {
            TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
            IControllableSpawner handler = CapabilityControllableSpawner.getHandler(spawner);
            if (handler.getConfig().incrementOnMobDeath) {
               handler.incrementSpawnedMobsCount();
            }
         }
      }

   }

   public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
      SpawnerConfig cfg = SpawnerUtil.getConfig(event.getEntityLiving().getEntityData().getTag("spawnercontrol:spawnerPos"));
      if (cfg != null) {
         try {
            ResourceLocation rl = EntityList.getKey(event.getEntity());
            if (rl != null) {
               SpawnerConfig.MobLoot.MobLootEntry entry = (SpawnerConfig.MobLoot.MobLootEntry)cfg.mobLoot.lootEntries.get(rl);
               event.setDroppedExperience(MathHelper.floor((float)event.getOriginalExperience() * entry.xpMultiplier + (float)entry.flatXpIncrease + (float)(event.getDroppedExperience() - event.getOriginalExperience())));
            }
         } catch (ExecutionException var4) {
            SpawnerControl.LOGGER.error("Error while handling spawned experience drop", var4);
         }
      }

   }

   public static void onLivingItemDrop(LivingDropsEvent event) {
      EntityLivingBase livingBase = event.getEntityLiving();
      SpawnerConfig cfg = SpawnerUtil.getConfig(livingBase.getEntityData().getTag("spawnercontrol:spawnerPos"));
      if (cfg != null) {
         try {
            ResourceLocation rl = EntityList.getKey(event.getEntity());
            if (rl != null) {
               SpawnerConfig.MobLoot.MobLootEntry entry = (SpawnerConfig.MobLoot.MobLootEntry)cfg.mobLoot.lootEntries.get(rl);
               List<EntityItem> drops = event.getDrops();
               if (entry.removeAllItems) {
                  drops.clear();
               } else {
                  for(EntityItem drop : (EntityItem[])drops.toArray(new EntityItem[0])) {
                     ItemStack stack = drop.getItem();

                     for(String s : entry.removedItems) {
                        String[] split = s.split(":");
                        if (stack.getUnlocalizedName().equals(split[0] + ":" + split[1]) && (split.length < 3 || stack.getMetadata() == Integer.parseInt(split[2]))) {
                           drops.remove(drop);
                        }
                     }
                  }
               }

               World world = livingBase.world;
               double x = livingBase.posX;
               double y = livingBase.posY;
               double z = livingBase.posZ;

               for(String s : entry.addedItems) {
                  String[] split = s.split(":");
                  if (split.length < 5 || world.rand.nextDouble() < Double.parseDouble(split[4])) {
                     ResourceLocation itemRL = new ResourceLocation(split[0], split[1]);
                     Item item = (Item)ForgeRegistries.ITEMS.getValue(itemRL);
                     if (item == null) {
                        item = Item.getItemFromBlock((Block)ForgeRegistries.BLOCKS.getValue(itemRL));
                     }

                     if (item != Items.AIR) {
                        int quantity = split.length < 3 ? 1 : Integer.parseInt(split[2]);
                        int meta = split.length < 4 ? 0 : Integer.parseInt(split[3]);
                        drops.add(new EntityItem(world, x, y, z, new ItemStack(item, quantity, meta)));
                     } else {
                        SpawnerControl.LOGGER.error("Error while handling spawned item drops");
                     }
                  }
               }
            }
         } catch (ExecutionException | NumberFormatException var22) {
            SpawnerControl.LOGGER.error("Error while handling spawned item drops", var22);
         }
      }

   }

   public static void onBlockBreak(BreakEvent event) {
      SpawnerConfig cfg = SpawnerUtil.getConfig(event.getWorld(), event.getPos());
      if (cfg != null) {
         int xp = cfg.xpDropped;
         if (cfg.randXpVariation > 0) {
            xp += event.getWorld().rand.nextInt(cfg.randXpVariation) + event.getWorld().rand.nextInt(cfg.randXpVariation);
         }

         event.setExpToDrop(xp);
      }

   }

   public static void onBlockHarvestDrops(HarvestDropsEvent event) {
      if (event.getHarvester() != null) {
         Block block = event.getState().getBlock();
         SpawnerConfig cfg;
         if (block instanceof BlockControlledSpawner) {
            cfg = ((BlockControlledSpawner)block).getConfig();
         } else {
            if (!(block instanceof BlockMobSpawner) || !MSCConfig.alterVanillaSpawner) {
               return;
            }

            cfg = MSCConfig.vanillaSpawnerConfig;
         }

         List<ItemStack> drops = event.getDrops();

         for(String entry : cfg.itemsDropped) {
            String[] split = entry.split(":");
            if (split.length > 1) {
               Item item = (Item)Item.REGISTRY.getObject(new ResourceLocation(split[0], split[1]));

               try {
                  if (item != null) {
                     int count = split.length >= 3 ? Integer.parseInt(split[2]) : 1;
                     int meta = split.length >= 4 ? Integer.parseInt(split[3]) : 0;
                     if (split.length < 5 || event.getWorld().rand.nextDouble() < Double.parseDouble(split[4])) {
                        drops.add(new ItemStack(item, count, meta));
                     }
                  }
               } catch (NumberFormatException var12) {
                  ;
               }
            }
         }
      }

   }
}
