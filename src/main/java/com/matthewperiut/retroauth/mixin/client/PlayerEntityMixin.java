package com.matthewperiut.retroauth.mixin.client;

import com.matthewperiut.retroauth.skin.SkinService;
import com.matthewperiut.retroauth.skin.data.PlayerEntitySkinData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends MobEntity implements PlayerEntitySkinData {
    @Unique
    private String textureModel;

    public PlayerEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "prepareCape", at = @At("HEAD"), cancellable = true)
    private void cancelUpdateCapeUrl(CallbackInfo ci) {
        ci.cancel();
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/mob/player/PlayerEntity;texture:Ljava/lang/String;"))
    private void redirectTexture(PlayerEntity instance, String value) {
        this.setTextureModel("default");
    }

    @Override
    public String getTextureModel() {
        return textureModel;
    }

    @Unique
    public void setTextureModel(String textureModel) {
        this.textureModel = textureModel;
        this.texture = Objects.equals(textureModel, "default") ? SkinService.STEVE_TEXTURE : SkinService.ALEX_TEXTURE;
    }
}
