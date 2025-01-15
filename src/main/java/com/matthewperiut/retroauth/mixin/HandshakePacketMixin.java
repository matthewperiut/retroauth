package com.matthewperiut.retroauth.mixin;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.handshake.HandshakePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.DataInputStream;

@Mixin(HandshakePacket.class)
abstract public class HandshakePacketMixin extends Packet {
    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/handshake/HandshakePacket;readString(Ljava/io/DataInputStream;I)Ljava/lang/String;"))
    String redir(DataInputStream dataInputStream, int i) {
        return readString(dataInputStream, 40);
    }
}
