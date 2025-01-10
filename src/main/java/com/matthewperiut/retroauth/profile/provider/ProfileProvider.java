package com.matthewperiut.retroauth.profile.provider;

import com.matthewperiut.retroauth.profile.GameProfile;

import java.util.concurrent.Future;

public interface ProfileProvider {
    Future<GameProfile> get(String username);
}