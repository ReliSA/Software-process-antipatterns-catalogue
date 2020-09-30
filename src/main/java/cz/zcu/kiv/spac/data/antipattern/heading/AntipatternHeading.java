package cz.zcu.kiv.spac.data.antipattern.heading;

import cz.zcu.kiv.spac.enums.FieldType;

/**
 * Abstract class representing heading for antipattern input form.
 */
public abstract class AntipatternHeading {

    private FieldType type;

    public void setType(FieldType type) {

        this.type = type;
    }

    public FieldType getType() {

        return type;
    }
}
