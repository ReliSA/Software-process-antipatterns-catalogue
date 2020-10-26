package cz.zcu.kiv.spac.data.antipattern.heading;

import cz.zcu.kiv.spac.enums.AntipatternHeadingType;
import cz.zcu.kiv.spac.enums.TemplateFieldType;

/**
 * Abstract class representing heading for antipattern input form.
 */
public abstract class AntipatternHeading {

    private String headingText;
    private AntipatternHeadingType type;

    public void setType(AntipatternHeadingType type) {

        this.type = type;
    }

    public AntipatternHeadingType getType() {

        return type;
    }

    public void setHeadingText(String headingText) {

        this.headingText = headingText;
    }

    public String getHeadingText() {

        return this.headingText;
    }
}
