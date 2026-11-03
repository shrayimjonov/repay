package uz.nodir.file.service.core;

import uz.nodir.file.model.entity.FileSaver;

import java.util.List;

public interface FileSaverDAO {

    FileSaver save(FileSaver fileSaver);

    List<FileSaver> findFilesByLoanExtId(String loanExtId);

    FileSaver findById(Long id);
}
