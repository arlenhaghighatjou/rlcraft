package tschipp.carryon.compat.llibrary;

import net.ilexiconn.llibrary.client.event.PlayerModelEvent.SetRotationAngles;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tschipp.carryon.common.config.CarryOnConfig;
import tschipp.carryon.common.handler.RegistrationHandler;
import tschipp.carryon.common.helper.ScriptParseHelper;
import tschipp.carryon.common.item.ItemEntity;
import tschipp.carryon.common.item.ItemTile;
import tschipp.carryon.common.scripting.CarryOnOverride;
import tschipp.carryon.common.scripting.ScriptChecker;

public class LLibraryEvents {
   public LLibraryEvents() {
   }

   @SubscribeEvent
   public void setAngles(SetRotationAngles event) {
      if (CarryOnConfig.settings.renderArms) {
         EntityPlayer player = event.getEntityPlayer();
         if (player != null) {
            ModelBiped modelBiped = event.getModel();
            if (modelBiped instanceof ModelPlayer) {
               ItemStack stack = player.getHeldItemMainhand();
               ModelPlayer model = (ModelPlayer)modelBiped;
               if (!stack.isEmpty() && stack.getItem() == RegistrationHandler.itemTile && ItemTile.hasTileData(stack) || stack.getItem() == RegistrationHandler.itemEntity && ItemEntity.hasEntityData(stack)) {
                  float rotation = 0.0F;
                  if (player.isRiding() && player.getRidingEntity() instanceof EntityLivingBase) {
                     rotation = 0.0F;
                  } else {
                     rotation = 0.0F;
                  }

                  CarryOnOverride overrider = ScriptChecker.getOverride(player);
                  if (overrider != null) {
                     double[] rotLeft = null;
                     double[] rotRight = null;
                     if (overrider.getRenderRotationLeftArm() != null) {
                        rotLeft = ScriptParseHelper.getXYZArray(overrider.getRenderRotationLeftArm());
                     }

                     if (overrider.getRenderRotationRightArm() != null) {
                        rotRight = ScriptParseHelper.getXYZArray(overrider.getRenderRotationRightArm());
                     }

                     boolean renderRight = overrider.isRenderRightArm();
                     boolean renderLeft = overrider.isRenderLeftArm();
                     if (renderLeft && rotLeft != null) {
                        this.render(model.bipedLeftArm, (float)rotLeft[0], (float)rotLeft[2], rotation);
                        this.render(model.bipedLeftArmwear, (float)rotLeft[0], (float)rotLeft[2], rotation);
                     } else if (renderLeft) {
                        this.render(model.bipedLeftArm, 0.8F + (player.isSneaking() ? 0.2F : 0.0F) - (stack.getItem() == RegistrationHandler.itemEntity ? -0.2F : 0.0F), stack.getItem() == RegistrationHandler.itemEntity ? 0.15F : 0.0F, rotation);
                        this.render(model.bipedLeftArmwear, 0.8F + (player.isSneaking() ? 0.2F : 0.0F) - (stack.getItem() == RegistrationHandler.itemEntity ? -0.2F : 0.0F), stack.getItem() == RegistrationHandler.itemEntity ? 0.15F : 0.0F, rotation);
                     }

                     if (renderRight && rotRight != null) {
                        this.render(model.bipedRightArm, (float)rotRight[0], (float)rotRight[2], rotation);
                        this.render(model.bipedRightArmwear, (float)rotRight[0], (float)rotRight[2], rotation);
                     } else if (renderRight) {
                        this.render(model.bipedRightArm, 0.8F + (player.isSneaking() ? 0.2F : 0.0F) - (stack.getItem() == RegistrationHandler.itemEntity ? -0.2F : 0.0F), stack.getItem() == RegistrationHandler.itemEntity ? -0.15F : 0.0F, rotation);
                        this.render(model.bipedRightArmwear, 0.8F + (player.isSneaking() ? 0.2F : 0.0F) - (stack.getItem() == RegistrationHandler.itemEntity ? -0.2F : 0.0F), stack.getItem() == RegistrationHandler.itemEntity ? -0.15F : 0.0F, rotation);
                     }
                  } else {
                     this.render(model.bipedRightArm, 0.8F + (player.isSneaking() ? 0.2F : 0.0F) - (stack.getItem() == RegistrationHandler.itemEntity ? -0.2F : 0.0F), stack.getItem() == RegistrationHandler.itemEntity ? -0.15F : 0.0F, rotation);
                     this.render(model.bipedRightArmwear, 0.8F + (player.isSneaking() ? 0.2F : 0.0F) - (stack.getItem() == RegistrationHandler.itemEntity ? -0.2F : 0.0F), stack.getItem() == RegistrationHandler.itemEntity ? -0.15F : 0.0F, rotation);
                     this.render(model.bipedLeftArm, 0.8F + (player.isSneaking() ? 0.2F : 0.0F) - (stack.getItem() == RegistrationHandler.itemEntity ? -0.2F : 0.0F), stack.getItem() == RegistrationHandler.itemEntity ? 0.15F : 0.0F, rotation);
                     this.render(model.bipedLeftArmwear, 0.8F + (player.isSneaking() ? 0.2F : 0.0F) - (stack.getItem() == RegistrationHandler.itemEntity ? -0.2F : 0.0F), stack.getItem() == RegistrationHandler.itemEntity ? 0.15F : 0.0F, rotation);
                  }
               }
            }
         }

      }
   }

   @SideOnly(Side.CLIENT)
   private void render(ModelRenderer arm, float x, float z, float rotation) {
      arm.rotateAngleX = -x;
      arm.rotateAngleY = (float)(-Math.toRadians((double)rotation));
      arm.rotateAngleZ = z;
   }
}
