package xzeroair.trinkets.races;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import xzeroair.trinkets.Registries;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.races.dragon.RaceDragon;
import xzeroair.trinkets.races.dragon.RaceDragonAttributes;
import xzeroair.trinkets.races.dwarf.RaceDwarf;
import xzeroair.trinkets.races.dwarf.RaceDwarfAttributes;
import xzeroair.trinkets.races.elf.RaceElf;
import xzeroair.trinkets.races.elf.RaceElfAttributes;
import xzeroair.trinkets.races.faelis.RaceFaelis;
import xzeroair.trinkets.races.faelis.RaceFaelisAttributes;
import xzeroair.trinkets.races.fairy.RaceFairy;
import xzeroair.trinkets.races.fairy.RaceFairyAttributes;
import xzeroair.trinkets.races.goblin.RaceGoblin;
import xzeroair.trinkets.races.goblin.RaceGoblinAttributes;
import xzeroair.trinkets.races.human.RaceHuman;
import xzeroair.trinkets.races.titan.RaceTitan;
import xzeroair.trinkets.races.titan.RaceTitanAttributes;
import xzeroair.trinkets.traits.elements.IElementProvider;

public class EntityRace extends Impl<EntityRace> implements IElementProvider {
   public static final ForgeRegistry<EntityRace> Registry = Registries.getRaceRegistry();
   protected final EaglercraftUUID uuid;
   protected final String name;
   protected int primaryColor;
   protected int secondaryColor;
   protected int magicAffinityValue;
   protected int raceHeight;
   protected int raceWidth;
   protected boolean canFly;

   private EntityRace(String name, String uuid, int color1, int color2, boolean internal) {
      this(name, uuid, color1, color2);
      this.setRegistryName(name);
   }

   public EntityRace(String name, String uuid, int color1, int color2) {
      this.primaryColor = 3289650;
      this.secondaryColor = 16777215;
      this.magicAffinityValue = 100;
      this.raceHeight = 100;
      this.raceWidth = 100;
      this.canFly = false;
      this.name = name;
      this.uuid = EaglercraftUUID.fromString(uuid);
      this.primaryColor = color1;
      this.secondaryColor = color2;
   }

   public static int getIdFromRace(EntityRace race) {
      return race == null ? 0 : Registry.getID(race);
   }

   public static EntityRace getRaceById(int id) {
      return (EntityRace)Registry.getValue(id);
   }

   public static EntityRace getByUUID(EaglercraftUUID uuid) {
      for(EntityRace race : Registry.getValuesCollection()) {
         if (race.getUUID().compareTo(uuid) == 0) {
            return race;
         }
      }

      return EntityRaces.none;
   }

   @Nullable
   public static EntityRace getByNameOrId(String id) {
      EntityRace race = (EntityRace)Registry.getValue(new ResourceLocation(id.toLowerCase()));
      if (race == null) {
         try {
            return getRaceById(Integer.parseInt(id));
         } catch (NumberFormatException var3) {
            ;
         }
      }

      return race;
   }

   public String getName() {
      return this.name;
   }

   public EaglercraftUUID getUUID() {
      return this.uuid;
   }

   public int getRaceHeight() {
      return this.raceHeight;
   }

   public EntityRace setRaceHeight(int raceHeight) {
      this.raceHeight = raceHeight;
      return this;
   }

   public int getRaceWidth() {
      return this.raceWidth;
   }

   public EntityRace setRaceWidth(int raceWidth) {
      this.raceWidth = raceWidth;
      return this;
   }

   public EntityRace setRaceSize(int sizeV) {
      return this.setRaceHeight(sizeV).setRaceWidth(sizeV);
   }

   public int getMagicAffinity() {
      return this.magicAffinityValue;
   }

   public EntityRace setMagicAffinity(int magicAffinity) {
      this.magicAffinityValue = magicAffinity;
      return this;
   }

   public boolean canFly() {
      return this.canFly;
   }

   public EntityRace setCanFly(boolean canFly) {
      this.canFly = canFly;
      return this;
   }

   public int getPrimaryColor() {
      return this.primaryColor;
   }

   public int getSecondaryColor() {
      return this.secondaryColor;
   }

   public EntityRacePropertiesHandler getRaceHandler(EntityLivingBase e) {
      String var2 = this.name;
      byte var3 = -1;
      switch(var2.hashCode()) {
      case 69759:
         if (var2.equals("Elf")) {
            var3 = 4;
         }
         break;
      case 66441410:
         if (var2.equals("Dwarf")) {
            var3 = 1;
         }
         break;
      case 67640757:
         if (var2.equals("Fairy")) {
            var3 = 0;
         }
         break;
      case 70086925:
         if (var2.equals("Human")) {
            var3 = 7;
         }
         break;
      case 80818412:
         if (var2.equals("Titan")) {
            var3 = 2;
         }
         break;
      case 2055055923:
         if (var2.equals("Dragon")) {
            var3 = 6;
         }
         break;
      case 2096738156:
         if (var2.equals("Faelis")) {
            var3 = 5;
         }
         break;
      case 2138207223:
         if (var2.equals("Goblin")) {
            var3 = 3;
         }
      }

      switch(var3) {
      case 0:
         return new RaceFairy(e);
      case 1:
         return new RaceDwarf(e);
      case 2:
         return new RaceTitan(e);
      case 3:
         return new RaceGoblin(e);
      case 4:
         return new RaceElf(e);
      case 5:
         return new RaceFaelis(e);
      case 6:
         return new RaceDragon(e);
      case 7:
         return new RaceHuman(e);
      default:
         return new EmptyHandler(e);
      }
   }

   public RaceAttributesWrapper getRaceAttributes() {
      String var1 = this.name;
      byte var2 = -1;
      switch(var1.hashCode()) {
      case 69759:
         if (var1.equals("Elf")) {
            var2 = 4;
         }
         break;
      case 66441410:
         if (var1.equals("Dwarf")) {
            var2 = 1;
         }
         break;
      case 67640757:
         if (var1.equals("Fairy")) {
            var2 = 0;
         }
         break;
      case 70086925:
         if (var1.equals("Human")) {
            var2 = 7;
         }
         break;
      case 80818412:
         if (var1.equals("Titan")) {
            var2 = 2;
         }
         break;
      case 2055055923:
         if (var1.equals("Dragon")) {
            var2 = 6;
         }
         break;
      case 2096738156:
         if (var1.equals("Faelis")) {
            var2 = 5;
         }
         break;
      case 2138207223:
         if (var1.equals("Goblin")) {
            var2 = 3;
         }
      }

      switch(var2) {
      case 0:
         return new RaceFairyAttributes();
      case 1:
         return new RaceDwarfAttributes();
      case 2:
         return new RaceTitanAttributes();
      case 3:
         return new RaceGoblinAttributes();
      case 4:
         return new RaceElfAttributes();
      case 5:
         return new RaceFaelisAttributes();
      case 6:
         return new RaceDragonAttributes();
      case 7:
         return new RaceAttributesWrapper();
      default:
         return new RaceAttributesWrapper();
      }
   }

   public boolean isNone() {
      return this.name.contentEquals("None");
   }

   public static void registerRaces() {
      registerRace((new EntityRace("None", "00000000-0000-0000-0000-000000000000", 11107684, 16374701, true)).setRaceSize(100).setMagicAffinity(100));
      registerRace((new EntityRace("Human", "c82ec7c3-2a9d-4a08-b0dd-7ce086c6771b", 11107684, 16374701, true)).setRaceSize(100).setMagicAffinity(100));
      registerRace((new EntityRace("Fairy", "e5869fac-0949-41f2-889b-4e6b8ca6d2e7", 12514535, 962222, true)).setRaceSize(25).setCanFly(true).setMagicAffinity(500));
      registerRace((new EntityRace("Dwarf", "917b555b-944a-4e44-afb6-ca638c6d91e5", 10832170, 7039851, true)).setRaceSize(75).setMagicAffinity(100));
      registerRace((new EntityRace("Titan", "a3bc433b-7bb7-4bd9-a88c-5fd120d04d59", 10066329, 3223595, true)).setRaceSize(300).setMagicAffinity(50));
      registerRace((new EntityRace("Elf", "25f92404-35f3-453b-ad48-9b788b2e12fc", 16374701, 11107684, true)).setRaceSize(100).setMagicAffinity(200));
      registerRace((new EntityRace("Goblin", "d917999a-0399-4c39-bfc5-79784dfff6ed", 6588004, 3096367, true)).setRaceSize(50).setMagicAffinity(75));
      registerRace((new EntityRace("Faelis", "cdccefa8-6a67-4394-b70d-c737953887a2", 16571252, 4465933, true)).setRaceSize(85).setMagicAffinity(125));
      registerRace((new EntityRace("Dragon", "3b75821e-6ec6-4dfe-9612-b7a988a7b30b", 3289650, 9509561, true)).setRaceSize(120).setCanFly(true).setMagicAffinity(400));
   }

   protected static void registerRace(EntityRace race) {
      Registry.register(race);
   }
}
