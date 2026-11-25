package svenhjol.meson.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class VillagerRegistry {
   public static VillagerProfession farmerProfession;
   public static VillagerProfession librarianProfession;
   public static VillagerProfession priestProfession;
   public static VillagerProfession smithProfession;
   public static VillagerProfession butcherProfession;
   public static VillagerProfession nitwitProfession;
   public static VillagerCareer farmer;
   public static VillagerCareer fisherman;
   public static VillagerCareer shepherd;
   public static VillagerCareer fletcher;
   public static VillagerCareer librarian;
   public static VillagerCareer cartographer;
   public static VillagerCareer cleric;
   public static VillagerCareer smith;
   public static VillagerCareer weapon;
   public static VillagerCareer tool;
   public static VillagerCareer butcher;
   public static VillagerCareer leatherWorker;
   public static VillagerCareer nitwit;

   public VillagerRegistry() {
   }

   @SubscribeEvent
   public static void onRegisterVillagers(Register<VillagerProfession> event) {
      butcherProfession = (VillagerProfession)event.getRegistry().getValue(new ResourceLocation("minecraft:butcher"));
      farmerProfession = (VillagerProfession)event.getRegistry().getValue(new ResourceLocation("minecraft:farmer"));
      priestProfession = (VillagerProfession)event.getRegistry().getValue(new ResourceLocation("minecraft:priest"));
      smithProfession = (VillagerProfession)event.getRegistry().getValue(new ResourceLocation("minecraft:smith"));
      librarianProfession = (VillagerProfession)event.getRegistry().getValue(new ResourceLocation("minecraft:librarian"));
      nitwitProfession = (VillagerProfession)event.getRegistry().getValue(new ResourceLocation("minecraft:nitwit"));
      if (farmerProfession != null) {
         farmer = farmerProfession.getCareer(0);
         fisherman = farmerProfession.getCareer(1);
         shepherd = farmerProfession.getCareer(2);
         fletcher = farmerProfession.getCareer(3);
      }

      if (librarianProfession != null) {
         librarian = librarianProfession.getCareer(0);
         cartographer = librarianProfession.getCareer(1);
      }

      if (priestProfession != null) {
         cleric = priestProfession.getCareer(0);
      }

      if (butcherProfession != null) {
         butcher = butcherProfession.getCareer(0);
         leatherWorker = butcherProfession.getCareer(1);
      }

      if (smithProfession != null) {
         smith = smithProfession.getCareer(0);
         weapon = smithProfession.getCareer(1);
         tool = smithProfession.getCareer(2);
      }

      if (nitwitProfession != null) {
         nitwit = nitwitProfession.getCareer(0);
      }

   }
}
