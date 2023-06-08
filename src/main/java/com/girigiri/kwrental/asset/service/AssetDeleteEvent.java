package com.girigiri.kwrental.asset.service;

import org.springframework.context.ApplicationEvent;

public abstract class AssetDeleteEvent extends ApplicationEvent {
	public AssetDeleteEvent(Object source) {
		super(source);
	}

	public abstract Long getAssetId();
}
