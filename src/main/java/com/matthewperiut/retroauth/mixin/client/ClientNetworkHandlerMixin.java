package com.matthewperiut.retroauth.mixin.client;

import com.matthewperiut.retroauth.profile.GameProfile;
import com.matthewperiut.retroauth.session.SessionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.packets.LoginPacket;
import net.minecraft.network.packets.Packet;
import net.minecraft.network.packets.PreLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.HttpURLConnection;
import java.net.URL;

@Mixin(ClientPacketListener.class)
public abstract class ClientNetworkHandlerMixin {
    @Shadow
    private Minecraft minecraft;

    @Shadow
    private Connection connection;

    @Shadow
    public abstract void send(Packet arg);

    @Redirect(method = "handlePreLogin", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
    private boolean checkServerId(String serverId, Object offline) {
        return serverId.trim().isEmpty() || serverId.equals(offline) || this.minecraft.user.sessionId.trim().isEmpty() || this.minecraft.user.sessionId.equals(offline);
    }

    @Inject(method = "handlePreLogin", at = @At(value = "NEW", target = "java/net/URL"), cancellable = true)
    private void onJoinServer(PreLoginPacket packet, CallbackInfo ci) {
        SessionData session = (SessionData) this.minecraft.user;

        try {
            if (session.getGameProfile() == null || session.getAccessToken() == null) {
                this.connection.disconnect("disconnect.loginFailedInfo", "Invalid access token!");
            }

            joinServer(session.getGameProfile(), session.getAccessToken(), packet.userName);
            this.send(new LoginPacket(this.minecraft.user.username, 14));
        } catch (Exception e) {
            this.connection.disconnect("disconnect.loginFailedInfo", e.getMessage());
        }

        ci.cancel();
    }

    @Unique
    private void joinServer(GameProfile profile, String accessToken, String serverId) throws Exception {
        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/join");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        String payload = String.format(
                "{\"accessToken\":\"%s\",\"selectedProfile\":\"%s\",\"serverId\":\"%s\"}",
                accessToken, profile.getId(), serverId
        );

        connection.getOutputStream().write(payload.getBytes());
        if (connection.getResponseCode() != 204) {
            throw new Exception("Failed to join server: " + connection.getResponseMessage());
        }
    }
}
