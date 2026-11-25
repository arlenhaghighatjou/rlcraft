package materialtweaker.mixin;

import materialtweaker.core.MaterialTweaker;
import materialtweaker.handlers.ForgeConfigHandler;
import net.minecraftforge.common.util.EnumHelper;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({EnumHelper.class})
public abstract class EnumHelperMixin {
   public EnumHelperMixin() {
   }

   @ModifyArgs(
      method = {"addToolMaterial"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraftforge/common/util/EnumHelper;addEnum(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Enum;"
),
      remap = false
   )
   private static void materialtweaker_addToolMaterial(Args args) {
      if (ForgeConfigHandler.server.printInfo) {
         try {
            String name = (String)args.get(1);
            Object[] properties = args.get(2);
            MaterialTweaker.LOGGER.log(Level.INFO, "materialtweaker: Tool Material Registering, Name: " + name + " HarvestLevel: " + properties[0] + " MaxUses: " + properties[1] + " Efficiency: " + properties[2] + " Damage: " + properties[3] + " Enchantability: " + properties[4]);
         } catch (Exception var3) {
            MaterialTweaker.LOGGER.log(Level.ERROR, "materialtweaker: Tool Material Attribute printing failed: " + var3);
         }
      }

   }

   @ModifyArgs(
      method = {"addArmorMaterial"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraftforge/common/util/EnumHelper;addEnum(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Enum;"
),
      remap = false
   )
   private static void materialtweaker_addArmorMaterial(Args args) {
      if (ForgeConfigHandler.server.printInfo) {
         try {
            String name = (String)args.get(1);
            Object[] properties = args.get(2);
            MaterialTweaker.LOGGER.log(Level.INFO, "materialtweaker: Armor Material Registering, Name: " + name + " DurabilityFactor: " + properties[1] + " ReductionAmounts: " + ((int[])((int[])properties[2]))[0] + " " + ((int[])((int[])properties[2]))[1] + " " + ((int[])((int[])properties[2]))[2] + " " + ((int[])((int[])properties[2]))[3] + " Enchantability: " + properties[3] + " Toughness: " + properties[5]);
         } catch (Exception var3) {
            MaterialTweaker.LOGGER.log(Level.ERROR, "materialtweaker: Armor Material Attribute printing failed: " + var3);
         }
      }

   }
}
