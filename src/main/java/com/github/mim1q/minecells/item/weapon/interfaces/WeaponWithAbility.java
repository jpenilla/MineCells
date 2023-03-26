package com.github.mim1q.minecells.item.weapon.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public interface WeaponWithAbility {
  String HOLD_KEY = "item.minecells.hold";
  String ABILITY_COOLDOWN_KEY = "item.minecells.ability_cooldown";
  String ABILITY_DAMAGE_KEY = "item.minecells.ability_damage";

  float getBaseAbilityDamage(ItemStack stack);
  default float getAbilityDamage(ItemStack stack) {
    return getBaseAbilityDamage(stack);
  }
  int getBaseAbilityCooldown(ItemStack stack);
  default int getAbilityCooldown(ItemStack stack) {
    return getBaseAbilityCooldown(stack);
  }
  private void fillTooltip(List<Text> tooltip, boolean hold, MutableText description, ItemStack stack) {
    var text = description.getString();
    var lines = text.split("\n");
    for (var line : lines) {
      tooltip.add(Text.literal(line).formatted(Formatting.GRAY));
    }
    var key = Text.keybind("key.use");
    var stringBuilder = new StringBuilder().append("(");
    if (hold) {
      stringBuilder.append(Text.translatable(HOLD_KEY).getString());
    }
    tooltip.add(Text.literal(stringBuilder.append(key.getString()).append(")").toString()).formatted(Formatting.DARK_GRAY));

    var damage = getAbilityDamage(stack);
    if (damage > 0) {
      tooltip.add(Text.translatable(ABILITY_DAMAGE_KEY, getAbilityDamage(stack)).formatted(Formatting.DARK_GRAY));
    }
    tooltip.add(Text.translatable(ABILITY_COOLDOWN_KEY, getAbilityCooldown(stack) / 20.0F).formatted(Formatting.DARK_GRAY));
  }

  default void fillTooltip(List<Text> lines, boolean hold, String descriptionKey, ItemStack stack) {
    fillTooltip(lines, hold, Text.translatable(descriptionKey), stack);
  }
}
