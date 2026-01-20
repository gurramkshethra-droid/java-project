package rideshare;

public class Ride {
    private int id;
    private int riderId;
    private int driverId;
    private String pickup;
    private String dropoff;
    private double fare;
    private String status;

    public Ride(int riderId, int driverId, String pickup, String dropoff, double fare, String status) {
        this.riderId = riderId;
        this.driverId = driverId;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.fare = fare;
        this.status = status;
    }

    public int getRiderId() { return riderId; }
    public int getDriverId() { return driverId; }
    public String getPickup() { return pickup; }
    public String getDropoff() { return dropoff; }
    public double getFare() { return fare; }
    public String getStatus() { return status; }
}