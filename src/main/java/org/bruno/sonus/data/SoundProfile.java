package org.bruno.sonus.data;

import org.bukkit.SoundCategory;

/**
 * Represents a single sound profile defined in the configuration.
 * This is an immutable data object.
 */
public record SoundProfile(
        String sound,
        SoundCategory source,
        float volume,
        float pitch,
        boolean loop,
        int loopTime // in seconds
) {}