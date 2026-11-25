package crafttweaker.mc1120.potions;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotion;
import crafttweaker.api.potions.IPotionEffect;
import crafttweaker.mc1120.item.MCItemStack;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class MCPotionEffect implements IPotionEffect {
   private final PotionEffect potionEffect;

   public MCPotionEffect(PotionEffect potionEffect) {
      this.potionEffect = potionEffect;
   }

   public int compareTo(IPotion other) {
      return this.potionEffect.compareTo((PotionEffect)other.getInternal());
   }

   public int getDuration() {
      return this.potionEffect.getDuration();
   }

   public IPotion getPotion() {
      return new MCPotion(this.potionEffect.getPotion());
   }

   public boolean doesShowParticles() {
      return this.potionEffect.doesShowParticles();
   }

   public int getAmplifier() {
      return this.potionEffect.getAmplifier();
   }

   public List<IItemStack> getCurativeItems() {
      return (List)this.potionEffect.getCurativeItems().stream().map(MCItemStack::new).collect(Collectors.toList());
   }

   public String getEffectName() {
      return this.potionEffect.getEffectName();
   }

   public boolean isAmbient() {
      return this.potionEffect.getIsAmbient();
   }

   public boolean isPotionDurationMax() {
      return this.potionEffect.getIsPotionDurationMax();
   }

   public boolean isCurativeItem(IItemStack item) {
      return this.potionEffect.isCurativeItem((ItemStack)item.getInternal());
   }

   public void setIsPotionDurationMax(boolean potionDurationMax) {
      this.potionEffect.setPotionDurationMax(potionDurationMax);
   }

   public void performEffect(IEntity entity) {
      if (entity.getInternal() instanceof EntityLivingBase) {
         this.potionEffect.performEffect((EntityLivingBase)entity.getInternal());
      }

   }

   public void combine(IPotionEffect other) {
      this.potionEffect.combine(CraftTweakerMC.getPotionEffect(other));
   }

   public Object getInternal() {
      return this.potionEffect;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCPotionEffect that = (MCPotionEffect)o;
         return Objects.equals(this.potionEffect, that.potionEffect);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.potionEffect});
   }
}
