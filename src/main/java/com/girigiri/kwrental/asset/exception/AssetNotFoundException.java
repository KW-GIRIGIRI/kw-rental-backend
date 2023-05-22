package com.girigiri.kwrental.asset.exception;

import com.girigiri.kwrental.common.exception.NotFoundException;

public class AssetNotFoundException extends NotFoundException {
    public AssetNotFoundException() {
        super("해당 자산을 찾지 못했습니다.");
    }
}
