package com.matthewperiut.retroauth.skin;

import com.matthewperiut.retroauth.RetroAuth;
import com.matthewperiut.retroauth.profile.GameProfile;
import com.matthewperiut.retroauth.profile.PlayerProfile;
import com.matthewperiut.retroauth.profile.provider.MojangProfileProvider;
import com.matthewperiut.retroauth.profile.provider.ProfileProvider;
import com.matthewperiut.retroauth.skin.data.PlayerEntitySkinData;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class SkinService {
    public static final String STEVE_TEXTURE = "/mob/steve.png";
    public static final String ALEX_TEXTURE = "/mob/alex.png";

    private static final SkinService INSTANCE = new SkinService();
    private final ConcurrentMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final Map<String, PlayerProfile> profiles = new HashMap<>();
    private final List<ProfileProvider> providers = Collections.singletonList(new MojangProfileProvider());

    public static SkinService getInstance() {
        return INSTANCE;
    }

    private void updatePlayer(PlayerEntity player, PlayerProfile playerProfile) {
        if (playerProfile == null) return;

        PlayerEntitySkinData skinData = (PlayerEntitySkinData) player;

        skinData.setTextureModel(playerProfile.getTextureModel());
        player.skinUrl = playerProfile.getSkinUrl();
        player.capeUrl = player.playerCapeUrl = playerProfile.getCapeUrl();

        ((Minecraft) FabricLoader.getInstance().getGameInstance()).worldRenderer.notifyEntityAdded(player);
    }

    private boolean updatePlayer(PlayerEntity player) {
        if (profiles.containsKey(player.name)) {
            PlayerProfile profile = profiles.get(player.name);
            updatePlayer(player, profile);
            return true;
        }

        return false;
    }

    private void initOffline(PlayerEntity player) {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.name).getBytes(StandardCharsets.UTF_8));
        PlayerEntitySkinData skinData = (PlayerEntitySkinData) player;
        skinData.setTextureModel("default"); // Default texture model for offline players
    }

    public void init(PlayerEntity player) {
        if (updatePlayer(player)) return;

        initOffline(player);

        (new Thread(() -> {
            init(player.name);
            updatePlayer(player);
        })).start();
    }

    public void init(String name) {
        if (profiles.containsKey(name)) return;

        ReentrantLock lock = locks.computeIfAbsent(name, k -> new ReentrantLock());

        lock.lock();
        try {
            if (profiles.containsKey(name)) return;

            for (ProfileProvider provider : providers) {
                GameProfile gameProfile;

                try {
                    gameProfile = provider.get(name).get();
                } catch (Exception e) {
                    RetroAuth.LOGGER.warn("Lookup using {} for profile {} failed!", provider.getClass().getSimpleName(), name);
                    continue;
                }

                // Map GameProfile to PlayerProfile
                PlayerProfile playerProfile = new PlayerProfile(
                        UUID.fromString(gameProfile.getId()),
                        gameProfile.getSkinUrl(),
                        gameProfile.getCapeUrl(),
                        gameProfile.getModel()
                );

                RetroAuth.LOGGER.info("Downloaded profile: " + gameProfile.getName() + " (" + gameProfile.getId() + ")");
                profiles.put(name, playerProfile);
                return;
            }

            profiles.put(name, null); // Profile not found
        } finally {
            lock.unlock();
        }
    }
}
