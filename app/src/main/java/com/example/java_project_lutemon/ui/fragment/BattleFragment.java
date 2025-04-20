package com.example.java_project_lutemon.ui.fragment;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.java_project_lutemon.R;
import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.repository.LutemonRepository;
import com.example.java_project_lutemon.core.skill.Skill;
import com.example.java_project_lutemon.ui.viewmodel.BattleViewModel;

import java.util.List;

public class BattleFragment extends Fragment {
    // UI components
    private Button btnAttack, btnSkill, btnEndBattle, btnAutoBattle;
    private TextView tvBattleLog, leftHealth, rightHealth, skillStatus;
    private ProgressBar leftHpBar, rightHpBar;
    private LinearLayout leftBuffIcons, rightBuffIcons;
    private View leftStats, rightStats;

    // ViewModel to manage battle logic and state
    private BattleViewModel viewModel;
    private boolean battleOverDialogShown = false;

    // Avatar and basic info UI
    private ImageView imgLeftAvatar, imgRightAvatar;
    private TextView tvLeftName, tvRightName, tvLeftStats, tvRightStats;
    private FrameLayout animationOverlay;

    // Load the layout
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_battle, container, false);
    }

    // Setup logic and event handlers after view creation
    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(BattleViewModel.class);

        // Bind UI references
        imgLeftAvatar = rootView.findViewById(R.id.img_left_avatar);
        imgRightAvatar = rootView.findViewById(R.id.img_right_avatar);
        tvLeftName = rootView.findViewById(R.id.tv_left_name);
        tvRightName = rootView.findViewById(R.id.tv_right_name);
        tvLeftStats = rootView.findViewById(R.id.tv_left_stats);
        tvRightStats = rootView.findViewById(R.id.tv_right_stats);
        btnAttack = rootView.findViewById(R.id.btnAttack);
        btnSkill = rootView.findViewById(R.id.btnSkill);
        btnEndBattle = rootView.findViewById(R.id.btnEndBattle);
        btnAutoBattle = rootView.findViewById(R.id.btnAutoBattle);
        tvBattleLog = rootView.findViewById(R.id.textBattleInfo);
        leftHealth = rootView.findViewById(R.id.leftHealth);
        rightHealth = rootView.findViewById(R.id.rightHealth);
        skillStatus = rootView.findViewById(R.id.skill_status);
        leftHpBar = rootView.findViewById(R.id.hpBarLeft);
        rightHpBar = rootView.findViewById(R.id.hpBarRight);
        leftBuffIcons = rootView.findViewById(R.id.leftBuffIcons);
        rightBuffIcons = rootView.findViewById(R.id.rightBuffIcons);
        leftStats = rootView.findViewById(R.id.leftStats);
        rightStats = rootView.findViewById(R.id.rightStats);
        animationOverlay = rootView.findViewById(R.id.animationOverlay);

        // Retrieve Lutemon IDs from bundle and initiate battle
        Bundle args = getArguments();
        if (args != null) {
            int leftId = args.getInt("leftLutemonId", -1);
            int rightId = args.getInt("rightLutemonId", -1);
            Lutemon left = LutemonRepository.getInstance().getLutemonById(leftId);
            Lutemon right = LutemonRepository.getInstance().getLutemonById(rightId);

            if (left != null && right != null) {
                viewModel.initBattle(left, right);
                updateLutemonCards(left, right);
                appendToBattleLog("Battle started！\n");
            } else {
                Toast.makeText(getContext(), "Invalid Lutemon IDs", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigate(R.id.action_battle_to_home);
            }
        }

        updateSkillUI();
        updateHpBars();
        updateBuffIcons();

        // Skill button action
        btnSkill.setOnClickListener(v -> {
            Lutemon user = viewModel.getPlayerLutemon().getValue();
            Skill skill = viewModel.getCurrentSkill();
            if (viewModel.useSkill(0)) {
                appendToBattleLog(user.getName() + " use skill「" + skill.getName() + "」：" + skill.getDescription());
                playSkillAnimation(imgLeftAvatar, imgRightAvatar, skill.getName());
            } else {
                appendToBattleLog("Skill is not ready.");
            }
            animateButton(btnSkill);
            updateSkillUI();
            updateHpBars();
            updateBuffIcons();
            checkBattleEnd();
        });

        // Normal attack button
        btnAttack.setOnClickListener(v -> {
            viewModel.performAttack();
            Lutemon attacker = viewModel.getPlayerLutemon().getValue();
            Lutemon defender = viewModel.getEnemyLutemon().getValue();
            int damage = Math.max(attacker.getAttack() - defender.getDefense(), 0);
            playAttackAnimation(imgLeftAvatar, imgRightAvatar, damage);
            viewModel.performAttack();
            appendToBattleLog(attacker.getName() + " attacked " + defender.getName() + " and dealt " + damage + " damage.");
            animateButton(btnAttack);
            updateSkillUI();
            updateHpBars();
            updateBuffIcons();
            checkBattleEnd();

            if (viewModel.isBattleOver().getValue() == null || !viewModel.isBattleOver().getValue()) {
                viewModel.autoEnemyTurn();
                Lutemon counter = viewModel.getEnemyLutemon().getValue();
                Lutemon target = viewModel.getPlayerLutemon().getValue();
                int counterDamage = Math.max(counter.getAttack() - target.getDefense(), 0);
                playAttackAnimation(imgLeftAvatar, imgRightAvatar, counterDamage);
                viewModel.performAttack();
                appendToBattleLog(counter.getName() + " counterattacked " + target.getName() + " and dealt " + counterDamage + " damage.");
                updateSkillUI();
                updateHpBars();
                updateBuffIcons();
                checkBattleEnd();
            }
        });

        // Return button
        btnEndBattle.setOnClickListener(v -> {
            NavHostFragment.findNavController(BattleFragment.this)
                    .navigate(R.id.action_battle_to_home);
        });

        // Auto battle button
        btnAutoBattle.setOnClickListener(v -> {
            disableAllButtons();
            appendToBattleLog(" Auto Battle started...");
            viewModel.startAutoBattle(
                    () -> {
                        Lutemon attacker = viewModel.getPlayerLutemon().getValue();
                        Lutemon defender = viewModel.getEnemyLutemon().getValue();
                        int damage = Math.max(attacker.getAttack() - defender.getDefense(), 0);

                        if (viewModel.getCurrentSkill() != null &&
                                viewModel.getCurrentSkill().getUsesLeft() > 0 &&
                                viewModel.getCurrentSkill().getCurrentCooldown() == 0) {
                            appendToBattleLog(attacker.getName() + " used skill [" +
                                    viewModel.getCurrentSkill().getName() + "]: " +
                                    viewModel.getCurrentSkill().getDescription());
                            playSkillAnimation(imgLeftAvatar, imgRightAvatar, viewModel.getCurrentSkill().getName());
                        } else {
                            appendToBattleLog(attacker.getName() + " attacked " +
                                    defender.getName() + " and dealt " + damage + " damage.");
                            playAttackAnimation(imgLeftAvatar, imgRightAvatar, damage);
                        }

                        updateSkillUI();
                        updateHpBars();
                        updateBuffIcons();
                    },
                    this::checkBattleEnd
            );
        });

    }

    private void playAttackAnimation(ImageView attackerView, ImageView defenderView, int damage) {
        ImageView sword = new ImageView(requireContext());
        sword.setImageResource(R.drawable.ic_sword);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(160, 160);
        sword.setLayoutParams(layoutParams);

        ViewGroup root = animationOverlay;
        root.addView(sword);

        int[] attackerLoc = new int[2];
        int[] defenderLoc = new int[2];
        int[] rootLoc = new int[2];
        attackerView.getLocationOnScreen(attackerLoc);
        defenderView.getLocationOnScreen(defenderLoc);
        root.getLocationOnScreen(rootLoc);

        int fromX = attackerLoc[0] - rootLoc[0];
        int fromY = attackerLoc[1] - rootLoc[1];
        int toX = defenderLoc[0] - rootLoc[0];
        int toY = defenderLoc[1] - rootLoc[1];

        sword.setX(fromX);
        sword.setY(fromY);

        sword.animate()
                .x(toX)
                .y(toY)
                .setDuration(600)
                .withEndAction(() -> {
                    root.removeView(sword);
                    showDefenseAnimation(defenderView, damage);
                })
                .start();
    }

    private void showDefenseAnimation(ImageView targetView, int damage) {
        ImageView shield = new ImageView(requireContext());
        shield.setImageResource(R.drawable.ic_shield_break);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(160, 160);
        shield.setLayoutParams(layoutParams);

        ViewGroup root = animationOverlay;
        root.addView(shield);

        int[] targetLoc = new int[2];
        int[] rootLoc = new int[2];
        targetView.getLocationOnScreen(targetLoc);
        root.getLocationOnScreen(rootLoc);

        int x = targetLoc[0] - rootLoc[0] + targetView.getWidth() / 4;
        int y = targetLoc[1] - rootLoc[1] - 20;

        shield.setX(x);
        shield.setY(y);
        shield.setAlpha(0f);
        shield.setScaleX(0.5f);
        shield.setScaleY(0.5f);

        shield.animate()
                .alpha(1f)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(300)
                .withEndAction(() -> {
                    shield.animate()
                            .alpha(0f)
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(300)
                            .withEndAction(() -> {
                                root.removeView(shield);
                                showDamageNumber(targetView, damage);
                            }).start();
                }).start();
    }


    private void showDamageNumber(ImageView targetView, int damage) {
        TextView dmgText = new TextView(requireContext());
        dmgText.setText("-" + damage);
        dmgText.setTextSize(32);
        dmgText.setTypeface(Typeface.DEFAULT_BOLD);
        dmgText.setTextColor(damage >= 10 ? Color.rgb(255, 215, 0) : Color.RED);
        dmgText.setShadowLayer(5f, 0f, 0f, Color.BLACK);

        ViewGroup root = animationOverlay;
        root.addView(dmgText);

        int[] targetLoc = new int[2];
        int[] rootLoc = new int[2];
        targetView.getLocationOnScreen(targetLoc);
        root.getLocationOnScreen(rootLoc);

        int x = targetLoc[0] - rootLoc[0] + targetView.getWidth() / 3;
        int y = targetLoc[1] - rootLoc[1] - 20;

        dmgText.setX(x);
        dmgText.setY(y);

        dmgText.setScaleX(0.8f);
        dmgText.setScaleY(0.8f);
        dmgText.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .translationYBy(-120)
                .alpha(0f)
                .setDuration(1000)
                .withEndAction(() -> root.removeView(dmgText))
                .start();
    }

    private void playSkillAnimation(ImageView attackerView, ImageView targetView, String skillName) {
        ImageView magic = new ImageView(requireContext());
        magic.setImageResource(R.drawable.ic_star);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(160, 160);
        magic.setLayoutParams(layoutParams);

        ViewGroup root = animationOverlay;
        root.addView(magic);

        int[] attackerLoc = new int[2];
        int[] targetLoc = new int[2];
        int[] rootLoc = new int[2];
        attackerView.getLocationOnScreen(attackerLoc);
        targetView.getLocationOnScreen(targetLoc);
        root.getLocationOnScreen(rootLoc);

        int fromX = attackerLoc[0] - rootLoc[0];
        int fromY = attackerLoc[1] - rootLoc[1];
        int toX = targetLoc[0] - rootLoc[0];
        int toY = targetLoc[1] - rootLoc[1];

        magic.setX(fromX);
        magic.setY(fromY);

        magic.animate()
                .x(toX)
                .y(toY)
                .setDuration(600)
                .withEndAction(() -> {
                    root.removeView(magic);
                    showSkillDamageNumber(targetView, skillName);
                })
                .start();
    }

    private void showSkillDamageNumber(ImageView targetView, String skillName) {
        TextView dmgText = new TextView(requireContext());
        dmgText.setText(skillName);
        dmgText.setTextSize(28);
        dmgText.setTypeface(Typeface.DEFAULT_BOLD);
        dmgText.setShadowLayer(6f, 0f, 0f, Color.BLACK);

        ViewGroup root = animationOverlay;
        root.addView(dmgText);

        int[] targetLoc = new int[2];
        int[] rootLoc = new int[2];
        targetView.getLocationOnScreen(targetLoc);
        root.getLocationOnScreen(rootLoc);

        int x = targetLoc[0] - rootLoc[0] + targetView.getWidth() / 3;
        int y = targetLoc[1] - rootLoc[1];

        dmgText.setX(x);
        dmgText.setY(y);

        if (skillName.toLowerCase().contains("beam") || skillName.toLowerCase().contains("boost")) {
            dmgText.setTextColor(Color.rgb(255, 215, 0));
        } else {
            dmgText.setTextColor(Color.RED);
        }

        dmgText.setScaleX(0.8f);
        dmgText.setScaleY(0.8f);
        dmgText.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .translationYBy(-100)
                .alpha(0f)
                .setDuration(1000)
                .withEndAction(() -> root.removeView(dmgText))
                .start();
    }

    private void disableAllButtons() {
        btnAttack.setEnabled(false);
        btnSkill.setEnabled(false);
        btnAutoBattle.setEnabled(false);
    }

    // Update stats display for both Lutemons
    private void updateLutemonCards(Lutemon left, Lutemon right) {
        tvLeftName.setText(left.getName());
        tvRightName.setText(right.getName());
        tvLeftStats.setText("ATK: " + left.getAttack() + "  DEF: " + left.getDefense() + "  EXP: " + left.getExperience());
        tvRightStats.setText("ATK: " + right.getAttack() + "  DEF: " + right.getDefense() + "  EXP: " + right.getExperience());
        imgLeftAvatar.setImageResource(left.getImageResId());
        imgRightAvatar.setImageResource(right.getImageResId());
    }

    // Append new log line to the battle log
    private void appendToBattleLog(String msg) {
        String oldText = tvBattleLog.getText().toString();
        tvBattleLog.setText(oldText + "\n" + msg);
    }

    // Update skill button status and description
    private void updateSkillUI() {
        Skill skill = viewModel.getCurrentSkill();
        if (skill != null) {
            int uses = skill.getUsesLeft();
            int cooldown = skill.getCurrentCooldown();
            boolean available = (uses > 0 && cooldown == 0);
            btnSkill.setEnabled(available);
            btnSkill.setAlpha(available ? 1f : 0.4f);
            skillStatus.setText("Skill: " + uses + " left, " + cooldown + " turn cooldown");
        } else {
            btnSkill.setEnabled(false);
            btnSkill.setAlpha(0.4f);
            skillStatus.setText("Skill: unavailable");
        }
    }

    // Update both HP bars and text
    private void updateHpBars() {
        int leftHp = viewModel.getLeftHp();
        int leftMax = viewModel.getLeftMaxHp();
        int rightHp = viewModel.getRightHp();
        int rightMax = viewModel.getRightMaxHp();
        leftHealth.setText("HP: " + leftHp);
        rightHealth.setText("HP: " + rightHp);
        animateHpBar(leftHpBar, leftHp, leftMax);
        animateHpBar(rightHpBar, rightHp, rightMax);
    }

    // Animate HP bar value change
    private void animateHpBar(ProgressBar bar, int hp, int maxHp) {
        bar.setMax(maxHp);
        ObjectAnimator animator = ObjectAnimator.ofInt(bar, "progress", bar.getProgress(), hp);
        animator.setDuration(400);
        animator.start();
    }

    // Update active buff icons for both Lutemons
    private void updateBuffIcons() {
        leftBuffIcons.removeAllViews();
        rightBuffIcons.removeAllViews();
        for (String tag : viewModel.getActiveBuffs(true)) {
            leftBuffIcons.addView(createBuffIcon(tag));
        }
        for (String tag : viewModel.getActiveBuffs(false)) {
            rightBuffIcons.addView(createBuffIcon(tag));
        }
    }

    // Create a single icon view for a buff
    private ImageView createBuffIcon(String tag) {
        ImageView icon = new ImageView(getContext());
        if (tag.equals("atk_up")) {
            icon.setImageResource(R.drawable.ic_sword);
        } else if (tag.equals("def_down")) {
            icon.setImageResource(R.drawable.ic_shield_break);
        } else {
            icon.setImageResource(R.drawable.ic_star);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(60, 60);
        params.setMarginEnd(8);
        icon.setLayoutParams(params);
        return icon;
    }

    // Check if battle is over, and delay showing dialog
    private void checkBattleEnd() {
        if (viewModel.isBattleOver().getValue() != null && viewModel.isBattleOver().getValue() && !battleOverDialogShown) {
            battleOverDialogShown = true;
            new Handler().postDelayed(this::showBattleEndDialog, 500);
        }
    }

    // Show result dialog with winner info and EXP gained
    private void showBattleEndDialog() {
        playVictoryFlash();
        appendToBattleLog("Battle ended. Winner: " + viewModel.getWinnerName());
        new AlertDialog.Builder(getContext())
                .setTitle("🎉 Battle Over")
                .setMessage("🏆 Winner: " + viewModel.getWinnerName() +
                        "\nEXP gained: " + viewModel.getExpGained().getValue())
                .setPositiveButton("Return", (dialog, which) -> {
                    NavHostFragment.findNavController(BattleFragment.this).popBackStack();
                })
                .setCancelable(false)
                .show();
    }

    private void playVictoryFlash() {
        ViewGroup root = (ViewGroup) getView();
        View flash = new View(requireContext());
        flash.setBackgroundColor(Color.YELLOW);
        flash.setAlpha(0f);
        root.addView(flash, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        flash.animate().alpha(0.8f).setDuration(100).withEndAction(() -> {
            flash.animate().alpha(0f).setDuration(400).withEndAction(() -> {
                root.removeView(flash);
            }).start();
        }).start();
    }

    // Animate button click with brief flash
    private void animateButton(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1.0f);
        anim.setDuration(200);
        anim.setRepeatCount(1);
        anim.setRepeatMode(AlphaAnimation.REVERSE);
        view.startAnimation(anim);
    }
}