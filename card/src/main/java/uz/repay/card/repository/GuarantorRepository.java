package uz.repay.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.repay.card.model.entity.Guarantor;

import java.util.Optional;


@Repository
public interface GuarantorRepository extends JpaRepository<Guarantor, Long> {
    @Override
    @EntityGraph(attributePaths = "guarantorCards.card")
    Optional<Guarantor> findById(Long id);
}
