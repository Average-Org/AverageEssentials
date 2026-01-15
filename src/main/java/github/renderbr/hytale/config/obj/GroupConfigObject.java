package github.renderbr.hytale.config.obj;

public class GroupConfigObject {
    public String prefix;
    public Integer weight = 1;

    public GroupConfigObject setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public GroupConfigObject setWeight(Integer weight) {
        this.weight = weight;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public Integer getWeight() {
        return weight;
    }
}
