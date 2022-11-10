import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class JsonParser {

    PropertiesGetter prop;
    ArrayList<JSONArray> files;
    ArrayList<JSONArray> elementList;
    String inputPath;
    String outputPath;

    JsonParser(){
        prop = new PropertiesGetter("config.properties");
        inputPath = prop.getPropData("INPUT_PATH");
        outputPath = prop.getPropData("OUTPUT_PATH");

        File f = new File(inputPath);
        files = new ArrayList<JSONArray>();
        elementList = new ArrayList<JSONArray>();

        String[] pathNames = f.list();

        for (int i = 0; i < Objects.requireNonNull(pathNames).length; i++) {

            try (FileReader reader = new FileReader(inputPath + pathNames[i])) {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(reader);

                JSONArray array = new JSONArray();
                array.add(obj);
                files.add(array);


            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void processFiles(){
        for (JSONArray file: files) {
            file.forEach(php -> this.parseObject((JSONObject) php));
        }

        for(int i=0; i<elementList.size(); i++)
        {
            createJson(elementList.get(i), i);
        }
    }

    private void parseObject(JSONObject PHPExpresion)
    {
        JSONObject nameObject = (JSONObject) PHPExpresion.get("intList");
        JSONObject nameListBase = (JSONObject) PHPExpresion.get("stringList");
        JSONArray nameList = (JSONArray) nameListBase.get("facenames");

        JSONArray element = new JSONArray();

        for (String s : (Iterable<String>) nameList) {
            String object = s.toLowerCase();
            JSONArray test = (JSONArray) nameObject.get("" + object);
            element.add(test);
        }
        elementList.add(element);
    }

    private void createJson(JSONArray elementList, int fileNumber){
        {
            JSONObject obj = new JSONObject();

            // obj.put("messages", list);
            JSONObject testObj = new JSONObject();
            ArrayList<JSONObject> filesJson = new ArrayList<JSONObject>();

            for (Object o : elementList) {
                JSONArray modifierList = new JSONArray();
                JSONArray phonemeList = new JSONArray();

                for (int j = 0; j < ((JSONArray) o).size(); j++) {
                    switch (j) {
                        case 0:
                            //System.out.println(0);
                            testObj.put("type", ((JSONArray) o).get(j));
                            // System.out.println(testObj);
                            break;
                        case 1:
                            testObj.put("baseValue", ((JSONArray) o).get(j));
                            obj.put("expression", testObj);
                            break;
                        default:
                            JSONObject phonemeObj = new JSONObject();
                            JSONObject modifierObj = new JSONObject();
                            if (j <= 15) {
                                modifierObj.put("type", j - 2);
                                modifierObj.put("baseValue", ((JSONArray) o).get(j));
                                modifierObj.put("excitementMultiplier", 0);

                                modifierList.add(modifierObj);
                            } else {
                                phonemeObj.put("type", j - 16);
                                phonemeObj.put("baseValue", ((JSONArray) o).get(j));
                                phonemeObj.put("excitementMultiplier", 0);

                                phonemeList.add(phonemeObj);
                            }

                    }

                }

                JSONObject modifierFinal = new JSONObject();
                modifierFinal.put("modifiers", modifierList);

                JSONObject phonemeFinal = new JSONObject();
                phonemeFinal.put("phonemes", modifierList);

                JSONArray preCompleteJson = new JSONArray();
                preCompleteJson.add(obj);
                preCompleteJson.add(modifierFinal);
                preCompleteJson.add(phonemeFinal);


                JSONObject completeJson = new JSONObject();
                completeJson.put("female", preCompleteJson);

                filesJson.add(completeJson);

            }
            WriteFiles(filesJson, fileNumber);
        }
}

    private void  WriteFiles(ArrayList<JSONObject> filesJson, int fileNumber ) {
            System.out.println(filesJson.size());

        for(int i=0; i<filesJson.size(); i++){
            FileWrite((filesJson.get(i)), fileNumber+1, i+1);
        }
    }


    private void FileWrite (JSONObject writeTarget, int fileNumber, int expressionNumber){

        try (FileWriter file = new FileWriter(outputPath + "/expression" + fileNumber + expressionNumber + ".json")) {
            file.write(writeTarget.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
