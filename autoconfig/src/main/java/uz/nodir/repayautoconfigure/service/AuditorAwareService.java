package uz.nodir.repayautoconfigure.service;


import jakarta.annotation.Nonnull;
import org.springframework.data.domain.AuditorAware;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;

import java.util.Optional;


public class AuditorAwareService implements AuditorAware<String> {

    @Override
    @Nonnull
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtils.getUsername());
    }
}
