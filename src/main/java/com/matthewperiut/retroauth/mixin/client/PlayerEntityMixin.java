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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends Mob implements PlayerEntitySkinData {
    @Unique
    private String textureModel;

    public PlayerEntityMixin(Level world) {
        super(world);
    }

    @Inject(method = "prepareCustomTextures", at = @At("HEAD"), cancellable = true)
    private void cancelUpdateCapeUrl(CallbackInfo ci) {
        ci.cancel();
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;textureName:Ljava/lang/String;"))
    private void redirectTexture(Player instance, String value) {
        this.setTextureModel("default");
    }

    @Override
    public String getTextureModel() {
        return textureModel;
    }

    @Unique
    public void setTextureModel(String textureModel) {
        this.textureModel = textureModel;
        this.textureName = Objects.equals(textureModel, "default") ? SkinService.STEVE_TEXTURE : SkinService.ALEX_TEXTURE;
    }
}
