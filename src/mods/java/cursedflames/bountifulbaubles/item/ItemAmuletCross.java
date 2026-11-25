package cursedflames.bountifulbaubles.item;

import baubles.api.BaubleType;
import baubles.api.render.IRenderBauble;
import baubles.api.render.IRenderBauble.RenderType;
import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.item.base.AGenericItemBauble;
import cursedflames.bountifulbaubles.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.item.IPhantomInkable;

public class ItemAmuletCross extends AGenericItemBauble implements IRenderBauble, IPhantomInkable {
   public static final int RESIST_TIME = 36;
   public static final ResourceLocation texture = new ResourceLocation("bountifulbaubles", "textures/models/armor/amulet_cross.png");
   @SideOnly(Side.CLIENT)
   private static ModelBiped model;

   public ItemAmuletCross() {
      super("amuletCross", BountifulBaubles.TAB);
   }

   public BaubleType getBaubleType(ItemStack arg0) {
      return BaubleType.AMULET;
   }

   public void onEquipped(ItemStack stack, EntityLivingBase player) {
      player.maxHurtResistantTime = 36;
      super.onEquipped(stack, player);
   }

   public void onUnequipped(ItemStack stack, EntityLivingBase player) {
      player.maxHurtResistantTime = 20;
      super.onUnequipped(stack, player);
   }

   public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
   }

   public void onRenderObject(ItemStack stack, EntityPlayer player, boolean isSlim, float partialTicks, float scale) {
      if (!(stack.getItem() instanceof IPhantomInkable) || !((IPhantomInkable)stack.getItem()).hasPhantomInk(stack)) {
         if (player.hasItemInSlot(EntityEquipmentSlot.CHEST)) {
            GlStateManager.translate(0.0F, -0.02F, -0.045F);
            GlStateManager.scale(1.1F, 1.1F, 1.1F);
         }

         Minecraft.getMinecraft().renderEngine.bindTexture(texture);
         float s = 0.07125F;
         GlStateManager.scale(0.07125F, 0.07125F, 0.07125F);
         if (model == null) {
            model = new ModelBiped();
         }

         model.bipedBody.render(1.0F);
      }
   }

   public RenderType getRenderType() {
      return RenderType.BODY;
   }

   public boolean hasPhantomInk(ItemStack stack) {
      return ItemUtil.hasPhantomInk(stack);
   }

   public void setPhantomInk(ItemStack stack, boolean ink) {
      ItemUtil.setPhantomInk(stack, ink);
   }
}
