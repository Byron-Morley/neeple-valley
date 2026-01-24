package com.liquidpixel.main.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.liquidpixel.main.dto.agent.Agent;
import com.liquidpixel.main.dto.ai.JobDto;
import com.liquidpixel.main.interfaces.IRecipe;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.model.item.Recipe;
import com.liquidpixel.main.model.item.RecipeDTO;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.model.person.Person;
import com.liquidpixel.main.model.sprite.BodyOffset;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.main.model.sprite.RawAnimationModel;
import com.liquidpixel.main.model.terrain.TilesetConfig;
import com.liquidpixel.main.model.ui.MenuTreeItem;
import com.liquidpixel.main.serialisers.Recipe.RecipeDTODeserializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ModelFactory implements Disposable {
    private static final String AGENTS_MODEL_PATH = "assets/model/entities/agents.yaml";
    private static final String ANIMATIONS_MODEL_PATH = "assets/model/entities/animations.yaml";
    private static final String ATLAS_MODEL_PATH = "assets/model/textures/atlas.yaml"; // Changed from .json to .yaml
    private static final String ITEMS_MODEL_PATH = "assets/model/entities/items/items.yaml";
    private static final String RECIPES_MODEL_PATH = "assets/model/objects/recipes.yaml";
    private static final String TERRAIN_MODELS_PATH = "assets/model/map/summer-terrain.yaml";
    private static final String JOBS_PATH = "assets/model/behaviors/jobs.yaml";
    private static final String BEHAVIORS_TREES_DIRECTORY = "assets/model/trees/";
    private static final String BEHAVIOR_TREES_PATH = "assets/model/trees/trees.yaml";
    private static final String MENU_PATH = "assets/model/ui/menu.yaml";
    private static final String BODY_OFFSET_MODEL_PATH = "assets/model/objects/bodies.yaml";
    private static final String PEOPLE_JSON_PATH = "assets/model/data/people.yaml";
    private static final String RAMPS_PATH = "assets/raw/ramps/ramps.json";


    private static Map<String, Object> cache = new HashMap<>();
    private static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    private static ObjectMapper recipeMapper = new ObjectMapper(new YAMLFactory())
        .registerModule(new SimpleModule().addDeserializer(HashMap.class, new RecipeDTODeserializer()));


    public static List<Person> getPeopleData() {
        if (!cache.containsKey(PEOPLE_JSON_PATH)) {
            try {
                List<Person> allPeople = new ArrayList<>();

                // Read the YAML file that contains paths to CSV files
                TypeReference<List<String>> typeRef = new TypeReference<>() {
                };
                List<String> csvPaths = (List<String>) readModel(PEOPLE_JSON_PATH, typeRef);

                // Process each CSV file
                for (String csvPath : csvPaths) {
                    FileHandle fileHandle = Gdx.files.local(csvPath);
                    String content = fileHandle.readString();
                    String[] lines = content.split("\n");

                    // Skip header line
                    for (int i = 1; i < lines.length; i++) {
                        String line = lines[i].trim();
                        if (!line.isEmpty()) {
                            // Split by comma but handle quoted values
                            String[] parts = parseCSVLine(line);

                            if (parts.length >= 3) {
                                String lastName = parts[0].trim();
                                String firstName = parts[1].trim();
                                String gender = parts[2].trim();

                                allPeople.add(new Person(lastName, firstName, gender));
                            }
                        }
                    }
                }
                cache.put(PEOPLE_JSON_PATH, allPeople);
            } catch (Exception e) {
                Gdx.app.error("ModelFactory", "Error loading people data", e);
                e.printStackTrace(); // Add this to see the full stack trace
                cache.put(PEOPLE_JSON_PATH, new ArrayList<Person>());
            }
        }

        return (List<Person>) cache.get(PEOPLE_JSON_PATH);
    }

    // Helper method to parse CSV lines with quoted values
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString().replace("\"", "").trim());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }

        // Add the last field
        result.add(field.toString().replace("\"", "").trim());

        return result.toArray(new String[0]);
    }


    public static Map<String, RawAnimationModel> getAnimationsModels() {
        TypeReference<HashMap<String, RawAnimationModel>> typeRef = new TypeReference<>() {
        };
        return (Map<String, RawAnimationModel>) readModel(ANIMATIONS_MODEL_PATH, typeRef);
    }

    public static List<MenuTreeItem> getMenuTree() {
        TypeReference<List<MenuTreeItem>> typeRef = new TypeReference<>() {
        };
        return (List<MenuTreeItem>) readModel(MENU_PATH, typeRef);
    }

    public static Set<String> getAtlasSpriteModels() {
        TypeReference<HashSet<String>> typeRef = new TypeReference<HashSet<String>>() {
        };
        return (Set<String>) readModel(ATLAS_MODEL_PATH, typeRef);
    }

    public static Map<String, Agent> getAgentsModel() {
        TypeReference<HashMap<String, Agent>> typeRef = new TypeReference<HashMap<String, Agent>>() {
        };
        return (Map<String, Agent>) readModel(AGENTS_MODEL_PATH, typeRef);
    }

    public static Map<String, BodyOffset> getBodyOffsetModel() {
        TypeReference<HashMap<String, BodyOffset>> typeRef = new TypeReference<>() {
        };
        return (Map<String, BodyOffset>) readModel(BODY_OFFSET_MODEL_PATH, typeRef);
    }

    public static TilesetConfig getTerrainModel() {
        return (TilesetConfig) readModel(TERRAIN_MODELS_PATH, TilesetConfig.class, objectMapper);
    }

    public static Map<String, Item> getItemsModel() {

        TypeReference<HashSet<String>> typeRef = new TypeReference<>() {
        };
        TypeReference<HashMap<String, Item>> itemRef = new TypeReference<>() {
        };

        Set<String> files = (Set<String>) readModel(ITEMS_MODEL_PATH, typeRef);

        Map<String, Item> items = new HashMap<>();

        for (String path : files) {
            Map<String, Item> models = (Map<String, Item>) readModel(path, itemRef);

            for (String key : models.keySet()) {
                Item item = models.get(key);
                String itemKey = createItemKey(path, "assets/model/entities/items/");
                item.setName(itemKey + "/" + key);
                items.put(itemKey + "/" + key, item);
            }
        }
        return items;
    }


    private static String createItemKey(String filePath, String folderPath) {
        int startIndex = filePath.indexOf(folderPath) + folderPath.length();
        int endIndex = filePath.lastIndexOf(".yaml"); // Changed from .json to .yaml
        return filePath.substring(startIndex, endIndex);
    }

    public static Map<String, IRecipe> getRecipesModel(Map<String, Item> items, ISpriteFactory spriteFactory) {
        TypeReference<HashMap<String, RecipeDTO>> typeRef = new TypeReference<>() {
        };

        // Make sure we're using the YAML mapper for recipes
        Map<String, RecipeDTO> recipesDTOs = (Map<String, RecipeDTO>) readModel(RECIPES_MODEL_PATH, typeRef, recipeMapper);

        Map<String, IRecipe> recipes = new HashMap<>();

        for (RecipeDTO dto : recipesDTOs.values()) {
            List<IStorageItem> inputs = new ArrayList<>();

            dto.getInput().forEach(input -> {
                Item item = items.get(input.getName());
                if (item != null) {
                    GameSprite sprite = spriteFactory.getSprite(item.getSpriteName());
                    inputs.add(new StorageItem(item.getName(), input.getQuantity(), item.getStackSize(), sprite));
                } else {
                    System.out.println("Input item not found: " + input.getName());
                }
            });

            List<IStorageItem> outputs = new ArrayList<>();
            dto.getOutput().forEach(output -> {
                Item item = items.get(output.getName());
                if (item != null) {
                    GameSprite sprite = spriteFactory.getSprite(item.getSpriteName());
                    outputs.add(new StorageItem(item.getName(), output.getQuantity(), item.getStackSize(), sprite));
                } else {
                    System.out.println("Output item not found: " + output.getName());
                }
            });

            Recipe recipe = new Recipe(
                dto.getName(),
                inputs,
                outputs,
                dto.getEffort()
            );

            recipes.put(dto.getName(), recipe);
        }

        return recipes;
    }


    public static Map<String, JobDto> getJobs() {
        TypeReference<HashMap<String, JobDto>> typeRef = new TypeReference<>() {
        };
        return (Map<String, JobDto>) readModel(JOBS_PATH, typeRef);
    }


    private static Object readModel(String modelPath, TypeReference typeRef) {
        return readModel(modelPath, typeRef, objectMapper);
    }


    private static Object readModel(String modelPath, TypeReference typeRef, ObjectMapper mapper) {
        if (!cache.containsKey(modelPath)) {
            try {
                Object o = mapper.readValue(Gdx.files.local(modelPath).file(), typeRef);
                cache.put(modelPath, o);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cache.get(modelPath);
    }

    private static Object readModel(String modelPath, Class clazz, ObjectMapper mapper) {
        if (!cache.containsKey(modelPath)) {
            try {
                Object o = mapper.readValue(Gdx.files.local(modelPath).file(), clazz);
                cache.put(modelPath, o);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cache.get(modelPath);
    }

    public static Set<String> getBehaviorTrees() {
        TypeReference<HashSet<String>> typeRef = new TypeReference<HashSet<String>>() {
        };
        return (Set<String>) readModel(BEHAVIOR_TREES_PATH, typeRef);
    }

    public static FileHandle getBehaviorTree(String treeFile) {

        FileHandle file = null;
        try {
            file = Gdx.files.internal(BEHAVIORS_TREES_DIRECTORY + treeFile);
        } catch (Exception e) {
            Gdx.app.error("BehaviorTree", "Error parsing behavior tree", e);
        }

        return file;
    }

    private static Object readModelWithDefaultTyping(String path, Class clazz) {
        ObjectMapper customMapper = new ObjectMapper(new YAMLFactory()).enableDefaultTyping(); // Changed to use YAMLFactory

        try {
            return customMapper.readValue(Gdx.files.internal(path).file(), clazz);
        } catch (IOException e) {
            throw new Error(e.getMessage(), e);
        }
    }

    /**
     * Loads color ramps from ramp files listed in the ramps.yaml file.
     * Each file contains multiple ramps, and each ramp has a variable number of colors.
     *
     * @return Map where key is filename and value is a list of Color[] arrays
     */
    public static Map<String, List<Color[]>> getRampsModel() {
        if (!cache.containsKey(RAMPS_PATH)) {
            try {
                // First, load the ramps.yaml index file which contains a list of ramp files
                TypeReference<List<String>> indexRef = new TypeReference<List<String>>() {
                };
                List<String> rampFilePaths = (List<String>) readModel(RAMPS_PATH, indexRef);

                Map<String, List<Color[]>> allRamps = new HashMap<>();

                // Then load each ramp file listed in the index
                for (String rampFilePath : rampFilePaths) {
                    // Parse ramp file to get ramp name (filename without extension)
                    String rampFileName = rampFilePath.substring(rampFilePath.lastIndexOf('/') + 1);
                    String rampName = rampFileName.substring(0, rampFileName.lastIndexOf('.'));

                    // Load the individual ramp file
                    TypeReference<List<List<Map<String, Integer>>>> rampRef =
                        new TypeReference<List<List<Map<String, Integer>>>>() {
                        };

                    List<List<Map<String, Integer>>> rampFile =
                        (List<List<Map<String, Integer>>>) readModel(rampFilePath, rampRef);

                    // Process all ramps in the file
                    List<Color[]> rampList = new ArrayList<>();

                    for (List<Map<String, Integer>> rampColors : rampFile) {
                        // Create a Color array for this ramp with variable size
                        Color[] colorRamp = new Color[rampColors.size()];

                        // Fill in the colors
                        for (int i = 0; i < rampColors.size(); i++) {
                            Map<String, Integer> colorMap = rampColors.get(i);
                            int r = colorMap.get("r");
                            int g = colorMap.get("g");
                            int b = colorMap.get("b");
                            colorRamp[i] = new Color(r, g, b, 255);
                        }

                        rampList.add(colorRamp);
                    }

                    allRamps.put(rampName, rampList);
                }

                cache.put(RAMPS_PATH, allRamps);
            } catch (Exception e) {
                Gdx.app.error("ModelFactory", "Error loading ramps data", e);
                e.printStackTrace();
                cache.put(RAMPS_PATH, new HashMap<String, List<Color[]>>());
            }
        }

        return (Map<String, List<Color[]>>) cache.get(RAMPS_PATH);
    }

    @Override
    public void dump() {
        cache.clear();
    }
}
