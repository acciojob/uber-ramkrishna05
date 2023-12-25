package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer=customerRepository2.findById(customerId).get();
		List<TripBooking>tripBookings=customer.getTripBookings();
		for(TripBooking tripBooking:tripBookings)
		{
			Driver driver=tripBooking.getDriver();
			Cab cab=driver.getCab();
			cab.setAvailable(true);
			driverRepository2.save(driver);
			tripBooking.setTripStatus(TripStatus.CANCELED);
		}
		customerRepository2.delete(customer);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		List<Driver>driverList=driverRepository2.findAll();
		Driver driver=null;
		for (Driver currdriver:driverList)
		{
			if(currdriver.getCab().isAvailable())
			{
				if(driver==null || currdriver.getId()<driver.getId())
				{
					driver=currdriver;
				}
			}
		}
		if(driver==null)
		{
			throw new Exception("No cab available");
		}
		TripBooking tripBooking=new TripBooking();
		tripBooking.setTripStatus(TripStatus.CONFIRMED);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDriver(driver);
		tripBooking.setDistanceInKm(distanceInKm);
		int rate=driver.getCab().getPerKmRate();
		tripBooking.setBill(rate*distanceInKm);
		tripBooking.setCustomer(customerRepository2.findById(customerId).get());

		driver.getTripBookingList().add(tripBooking);
		driver.getCab().setAvailable(false);
		driverRepository2.save(driver);

		Customer customer=customerRepository2.findById(customerId).get();
		customer.getTripBookings().add(tripBooking);
		customerRepository2.save(customer);
		tripBookingRepository2.save(tripBooking);
		return tripBooking;

	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();
		tripBooking.getDriver().getCab().setAvailable(true);
		tripBooking.setTripStatus(TripStatus.CANCELED);
		tripBooking.setBill(0);
		tripBookingRepository2.save(tripBooking);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();
		tripBooking.setTripStatus(TripStatus.COMPLETED);
		tripBooking.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(tripBooking);

	}
}
