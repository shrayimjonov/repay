package uz.repay.common.model.enums.conversion;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.springframework.context.annotation.Configuration;
import uz.repay.common.model.enums.Language;


@Configuration
public class LanguageConversion extends Conversion<Language> {
    @Override
    public Class<Language> getConvertedType() {
        return Language.class;
    }

    @Override
    public String getLogicalTypeName() {
        return "languageEnum";
    }

    @Override
    public Language fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
        return Language.valueOf(value.toString().toUpperCase());
    }


    @Override
    public CharSequence toCharSequence(Language value, Schema schema, LogicalType type) {
        return value.name();
    }
}
