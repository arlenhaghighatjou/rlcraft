package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.core.AffixNBT;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.rank.RankManager;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class AffixInfested extends AffixBase {
   public AffixInfested() {
      super("infested", AffixCategory.OFFENSE);
   }

   public void onInitialSpawn(EntityLiving entity, IChampionship cap) {
      AffixNBT.Integer buffer = (AffixNBT.Integer)AffixNBT.getData(cap, this.getIdentifier(), AffixNBT.Integer.class);
      buffer.num = Math.min(ConfigHandler.affix.infested.silverfishTotal, Math.max(1, (int)((double)entity.getMaxHealth() * ConfigHandler.affix.infested.silverfishPerHealth)));
      buffer.saveData(entity);
   }

   public void onSpawn(EntityLiving entity, IChampionship cap) {
      entity.tasks.addTask(0, new AffixInfested.AISpawnParasite(entity));
   }

   public float onHealed(EntityLiving entity, IChampionship cap, float amount, float newAmount) {
      if (newAmount > 0.0F && rand.nextFloat() < 0.5F) {
         AffixNBT.Integer buffer = (AffixNBT.Integer)AffixNBT.getData(cap, this.getIdentifier(), AffixNBT.Integer.class);
         buffer.num = Math.min(ConfigHandler.affix.infested.silverfishTotal, buffer.num + 2);
         buffer.saveData(entity);
         return 0.0F;
      } else {
         return newAmount;
      }
   }

   public void onDeath(EntityLiving entity, IChampionship cap, DamageSource source, LivingDeathEvent evt) {
      if (!entity.world.isRemote) {
         AffixNBT.Integer buffer = (AffixNBT.Integer)AffixNBT.getData(cap, this.getIdentifier(), AffixNBT.Integer.class);
         EntityLivingBase target = null;
         if (source.getTrueSource() instanceof EntityLivingBase) {
            target = (EntityLivingBase)source.getTrueSource();
         }

         boolean isEnder = false;
         if (entity instanceof EntityEnderman || entity instanceof EntityShulker || entity instanceof EntityEndermite || entity instanceof EntityDragon) {
            isEnder = true;
         }

         for(EntityLiving en : this.spawnParasites(entity.world, entity.getPosition(), buffer.num, isEnder)) {
            en.setRevengeTarget(target);
         }
      }

   }

   private List<EntityLiving> spawnParasites(World world, BlockPos pos, int amount, boolean isEnder) {
      List<EntityLiving> parasites = Lists.newArrayList();

      for(int i = 0; i < amount; ++i) {
         EntityLiving para = (EntityLiving)(isEnder ? new EntityEndermite(world) : new EntitySilverfish(world));
         para.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
         IChampionship chp = CapabilityChampionship.getChampionship(para);
         if (chp != null) {
            chp.setRank(RankManager.getEmptyRank());
         }

         world.spawnEntity(para);
         para.spawnExplosionParticle();
         parasites.add(para);
      }

      return parasites;
   }

   public boolean canApply(EntityLiving entity) {
      return !(entity instanceof EntitySilverfish) && !(entity instanceof EntityEndermite);
   }

   class AISpawnParasite extends EntityAIBase {
      private final EntityLiving entity;
      private int attackTime;

      public AISpawnParasite(EntityLiving entityLiving) {
         this.entity = entityLiving;
      }

      public boolean shouldExecute() {
         EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
         return AffixBase.isValidAffixTarget(this.entity, entitylivingbase, true) && entitylivingbase.world.getDifficulty() != EnumDifficulty.PEACEFUL;
      }

      public void startExecuting() {
         this.attackTime = ConfigHandler.affix.infested.silverfishInterval;
      }

      public void updateTask() {
         if (this.entity.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
            --this.attackTime;
            EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
            IChampionship chp = CapabilityChampionship.getChampionship(this.entity);
            if (entitylivingbase != null && chp != null) {
               this.entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 180.0F, 180.0F);
               AffixNBT.Integer buffer = (AffixNBT.Integer)AffixNBT.getData(chp, AffixInfested.this.getIdentifier(), AffixNBT.Integer.class);
               if (this.attackTime <= 0 && buffer.num > 0) {
                  this.attackTime = ConfigHandler.affix.desecrator.attackInterval + this.entity.getRNG().nextInt(5) * 10;
                  boolean isEnder = false;
                  if (this.entity instanceof EntityEnderman || this.entity instanceof EntityShulker || this.entity instanceof EntityEndermite || this.entity instanceof EntityDragon) {
                     isEnder = true;
                  }

                  List<EntityLiving> parasites = AffixInfested.this.spawnParasites(this.entity.world, this.entity.getPosition(), ConfigHandler.affix.infested.silverfishAmount, isEnder);

                  for(EntityLiving en : parasites) {
                     en.setAttackTarget(entitylivingbase);
                  }

                  buffer.num = Math.max(0, buffer.num - parasites.size());
                  buffer.saveData(this.entity);
               }
            }

            super.updateTask();
         }

      }
   }
}
