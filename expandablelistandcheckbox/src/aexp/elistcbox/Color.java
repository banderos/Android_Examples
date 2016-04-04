package aexp.elistcbox;

public class Color {
    public String color = null;
    public String rgb = null;
    public boolean state = false;

    public Color( String color, String rgb, boolean state ) {
        this.color = color;
        this.rgb = rgb;
        this.state = state;
    }

    public String getColor() {
	    return color;
    }

    public String getRgb() {
	    return rgb;
    }

    public boolean getState() {
	    return state;
    }

}
