import java.io.*;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonFileParser
{
    ArrayList<JSONArray> elementList;

    JsonFileParser(){
        elementList = new  ArrayList<JSONArray> ();
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        File f = new File("resources/input");
        String[] pathnames = f.list();

        for (int i = 0; i < pathnames.length; i++) {

            try (FileReader reader = new FileReader("resources/input/" + pathnames[i])) {
                //Read JSON file
                JsonFileParser parseObject = new JsonFileParser();
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(reader);
                JSONArray array = new JSONArray();

                array.add(obj);

                array.forEach(php -> parseObject.parseObject((JSONObject) php));
                parseObject.createJson(i);


            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }


    public void parseObject(JSONObject PHPExpresion)
    {
        JSONObject nameObject = (JSONObject) PHPExpresion.get("intList");
        JSONObject nameListBase = (JSONObject) PHPExpresion.get("stringList");
        JSONArray nameList = (JSONArray) nameListBase.get("facenames");

        for (String s : (Iterable<String>) nameList) {
            String object = s.toLowerCase();
            JSONArray test = (JSONArray) nameObject.get("" + object);
            elementList.add(test);
        }
    }

    public void createJson(int number)
    {
        JSONObject obj = new JSONObject();

       // obj.put("messages", list);
        JSONObject testObj = new JSONObject();

        for (int i=0; i<elementList.size(); i++){
            JSONArray modifierList = new JSONArray();
            JSONArray phonemeList = new JSONArray();

            for (int j = 0; j< elementList.get(i).size(); j++){

                    switch(j){
                        case 0 :
                            //System.out.println(0);
                            testObj.put("type", (elementList.get(i)).get(j));
                           // System.out.println(testObj);
                            break;
                        case 1 :
                            testObj.put("baseValue", (elementList.get(i)).get(j));
                           // System.out.println(testObj);
                            obj.put("expression", testObj);
                            //System.out.println(obj);
                            break;
                        default :
                            if(j<=15){

                                JSONObject phonemeObj =  new JSONObject();
                                phonemeObj.put("type", j-2);
                                phonemeObj.put("baseValue", (elementList.get(i)).get(j));
                                phonemeObj.put("excitementMultiplier", 0);

                                phonemeList.add(phonemeObj);
                            }
                            else{
                                JSONObject modifierObj =  new JSONObject();
                                modifierObj.put("type", j-16);
                                modifierObj.put("baseValue", (elementList.get(i)).get(j));
                                modifierObj.put("excitementMultiplier", 0);

                                modifierList.add(modifierObj);
                            }

                    }

            }

            JSONObject phonemeFinal = new JSONObject();
            phonemeFinal.put("phonemes", phonemeList);

            JSONObject modifierFinal = new JSONObject();
            modifierFinal.put("modifiers", modifierList);

            JSONArray preCompleteJson = new JSONArray();
            preCompleteJson.add(obj);
            preCompleteJson.add(phonemeFinal);
            preCompleteJson.add(modifierFinal);


            JSONObject completeJson = new JSONObject();
            completeJson.put("female", preCompleteJson);

            FileWrite(completeJson, i, number);
        }
    }

    void FileWrite (JSONObject writeTarget, Integer number, int number2){
          try (FileWriter file = new FileWriter("resources/output/expression" + number2 + number + ".json")) {
            file.write(writeTarget.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}