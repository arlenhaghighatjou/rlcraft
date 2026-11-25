package org.orecruncher.dsurround.registry.effect;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.config.EntityConfig;

@SideOnly(Side.CLIENT)
public class EntityEffectInfo {
   public final String effects;
   public final String variator;

   public EntityEffectInfo() {
      this.effects = "";
      this.variator = "default";
   }

   public EntityEffectInfo(@Nonnull EntityConfig ec) {
      this.effects = ec.effects;
      this.variator = ec.variator;
   }

   public String toString() {
      if (!this.effects.isEmpty()) {
         StringBuilder builder = new StringBuilder();
         builder.append(this.effects);
         builder.append("; variator=").append(this.variator);
         return builder.toString();
      } else {
         return "<NONE>";
      }
   }
}
