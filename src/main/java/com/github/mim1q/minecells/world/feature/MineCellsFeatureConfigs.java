package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.world.feature.tree.PromenadeShrubTrunkPlacer;
import com.github.mim1q.minecells.world.feature.tree.PromenadeTreeTrunkPlacer;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BushFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.TrunkPlacer;

public class MineCellsFeatureConfigs {
  public static final TreeFeatureConfig PROMENADE_TREE_CONFIG = simpleTree(
    new PromenadeTreeTrunkPlacer(30, 20, 0)
  );
  public static final TreeFeatureConfig PROMENADE_SHRUB_CONFIG = shrub(
    new PromenadeShrubTrunkPlacer(1, 0, 0)
  );

  private static TreeFeatureConfig simpleTree(TrunkPlacer trunkPlacer) {
    return new TreeFeatureConfig.Builder(
      BlockStateProvider.of(MineCellsBlocks.PUTRID_LOG.getDefaultState()),
      trunkPlacer,
      BlockStateProvider.of(MineCellsBlocks.WILTED_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true)),
      new BushFoliagePlacer(UniformIntProvider.create(1, 2), ConstantIntProvider.create(0), 2),
      new TwoLayersFeatureSize(1, 0, 1)
    ).ignoreVines().build();
  }

  private static TreeFeatureConfig shrub(TrunkPlacer trunkPlacer) {
    return new TreeFeatureConfig.Builder(
      BlockStateProvider.of(MineCellsBlocks.PUTRID_LOG.getDefaultState()),
      trunkPlacer,
      BlockStateProvider.of(MineCellsBlocks.WILTED_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true)),
      new BushFoliagePlacer(UniformIntProvider.create(1, 2), ConstantIntProvider.create(0), 2),
      new TwoLayersFeatureSize(1, 0, 1)
    ).ignoreVines().build();
  }
}
