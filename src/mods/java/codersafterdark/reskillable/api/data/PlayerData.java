package codersafterdark.reskillable.api.data;

import codersafterdark.reskillable.api.ReskillableAPI;
import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementCache;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.api.unlockable.Ability;
import codersafterdark.reskillable.api.unlockable.IAbilityEventHandler;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class PlayerData {
   private static final String TAG_SKILLS_CMP = "SkillLevels";
   private final boolean client;
   private final RequirementCache requirementCache;
   public WeakReference<EntityPlayer> playerWR;
   private Map<Skill, PlayerSkillInfo> skillInfo = new HashMap();

   public PlayerData(EntityPlayer player) {
      this.playerWR = new WeakReference(player);
      this.client = player.getEntityWorld().isRemote;
      this.requirementCache = RequirementCache.getCache(player);
      ReskillableRegistries.SKILLS.getValuesCollection().forEach((s) -> {
         PlayerSkillInfo var10000 = (PlayerSkillInfo)this.skillInfo.put(s, new PlayerSkillInfo(s));
      });
      this.load();
   }

   public PlayerSkillInfo getSkillInfo(Skill s) {
      return (PlayerSkillInfo)this.skillInfo.get(s);
   }

   public Collection<PlayerSkillInfo> getAllSkillInfo() {
      return this.skillInfo.values();
   }

   public boolean hasAnyAbilities() {
      return !this.getAllAbilities().isEmpty();
   }

   public Set<Ability> getAllAbilities() {
      Set<Ability> set = new TreeSet();
      this.skillInfo.values().forEach((info) -> {
         info.addAbilities(set);
      });
      return set;
   }

   public boolean matchStats(RequirementHolder holder) {
      return this.playerWR.get() == null || holder.getRequirements().stream().allMatch(this::requirementAchieved);
   }

   public boolean requirementAchieved(Requirement requirement) {
      return this.getRequirementCache().requirementAchieved(requirement);
   }

   public final RequirementCache getRequirementCache() {
      return this.requirementCache;
   }

   public void load() {
      if (!this.client) {
         EntityPlayer player = (EntityPlayer)this.playerWR.get();
         if (player != null) {
            NBTTagCompound cmp = PlayerDataHandler.getDataCompoundForPlayer(player);
            this.loadFromNBT(cmp);
         }
      }

   }

   public void save() {
      if (!this.client) {
         EntityPlayer player = (EntityPlayer)this.playerWR.get();
         if (player != null) {
            NBTTagCompound cmp = PlayerDataHandler.getDataCompoundForPlayer(player);
            this.saveToNBT(cmp);
         }
      }

   }

   public void sync() {
      if (!this.client) {
         EntityPlayer player = (EntityPlayer)this.playerWR.get();
         ReskillableAPI.getInstance().syncPlayerData(player, this);
      }

   }

   public void saveAndSync() {
      this.save();
      this.sync();
   }

   public void loadFromNBT(NBTTagCompound cmp) {
      NBTTagCompound skillsCmp = cmp.getCompoundTag("SkillLevels");

      for(PlayerSkillInfo info : this.skillInfo.values()) {
         String key = info.skill.getKey();
         if (skillsCmp.hasKey(key)) {
            NBTTagCompound infoCmp = skillsCmp.getCompoundTag(key);
            info.loadFromNBT(infoCmp);
         }
      }

   }

   public void saveToNBT(NBTTagCompound cmp) {
      NBTTagCompound skillsCmp = new NBTTagCompound();

      for(PlayerSkillInfo info : this.skillInfo.values()) {
         String key = info.skill.getKey();
         NBTTagCompound infoCmp = new NBTTagCompound();
         info.saveToNBT(infoCmp);
         skillsCmp.setTag(key, infoCmp);
      }

      cmp.setTag("SkillLevels", skillsCmp);
   }

   public void tickPlayer(PlayerTickEvent event) {
      this.forEachEventHandler((h) -> {
         h.onPlayerTick(event);
      });
   }

   public void blockDrops(HarvestDropsEvent event) {
      this.forEachEventHandler((h) -> {
         h.onBlockDrops(event);
      });
   }

   public void mobDrops(LivingDropsEvent event) {
      this.forEachEventHandler((h) -> {
         h.onMobDrops(event);
      });
   }

   public void breakSpeed(BreakSpeed event) {
      this.forEachEventHandler((h) -> {
         h.getBreakSpeed(event);
      });
   }

   public void attackMob(LivingHurtEvent event) {
      this.forEachEventHandler((h) -> {
         h.onAttackMob(event);
      });
   }

   public void hurt(LivingHurtEvent event) {
      this.forEachEventHandler((h) -> {
         h.onHurt(event);
      });
   }

   public void rightClickBlock(RightClickBlock event) {
      this.forEachEventHandler((h) -> {
         h.onRightClickBlock(event);
      });
   }

   public void enderTeleport(EnderTeleportEvent event) {
      this.forEachEventHandler((h) -> {
         h.onEnderTeleport(event);
      });
   }

   public void killMob(LivingDeathEvent event) {
      this.forEachEventHandler((h) -> {
         h.onKillMob(event);
      });
   }

   public void forEachEventHandler(Consumer<IAbilityEventHandler> consumer) {
      this.skillInfo.values().forEach((info) -> {
         info.forEachEventHandler(consumer);
      });
   }
}
