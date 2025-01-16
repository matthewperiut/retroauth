package com.matthewperiut.retroauth.mixin.client;

import com.matthewperiut.retroauth.RetroAuth;
import com.matthewperiut.retroauth.profile.GameProfile;
import com.matthewperiut.retroauth.session.SessionData;
import com.matthewperiut.retroauth.skin.SkinService;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.regex.Pattern;

@Mixin(Session.class)
public class SessionMixin implements SessionData {
    @Unique
    private static final Pattern UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
    @Unique
    private GameProfile gameProfile;
    @Unique
    private String accessToken;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(String username, String sessionId, CallbackInfo ci) {
        String[] split = sessionId.split(":");
        if (split.length == 3 && split[0].equalsIgnoreCase("token")) {
            accessToken = split[1];
            UUID uuid = UUID.fromString(UUID_PATTERN.matcher(split[2]).replaceAll("$1-$2-$3-$4-$5"));
            gameProfile = new GameProfile(uuid.toString(), username, null, null, null, null);

            RetroAuth.LOGGER.info("Signed in as {} ({})", username, uuid);
        } else {
            RetroAuth.LOGGER.info("Signed in as {}", username);
        }

        SkinService.getInstance().init(username);
    }

    @Override
    public GameProfile getGameProfile() {
        return gameProfile;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }
}
