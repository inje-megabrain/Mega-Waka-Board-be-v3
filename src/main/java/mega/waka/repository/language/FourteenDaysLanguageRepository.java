package mega.waka.repository.language;

import mega.waka.entity.language.FourteenDaysLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FourteenDaysLanguageRepository extends JpaRepository<FourteenDaysLanguage,Long> {
}
