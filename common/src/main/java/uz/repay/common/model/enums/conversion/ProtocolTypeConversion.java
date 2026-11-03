package uz.repay.common.model.enums.conversion;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.springframework.context.annotation.Configuration;
import uz.repay.common.model.enums.ProtocolType;


@Configuration
public class ProtocolTypeConversion extends Conversion<ProtocolType> {
    @Override
    public Class<ProtocolType> getConvertedType() {
        return ProtocolType.class;
    }

    @Override
    public String getLogicalTypeName() {
        return "protocolTypeEnum";
    }

    @Override
    public ProtocolType fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
        return ProtocolType.valueOf(type.toString().toUpperCase());
    }

    @Override
    public CharSequence toCharSequence(ProtocolType value, Schema schema, LogicalType type) {
        return value.name();
    }
}
