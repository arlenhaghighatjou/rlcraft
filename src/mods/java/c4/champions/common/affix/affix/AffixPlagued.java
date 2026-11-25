package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.init.ChampionsRegistry;
import c4.champions.common.potion.PotionPlague;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class AffixPlagued extends AffixBase {
   public AffixPlagued() {
      super("plagued", AffixCategory.OFFENSE);
   }

   public void onUpdate(EntityLiving entity, IChampionship cap) {
      if (!entity.world.isRemote) {
         for(Entity entity1 : entity.world.getEntitiesWithinAABBExcludingEntity(entity, entity.getEntityBoundingBox().grow((double)ConfigHandler.affix.plagued.infectRange))) {
            if (entity1 instanceof EntityLivingBase) {
               EntityLivingBase target = (EntityLivingBase)entity1;
               if (canEntityBeInfected(entity, target)) {
                  target.addPotionEffect(new PotionEffect(ChampionsRegistry.plague, ConfigHandler.affix.plagued.duration));
               }
            }
         }

         if (entity.isPotionActive(PotionPlague.getInfectionPotion())) {
            entity.removePotionEffect(PotionPlague.getInfectionPotion());
         }
      }

   }

   public void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float amount, LivingAttackEvent evt) {
      target.addPotionEffect(new PotionEffect(ChampionsRegistry.plague, ConfigHandler.affix.plagued.duration));
   }

   public static boolean canEntityBeInfected(EntityLivingBase host, EntityLivingBase target) {
      return !(target instanceof EntityArmorStand) && host.world.rayTraceBlocks(new Vec3d(host.posX, host.posY + (double)(host.height / 2.0F), host.posZ), new Vec3d(target.posX, target.posY + (double)(target.height / 2.0F), target.posZ), true, true, false) == null;
   }
}
