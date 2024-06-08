package de.zebrajaeger.phserver.settings;

public interface SettingsValue<T> {
    void read(T value);
    void write(T value);
}
