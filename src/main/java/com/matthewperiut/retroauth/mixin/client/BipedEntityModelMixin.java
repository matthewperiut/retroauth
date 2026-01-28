package com.matthewperiut.retroauth.mixin.client;

import com.matthewperiut.retroauth.skin.ModernPlayerEntityModel;
import com.matthewperiut.retroauth.skin.data.ModelPartData;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(HumanoidModel.class)
public class BipedEntityModelMixin {
    @Redirect(
            method = "<init>(FF)V",
            at = @At(value = "NEW", target = "(II)Lnet/minecraft/client/model/geom/ModelPart;"),
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/model/HumanoidModel;ear:Lnet/minecraft/client/model/geom/ModelPart;", shift = Shift.AFTER))
    )
    private ModelPart onTexturedQuad(int u, int v) {
        ModelPart modelPart = new ModelPart(u, v);

        HumanoidModel self = (HumanoidModel) (Object) this;
        if (self instanceof ModernPlayerEntityModel) {
            ((ModelPartData) modelPart).setTextureHeight(64);
        }

        return modelPart;
    }
}
