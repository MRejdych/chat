package chatV2.common.utils;

import java.util.regex.Pattern;

public final class Validator {
    private static final Pattern userNamePattern = Pattern.compile("^[a-zA-Z0-9_-]{3,15}$");

    public static boolean checkValidUsername(String username) {
        return username != null && userNamePattern.matcher(username).matches();
    }
}
