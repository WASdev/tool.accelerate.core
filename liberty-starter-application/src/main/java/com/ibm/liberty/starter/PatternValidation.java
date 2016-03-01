package com.ibm.liberty.starter;

import java.util.regex.Pattern;

public class PatternValidation {

    public enum PatternType {
        TECH, NAME, PATH_EXTENSION, DEPLOY
    }

    public static boolean checkPattern(PatternType patternType, String object) {
        boolean patternPassed = false;
        Pattern pattern = getPattern(patternType);
        if (pattern.matcher(object).matches()) {
            patternPassed = true;
        }
        return patternPassed;
    }

    private static Pattern getPattern(PatternType patternType) {
        Pattern pattern = Pattern.compile("");
        switch (patternType) {
            case TECH:
                pattern = Pattern.compile("[a-z]*");
                break;
            case NAME:
                pattern = Pattern.compile("[a-zA-Z0-9_-]*");
                break;
            case PATH_EXTENSION:
                pattern = Pattern.compile("[a-z0-9-_/.]*");
                break;
            case DEPLOY:
                pattern = Pattern.compile("[a-z]*");
        }
        return pattern;
    }

}
