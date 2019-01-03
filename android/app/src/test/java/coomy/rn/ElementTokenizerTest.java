package coomy.rn;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ElementTokenizerTest {

    private Gson gson = new GsonBuilder().registerTypeAdapter(Element.class, new ElementTokenizer()).create();
    String Json = "{\"tagName\":\"div\",\"props\":{\"id\":\"container\"},\"childrens\":[{\"tagName\":\"h1\",\"props\":{\"style\":\"color: red\"},\"childrens\":[\"simple virtual dom\"]},{\"tagName\":\"p\",\"props\":{},\"childrens\":[\"hello world\"]}]}";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void deserialize() {
        Element el = gson.fromJson(Json, Element.class);
        assertEquals("div", el.getTagName());

        Element []elc = el.getChildrens();
        assertEquals(2, elc.length);

        assertEquals("h1", elc[0].getTagName());
        assertEquals(1, elc[0].getChildrens().length);
        assertEquals("simple virtual dom", elc[0].getChildrens()[0].getTagName());

        assertEquals("p", elc[1].getTagName());
        assertEquals(1, elc[1].getChildrens().length);
        assertEquals("hello world", elc[1].getChildrens()[0].getTagName());
    }

    @Test
    public void serialize() {
        Element el = gson.fromJson(Json, Element.class);
        String jsonnn = gson.toJson(el);
        assertEquals(Json, jsonnn);
    }
}