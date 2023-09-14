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
import org.intellij.lang.annotations.Language;
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
        Map<String,String> languageList = new HashMap<>();
        Map<String,String> projectList = new HashMap<>();
        Map<String,String> editList = new HashMap<>();
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
                languageList = set_Language(languages);
                projectList = set_Project(projects);
                editList = set_Editor(editors);
            }
            set_Member_By_Language(findMember.get(),languageList);
            set_Member_By_Editor(findMember.get(),editList);
            set_Member_By_Project(findMember.get(),projectList);

            responseInfoDto = new ResponseInfoDto().builder()
                .name(findMember.get().getName())
                .totalLanguages(findMember.get().getSevenlanguages().stream().distinct().toList())
                .totalEditors(findMember.get().getSeveneditors().stream().distinct().toList())
                .totalProejects(findMember.get().getSevenprojects().stream().distinct().toList())
                    .money(findMember.get().getMoney())
                    .oranization(findMember.get().getOrganization())
                    .imageURL(findMember.get().getImage())
                    .department(findMember.get().getDepartment())
                .build();

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
        Map<String,String> languageList = new HashMap<>();
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
        Map<String,String> projectList = new HashMap<>();
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
        Map<String,String> editList = new HashMap<>();
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
    //TODO 중복 제거 어떻게 할지.. 현재 로직은 너무 복잡함.
    private void set_Member_By_Language(Member addLanguageMember,Map<String,String> languageList) {
        List<SevenDaysLanguage> languages = sevenDaysLanguageRepository.findByMemberId(addLanguageMember.getId());
        if(languages.isEmpty()){
            for(String languageName : languageList.keySet()){
                SevenDaysLanguage createLanguage = new SevenDaysLanguage().builder()
                        .name(languageName)
                        .time(languageList.get(languageName))
                        .member(addLanguageMember)
                        .build();
                addLanguageMember.getSevenlanguages().add(createLanguage);
            }
        }
        else{
            for(String languageName : languageList.keySet()){
                Optional<SevenDaysLanguage> findLanguage = languages.stream().filter(language -> language.getName().equals(languageName)).findFirst();
               if(findLanguage.isPresent()){
                   addLanguageMember.getSevenlanguages().remove(findLanguage);
                   findLanguage.get().setTime(languageList.get(languageName));
                   addLanguageMember.getSevenlanguages().add(findLanguage.get());
               }
               else{
                   SevenDaysLanguage createLanguage = new SevenDaysLanguage().builder()
                           .name(languageName)
                           .time(languageList.get(languageName))
                           .member(addLanguageMember)
                           .build();
                   addLanguageMember.getSevenlanguages().add(createLanguage);
               }
            }
        }
        memberRepository.save(addLanguageMember);
    }
    private void set_Member_By_Project(Member addProjectMember,Map<String,String> projectList) {
        List<SevenDaysProject> projects = sevendaysProjectRepository.findByMemberId(addProjectMember.getId());
        if(projects.isEmpty()){
            for(String projectName : projectList.keySet()){
                SevenDaysProject createProject = new SevenDaysProject().builder()
                        .name(projectName)
                        .time(projectList.get(projectName))
                        .member(addProjectMember)
                        .build();
                addProjectMember.getSevenprojects().add(createProject);
            }
        }
        else{
            for(String projectName : projectList.keySet()){
                Optional<SevenDaysProject> findProject = projects.stream().filter(project -> project.getName().equals(projectName)).findFirst();
                if(findProject.isPresent()){

                    addProjectMember.getSevenprojects().remove(findProject);
                    findProject.get().setTime(projectList.get(projectName));
                    addProjectMember.getSevenprojects().add(findProject.get());
                }
                else{
                    SevenDaysProject createProject = new SevenDaysProject().builder()
                            .name(projectName)
                            .time(projectList.get(projectName))
                            .member(addProjectMember)
                            .build();
                    addProjectMember.getSevenprojects().add(createProject);
                }
            }
        }
        memberRepository.save(addProjectMember);
    }
    private void set_Member_By_Editor(Member addEditorMember,Map<String,String> editList) {
        List<SevenDaysEditor> editors = sevenDaysEditorRepository.findByMemberId(addEditorMember.getId());
        if(editors.isEmpty()){
            for(String editorName : editList.keySet()){
                SevenDaysEditor createEditor = new SevenDaysEditor().builder()
                        .name(editorName)
                        .time(editList.get(editorName))
                        .member(addEditorMember)
                        .build();
                addEditorMember.getSeveneditors().add(createEditor);
            }
        }
        else{
            for(String editorName : editList.keySet()){
                Optional<SevenDaysEditor> findEditor = editors.stream().filter(editor -> editor.getName().equals(editorName)).findFirst();
                if(editors.stream().anyMatch(editor -> editor.getName().equals(editorName))){
                    addEditorMember.getSeveneditors().remove(findEditor);
                    findEditor.get().setTime(editList.get(editorName));
                    addEditorMember.getSeveneditors().add(findEditor.get());
                }
                else{
                    SevenDaysEditor createEditor = new SevenDaysEditor().builder()
                            .name(editorName)
                            .time(editList.get(editorName))
                            .member(addEditorMember)
                            .build();
                    addEditorMember.getSeveneditors().add(createEditor);
                }
            }
        }
        memberRepository.save(addEditorMember);
    }

}
