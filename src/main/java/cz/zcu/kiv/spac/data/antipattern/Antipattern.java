package cz.zcu.kiv.spac.data.antipattern;

public class Antipattern {

    private String name;
    private String content;

    public Antipattern(String name, String content) {

        this.name = name;
        this.content = content;
    }

    public String getContent() {

        return content;
    }

    public String getName() {

        return name;
    }
}
