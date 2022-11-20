package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public class MineCellsBlockTags {
  public static final TagKey<Block> CONJUNCTIVIUS_UNBREAKABLE = TagKey.of(Registry.BLOCK_KEY, MineCells.createId("conjunctivius_unbreakable"));
}