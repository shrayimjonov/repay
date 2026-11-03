package uz.repay.common.model.enums.conversion.component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericData;
import org.springframework.stereotype.Component;
import uz.repay.common.model.enums.conversion.LanguageConversion;
import uz.repay.common.model.enums.conversion.PayerTypeConversion;
import uz.repay.common.model.enums.conversion.ProtocolTypeConversion;


@Component
@Slf4j
public class TypeRegistrar {

    @PostConstruct
    public void register(){
        log.info("Register all enum types to avro scheme");

        GenericData genericData = GenericData.get();
        genericData.addLogicalTypeConversion(new LanguageConversion());
        genericData.addLogicalTypeConversion(new PayerTypeConversion());
        genericData.addLogicalTypeConversion(new ProtocolTypeConversion());
    }
}
