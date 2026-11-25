package net.silentchaos512.lib.item;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuidePage;
import net.silentchaos512.lib.guidebook.misc.GuideBookUtils;

public class ItemGuideBookSL extends Item {
   private static final List<ItemGuideBookSL> bookList = new ArrayList();
   public boolean giveBookOnFirstLogin = true;
   @SideOnly(Side.CLIENT)
   public IGuidePage forcedPage;
   public final GuideBook book;
   public final int bookId;

   public ItemGuideBookSL(GuideBook book) {
      this.book = book;
      this.bookId = bookList.size();
      bookList.add(this);
   }

   @Nullable
   public static ItemGuideBookSL getBookById(int id) {
      return id >= 0 && id < bookList.size() ? (ItemGuideBookSL)bookList.get(id) : null;
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (player.isSneaking()) {
         IBlockState state = world.getBlockState(pos);
         Block block = state.getBlock();
         ItemStack blockStack = new ItemStack(block, 1, block.damageDropped(state));
         IGuidePage page = GuideBookUtils.findFirstPageForStack(this.book, blockStack);
         if (page != null) {
            if (world.isRemote) {
               this.forcedPage = page;
            }

            this.onItemRightClick(world, player, hand);
            return EnumActionResult.SUCCESS;
         }
      }

      return EnumActionResult.FAIL;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      player.openGui(SilentLib.instance, this.bookId, world, (int)player.posX, (int)player.posY, (int)player.posZ);
      return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.ITALIC + I18n.format(this.getUnlocalizedName(stack) + ".desc", new Object[0]));
   }
}
