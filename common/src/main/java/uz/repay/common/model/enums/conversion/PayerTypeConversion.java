package uz.repay.common.model.enums.conversion;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.springframework.context.annotation.Configuration;
import uz.repay.common.model.enums.Payer;


@Configuration
public class PayerTypeConversion extends Conversion<Payer> {
    @Override
    public Class<Payer> getConvertedType() {
        return Payer.class;
    }

    @Override
    public String getLogicalTypeName() {
        return "payerEnum";
    }

    @Override
    public Payer fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
        return Payer.valueOf(type.toString().toUpperCase());
    }

    @Override
    public CharSequence toCharSequence(Payer value, Schema schema, LogicalType type) {
        return value.name();
    }
}
