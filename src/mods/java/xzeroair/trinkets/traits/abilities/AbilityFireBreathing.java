package xzeroair.trinkets.traits.abilities;

import java.util.function.BiFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.client.keybinds.ModKeyBindings;
import xzeroair.trinkets.entity.MovingThrownProjectile;
import xzeroair.trinkets.traits.abilities.interfaces.IKeyBindInterface;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.ColorHelper;

public class AbilityFireBreathing extends Ability implements IKeyBindInterface {
   protected double breathStage = 0.0D;

   public AbilityFireBreathing() {
      super("fire_breathing");
   }

   protected boolean DragonBreath(Entity entity) {
      if (this.breathStage > 3.0D) {
         this.breathStage = 0.0D;
      }

      if (this.breathStage == 0.0D) {
         MagicStats magic = Capabilities.getMagicStats(entity);
         if (magic != null && !magic.spendMana(TrinketsConfig.SERVER.races.dragon.breath_cost)) {
            return false;
         }

         int bcolor = ((Integer)Capabilities.getEntityProperties(entity, Integer.valueOf(16711680), (prop, color) -> {
            return ColorHelper.convertHexadecimalToDecimal(prop.getRaceHandler().getAltTraitColor());
         })).intValue();
         World world = entity.getEntityWorld();
         float headPosX = (float)(entity.posX + 0.5400000214576721D * Math.cos((double)(entity.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
         float headPosZ = (float)(entity.posZ + 0.5400000214576721D * Math.sin((double)(entity.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
         float headPosY = (float)(entity.posY + (double)entity.getEyeHeight() * 0.8D);
         double d2 = entity.getLookVec().x;
         double d3 = entity.getLookVec().y;
         double d4 = entity.getLookVec().z;
         world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERDRAGON_SHOOT, SoundCategory.PLAYERS, 0.5F, 0.4F / (Reference.random.nextFloat() * 0.4F + 0.8F));
         if (!world.isRemote) {
            MovingThrownProjectile breath = new MovingThrownProjectile(entity.getEntityWorld(), (EntityLivingBase)entity, d2, d3, d4, bcolor);
            breath.setPosition((double)headPosX, (double)headPosY, (double)headPosZ);
            breath.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0F, 1.5F, 1.0F);
            world.spawnEntity(breath.setColor(bcolor));
         }
      }

      ++this.breathStage;
      return true;
   }

   @SideOnly(Side.CLIENT)
   public String getKey() {
      return ModKeyBindings.RACE_ABILITY.getDisplayName();
   }

   @SideOnly(Side.CLIENT)
   public String getAuxKey() {
      return ModKeyBindings.AUX_KEY.getDisplayName();
   }

   public boolean onKeyPress(Entity entity, boolean Aux) {
      MagicStats magic = Capabilities.getMagicStats(entity);
      if (magic != null) {
         return magic.getMana() >= TrinketsConfig.SERVER.races.dragon.breath_cost;
      } else {
         return true;
      }
   }

   public boolean onKeyDown(Entity entity, boolean Aux) {
      return this.DragonBreath(entity);
   }

   public boolean onKeyRelease(Entity entity, boolean Aux) {
      if (this.breathStage != 0.0D) {
         this.breathStage = 0.0D;
      }

      return true;
   }
}
