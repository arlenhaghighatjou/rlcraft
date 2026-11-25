package boni.dummy;

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
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "testdummy",
   version = "1.12",
   name = "MmmMmmMmmMmm",
   acceptedMinecraftVersions = "[1.12,1.13)"
)
public class TestDummyMod {
   public static final String MODID = "testdummy";
   public static final String VERSION = "1.12";
   public static final Logger log = LogManager.getLogger("testdummy");
   @Instance
   public static TestDummyMod instance;
   @SidedProxy(
      clientSide = "boni.dummy.ClientProxy",
      serverSide = "boni.dummy.CommonProxy"
   )
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

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(this);
      EntityRegistry.registerModEntity(new ResourceLocation("testdummy", "dummy"), EntityDummy.class, "Dummy", 0, instance, 128, 10, false);
      EntityRegistry.registerModEntity(new ResourceLocation("testdummy", "floating_number"), EntityFloatingNumber.class, "FloatingNumber", 1, instance, 128, 1, false);
      EntityRegistry.registerModEntity(new ResourceLocation("testdummy", "floating_number_dps"), EntityDpsFloatingNumber.class, "FloatingNumberDPS", 2, instance, 128, 1, false);
      proxy.preinit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init();
   }

   @SubscribeEvent
   public void register(Register<Item> e) {
      e.getRegistry().register(itemDummy);
      GameRegistry.addShapedRecipe(new ResourceLocation("testdummy", "dummy"), (ResourceLocation)null, new ItemStack(itemDummy), new Object[]{" B ", "HWH", " P ", 'B', Item.getItemFromBlock(Blocks.HAY_BLOCK), 'H', Items.WHEAT, 'W', new ItemStack(Blocks.WOOL, 0, 32767), 'P', "plankWood"});
   }
}
