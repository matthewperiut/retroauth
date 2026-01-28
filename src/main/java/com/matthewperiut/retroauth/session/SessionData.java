package com.matthewperiut.retroauth.session;

import com.matthewperiut.retroauth.profile.GameProfile;

public interface SessionData {
    GameProfile getGameProfile();

    String getAccessToken();
}
