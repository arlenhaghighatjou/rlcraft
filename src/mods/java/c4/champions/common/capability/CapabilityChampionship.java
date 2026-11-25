package c4.champions.common.capability;

import c4.champions.Champions;
import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.IAffix;
import c4.champions.common.affix.filter.AffixFilter;
import c4.champions.common.affix.filter.AffixFilterManager;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import c4.champions.integrations.gamestages.ChampionStages;
import c4.champions.network.NetworkHandler;
import c4.champions.network.PacketSyncAffix;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class CapabilityChampionship {
   @CapabilityInject(IChampionship.class)
   public static final Capability<IChampionship> CHAMPION_CAP = null;
   public static final EnumFacing DEFAULT_FACING = null;
   public static final ResourceLocation ID = new ResourceLocation("champions", "championship");
   private static final String AFFIX_TAG = "affixes";
   private static final String TIER_TAG = "tier";
   private static final String DATA_TAG = "data";
   private static final String NAME_TAG = "name";

   public CapabilityChampionship() {
   }

   @Nullable
   public static IChampionship getChampionship(EntityLiving entityIn) {
      return entityIn != null && entityIn.hasCapability(CHAMPION_CAP, DEFAULT_FACING) ? (IChampionship)entityIn.getCapability(CHAMPION_CAP, DEFAULT_FACING) : null;
   }

   public static void register() {
      CapabilityManager.INSTANCE.register(IChampionship.class, new IStorage<IChampionship>() {
         public NBTBase writeNBT(Capability<IChampionship> capability, IChampionship instance, EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            Rank rank = instance.getRank();
            if (rank != null) {
               if (rank.getTier() > 0) {
                  NBTTagList list = new NBTTagList();
                  UnmodifiableIterator var7 = instance.getAffixes().iterator();

                  while(var7.hasNext()) {
                     String id = (String)var7.next();
                     NBTTagCompound tag = new NBTTagCompound();
                     tag.setString("identifier", id);
                     tag.setTag("data", instance.getAffixData(id));
                     list.appendTag(tag);
                  }

                  compound.setTag("affixes", list);
                  compound.setString("name", instance.getName());
               }

               compound.setInteger("tier", rank.getTier());
            }

            return compound;
         }

         public void readNBT(Capability<IChampionship> capability, IChampionship instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound compound = (NBTTagCompound)nbt;
            if (compound.hasKey("tier")) {
               int tier = compound.getInteger("tier");
               instance.setRank(RankManager.getRankForTier(tier));
               if (tier > 0) {
                  NBTTagList list = compound.getTagList("affixes", 10);
                  Map<String, NBTTagCompound> affixes = Maps.newHashMap();

                  for(int i = 0; i < list.tagCount(); ++i) {
                     NBTTagCompound tag = list.getCompoundTagAt(i);
                     String id = tag.getString("identifier");
                     AffixFilter filter = AffixFilterManager.getAffixFilter(id);
                     if (filter != null && filter.isEnabled()) {
                        affixes.put(tag.getString("identifier"), tag.getCompoundTag("data"));
                     }
                  }

                  instance.setAffixData(affixes);
                  instance.setName(compound.getString("name"));
               }
            }

         }
      }, Championship::new);
   }

   public static ICapabilityProvider createProvider(IChampionship championship) {
      return new CapabilityChampionship.Provider(championship, CHAMPION_CAP, DEFAULT_FACING);
   }

   @EventBusSubscriber(
      modid = "champions"
   )
   public static class EventHandler {
      public EventHandler() {
      }

      @SubscribeEvent
      public static void attachCapabilities(AttachCapabilitiesEvent<Entity> evt) {
         Entity entity = (Entity)evt.getObject();
         if (ChampionHelper.isValidChampion(entity) && ChampionHelper.isValidDimension(entity.dimension)) {
            evt.addCapability(CapabilityChampionship.ID, CapabilityChampionship.createProvider(new Championship()));
         }

      }

      @SubscribeEvent
      public static void entityJoin(EntityJoinWorldEvent evt) {
         Entity entity = evt.getEntity();
         if (!entity.world.isRemote && ChampionHelper.isValidChampion(entity)) {
            EntityLiving living = (EntityLiving)entity;
            IChampionship chp = CapabilityChampionship.getChampionship(living);
            if (chp != null && chp.getRank() == null) {
               Rank rank = ChampionHelper.generateRank(living);
               chp.setRank(rank);
               if (rank.getTier() > 0) {
                  Set<String> affixes = ChampionHelper.generateAffixes(rank, living);
                  chp.setAffixes(affixes);
                  chp.setName(ChampionHelper.generateRandomName());
                  chp.getRank().applyGrowth(living);
                  UnmodifiableIterator var6 = chp.getAffixes().iterator();

                  while(var6.hasNext()) {
                     String s = (String)var6.next();
                     IAffix affix = AffixRegistry.getAffix(s);
                     if (affix != null) {
                        affix.onInitialSpawn(living, chp);
                     }
                  }
               }
            }
         }

      }

      @SubscribeEvent
      public static void entitySpawn(SpecialSpawn evt) {
         Entity entity = evt.getEntity();
         if (!entity.world.isRemote && ChampionHelper.isValidChampion(entity)) {
            EntityLiving living = (EntityLiving)entity;
            IChampionship chp = CapabilityChampionship.getChampionship(living);
            if (chp != null) {
               if (evt.getSpawner() != null && !ConfigHandler.championSpawners) {
                  chp.setRank(RankManager.getEmptyRank());
                  return;
               }

               if (Champions.isGameStagesLoaded && !ChampionStages.canSpawn(living)) {
                  chp.setRank(RankManager.getEmptyRank());
                  return;
               }

               if (chp.getRank() == null) {
                  Rank rank = ChampionHelper.generateRank(living);
                  chp.setRank(rank);
                  if (rank.getTier() > 0) {
                     Set<String> affixes = ChampionHelper.generateAffixes(rank, living);
                     chp.setAffixes(affixes);
                     chp.setName(ChampionHelper.generateRandomName());
                     chp.getRank().applyGrowth(living);
                     UnmodifiableIterator var6 = chp.getAffixes().iterator();

                     while(var6.hasNext()) {
                        String s = (String)var6.next();
                        IAffix affix = AffixRegistry.getAffix(s);
                        if (affix != null) {
                           affix.onInitialSpawn(living, chp);
                        }
                     }
                  }
               }
            }
         }

      }

      @SubscribeEvent
      public static void startTracking(StartTracking evt) {
         Entity entity = evt.getTarget();
         if (evt.getEntityPlayer() instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP)evt.getEntityPlayer();
            if (ChampionHelper.isValidChampion(entity)) {
               IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entity);
               if (chp != null && ChampionHelper.isElite(chp.getRank())) {
                  NetworkHandler.INSTANCE.sendTo(new PacketSyncAffix(entity.getEntityId(), chp.getRank().getTier(), chp.getAffixData(), chp.getName()), playerMP);
               }
            }
         }

      }
   }

   public static class Provider implements ICapabilitySerializable<NBTBase> {
      final Capability<IChampionship> capability;
      final EnumFacing facing;
      final IChampionship instance;

      Provider(IChampionship instance, Capability<IChampionship> capability, @Nullable EnumFacing facing) {
         this.instance = instance;
         this.capability = capability;
         this.facing = facing;
      }

      public boolean hasCapability(@Nullable Capability<?> capability, EnumFacing facing) {
         return capability == this.getCapability();
      }

      public <T> T getCapability(@Nullable Capability<T> capability, EnumFacing facing) {
         return (T)(capability == this.getCapability() ? this.getCapability().cast(this.instance) : null);
      }

      final Capability<IChampionship> getCapability() {
         return this.capability;
      }

      EnumFacing getFacing() {
         return this.facing;
      }

      final IChampionship getInstance() {
         return this.instance;
      }

      public NBTBase serializeNBT() {
         return (NBTBase)(this.getCapability() != null ? this.getCapability().writeNBT(this.getInstance(), this.getFacing()) : new NBTTagCompound());
      }

      public void deserializeNBT(NBTBase nbt) {
         if (this.getCapability() != null) {
            this.getCapability().readNBT(this.getInstance(), this.getFacing(), nbt);
         }

      }
   }
}
