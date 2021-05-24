package cz.zcu.kiv.spac.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum class representing filter choices for antipattern.
 */
public enum AntipatternFilterChoices {

    ALL("All"),
    CREATED("Created"),
    MENTIONED("Mentioned");

    private String text;

    /**
     * Constructor.
     * @param text - Text displayed in choicebox.
     */
    AntipatternFilterChoices(String text) {

        this.text = text;
    }

    public String getText() {

        return this.text;
    }

    public static AntipatternFilterChoices getAntipatternFilterChoice(String text) {

        if (text.equals(ALL.getText())) {

            return ALL;
        }

        if (text.equals(CREATED.getText())) {

            return CREATED;
        }

        if (text.equals(MENTIONED.getText())) {

            return MENTIONED;
        }

        return null;
    }

    public static List<String> getTexts() {

        List<String> texts = new ArrayList<>();

        texts.add(ALL.getText());
        texts.add(CREATED.getText());
        texts.add(MENTIONED.getText());

        return texts;
    }

}
