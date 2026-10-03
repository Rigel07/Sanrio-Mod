package com.sanriofriends.dialogue;

import com.sanriofriends.SanrioCharacter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

/** Datos de amistad y misiones de cada jugador (se guardan en el jugador). */
public final class FriendData {
    public static final String KEY = "sanriofriends_data";

    private FriendData() {}

    private static CompoundTag root(ServerPlayer p) {
        return p.getPersistentData().getCompound(KEY);
    }

    private static CompoundTag entry(ServerPlayer p, SanrioCharacter c) {
        return root(p).getCompound(c.id);
    }

    public static int getInt(ServerPlayer p, SanrioCharacter c, String key) {
        return entry(p, c).getInt(key);
    }

    public static long getLong(ServerPlayer p, SanrioCharacter c, String key) {
        return entry(p, c).getLong(key);
    }

    public static void setInt(ServerPlayer p, SanrioCharacter c, String key, int value) {
        CompoundTag r = root(p);
        CompoundTag e = r.getCompound(c.id);
        e.putInt(key, value);
        r.put(c.id, e);
        p.getPersistentData().put(KEY, r);
    }

    public static void setLong(ServerPlayer p, SanrioCharacter c, String key, long value) {
        CompoundTag r = root(p);
        CompoundTag e = r.getCompound(c.id);
        e.putLong(key, value);
        r.put(c.id, e);
        p.getPersistentData().put(KEY, r);
    }

    /** Misiones completadas con ese personaje. */
    public static int completed(ServerPlayer p, SanrioCharacter c) {
        return getInt(p, c, "done");
    }

    /** Amigos = al menos una misión completada. */
    public static boolean isFriend(ServerPlayer p, SanrioCharacter c) {
        return completed(p, c) >= 1;
    }

    public static boolean questActive(ServerPlayer p, SanrioCharacter c) {
        return getInt(p, c, "active") == 1;
    }

    public static int progress(ServerPlayer p, SanrioCharacter c) {
        return getInt(p, c, "progress");
    }

    public static String friendTitle(int done) {
        if (done >= 6) return "Mejores amigos para siempre";
        if (done >= 3) return "Grandes amigos";
        if (done >= 1) return "Amigos";
        return "Aún no sois amigos";
    }
}
