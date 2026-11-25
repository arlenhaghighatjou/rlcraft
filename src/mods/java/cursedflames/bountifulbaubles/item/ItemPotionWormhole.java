package cursedflames.bountifulbaubles.item;

import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.item.base.GenericItemBB;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPotionWormhole extends GenericItemBB {
   public static final int GUI_ID = 2;

   public ItemPotionWormhole() {
      super("potionWormhole", BountifulBaubles.TAB);
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      if (world.playerEntities.size() < 2) {
         player.sendStatusMessage(new TextComponentTranslation(ModItems.potionWormhole.getUnlocalizedName() + ".nootherplayers", new Object[0]), true);
         return new ActionResult(EnumActionResult.FAIL, player.getHeldItem(hand));
      } else {
         player.setActiveHand(hand);
         return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
      }
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.DRINK;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 15;
   }

   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entity) {
      EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
      if (player != null && !worldIn.isRemote) {
         player.openGui(BountifulBaubles.instance, 2, worldIn, (int)player.posX, (int)player.posY, (int)player.posZ);
      }

      return stack;
   }

   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      String base = this.getUnlocalizedName() + ".tooltip.";
      if (Minecraft.getMinecraft().isSingleplayer()) {
         tooltip.add(I18n.translateToLocal(base + "sp"));
      } else {
         tooltip.add(I18n.translateToLocal(base + "mp"));
      }

   }
}
