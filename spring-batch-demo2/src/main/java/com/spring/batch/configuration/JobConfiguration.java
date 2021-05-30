package com.spring.batch.configuration;



import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.spring.batch.domain.Customer;
import com.spring.batch.domain.CustomerLineAggregator;
import com.spring.batch.domain.CustomerRowMapper;
import com.spring.batch.processor.UpperCaseItemProcessor;

@Configuration
public class JobConfiguration {

	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	public DataSource dataSource;
	
	@Bean
	public JdbcCursorItemReader<Customer> cursorItemReader() {
		JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<>();
		reader.setSql("select id, firstName, lastName, birthdate from customer");
		reader.setDataSource(this.dataSource);
		reader.setRowMapper(new CustomerRowMapper());
		
		return reader;
	}
	
	//@Bean
	public JdbcPagingItemReader<Customer> pagingItemReader() {
		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
		
		reader.setDataSource(dataSource);
		reader.setFetchSize(10);
		reader.setRowMapper(new CustomerRowMapper());
		
		MySqlPagingQueryProvider queryProvider = 
				new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("id,firstName,lastName,birthdate");
		queryProvider.setFromClause("from customer");
		
		Map<String, Order> shortKeys = new HashMap<>(1);
		shortKeys.put("id", Order.ASCENDING);
		
		queryProvider.setSortKeys(shortKeys);
		
		reader.setQueryProvider(queryProvider);
		
		
		return reader;
	}
	
	
	/*
	@Bean
	public ItemWriter<Object> customerItemWriter(){
		return items-> {
			for(Object item : items) {
				System.out.println(item.toString());
			}
		};
	}*/
	
	
	@Bean
	public FlatFileItemWriter<Customer> customerItemWriter1() throws Exception{
		
		
		FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();
		//itemWriter.setLineAggregator(new PassThroughLineAggregator<>());
		itemWriter.setLineAggregator(new CustomerLineAggregator());
		
		String customerOutputPath = File.createTempFile("data/customerOutput", ".out").getAbsolutePath().toString();
		System.out.println(">> Output Path:"+customerOutputPath);
		itemWriter.setResource(new FileSystemResource(customerOutputPath));
		itemWriter.afterPropertiesSet();
		
		return itemWriter;
	}
	
	@Bean 
	public StaxEventItemWriter<Customer> customerItemWriter2() throws Exception{
		
		XStreamMarshaller marshaller = new XStreamMarshaller();
		
		Map<String, Class> aliases = new HashMap<>();
		aliases.put("customer", Customer.class);
		
		marshaller.setAliases(aliases);
		
		
		StaxEventItemWriter<Customer> itemWriter = new StaxEventItemWriter<>();
		itemWriter.setRootTagName("customers");
		itemWriter.setMarshaller(marshaller);
		String customerOutputPath = File.createTempFile("customerOutput", ".xml").getAbsolutePath().toString();
		System.out.println(">> Output Path: "+customerOutputPath);
		itemWriter.setResource(new FileSystemResource(customerOutputPath));
		
		itemWriter.afterPropertiesSet();
		
		
		return itemWriter;
		
	}
	
	
	public CompositeItemWriter<Customer> itemWriter() throws Exception{
		

		List<ItemWriter<? super Customer>> writers = new ArrayList<>(2);
		
		writers.add(customerItemWriter1());
		writers.add(customerItemWriter2());
		
		CompositeItemWriter<Customer> itemWriter = new CompositeItemWriter<>();
		itemWriter.setDelegates(writers);
		itemWriter.afterPropertiesSet();
		
		return itemWriter;
	}
	
	
	@Bean
	public UpperCaseItemProcessor itemProcessor() {
		return new UpperCaseItemProcessor();
	}
	
	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1")
				.<Customer, Customer>chunk(10)
				.reader(cursorItemReader())
				//.reader(pagingItemReader())
				//.writer(customerItemWriter())
				.processor(itemProcessor())
				.writer(itemWriter())
				.build();
	}
			
	@Bean
	public Job job() throws Exception {
		return jobBuilderFactory.get("read_from_db_job411")
				.start(step1())
				.build();
				
	}
	
}
