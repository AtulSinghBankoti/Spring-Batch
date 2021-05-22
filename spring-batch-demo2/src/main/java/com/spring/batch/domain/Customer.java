package com.spring.batch.domain;

import java.util.Date;

public class Customer {

	private final long id;
	
	private final String fisrtName;
	
	
	private final String lastName;
	
	
	private final Date birthdate;


	public Customer(long id, String fisrtName, String lastName, Date birthdate) {
		this.id = id;
		this.fisrtName = fisrtName;
		this.lastName = lastName;
		this.birthdate = birthdate;
	}


	@Override
	public String toString() {
		return "Customer [id=" + id + ", fisrtName=" + fisrtName + ", lastName=" + lastName + ", birthdate="
				+ birthdate + "]";
	}
	
}
