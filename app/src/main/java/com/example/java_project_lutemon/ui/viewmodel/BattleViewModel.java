package com.example.java_project_lutemon.ui.viewmodel;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.java_project_lutemon.core.battle.BattleManager;
import com.example.java_project_lutemon.core.battle.BattleResult;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.repository.LutemonRepository;
import com.example.java_project_lutemon.core.skill.Skill;
import com.example.java_project_lutemon.core.state.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleViewModel extends ViewModel {
    private Lutemon _winner;
    private final MutableLiveData<Lutemon> _playerLutemon = new MutableLiveData<>();
    private final MutableLiveData<Lutemon> _enemyLutemon = new MutableLiveData<>();
    private final MutableLiveData<String> _battleLog = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isBattleOver = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> _expGained = new MutableLiveData<>(0);

    private final BattleManager battleManager = BattleManager.getInstance();

    public LiveData<Lutemon> getPlayerLutemon() { return _playerLutemon; }
    public LiveData<Lutemon> getEnemyLutemon() { return _enemyLutemon; }
    public LiveData<Boolean> isBattleOver() { return _isBattleOver; }
    public LiveData<Integer> getExpGained() { return _expGained; }

    public void initBattle(Lutemon player, Lutemon enemy) {
        _playerLutemon.setValue(player);
        _enemyLutemon.setValue(enemy);
        _battleLog.setValue("Battle started!");
        _isBattleOver.setValue(false);
        _expGained.setValue(0);
        _winner = null;
        battleManager.startBattle(player, enemy);
    }

    public void executeAttack() {
        if (_isBattleOver.getValue()) return;
        handleBattleTurn(battleManager.executeTurn(-1));
    }

    public boolean useSkill(int skillId) {
        if (_isBattleOver.getValue()) return false;
        handleBattleTurn(battleManager.executeTurn(skillId));
        return true;
    }

    public void performAttack() {
        if (_isBattleOver.getValue()) return;
        handleBattleTurn(battleManager.executeTurn(-1));
    }

    public void autoEnemyTurn() {
        if (!_isBattleOver.getValue()) {
            handleBattleTurn(battleManager.executeTurn(-1));
        }
    }

    private void handleBattleTurn(BattleResult result) {
        if (result == null) return;

        _battleLog.setValue(result.getLogMessage());
        _playerLutemon.setValue(battleManager.getLeftLutemon());
        _enemyLutemon.setValue(battleManager.getRightLutemon());

        if (result.isWin()) {

            LutemonRepository repo = LutemonRepository.getInstance();
            Lutemon winner = result.getWinner();
            Lutemon loser = result.getLoser();

            if (winner != null && loser != null) {
                winner.incrementTotalBattles();
                loser.incrementTotalBattles();
                winner.incrementTotalWins();

                int exp = 5 + new Random().nextInt(5);
                _expGained.setValue(exp);

                winner.gainExp(exp);
                loser.resetExp();

                winner.resetAfterBattle();
                loser.resetAfterBattle();

                winner.setState(GameState.REST);
                loser.setState(GameState.REST);
                repo.saveLutemon(winner);
                repo.saveLutemon(loser);

                _winner = winner;
            } else {
                _winner = result.getWinner();
            }

            _isBattleOver.setValue(true);
        }
    }


    public void startAutoBattle(Runnable onEachTurn, Runnable onComplete) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable loop = new Runnable() {
            @Override
            public void run() {
                if (_isBattleOver.getValue() != null && _isBattleOver.getValue()) {
                    onComplete.run(); return;
                }
                if (!useSkill(0)) performAttack();
                onEachTurn.run();
                handler.postDelayed(() -> {
                    if (!_isBattleOver.getValue()) {
                        autoEnemyTurn();
                        onEachTurn.run();
                        handler.postDelayed(this, 1000);
                    } else onComplete.run();
                }, 1000);
            }
        };
        handler.postDelayed(loop, 1000);
    }

    private void resetLutemonStatesAfterBattle(Lutemon winner, Lutemon loser) {
        winner.resetAfterBattle();
        loser.resetAfterBattle();
        winner.setState(GameState.REST);
        loser.setState(GameState.REST);
        LutemonRepository.getInstance().saveLutemon(winner);
        LutemonRepository.getInstance().saveLutemon(loser);
    }

    private int calculateExp(Lutemon defeatedEnemy) {
        return (int) (Math.random() * 5 + 5);
    }

    public Skill getCurrentSkill() {
        return battleManager.getCurrentSkill();
    }

    public String getWinnerName() {
        return _winner != null ? _winner.getName() : "Unknown";
    }

    public List<String> getActiveBuffs(boolean isLeft) {
        Lutemon lutemon = isLeft ? _playerLutemon.getValue() : _enemyLutemon.getValue();
        List<String> buffs = new ArrayList<>();
        if (lutemon != null) {
            for (Skill skill : lutemon.getSkillList()) {
                if (skill.isActive()) {
                    String name = skill.getName().toLowerCase();
                    if (name.contains("attack")) buffs.add("atk_up");
                    else if (name.contains("defense")) buffs.add("def_down");
                    else buffs.add("other");
                }
            }
        }
        return buffs;
    }

    public int getLeftHp() {
        return _playerLutemon.getValue() != null ? _playerLutemon.getValue().getCurrentHp() : 0;
    }

    public int getLeftMaxHp() {
        return _playerLutemon.getValue() != null ? _playerLutemon.getValue().getMaxHp() : 100;
    }

    public int getRightHp() {
        return _enemyLutemon.getValue() != null ? _enemyLutemon.getValue().getCurrentHp() : 0;
    }

    public int getRightMaxHp() {
        return _enemyLutemon.getValue() != null ? _enemyLutemon.getValue().getMaxHp() : 100;
    }
}
