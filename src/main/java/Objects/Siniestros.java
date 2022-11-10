package Objects;

/**
 * Created by pacovarrubias on 22/04/2017.
 */

public class Siniestros {
    String customer;
    String vin;
    String model;
    String color;
    String make;
    String platenumber;
    String photo;

    public Siniestros()
    {

    }

    public Siniestros (String customer,String vin, String model, String color, String make, String PlateNumber,  String Photo) {
        this.customer = customer;
        this.vin = vin;
        this.model = model;
        this.color = color;
        this.make = make;
        this.platenumber = PlateNumber;
        this.photo = Photo;


    }

    public String getCustomer() {
        return customer;
    }

    public String getVin() {
        return vin;
    }

    public String getModel() {
        return model;
    }

   public String getColor() {
        return color;
    }

    public String getMake() {
        return make;
    }

    public String getPlatenumber() {return platenumber;}

    public String getPhoto() {
        return photo;
    }





}
