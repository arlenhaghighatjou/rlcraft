package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ChargeItem extends ItemBase {
   public static int CHARGE_EXPERIENCE = 50;
   public ProjectileInfo projectileInfo;

   public ChargeItem(ProjectileInfo projectileInfo) {
      this.projectileInfo = projectileInfo;
      this.modInfo = LycanitesMobs.modInfo;
      if (this.projectileInfo != null) {
         this.itemName = projectileInfo.chargeItemName;
         LycanitesMobs.logDebug("Projectile", "Created Charge Item: " + projectileInfo.chargeItemName);
         this.setup();
      }

   }

   public void setup() {
      this.setRegistryName(this.modInfo.modid, this.itemName);
      this.setUnlocalizedName(this.itemName);
      this.setCreativeTab(LycanitesMobs.chargesTab);
   }

   public String getItemStackDisplayName(ItemStack stack) {
      return this.getProjectileName() + " " + LanguageManager.translate("item.lycanitesmobs.charge");
   }

   public void addInformation(ItemStack itemStack, World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
      super.addInformation(itemStack, world, tooltip, tooltipFlag);
      FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

      for(String description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
         for(Object formattedDescription : fontRenderer.listFormattedStringToWidth("-------------------\n" + description, DESCRIPTION_WIDTH)) {
            if (formattedDescription instanceof String) {
               tooltip.add("§3" + formattedDescription);
            }
         }
      }

   }

   public String getDescription(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      return LanguageManager.translate("item.lycanitesmobs.charge.description");
   }

   public List<String> getAdditionalDescriptions(ItemStack itemStack, World world, ITooltipFlag tooltipFlag) {
      List<String> descriptions = new ArrayList();
      if (!this.getElements().isEmpty()) {
         String elements = LanguageManager.translate("item.lycanitesmobs.charge.elements") + " " + this.getElementNames();
         descriptions.add(elements);
      }

      String projectile = LanguageManager.translate("item.lycanitesmobs.charge.projectile") + " " + this.getProjectileName();
      descriptions.add(projectile);
      return descriptions;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      if (!world.isRemote && player.isSneaking()) {
         BaseProjectileEntity projectile = this.createProjectile(itemStack, world, player);
         if (projectile == null) {
            LycanitesMobs.logWarning("", "Failed to create projectile from Charge Item: " + this.itemName);
            return new ActionResult(EnumActionResult.FAIL, itemStack);
         }

         world.spawnEntity(projectile);
         if (!player.capabilities.isCreativeMode) {
            itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
         }

         this.playSound(world, player.getPosition(), projectile.getLaunchSound(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      }

      return new ActionResult(EnumActionResult.SUCCESS, itemStack);
   }

   public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
      return !(entity instanceof TameableCreatureEntity) || ((TameableCreatureEntity)entity).getPlayerOwner() != player;
   }

   public BaseProjectileEntity createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
      return this.projectileInfo == null ? null : this.projectileInfo.createProjectile(world, entityPlayer);
   }

   public List<ElementInfo> getElements() {
      return (List<ElementInfo>)(this.projectileInfo == null ? new ArrayList() : this.projectileInfo.elements);
   }

   public String getElementNames() {
      String elementNames = "";
      boolean firstElement = true;

      for(ElementInfo element : this.getElements()) {
         if (!firstElement) {
            elementNames = elementNames + ", ";
         }

         firstElement = false;
         elementNames = elementNames + element.getTitle();
      }

      return elementNames;
   }

   public String getProjectileName() {
      return this.projectileInfo != null ? this.projectileInfo.getTitle() : LanguageManager.translate("item.lycanitesmobs.charge");
   }
}
