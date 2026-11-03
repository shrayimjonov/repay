package uz.repay.loan.model.entity.meta;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import uz.repay.common.model.enums.Branch;
import uz.repay.loan.model.entity.Loan;

import java.time.LocalDate;


@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Loan.class)
public class Loan_ {
    public static volatile SingularAttribute<Loan, Long> id;
    public static volatile SingularAttribute<Loan, Branch> branch;
    public static volatile SingularAttribute<Loan, String> pinfl;
    public static volatile SingularAttribute<Loan, String> extId;
    public static volatile SingularAttribute<Loan, String> payerName;
    public static volatile SingularAttribute<Loan, String> payerPhone;
    public static volatile SingularAttribute<Loan, String> idNibbd;
    public static volatile SingularAttribute<Loan, Boolean> isConvicted;
    public static volatile SingularAttribute<Loan, LocalDate> createdDate;
    public static volatile SingularAttribute<Loan, String> createdBy;

    public static final String FULL_NAME = "payerName";
    public static final String PHONE = "payerPhone";
    public static final String LOAN_ID = "extId";
    public static final String BRANCH = "branch";
    public static final String PINFL = "pinfl";
    public static final String CLIENT_ID = "idNibbd";
    public static final String IS_DELETED = "isDeleted";
    public static final String IS_CONVICTED = "isConvicted";
    public static final String CREATED_DATE = "createdDate";
    public static final String CREATED_BY = "createdBy";
    public static final String IS_INSOLVENT = "isInsolvent";


}
