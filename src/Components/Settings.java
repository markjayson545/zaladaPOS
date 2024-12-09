package Components;

public class Settings {
    String appName = "Salamat Shapi";

    int width = 500;
    int height = 350;

    boolean isAlwaysOnTop = true;
    boolean isResizable = true;
    boolean isVisible = true;
    boolean isCentered = true;
    boolean isLoggedIn = false;

    public boolean getSetting(String setting) {
        switch (setting) {
            case "alwaysOnTop":
                return isAlwaysOnTop;
            case "resizable":
                return isResizable;
            case "visible":
                return isVisible;
            case "centered":
                return isCentered;
            case "isLoggedIn":
                return isLoggedIn;
            default:
                return false;
        }
    }

    public int getDimension(String dimension) {
        if (dimension.equals("width")) {
            return width;
        } else if (dimension.equals("height")) {
            return height;
        }
        return 0;
    }

    public String getAppName() {
        return appName;
    }

}
