package com.girigiri.kwrental.asset.equipment.service;

import java.util.List;

public interface ItemSaverPerEquipment {

	int execute(final List<ToBeSavedItem> toBeSavedItems);
}
