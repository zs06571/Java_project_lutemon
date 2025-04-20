package com.example.java_project_lutemon.core.model;

public class LutemonFactory {
    public static Lutemon create(LutemonType type, String name, int id) {
        Lutemon l;
        switch (type) {
            case WHITE:
                l = new White(name, type.getColorResId(), id);
                break;
            case GREEN:
                l = new Green(name, type.getColorResId(), id);
                break;
            case PINK:
                l = new Pink(name, type.getColorResId(), id);
                break;
            case BLACK:
                l = new Black(name, type.getColorResId(), id);
                break;
            case ORANGE:
                l = new Orange(name, type.getColorResId(), id);
                break;
            default:
                throw new IllegalArgumentException("Unknown Lutemon type");
        }
        l.setType(type);
        return l;
    }
}
