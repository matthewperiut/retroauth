package com.matthewperiut.retroauth.mixin.client;

import com.matthewperiut.retroauth.skin.CapeImageProcessor;
import com.matthewperiut.retroauth.skin.data.PlayerEntitySkinData;
import com.matthewperiut.retroauth.skin.data.SkinImageProcessorData;
import net.minecraft.client.entity.mob.player.ClientPlayerEntity;
import net.minecraft.client.render.texture.HttpImageProcessor;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Unique
    private Entity currentEntity; // I hate this but there is no way to get it from @ModifyArg

    @Inject(method = "notifyEntityRemoved", at = @At("HEAD"), cancellable = true)
    private void dontUnloadLocalPlayerSkin(Entity entity, CallbackInfo ci) {
        if (entity instanceof ClientPlayerEntity) {
            ci.cancel();
        }
    }

    @Inject(method = "notifyEntityAdded", at = @At("HEAD"))
    private void getEnttity(Entity entity, CallbackInfo ci) {
        currentEntity = entity;
    }

    @ModifyArg(
            method = "notifyEntityAdded", index = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/texture/TextureManager;addHttpTexture(Ljava/lang/String;Lnet/minecraft/client/render/texture/HttpImageProcessor;)Lnet/minecraft/client/render/texture/HttpTexture;", ordinal = 0)
    )
    private HttpImageProcessor redirectSkinProcessor(HttpImageProcessor def) {
        if (currentEntity instanceof PlayerEntity) {
            PlayerEntitySkinData playerSkinData = (PlayerEntitySkinData) currentEntity;
            SkinImageProcessorData skinImageProcessorData = (SkinImageProcessorData) def;
            skinImageProcessorData.setTextureModel(playerSkinData.getTextureModel());
        }
        return def;
    }

    @ModifyArg(
            method = "notifyEntityAdded", index = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/texture/TextureManager;addHttpTexture(Ljava/lang/String;Lnet/minecraft/client/render/texture/HttpImageProcessor;)Lnet/minecraft/client/render/texture/HttpTexture;", ordinal = 1)
    )
    private HttpImageProcessor redirectCapeProcessor(HttpImageProcessor def) {
        return new CapeImageProcessor();
    }
}
