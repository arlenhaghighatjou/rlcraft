package com.github.fnar.util;

import net.lax1dude.eaglercraft.v1_8.HString;

public class ReportThisIssueException extends RuntimeException {
   public ReportThisIssueException(Throwable cause) {
      super(HString.format("Please report this issue at %s: ", "https://github.com/fnar/minecraft-roguelike/issues"), cause);
   }
}
