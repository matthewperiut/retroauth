package com.matthewperiut.retroauth.mixin.client;

import com.matthewperiut.retroauth.skin.CapeImageProcessor;
import com.matthewperiut.retroauth.skin.data.PlayerEntitySkinData;
import com.matthewperiut.retroauth.skin.data.SkinImageProcessorData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.HttpTextureProcessor;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Unique
    private Entity currentEntity; // I hate this but there is no way to get it from @ModifyArg

    @Inject(method = "entityRemoved", at = @At("HEAD"), cancellable = true)
    private void dontUnloadLocalPlayerSkin(Entity entity, CallbackInfo ci) {
        if (entity instanceof LocalPlayer) {
            ci.cancel();
        }
    }

    @Inject(method = "entityAdded", at = @At("HEAD"))
    private void getEnttity(Entity entity, CallbackInfo ci) {
        currentEntity = entity;
    }

    @ModifyArg(
            method = "entityAdded", index = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Textures;addHttpTexture(Ljava/lang/String;Lnet/minecraft/client/renderer/HttpTextureProcessor;)Lnet/minecraft/client/renderer/HttpTexture;", ordinal = 0)
    )
    private HttpTextureProcessor redirectSkinProcessor(HttpTextureProcessor def) {
        if (currentEntity instanceof Player) {
            PlayerEntitySkinData playerSkinData = (PlayerEntitySkinData) currentEntity;
            SkinImageProcessorData skinImageProcessorData = (SkinImageProcessorData) def;
            skinImageProcessorData.setTextureModel(playerSkinData.getTextureModel());
        }
        return def;
    }

    @ModifyArg(
            method = "entityAdded", index = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Textures;addHttpTexture(Ljava/lang/String;Lnet/minecraft/client/renderer/HttpTextureProcessor;)Lnet/minecraft/client/renderer/HttpTexture;", ordinal = 1)
    )
    private HttpTextureProcessor redirectCapeProcessor(HttpTextureProcessor def) {
        return new CapeImageProcessor();
    }
}
