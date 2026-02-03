package com.matthewperiut.retroauth.mixin;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.handshake.HandshakePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HandshakePacket.class)
abstract class HandshakePacketMixin extends Packet {

    @ModifyArg(
            method = "read",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/handshake/HandshakePacket;readString(Ljava/io/DataInputStream;I)Ljava/lang/String;"
            ),
            index = 1
    )
    private int widenMaxLen(int originalMax) {
        return Math.max(originalMax, originalMax+8);
    }
}

