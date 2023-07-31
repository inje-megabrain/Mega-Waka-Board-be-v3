package mega.waka.repository.language;

import mega.waka.entity.Member;
import mega.waka.entity.language.SevenDaysLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SevenDaysLanguageRepository extends JpaRepository<SevenDaysLanguage, Long> {

    List<SevenDaysLanguage> findByMemberId(UUID memberId);
}
