package uz.repay.common.service;

import org.springframework.data.jpa.domain.Specification;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.ResponseStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JpaSpecificationBuilder<E> {
    protected Specification<E> createSpecByLikeCondition(String metaModel, String searchValue) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get(metaModel).as(String.class), "%" + searchValue + "%");
    }

    protected Specification<E> createSpecByStatusInCondition(String metaModel, List<String> searchValue) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get(metaModel)).value(searchValue);
    }

    protected Specification<E> createSpecByBoolEqualCondition(String metaModel, Boolean value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(metaModel), value);
    }

    protected Specification<E> createSpecByStringEqualCondition(String metaModel, String value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(metaModel), value);
    }

    protected Specification<E> createSpecByDateBetweenCondition(String metaModel, String startDate, String endDate) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate dateStart = LocalDate.parse(startDate, dateFormatter);
        LocalDate dateFinish = LocalDate.parse(endDate, dateFormatter);

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get(metaModel), dateStart, dateFinish);
    }

    protected Specification<E> createSpecByResponseStatusInCondition(String metaModel, List<ResponseStatus> searchValue) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get(metaModel)).value(searchValue);
    }

    protected Specification<E> createSpecByBranchInCondition(String metaModel, List<Branch> searchValue) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get(metaModel)).value(searchValue);
    }
}
