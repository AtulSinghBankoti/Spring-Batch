package com.spring.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.spring.batch.domain.Customer;

public class UpperCaseItemProcessor implements ItemProcessor<Customer, Customer>{

	@Override
	public Customer process(Customer item) throws Exception {
		return new Customer(item.getId(),
				item.getFisrtName().toUpperCase(),
				item.getLastName().toUpperCase(),
				item.getBirthdate());
	}
	
	

	
}
