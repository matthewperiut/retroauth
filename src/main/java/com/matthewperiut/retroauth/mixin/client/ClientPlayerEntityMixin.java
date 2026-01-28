package com.matthewperiut.retroauth.mixin.client;

import com.matthewperiut.retroauth.skin.SkinService;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)

public abstract class ClientPlayerEntityMixin extends Player {
    public ClientPlayerEntityMixin(Level arg) {
        super(arg);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        SkinService.getInstance().init(this);
    }
}
