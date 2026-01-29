package com.matthewperiut.retroauth.mixin.server;

import net.minecraft.network.packet.LoginPacket;
import net.minecraft.server.network.handler.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerLoginNetworkHandler.class)
public interface ServerNetworkHandlerAccessor {
    @Accessor("key")
    String getServerId();

    @Accessor("packet")
    void setLoginPacket(LoginPacket loginPacket);
}
