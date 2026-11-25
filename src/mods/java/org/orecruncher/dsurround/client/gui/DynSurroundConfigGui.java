package org.orecruncher.dsurround.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.sound.SoundEngine;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.sound.SoundRegistry;
import org.orecruncher.lib.ConfigProcessor;
import org.orecruncher.lib.gui.GuiConfigBase;

@SideOnly(Side.CLIENT)
public class DynSurroundConfigGui extends GuiConfigBase {
   private final Configuration config = ModBase.config();
   private final ConfigCategory soundCategory;

   public DynSurroundConfigGui(GuiScreen parentScreen) {
      super(parentScreen, new ArrayList(), "dsurround", false, false, "Dynamic Surroundings");
      this.titleLine2 = this.config.getConfigFile().getAbsolutePath();
      this.soundCategory = (new ConfigCategory("Individual Sound Configuration")).setLanguageKey("dsurround.cfg.individual");
      ConfigElement soundElement = new ConfigElement(this.soundCategory);
      this.generateSoundList(this.soundCategory);
      this.configElements.add(soundElement);
      this.addConfigCategory("general");
      this.addConfigCategory("player");
      this.addConfigCategory("rain");
      this.addConfigCategory("fog");
      this.addConfigCategory("aurora");
      this.addConfigCategory("effects");
      this.addConfigCategory("biomes");
      this.addConfigCategory("sound");
      this.addConfigCategory("profiles");
      this.addConfigCategory("speechbubbles");
      this.addConfigCategory("commands");
      this.addConfigCategory("asm");
      this.addConfigCategory("logging");
   }

   private void addConfigCategory(@Nonnull String category) {
      ConfigCategory cat = this.config.getCategory(category);
      this.configElements.add(new ConfigElement(cat));
   }

   protected void doFixups() {
      this.saveSoundList();
      this.config.save();
      ConfigProcessor.process(this.config, ModOptions.class);
   }

   protected void saveSoundList() {
      List<String> soundVolumes = new ArrayList();

      for(Entry<String, Property> entry : this.soundCategory.entrySet()) {
         String parms = ((Property)entry.getValue()).getString();
         if (!StringUtils.isEmpty(parms)) {
            soundVolumes.add((String)entry.getKey() + " " + parms);
         }
      }

      String[] results = (String[])soundVolumes.toArray(new String[0]);
      this.config.getCategory("sound").get("Sound Settings").set(results);
   }

   protected void generateSoundList(ConfigCategory cat) {
      cat.setRequiresMcRestart(false);
      cat.setRequiresWorldRestart(false);
      List<String> sounds = new ArrayList();

      for(Object resource : SoundEngine.instance().getSoundRegistry().getKeys()) {
         sounds.add(resource.toString());
      }

      Collections.sort(sounds);
      SoundRegistry registry = RegistryManager.SOUND;

      for(String sound : sounds) {
         Property prop = new Property(sound, "", Type.STRING);
         prop.setDefaultValue("");
         prop.setRequiresMcRestart(false);
         prop.setRequiresWorldRestart(false);
         prop.setConfigEntryClass(SoundConfigEntry.class);
         StringBuilder builder = new StringBuilder();
         ResourceLocation res = new ResourceLocation(sound);
         if (registry.isSoundBlocked(res)) {
            builder.append("block").append(' ');
         }

         if (registry.isSoundCulled(res)) {
            builder.append("cull").append(' ');
         }

         float v = registry.getVolumeScale(res);
         if (v != 1.0F) {
            builder.append((int)(v * 100.0F));
         }

         prop.setValue(builder.toString());
         cat.put(sound, prop);
      }

   }
}
