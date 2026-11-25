package com.charles445.aireducer.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;

public class VanillaConfig {
   @Comment({"Master switch for this handler"})
   @Name("ENABLED")
   public boolean ENABLED = true;
   @Comment({"Replace vanilla entity avoid tasks for better performance"})
   @Name("Avoid Task Replacement")
   public boolean avoidTaskReplacement = true;
   @Comment({"Should vanilla rabbit AI be tweaked"})
   @Name("Rabbit")
   public boolean rabbit = true;
   @Comment({"Delay for rabbits checking if they are scared - Vanilla is 1"})
   @Name("RabbitAvoidDelay")
   @RangeInt(
      min = 1,
      max = 10
   )
   public int rabbit_should_avoid = 2;

   public VanillaConfig() {
   }
}
