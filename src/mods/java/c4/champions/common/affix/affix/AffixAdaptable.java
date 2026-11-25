package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.core.AffixNBT;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class AffixAdaptable extends AffixBase {
   public AffixAdaptable() {
      super("adaptable", AffixCategory.DEFENSE);
   }

   public float onHurt(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {
      String type = source.getDamageType();
      AffixAdaptable.DamageType damageType = (AffixAdaptable.DamageType)AffixNBT.getData(cap, this.getIdentifier(), AffixAdaptable.DamageType.class);
      if (damageType.name.equalsIgnoreCase(type)) {
         newAmount = (float)((double)newAmount - (double)amount * ConfigHandler.affix.adaptable.damageReductionIncrement * (double)damageType.count);
         ++damageType.count;
      } else {
         damageType.name = type;
         damageType.count = 0;
      }

      damageType.saveData(entity);
      return Math.max(amount * (float)(1.0D - ConfigHandler.affix.adaptable.maxDamageReduction), newAmount);
   }

   public static class DamageType extends AffixNBT {
      String name;
      int count;

      public DamageType() {
      }

      public void readFromNBT(NBTTagCompound tag) {
         this.name = tag.getString("name");
         this.count = tag.getInteger("count");
      }

      public NBTTagCompound writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setString("name", this.name);
         compound.setInteger("count", this.count);
         return compound;
      }
   }
}
