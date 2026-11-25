package org.orecruncher.dsurround.server.services;

import javax.annotation.Nonnull;

public abstract class Service {
   protected final String serviceName;

   protected Service(@Nonnull String serviceName) {
      this.serviceName = serviceName;
   }

   public String getServiceName() {
      return this.serviceName;
   }

   public void init() {
   }

   public void fini() {
   }
}
