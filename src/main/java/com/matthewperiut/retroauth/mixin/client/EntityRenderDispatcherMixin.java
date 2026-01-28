package com.matthewperiut.retroauth.mixin.client;

import com.matthewperiut.retroauth.skin.data.PlayerEntityRendererSkinData;
import com.matthewperiut.retroauth.skin.data.PlayerEntitySkinData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Unique
    private final Map<String, PlayerRenderer> playerRenderers = new HashMap<>();
    @Shadow
    private Map<Class<? extends Entity>, EntityRenderer> renderers;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        for (String model : Arrays.asList("default", "slim")) {
            PlayerRenderer renderer = new PlayerRenderer();
            ((PlayerEntityRendererSkinData) renderer).setThinArms(model == "slim");
            renderer.init((EntityRenderDispatcher) (Object) this);
            playerRenderers.put(model, renderer);
        }

        this.renderers.put(Player.class, playerRenderers.get("default"));
    }

    @Inject(method = "getRenderer(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/client/renderer/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    private void onGet(Entity entity, CallbackInfoReturnable<EntityRenderer> cir) {
        if (entity instanceof Player player) {
            String textureModel = ((PlayerEntitySkinData) player).getTextureModel();
            cir.setReturnValue(playerRenderers.get(textureModel));
        }
    }
}
