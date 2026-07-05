package com.matthewperiut.retroauth.mixin;

import net.minecraft.network.packet.handshake.HandshakePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Widens the handshake string-length cap. Vanilla b1.7.3 reads the handshake
 * string with a small max ({@code readString(stream, 32)}); modern
 * auth/companion mods can carry a longer payload through the handshake, which
 * otherwise trips {@code "Received string length longer than maximum allowed"}.
 *
 * <p>Bumping the constant (rather than {@code @ModifyArg} on the inherited
 * {@code readString} call, whose owner is ambiguous between HandshakePacket and
 * its Packet superclass) is mapping-stable and always applies.
 */
@Mixin(HandshakePacket.class)
public abstract class HandshakePacketMixin {

    @ModifyConstant(method = "read", constant = @Constant(intValue = 32))
    private int retroauth$widenHandshakeLimit(int original) {
        return 512;
    }
}
