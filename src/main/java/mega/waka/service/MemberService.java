package mega.waka.service;

import mega.waka.entity.Member;
import mega.waka.entity.Money;
import mega.waka.entity.dto.ResponseInfoDto;
import mega.waka.entity.dto.ResponseInfoThirtyDaysDto;
import mega.waka.entity.dto.ResponseMemberDto;
import mega.waka.repository.MemberRepository;
import mega.waka.repository.MoneyRepository;
import mega.waka.repository.language.OneDayLanguageRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MoneyRepository moneyRepository;

    public MemberService(MemberRepository memberRepository,
                          MoneyRepository moneyRepository) {
        this.memberRepository = memberRepository;
        this.moneyRepository = moneyRepository;
    }
    public List<ResponseMemberDto> memberList(){
       List<Member> findMembers = memberRepository.findAll();
       List<ResponseMemberDto> dtos = new ArrayList<>();
       for(Member member : findMembers){
           ResponseMemberDto dto = new ResponseMemberDto().builder()
                   .id(member.getId())
                   .fourteenDays(member.getFourteenDays())
                   .image(member.getImage())
                   .oneDay(member.getOneDay())
                   .sevenDays(member.getSevenDays())
                   .thirtyDays(member.getThirtyDays())
                   .organization(member.getOrganization())
                   .name(member.getName())
                   .department(member.getDepartment())
                   .startDate(member.getStartDate())
                   .money(member.getMoney())
                   .build();
           dtos.add(dto);
       }
       return dtos;
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
    public ResponseInfoDto get_Member_info_day(UUID id, int date){
        Optional<Member> findMember = memberRepository.findById(id);
        findMember.orElseThrow(()->{
            throw new IllegalArgumentException("해당하는 멤버가 없습니다.");
        });

        ResponseInfoDto responseInfoDto = new ResponseInfoDto().builder()
                .name(findMember.get().getName())
                .Languages(findMember.get().getSevenlanguages())
                .Editors(findMember.get().getSeveneditors())
                .Proejects(findMember.get().getSevenprojects())
                .build();
        return responseInfoDto;
    }
    public ResponseInfoThirtyDaysDto get_Member_info_ThirtyDays(UUID id, int date){
        Optional<Member> findMember = memberRepository.findById(id);
        findMember.orElseThrow(()->{
            throw new IllegalArgumentException("해당하는 멤버가 없습니다.");
        });
        ResponseInfoThirtyDaysDto dto = new ResponseInfoThirtyDaysDto().builder()
                .name(findMember.get().getName())
                .Editors(findMember.get().getThirtyDaysEditors())
                .Languages(findMember.get().getThirtyDaysLanguages())
                .Proejects(findMember.get().getThirtyDaysProjects())
                .build();
        return dto;
    }
    public void add_Member_By_apiKey(String name, String organization, String apiKey, String githubId,String department) {  // member 생성 api
        Member findMember = memberRepository.findByNameAndOrganization(name,organization);
        if(findMember ==null){
            Money money = new Money().builder().amount(0).updateDate(LocalDate.now()).build();
            Member member = new Member();
            member.setSecretKey(apiKey);
            member.setName(name);
            member.setId(UUID.randomUUID());
            member.setFourteenDays("0:0");
            member.setSevenDays("0:0");
            member.setOneDay("0:0");
            member.setImage("https://avatars.githubusercontent.com/"+githubId);
            member.setOrganization(organization);
            member.setStartDate(LocalDateTime.now());
            member.setDepartment(department);
            member.setMoney(money);
            moneyRepository.save(money);
            memberRepository.save(member);
        }
        else{
            throw new IllegalArgumentException("이미 존재하는 멤버입니다.");
        }
    }
}
