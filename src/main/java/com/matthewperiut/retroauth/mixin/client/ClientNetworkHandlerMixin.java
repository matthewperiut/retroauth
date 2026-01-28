package com.matthewperiut.retroauth.mixin.client;

import com.matthewperiut.retroauth.profile.GameProfile;
import com.matthewperiut.retroauth.session.SessionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.packet.HandshakePacket;
import net.minecraft.network.packet.LoginPacket;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.HttpURLConnection;
import java.net.URL;

@Mixin(ClientNetworkHandler.class)
public abstract class ClientNetworkHandlerMixin {
    @Shadow
    private Minecraft minecraft;

    @Shadow
    private Connection connection;

    @Shadow
    public abstract void sendPacket(Packet arg);

    @Redirect(method = "handleHandshake", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
    private boolean checkServerId(String serverId, Object offline) {
        return serverId.trim().isEmpty() || serverId.equals(offline) || this.minecraft.session.id.trim().isEmpty() || this.minecraft.session.id.equals(offline);
    }

    @Inject(method = "handleHandshake", at = @At(value = "NEW", target = "java/net/URL"), cancellable = true)
    private void onJoinServer(HandshakePacket packet, CallbackInfo ci) {
        SessionData session = (SessionData) this.minecraft.session;

        try {
            if (session.getGameProfile() == null || session.getAccessToken() == null) {
                this.connection.close("disconnect.loginFailedInfo", "Invalid access token!");
            }

            joinServer(session.getGameProfile(), session.getAccessToken(), packet.key);
            this.sendPacket(new LoginPacket(this.minecraft.session.username, 14));
        } catch (Exception e) {
            this.connection.close("disconnect.loginFailedInfo", e.getMessage());
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
