package com.example.storage.util;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public final class MyAppGlideModule extends AppGlideModule {
	@Override
	public boolean isManifestParsingEnabled() {
		return false;
	}
}