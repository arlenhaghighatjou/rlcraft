package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WeaponToolArmorBase extends ItemArmor {
   private byte effectHit;
   protected boolean calling;
   protected byte idTool;

   public WeaponToolArmorBase(ArmorMaterial material, String name, int renderI, boolean fear, byte id, EntityEquipmentSlot eq) {
      super(material, renderI, eq);
      this.setRegistryName("armor_" + name);
      this.setUnlocalizedName("srparasites.armor_" + name);
      this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
      this.calling = fear;
      this.idTool = id;
      this.addPropertyOverride(new ResourceLocation("vinni"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            return SRPConfig.useVinniT ? 1.0F : 0.0F;
         }
      });
   }

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
      if (!worldIn.isRemote && this.calling && SRPConfigSystems.useOneMind && worldIn.rand.nextInt(10) == 0 && entityIn.ticksExisted % 40 == 0) {
         ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(SRPPotions.PREY_E, 1200, 0, false, false));
      }

   }

   public boolean canCall() {
      return this.calling;
   }

   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
      return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
   }

   public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      super.onCreated(stack, worldIn, playerIn);
   }

   public EnumRarity getRarity(ItemStack stack) {
      return super.getRarity(stack);
   }

   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      ArrayList<String> resistanceS = new ArrayList();
      ArrayList<Integer> resistanceI = new ArrayList();
      NBTTagCompound compound = stack.getTagCompound();
      if (compound == null) {
         compound = new NBTTagCompound();
      }

      if (compound.hasKey("sprresistances")) {
         NBTTagList allResS = compound.getTagList("sprresistances", 10);
         NBTTagList allResI = compound.getTagList("sprresistancei", 10);
         if (allResS.tagCount() != allResI.tagCount()) {
            return;
         }

         for(int i = 0; i < allResS.tagCount(); ++i) {
            NBTTagCompound resT = allResS.getCompoundTagAt(i);
            String res = resT.getString("resistance" + i);
            resistanceS.add(i, res);
            NBTTagCompound resU = allResI.getCompoundTagAt(i);
            int resi = resU.getInteger("resistance" + i);
            resistanceI.add(i, Integer.valueOf(resi));
         }

         tooltip.add(TextFormatting.DARK_PURPLE + "Current Adaptation:");

         for(int i = 0; i < resistanceS.size(); ++i) {
            double reduc = (double)Math.min(((Integer)resistanceI.get(i)).intValue(), this.calling ? SRPConfig.sentientPointCap : SRPConfig.livingPointCap);
            reduc = reduc * (this.calling ? (double)SRPConfig.sentientPointReduction : (double)SRPConfig.livingPointReduction);
            DecimalFormat decimalFormat = new DecimalFormat("##.##");
            decimalFormat.setRoundingMode(RoundingMode.DOWN);
            String formatResult = decimalFormat.format(reduc * 100.0D);
            tooltip.add(TextFormatting.YELLOW + "-> " + (String)resistanceS.get(i));
            tooltip.add(TextFormatting.YELLOW + " reduction: " + formatResult + "%");
         }
      }

   }
}
