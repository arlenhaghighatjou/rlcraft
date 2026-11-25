package org.orecruncher.dsurround.client.footsteps.accents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.footsteps.IFootstepAccentProvider;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.dsurround.registry.item.IArmorItemData;
import org.orecruncher.dsurround.registry.item.IItemData;
import org.orecruncher.dsurround.registry.item.ItemClass;
import org.orecruncher.dsurround.registry.item.ItemUtils;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public class ArmorAccents implements IFootstepAccentProvider {
   public ArmorAccents() {
   }

   @Nonnull
   public String getName() {
      return "Armor Accents";
   }

   @Nullable
   protected IAcoustic resolveArmor(@Nonnull ItemStack stack) {
      IItemData id = ItemUtils.getItemData(stack.getItem());
      return id instanceof IArmorItemData ? ((IArmorItemData)id).getArmorSound(stack) : null;
   }

   protected IAcoustic resolveFootArmor(@Nonnull ItemStack stack) {
      IItemData id = ItemUtils.getItemData(stack.getItem());
      return id instanceof IArmorItemData ? ((IArmorItemData)id).getFootArmorSound(stack) : null;
   }

   public void provide(@Nonnull EntityLivingBase entity, @Nullable BlockPos pos, @Nonnull ObjectArray<IAcoustic> in) {
      if (ModOptions.sound.enableArmorSounds) {
         ItemStack armor;
         ItemStack foot;
         if (EnvironStateHandler.EnvironState.isPlayer(entity)) {
            armor = EnvironStateHandler.EnvironState.getPlayerItemStack();
            foot = EnvironStateHandler.EnvironState.getPlayerFootArmorStack();
         } else {
            armor = ItemClass.effectiveArmorStack(entity);
            foot = ItemClass.footArmorStack(entity);
         }

         IAcoustic armorAddon = this.resolveArmor(armor);
         IAcoustic footAddon = this.resolveFootArmor(foot);
         if (armorAddon != null) {
            in.add(armorAddon);
            if (armorAddon == footAddon) {
               footAddon = null;
            }
         }

         if (footAddon != null) {
            in.add(footAddon);
         }

      }
   }
}
