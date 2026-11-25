package artifacts.common.item;

import artifacts.Artifacts;
import artifacts.common.ModConfig;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@MethodsReturnNonnullByDefault
public class BaubleBase extends Item implements IBauble {
   public final BaubleType type;
   public final String name;
   protected SoundEvent equipSound = SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND;
   protected SoundEvent unequipSound = SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND;
   protected float equipPitch = 1.0F;

   public BaubleBase(String name, BaubleType type) {
      this.setRegistryName(name);
      this.setUnlocalizedName("artifacts." + name);
      this.setCreativeTab(Artifacts.ARTIFACTS_TAB);
      this.setMaxStackSize(1);
      this.type = type;
      this.name = name;
   }

   public BaubleBase setEquipSound(SoundEvent equipSound, SoundEvent unequipSound, float equipPitch) {
      this.equipSound = equipSound;
      this.unequipSound = unequipSound;
      this.equipPitch = equipPitch;
      return this;
   }

   public BaubleBase setEquipSound(SoundEvent equipSound, float pitch) {
      return this.setEquipSound(equipSound, equipSound, pitch);
   }

   public BaubleBase setEquipSound(SoundEvent equipSound, SoundEvent unequipSound) {
      return this.setEquipSound(equipSound, unequipSound, 1.0F);
   }

   public BaubleBase setEquipSound(SoundEvent equipSound) {
      return this.setEquipSound(equipSound, equipSound, 1.0F);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      if (!GuiScreen.isShiftKeyDown() && !ModConfig.client.alwaysShowTooltip) {
         tooltip.add(TextFormatting.BLUE + "" + I18n.translateToLocal("tooltip.artifacts.shiftinfo.name"));
      } else {
         tooltip.add(TextFormatting.BLUE + "" + I18n.translateToLocal("tooltip.artifacts." + this.name + ".name"));
      }

      if (stack.getTagCompound() != null && stack.getTagCompound().getBoolean("phantom_thread_invisible")) {
         tooltip.add(TextFormatting.AQUA + "" + I18n.translateToLocal("tooltip.artifacts.phantomthread.name"));
      }

   }

   public void registerModel() {
      Artifacts.proxy.registerItemRenderer(this, 0, this.name);
   }

   public EnumRarity getRarity(ItemStack par1ItemStack) {
      return EnumRarity.RARE;
   }

   public BaubleType getBaubleType(ItemStack itemStack) {
      return this.type;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
      IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

      for(int i = 0; i < baubles.getSlots(); ++i) {
         if (baubles.getStackInSlot(i).isEmpty() && baubles.isItemValidForSlot(i, player.getHeldItem(hand), player)) {
            baubles.setStackInSlot(i, player.getHeldItem(hand).copy());
            if (!player.capabilities.isCreativeMode) {
               player.getHeldItem(hand).setCount(0);
            }

            this.onEquipped(player.getHeldItem(hand), player);
            return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
         }
      }

      return new ActionResult(EnumActionResult.FAIL, player.getHeldItem(hand));
   }

   public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
      player.playSound(this.equipSound, 0.75F, 0.95F * this.equipPitch);
   }

   public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
      player.playSound(this.unequipSound, 0.75F, this.equipPitch);
   }
}
