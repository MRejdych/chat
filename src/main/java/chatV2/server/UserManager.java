package chatV2.server;

import chatV2.common.data.UserInfo;
import chatV2.common.utils.StreamUtilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public final class UserManager {
    private static final String USERDAT_FILENAME = "users";
    private static UserManager instance = null;
    private final String userdatPath;

    public static UserManager getInstance() {
        return instance;
    }

    public static void createInstance(String userdatDir) {
        instance = new UserManager(userdatDir + USERDAT_FILENAME);
    }


    private UserManager(String userdatPath) {
        this.userdatPath = userdatPath;
        if (!new File(userdatPath).exists()) {
            try {
                JSONArray jsonArray = new JSONArray("[]");
                updateToFile(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public UserInfo getUserInfo( String username) {
        try {
            JSONTokener jsonTokener = new JSONTokener(readAllString());
            JSONArray jsonArray = new JSONArray(jsonTokener);
            int countOfObject = jsonArray.length();
            for (int i = 0; i < countOfObject; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("username").equals(username)) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setAccountId(jsonObject.getInt("id"));
                    userInfo.setUserName(jsonObject.getString("username"));
                    userInfo.setState(UserInfo.STATE_ONLINE);
                    return userInfo;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<UserInfo> getAllUsersInfos() {
        List<UserInfo> users = new ArrayList<>();
        try {
            JSONTokener jsonTokener = new JSONTokener(readAllString());
            JSONArray jsonArray = new JSONArray(jsonTokener);
            int countOfObject = jsonArray.length();
            for (int i = 0; i < countOfObject; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                UserInfo userInfo = new UserInfo();
                userInfo.setAccountId(jsonObject.getInt("id"));
                userInfo.setUserName(jsonObject.getString("username"));
                userInfo.setState(UserInfo.STATE_OFFLINE);
                users.add(userInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private synchronized void updateToFile(JSONArray jsonArray) {
        OutputStream out = null;
        StringWriter writer = new StringWriter();
        File userdatFile = new File(userdatPath);
        userdatFile.delete();
        try {
            jsonArray.write(writer);
            out = new FileOutputStream(userdatFile);
            out.write(writer.toString().getBytes(Charset.forName("UTF-8")));
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtilities.tryCloseStream(out, writer);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private synchronized String readAllString() {
        BufferedReader reader = null;
        File userdatFile = new File(userdatPath);
        try {
            if (!userdatFile.exists()) {
                userdatFile.createNewFile();
            }

            reader = new BufferedReader(new FileReader(userdatFile));
            char[] buffer = new char[1024 * 32];
            int length = reader.read(buffer);
            if (length > 0) {
                return new String(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtilities.tryCloseStream(reader);
        }
        return "";
    }

    private boolean checkExistsUser(String username) {
        try {
            JSONTokener jsonTokener = new JSONTokener(readAllString());
            JSONArray jsonArray = new JSONArray(jsonTokener);
            int countOfObject = jsonArray.length();
            for (int i = 0; i < countOfObject; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("username").equals(username)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUser( String username) {
        Random rand = new Random(new Date().getTime());

        if (checkExistsUser(username))
            return;
        try {
            JSONTokener jsonTokener = new JSONTokener(readAllString());
            JSONArray jsonArray = new JSONArray(jsonTokener);
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("id", rand.nextInt(Integer.MAX_VALUE));
            jsonObject.put("username", username);

            jsonArray.put(jsonObject);

            updateToFile(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
