package de.zebrajaeger.phserver.battery;

public class Batteries {
    /**
     * Parkside x20 singe cell (not parkside performance)<br>
     * <a href="https://www.lidl.de/c/parkside-x-20-v-team/a10007371">Parkside x20</a>
     **/
    public static final BatteryValues INR18650E = getINR18650EValues();
    /**
     * Parkside x20 pack (not parkside performance)<br>
     * <a href="https://www.lidl.de/c/parkside-x-20-v-team/a10007371">Parkside x20</a>
     **/
    public static final BatteryValues ParksideX20 = INR18650E.createBatteryPackWithCellCountOf(5);

    private static BatteryValues getINR18650EValues() {
        BatteryValues values = new BatteryValues();
        values.add(4.2f, 100f);
        values.add(4.1f, 97.849f);
        values.add(4.0f, 89.247f);
        values.add(3.9f, 79.570f);
        values.add(3.8f, 70.968f);
        values.add(3.7f, 60.215f);
        values.add(3.6f, 44.086f);
        values.add(3.5f, 19.355f);
        values.add(3.4f, 6.452f);
        values.add(3.3f, 0.645f);
        values.add(3.2f, 0f);
        return values;
    }
}
