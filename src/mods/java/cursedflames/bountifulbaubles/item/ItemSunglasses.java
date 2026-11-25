package cursedflames.bountifulbaubles.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import baubles.api.render.IRenderBauble.RenderType;
import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.baubleeffect.PotionNegation;
import cursedflames.bountifulbaubles.client.layer.IRenderObject;
import cursedflames.bountifulbaubles.item.armor.ItemArmorBB;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.item.IPhantomInkable;

public class ItemSunglasses extends ItemArmorBB implements IBauble, ISpecialArmor, IRenderBauble, PotionNegation.IPotionNegateItem, IRenderObject {
   private static final ResourceLocation texture = new ResourceLocation("bountifulbaubles", "textures/models/armor/sunglasses_layer_1.png");

   public ItemSunglasses(ArmorMaterial mat) {
      super("trinketMagicLenses", "sunglasses", mat, EntityEquipmentSlot.HEAD, BountifulBaubles.TAB);
   }

   public BaubleType getBaubleType(ItemStack arg0) {
      return BaubleType.HEAD;
   }

   public void onWornTick(ItemStack stack, EntityLivingBase player) {
      PotionNegation.negatePotion(player, Arrays.asList("minecraft:blindness"));
   }

   public void onArmorTick(World world, EntityPlayer player, ItemStack armor) {
      PotionNegation.negatePotion(player, Arrays.asList("minecraft:blindness"));
   }

   public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
      return new ArmorProperties(-2147483647, 0.0D, 0);
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
      return 0;
   }

   public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
      if (!stack.isEmpty() && stack.getItem() instanceof ItemArmorBB) {
         if (stack.getItem() instanceof IPhantomInkable && ((IPhantomInkable)stack.getItem()).hasPhantomInk(stack)) {
            return new ModelBiped();
         } else {
            ModelBiped model = BountifulBaubles.proxy.getArmorModel(this.modelName + "1");
            model.isSneak = defaultModel.isSneak;
            model.isRiding = defaultModel.isRiding;
            model.isChild = defaultModel.isChild;
            model.rightArmPose = defaultModel.rightArmPose;
            model.leftArmPose = defaultModel.leftArmPose;
            return model;
         }
      } else {
         return defaultModel;
      }
   }

   @SideOnly(Side.CLIENT)
   ResourceLocation getRenderTexture() {
      return texture;
   }

   @SideOnly(Side.CLIENT)
   public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
   }

   public void onRenderObject(ItemStack stack, EntityPlayer player, boolean isSlim, float partialTicks, float scale) {
      if (!(stack.getItem() instanceof IPhantomInkable) || !((IPhantomInkable)stack.getItem()).hasPhantomInk(stack)) {
         Minecraft.getMinecraft().renderEngine.bindTexture(this.getRenderTexture());
         GlStateManager.pushMatrix();
         GlStateManager.enableBlend();
         ModelBiped model = BountifulBaubles.proxy.getArmorModel(this.modelName + "2");
         float s = 0.0625F;
         GlStateManager.scale(s, s, s);
         model.bipedHead.render(1.0F);
         GlStateManager.popMatrix();
      }
   }

   public RenderType getRenderType() {
      return RenderType.HEAD;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      if (stack.getItem() instanceof IPhantomInkable && ((IPhantomInkable)stack.getItem()).hasPhantomInk(stack)) {
         tooltip.add(BountifulBaubles.proxy.translate("bountifulbaubles.misc.hasPhantomInk"));
      }

   }

   public List<String> getCureEffects() {
      return Arrays.asList("minecraft:blindness");
   }
}
