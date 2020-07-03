package com.rs.game.player.cutscenes;

import com.rs.game.player.Player;
import com.rs.game.player.cutscenes.actions.CutsceneAction;
import com.rs.game.player.cutscenes.actions.LookCameraAction;
import com.rs.game.player.cutscenes.actions.PosCameraAction;

import java.util.ArrayList;

public class EdgeWilderness extends Cutscene {

	@Override
	public CutsceneAction[] getActions(Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<>();

		actionsList.add(new PosCameraAction(80, 75, 5000, 6, 6, -1));
		actionsList.add(new LookCameraAction(30, 75, 1000, 6, 6, 10));
		actionsList.add(new PosCameraAction(30, 75, 5000, 3, 3, 10));

		return actionsList.toArray(new CutsceneAction[0]);
	}

	@Override
	public boolean hiddenMinimap() {
		return true;
	}

}
