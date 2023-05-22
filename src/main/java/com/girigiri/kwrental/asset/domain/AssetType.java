package com.girigiri.kwrental.asset.domain;

public enum AssetType {
    EQUIPMENT("equipment"),
    LAB_ROOM("lab_room");

    private final String discriminatorValue;

    AssetType(final String discriminatorValue) {
        this.discriminatorValue = discriminatorValue;
    }

    public String getDiscriminatorValue() {
        return discriminatorValue;
    }
}
