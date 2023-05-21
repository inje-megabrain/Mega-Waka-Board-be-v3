package mega.waka.repository.language;

import mega.waka.entity.language.SevenDaysLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SevenDaysLanguageRepository extends JpaRepository<SevenDaysLanguage, Long> {

    SevenDaysLanguage findByName(String name);
}
