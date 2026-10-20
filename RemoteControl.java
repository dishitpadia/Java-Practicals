interface Switchable {
    void ON();

    void OFF();

    default void toggle() {
        System.out.println("Toggling device");
        ON();
    }
}

class Fan implements Switchable {

    boolean isON = false;

    public void ON() {
        if (!isON) {
            System.out.println("The fan turned on");
            isON = true;
        }

        else {
            System.out.println("The fan is already turned on");
        }
    }

    public void OFF() {
        if (isON) {
            System.out.println("The fan turned on");
            isON = false;
        }

        else {
            System.out.println("The fan is already turned off");
        }
    }

    public boolean getStatus() {
        return isON;
    }

}

class Light implements Switchable {

    boolean isOn = false;

    public void ON() {
        if (!isOn) {
            System.out.println("The light turned on");
            isOn = true;
        }

        else {
            System.out.println("The light is already turned on");
        }
    }

    public void OFF() {
        if (isOn) {
            System.out.println("The light turned off");
            isOn = false;
        }

        else {
            System.out.println("The light is already turned off");
        }
    }

}

interface SwitchPermission {
    boolean maySwitchOn(Switchable device, int hour);
}

public class RemoteControl {
    public static void main(String[] args) {

        Fan fan = new Fan();
        Light light = new Light();

        Switchable[] devices = { fan, light };

        System.out.println("\n--- Toggling devices ---");

        for (Switchable device : devices) {
            device.toggle();
        }

        System.out.println("\n--- Turning Devices OFF ---");

        for (Switchable device : devices) {
            device.OFF();
        }

        SwitchPermission permissionAnonymous = new SwitchPermission() {
            @Override
            public boolean maySwitchOn(Switchable device, int hour) {
                return hour >= 6 && hour < 22;
            }
        };

        SwitchPermission permissionLambda =
                (device, hour) -> hour >= 8 && hour < 20;

        System.out.println("\nPermission using anonymous class");

        System.out.println("Fan at 5 AM: " + permissionAnonymous.maySwitchOn(fan, 5));

        System.out.println("Fan at 10 PM: " + permissionAnonymous.maySwitchOn(fan, 22));

        System.out.println("Fan at 3 AM: " + permissionAnonymous.maySwitchOn(light, 3));

        System.out.println("\nPermission using lambda function");

        System.out.println("Light at 9 PM: " + permissionLambda.maySwitchOn(light, 21));

        System.out.println("Light at 8 AM: " + permissionLambda.maySwitchOn(light, 8));
    }
}