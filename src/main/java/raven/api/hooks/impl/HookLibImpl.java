package raven.api.hooks.impl;

import gloomyfolken.hooklib.minecraft.HookLoader;

/**
 * Created by r4v3n6101 on 19.03.2016.
 */
public class HookLibImpl extends HookLoader {

    @Override
    protected void registerHooks() {
        registerHookContainer(ClientHook.class.getName());
    }
}
