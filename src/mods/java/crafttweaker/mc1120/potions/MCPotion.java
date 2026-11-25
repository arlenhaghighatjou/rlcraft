package crafttweaker.mc1120.potions;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.potions.IPotion;
import crafttweaker.api.potions.IPotionEffect;
import crafttweaker.mc1120.item.MCItemStack;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class MCPotion implements IPotion {
   private final Potion potion;

   public MCPotion(Potion potion) {
      this.potion = potion;
   }

   public String name() {
      return this.potion.getName();
   }

   public boolean isBadEffect() {
      return this.potion.isBadEffect();
   }

   public int getLiquidColor() {
      return this.potion.getLiquidColor();
   }

   public int getLiquidColour() {
      return this.potion.getLiquidColor();
   }

   public IPotionEffect makePotionEffect(int duration, int amplifier) {
      return new MCPotionEffect(new PotionEffect(this.potion, duration, amplifier));
   }

   public IPotionEffect makePotionEffect(int duration, int amplifier, boolean ambient, boolean showParticles) {
      return new MCPotionEffect(new PotionEffect(this.potion, duration, amplifier, ambient, showParticles));
   }

   public List<IItemStack> getCurativeItems() {
      return (List)this.potion.getCurativeItems().stream().map(MCItemStack::new).collect(Collectors.toList());
   }

   public boolean hasStatusIcon() {
      return this.potion.hasStatusIcon();
   }

   public boolean isBeneficial() {
      return this.potion.isBeneficial();
   }

   public void setBeneficial() {
      this.potion.setBeneficial();
   }

   public boolean isInstant() {
      return this.potion.isInstant();
   }

   public Potion getInternal() {
      return this.potion;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCPotion mcPotion = (MCPotion)o;
         return Objects.equals(this.potion, mcPotion.potion);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.potion});
   }
}
