package com.rs.game.npc.combat.impl.boss;

import com.rs.game.npc.Npc;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.data.NpcCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.game.world.*;
import com.rs.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peng on 4.1.2017 21:54.
 */
public class KalphiteQueenCombat extends CombatScript {

    @Override
    public Object[] getKeys() {
        return new Object[]{"Kalphite Queen"};
    }

    private Player getTarget(List<Player> list, final Entity fromEntity, WorldTile startTile) {
        if (fromEntity == null) {
            return null;
        }
        ArrayList<Player> added = new ArrayList<>();
        for (int regionId : fromEntity.getMapRegionsIds()) {
            List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
            if (playersIndexes == null) continue;
            for (Integer playerIndex : playersIndexes) {
                Player player = World.getPlayers().get(playerIndex);
                if (player == null || list.contains(player) || !player.withinDistance(fromEntity)
                    || !player.withinDistance(startTile)) continue;
                added.add(player);
            }
        }
        if (added.isEmpty()) return null;
        added.sort((o1, o2) -> {
            if (o1 == null) return 1;
            if (o2 == null) return -1;
            return Integer.compare(Utils.getDistance(o1, fromEntity), Utils.getDistance(o2, fromEntity));
        });
        return added.get(0);

    }

    private void attackMageTarget(final List<Player> arrayList, Entity fromEntity, final Npc startTile, Entity t) {
        final Entity target = t == null ? getTarget(arrayList, fromEntity, startTile) : t;
        if (target == null) return;
        if (target instanceof Player) arrayList.add((Player) target);
        World.sendProjectile(fromEntity, target, 280, fromEntity == startTile ? 70 : 20, 20, 60, 30, 0, 0);
        delayHit(startTile, 0, target, getMagicHit(startTile, getRandomMaxHit(startTile, startTile.getMaxHit(),
                NpcCombatDefinitions.MAGIC, target)));
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                target.setNextGraphics(new Graphics(281));
                attackMageTarget(arrayList, target, startTile, null);
            }
        });
    }

    @Override
    public int attack(Npc npc, Entity target) {
        final NpcCombatDefinitions defs = npc.getCombatDefinitions();
        int attackStyle = Utils.random(3);
        if (attackStyle == 0) {
            int distanceX = target.getX() - npc.getX();
            int distanceY = target.getY() - npc.getY();
            int size = npc.getSize();
            if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)
                attackStyle = Utils.random(2); // set mage
            else {
                npc.setNextAnimation(new Animation(defs.getAttackEmote()));
                delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NpcCombatDefinitions
                        .MELEE, target)));
                return defs.getAttackDelay();
            }
        }
        npc.setNextAnimation(new Animation(npc.getId() == 1158 ? 6240 : 6234));
        if (attackStyle == 1) { // range easy one
            for (final Entity t : npc.getPossibleTargets()) {
                delayHit(npc, 2, t, getRangeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NpcCombatDefinitions
                        .RANGE, t)));
                World.sendProjectile(npc, t, 288, 46, 31, 50, 30, 16, 0);
            }
        } else {
            npc.setNextGraphics(new Graphics(npc.getId() == 1158 ? 278 : 279));
            WorldTasksManager.schedule(new WorldTask() {

                @Override
                public void run() {
                    attackMageTarget(new ArrayList<>(), npc, npc, target);
                }

            });
        }
        return defs.getAttackDelay();
    }
}
