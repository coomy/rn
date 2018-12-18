package coomy.rn;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ElementDeserializer implements JsonDeserializer<Element> {
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
        if (childs!=null && !childs.isJsonNull()) {
            JsonArray jsonChildrensArray = childs.getAsJsonArray();
            childrens = new Element[jsonChildrensArray.size()];
            for (int i = 0; i < childrens.length; i++) {
                JsonElement jsonChildren = jsonChildrensArray.get(i);
                if (!jsonChildren.isJsonNull() && jsonChildren.isJsonObject()) {
                    childrens[i] = deserialize(jsonChildren, typeOfT, context);
                }
                else {
                    childrens[i] = null; // 这个地方怎么处理。。如果特殊化，必须重写Serializer了。那两个都重写了，要考虑下TypeAdapter。
                }
            }
        }

        Element el = new Element();
        el.tagName = tagName;
        el.props = props;
        el.childrens = childrens;
        el.key = "xxx";

        return el;
    }
}
