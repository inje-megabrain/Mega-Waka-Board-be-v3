package mega.waka.repository.language;

import mega.waka.entity.language.OneDaysLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OneDayLanguageRepository extends JpaRepository<OneDaysLanguage,Long> {
    List<OneDaysLanguage> findByMemberId(UUID id);
    OneDaysLanguage findByCreateDateBetween(LocalDate start, LocalDate end);
}
