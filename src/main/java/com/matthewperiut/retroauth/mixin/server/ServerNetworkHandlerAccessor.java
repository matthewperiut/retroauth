package com.matthewperiut.retroauth.mixin.server;

import net.minecraft.network.packets.LoginPacket;
import net.minecraft.server.network.ServerLoginPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerLoginPacketListener.class)
public interface ServerNetworkHandlerAccessor {
    @Accessor("serverId")
    String getServerId();

    @Accessor("loginPacket")
    void setLoginPacket(LoginPacket loginPacket);
}
