package com.spring.batch.configuration.flow;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FlowLastConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	
	@Bean
	public Step myStep2() {
		return stepBuilderFactory.get("mystep2")
				.tasklet((contribution,  chunkContext)->{
						System.out.println("mystep was executed");
						return RepeatStatus.FINISHED;
					
				}).build();
	}
	
	
	@Bean
	public Job flowLastJob(Flow flow) {
		return jobBuilderFactory.get("flowLastJob")
				.start(myStep2())
				.on("COMPLETED").to(flow)
				.end()
				.build();
	}
}
