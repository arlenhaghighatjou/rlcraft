package c4.champions.common.affix.affix;

import c4.champions.Champions;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import java.lang.reflect.Field;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class AffixMolten extends AffixBase {
   private static final Field IMMUNE_TO_FIRE = ReflectionHelper.findField(Entity.class, "isImmuneToFire", "isImmuneToFire");

   public AffixMolten() {
      super("molten", AffixCategory.OFFENSE);
   }

   public void onSpawn(EntityLiving entity, IChampionship cap) {
      entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 40, 0, true, false));
      entity.setPathPriority(PathNodeType.WATER, -1.0F);
      entity.setPathPriority(PathNodeType.LAVA, 8.0F);
      entity.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
      entity.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
      Iterator<EntityAITaskEntry> iterator = entity.tasks.taskEntries.iterator();

      while(iterator.hasNext()) {
         EntityAITaskEntry entityaitasks$entityaitaskentry = (EntityAITaskEntry)iterator.next();
         EntityAIBase entityaibase = entityaitasks$entityaitaskentry.action;
         if (entityaibase instanceof EntityAIFleeSun || entityaibase instanceof EntityAIRestrictSun) {
            iterator.remove();
         }
      }

      if (entity.getNavigator() instanceof PathNavigateGround) {
         ((PathNavigateGround)entity.getNavigator()).setAvoidSun(false);
      }

   }

   public void onUpdate(EntityLiving entity, IChampionship cap) {
      if (!entity.world.isRemote) {
         if (entity.ticksExisted % 20 == 0) {
            entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 40, 0, true, false));
         }

         if (!ConfigHandler.affix.molten.waterResistant && entity.isWet()) {
            entity.attackEntityFrom(DamageSource.DROWN, 1.0F);
         }
      }

      if (!entity.isImmuneToFire()) {
         try {
            IMMUNE_TO_FIRE.setBoolean(entity, true);
         } catch (IllegalAccessException var4) {
            Champions.logger.log(Level.ERROR, "Error setting fire immunity for " + entity.toString());
         }
      }

   }

   public void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float amount, LivingAttackEvent evt) {
      target.setFire(10);
      source.setMagicDamage();
   }
}
