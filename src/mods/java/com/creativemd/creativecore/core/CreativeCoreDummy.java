package com.creativemd.creativecore.core;

import com.google.common.eventbus.EventBus;
import java.util.Arrays;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class CreativeCoreDummy extends DummyModContainer {
   public static final String modid = "creativecoredummy";
   public static final String version = "1.0.0";

   public CreativeCoreDummy() {
      super(new ModMetadata());
      ModMetadata meta = this.getMetadata();
      meta.modId = "creativecoredummy";
      meta.name = "CreativeCoreDummy";
      meta.version = "1.0.0";
      meta.credits = "CreativeMD";
      meta.authorList = Arrays.asList("CreativeMD");
      meta.description = "";
      meta.url = "";
      meta.screenshots = new String[0];
      meta.logoFile = "";
   }

   public boolean registerBus(EventBus bus, LoadController controller) {
      bus.register(this);
      return true;
   }
}
