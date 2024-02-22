package io.github.exampleuser.exampleplugin;

/**
 * Implemented in classes that should support being reloaded IE executing the methods during runtime after startup.
 */
public interface Reloadable {
    /**
     * On plugin load.
     */
    void onLoad();

    /**
     * On plugin enable.
     */
    void onEnable();

    /**
     * On plugin disable.
     */
    void onDisable();
}
