package uz.nodir.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.nodir.file.model.entity.FileSaver;

import java.util.List;

@Repository
public interface FileSaverRepository extends JpaRepository<FileSaver, Long> {
    List<FileSaver> findByLoanExtIdAndIsInternal(String loanExtId, boolean isInternal);
}
