package com.lycanitesmobs.client.obj;

import net.minecraftforge.fml.common.eventhandler.Event;

public class TessellatorModelEvent extends Event {
   public TessellatorModel model;

   public TessellatorModelEvent(TessellatorModel model) {
      this.model = model;
   }

   public boolean isCancelable() {
      return true;
   }

   public static class RenderGroupEvent extends TessellatorModelEvent {
      public String group;

      public RenderGroupEvent(String groupName, TessellatorModel model) {
         super(model);
         this.group = groupName;
      }

      public static class Post extends TessellatorModelEvent.RenderGroupEvent {
         public Post(String g, TessellatorModel m) {
            super(g, m);
         }
      }

      public static class Pre extends TessellatorModelEvent.RenderGroupEvent {
         public Pre(String g, TessellatorModel m) {
            super(g, m);
         }
      }
   }

   public static class RenderPost extends TessellatorModelEvent {
      public RenderPost(TessellatorModel model) {
         super(model);
      }
   }

   public static class RenderPre extends TessellatorModelEvent {
      public RenderPre(TessellatorModel model) {
         super(model);
      }
   }
}
