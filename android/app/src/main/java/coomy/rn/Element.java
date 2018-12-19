package coomy.rn;

import java.util.HashMap;

public class Element {
    private String tagName;
    private HashMap<String, String> props;
    private Element[] childrens;
    private String key;
    private boolean isText = false;

    Element() {
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public HashMap<String, String> getProps() {
        return props;
    }

    public void setProps(HashMap<String, String> props) {
        this.props = props;
    }

    public Element[] getChildrens() {
        return childrens;
    }

    public void setChildrens(Element[] childrens) {
        this.childrens = childrens;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isText() {
        return isText;
    }

    public void setText(boolean text) {
        isText = text;
    }
}
