package noppes.vc.client;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VCSounds {
   public static final SoundEvent GUN_PISTAL_SHOT = new VCSoundEvent(new ResourceLocation("variedcommodities", "gun.pistol.shot"));
   public static final SoundEvent GUN_PISTAL_TRIGGER = new VCSoundEvent(new ResourceLocation("variedcommodities", "gun.pistol.trigger"));
   public static final SoundEvent GUN_EMPTY = new VCSoundEvent(new ResourceLocation("variedcommodities", "gun.empty"));
   public static final SoundEvent GUN_AK47_LOAD = new VCSoundEvent(new ResourceLocation("variedcommodities", "gun.ak47.load"));
   public static final SoundEvent MAGIC_SHOT = new VCSoundEvent(new ResourceLocation("variedcommodities", "magic.shot"));
   public static final SoundEvent MAGIC_CHARGE = new VCSoundEvent(new ResourceLocation("variedcommodities", "magic.charge"));
   public static final SoundEvent GENERIC_SWOSH = new VCSoundEvent(new ResourceLocation("variedcommodities", "misc.swosh"));
   public static final SoundEvent HALLELUJAH = new VCSoundEvent(new ResourceLocation("variedcommodities", "misc.hallelujah"));

   public VCSounds() {
   }

   @SubscribeEvent
   public void registerItems(Register<SoundEvent> event) {
      event.getRegistry().register(GUN_PISTAL_SHOT);
      event.getRegistry().register(GUN_PISTAL_TRIGGER);
      event.getRegistry().register(GUN_EMPTY);
      event.getRegistry().register(GUN_AK47_LOAD);
      event.getRegistry().register(MAGIC_SHOT);
      event.getRegistry().register(MAGIC_CHARGE);
      event.getRegistry().register(GENERIC_SWOSH);
      event.getRegistry().register(HALLELUJAH);
   }
}
