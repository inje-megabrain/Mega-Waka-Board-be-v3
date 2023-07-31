package mega.waka.service;

import jakarta.transaction.Transactional;
import mega.waka.entity.Member;
import mega.waka.entity.Money;
import mega.waka.entity.dto.ResponseInfoDto;
import mega.waka.entity.dto.ResponseMemberDto;
import mega.waka.entity.dto.ResponseSummariesDto;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.project.SevenDaysProject;
import mega.waka.repository.MemberRepository;
import mega.waka.repository.MoneyRepository;
import mega.waka.repository.editor.SevenDaysEditorRepository;
import mega.waka.repository.language.SevenDaysLanguageRepository;
import mega.waka.repository.project.SevendaysProjectRepository;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MoneyRepository moneyRepository;
    private final SevenDaysLanguageRepository sevenDaysLanguageRepository;
    private final SevendaysProjectRepository sevendaysProjectRepository;
    private final SevenDaysEditorRepository sevenDaysEditorRepository;

    private Map<String,String> languageList = new HashMap<>();
    private Map<String,String> projectList = new HashMap<>();
    private Map<String,String> editList = new HashMap<>();

    public MemberService(MemberRepository memberRepository,
                         MoneyRepository moneyRepository, SevenDaysLanguageRepository sevenDaysLanguageRepository,
                         SevendaysProjectRepository sevendaysProjectRepository, SevenDaysEditorRepository sevenDaysEditorRepository) {
        this.memberRepository = memberRepository;
        this.moneyRepository = moneyRepository;
        this.sevenDaysLanguageRepository = sevenDaysLanguageRepository;
        this.sevendaysProjectRepository = sevendaysProjectRepository;
        this.sevenDaysEditorRepository = sevenDaysEditorRepository;
    }
    public Member getMember(String name){
        return memberRepository.findByName(name);
    }
    public String Authentication_apiKey(String apiKey){ //Member Create 시 api key 검증
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl ="https://wakatime.com/api/v1/users/current/summaries?range=last_7_days";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey,"");

        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        String responseData = response.getBody();
        if(responseData.isBlank()) return "";
        else return "200";
    }
    public List<ResponseMemberDto> memberList(){ // Member List 조회
       List<Member> findMembers = memberRepository.findAll();
       List<ResponseMemberDto> dtos = new ArrayList<>();
       for(Member member : findMembers){
           ResponseMemberDto dto = new ResponseMemberDto().builder()
                   .id(member.getId())
                   .image(member.getImage())
                   .sevenDays(member.getSevenDays())
                   .organization(member.getOrganization())
                   .name(member.getName())
                   .department(member.getDepartment())
                   .startDate(member.getStartDate())
                   .money(member.getMoney())
                   .department(member.getDepartment())
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

        moneyRepository.delete(member.get().getMoney());
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
    @Transactional
    public Map<String,ResponseSummariesDto> get_totals_Member(UUID id){ // 7일간 일평균 사용량 조회 api
        Optional<Member> findMember = memberRepository.findById(id);
        findMember.orElseThrow(()->{
            throw new IllegalArgumentException("해당하는 멤버가 없습니다.");
        });
        Map<String,ResponseSummariesDto> map = new HashMap<>();
        String responseData="";
        try{
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl ="https://wakatime.com/api/v1/users/current/summaries?range=last_7_days";
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(findMember.get().getSecretKey(),"");

            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );
            responseData = response.getBody();
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(responseData);
            JSONArray data = (JSONArray) jsonObject.get("data");
            for(int i=0;i<data.size();i++){
                JSONObject object = (JSONObject) data.get(i);
                JSONObject range = (JSONObject) object.get("range");
                String date = range.get("date").toString();
                JSONArray languages = (JSONArray) object.get("languages");
                JSONArray projects = (JSONArray) object.get("projects");
                JSONArray editors = (JSONArray) object.get("editors");

                ResponseSummariesDto dto = new ResponseSummariesDto().builder()
                        .summariesEditors(editors)
                        .summariesLanguages(languages)
                        .summariesProjects(projects)
                        .build();
                map.put(date,dto);
            }
        }catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
    @Transactional
    public ResponseInfoDto get_Member_info_day(String id){ // 멤버 상세 조회
        Optional<Member> findMember = memberRepository.findById(UUID.fromString(id));
        findMember.orElseThrow(()->{
            throw new IllegalArgumentException("해당하는 멤버가 없습니다.");
        });
        ResponseInfoDto responseInfoDto=null;
        String responseData="";
        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl ="https://wakatime.com/api/v1/users/current/stats/last_7_days";
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(findMember.get().getSecretKey(),"");

            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );
            responseData = response.getBody();
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(responseData);
            JSONObject data = (JSONObject) jsonObject.get("data");

            JSONArray languages = (JSONArray) data.get("languages");
            JSONArray editors = (JSONArray) data.get("editors");
            JSONArray projects = (JSONArray) data.get("projects");
            if(!languages.isEmpty() || !editors.isEmpty() || !projects.isEmpty() || languages!=null || editors!=null || projects!=null){
                set_Language(languages);
                set_Project(projects);
                set_Editor(editors);
            }
            set_Member_By_Language(findMember.get());
            set_Member_By_Editor(findMember.get());
            set_Member_By_Project(findMember.get());
            responseInfoDto = new ResponseInfoDto().builder()
                .name(findMember.get().getName())
                .totalLanguages(Set.copyOf(findMember.get().getSevenlanguages()))
                .totalEditors(Set.copyOf(findMember.get().getSeveneditors()))
                .totalProejects(Set.copyOf(findMember.get().getSevenprojects()))
                    .money(findMember.get().getMoney())
                    .oranization(findMember.get().getOrganization())
                    .imageURL(findMember.get().getImage())
                    .department(findMember.get().getDepartment())
                .build();
            languageList.clear();
            editList.clear();
            projectList.clear();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return responseInfoDto;
    }
    @Transactional
    public void add_Member_By_apiKey(String name, String organization, String apiKey, String githubId,String department) {  // member 생성 api
        Member findMember = memberRepository.findByNameAndOrganization(name,organization);
        if(findMember ==null){
            Money money = new Money().builder().amount(0).updateDate(LocalDate.now().minusDays(1)).build();
            Member member = new Member();
            member.setSecretKey(apiKey);
            member.setName(name);
            member.setId(UUID.randomUUID());
            member.setSevenDays("0:0");
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
    private Map<String,String> set_Language(JSONArray languages){
        for(int j=0;j<languages.size();j++) {
            JSONObject index = (JSONObject) languages.get(j);
            String name = (String) index.get("name");
            Long hour = (Long) index.get("hours");
            Long min = (Long) index.get("minutes");
            if(min>=60){
                hour+=min/60;
                min = min%60;
            }
            if(languageList.containsKey(name)){
                String[] time = languageList.get(name).split(":");
                Long hour2 = Long.parseLong(time[0]);
                Long min2 = Long.parseLong(time[1]);
                hour += hour2;
                min += min2;
                if(min>=60){
                    hour+=min/60;
                    min = min%60;
                }
                languageList.replace(name,hour+":"+min);
            }
            else{

                languageList.put(name,hour+":"+min);
            }
        }
        return languageList;
    }
    private Map<String,String> set_Project(JSONArray projects){

        for(int j=0;j<projects.size();j++) {
            JSONObject index = (JSONObject) projects.get(j);
            String name = (String) index.get("name");
            Long hour = (Long) index.get("hours");
            Long min = (Long) index.get("minutes");
            if(min>=60){
                hour+=min/60;
                min = min%60;
            }
            if(projectList.containsKey(name)){
                String[] time = projectList.get(name).split(":");
                Long hour2 = Long.parseLong(time[0]);
                Long min2 = Long.parseLong(time[1]);
                hour += hour2;
                min += min2;
                if(min>=60){
                    hour+=min/60;
                    min = min%60;
                }
                projectList.replace(name,hour+":"+min);
            }
            else{

                projectList.put(name,hour+":"+min);
            }
        }
        return projectList;

    }
    private Map<String,String> set_Editor(JSONArray editor){

        for(int j=0;j<editor.size();j++) {
            JSONObject index = (JSONObject) editor.get(j);
            String name = (String) index.get("name");
            Long hour = (Long) index.get("hours");
            Long min = (Long) index.get("minutes");
            if(min>=60){
                hour+=min/60;
                min = min%60;
            }
            if(editList.containsKey(name)){
                String[] time = editList.get(name).split(":");
                Long hour2 = Long.parseLong(time[0]);
                Long min2 = Long.parseLong(time[1]);
                hour += hour2;
                min += min2;
                if(min>=60){
                    hour+=min/60;
                    min = min%60;
                }
                editList.replace(name,hour+":"+min);
            }
            else{

                editList.put(name,hour+":"+min);
            }
        }
        return editList;

    }
    private void set_Member_By_Language(Member member) {
        if (member.getSevenlanguages().size() == 0) { //최초 등록시
            for (String key : languageList.keySet()) {
                SevenDaysLanguage language = new SevenDaysLanguage().builder()
                        .name(key)
                        .time(languageList.get(key))
                        .build();
                member.getSevenlanguages().add(language);
            }
            memberRepository.save(member);
        } else {
            for (int i = 0; i < member.getSevenlanguages().size(); i++) {
                boolean flag = false;
                String name = "";
                for (String key : languageList.keySet()) {
                    name = key;
                    if (member.getSevenlanguages().get(i).getName().equals(key)){
                        if (member.getSevenlanguages().get(i).getTime().equals(languageList.get(key))) {
                            flag = true;
                            break;
                        } else {
                            SevenDaysLanguage language = sevenDaysLanguageRepository.findByName(key);
                            language.setTime(languageList.get(key));
                            sevenDaysLanguageRepository.save(language);
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag == false) {
                    SevenDaysLanguage language = new SevenDaysLanguage().builder()
                            .name(name)
                            .time(languageList.get(name))
                            .build();
                    member.getSevenlanguages().add(language);
                }
            }
            memberRepository.save(member);
        }
    }
    private void set_Member_By_Project(Member member) {
        if(member.getSevenprojects().size()==0){
            for (String key : projectList.keySet()) {
                SevenDaysProject project = new SevenDaysProject().builder()
                        .name(key)
                        .time(projectList.get(key))
                        .build();
                member.getSevenprojects().add(project);
            }
            memberRepository.save(member);
        }
        else{
            for(int i=0; i<member.getSevenprojects().size();i++){
                boolean flag = false;
                String name="";
                for(String key : projectList.keySet()){
                    name = key;
                    if(member.getSevenprojects().get(i).getName().equals(key)){
                        if (member.getSevenprojects().get(i).getTime().equals(projectList.get(key))) {
                            flag = true;
                            break;
                        } else {
                            SevenDaysProject project = sevendaysProjectRepository.findByName(key);
                            project.setTime(projectList.get(key));
                            sevendaysProjectRepository.save(project);
                            flag = true;
                            break;
                        }
                    }
                }
                if(flag==false){
                    SevenDaysProject project = new SevenDaysProject().builder()
                            .name(name)
                            .time(projectList.get(name))
                            .build();
                    member.getSevenprojects().add(project);
                }
            }
            memberRepository.save(member);
        }
    }
    private void set_Member_By_Editor(Member member) {
        if(member.getSeveneditors().size()==0){
            for (String key : editList.keySet()) {
                SevenDaysEditor editor = new SevenDaysEditor().builder()
                        .name(key)
                        .time(editList.get(key))
                        .build();
                member.getSeveneditors().add(editor);
            }
            memberRepository.save(member);
        }
        else{
            for(int i=0; i<member.getSeveneditors().size();i++){
                boolean flag = false;
                String name="";
                for(String key : editList.keySet()){
                    name = key;
                    if(member.getSeveneditors().get(i).getName().equals(key)){
                        if (member.getSeveneditors().get(i).getTime().equals(languageList.get(key))) {
                            flag = true;
                            break;
                        } else {
                            SevenDaysEditor editor = sevenDaysEditorRepository.findByName(key);
                            editor.setTime(editList.get(key));
                            sevenDaysEditorRepository.save(editor);
                            flag = true;
                            break;
                        }
                    }
                }
                if(flag==false){
                    SevenDaysEditor editor = new SevenDaysEditor().builder()
                            .name(name)
                            .time(editList.get(name))
                            .build();
                    member.getSeveneditors().add(editor);
                }
            }
            memberRepository.save(member);
        }
    }

}
