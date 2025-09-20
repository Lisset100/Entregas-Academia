package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.entity.Movie;
import com.javatechie.spring.batch.repository.MovieRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private MovieRepository movieRepository;

    // Reader: lee el CSV
    @Bean
    public FlatFileItemReader<Movie> reader() {
        FlatFileItemReader<Movie> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/movies.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1); // Saltar cabecera
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    // Mapper: mapea cada línea del CSV a un objeto Movie
    private LineMapper<Movie> lineMapper() {
        DefaultLineMapper<Movie> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","title","genre","rating","releaseYear");

        BeanWrapperFieldSetMapper<Movie> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Movie.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    // Processor: solo dejamos películas con rating >= 7
    @Bean
    public MovieProcessor processor() {
        return new MovieProcessor();
    }

    // Writer: guarda los objetos en la base de datos
    @Bean
    public RepositoryItemWriter<Movie> writer() {
        RepositoryItemWriter<Movie> writer = new RepositoryItemWriter<>();
        writer.setRepository(movieRepository);
        writer.setMethodName("save");
        return writer;
    }

    // Step: combina Reader, Processor y Writer
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("csv-step")
                .<Movie, Movie>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    // Job: ejecuta el Step
    @Bean
    public Job runJob() {
        return jobBuilderFactory.get("importMovies")
                .flow(step1())
                .end()
                .build();
    }

    // TaskExecutor para concurrencia
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }
}
