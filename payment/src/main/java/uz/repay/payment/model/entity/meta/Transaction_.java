package uz.repay.payment.model.entity.meta;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.payment.model.entity.Transaction;

import java.time.LocalDate;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Transaction.class)
public class Transaction_ {
    public static volatile SingularAttribute<Transaction, Long> id;
    public static volatile SingularAttribute<Transaction, Long> amount;
    public static volatile SingularAttribute<Transaction, Branch> branch;
    public static volatile SingularAttribute<Transaction, String> cardPan;
    public static volatile SingularAttribute<Transaction, String> loanExtId;
    public static volatile SingularAttribute<Transaction, String> refNum;
    public static volatile SingularAttribute<Transaction, LocalDate> createdDate;
    public static volatile SingularAttribute<Transaction, ResponseStatus> status;


    public static final String AMOUNT = "amount";
    public static final String BRANCH = "branch";
    public static final String CARD_PAN = "cardPan";
    public static final String LOAN_EXT_ID = "loanExtId";
    public static final String REF_NUM = "refNum";
    public static final String CREATED_DATE = "createdDate";
    public static final String STATUS = "status";

}
