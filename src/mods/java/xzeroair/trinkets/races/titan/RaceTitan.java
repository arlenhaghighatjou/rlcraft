package xzeroair.trinkets.races.titan;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.races.EntityRacePropertiesHandler;
import xzeroair.trinkets.races.titan.config.TitanConfig;
import xzeroair.trinkets.traits.abilities.other.AbilityHeavy;
import xzeroair.trinkets.traits.abilities.other.AbilityLargeHands;
import xzeroair.trinkets.util.TrinketsConfig;

public class RaceTitan extends EntityRacePropertiesHandler {
   public static final TitanConfig serverConfig;
   public static List<String> disallowedMounts;

   public RaceTitan(@Nonnull EntityLivingBase e) {
      super(e, EntityRaces.titan);
      disallowedMounts = Arrays.asList(serverConfig.mountBlacklist);
   }

   public void startTransformation() {
      this.addAbility(new AbilityLargeHands());
      if (serverConfig.sink) {
         this.addAbility(new AbilityHeavy());
      }

   }

   public void whileTransformed() {
      if (!this.entity.world.isRemote && this.entity.isRiding()) {
         Entity mount = this.entity.getRidingEntity();
         if (mount != null && !this.mountEntity(mount)) {
            this.entity.dismountRidingEntity();
         }
      }

   }

   public boolean mountEntity(Entity mount) {
      if (this.isCreativePlayer()) {
         return true;
      } else if (!serverConfig.canMount) {
         return false;
      } else if (disallowedMounts.isEmpty()) {
         return true;
      } else {
         try {
            ResourceLocation regName = EntityRegistry.getEntry(mount.getClass()).getRegistryName();
            String modID = regName.getResourceDomain();
            String entityID = regName.getResourcePath();
            boolean doesWildcardExist = disallowedMounts.contains(modID + ":*");
            boolean exists = disallowedMounts.contains(regName.toString());
            if (doesWildcardExist || exists) {
               return serverConfig.whitelist;
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }

         return !serverConfig.whitelist;
      }
   }

   @SideOnly(Side.CLIENT)
   public void doRenderPlayerPre(EntityPlayer entity, double x, double y, double z, RenderPlayer renderer, float partialTick) {
      super.doRenderPlayerPre(entity, x, y, z, renderer, partialTick);
      if (entity.limbSwingAmount > 0.0F) {
         entity.limbSwingAmount -= 0.04F;
      }

   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.titan;
      disallowedMounts = Arrays.asList(serverConfig.mountBlacklist);
   }
}
