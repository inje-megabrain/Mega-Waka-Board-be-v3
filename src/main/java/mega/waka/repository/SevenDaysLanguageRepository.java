package mega.waka.repository;

import mega.waka.entity.SevenDaysLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SevenDaysLanguageRepository extends JpaRepository<SevenDaysLanguage, Long> {

    SevenDaysLanguage findByName(String name);
}
