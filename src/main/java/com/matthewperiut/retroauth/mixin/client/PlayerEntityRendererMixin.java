package com.matthewperiut.retroauth.mixin.client;

import com.matthewperiut.retroauth.skin.ModernPlayerEntityModel;
import com.matthewperiut.retroauth.skin.data.PlayerEntityRendererSkinData;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.entity.HumanoidModel;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin extends MobRenderer implements PlayerEntityRendererSkinData {
    @Shadow
    private HumanoidModel player;

    public PlayerEntityRendererMixin(Model arg, float f) {
        super(arg, f);
    }

    public void setThinArms(boolean thinArms) {
        this.model = this.player = new ModernPlayerEntityModel(0.0F, thinArms);
    }

    @Inject(method = "renderRightHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelPart;render(F)V"))
    private void fixFirstPerson$1(CallbackInfo ci) {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Inject(method = "renderRightHand", at = @At("RETURN"))
    private void fixFirstPerson$2(CallbackInfo ci) {
        ((ModernPlayerEntityModel) player).rightSleeve.render(0.0625F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    @Inject(method = "render(Lnet/minecraft/entity/mob/player/PlayerEntity;DDDFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/MobRenderer;render(Lnet/minecraft/entity/mob/MobEntity;DDDFF)V"))
    private void fixOuterLayer$1(CallbackInfo ci) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Inject(method = "render(Lnet/minecraft/entity/mob/player/PlayerEntity;DDDFF)V", at = @At("RETURN"))
    private void fixOuterLayer$2(CallbackInfo ci) {
        GL11.glDisable(GL11.GL_BLEND);
    }


    @Redirect(method = "renderMore(Lnet/minecraft/entity/mob/player/PlayerEntity;F)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/model/entity/HumanoidModel;renderCape(F)V"
            )
    )
    protected void mojangFixStationApi_method_827(HumanoidModel instance, float v) {
        instance.renderCape(v);
    }
}
