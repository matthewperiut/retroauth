package com.matthewperiut.retroauth.mixin.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matthewperiut.retroauth.RetroAuth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import net.minecraft.network.packet.LoginPacket;
import net.minecraft.server.network.handler.ServerLoginNetworkHandler;

@Mixin(targets = "net.minecraft.server.network.handler.ServerLoginNetworkHandler$net.minecraft.server.network.handler.ServerLoginNetworkHandler__15575233")
public class ServerNetworkHandlerMixin {
    private static final String SESSION_SERVER_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined";
    @Shadow
    @Final
    ServerLoginNetworkHandler f_55814992;
    @Shadow
    @Final
    LoginPacket f_12137288;

    /**
     * @reason Swap auth logic completely
     * @author Slainlight
     */
    @Overwrite(remap = false)
    public void run() {
        try {
            ServerNetworkHandlerAccessor accessor = (ServerNetworkHandlerAccessor) f_55814992;

            try {
                // Construct the URL for Mojang's session server
                String requestUrl = SESSION_SERVER_URL + "?username=" + f_12137288.username + "&serverId=" + accessor.getServerId();

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
                    accessor.setLoginPacket(f_12137288);
                    return;
                }
            } catch (Exception ignored) {
            }

            f_55814992.disconnect("Failed to authenticate!");
        } catch (Exception e) {
            f_55814992.disconnect("Failed to authenticate! [internal error " + e + "]");
            e.printStackTrace();
        }
    }
}
