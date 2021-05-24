package cz.zcu.kiv.spac.data.antipattern.heading;

import cz.zcu.kiv.spac.enums.AntipatternHeadingType;

/**
 * Abstract class representing heading for antipattern input form.
 */
public abstract class AntipatternHeading {

    private String headingText;
    private String headingName;
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

    public void setHeadingName(String headingName) {

        this.headingName = headingName;
    }

    public String getHeadingName() {

        return this.headingName;
    }
}
