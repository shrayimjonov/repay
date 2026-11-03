package uz.nodir.notification.service.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import uz.nodir.notification.model.entity.Template;

import java.util.List;

public interface TemplateDAO {
    Template save(Template template);

    List<Template> findAll();

    void deleteById(Long id);

    void evictTemplateCache();

    Page<Template> findAll(Specification<Template> specification, Pageable pageable);
}
