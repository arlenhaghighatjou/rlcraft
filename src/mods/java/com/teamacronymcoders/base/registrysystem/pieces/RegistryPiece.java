package com.teamacronymcoders.base.registrysystem.pieces;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

@Retention(RetentionPolicy.RUNTIME)
public @interface RegistryPiece {
   RegistrySide value() default RegistrySide.BOTH;

   String modid() default "";

   EventPriority priority() default EventPriority.NORMAL;
}
