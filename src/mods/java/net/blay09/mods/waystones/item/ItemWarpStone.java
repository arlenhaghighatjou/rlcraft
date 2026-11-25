package net.blay09.mods.waystones.item;

import java.util.List;
import javax.annotation.Nullable;
import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWarpStone extends Item implements IResetUseOnDamage {
   public static final String name = "warp_stone";
   public static final ResourceLocation registryName = new ResourceLocation("waystones", "warp_stone");
   public static long lastTimerUpdate;

   public ItemWarpStone() {
      this.setRegistryName("warp_stone");
      this.setUnlocalizedName(registryName.toString());
      this.setCreativeTab(Waystones.creativeTab);
      this.setMaxStackSize(1);
      this.setMaxDamage(100);
   }

   public int getMaxItemUseDuration(ItemStack itemStack) {
      return WaystoneConfig.general.warpStoneUseTime;
   }

   public EnumAction getItemUseAction(ItemStack itemStack) {
      return Waystones.proxy.isVivecraftInstalled() ? EnumAction.NONE : EnumAction.BOW;
   }

   public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entityLiving) {
      if (world.isRemote && entityLiving instanceof EntityPlayer) {
         Waystones.proxy.openWaystoneSelection((EntityPlayer)entityLiving, WarpMode.WARP_STONE, entityLiving.getActiveHand(), (WaystoneEntry)null);
      }

      return itemStack;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      if (player.capabilities.isCreativeMode) {
         PlayerWaystoneHelper.setLastWarpStoneUse(player, 0L);
      }

      if (PlayerWaystoneHelper.canUseWarpStone(player)) {
         if (!player.isHandActive() && world.isRemote) {
            Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, new BlockPos(player.posX, player.posY, player.posZ), 2.0F);
         }

         if (Waystones.proxy.isVivecraftInstalled()) {
            this.onItemUseFinish(itemStack, world, player);
         } else {
            player.setActiveHand(hand);
         }

         return new ActionResult(EnumActionResult.SUCCESS, itemStack);
      } else {
         player.sendStatusMessage(new TextComponentTranslation("waystones:stoneNotCharged", new Object[0]), true);
         return new ActionResult(EnumActionResult.FAIL, itemStack);
      }
   }

   @SideOnly(Side.CLIENT)
   public boolean showDurabilityBar(ItemStack itemStack) {
      return this.getDurabilityForDisplay(itemStack) > 0.0D;
   }

   @SideOnly(Side.CLIENT)
   public double getDurabilityForDisplay(ItemStack stack) {
      EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
      if (player == null) {
         return 0.0D;
      } else {
         long timeLeft = PlayerWaystoneHelper.getLastWarpStoneUse(player);
         long timeSince = System.currentTimeMillis() - lastTimerUpdate;
         float percentage = (float)timeSince / (float)timeLeft;
         return 1.0D - (double)Math.max(0.0F, Math.min(1.0F, percentage));
      }
   }

   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack itemStack) {
      return PlayerWaystoneHelper.canUseWarpStone(FMLClientHandler.instance().getClientPlayerEntity());
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (player != null) {
         long timeLeft = PlayerWaystoneHelper.getLastWarpStoneUse(player);
         long timeSince = System.currentTimeMillis() - lastTimerUpdate;
         int secondsLeft = (int)((timeLeft - timeSince) / 1000L);
         if (secondsLeft > 0) {
            tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:cooldownLeft", new Object[]{secondsLeft}));
         }

      }
   }
}
