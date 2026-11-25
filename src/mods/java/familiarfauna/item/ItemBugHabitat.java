package familiarfauna.item;

import familiarfauna.entities.EntityButterfly;
import familiarfauna.entities.EntityDragonfly;
import familiarfauna.entities.EntityPixie;
import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBugHabitat extends Item {
   public ItemBugHabitat() {
      this.addPropertyOverride(new ResourceLocation("bug"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(@Nonnull ItemStack stack, World world, EntityLivingBase entity) {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Bug")) {
               if (stack.getTagCompound().getString("Bug").equals("butterfly")) {
                  return 1.0F;
               }

               if (stack.getTagCompound().getString("Bug").equals("dragonfly")) {
                  return 2.0F;
               }

               if (stack.getTagCompound().getString("Bug").equals("pixie")) {
                  return 3.0F;
               }
            }

            return 0.0F;
         }
      });
      this.addPropertyOverride(new ResourceLocation("variant"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(@Nonnull ItemStack stack, World world, EntityLivingBase entity) {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Bug")) {
               if (stack.getTagCompound().getString("Bug").equals("butterfly")) {
                  if (stack.getTagCompound().hasKey("Type")) {
                     return (float)(stack.getTagCompound().getInteger("Type") + 1);
                  }

                  return 1.0F;
               }

               if (stack.getTagCompound().getString("Bug").equals("dragonfly")) {
                  if (stack.getTagCompound().hasKey("Type")) {
                     return (float)(stack.getTagCompound().getInteger("Type") + 1);
                  }

                  return 1.0F;
               }

               if (stack.getTagCompound().getString("Bug").equals("pixie")) {
                  if (stack.getTagCompound().hasKey("Type")) {
                     return (float)(stack.getTagCompound().getInteger("Type") + 1);
                  }

                  return 1.0F;
               }
            }

            return 0.0F;
         }
      });
      this.maxStackSize = 1;
   }

   public String getUnlocalizedName(ItemStack stack) {
      return stack.hasTagCompound() && stack.getTagCompound().hasKey("Bug") && !stack.getTagCompound().hasKey("Name") ? super.getUnlocalizedName() + "_" + stack.getTagCompound().getString("Bug") : super.getUnlocalizedName();
   }

   public String getItemStackDisplayName(ItemStack stack) {
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Name")) {
         String name = stack.getTagCompound().getString("Name");
         return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim() + " - " + TextFormatting.ITALIC + name + TextFormatting.RESET;
      } else {
         return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
      }
   }

   protected Vec3d getAirPositionInFrontOfPlayer(World world, EntityPlayer player, double targetDistance) {
      float cosYaw = MathHelper.cos(-player.rotationYaw * 0.017453292F - 3.1415927F);
      float sinYaw = MathHelper.sin(-player.rotationYaw * 0.017453292F - 3.1415927F);
      float cosPitch = -MathHelper.cos(-player.rotationPitch * 0.017453292F);
      float facingX = sinYaw * cosPitch;
      float facingY = MathHelper.sin(-player.rotationPitch * 0.017453292F);
      float facingZ = cosYaw * cosPitch;
      Vec3d playerEyePosition = new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
      Vec3d targetPosition = playerEyePosition.addVector((double)facingX * targetDistance, (double)facingY * targetDistance, (double)facingZ * targetDistance);
      RayTraceResult hit = this.rayTrace(world, player, true);
      if (hit == null) {
         return targetPosition;
      } else {
         double distance = playerEyePosition.distanceTo(hit.hitVec) * 0.9D;
         return playerEyePosition.addVector((double)facingX * distance, (double)facingY * distance, (double)facingZ * distance);
      }
   }

   public boolean releaseBug(ItemStack stack, World world, EntityPlayer player, EnumHand hand, Vec3d releasePoint) {
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Bug") && !world.isRemote) {
         String bugEntity = stack.getTagCompound().getString("Bug");
         String bugName = "";
         int bugType = 0;
         if (stack.getTagCompound().hasKey("Name")) {
            bugName = stack.getTagCompound().getString("Name");
         }

         if (stack.getTagCompound().hasKey("Type")) {
            bugType = stack.getTagCompound().getInteger("Type");
         }

         if (bugEntity.equals("butterfly")) {
            EntityButterfly butterfly = new EntityButterfly(world);
            butterfly.setButterflyType(bugType);
            butterfly.setLocationAndAngles(releasePoint.x, releasePoint.y, releasePoint.z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
            if (bugName != "") {
               butterfly.setCustomNameTag(bugName);
            }

            world.spawnEntity(butterfly);
         }

         if (bugEntity.equals("dragonfly")) {
            EntityDragonfly dragonfly = new EntityDragonfly(world);
            dragonfly.setDragonflyType(bugType);
            dragonfly.setLocationAndAngles(releasePoint.x, releasePoint.y, releasePoint.z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
            if (bugName != "") {
               dragonfly.setCustomNameTag(bugName);
            }

            world.spawnEntity(dragonfly);
         }

         if (bugEntity.equals("pixie")) {
            EntityPixie pixie = new EntityPixie(world);
            pixie.setPixieType(bugType);
            pixie.setLocationAndAngles(releasePoint.x, releasePoint.y, releasePoint.z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
            if (bugName != "") {
               pixie.setCustomNameTag(bugName);
            }

            world.spawnEntity(pixie);
         }

         stack.getTagCompound().removeTag("Bug");
         stack.getTagCompound().removeTag("Type");
         stack.getTagCompound().removeTag("Name");
         return true;
      } else {
         return false;
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (world.isRemote) {
         return new ActionResult(EnumActionResult.FAIL, stack);
      } else {
         Vec3d releasePoint = this.getAirPositionInFrontOfPlayer(world, player, 2.0D);
         return this.releaseBug(stack, world, player, hand, releasePoint) ? new ActionResult(EnumActionResult.SUCCESS, stack) : new ActionResult(EnumActionResult.FAIL, stack);
      }
   }
}
