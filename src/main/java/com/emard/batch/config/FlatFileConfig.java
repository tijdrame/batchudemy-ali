package com.emard.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.emard.batch.listener.HelloJobExecutionListener;
import com.emard.batch.mapper.ProductMapper;
import com.emard.batch.model.Product;
import com.emard.batch.reader.ProductServiceAdapter;
import com.emard.batch.service.ProductService;
import com.emard.batch.writter.ProductWriter;

@EnableBatchProcessing
@Configuration
public class FlatFileConfig {

    private final JobBuilderFactory jobs;

    private final StepBuilderFactory steps;

    private final HelloJobExecutionListener executionListener;
    private final ProductWriter writer;
    private final DataSource dataSource;
    private final ProductMapper productMapper;
    private final ProductServiceAdapter serviceAdapter;

    public FlatFileConfig(JobBuilderFactory jobs, StepBuilderFactory steps,
            HelloJobExecutionListener executionListener,
            ProductWriter writer, DataSource dataSource, ProductMapper productMapper,
            ProductServiceAdapter service) {
        this.jobs = jobs;
        this.steps = steps;
        this.executionListener = executionListener;
        this.writer = writer;
        this.dataSource = dataSource;
        this.productMapper = productMapper;
        this.serviceAdapter = service;
    }

    @StepScope
    @Bean
    public FlatFileItemReader<Product> flatFileItemReader(
            @Value("#{jobParameters['fileInput']}") FileSystemResource inputFile) {
        FlatFileItemReader<Product> reader = new FlatFileItemReader<>();
        // know where is the file
        // reader.setResource(new FileSystemResource("input/product.csv"));
        reader.setResource(inputFile);
        // create the linemapper
        reader.setLineMapper(
                new DefaultLineMapper<Product>() {
                    {
                        setLineTokenizer(new DelimitedLineTokenizer() {
                            {
                                setStrict(false);// si le nb de colonnes n'est pas conforme il laisse passer aussi
                                setNames(new String[] { "productId", "productName", "productDesc", "price", "unit" });
                                // setDelimiter(",");
                            }
                        });
                        setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                            {
                                setTargetType(Product.class);
                            }
                        });
                    }
                });
        // sauter la 1ere ligne
        reader.setLinesToSkip(1);
        return reader;
    }

    @StepScope
    @Bean
    // read xml
    public StaxEventItemReader<Product> xmlItemReader(
            @Value("#{jobParameters['fileInput2']}") FileSystemResource inputFile) {
        Jaxb2Marshaller productMarshaller = new Jaxb2Marshaller();
        productMarshaller.setClassesToBeBound(Product.class);
        return new StaxEventItemReaderBuilder<Product>()
                .name("xmlItemReader")
                // .resource(new ClassPathResource(inputFile))
                .resource(inputFile)
                .addFragmentRootElements("product")
                .unmarshaller(productMarshaller)
                .build();
    }

    protected static class PlayerFieldSetMapper implements FieldSetMapper<Product> {
        public Product mapFieldSet(FieldSet fieldSet) {
            Product product = new Product();
            product.setProductID(fieldSet.readInt(0));
            product.setProductName(fieldSet.readString(1));
            product.setProductDesc(fieldSet.readString(2));
            product.setPrice(fieldSet.readBigDecimal(3));
            product.setUnit(fieldSet.readInt(4));
            return product;
        }
    }

    // @Bean
    /*
     * public FixedLengthTokenizer fixedLengthTokenizer() {
     * FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
     * tokenizer.setNames("productId", "productName", "productDesc", "price",
     * "unit");
     * tokenizer.setColumns(new Range(1,16),
     * new Range(17,41),
     * new Range(42,65),
     * new Range(66,73),
     * new Range(74,80));
     * return tokenizer;
     * }
     */

    @StepScope
    @Bean
    public FlatFileItemReader<Product> flatFileSimpleItemReader(
            @Value("#{jobParameters['fileInput']}") FileSystemResource inputFile) {
        return new FlatFileItemReaderBuilder<Product>()
                .name("flatFileFixedItemReader")
                .linesToSkip(1)
                .resource(inputFile)
                .delimited()
                .names("productId", "productName", "productDesc", "price", "unit")
                .targetType(Product.class)
                .build();
    }

    @StepScope
    @Bean
    public FlatFileItemReader<Product> flatFileFixedItemReader(
            @Value("#{jobParameters['fileInputFix']}") FileSystemResource inputFile) {
        Range[] ranges = { new Range(1, 16), new Range(17, 41),
                new Range(42, 65), new Range(66, 73), new Range(74, 80) };
        return new FlatFileItemReaderBuilder<Product>()
                .name("flatFileFixedItemReader")
                .linesToSkip(1)
                .resource(inputFile)
                // .lineTokenizer(fixedLengthTokenizer())lui ou la ligne suivante
                .fixedLength()
                .columns(ranges)
                .names("productId", "productName", "productDesc", "price", "unit")
                .targetType(Product.class)
                .build();
    }

    

    @Bean
    public JdbcCursorItemReader<Product> cursorItemReader() {
        return new JdbcCursorItemReaderBuilder<Product>()
                .name("cursorItemReader")
                .dataSource(dataSource)
                .sql("select * from products")
                .rowMapper(productMapper)
                .build();
    }

    @StepScope
    @Bean
    public JsonItemReader<Product> jsonItemReader(
            @Value("#{jobParameters['fileInputJson']}") FileSystemResource inputFile) {
        return new JsonItemReaderBuilder<Product>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Product.class))
                .resource(inputFile)
                .name("jsonItemReader")
                .build();
    }

@Bean
public ItemReaderAdapter<Product> serviceItemReader(){
    ItemReaderAdapter<Product> reader = new ItemReaderAdapter<Product>();
    reader.setTargetObject(serviceAdapter);
    reader.setTargetMethod("nextProduct");
    return reader;
}

    @Bean
    public Step step1() {
        return steps.get("step1")
                .<Product, Product>chunk(2)
                // .reader(reader())
                .reader(serviceItemReader())
                // .processor(inMemItemProcessor)
                //.writer(itemWriter())
                .writer(writer)
                .build();
    }

    @Bean
    public Step step2() {
        return steps.get("step2")
                .<Product, Product>chunk(3)
                // .reader(reader())
                .reader(xmlItemReader(null))
                // .processor(inMemItemProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job helloWorldJob() {
        return jobs.get("helloWorldJob")
                .incrementer(new RunIdIncrementer())
                .listener(executionListener)
                .start(step1())
                //.next(step2())
                .build();
    }

}
