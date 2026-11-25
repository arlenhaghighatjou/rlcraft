package cursedflames.bountifulbaubles.item;

import baubles.api.BaubleType;
import baubles.api.render.IRenderBauble;
import baubles.api.render.IRenderBauble.RenderType;
import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.item.base.AGenericItemBauble;
import cursedflames.bountifulbaubles.potion.ModPotions;
import cursedflames.bountifulbaubles.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.item.IPhantomInkable;

public class ItemAmuletSin extends AGenericItemBauble implements IRenderBauble, IPhantomInkable {
   public final ResourceLocation texture;
   @SideOnly(Side.CLIENT)
   private static ModelBiped model;

   public ItemAmuletSin(String name, String textureName) {
      super(name, BountifulBaubles.TAB);
      this.texture = new ResourceLocation("bountifulbaubles", "textures/models/armor/" + textureName + ".png");
   }

   public BaubleType getBaubleType(ItemStack stack) {
      return BaubleType.AMULET;
   }

   protected static void addEffect(EntityPlayer player, int level, int time, boolean particles) {
      player.addPotionEffect(new PotionEffect(ModPotions.sin, time, level, false, particles));
   }

   public boolean hasPhantomInk(ItemStack stack) {
      return ItemUtil.hasPhantomInk(stack);
   }

   public void setPhantomInk(ItemStack stack, boolean ink) {
      ItemUtil.setPhantomInk(stack, ink);
   }

   public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
   }

   public void onRenderObject(ItemStack stack, EntityPlayer player, boolean isSlim, float partialTicks, float scale) {
      if (!(stack.getItem() instanceof IPhantomInkable) || !((IPhantomInkable)stack.getItem()).hasPhantomInk(stack)) {
         if (player.hasItemInSlot(EntityEquipmentSlot.CHEST)) {
            GlStateManager.translate(0.0F, -0.02F, -0.045F);
            GlStateManager.scale(1.1F, 1.1F, 1.1F);
         }

         Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
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
}
