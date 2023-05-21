package mega.waka.repository.language;

import mega.waka.entity.language.ThirtyDaysLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirtyDaysLanguageRepository extends JpaRepository<ThirtyDaysLanguage,Long> {
}
