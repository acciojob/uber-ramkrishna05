package com.driver.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customber_info")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  int id;
    private String mobile;
    private  String password;
    @OneToMany(mappedBy = "customber",cascade = CascadeType.ALL)
    private List<TripBooking>tripBookings=new ArrayList<>();

    public Customer(int id, String mobile, String password, List<TripBooking> tripBookings) {
        this.id = id;
        this.mobile = mobile;
        this.password = password;
        this.tripBookings = tripBookings;

    }

    public Customer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<TripBooking> getTripBookings() {
        return tripBookings;
    }

    public void setTripBookings(List<TripBooking> tripBookings) {
        this.tripBookings = tripBookings;
    }
}