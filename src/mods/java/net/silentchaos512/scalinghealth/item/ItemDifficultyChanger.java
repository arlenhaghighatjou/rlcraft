package net.silentchaos512.scalinghealth.item;

import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.lib.registry.ICustomModel;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.init.ModSounds;
import net.silentchaos512.scalinghealth.lib.EnumModParticles;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;

public class ItemDifficultyChanger extends Item implements ICustomModel {
   public ItemDifficultyChanger() {
      this.setHasSubtypes(true);
   }

   public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
      if (stack.getItemDamage() <= 1) {
         String amountStr = String.format("%d", stack.getItemDamage() == ItemDifficultyChanger.Type.ENCHANTED.ordinal() ? (int)Config.Items.enchantedHeartChange : (int)Config.Items.cursedHeartChange);
         if (amountStr.matches("^\\d+")) {
            amountStr = "+" + amountStr;
         }

         String line = ScalingHealth.i18n.itemSubText("difficultychanger", "effectDesc", new Object[]{amountStr});
         list.add(TextFormatting.WHITE + line);
      }
   }

   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.EPIC;
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
      if (this.isInCreativeTab(tab)) {
         for(ItemDifficultyChanger.Type type : ItemDifficultyChanger.Type.values()) {
            list.add(new ItemStack(this, 1, type.getItemDamage()));
         }

      }
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
      if (data == null) {
         return new ActionResult(EnumActionResult.PASS, stack);
      } else {
         double particleX = player.posX;
         double particleY = player.posY + (double)(0.65F * player.height);
         double particleZ = player.posZ;
         switch(ItemDifficultyChanger.Type.getByMeta(stack.getItemDamage())) {
         case ENCHANTED:
            if (!world.isRemote) {
               data.incrementDifficulty((double)Config.Items.enchantedHeartChange);
               stack.shrink(1);
            }

            for(int i = 0; i < 20 - 5 * ScalingHealth.proxy.getParticleSettings(); ++i) {
               double xSpeed = 0.08D * ScalingHealth.random.nextGaussian();
               double ySpeed = 0.05D * ScalingHealth.random.nextGaussian();
               double zSpeed = 0.08D * ScalingHealth.random.nextGaussian();
               ScalingHealth.proxy.spawnParticles(EnumModParticles.ENCHANTED_HEART, new Color(1.0F, 1.0F, 0.5F), world, particleX, particleY, particleZ, xSpeed, ySpeed, zSpeed);
            }

            world.playSound((EntityPlayer)null, player.getPosition(), ModSounds.ENCHANTED_HEART_USE, SoundCategory.PLAYERS, 0.4F, 1.7F);
            return new ActionResult(EnumActionResult.SUCCESS, stack);
         case CURSED:
            if (!world.isRemote) {
               data.incrementDifficulty((double)Config.Items.cursedHeartChange);
               stack.shrink(1);
            }

            for(int i = 0; i < 20 - 5 * ScalingHealth.proxy.getParticleSettings(); ++i) {
               double xSpeed = 0.08D * ScalingHealth.random.nextGaussian();
               double ySpeed = 0.05D * ScalingHealth.random.nextGaussian();
               double zSpeed = 0.08D * ScalingHealth.random.nextGaussian();
               ScalingHealth.proxy.spawnParticles(EnumModParticles.CURSED_HEART, new Color(0.4F, 0.0F, 0.6F), world, particleX, particleY, particleZ, xSpeed, ySpeed, zSpeed);
            }

            world.playSound((EntityPlayer)null, player.getPosition(), ModSounds.CURSED_HEART_USE, SoundCategory.PLAYERS, 0.3F, (float)(0.699999988079071D + 0.05000000074505806D * ScalingHealth.random.nextGaussian()));
            return new ActionResult(EnumActionResult.SUCCESS, stack);
         default:
            ScalingHealth.logHelper.warn("DifficultyChanger invalid meta: {}", new Object[]{stack.getItemDamage()});
            return new ActionResult(EnumActionResult.PASS, stack);
         }
      }
   }

   public String getUnlocalizedName(ItemStack stack) {
      return ScalingHealth.i18n.getKey("item", ItemDifficultyChanger.Type.getByMeta(stack.getItemDamage()).getItemName());
   }

   public void registerModels() {
      for(ItemDifficultyChanger.Type type : ItemDifficultyChanger.Type.values()) {
         ScalingHealth.registry.setModel(this, type.getItemDamage(), type.getItemName());
      }

   }

   static enum Type {
      ENCHANTED,
      CURSED;

      private Type() {
      }

      static ItemDifficultyChanger.Type getByMeta(int meta) {
         return values()[meta & 1];
      }

      public String getItemName() {
         return this.name().toLowerCase(Locale.ROOT) + "_heart";
      }

      public int getItemDamage() {
         return this.ordinal();
      }
   }
}
