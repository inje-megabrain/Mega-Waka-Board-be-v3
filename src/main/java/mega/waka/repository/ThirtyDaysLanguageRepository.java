package mega.waka.repository;

import mega.waka.entity.ThirtyDaysLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirtyDaysLanguageRepository extends JpaRepository<ThirtyDaysLanguage,Long> {
}
