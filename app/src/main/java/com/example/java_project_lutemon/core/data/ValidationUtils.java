package com.example.java_project_lutemon.core.data;

import com.example.java_project_lutemon.core.model.Lutemon;
import com.example.java_project_lutemon.core.skill.Skill;
import java.util.List;

public class ValidationUtils {
    public static boolean isValidName(String name) {
        return name != null && name.length() >= 2 && name.length() <= 10;
    }

    public static boolean isSkillUsable(Lutemon lutemon, int skillId) {
        try {
            List<Skill> skills = lutemon.getSkillList();
            return skillId >= 0 && skillId < skills.size();
        } catch (UnsupportedOperationException e) {
            ErrorHandler.logError("ValidationUtils", "Lutemon doesn't support skills");
            return false;
        }
    }
}