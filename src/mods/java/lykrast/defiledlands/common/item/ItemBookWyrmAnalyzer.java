package lykrast.defiledlands.common.item;

import java.util.List;
import javax.annotation.Nullable;
import lykrast.defiledlands.common.entity.passive.EntityBookWyrm;
import lykrast.defiledlands.common.util.LocUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemBookWyrmAnalyzer extends Item {
   public ItemBookWyrmAnalyzer() {
      this.setMaxStackSize(1);
   }

   public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
      if (entity.world.isRemote) {
         return false;
      } else if (entity instanceof EntityBookWyrm) {
         EntityBookWyrm target = (EntityBookWyrm)entity;
         String base = "ui.defiledlands.book_wyrm_analyze.";
         player.sendMessage(new TextComponentTranslation(base + "health", new Object[]{(int)target.getHealth(), (int)target.getMaxHealth()}));
         player.sendMessage(new TextComponentTranslation(base + "digest_time", new Object[]{target.getDigestTime()}));
         player.sendMessage(new TextComponentTranslation(base + "max_level", new Object[]{target.getMaxLevel()}));
         player.sendMessage(new TextComponentTranslation(base + "digested", new Object[]{target.digested}));
         if (target.digesting > 0) {
            player.sendMessage(new TextComponentTranslation(base + "digesting", new Object[]{target.digesting}));
         }

         if (target.isChild()) {
            int minutes = (int)Math.ceil((double)(-target.getGrowingAge()) / 1200.0D);
            player.sendMessage(new TextComponentTranslation(base + "maturing", new Object[]{minutes}));
         } else if (target.getGrowingAge() > 0) {
            int minutes = (int)Math.ceil((double)target.getGrowingAge() / 1200.0D);
            player.sendMessage(new TextComponentTranslation(base + "reproduce", new Object[]{minutes}));
         } else {
            player.sendMessage(new TextComponentTranslation(base + "ready", new Object[0]));
         }

         if (target.isGolden()) {
            player.sendMessage(new TextComponentTranslation(base + "golden", new Object[0]));
         }

         return true;
      } else {
         return false;
      }
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.addAll(LocUtils.getTooltips(TextFormatting.GRAY.toString() + I18n.format(super.getUnlocalizedName(stack) + ".tooltip", new Object[0])));
   }
}
