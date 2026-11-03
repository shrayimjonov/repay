package uz.repay.loan.model.entity.meta;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import uz.repay.loan.model.entity.DebtLoan;



@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DebtLoan.class)
public class DebtLoan_ {

    public static volatile SingularAttribute<DebtLoan, Long> id;
    public static volatile SingularAttribute<DebtLoan, String> payerName;
    public static volatile SingularAttribute<DebtLoan, String> payerPhone;
    public static volatile SingularAttribute<DebtLoan, String> loanExtId;
    public static volatile SingularAttribute<DebtLoan, String> branch;
    public static volatile SingularAttribute<DebtLoan, String> pinfl;

    public static final String FULLNAME = "payerName";
    public static final String PHONE = "payerPhone";
    public static final String LOAN_ID = "loanExtId";
    public static final String BRANCH = "branch";
    public static final String PINFL = "pinfl";



}
