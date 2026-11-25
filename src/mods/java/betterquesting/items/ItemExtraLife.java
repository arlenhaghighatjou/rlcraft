package betterquesting.items;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.properties.NativeProps;
import betterquesting.core.BetterQuesting;
import betterquesting.storage.LifeDatabase;
import betterquesting.storage.QuestSettings;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemExtraLife extends Item {
   public ItemExtraLife() {
      this.setUnlocalizedName("betterquesting.extra_life");
      this.setCreativeTab(BetterQuesting.tabQuesting);
      this.setHasSubtypes(true);
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (stack.getItemDamage() == 0 && hand == EnumHand.MAIN_HAND) {
         if (((Boolean)QuestSettings.INSTANCE.getProperty(NativeProps.HARDCORE)).booleanValue()) {
            if (!player.capabilities.isCreativeMode) {
               stack.grow(-1);
            }

            EaglercraftUUID uuid = QuestingAPI.getQuestingUUID(player);
            int lives = LifeDatabase.INSTANCE.getLives(uuid);
            if (lives >= ((Integer)QuestSettings.INSTANCE.getProperty(NativeProps.LIVES_MAX)).intValue()) {
               if (!world.isRemote) {
                  player.sendStatusMessage((new TextComponentString(TextFormatting.RED.toString())).appendSibling(new TextComponentTranslation("betterquesting.gui.full_lives", new Object[0])), true);
               }

               return new ActionResult(EnumActionResult.PASS, stack);
            }

            player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
            if (!world.isRemote) {
               LifeDatabase.INSTANCE.setLives(uuid, lives + 1);
               player.sendStatusMessage(new TextComponentTranslation("betterquesting.gui.remaining_lives", new Object[]{TextFormatting.YELLOW.toString() + (lives + 1)}), true);
            }
         } else if (!world.isRemote) {
            player.sendStatusMessage(new TextComponentTranslation("betterquesting.msg.heart_disabled", new Object[0]), true);
         }

         return new ActionResult(EnumActionResult.PASS, stack);
      } else {
         return new ActionResult(EnumActionResult.PASS, stack);
      }
   }

   @Nonnull
   public String getUnlocalizedName(ItemStack stack) {
      switch(stack.getItemDamage() % 3) {
      case 1:
         return this.getUnlocalizedName() + ".half";
      case 2:
         return this.getUnlocalizedName() + ".quarter";
      default:
         return this.getUnlocalizedName() + ".full";
      }
   }

   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return stack.getItemDamage() == 0;
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
      if (this.isInCreativeTab(tab)) {
         list.add(new ItemStack(this, 1, 0));
         list.add(new ItemStack(this, 1, 1));
         list.add(new ItemStack(this, 1, 2));
      }

   }
}
