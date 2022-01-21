package ee.linde.jitevolution.services.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public final class ProjectConfigs {
    private static final String PROJECT_CONFIG_DIRECTORY = ".jit-evolution";
    private static final String PROJECT_CONFIG_FILENAME = "config.json";
    private static final String PROJECT_CONFIG_ID = "projectId";

    public static String ensureProjectConfigs() throws IOException {
        File directory = new File(PROJECT_CONFIG_DIRECTORY);
        if (!directory.exists()){
            directory.mkdir();
        }
        boolean shouldCreateConfig = false;
        String projectId = "";

        File configFile = new File(directory.getName() + "/" + PROJECT_CONFIG_FILENAME);
        if (configFile.isFile()){
            try {
                JsonElement json = JsonParser.parseReader(new FileReader(configFile.getAbsoluteFile()));
                if (json.isJsonObject()){
                    JsonObject obj = json.getAsJsonObject();
                    if (obj != null && obj.has(PROJECT_CONFIG_ID)){
                        projectId = obj.get(PROJECT_CONFIG_ID).getAsString();
                    }
                    else{
                        shouldCreateConfig = true;
                    }
                } else{
                    shouldCreateConfig = true;
                }

            }
            catch (JsonSyntaxException ex){
                shouldCreateConfig = true;
            }
        }
        else{
            shouldCreateConfig = true;
        }

        if (shouldCreateConfig){
            projectId = configFile.getAbsoluteFile().getParentFile().getParentFile().getName() + "-" + UUID.randomUUID();
            writeConfigFile(configFile, projectId);
        }

        return projectId;
    }

    private static void writeConfigFile(File file, String projectId) throws IOException {
        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        fileWriter.write(String.format("{\n\t\"%s\": \"%s\"\n}", PROJECT_CONFIG_ID, projectId));
        fileWriter.flush();
    }
}
