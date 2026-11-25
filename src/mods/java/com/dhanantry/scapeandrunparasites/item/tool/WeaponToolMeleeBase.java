package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.collect.Multimap;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WeaponToolMeleeBase extends ItemSword implements IHaveReach {
   private float addReach;
   private float attackDamage;
   private byte effectHit;
   protected double attackSpeed;
   protected boolean calling;
   protected byte idTool;

   public WeaponToolMeleeBase(ToolMaterial material, String name, double attackspeed, float range, float attackD, boolean fear, byte id) {
      super(material);
      this.setRegistryName("weapon_" + name);
      this.setUnlocalizedName("srparasites.weapon_" + name);
      this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
      this.attackSpeed = attackspeed;
      this.addReach = range;
      this.attackDamage = attackD;
      this.calling = fear;
      this.idTool = id;
      this.addPropertyOverride(new ResourceLocation("vinni"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            return SRPConfig.useVinniT ? 1.0F : 0.0F;
         }
      });
   }

   public float getReach() {
      return this.addReach;
   }

   public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);
      if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
         multimap.clear();
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.attackDamage, 0));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed, 0));
      }

      return multimap;
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      return super.hitEntity(stack, target, attacker);
   }

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
      if (!worldIn.isRemote && this.calling && SRPConfigSystems.useOneMind && worldIn.rand.nextInt(10) == 0 && entityIn.ticksExisted % 40 == 0) {
         ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(SRPPotions.PREY_E, 1200, 0, false, false));
      }

   }

   public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      super.onCreated(stack, worldIn, playerIn);
   }

   public EnumRarity getRarity(ItemStack stack) {
      return super.getRarity(stack);
   }
}
