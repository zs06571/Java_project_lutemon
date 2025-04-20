package com.example.java_project_lutemon.core.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData; // 添加这行导入
import androidx.lifecycle.Transformations;

import com.example.java_project_lutemon.core.skill.AttackBuffSkill;
import com.example.java_project_lutemon.core.skill.BalancedBuffSkill;
import com.example.java_project_lutemon.core.skill.DebuffSkill;
import com.example.java_project_lutemon.core.skill.DefenseBuffSkill;
import com.example.java_project_lutemon.core.skill.HealSkill;
import com.example.java_project_lutemon.core.skill.Skill;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.state.GameState;
import com.example.java_project_lutemon.core.model.White;
import com.example.java_project_lutemon.core.model.Green;
import com.example.java_project_lutemon.core.model.Black;
import com.example.java_project_lutemon.core.model.Pink;
import com.example.java_project_lutemon.core.model.Orange;
import com.example.java_project_lutemon.utils.gson.RuntimeTypeAdapterFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LutemonRepository {
    private final List<Lutemon> lutemons;
    private final MutableLiveData<List<Lutemon>> lutemonsLiveData = new MutableLiveData<>();
    private static LutemonRepository instance;
    private Map<Integer, Lutemon> lutemonMap = new HashMap<>();
    private static final String SAVE_FILE_NAME = "lutemon_save.json";

    public LutemonRepository() {
        lutemons = new ArrayList<>();
        lutemonsLiveData.setValue(new ArrayList<>());
    }

    public static LutemonRepository getInstance() {
        if (instance == null) {
            instance = new LutemonRepository();
        }
        return instance;
    }

    public void addLutemon(Lutemon lutemon) {
        synchronized (this) {
            lutemons.add(lutemon);
            lutemonsLiveData.postValue(new ArrayList<>(lutemons));
        }
    }

    public LiveData<List<Lutemon>> getAllLutemonsLiveData() {
        return Transformations.distinctUntilChanged(lutemonsLiveData);
    }

    public List<Lutemon> getAllLutemons() {
        return new ArrayList<>(lutemons);
    }

    public Lutemon getLutemonById(int id) {
        for (Lutemon l : lutemons) {
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }

    public List<Lutemon> getAvailableLutemons() {
        List<Lutemon> available = new ArrayList<>();
        for (Lutemon l : lutemons) {
            if (l.getState() == GameState.REST) {
                available.add(l);
            }
        }
        return available;
    }

    public void deleteLutemon(int id) {
        Iterator<Lutemon> iterator = lutemons.iterator();
        while (iterator.hasNext()) {
            Lutemon l = iterator.next();
            if (l.getId() == id) {
                iterator.remove();
                break;
            }
        }
        lutemonsLiveData.setValue(new ArrayList<>(lutemons));
    }


    public void updateLutemonState(int id, GameState newState) {
        for (Lutemon lutemon : lutemons) {
            if (lutemon.getId() == id) {
                lutemon.setState(newState);
                break;
            }
        }
        lutemonsLiveData.setValue(new ArrayList<>(lutemons));
    }

    private final AtomicInteger idCounter = new AtomicInteger(0);

    public int generateId() {
        return idCounter.incrementAndGet();
    }

    public void updateLutemon(Lutemon updatedLutemon) {
        synchronized (this) {
            for (int i = 0; i < lutemons.size(); i++) {
                if (lutemons.get(i).getId() == updatedLutemon.getId()) {
                    lutemons.set(i, updatedLutemon);
                    break;
                }
            }
            lutemonsLiveData.postValue(new ArrayList<>(lutemons));
        }
    }

    public List<Lutemon> getLutemonsByState(GameState state) {
        return lutemons.stream()
                .filter(l -> l.getState() == state)
                .collect(Collectors.toList());
    }

    public void saveLutemon(Lutemon lutemon) {
        for (int i = 0; i < lutemons.size(); i++) {
            if (lutemons.get(i).getId() == lutemon.getId()) {
                lutemons.set(i, lutemon);
                break;
            }
        }
        lutemonMap.put(lutemon.getId(), lutemon);

        lutemonsLiveData.postValue(new ArrayList<>(lutemons));
    }

    public void saveToFile(Context context) {
        try {
            for (Lutemon l : LutemonRepository.getInstance().getAllLutemons()) {
            }
            Gson gson = buildGsonWithLutemonTypes();
            String json = gson.toJson(lutemons);
            FileOutputStream fos = context.openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput(SAVE_FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            Gson gson = buildGsonWithLutemonTypes();
            Type listType = new TypeToken<ArrayList<Lutemon>>() {}.getType();
            List<Lutemon> loaded = gson.fromJson(builder.toString(), listType);
            lutemons.clear();
            lutemons.addAll(loaded);
            lutemonsLiveData.postValue(new ArrayList<>(lutemons));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Gson buildGsonWithLutemonTypes() {
        RuntimeTypeAdapterFactory<Lutemon> lutemonAdapter =
                RuntimeTypeAdapterFactory.of(Lutemon.class, "type")
                        .registerSubtype(White.class, "white")
                        .registerSubtype(Green.class, "green")
                        .registerSubtype(Black.class, "black")
                        .registerSubtype(Pink.class, "pink")
                        .registerSubtype(Orange.class, "orange");

        RuntimeTypeAdapterFactory<Skill> skillAdapter =
                RuntimeTypeAdapterFactory.of(Skill.class, "skillType")
                        .registerSubtype(HealSkill.class, "healSkill")
                        .registerSubtype(AttackBuffSkill.class, "attackBuffSkill")
                        .registerSubtype(DefenseBuffSkill.class, "defenseBuffSkill")
                        .registerSubtype(DebuffSkill.class, "debuffSkill")
                        .registerSubtype(BalancedBuffSkill.class, "balancedBuffSkill");

        return new GsonBuilder()
                .registerTypeAdapterFactory(lutemonAdapter)
                .registerTypeAdapterFactory(skillAdapter)
                .create();
    }

    public void clearAll() {
        lutemons.clear();
        lutemonsLiveData.postValue(new ArrayList<>(lutemons));
    }
}
