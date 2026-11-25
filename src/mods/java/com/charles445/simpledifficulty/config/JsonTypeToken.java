package com.charles445.simpledifficulty.config;

import com.charles445.simpledifficulty.api.config.json.JsonConsumableTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureIdentity;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonConsumableTemperatureMigrate;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonConsumableThirstMigrate;
import com.charles445.simpledifficulty.api.config.json.migrate.JsonTemperatureMetadataMigrate;
import com.charles445.simpledifficulty.config.json.ExtraItem;
import com.charles445.simpledifficulty.config.json.MaterialTemperature;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JsonTypeToken {
   public JsonTypeToken() {
   }

   public static Type get(JsonFileName jcfn) {
      switch(jcfn) {
      case armorTemperatures:
         return (new TypeToken<Map<String, List<JsonTemperatureIdentity>>>() {
         }).getType();
      case blockTemperatures:
         return (new TypeToken<Map<String, List<JsonPropertyTemperature>>>() {
         }).getType();
      case consumableTemperature:
         return (new TypeToken<Map<String, List<JsonConsumableTemperature>>>() {
         }).getType();
      case consumableThirst:
         return (new TypeToken<Map<String, List<JsonConsumableThirst>>>() {
         }).getType();
      case dimensionTemperature:
         return (new TypeToken<Map<String, JsonTemperature>>() {
         }).getType();
      case fluidTemperatures:
         return (new TypeToken<Map<String, JsonTemperature>>() {
         }).getType();
      case heldItemTemperatures:
         return (new TypeToken<Map<String, List<JsonTemperatureIdentity>>>() {
         }).getType();
      case materialTemperature:
         return (new TypeToken<MaterialTemperature>() {
         }).getType();
      case extraItems:
         return (new TypeToken<Map<String, ExtraItem>>() {
         }).getType();
      case armorTemperatures_MIGRATE:
         return (new TypeToken<Map<String, JsonTemperature>>() {
         }).getType();
      case consumableTemperature_MIGRATE:
         return (new TypeToken<Map<String, List<JsonConsumableTemperatureMigrate>>>() {
         }).getType();
      case consumableThirst_MIGRATE:
         return (new TypeToken<Map<String, List<JsonConsumableThirstMigrate>>>() {
         }).getType();
      case heldItemTemperatures_MIGRATE:
         return (new TypeToken<Map<String, List<JsonTemperatureMetadataMigrate>>>() {
         }).getType();
      default:
         return null;
      }
   }
}
