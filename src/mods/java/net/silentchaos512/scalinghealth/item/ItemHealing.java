package net.silentchaos512.scalinghealth.item;

import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.silentchaos512.lib.registry.ICustomModel;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.init.ModPotions;

public class ItemHealing extends Item implements ICustomModel {
   private static final int USE_TIME = 100;

   public ItemHealing() {
      this.setMaxStackSize(4);
      this.setHasSubtypes(true);
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BOW;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 100;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (player.getHealth() < player.getMaxHealth() && !player.isPotionActive(ModPotions.bandaged)) {
         player.setActiveHand(hand);
         return new ActionResult(EnumActionResult.SUCCESS, stack);
      } else {
         return new ActionResult(EnumActionResult.FAIL, stack);
      }
   }

   public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
      if (!world.isRemote) {
         ItemHealing.Type healingType = ItemHealing.Type.clampMeta(stack.getItemDamage());
         entityLiving.addPotionEffect(new PotionEffect(ModPotions.bandaged, healingType.effectDuration, healingType.amplifier, false, false));
         stack.shrink(1);
      }

      return stack;
   }

   public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
      if (count % 10 == 0) {
         player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.25F, (float)(1.100000023841858D + 0.05000000074505806D * ScalingHealth.random.nextGaussian()));
      }

   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
      if (this.isInCreativeTab(tab)) {
         for(ItemHealing.Type type : ItemHealing.Type.values()) {
            list.add(new ItemStack(this, 1, type.ordinal()));
         }

      }
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
      ItemHealing.Type healingType = ItemHealing.Type.clampMeta(stack.getItemDamage());
      list.add(ScalingHealth.i18n.itemSubText("healingitem", "healingValue", new Object[]{(int)(healingType.healPercentage * 100.0F), healingType.effectDuration / 20}));
      list.add(ScalingHealth.i18n.itemSubText("healingitem", "howToUse", new Object[]{Integer.valueOf(5)}));
   }

   public String getUnlocalizedName(ItemStack stack) {
      return ScalingHealth.i18n.getKey("item", ItemHealing.Type.clampMeta(stack.getItemDamage()).getItemName());
   }

   public void registerModels() {
      for(ItemHealing.Type type : ItemHealing.Type.values()) {
         ScalingHealth.registry.setModel(this, type.getItemDamage(), type.getItemName());
      }

   }

   public static enum Type {
      BANDAGE(0.3F, 1),
      MEDKIT(0.7F, 4);

      public final float healPercentage;
      public final int effectDuration;
      public final int amplifier;

      private Type(float healPercentage, int speed) {
         this.healPercentage = healPercentage;
         this.effectDuration = (int)(healPercentage * 100.0F * 20.0F * 2.0F / (float)speed);
         this.amplifier = speed - 1;
      }

      public static ItemHealing.Type clampMeta(int unclampedMetadata) {
         return values()[MathHelper.clamp(unclampedMetadata, 0, values().length - 1)];
      }

      public String getItemName() {
         return this.name().toLowerCase(Locale.ROOT);
      }

      public int getItemDamage() {
         return this.ordinal();
      }
   }
}
