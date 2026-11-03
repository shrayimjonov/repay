package uz.repay.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.repay.card.model.entity.Loan;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    String LOAN_CARD_GRAPH = "loanCards.card";
    String GUARANTOR_CARD_GRAPH = "guarantors.guarantor.guarantorCards.card";

    @Override
    @EntityGraph(attributePaths = {
            LOAN_CARD_GRAPH,
            GUARANTOR_CARD_GRAPH
    })
    Optional<Loan> findById(Long id);

    @EntityGraph(attributePaths = {
            LOAN_CARD_GRAPH,
            GUARANTOR_CARD_GRAPH
    })
    Optional<Loan> findByExtId(String loanExtId);

    @EntityGraph(attributePaths = {
            LOAN_CARD_GRAPH,
            GUARANTOR_CARD_GRAPH
    })
    List<Loan> findAllByIsDeleted(Boolean isDeleted);

    @EntityGraph(attributePaths = {
            LOAN_CARD_GRAPH,
            GUARANTOR_CARD_GRAPH
    })
    @Query(value = "select l from Loan l where l.extId in :extIds and l.isDeleted=:isDeleted")
    List<Loan> findByIdsAndIsDeleted(List<String> extIds, Boolean isDeleted);
}
