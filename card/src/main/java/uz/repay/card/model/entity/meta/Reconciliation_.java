package uz.repay.card.model.entity.meta;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import uz.repay.card.model.entity.Reconciliation;
import uz.repay.common.model.enums.ResponseStatus;

import java.time.LocalDate;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Reconciliation.class)
public class Reconciliation_ {
    public static volatile SingularAttribute<Reconciliation, Long> id;
    public static volatile SingularAttribute<Reconciliation, String> terminalId;
    public static volatile SingularAttribute<Reconciliation, LocalDate> createdDate;
    public static volatile SingularAttribute<Reconciliation, ResponseStatus> status;
    public static volatile SingularAttribute<Reconciliation, String> paymentId;

    public static final String TERMINAL = "terminalId";
    public static final String CREATED_DATE = "createdDate";
    public static final String STATUS = "status";
    public static final String PAYMENT_ID = "paymentId";
}
