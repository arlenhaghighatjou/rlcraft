package com.endertech.minecraft.mods.adhooks.init;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ColorARGB;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.mods.adhooks.Main;
import com.endertech.minecraft.mods.adhooks.items.Hook;
import com.endertech.minecraft.mods.adhooks.items.Launcher;
import com.endertech.minecraft.mods.adhooks.items.Properties;
import com.endertech.minecraft.mods.adhooks.items.Rope;
import com.google.common.collect.UnmodifiableIterator;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@EventBusSubscriber
public enum HookType implements IForgeEnum {
   PROTOTYPE(new Properties()),
   PUDGE((new Properties()).launcher(300, 0.7F, 1.0F).hook(10.0F, 0.0F).rope(32, 0.0F, 3.0F, -4259840)),
   SPEAR((new Properties()).launcher(180, 0.5F, 1.0F).hook(5.0F, 2.0F).rope(64, 0.6F, 4.0F, -1090519040)),
   WEB((new Properties()).launcher(240, 0.8F, 1.0F).hook(2.0F, 0.0F).rope(76, 0.95F, 6.0F, -1));

   final Properties props;
   public final Hook hook;
   public final Rope rope;
   public final Launcher launcher;

   private HookType(Properties props) {
      props.hookType = this;
      this.props = props;
      ForgeMod mod = Main.instance;
      Path configDir = mod.getConfig().getConfigDir();
      UnitConfig hookConfig = new UnitConfig(configDir, "hooks", this.getName());
      this.hook = new Hook(mod, hookConfig, this, props.hookStrength, props.hookDamage, props.hookStrength);
      UnitConfig launcherConfig = new UnitConfig(configDir, "launchers", this.getName());
      this.rope = new Rope(launcherConfig, props.ropeMaxLength, props.ropeWidth, props.ropeElasticity, ColorARGB.from(props.ropeColorARGB));
      this.launcher = new Launcher(mod, this.getName(), launcherConfig, this, props.durability, props.reelingSpeed, props.boostStrength);
      hookConfig.save();
      launcherConfig.save();
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      EntityPlayer player = event.player;
      if (player instanceof EntityPlayerMP) {
         for(HookType type : values()) {
            Main.instance.getConnection().sendToPlayer(type.props.readValues(), (EntityPlayerMP)player);
         }
      }

   }

   @SubscribeEvent
   public static void remapMissingItems(MissingMappings<Item> event) {
      Map<String, Hook> hookMap = new HashMap();
      Map<String, Launcher> launcherMap = new HashMap();

      for(HookType element : values()) {
         hookMap.put("hook" + element.name().toLowerCase(Locale.ROOT), element.hook);
         launcherMap.put("hooklauncher" + element.name().toLowerCase(Locale.ROOT), element.launcher);
      }

      UnmodifiableIterator var8 = event.getMappings().iterator();

      while(var8.hasNext()) {
         Mapping<Item> mapping = (Mapping)var8.next();
         String oldName = mapping.key.getResourcePath().toLowerCase(Locale.ROOT);
         Hook hook = (Hook)hookMap.get(oldName);
         Launcher launcher = (Launcher)launcherMap.get(oldName);
         if (hook != null) {
            mapping.remap(hook);
         }

         if (launcher != null) {
            mapping.remap(launcher);
         }
      }

   }

   public String getName() {
      return this.getLowerCase(this.name());
   }
}
