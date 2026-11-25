package com.lycanitesmobs.core.item.equipment;

import com.google.common.collect.Multimap;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.features.DamageEquipmentFeature;
import com.lycanitesmobs.core.item.equipment.features.EffectEquipmentFeature;
import com.lycanitesmobs.core.item.equipment.features.EquipmentFeature;
import com.lycanitesmobs.core.item.equipment.features.HarvestEquipmentFeature;
import com.lycanitesmobs.core.item.equipment.features.ProjectileEquipmentFeature;
import com.lycanitesmobs.core.item.equipment.features.SummonEquipmentFeature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEquipment extends ItemBase {
   public static ItemStack ITEMSTACK_TO_RENDER;
   public static int PART_LIMIT = 20;
   public static int SHARPNESS_MAX = 1500;
   public static int MANA_MAX = 1500;

   public ItemEquipment() {
      this.itemName = "equipment";
      this.modInfo = LycanitesMobs.modInfo;
      this.setRegistryName(this.modInfo.modid, this.itemName);
      this.setUnlocalizedName(this.itemName);
      this.setMaxStackSize(1);
   }

   public void addInformation(ItemStack itemStack, World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
      super.addInformation(itemStack, world, tooltip, tooltipFlag);
      FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

      for(String description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
         for(Object formattedDescription : fontRenderer.listFormattedStringToWidth(description, DESCRIPTION_WIDTH + 100)) {
            if (formattedDescription instanceof String) {
               tooltip.add("§3" + formattedDescription);
            }
         }
      }

   }

   public String getDescription(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      return LanguageManager.translate("item.equipment.description");
   }

   public List<String> getAdditionalDescriptions(ItemStack itemStack, World world, ITooltipFlag tooltipFlag) {
      List<String> descriptions = new ArrayList();
      descriptions.add(LanguageManager.translate("equipment.sharpness") + " " + this.getSharpness(itemStack) + "/" + SHARPNESS_MAX);
      descriptions.add(LanguageManager.translate("equipment.mana") + " " + this.getMana(itemStack) + "/" + MANA_MAX);
      descriptions.add("-------------------");

      for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(itemStack)) {
         ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
         if (equipmentPart != null) {
            int partLevel = equipmentPart.getLevel(equipmentPartStack);
            descriptions.add(equipmentPart.getItemStackDisplayName(itemStack) + " " + LanguageManager.translate("entity.level") + " " + partLevel);
         }
      }

      descriptions.add("-------------------\n");
      String damageDescription = LanguageManager.translate("equipment.feature.damage") + " " + String.format("%.0f", this.getDamageAmount(itemStack) + 1.0D);
      damageDescription = damageDescription + "\n" + LanguageManager.translate("equipment.feature.damage.cooldown") + " " + String.format("%.1f", this.getDamageCooldown(itemStack));
      damageDescription = damageDescription + "\n" + LanguageManager.translate("equipment.feature.damage.knockback") + " " + String.format("%.0f", this.getDamageKnockback(itemStack));
      damageDescription = damageDescription + "\n" + LanguageManager.translate("equipment.feature.damage.range") + " " + String.format("%.1f", this.getDamageRange(itemStack));
      damageDescription = damageDescription + "\n" + LanguageManager.translate("equipment.feature.damage.sweep") + " " + String.format("%.0f", Math.min(this.getDamageSweep(itemStack), 360.0D));
      descriptions.add(damageDescription);
      String harvestSummaries = this.getFeatureSummaries(itemStack, "harvest");
      String effectSummaries = this.getFeatureSummaries(itemStack, "effect");
      String projectileSummaries = this.getFeatureSummaries(itemStack, "projectile");
      String summonSummaries = this.getFeatureSummaries(itemStack, "summon");
      if (!"".equals(harvestSummaries) || !"".equals(effectSummaries) || !"".equals(projectileSummaries) || !"".equals(summonSummaries)) {
         descriptions.add("-------------------\n");
         if (!"".equals(harvestSummaries)) {
            descriptions.add(LanguageManager.translate("equipment.feature.harvest") + " " + harvestSummaries);
         }

         if (!"".equals(effectSummaries)) {
            descriptions.add(LanguageManager.translate("equipment.feature.effect") + " " + effectSummaries);
         }

         if (!"".equals(projectileSummaries)) {
            descriptions.add(LanguageManager.translate("equipment.feature.projectile") + " " + projectileSummaries);
         }

         if (!"".equals(summonSummaries)) {
            descriptions.add(LanguageManager.translate("equipment.feature.summon") + " " + summonSummaries);
         }
      }

      return descriptions;
   }

   public String getFeatureSummaries(ItemStack itemStack, String featureType) {
      Map<EquipmentFeature, ItemStack> effectFeatures = this.getFeaturesByTypeWithPartStack(itemStack, featureType);
      String featureSummaries = "";
      boolean first = true;

      for(EquipmentFeature equipmentFeature : effectFeatures.keySet()) {
         if (!first) {
            featureSummaries = featureSummaries + ", ";
         }

         first = false;
         String featureSummary = equipmentFeature.getSummary((ItemStack)effectFeatures.get(equipmentFeature), this.getPartLevel((ItemStack)effectFeatures.get(equipmentFeature)));
         if (featureSummary != null) {
            featureSummaries = featureSummaries + featureSummary;
         }
      }

      return featureSummaries;
   }

   public int getMetadata(ItemStack stack) {
      ITEMSTACK_TO_RENDER = stack;
      return super.getMetadata(stack);
   }

   @SideOnly(Side.CLIENT)
   public FontRenderer getFontRenderer(ItemStack stack) {
      return LycanitesMobs.proxy.getFontRenderer();
   }

   public boolean showDurabilityBar(ItemStack itemStack) {
      return (double)this.getSharpness(itemStack) < (double)SHARPNESS_MAX;
   }

   public double getDurabilityForDisplay(ItemStack itemStack) {
      return 1.0D - (double)this.getSharpness(itemStack) / (double)SHARPNESS_MAX;
   }

   public NonNullList<ItemStack> getEquipmentPartStacks(ItemStack itemStack) {
      NonNullList<ItemStack> itemStacks = NonNullList.withSize(PART_LIMIT, ItemStack.EMPTY);
      NBTTagCompound nbt = this.getTagCompound(itemStack);
      if (nbt.hasKey("Items")) {
         ItemStackHelper.loadAllItems(nbt, itemStacks);
      }

      return itemStacks;
   }

   public int getEquipmentPartCount(ItemStack itemStack) {
      int partCount = 0;

      for(ItemStack partStack : this.getEquipmentPartStacks(itemStack)) {
         if (!partStack.isEmpty()) {
            ++partCount;
         }
      }

      return partCount;
   }

   public ItemEquipmentPart getEquipmentPart(ItemStack itemStack) {
      if (itemStack.isEmpty()) {
         return null;
      } else {
         return !(itemStack.getItem() instanceof ItemEquipmentPart) ? null : (ItemEquipmentPart)itemStack.getItem();
      }
   }

   public void addEquipmentPart(ItemStack equipmentStack, ItemStack equipmentPartStack, int slotIndex) {
      if (slotIndex < PART_LIMIT) {
         NonNullList<ItemStack> itemStacks = this.getEquipmentPartStacks(equipmentStack);
         itemStacks.set(slotIndex, equipmentPartStack);
         NBTTagCompound nbt = this.getTagCompound(equipmentStack);
         ItemStackHelper.saveAllItems(nbt, itemStacks);
         equipmentStack.setTagCompound(nbt);
      }
   }

   public List<EquipmentFeature> getFeaturesByType(ItemStack equipmentStack, String featureType) {
      List<EquipmentFeature> features = new ArrayList();

      for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
         ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
         if (equipmentPart != null) {
            for(EquipmentFeature feature : equipmentPart.features) {
               if (feature.isActive(equipmentPartStack, equipmentPart.getLevel(equipmentPartStack)) && feature.featureType.equalsIgnoreCase(featureType)) {
                  features.add(feature);
               }
            }
         }
      }

      return features;
   }

   public Map<EquipmentFeature, ItemStack> getFeaturesByTypeWithPartStack(ItemStack equipmentStack, String featureType) {
      Map<EquipmentFeature, ItemStack> features = new HashMap();

      for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
         ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
         if (equipmentPart != null) {
            for(EquipmentFeature feature : equipmentPart.features) {
               if (feature.isActive(equipmentPartStack, equipmentPart.getLevel(equipmentPartStack)) && feature.featureType.equalsIgnoreCase(featureType)) {
                  features.put(feature, equipmentPartStack);
               }
            }
         }
      }

      return features;
   }

   public void applyLevelCap(ItemStack equipmentStack, int levelCap) {
      for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
         ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
         if (equipmentPart != null) {
            equipmentPart.setLevel(equipmentPartStack, Math.min(levelCap, equipmentPart.getLevel(equipmentPartStack)));
         }
      }

   }

   public int getHighestLevel(ItemStack equipmentStack) {
      int highestLevel = 0;

      for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
         ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
         if (equipmentPart != null) {
            int partLevel = equipmentPart.getLevel(equipmentPartStack);
            if (partLevel > highestLevel) {
               highestLevel = partLevel;
            }
         }
      }

      return highestLevel;
   }

   public int getPartLevel(ItemStack partStack) {
      Item featureItem = partStack.getItem();
      if (!(featureItem instanceof ItemEquipmentPart)) {
         return 1;
      } else {
         ItemEquipmentPart featurePart = (ItemEquipmentPart)featureItem;
         return featurePart.getLevel(partStack);
      }
   }

   public int getSharpness(ItemStack equipmentStack) {
      int lowestSharpness = SHARPNESS_MAX;

      for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
         ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
         if (equipmentPart != null) {
            int partSharpness = equipmentPart.getSharpness(equipmentPartStack);
            if (partSharpness < lowestSharpness) {
               lowestSharpness = partSharpness;
            }
         }
      }

      return lowestSharpness;
   }

   public boolean addSharpness(ItemStack equipmentStack, int sharpness) {
      boolean sharpnessIncreased = false;
      NonNullList<ItemStack> itemStacks = this.getEquipmentPartStacks(equipmentStack);

      for(ItemStack equipmentPartStack : itemStacks) {
         ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
         if (equipmentPart != null) {
            sharpnessIncreased = equipmentPart.addSharpness(equipmentPartStack, sharpness) || sharpnessIncreased;
         }
      }

      NBTTagCompound nbt = this.getTagCompound(equipmentStack);
      ItemStackHelper.saveAllItems(nbt, itemStacks);
      equipmentStack.setTagCompound(nbt);
      return sharpnessIncreased;
   }

   public boolean removeSharpness(ItemStack equipmentStack, int sharpness) {
      boolean sharpnessDecreased = false;
      NonNullList<ItemStack> itemStacks = this.getEquipmentPartStacks(equipmentStack);

      for(ItemStack equipmentPartStack : itemStacks) {
         ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
         if (equipmentPart != null) {
            sharpnessDecreased = equipmentPart.removeSharpness(equipmentPartStack, sharpness) || sharpnessDecreased;
         }
      }

      NBTTagCompound nbt = this.getTagCompound(equipmentStack);
      ItemStackHelper.saveAllItems(nbt, itemStacks);
      equipmentStack.setTagCompound(nbt);
      return sharpnessDecreased;
   }

   public int getMana(ItemStack equipmentStack) {
      int lowestMana = MANA_MAX;

      for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
         ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
         if (equipmentPart != null) {
            int partMana = equipmentPart.getMana(equipmentPartStack);
            if (partMana < lowestMana) {
               lowestMana = partMana;
            }
         }
      }

      return lowestMana;
   }

   public boolean addMana(ItemStack equipmentStack, int mana) {
      boolean manaIncreased = false;
      NonNullList<ItemStack> itemStacks = this.getEquipmentPartStacks(equipmentStack);

      for(ItemStack equipmentPartStack : itemStacks) {
         ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
         if (equipmentPart != null) {
            manaIncreased = equipmentPart.addMana(equipmentPartStack, mana) || manaIncreased;
         }
      }

      NBTTagCompound nbt = this.getTagCompound(equipmentStack);
      ItemStackHelper.saveAllItems(nbt, itemStacks);
      equipmentStack.setTagCompound(nbt);
      return manaIncreased;
   }

   public boolean removeMana(ItemStack equipmentStack, int mana) {
      boolean manaDecreased = false;
      NonNullList<ItemStack> itemStacks = this.getEquipmentPartStacks(equipmentStack);

      for(ItemStack equipmentPartStack : itemStacks) {
         ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
         if (equipmentPart != null) {
            manaDecreased = equipmentPart.removeMana(equipmentPartStack, mana) || manaDecreased;
         }
      }

      NBTTagCompound nbt = this.getTagCompound(equipmentStack);
      ItemStackHelper.saveAllItems(nbt, itemStacks);
      equipmentStack.setTagCompound(nbt);
      return manaDecreased;
   }

   public EnumAction getItemUseAction(ItemStack itemStack) {
      return EnumAction.BOW;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 18000;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      if (this.getMana(itemStack) <= 0) {
         return new ActionResult(EnumActionResult.FAIL, itemStack);
      } else {
         boolean active = false;

         for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
            ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
            if (projectileFeature.onUseSecondary(world, player, hand)) {
               active = true;
            }
         }

         if (active) {
            player.setActiveHand(hand);
            return new ActionResult(EnumActionResult.SUCCESS, itemStack);
         } else {
            return new ActionResult(EnumActionResult.FAIL, itemStack);
         }
      }
   }

   public void onUsingTick(ItemStack itemStack, EntityLivingBase user, int count) {
      if (user.isHandActive() && this.getMana(itemStack) > 0) {
         boolean usedMana = false;

         for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
            ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
            usedMana = projectileFeature.onHoldSecondary(user, count) || usedMana;
         }

         if (usedMana) {
            this.removeMana(itemStack, 1);
         }

      }
   }

   public void onItemLeftClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      if (this.getMana(itemStack) > 0) {
         boolean usedMana = false;

         for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
            ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
            usedMana = projectileFeature.onUsePrimary(world, player, hand) || usedMana;
         }

         if (usedMana) {
            this.removeMana(itemStack, 1);
         }

      }
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack itemStack = player.getHeldItem(hand);
      if (this.getMana(itemStack) <= 0) {
         return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
      } else {
         boolean active = false;

         for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
            HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
            if (harvestFeature.onBlockUsed(world, player, pos, itemStack, facing)) {
               active = true;
            }
         }

         if (active) {
            player.setActiveHand(hand);
            this.removeSharpness(itemStack, 1);
            return EnumActionResult.SUCCESS;
         } else {
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
         }
      }
   }

   public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
      boolean entityInteraction = false;
      if (this.getMana(itemStack) <= 0) {
         return false;
      } else {
         for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
            HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
            if (harvestFeature.onEntityInteraction(player, target, itemStack)) {
               entityInteraction = true;
            }
         }

         if (entityInteraction) {
            this.removeSharpness(itemStack, 1);
            return true;
         } else {
            return false;
         }
      }
   }

   public Set<String> getToolClasses(ItemStack itemStack) {
      Map<String, Boolean> toolTypes = new HashMap();

      for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
         HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
         String toolType = harvestFeature.getToolType();
         if (toolType != null) {
            toolTypes.put(toolType, Boolean.valueOf(true));
         }
      }

      return toolTypes.keySet();
   }

   public int getHarvestLevel(ItemStack itemStack, String toolClass, EntityPlayer player, IBlockState blockState) {
      if (this.getSharpness(itemStack) <= 0) {
         return -1;
      } else {
         int harvestLevel = -1;

         for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
            HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
            if (harvestLevel < harvestFeature.harvestLevel && toolClass.equals(harvestFeature.getToolType())) {
               harvestLevel = harvestFeature.harvestLevel;
            }
         }

         return harvestLevel;
      }
   }

   public boolean canHarvestBlock(IBlockState blockState, ItemStack itemStack) {
      if (this.getSharpness(itemStack) <= 0) {
         return false;
      } else {
         for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
            HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
            if (harvestFeature.canHarvestBlock(blockState)) {
               return true;
            }
         }

         return false;
      }
   }

   public float getDestroySpeed(ItemStack itemStack, IBlockState blockState) {
      if (this.getSharpness(itemStack) <= 0) {
         return 1.0F;
      } else {
         float speed = 1.0F;

         for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
            HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
            speed += harvestFeature.getHarvestSpeed(blockState);
         }

         return speed;
      }
   }

   public boolean onBlockDestroyed(ItemStack itemStack, World worldIn, IBlockState blockState, BlockPos pos, EntityLivingBase entityLiving) {
      if (this.getSharpness(itemStack) <= 0) {
         return super.onBlockDestroyed(itemStack, worldIn, blockState, pos, entityLiving);
      } else if (worldIn.isRemote) {
         return super.onBlockDestroyed(itemStack, worldIn, blockState, pos, entityLiving);
      } else {
         for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
            HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
            harvestFeature.onBlockDestroyed(worldIn, blockState, pos, entityLiving);
         }

         this.removeSharpness(itemStack, 1);
         return super.onBlockDestroyed(itemStack, worldIn, blockState, pos, entityLiving);
      }
   }

   public boolean hitEntity(ItemStack itemStack, EntityLivingBase primaryTarget, EntityLivingBase attacker) {
      if (this.getSharpness(itemStack) <= 0) {
         return true;
      } else {
         ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(attacker);
         boolean attackOnCooldown = false;
         if (extendedEntity != null) {
            int currentCooldown = extendedEntity.getProjectileCooldown(1, "equipment_melee");
            int weaponCooldown = 9 * (int)this.getDamageCooldown(itemStack);
            attackOnCooldown = currentCooldown > 0;
            if (currentCooldown < weaponCooldown) {
               extendedEntity.setProjectileCooldown(1, "equipment_melee", weaponCooldown);
            }
         }

         List<EntityLivingBase> targets = new ArrayList();
         targets.add(primaryTarget);
         if (attacker != null && !attacker.getEntityWorld().isRemote && !attacker.isSneaking() && !attackOnCooldown) {
            double sweepAngle = this.getDamageSweep(itemStack) / 2.0D;
            if (sweepAngle > 0.0D) {
               float sweepDamage = (float)this.getDamageAmount(itemStack);
               double sweepRange = 1.0D + this.getDamageRange(itemStack);
               List<EntityLivingBase> possibleTargets = attacker.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, attacker.getEntityBoundingBox().grow(sweepRange, sweepRange, sweepRange));
               Iterator equipmentFeature = possibleTargets.iterator();

               label169:
               while(true) {
                  EntityLivingBase possibleTarget;
                  while(true) {
                     while(true) {
                        if (!equipmentFeature.hasNext()) {
                           break label169;
                        }

                        possibleTarget = (EntityLivingBase)equipmentFeature.next();
                        if (possibleTarget != attacker && possibleTarget != primaryTarget && possibleTarget.isEntityAlive() && !(possibleTarget instanceof EntityPlayer)) {
                           if (!(possibleTarget instanceof EntityTameable)) {
                              break;
                           }

                           EntityTameable possibleTameableTarget = (EntityTameable)possibleTarget;
                           if ((possibleTameableTarget.getOwner() == null || attacker.getEntityWorld().getMinecraftServer().isPVPEnabled()) && possibleTameableTarget.getOwner() != attacker) {
                              break;
                           }
                        }
                     }

                     if (!(possibleTarget instanceof TameableCreatureEntity)) {
                        break;
                     }

                     TameableCreatureEntity possibleTameableTarget = (TameableCreatureEntity)possibleTarget;
                     if ((possibleTameableTarget.getPlayerOwner() == null || attacker.getEntityWorld().getMinecraftServer().isPVPEnabled()) && possibleTameableTarget.getPlayerOwner() != attacker) {
                        break;
                     }
                  }

                  double targetXDist = possibleTarget.posX - attacker.posX;
                  double targetZDist = attacker.posZ - possibleTarget.posZ;
                  double targetAngleAbsolute = 180.0D + Math.toDegrees(Math.atan2(targetXDist, targetZDist));
                  double targetAngle = Math.abs(targetAngleAbsolute - (double)attacker.rotationYaw);
                  if (targetAngle > 180.0D) {
                     targetAngle = 180.0D - (targetAngle - 180.0D);
                  }

                  if (targetAngle <= sweepAngle) {
                     targets.add(possibleTarget);
                     DamageSource sweepSource = DamageSource.GENERIC;
                     if (attacker instanceof EntityPlayer) {
                        sweepSource = DamageSource.causePlayerDamage((EntityPlayer)attacker);
                     }

                     possibleTarget.attackEntityFrom(sweepSource, sweepDamage);
                  }
               }
            }
         }

         if (attacker instanceof EntityPlayer && targets.size() > 1) {
            EntityPlayer playerAttacker = (EntityPlayer)attacker;
            playerAttacker.spawnSweepParticles();
            attacker.getEntityWorld().playSound((EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0F, 1.0F);
         }

         boolean usedMana = false;

         for(EntityLivingBase target : targets) {
            double knockback = this.getDamageKnockback(itemStack);
            if (knockback != 0.0D && attacker != null && target != null) {
               double xDist = attacker.posX - target.posX;
               double zDist = attacker.posZ - target.posZ;
               double xzDist = Math.max((double)MathHelper.sqrt(xDist * xDist + zDist * zDist), 0.01D);
               double motionCap = 10.0D;
               if (target.motionX < motionCap && target.motionX > -motionCap && target.motionZ < motionCap && target.motionZ > -motionCap) {
                  target.addVelocity(-(xDist / xzDist * knockback + target.motionX * knockback), 0.0D, -(zDist / xzDist * knockback + target.motionZ * knockback));
               }
            }

            for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "effect")) {
               EffectEquipmentFeature effectFeature = (EffectEquipmentFeature)equipmentFeature;
               effectFeature.onHitEntity(itemStack, target, attacker);
            }

            for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "summon")) {
               SummonEquipmentFeature summonFeature = (SummonEquipmentFeature)equipmentFeature;
               usedMana = summonFeature.onHitEntity(itemStack, target, attacker) || usedMana;
            }

            for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
               ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
               usedMana = projectileFeature.onHitEntity(itemStack, target, attacker) || usedMana;
            }
         }

         if (usedMana) {
            this.removeMana(itemStack, 1);
         }

         this.removeSharpness(itemStack, 1);
         return true;
      }
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack itemStack) {
      Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
      if (slot == EntityEquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.getDamageAmount(itemStack), 0));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -this.getDamageCooldown(itemStack), 0));
      }

      return multimap;
   }

   public double getDamageAmount(ItemStack itemStack) {
      if (this.getSharpness(itemStack) <= 0) {
         return 0.0D;
      } else {
         double damage = 0.0D;

         for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "damage")) {
            DamageEquipmentFeature damageFeature = (DamageEquipmentFeature)equipmentFeature;
            damage += (double)damageFeature.damageAmount;
         }

         return damage;
      }
   }

   public double getDamageCooldown(ItemStack itemStack) {
      double cooldown = 0.0D;
      double i = 0.0D;

      for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "damage")) {
         DamageEquipmentFeature damageFeature = (DamageEquipmentFeature)equipmentFeature;
         cooldown += damageFeature.damageCooldown;
         ++i;
      }

      if (i == 0.0D) {
         return 2.4D;
      } else {
         cooldown = 2.4D * (cooldown / i);
         return Math.min(3.5D, cooldown);
      }
   }

   public double getDamageRange(ItemStack itemStack) {
      double range = 0.0D;

      for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "damage")) {
         DamageEquipmentFeature damageFeature = (DamageEquipmentFeature)equipmentFeature;
         range += damageFeature.damageRange;
      }

      return range;
   }

   public double getDamageKnockback(ItemStack itemStack) {
      if (this.getSharpness(itemStack) <= 0) {
         return 0.0D;
      } else {
         double knockback = 0.0D;

         for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "damage")) {
            DamageEquipmentFeature damageFeature = (DamageEquipmentFeature)equipmentFeature;
            knockback += damageFeature.damageKnockback;
         }

         return knockback;
      }
   }

   public double getDamageSweep(ItemStack itemStack) {
      if (this.getSharpness(itemStack) <= 0) {
         return 0.0D;
      } else {
         double sweep = 0.0D;

         for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "damage")) {
            DamageEquipmentFeature damageFeature = (DamageEquipmentFeature)equipmentFeature;
            sweep += damageFeature.damageSweep;
         }

         return sweep;
      }
   }

   public ModelResourceLocation getModelResourceLocation() {
      return new ModelResourceLocation(new ResourceLocation("lycanitesmobs", "equipmentpart"), "inventory");
   }

   public ResourceLocation getTexture(ItemStack itemStack) {
      return AssetManager.getTexture(this.itemName);
   }
}
