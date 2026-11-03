package uz.repay.loan.configuration.graphql;

import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GraphQLRegisterType {

    @Bean
    public GraphQLScalarType localDateTime() {
        return LocalDateTimeScalar.LOCAL_DATE_TIME;
    }
}
