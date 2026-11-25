package vazkii.quark.world.item;

import javax.annotation.Nonnull;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.item.ItemModArmor;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.world.client.model.ModelPirateHat;

public class ItemPirateHat extends ItemModArmor implements IQuarkItem {
   @SideOnly(Side.CLIENT)
   public static ModelBiped headModel;

   public ItemPirateHat() {
      super("pirate_hat", ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.HEAD);
      this.setMaxDamage(-1);
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
      if (headModel == null) {
         headModel = new ModelPirateHat();
      }

      return headModel;
   }

   public boolean hasColor(@Nonnull ItemStack stack) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      return "quark:textures/entity/pirate_hat.png";
   }

   @Nonnull
   public IRarity getForgeRarity(@Nonnull ItemStack stack) {
      return EnumRarity.RARE;
   }
}
