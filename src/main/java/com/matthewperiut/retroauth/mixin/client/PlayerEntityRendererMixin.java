package com.matthewperiut.retroauth.mixin.client;

import com.matthewperiut.retroauth.skin.ModernPlayerEntityModel;
import com.matthewperiut.retroauth.skin.data.PlayerEntityRendererSkinData;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer implements PlayerEntityRendererSkinData {
    @Shadow
    private HumanoidModel humanoidModel;

    public PlayerEntityRendererMixin(Model arg, float f) {
        super(arg, f);
    }

    public void setThinArms(boolean thinArms) {
        this.model = this.humanoidModel = new ModernPlayerEntityModel(0.0F, thinArms);
    }

    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(F)V"))
    private void fixFirstPerson$1(CallbackInfo ci) {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Inject(method = "renderHand", at = @At("RETURN"))
    private void fixFirstPerson$2(CallbackInfo ci) {
        ((ModernPlayerEntityModel) humanoidModel).rightSleeve.render(0.0625F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/player/Player;DDDFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/Mob;DDDFF)V"))
    private void fixOuterLayer$1(CallbackInfo ci) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/player/Player;DDDFF)V", at = @At("RETURN"))
    private void fixOuterLayer$2(CallbackInfo ci) {
        GL11.glDisable(GL11.GL_BLEND);
    }


    @Redirect(method = "additionalRendering(Lnet/minecraft/world/entity/player/Player;F)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/HumanoidModel;renderCloak(F)V"
            )
    )
    protected void mojangFixStationApi_method_827(HumanoidModel instance, float v) {
        instance.renderCloak(v);
    }
}
