package uz.repay.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.repay.card.model.entity.LoanCard;
import uz.repay.card.model.entity.pk.LoanCardKey;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanCardRepository extends JpaRepository<LoanCard, LoanCardKey> {
    @Query(value = "select lc from LoanCard lc left join fetch lc.card left join fetch lc.loan where lc.id.loanId=:loanId and lc.id.cardId=:cardId")
    Optional<LoanCard> findByLoanIdAndCardId(Long loanId, Long cardId);

    @Query(value = "select lc from LoanCard lc where lc.card.token=:token and lc.loan.isDeleted=false and lc.isDeleted=false")
    List<LoanCard> findByCardToken(String token);

    @Query(value = "select lc from LoanCard lc where lc.card.pan=:pan and lc.loan.isDeleted=false")
    List<LoanCard> findByCardPan(String pan);
}
