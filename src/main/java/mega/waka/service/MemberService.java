package mega.waka.service;

import mega.waka.entity.Member;
import mega.waka.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void delete_member(UUID id) {  // member 삭제 api
        Optional<Member> member = memberRepository.findById(id);
        member.orElseThrow(()->{
            throw new IllegalArgumentException("해당하는 멤버가 없습니다.");
        });
        memberRepository.delete(member.get());
    }
    public void update_apiKey(UUID id, String apiKey) {  // apiKey 변경 api
        Optional<Member> member = memberRepository.findById(id);
        member.orElseThrow(()->{
            throw new IllegalArgumentException("해당하는 멤버가 없습니다.");
        });
        member.get().setSecretKey(apiKey);
        memberRepository.save(member.get());
    }

    public void add_Member_By_apiKey(String name, String organization, String apiKey) {  // member 생성 api
        Optional<Member> findMember = memberRepository.findByNameAAndOrganization(name,organization);
        findMember.orElseThrow(()->{
            throw new IllegalArgumentException("이미 존재하는 멤버입니다.");
        });
        Member member = new Member();
        member.setSecretKey(apiKey);
        member.setName(name);
        member.setId(UUID.randomUUID());
        member.setFourteenDays("0:0");
        member.setSevenDays("0:0");
        member.setThirtyDays("0:0");
        member.setOrganization(organization);
        member.setUpdateDate(LocalDateTime.now());
        member.setStartDate(LocalDateTime.now());
        memberRepository.save(member);
    }
}
