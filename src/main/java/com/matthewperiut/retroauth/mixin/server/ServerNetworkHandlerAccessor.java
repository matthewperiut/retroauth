package com.matthewperiut.retroauth.mixin.server;

import net.minecraft.network.packet.login.LoginHelloPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerLoginNetworkHandler.class)
public interface ServerNetworkHandlerAccessor {
    @Accessor
    String getServerId();

    @Accessor
    void setLoginPacket(LoginHelloPacket loginPacket);
}
