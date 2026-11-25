package ichttt.mods.firstaid.client;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.client.gui.GuiHealthScreen;
import ichttt.mods.firstaid.client.util.EventCalendar;
import ichttt.mods.firstaid.common.IProxy;
import ichttt.mods.firstaid.common.config.ConfigEntry;
import ichttt.mods.firstaid.common.config.ExtraConfig;
import ichttt.mods.firstaid.common.config.ExtraConfigManager;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy implements IProxy {
   public static final KeyBinding showWounds = new KeyBinding("keybinds.show_wounds", KeyConflictContext.IN_GAME, 35, "First Aid");
   public static List<ConfigEntry<ExtraConfig.Advanced>> advancedConfigOptions;

   public ClientProxy() {
   }

   public void preInit() {
      FirstAid.LOGGER.debug("Loading ClientProxy");
      MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
      ClientRegistry.registerKeyBinding(showWounds);
   }

   public void init() {
      EventCalendar.checkDate();
      ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(HUDHandler.INSTANCE);
      advancedConfigOptions = ExtraConfigManager.getAnnotatedFields(ExtraConfig.Advanced.class, FirstAidConfig.class);
   }

   public void showGuiApplyHealth(EnumHand activeHand) {
      Minecraft mc = Minecraft.getMinecraft();
      GuiHealthScreen.INSTANCE = new GuiHealthScreen((AbstractPlayerDamageModel)mc.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null), activeHand);
      mc.displayGuiScreen(GuiHealthScreen.INSTANCE);
   }
}
