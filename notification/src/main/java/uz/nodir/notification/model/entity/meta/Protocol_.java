package uz.nodir.notification.model.entity.meta;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import uz.nodir.notification.model.entity.SMSProtocol;
import uz.nodir.notification.model.enums.TemplateType;

import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SMSProtocol.class)
public class Protocol_ {
    public static volatile SingularAttribute<SMSProtocol, Long> id;
    public static volatile SingularAttribute<SMSProtocol, String> message;
    public static volatile SingularAttribute<SMSProtocol, String> phone;
    public static volatile SingularAttribute<SMSProtocol, TemplateType> type;
    public static volatile SingularAttribute<SMSProtocol, LocalDateTime> createdDate;

    public static final String TEXT = "message";
    public static final String PHONE = "phone";
    public static final String DATE = "createdDate";
    public static final String TYPE = "type";
}
