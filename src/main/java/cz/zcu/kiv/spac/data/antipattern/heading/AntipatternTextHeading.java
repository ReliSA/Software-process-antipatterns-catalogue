package cz.zcu.kiv.spac.data.antipattern.heading;

/**
 * Class representing textarea and textfield value in input antipattern form.
 */
public class AntipatternTextHeading extends AntipatternHeading {

    private String value;

    public AntipatternTextHeading(String value) {

        this.value = value;
    }

    public String getValue() {

        return value;
    }

    public void appendValue(String value) {

        this.value += value;
    }
}
