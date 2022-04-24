package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.model.projectile.GrenadeEntityModel;
import com.github.mim1q.minecells.client.renderer.GrenadierEntityRenderer;
import com.github.mim1q.minecells.client.renderer.JumpingZombieEntityRenderer;
import com.github.mim1q.minecells.client.renderer.ShockerEntityRenderer;
import com.github.mim1q.minecells.client.renderer.projectile.GrenadeEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class RendererRegistry {

    public static final EntityModelLayer GRENADE_LAYER = new EntityModelLayer(new Identifier(MineCells.MOD_ID, "grenade_render_layer"), "grenade_render_layer");

    public static void register() {
        EntityRendererRegistry.register(EntityRegistry.JUMPING_ZOMBIE, JumpingZombieEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.SHOCKER, ShockerEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.GRENADIER, GrenadierEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.GRENADE, GrenadeEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(GRENADE_LAYER, GrenadeEntityModel::getTexturedModelData);
    }
}
