package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.UndeadArcherEntityModel;
import com.github.mim1q.minecells.entity.UndeadArcherEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.util.Identifier;

public class UndeadArcherEntityRenderer extends MobEntityRenderer<UndeadArcherEntity, UndeadArcherEntityModel> {

    private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/undead_archer.png");

    public UndeadArcherEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new UndeadArcherEntityModel(ctx.getPart(RendererRegistry.UNDEAD_ARCHER_LAYER)), 0.35F);
        this.addFeature(new HeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()));
    }

    @Override
    public Identifier getTexture(UndeadArcherEntity entity) {
        return TEXTURE;
    }
}
