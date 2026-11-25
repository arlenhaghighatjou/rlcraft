package org.orecruncher.dsurround.registry.item.compat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.dsurround.registry.item.ItemClass;
import org.orecruncher.dsurround.registry.item.SimpleArmorItemData;

@SideOnly(Side.CLIENT)
public class ConstructArmoryItemData extends SimpleArmorItemData {
   public ConstructArmoryItemData(@Nonnull ItemClass ic) {
      super(ic);
   }

   protected String getIdentifier(@Nonnull ItemStack stack) {
      NBTTagCompound nbt = stack.getTagCompound();
      if (nbt != null && nbt.hasKey("Modifiers")) {
         NBTTagList modifiers = nbt.getTagList("Modifiers", 10);
         if (modifiers != null && !modifiers.hasNoTags()) {
            NBTTagCompound piece = modifiers.getCompoundTagAt(0);
            if (piece != null && piece.hasKey("identifier")) {
               String id = piece.getString("identifier");
               if (!StringUtils.isEmpty(id)) {
                  return id;
               }
            }
         }
      }

      return null;
   }

   @Nullable
   public SoundEffect getEquipSound(@Nonnull ItemStack stack) {
      return super.getEquipSound(stack);
   }

   @Nullable
   public SoundEffect getSwingSound(@Nonnull ItemStack stack) {
      return super.getSwingSound(stack);
   }

   @Nullable
   public SoundEffect getUseSound(@Nonnull ItemStack stack) {
      return super.getUseSound(stack);
   }

   @Nullable
   public IAcoustic getArmorSound(@Nonnull ItemStack stack) {
      String id = this.getIdentifier(stack);
      if (id != null) {
         IAcoustic a = (IAcoustic)ConstructArmoryProducer.ARMOR.get(id);
         if (a != null) {
            return a;
         }
      }

      return super.getArmorSound(stack);
   }

   @Nullable
   public IAcoustic getFootArmorSound(@Nonnull ItemStack stack) {
      String id = this.getIdentifier(stack);
      if (id != null) {
         IAcoustic a = (IAcoustic)ConstructArmoryProducer.FOOT.get(id);
         if (a != null) {
            return a;
         }
      }

      return super.getFootArmorSound(stack);
   }
}
