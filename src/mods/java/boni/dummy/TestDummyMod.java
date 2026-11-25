package boni.dummy;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Arrays;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "testdummy",
   version = "1.12",
   name = "MmmMmmMmmMmm",
   acceptedMinecraftVersions = "[1.12,1.13)"
)
public class TestDummyMod {
   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new boni.dummy.ClientProxy() : new boni.dummy.CommonProxy();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      }

   }

   public static final String MODID = "testdummy";
   public static final String VERSION = "1.12";
   public static final Logger log = LogManager.getLogger("testdummy");
   public static TestDummyMod instance;
   public static CommonProxy proxy;
   public static Item itemDummy = new ItemDummy();

   public TestDummyMod() {
      log.info("Please don't hurt me. :S");
      EntityArrow.ARROW_TARGETS = Predicates.and(Arrays.asList(EntitySelectors.NOT_SPECTATING, (entity) -> {
         return entity.canBeCollidedWith();
      }, (entity) -> {
         return entity.isEntityAlive() || entity instanceof EntityDummy;
      }));
   }

   public void preInit(FMLPreInitializationEvent event) {
      this.register(MinecraftForge.EVENT_BUS);
      EntityRegistry.registerModEntity(new ResourceLocation("testdummy", "dummy"), EntityDummy.class, "Dummy", 0, instance, 128, 10, false, EntityDummy::new);
      EntityRegistry.registerModEntity(new ResourceLocation("testdummy", "floating_number"), EntityFloatingNumber.class, "FloatingNumber", 1, instance, 128, 1, false, EntityFloatingNumber::new);
      EntityRegistry.registerModEntity(new ResourceLocation("testdummy", "floating_number_dps"), EntityDpsFloatingNumber.class, "FloatingNumberDPS", 2, instance, 128, 1, false, EntityDpsFloatingNumber::new);
      proxy.preinit();
   }

   public void init(FMLInitializationEvent event) {
      proxy.init();
   }

   public void register(EventBus bus) {
      bus.registerGeneric(Register.class, Item.class, this::register);
   }

   public void register(Register<Item> e) {
      e.getRegistry().register(itemDummy);
      GameRegistry.addShapedRecipe(new ResourceLocation("testdummy", "dummy"), (ResourceLocation)null, new ItemStack(itemDummy), new Object[]{" B ", "HWH", " P ", 'B', Item.getItemFromBlock(Blocks.HAY_BLOCK), 'H', Items.WHEAT, 'W', new ItemStack(Blocks.WOOL, 0, 32767), 'P', "plankWood"});
   }
}
