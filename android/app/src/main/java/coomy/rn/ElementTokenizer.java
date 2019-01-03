package coomy.rn;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class ElementTokenizer implements JsonDeserializer<Element>, JsonSerializer<Element> {
    @Override
    public Element deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String tagName = jsonObject.get("tagName").getAsString();

        JsonElement strProps = jsonObject.get("props");
        Type mapType = new TypeToken<HashMap<String, String>>() {}.getType();
        HashMap<String, String> props =  context.deserialize(strProps, mapType);

        JsonElement childs = jsonObject.get("childrens");
        Element[] childrens = null;
        if (childs!=null && !childs.isJsonNull() && childs.isJsonArray()) {
            JsonArray jsonChildrensArray = childs.getAsJsonArray();
            childrens = new Element[jsonChildrensArray.size()];
            for (int i = 0; i < childrens.length; i++) {
                JsonElement jsonChildren = jsonChildrensArray.get(i);
                if (!jsonChildren.isJsonNull()) {
                    if (jsonChildren.isJsonObject()||jsonChildren.isJsonArray()) {
                        childrens[i] = deserialize(jsonChildren, typeOfT, context);
                    } else if (jsonChildren.isJsonPrimitive()) {
                        childrens[i] = new Element();
                        childrens[i].setTagName(jsonChildren.getAsString());
                        childrens[i].setText(true);
                    }
                }
            }
        }

        Element el = new Element();
        el.setTagName(tagName);
        el.setProps(props);
        el.setChildrens(childrens);
        el.setKey("xxx");

        return el;
    }

    @Override
    public JsonElement serialize(Element src, Type typeOfSrc, JsonSerializationContext context) {
        if (src==null)
            return null;

        JsonObject obj = new JsonObject();
        Gson gson = new Gson();

        if (!src.isText()) {
            obj.addProperty("tagName", src.getTagName());

            Type mapType = new TypeToken<HashMap<String, String>>() {}.getType();
            obj.add("props", context.serialize(src.getProps(), mapType));

            JsonArray arr = new JsonArray();
            Element[] childrens = src.getChildrens();
            for (int i=0; i<childrens.length; i++) {
                arr.add(serialize(childrens[i], typeOfSrc, context));
            }
            obj.add("childrens", arr);
        } else {
            return new JsonPrimitive(src.getTagName());
        }

        return obj;
    }
}
