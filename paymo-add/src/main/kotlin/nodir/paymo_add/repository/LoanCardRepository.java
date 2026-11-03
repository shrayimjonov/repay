package nodir.paymo_add.repository;

import nodir.paymo_add.entity.LoanCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanCardRepository extends JpaRepository<LoanCard, Long> {
    @Query(value = "SELECT lc.id,l.external_id,lc.loan_id,lc.card_id,lc.uzcard_contract_id,c.card_pan,c.card_holder,c.card_phone,c.sms,c.uzcard_client_id FROM marketplace.loan l\n" +
            "  inner JOIN marketplace.loan_card lc ON l.id = lc.loan_id\n" +
            "  inner JOIN payment_core.card  c ON lc.card_id = c.id\n" +
            "  where  l.external_id=:loanId", nativeQuery = true)
    List<LoanCard> findByExternal_id(String loanId);
    @Query(value = "SELECT lc.id,l.external_id,lc.loan_id,lc.card_id,lc.uzcard_contract_id,c.card_pan,c.card_holder,c.card_phone,c.sms,c.uzcard_client_id FROM marketplace.loan l\n" +
            "  inner JOIN marketplace.loan_card lc ON l.id = lc.loan_id\n" +
            "  inner JOIN payment_core.card  c ON lc.card_id = c.id\n" +
            "  where c.card_pan=:card",nativeQuery = true)
    List<LoanCard> findByCard_pan(String card);
    @Query(value = "SELECT lc.id,l.external_id,lc.loan_id,lc.card_id,lc.uzcard_contract_id,c.card_pan,c.card_holder,c.card_phone,c.sms,c.uzcard_client_id FROM marketplace.loan l\n" +
            "  inner JOIN marketplace.loan_card lc ON l.id = lc.loan_id\n" +
            "  inner JOIN payment_core.card  c ON lc.card_id = c.id\n" +
            "  where c.card_phone=:phone", nativeQuery = true)
    List<LoanCard> findByCard_phone(String phone);

    @Query(value = "SELECT lc.id,l.external_id,lc.loan_id,lc.card_id,lc.uzcard_contract_id,c.card_pan,c.card_holder,c.card_phone,c.sms,c.uzcard_client_id FROM marketplace.loan l\n" +
            "  inner JOIN marketplace.loan_card lc ON l.id = lc.loan_id\n" +
            "  inner JOIN payment_core.card  c ON lc.card_id = c.id\n" +
            "  where lc.id=:id", nativeQuery = true)
    Optional<LoanCard> findByLoanCardId(Long id);

    @Query(value = "update marketplace.loan_card lc set lc.uzcard_contract_id = NULL where id=:id",nativeQuery = true)
    @Modifying
    void updateUzCardContract(Long id);
}
