package xzeroair.trinkets.items.base;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.TranslationHelper;
import xzeroair.trinkets.util.interfaces.IsModelLoaded;

public class FoodBase extends ItemFood implements IsModelLoaded {
   private EaglercraftUUID uuid;
   protected int cooldown = 0;
   protected boolean canEat = true;

   public FoodBase(String name, int heal, float saturation) {
      super(heal, saturation, false);
      this.setUnlocalizedName(name);
      this.setRegistryName(name);
      this.setCreativeTab(Trinkets.trinketstab);
   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      return helper.formatAddVariables(translation);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltips, ITooltipFlag flagIn) {
      super.addInformation(stack, world, tooltips, flagIn);
      if (world != null) {
         TranslationHelper helper = TranslationHelper.INSTANCE;

         for(int i = 1; i < 10; ++i) {
            String string = helper.getLangTranslation(stack.getUnlocalizedName() + ".tooltip" + i, (lang) -> {
               return (void)this.customItemInformation(stack, world, flagIn, index, lang);
            });
            if (!helper.isStringEmpty(string)) {
               tooltips.add(string);
            }
         }

         TextComponentTranslation ctrl = new TextComponentTranslation("xat.holdctrl", new Object[0]);
         boolean tanEnabled = Trinkets.ToughAsNails && TrinketsConfig.compat.toughasnails;
         boolean faEnabled = Trinkets.FirstAid;
         boolean evEnabled = Trinkets.EnhancedVisuals && TrinketsConfig.compat.enhancedvisuals;
         String TAN = !tanEnabled ? "" : helper.getLangTranslation(stack.getUnlocalizedName() + ".compat.tan", (lang) -> {
            return (void)this.customItemInformation(stack, world, flagIn, 11, lang);
         });
         String FA = !faEnabled ? "" : helper.getLangTranslation(stack.getUnlocalizedName() + ".compat.firstaid", (lang) -> {
            return (void)this.customItemInformation(stack, world, flagIn, 12, lang);
         });
         String EV = !evEnabled ? "" : helper.getLangTranslation(stack.getUnlocalizedName() + ".compat.enhancedvisuals", (lang) -> {
            return (void)this.customItemInformation(stack, world, flagIn, 13, lang);
         });
         if (GuiScreen.isCtrlKeyDown()) {
            if (!helper.isStringEmpty(TAN)) {
               tooltips.add(TAN + helper.gold + " (Tough as Nails)");
            }

            if (!helper.isStringEmpty(FA)) {
               tooltips.add(FA + helper.gold + " (First Aid)");
            }

            if (!helper.isStringEmpty(EV)) {
               tooltips.add(EV + helper.gold + " (Enhanced Visuals)");
            }
         } else if (!helper.isStringEmpty(TAN) || !helper.isStringEmpty(EV) || !helper.isStringEmpty(FA)) {
            tooltips.add(helper.reset + "" + helper.dGray + ctrl.getFormattedText());
         }

      }
   }

   public String getItemStackDisplayName(ItemStack stack) {
      return super.getItemStackDisplayName(stack);
   }

   public EaglercraftUUID getUUID() {
      return this.uuid;
   }

   public void setUUID(String uuid) {
      if (!uuid.isEmpty()) {
         this.uuid = EaglercraftUUID.fromString(uuid);
      } else {
         this.uuid = EaglercraftUUID.randomUUID();
      }

   }

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      if (this.cooldown > 0) {
         this.canEat = false;
         --this.cooldown;
      } else {
         this.canEat = true;
      }

      super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      return super.onItemRightClick(worldIn, playerIn, handIn);
   }

   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
      return super.onItemUseFinish(stack, worldIn, entityLiving);
   }

   public void onCreated(ItemStack stack, World world, EntityPlayer player) {
      super.onCreated(stack, world, player);
   }

   public int getCooldown() {
      return this.cooldown;
   }

   public void setCooldown(int cooldown) {
      this.cooldown = cooldown;
   }

   public boolean getEdible() {
      return this.canEat;
   }

   public void registerModels() {
      Trinkets.proxy.registerItemRenderer(this, 0, "inventory");
   }
}
