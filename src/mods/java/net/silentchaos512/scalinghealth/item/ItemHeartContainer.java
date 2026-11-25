package net.silentchaos512.scalinghealth.item;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.EntityHelper;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.init.ModSounds;
import net.silentchaos512.scalinghealth.lib.EnumModParticles;
import net.silentchaos512.scalinghealth.network.NetworkHandler;
import net.silentchaos512.scalinghealth.network.message.MessageDataSync;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;

public class ItemHeartContainer extends Item {
   public ItemHeartContainer() {
   }

   public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
      list.add(ScalingHealth.i18n.subText(this, "desc", new Object[0]));
   }

   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.RARE;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (!world.isRemote) {
         SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
         if (data == null) {
            return ActionResult.newResult(EnumActionResult.PASS, stack);
         }

         boolean healthIncreaseAllowed = isHealthIncreaseAllowed(data);
         int levelRequirement = getLevelsRequiredToUse(player, stack, healthIncreaseAllowed);
         if (player.experienceLevel < levelRequirement) {
            ChatHelper.translateStatus(player, ScalingHealth.i18n.getKey(this, "notEnoughXP"), true, new Object[]{levelRequirement});
            return ActionResult.newResult(EnumActionResult.PASS, stack);
         }

         boolean consumed = Config.Items.Heart.healthRestored > 0 && player.getHealth() < player.getMaxHealth();
         if (consumed) {
            doExtraHealing(player);
         }

         if (!healthIncreaseAllowed) {
            return this.useAsHealingItem(world, player, stack, levelRequirement, consumed);
         }

         this.useForHealthIncrease(world, player, stack, data, levelRequirement);
      }

      return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
   }

   private static void doExtraHealing(EntityPlayer player) {
      int current = (int)player.getHealth();
      EntityHelper.heal(player, (float)Config.Items.Heart.healthRestored, Config.Items.Heart.healingEvent);
      int newHealth = (int)player.getHealth();
      if (current + Config.Items.Heart.healthRestored != newHealth) {
         ScalingHealth.logHelper.warn("Another mod seems to have canceled healing from a heart container (player {})", new Object[]{player.getName()});
      }

   }

   private void incrementUseStat(EntityPlayer player) {
      StatBase useStat = StatList.getObjectUseStats(this);
      if (useStat != null) {
         player.addStat(useStat);
      }

   }

   @Nonnull
   private ActionResult<ItemStack> useAsHealingItem(World world, EntityPlayer player, ItemStack stack, int levelRequirement, boolean consumed) {
      if (consumed) {
         world.playSound((EntityPlayer)null, player.getPosition(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, 1.0F + 0.1F * (float)ScalingHealth.random.nextGaussian());
         stack.shrink(1);
         consumeLevels(player, levelRequirement);
         this.incrementUseStat(player);
         return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
      } else {
         return ActionResult.newResult(EnumActionResult.PASS, stack);
      }
   }

   private void useForHealthIncrease(World world, EntityPlayer player, ItemStack stack, SHPlayerDataHandler.PlayerData data, int levelRequirement) {
      data.incrementMaxHealth(2.0F);
      stack.shrink(1);
      spawnParticlesAndPlaySound(world, player);
      consumeLevels(player, levelRequirement);
      this.incrementUseStat(player);
      NetworkHandler.INSTANCE.sendTo(new MessageDataSync(data, player), (EntityPlayerMP)player);
   }

   private static int getLevelsRequiredToUse(EntityPlayer player, ItemStack stack, boolean healthIncreaseAllowed) {
      return player.capabilities.isCreativeMode ? 0 : Config.Items.Heart.xpCost;
   }

   private static void consumeLevels(EntityPlayer player, int amount) {
      player.experienceLevel -= amount;
   }

   private static boolean isHealthIncreaseAllowed(SHPlayerDataHandler.PlayerData data) {
      return Config.Items.Heart.increaseHealth && (Config.Player.Health.maxHealth == 0 || data.getMaxHealth() < (float)Config.Player.Health.maxHealth);
   }

   private static void spawnParticlesAndPlaySound(World world, EntityPlayer player) {
      double particleX = player.posX;
      double particleY = player.posY + (double)(0.65F * player.height);
      double particleZ = player.posZ;

      for(int i = 0; i < 40 - 10 * ScalingHealth.proxy.getParticleSettings(); ++i) {
         double xSpeed = 0.08D * ScalingHealth.random.nextGaussian();
         double ySpeed = 0.05D * ScalingHealth.random.nextGaussian();
         double zSpeed = 0.08D * ScalingHealth.random.nextGaussian();
         ScalingHealth.proxy.spawnParticles(EnumModParticles.HEART_CONTAINER, new Color(1.0F, 0.0F, 0.0F), world, particleX, particleY, particleZ, xSpeed, ySpeed, zSpeed);
      }

      ScalingHealth.proxy.playSoundOnClient(player, ModSounds.HEART_CONTAINER_USE, 0.5F, 1.0F + 0.1F * (float)ScalingHealth.random.nextGaussian());
   }
}
