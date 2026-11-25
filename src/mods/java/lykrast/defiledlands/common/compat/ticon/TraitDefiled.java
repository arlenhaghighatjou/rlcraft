package lykrast.defiledlands.common.compat.ticon;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lykrast.defiledlands.common.entity.IEntityDefiled;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitDefiled extends AbstractTrait {
   private static final float DAMAGE = 4.0F;

   public TraitDefiled() {
      super("defiled", 9194151);
   }

   public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
      return target instanceof IEntityDefiled ? super.damage(tool, player, target, damage, newDamage, isCritical) : super.damage(tool, player, target, damage, newDamage + 4.0F, isCritical);
   }

   public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
      String loc = String.format("modifier.%s.extra", this.getModifierIdentifier());
      return ImmutableList.of(Util.translateFormatted(loc, new Object[]{Util.df.format(4.0D)}));
   }
}
