package xzeroair.trinkets.items.foods;

import java.util.function.Consumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.items.base.FoodBase;
import xzeroair.trinkets.races.EntityRace;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.MagicHelper;

public class Restore_Item extends FoodBase {
   public Restore_Item(String name) {
      super(name, 0, 0.0F);
      this.setAlwaysEdible();
      this.setUUID("339036e8-b984-40da-936b-f62da371e104");
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 32;
   }

   public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)entity;
         entityplayer.getFoodStats().addStats(this, stack);
         world.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
         this.onFoodEaten(stack, world, entityplayer);
         entityplayer.addStat(StatList.getObjectUseStats(this));
         if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
         }
      }

      stack.shrink(1);
      Capabilities.getEntityProperties(entity, (prop) -> {
         prop.setImbuedRace((EntityRace)null);
      });
      MagicHelper.refillMana(entity);
      if (TrinketsConfig.SERVER.mana.reagentHarmful) {
         if (!entity.isPotionActive(MobEffects.POISON)) {
            entity.addPotionEffect(new PotionEffect(MobEffects.POISON, 400, 1, false, false));
         }

         if (!entity.isPotionActive(MobEffects.WEAKNESS)) {
            entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 600, 1, false, false));
         }
      }

      if (stack.isEmpty()) {
         return new ItemStack(Items.GLASS_BOTTLE);
      } else {
         EntityPlayer entityplayer = entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
         if (entityplayer instanceof EntityPlayerMP && !entityplayer.capabilities.isCreativeMode && !entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE))) {
            world.spawnEntity(new EntityItem(world, entityplayer.posX, entityplayer.posY, entityplayer.posZ, new ItemStack(Items.GLASS_BOTTLE)));
         }

         return stack;
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
      return super.onItemRightClick(worldIn, player, handIn);
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.DRINK;
   }

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      super.registerModels();
   }
}
