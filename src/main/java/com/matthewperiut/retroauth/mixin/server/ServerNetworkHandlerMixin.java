package com.matthewperiut.retroauth.mixin.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matthewperiut.retroauth.RetroAuth;
import net.minecraft.network.packet.login.LoginHelloPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Mixin(targets = "net.minecraft.server.network.ServerLoginNetworkHandler$AuthThread")
public class ServerNetworkHandlerMixin {
    private static final String SESSION_SERVER_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined";
    @Shadow
    @Final
    ServerLoginNetworkHandler networkHandler;
    @Shadow
    @Final
    LoginHelloPacket loginPacket;

    /**
     * @reason Swap auth logic completely
     * @author js6pak
     */
    @Overwrite(remap = false)
    public void run() {
        try {
            ServerNetworkHandlerAccessor accessor = (ServerNetworkHandlerAccessor) networkHandler;

            try {
                // Construct the URL for Mojang's session server
                String requestUrl = SESSION_SERVER_URL + "?username=" + loginPacket.username + "&serverId=" + accessor.getServerId();

                // Open connection
                HttpURLConnection connection = (HttpURLConnection) new URL(requestUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                // Check response code
                if (connection.getResponseCode() == 200) {
                    // Parse the JSON response
                    JsonObject jsonResponse = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
                    String name = jsonResponse.get("name").getAsString();
                    String id = jsonResponse.get("id").getAsString();

                    RetroAuth.LOGGER.info("Authenticated " + name + " as " + id);
                    accessor.setLoginPacket(loginPacket);
                    return;
                }
            } catch (Exception ignored) {
            }

            networkHandler.disconnect("Failed to authenticate!");
        } catch (Exception e) {
            networkHandler.disconnect("Failed to authenticate! [internal error " + e + "]");
            e.printStackTrace();
        }
    }
}
