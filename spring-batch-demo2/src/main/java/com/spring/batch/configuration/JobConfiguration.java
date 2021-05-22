package com.spring.batch.configuration;

import java.util.HashMap;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring.batch.domain.Customer;
import com.spring.batch.domain.CustomerRowMapper;

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
	
	
	@Bean
	public ItemWriter<Object> customerItemWriter(){
		return items-> {
			for(Object item : items) {
				System.out.println(item.toString());
			}
		};
	}
	
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.chunk(10)
				.reader(cursorItemReader())
				//.reader(pagingItemReader())
				.writer(customerItemWriter())
				.build();
	}
			
	@Bean
	public Job job() {
		return jobBuilderFactory.get("read_from_db_job2")
				.start(step1())
				.build();
				
	}
	
}
